import React from 'react';
import { Icon } from 'antd';
import molecule from 'molecule';
import {
    getEditorInitialActions,
    IExtension,
} from 'molecule/esm/model';
import { searchById } from 'molecule/esm/services/helper';
import { workbenchActions } from '../../../controller/dataSync/offlineAction';
import { resetEditorGroup } from '../utils';
import {
    TASK_RUN_ID,
    TASK_STOP_ID,
    TASK_SUBMIT_ID,
    TASK_RELEASE_ID,
    TASK_OPS_ID,
    OUTPUT_LOG,
} from '../utils/const';
import store from '../../../store';
import { matchTaskParams, filterSql } from '../../../comm';
import { TASK_TYPE } from '../../../comm/const';
import { debounce } from 'lodash';
import { execSql, stopSql } from '../../../controller/editor/editorAction';

function initActions() {
    molecule.editor.setDefaultActions([
        {
            id: TASK_RUN_ID,
            name: 'Run Task',
            icon: 'play',
            place: 'outer',
            disabled: true,
            title: '运行'
        },
        {
            id: TASK_STOP_ID,
            name: 'Stop Task',
            icon: 'debug-pause',
            place: 'outer',
            disabled: true,
            title: '停止运行'
        },
        {
            id: TASK_SUBMIT_ID,
            name: '提交至调度引擎',
            icon: <Icon type="upload" />,
            place: 'outer',
            disabled: true,
            title: '提交至调度引擎'
        },
        {
            id: TASK_RELEASE_ID,
            name: '拷贝任务至目标项目，或下载至本地',
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
                            d="M63.508 465.381l266.15 157.138 129.174 265.176 135.447-111.9 159.066 93.937 205.781-733.767L63.508 465.38zm393.848 206.332l-.115 130.788-91.16-187.16 432.344-326.935-341.069 383.307zM146.17 472.828l679.898-250.046-483.777 365.836-196.12-115.79zM731.262 815.34l-231.89-136.931 394.754-443.758L731.262 815.34z"
                        />
                    </svg>
                </span>
            ),
            place: 'outer',
            disabled: true,
            title: '拷贝任务至目标项目，或下载至本地'
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

                    const currentTab = current.tab;

                    const { tabs, currentTab: currentTaskId } = (
                        store.getState() as any
                    ).workbenchReducer;
                    const task = tabs.find(
                        (tab: any) => tab.id === currentTaskId
                    );

                    const params: any = {
                        projectId: currentTab?.id,
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
                    )(store.dispatch).then(() => {
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
                    });
                }
                break;
            }
            case TASK_STOP_ID: {
                const { tabs, currentTab: currentTaskId } = (
                    store.getState() as any
                ).workbenchReducer;
                const task = tabs.find((tab: any) => tab.id === currentTaskId);

                stopSql(task.id, task, false)(store.dispatch, store.getState);
                molecule.editor.updateActions([
                    {
                        id: TASK_RUN_ID,
                        icon: 'play',
                        disabled: true,
                    },
                    {
                        id: TASK_STOP_ID,
                        disabled: false,
                    },
                ]);
                break;
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
