/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import { Icon, message, Modal, Tag } from 'antd';
import molecule from '@dtinsight/molecule';
import {
    getEditorInitialActions,
    IExtension,
} from '@dtinsight/molecule/esm/model';
import { searchById } from '@dtinsight/molecule/esm/services/helper';
import { workbenchActions } from '../../../controller/dataSync/offlineAction';
import { workbenchAction } from '../../../controller/dataSync/actionType';
import { resetEditorGroup } from '../utils';
import {
    TASK_RUN_ID,
    TASK_STOP_ID,
    TASK_SUBMIT_ID,
    TASK_OPS_ID,
    OUTPUT_LOG,
    TASK_SAVE_ID,
} from '../utils/const';
import store from '../../../store';
import { matchTaskParams, filterSql, formatDateTime } from '../../../comm';
import { TASK_TYPE } from '../../../comm/const';
import { debounce } from 'lodash';
import {
    execSql,
    stopSql,
    execDataSync,
    stopDataSync,
} from '../../../controller/editor/editorAction';
import ajax from '../../../api';
import ReactDOM from 'react-dom';
import Result from '../../task/result';
import Publish, { CONTAINER_ID } from '../../task/publish';

const confirm = Modal.confirm;

function initActions() {
    molecule.editor.setDefaultActions([
        {
            id: TASK_SAVE_ID,
            name: 'Save Task',
            icon: 'save',
            place: 'outer',
            disabled: true,
            title: '保存',
        },
        {
            id: TASK_RUN_ID,
            name: 'Run Task',
            icon: 'play',
            place: 'outer',
            disabled: true,
            title: '运行',
        },
        {
            id: TASK_STOP_ID,
            name: 'Stop Task',
            icon: 'debug-pause',
            place: 'outer',
            disabled: true,
            title: '停止运行',
        },
        {
            id: TASK_SUBMIT_ID,
            name: '提交至调度引擎',
            icon: <Icon type="upload" />,
            place: 'outer',
            disabled: true,
            title: '提交至调度引擎',
        },
        {
            id: TASK_OPS_ID,
            name: '运维',
            title: '运维',
            icon: (
                <span style={{ fontSize: 14, display: 'flex' }}>
                    <svg
                        viewBox="0 0 1024 1024"
                        xmlns="http://www.w3.org/2000/svg"
                        width="1em"
                        height="1em"
                    >
                        <path
                            fill="currentColor"
                            d="M512 0C292.571 0 109.714 138.971 36.571 329.143h80.458c21.942-43.886 51.2-87.772 87.771-124.343C285.257 117.029 394.971 73.143 512 73.143S738.743 117.029 819.2 204.8c80.457 80.457 131.657 190.171 131.657 307.2S906.971 738.743 819.2 819.2C738.743 899.657 629.029 950.857 512 950.857S285.257 906.971 204.8 819.2c-36.571-36.571-65.829-80.457-87.771-124.343H36.57C109.714 885.03 292.571 1024 512 1024c285.257 0 512-226.743 512-512S789.943 0 512 0zM402.286 665.6l51.2 51.2 204.8-204.8-204.8-204.8-51.2 51.2 117.028 117.029H0v73.142h519.314L402.286 665.6z"
                        />
                    </svg>
                </span>
            ),
            place: 'outer',
            disabled: true,
        },
        ...getEditorInitialActions(),
    ]);
}

