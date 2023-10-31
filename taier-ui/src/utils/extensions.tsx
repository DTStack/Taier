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

import molecule from '@dtinsight/molecule';
import { EventBus } from '@dtinsight/molecule/esm/common/event';
import { TreeViewUtil } from '@dtinsight/molecule/esm/common/treeUtil';
import type { IFolderTreeNodeProps } from '@dtinsight/molecule/esm/model';
import md5 from 'md5';

import stream from '@/api';
import { ResourceIcon,SyntaxIcon } from '@/components/icon';
import Result from '@/components/task/result';
import type { RESOURCE_TYPE } from '@/constant';
import { CATALOGUE_TYPE, ID_COLLECTIONS, TASK_TYPE_ENUM } from '@/constant';
import type { CatalogueDataProps, IOfflineTaskProps } from '@/interface';
import { IJobType } from '@/interface';
import { executeService, taskRenderService } from '@/services';
import taskResultService, { createLog } from '@/services/taskResultService';
import taskSaveService from '@/services/taskSaveService';
import { filterSql } from '.';

/**
 * 根据不同任务渲染不同的图标
 */
export function fileIcon(type: TASK_TYPE_ENUM | RESOURCE_TYPE | null, source: CATALOGUE_TYPE): string | JSX.Element {
    switch (source) {
        case CATALOGUE_TYPE.TASK: {
            return taskRenderService.renderTaskIcon(type as TASK_TYPE_ENUM);
        }
        case CATALOGUE_TYPE.RESOURCE: {
            return <ResourceIcon style={{ color: '#0065f6' }} />;
        }
        case CATALOGUE_TYPE.FUNCTION:
        default:
            return 'code';
    }
}

/**
 * 运行任务
 */
export function runTask(current: molecule.model.IEditorGroup) {
    const currentTabData: (CatalogueDataProps & IOfflineTaskProps & { value?: string }) | undefined = current.tab?.data;
    if (currentTabData) {
        // active 日志 窗口
        const { data } = molecule.panel.getState();
        const {
            panel: { hidden },
        } = molecule.layout.getState();
        if (hidden) {
            molecule.layout.togglePanelVisibility();
        }
        molecule.panel.setState({
            current: data?.find((item) => item.id === ID_COLLECTIONS.OUTPUT_LOG_ID),
        });

        if (currentTabData.taskType === TASK_TYPE_ENUM.SYNC) {
            const params: any = {
                taskId: currentTabData.id,
                name: currentTabData.name,
                taskParams: currentTabData.taskParams,
            };
            executeService.execDataSync(currentTabData.id, params);
        } else {
            const params = {
                taskVariables: currentTabData.taskVariables || [],
                // 是否为单 session 模式, 为 true 时，支持batchSession 时，则支持批量SQL，false 则相反
                singleSession: false,
                taskParams: currentTabData.taskParams,
            };

            const value = currentTabData.value || '';
            // 需要被执行的 sql 语句
            const sqls: string[] = [];

            const field = taskRenderService.getField(currentTabData.taskType);
            if (
                field?.jobType === IJobType.SQL &&
                taskRenderService.getRenderKind(currentTabData.taskType) === 'editor'
            ) {
                const rawSelections = molecule.editor.editorInstance.getSelections() || [];
                // 排除鼠标 focus 在 editor 中的情况
                const selections = rawSelections.filter(
                    (s) => s.startLineNumber !== s.endLineNumber || s.startColumn !== s.endColumn
                );
                // 如果存在选中行，则执行选中行
                if (selections?.length) {
                    selections?.forEach((s) => {
                        const text = molecule.editor.editorInstance.getModel()?.getValueInRange(s);
                        if (text) {
                            sqls.push(...filterSql(text));
                        }
                    });
                } else {
                    sqls.push(...filterSql(value));
                }
            } else {
                sqls.push(value);
            }

            executeService.execSql(currentTabData.id, currentTabData, params, sqls).then(() => {
                const { results } = taskResultService.getState();
                let nextActivePanel: string | null = null;
                Object.entries(results).forEach(([key, values]) => {
                    const panel = molecule.panel.getPanel(key);
                    const renderPane = () => (
                        <Result
                            data={values}
                            tab={{
                                tableType: 0,
                            }}
                            extraView={null}
                        />
                    );
                    if (!panel) {
                        const panels = molecule.panel.getState().data || [];
                        const resultPanles = panels.filter((p) => p.name?.includes('结果'));
                        const lastIndexOf = Number(resultPanles[resultPanles.length - 1]?.name?.slice(2) || '');

                        nextActivePanel = key;
                        molecule.panel.add({
                            id: key,
                            name: `结果 ${lastIndexOf + 1}`,
                            closable: true,
                            renderPane,
                        });
                    } else {
                        // 更新已有的panel
                        molecule.panel.update({
                            id: key,
                            renderPane,
                        });
                    }
                });

                const nextKey = nextActivePanel || `${currentTabData.id}-${md5(sqls.at(-1) || 'sync')}`;

                if (molecule.panel.getPanel(nextKey)) {
                    molecule.panel.setActive(nextKey);
                }
            });
        }
    }
}

/**
 * 语法检查
 */
export function syntaxValidate(current: molecule.model.IEditorGroup) {
    const currentTabData: IOfflineTaskProps | undefined = current.tab?.data;
    if (!currentTabData) return;
    // 禁用语法检查
    molecule.editor.updateActions([
        {
            id: ID_COLLECTIONS.TASK_SYNTAX_ID,
            icon: 'loading~spin',
            disabled: true,
        },
    ]);

    // active 日志 窗口
    const { data } = molecule.panel.getState();
    const {
        panel: { hidden },
    } = molecule.layout.getState();
    if (hidden) {
        molecule.layout.togglePanelVisibility();
    }
    molecule.panel.setState({
        current: data?.find((item) => item.id === ID_COLLECTIONS.OUTPUT_LOG_ID),
    });

    const logId = currentTabData.id.toString();
    taskResultService.clearLogs(logId);
    taskResultService.appendLogs(logId, createLog('语法检查开始', 'info'));

    const params = taskSaveService.transformTabDataToParams(currentTabData);

    let isSuccess = false;
    stream
        .checkSyntax(params)
        .then((res) => {
            if (res.message) {
                taskResultService.appendLogs(logId, createLog(res.message, 'error'));
            }
            if (res && res.code === 1) {
                if (res.data.code === 1) {
                    taskResultService.appendLogs(logId, createLog('语法检查通过', 'info'));
                    isSuccess = true;
                } else {
                    taskResultService.appendLogs(logId, createLog(res.data.errorMsg, 'error'));
                }
            }
        })
        .catch((e) => {
            console.trace(e);
        })
        .finally(() => {
            if (!isSuccess) {
                taskResultService.appendLogs(logId, createLog('语法检查失败！', 'error'));
            }
            // 恢复语法检查按钮
            molecule.editor.updateActions([
                {
                    id: ID_COLLECTIONS.TASK_SYNTAX_ID,
                    icon: <SyntaxIcon />,
                    disabled: false,
                },
            ]);
        });
}

export function getParentNode(treeList: IFolderTreeNodeProps, currentNode: IFolderTreeNodeProps) {
    const treeView = new TreeViewUtil(treeList);
    const parentNode = treeView.getHashMap(currentNode.id)?.parent;
    if (parentNode) {
        return treeView.getNode(parentNode);
    }
    return null;
}

/**
 * This function for executing func after switching task
 */
export function onTaskSwitch(func: () => void) {
    EventBus.subscribe(ID_COLLECTIONS.TASK_SWITCH_EVENT, () => {
        func();
    });
}
