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

import molecule from '@dtinsight/molecule/esm';
import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import { localize } from '@dtinsight/molecule/esm/i18n/localize';
import type { IExtension } from '@dtinsight/molecule/esm/model';
import { FileTypes, TreeNodeModel } from '@dtinsight/molecule/esm/model';
import { message, Modal } from 'antd';

import api from '@/api';
import notification from '@/components/notification';
import type { ICreateTaskFormFieldProps } from '@/components/task/create';
import Create from '@/components/task/create';
import EditFolder from '@/components/task/editFolder';
import { CATALOGUE_TYPE, ID_COLLECTIONS,TASK_TYPE_ENUM } from '@/constant';
import { IComputeType } from '@/interface';
import { catalogueService,taskRenderService } from '@/services';
import taskSaveService from '@/services/taskSaveService';
import { getParentNode } from '@/utils/extensions';

/**
 * 	实时采集和FlinkSql任务的computeType返回0
 * @param type 任务类型
 * @returns
 */
function getComputeType(type: TASK_TYPE_ENUM): number {
    if (type === TASK_TYPE_ENUM.DATA_ACQUISITION || type === TASK_TYPE_ENUM.SQL) {
        return IComputeType.STREAM;
    }
    return IComputeType.BATCH;
}

/**
 * Open a tab for creating task
 */
function openCreateTab(id?: string) {
    const onSubmit = (values: ICreateTaskFormFieldProps) => {
        const { resourceIdList, ...restValues } = values;
        return new Promise<boolean>((resolve) => {
            const params: Record<string, any> = {
                ...restValues,
                resourceIdList,
                computeType: getComputeType(values.taskType),
                parentId: values.nodePid,
            };

            api.addOfflineTask(params)
                .then((res) => {
                    if (res.code === 1) {
                        const { data } = res;
                        const groupId = molecule.editor.getGroupIdByTab(tabId);
                        if (!groupId) return;
                        molecule.editor.closeTab(tabId, groupId);
                        molecule.explorer.forceUpdate();

                        const parentNode = molecule.folderTree.get(`${params.parentId}-folder`);
                        if (parentNode) {
                            catalogueService.loadTreeNode(parentNode.data, CATALOGUE_TYPE.TASK).then(() => {
                                // open this brand-new task
                                taskRenderService.openTask({ id: data.id });
                            });
                        }
                    }
                })
                .finally(() => {
                    resolve(false);
                });
        });
    };

    const tabId = `${ID_COLLECTIONS.CREATE_TASK_PREFIX}_${new Date().getTime()}`;
    const { folderTree } = molecule.folderTree.getState();
    if (!folderTree?.current && !folderTree?.data?.length) return;
    const tabData = {
        id: tabId,
        modified: false,
        name: localize('create task', '新建任务'),
        icon: 'file-add',
        breadcrumb: [
            {
                id: catalogueService.getRootFolder(CATALOGUE_TYPE.TASK)!.id,
                name: catalogueService.getRootFolder(CATALOGUE_TYPE.TASK)!.name,
            },
            {
                id: tabId,
                name: localize('create task', '新建任务'),
            },
        ],
        data: {
            // always create task under the root node or create task from context menu
            nodePid: id || folderTree.data?.[0].id,
        },
        renderPane: () => {
            return <Create key={tabId} onSubmit={onSubmit} />;
        },
    };

    molecule.editor.open(tabData);
    molecule.explorer.forceUpdate();
}

function init() {
    molecule.explorer.onPanelToolbarClick((panel, toolbarId: string) => {
        const { SAMPLE_FOLDER_PANEL_ID } = molecule.builtin.getConstants();
        // 如果是任务刷新，执行重新加载
        if (panel.id === SAMPLE_FOLDER_PANEL_ID && toolbarId === 'refresh') {
            const { current } = molecule.editor.getState();
            const { folderTree } = molecule.folderTree.getState();
            if (!folderTree?.data?.length) return;
            if (current) {
                // keep the folderTree's current consistent with the editor's current
                molecule.folderTree.setActive(Number(current.activeTab));
            }
            const currentFolderTree = molecule.folderTree.getState().folderTree?.current;
            if (!currentFolderTree) return;
            // expand the current Node's parentNode
            const expandKeys = molecule.folderTree.getExpandKeys();
            const parentNode = getParentNode(folderTree!.data![0], currentFolderTree);
            if (!parentNode) return;
            if (!expandKeys.includes(parentNode.id)) {
                molecule.folderTree.setExpandKeys([...expandKeys, parentNode.id]);
            }

            // reload the parentNode
            catalogueService.loadTreeNode(parentNode.data, CATALOGUE_TYPE.TASK).then(() => {
                // TODO: don't need it after fix the issue https://github.com/DTStack/molecule/issues/724
                if (molecule.folderTree.getState().folderTree?.current?.id !== undefined) {
                    document
                        .querySelector<HTMLDivElement>('.mo-tree__treenode--active')
                        ?.classList.remove('mo-tree__treenode--active');
                    const dom = document.querySelector<HTMLDivElement>(
                        `div.mo-tree__treenode[data-key="${molecule.folderTree.getState().folderTree?.current?.id}"]`
                    );
                    dom?.classList.add('mo-tree__treenode--active');
                }
            });
        }
    });
}