function emitEvent() {
    molecule.editor.onActionsClick(async (menuId, current) => {
        switch (menuId) {
            case TASK_RUN_ID: {
                // TODO
                const value = current.tab?.data.value || '';
                if (value) {
                    // 禁用运行按钮，启用停止按钮
                    molecule.editor.updateActions([
                        {
                            id: TASK_RUN_ID,
                            icon: 'loading~spin',
                            disabled: true,
                        },
                        {
                            id: TASK_STOP_ID,
                            disabled: false,
                        },
                    ]);

                    // active 日志 窗口
                    const { data } = molecule.panel.getState();
                    molecule.panel.setState({
                        current: data?.find((item) => item.id === OUTPUT_LOG),
                    });

                    const currentTab = current.tab!;

                    const { tabs, currentTab: currentTaskId } = (
                        store.getState() as any
                    ).workbenchReducer;
                    const task = tabs.find(
                        (tab: any) => tab.id === currentTaskId
                    );

                    if (task.taskType === TASK_TYPE.SYNC) {
                        const params: any = {
                            taskId: task.id,
                            name: task.name,
                            taskParams: task?.taskParams,
                        };
                        execDataSync(
                            task.id,
                            params
                        )(store.dispatch).finally(() => {
                            // update the status of buttons
                            molecule.editor.updateActions([
                                {
                                    id: TASK_SAVE_ID,
                                    disabled: false,
                                },
                                {
                                    id: TASK_RUN_ID,
                                    icon: 'play',
                                    disabled: false,
                                },
                                {
                                    id: TASK_STOP_ID,
                                    disabled: true,
                                },
                            ]);
                        });
                    } else {
                        const params: any = {
                            taskVariables: currentTab?.data.taskVariables || [],
                            singleSession: false, // 是否为单 session 模式, 为 true 时，支持batchSession 时，则支持批量SQL，false 则相反
                            taskParams: currentTab?.data.taskParams,
                        };

                        // [TODO]
                        const sqls = filterSql(currentTab?.data.value);
                        execSql(
                            currentTab?.id,
                            task,
                            params,
                            sqls
                        )(store.dispatch)
                            .then(() => {
                                const results: {
                                    jobId: string;
                                    data: any[];
                                }[] =
                                    (store.getState() as any).editor.console[
                                        task.id
                                    ]?.results || [];

                                if (!results.length) {
                                    return;
                                }

                                results.forEach((res) => {
                                    const panel = molecule.panel.getPanel(
                                        res.jobId
                                    );
                                    if (!panel) {
                                        const panels =
                                            molecule.panel.getState().data ||
                                            [];
                                        const resultPanles = panels.filter(
                                            (p) => p.name?.includes('结果')
                                        );
                                        const lastIndexOf = Number(
                                            resultPanles[
                                                resultPanles.length - 1
                                            ]?.name?.slice(2) || ''
                                        );

                                        molecule.panel.open({
                                            id: res.jobId,
                                            name: `结果 ${lastIndexOf + 1}`,
                                            closable: true,
                                            renderPane: () => (
                                                <Result
                                                    isShow
                                                    data={res.data}
                                                    tab={{
                                                        tableType: 0,
                                                    }}
                                                    extraView={null}
                                                />
                                            ),
                                        });
                                    }
                                });
                            })
                            .finally(() => {
                                // update the status of buttons
                                molecule.editor.updateActions([
                                    {
                                        id: TASK_SAVE_ID,
                                        disabled: false,
                                    },
                                    {
                                        id: TASK_RUN_ID,
                                        icon: 'play',
                                        disabled: false,
                                    },
                                    {
                                        id: TASK_STOP_ID,
                                        disabled: true,
                                    },
                                ]);
                            });
                    }
                }
                break;
            }
            case TASK_STOP_ID: {
                const { tabs, currentTab: currentTaskId } = (
                    store.getState() as any
                ).workbenchReducer;
                const task = tabs.find((tab: any) => tab.id === currentTaskId);

                if (task.taskType === TASK_TYPE.SYNC) {
                    stopDataSync(task.id, false)(
                        store.dispatch,
                        store.getState
                    );
                } else {
                    stopSql(
                        task.id,
                        task,
                        false
                    )(store.dispatch, store.getState);
                }
                molecule.editor.updateActions([
                    {
                        id: TASK_RUN_ID,
                        icon: 'play',
                        disabled: false,
                    },
                    {
                        id: TASK_STOP_ID,
                        disabled: true,
                    },
                ]);
                break;
            }
            case TASK_SAVE_ID: {
                const params = {
                    ...current.tab?.data,
                    sqlText: current.tab?.data.value,
                };
                const uploadTask = () => {
                    const id = params.id;
                    ajax.getOfflineTaskByID({ id }).then((res) => {
                        const { success, data } = res;
                        if (success) {
                            store.dispatch({
                                type: workbenchAction.LOAD_TASK_DETAIL,
                                payload: data,
                            });
                            molecule.editor.updateActions([
                                {
                                    id: TASK_SAVE_ID,
                                    disabled: false,
                                },
                                {
                                    id: TASK_RUN_ID,
                                    icon: 'play',
                                    disabled: false,
                                },
                                {
                                    id: TASK_STOP_ID,
                                    disabled: true,
                                },
                                {
                                    id: TASK_SUBMIT_ID,
                                    disabled: false,
                                },
                            ]);
                        }
                    });
                };
                const succCallback = (res: any) => {
                    if (res.code === 1) {
                        const fileData = res.data;
                        const lockInfo = fileData.readWriteLockVO;
                        const lockStatus = lockInfo?.result; // 1-正常，2-被锁定，3-需同步
                        if (lockStatus === 0) {
                            message.success('保存成功！');
                            uploadTask();
                            // 如果是锁定状态，点击确定按钮，强制更新，否则，取消保存
                        } else if (lockStatus === 1) {
                            // 2-被锁定
                            confirm({
                                title: '锁定提醒', // 锁定提示
                                content: (
                                    <span>
                                        文件正在被
                                        {lockInfo.lastKeepLockUserName}
                                        编辑中，开始编辑时间为
                                        {formatDateTime(lockInfo.gmtModified)}。
                                        强制保存可能导致
                                        {lockInfo.lastKeepLockUserName}
                                        对文件的修改无法正常保存！
                                    </span>
                                ),
                                okText: '确定保存',
                                okType: 'danger',
                                cancelText: '取消',
                                onOk() {
                                    const succCall = (res: any) => {
                                        if (res.code === 1) {
                                            message.success('保存成功！');
                                            uploadTask();
                                        }
                                    };
                                    ajax.forceUpdateOfflineTask(params).then(
                                        succCall
                                    );
                                },
                            });
                            // 如果同步状态，则提示会覆盖代码，
                            // 点击确认，重新拉取代码并覆盖当前代码，取消则退出
                        } else if (lockStatus === 2) {
                            // 2-需同步
                            confirm({
                                title: '保存警告',
                                content: (
                                    <span>
                                        文件已经被
                                        {lockInfo.lastKeepLockUserName}
                                        编辑过，编辑时间为
                                        {formatDateTime(lockInfo.gmtModified)}。
                                        点击确认按钮会
                                        <Tag color="orange">覆盖</Tag>
                                        您本地的代码，请您提前做好备份！
                                    </span>
                                ),
                                okText: '确定覆盖',
                                okType: 'danger',
                                cancelText: '取消',
                                onOk() {
                                    const reqParams: any = {
                                        id: params.id,
                                        lockVersion: lockInfo.version,
                                    };
                                    // 更新version, getLock信息
                                    ajax.getOfflineTaskDetail(reqParams).then(
                                        (res: any) => {
                                            if (res.code === 1) {
                                                const taskInfo = res.data;
                                                taskInfo.merged = true;
                                                uploadTask();
                                            }
                                        }
                                    );
                                },
                            });
                        }
                        return res;
                    }
                };
                ajax.saveOfflineJobData(params).then(succCallback);
                break;
            }
            case TASK_SUBMIT_ID: {
                const currentTab = current.tab;
                const root = document.getElementById('molecule')!;

                const target = document.getElementById(CONTAINER_ID);
                if (target) {
                    target.parentElement?.removeChild(target);
                }
                const node = document.createElement('div');
                node.id = CONTAINER_ID;
                root.appendChild(node);
                if (currentTab) {
                    ReactDOM.render(<Publish data={currentTab.data} />, node);
                }
            }
        }
    });
}