// 初始化右键菜单
function initContextMenu() {
    molecule.folderTree.onRightClick((treeNode, menu) => {
        if (!treeNode.isLeaf) {
            // remove rename action
            const idx = menu.findIndex((m) => m.id === molecule.builtin.getConstants().RENAME_COMMAND_ID);
            menu.splice(idx, 1);
            // insert these menus into folder context
            menu.splice(0, 1, { id: ID_COLLECTIONS.TASK_CREATE_ID, name: '新建任务' });
            menu.splice(2, 0, {
                id: ID_COLLECTIONS.FOLDERTREE_CONTEXT_EDIT,
                name: '编辑',
            });
        } else {
            // remove rename action
            const idx = menu.findIndex((m) => m.id === molecule.builtin.getConstants().RENAME_COMMAND_ID);
            menu.splice(idx, 1);
            menu.splice(1, 0, {
                id: ID_COLLECTIONS.FOLDERTREE_CONTEXT_EDIT,
                name: '编辑',
            });
        }
    });
}

function createTask() {
    molecule.folderTree.onCreate(async (type, id) => {
        const folderTree = molecule.folderTree.getState().folderTree;
        if (!folderTree?.data?.length) {
            message.error('请先配置集群并进行绑定!');
            return;
        }
        if (type === 'File') {
            openCreateTab();
        } else if (type === 'Folder') {
            const parentId = typeof id === 'string' ? id : folderTree.data?.[0].id;
            molecule.folderTree.add(
                new TreeNodeModel({
                    id: `${ID_COLLECTIONS.CREATE_FOLDER_PREFIX}_${new Date().getTime()}`,
                    name: '',
                    isLeaf: false,
                    fileType: FileTypes.Folder,
                    isEditable: true,
                    data: {
                        parentId,
                    },
                }),
                parentId
            );
        }
    });
}

function editTreeNodeName() {
    molecule.folderTree.onUpdateFileName((file) => {
        const {
            name,
            data: { parentId },
            id,
        } = file;
        if (!name || name.length > 64) {
            notification.error({ key: 'create', message: '目录名称不得超过64个字符且不为空！' });
            if (molecule.folderTree.get(id)) {
                molecule.folderTree.remove(id);
            }
            return;
        }
        const [nodePid] = parentId.split('-');
        api.addOfflineCatalogue({
            nodeName: name,
            nodePid,
        }).then((res) => {
            if (res.code === 1) {
                const parentNode = molecule.folderTree.get(parentId);
                catalogueService.loadTreeNode(
                    {
                        id: parentNode?.data.id,
                        catalogueType: parentNode?.data.catalogueType,
                    },
                    CATALOGUE_TYPE.TASK
                );
                molecule.explorer.forceUpdate();
            } else {
                molecule.folderTree.remove(id);
            }
        });
    });
}

function onSelectFile() {
    molecule.folderTree.onSelectFile((file) => {
        molecule.folderTree.setActive(file.id);
        taskRenderService.openTask({ id: file.id }, { create: false });
    });
}

function onRemove() {
    molecule.folderTree.onRemove((id) => {
        const treeNode = molecule.folderTree.get(id);
        const type = treeNode?.fileType;
        Modal.confirm({
            title: `确认要删除此${type === 'File' ? '任务' : '文件夹'}吗?`,
            content: `删除的${type === 'File' ? '任务' : '文件夹'}无法${type === 'File' ? '找回' : '恢复'}！`,
            onOk() {
                if (type === 'Folder') {
                    api.delOfflineFolder({ id: treeNode?.data.id }).then((res) => {
                        if (res.code === 1) {
                            message.success('删除成功');
                            molecule.folderTree.remove(id);
                        }
                        return res;
                    });
                    return;
                }
                api.delOfflineTask({ taskId: id }).then((res) => {
                    if (res.code === 1) {
                        message.success('删除成功');
                        molecule.folderTree.remove(id);
                        // Close the opened tab
                        const isOpened = molecule.editor.isOpened(id.toString());
                        if (isOpened) {
                            const groupId = molecule.editor.getGroupIdByTab(id.toString());
                            if (groupId) {
                                molecule.editor.closeTab(id.toString(), groupId);
                            }
                        }
                    }
                    return res;
                });
            },
            onCancel() {},
        });
    });
}