const updateTaskVariables = debounce((tab) => {
    const { taskCustomParams } = (store.getState() as any).workbenchReducer;
    const data = matchTaskParams(taskCustomParams, tab.data?.value || '');
    tab.data!.taskVariables = data;
    molecule.editor.updateTab(tab);
}, 300);

export default class EditorExtension implements IExtension {
    activate() {
        initActions();
        emitEvent();

        molecule.editor.onSelectTab((tabId, groupId) => {
            const { current } = molecule.editor.getState();
            if (!current) return;
            const group = molecule.editor.getGroupById(groupId || current.id!);
            if (group) {
                const targetTab = group.data?.find(searchById(tabId));
                if (targetTab?.data.taskType === TASK_TYPE.SQL) {
                    molecule.editor.updateActions([
                        { id: TASK_RUN_ID, disabled: false },
                        { id: TASK_SAVE_ID, disabled: false },
                        { id: TASK_SUBMIT_ID, disabled: false },
                    ]);
                } else {
                    resetEditorGroup();
                }
            }
        });

        molecule.editor.onCloseTab(() => {
            const { current } = molecule.editor.getState();
            if (current?.tab?.data.taskType === TASK_TYPE.SQL) {
                molecule.editor.updateActions([
                    { id: TASK_RUN_ID, disabled: false },
                    { id: TASK_SAVE_ID, disabled: false },
                    { id: TASK_SUBMIT_ID, disabled: false },
                ]);
            } else {
                resetEditorGroup();
            }
        });

        const actions = workbenchActions(store.dispatch);
        actions.loadTaskParams();

        molecule.editor.onUpdateTab((tab) => {
            updateTaskVariables(tab);
        });
    }
}