function contextMenu() {
    molecule.folderTree.onContextMenu((menu, treeNode) => {
        switch (menu.id) {
            case ID_COLLECTIONS.TASK_CREATE_ID: {
                openCreateTab(treeNode!.data.id);
                break;
            }
            case ID_COLLECTIONS.FOLDERTREE_CONTEXT_EDIT: {
                const isFile = treeNode!.fileType === 'File';

                const tabId = isFile
                    ? `${ID_COLLECTIONS.EDIT_TASK_PREFIX}_${new Date().getTime()}`
                    : `${ID_COLLECTIONS.EDIT_FOLDER_PREFIX}_${new Date().getTime()}`;

                const onSubmit = (values: ICreateTaskFormFieldProps) => {
                    return new Promise<boolean>((resolve) => {
                        const params = {
                            taskId: treeNode!.data.id,
                            name: values.name,
                            catalogueId: values.nodePid,
                            desc: values.taskDesc,
                            componentVersion: values.componentVersion,
                        };
                        api.editTask(params)
                            .then((res) => {
                                if (res.code === 1) {
                                    message.success('编辑成功');
                                    taskSaveService.updateFolderAndTabAfterSave(
                                        treeNode!.data.parentId,
                                        params.catalogueId,
                                        params.taskId,
                                        params.name,
                                        tabId
                                    );
                                }
                            })
                            .finally(() => {
                                resolve(false);
                            });
                    });
                };

                const onSubmitFolder = (values: any) => {
                    return new Promise<boolean>((resolve) => {
                        const params = {
                            id: treeNode!.data.id,
                            type: 'folder',
                            ...values,
                        };
                        api.editOfflineCatalogue(params)
                            .then((res) => {
                                if (res.code === 1) {
                                    message.success('编辑成功');
                                    taskSaveService.updateFolderAndTabAfterSave(
                                        treeNode!.data.parentId,
                                        params.nodePid,
                                        params.nodeName,
                                        params.id,
                                        tabId
                                    );
                                }
                            })
                            .finally(() => {
                                resolve(false);
                            });
                    });
                };

                const breadcrumb = [
                    {
                        id: catalogueService.getRootFolder(CATALOGUE_TYPE.TASK)!.id,
                        name: catalogueService.getRootFolder(CATALOGUE_TYPE.TASK)!.name,
                    },
                    {
                        id: tabId,
                        name: isFile ? localize('update task', '编辑任务') : localize('update folder', '编辑文件夹'),
                    },
                ];
                const tabData: molecule.model.IEditorTab = {
                    id: tabId,
                    data: isFile
                        ? {
                              id: treeNode?.data.id,
                              name: treeNode?.name,
                              taskType: treeNode?.data.taskType,
                              nodePid: treeNode?.data.parentId,
                              taskDesc: treeNode?.data.taskDesc,
                          }
                        : {
                              id: treeNode?.id,
                              nodePid: treeNode?.data.parentId,
                              dt_nodeName: treeNode?.name,
                          },
                    icon: 'edit',
                    breadcrumb,
                    name: isFile ? localize('update task', '编辑任务') : localize('update folder', '编辑文件夹'),
                    renderPane: () => {
                        return (
                            <>
                                {isFile ? (
                                    <Create key={tabId} record={treeNode!.data} onSubmit={onSubmit} />
                                ) : (
                                    <EditFolder
                                        tabId={tabId}
                                        key={tabId}
                                        record={treeNode!.data}
                                        onSubmitFolder={onSubmitFolder}
                                    />
                                )}
                            </>
                        );
                    },
                };

                const { groups = [] } = molecule.editor.getState();
                const isExist = groups.some((group) => group.data?.some((tab) => tab.id === tabId));
                if (!isExist) {
                    molecule.editor.open(tabData);
                    molecule.explorer.forceUpdate();
                }
                break;
            }
            default:
                break;
        }
    });
}

// 文件夹树异步加载
function onLoadTree() {
    molecule.folderTree.onLoadData((treeNode, callback) => {
        catalogueService.loadTreeNode(treeNode.data, CATALOGUE_TYPE.TASK).then((res) => {
            if (res) {
                callback(res);
            } else {
                callback(treeNode);
            }
        });
    });
}

export default class FolderTreeExtension implements IExtension {
    id: UniqueId = 'folderTree';
    name = 'folderTree';
    dispose(): void {
        throw new Error('Method not implemented.');
    }
    activate() {
        // 初始化 entry 样式和刷新
        init();
        // 初始化右键菜单
        initContextMenu();
        // 文件夹异步加载
        onLoadTree();
        // 新建任务
        createTask();
        // 目录树点击事件
        onSelectFile();
        // 右键菜单点击事件
        contextMenu();
        // 删除事件
        onRemove();
        // 新建文件夹
        editTreeNodeName();
    }
}
