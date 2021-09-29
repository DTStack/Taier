import React from 'react';
import { message } from 'antd';
import {
    FileTypes,
    IExtension,
    TreeNodeModel,
} from '@dtinsight/molecule/esm/model';
import { localize } from '@dtinsight/molecule/esm/i18n/localize';
import molecule from '@dtinsight/molecule/esm';
import { connect } from '@dtinsight/molecule/esm/react';

import Open from '../../task/open';
import EditFolder from '../../task/editFolder';
import {
    getCatalogueViaNode,
    loadTreeNode,
    resetEditorGroup,
    transformCatalogueToTree,
} from '../utils';
import DataSync from '../../dataSync';
import ajax from '../../../api';
import { TASK_TYPE } from '../../../comm/const';
import {
    CREATE_FOLDER_PREFIX,
    CREATE_TASK_PREFIX,
    EDIT_FOLDER_PREFIX,
    EDIT_TASK_PREFIX,
    FOLDERTREE_CONTEXT_EDIT,
    TASK_CREATE_ID,
    TASK_RUN_ID,
    TASK_SAVE_ID,
    TASK_SUBMIT_ID,
} from '../utils/const';
import store from '../../../store';
import { workbenchAction } from '../../../controller/dataSync/actionType';
import { editorAction } from '../../../controller/editor/actionTypes';
import { cloneDeep } from 'lodash';
import { getStatusBarLanguage, updateStatusBarLanguage } from '../statusBar';

const OpenView = connect(molecule.editor, Open);
const EditFolderView = connect(molecule.editor, EditFolder);

/**
 * Update task tree node
 * @param data the back-end data
 */
function updateTree(data: any) {
    getCatalogueViaNode({
        id: data.parentId,
        catalogueType: data.catalogueType,
    }).then((data) => {
        const nextNode = transformCatalogueToTree(data, 'task');
        nextNode && molecule.folderTree.update(nextNode);
    });
}

/**
 * Open a tab for creating task
 * @param id the nodePid in tab
 */
function openCreateTab(id: number) {
    const onSubmit = (values: any) => {
        return new Promise<boolean>((resolve) => {
            const params = {
                ...values,
                computeType: 1,
                lockVersion: 0,
                version: 0,
            };
            ajax.addOfflineTask(params)
                .then((res: any) => {
                    if (res.code === 1) {
                        const { data } = res;
                        molecule.editor.closeTab(tabId, 1);
                        molecule.explorer.forceUpdate();
                        updateTree(data);
                        const { current } = molecule.editor.getState();
                        if (current?.tab?.data.taskType === TASK_TYPE.SQL) {
                            molecule.editor.updateActions([
                                { id: TASK_RUN_ID, disabled: false },
                            ]);
                        }
                    }
                })
                .finally(() => {
                    resolve(false);
                });
        });
    };

    const tabId = `${CREATE_TASK_PREFIX}_${new Date().getTime()}`;
    const tabData = {
        id: tabId,
        modified: false,
        name: localize('create task', '新建任务'),
        data: {
            value: id,
        },
        renderPane: () => {
            return <OpenView key={tabId} tabId={tabId} onSubmit={onSubmit} />;
        },
    };

    molecule.editor.open(tabData);
    molecule.explorer.forceUpdate();
}

function init() {
    molecule.explorer.onPanelToolbarClick((panel, toolbarId: string) => {
        const getRootNode = () =>
            molecule.folderTree.getState().folderTree?.data![0];
        // 如果是任务刷新，执行重新加载
        if (panel.id === 'sidebar.explore.folders' && toolbarId === 'refresh') {
            const rootNode = getRootNode();
            if (rootNode) {
                loadTreeNode(rootNode.data, 'task');
            }
        }
    });

    molecule.folderTree.setEntry(
        <div style={{ marginTop: 20, textAlign: 'center' }}>
            未找到任务开发目录，请联系管理员
        </div>
    );
}

// 初始化右键菜单
function initContextMenu() {
    molecule.folderTree.onRightClick((treeNode, menu) => {
        if (!treeNode.isLeaf) {
            // insert these menus into folder context
            menu.splice(0, 1, { id: 'task.create', name: '新建任务' });
            menu.splice(3, 0, {
                id: FOLDERTREE_CONTEXT_EDIT,
                name: '编辑',
            });
        } else {
            menu.splice(2, 0, {
                id: FOLDERTREE_CONTEXT_EDIT,
                name: '编辑',
            });
        }
    });
}

function createTask() {
    molecule.folderTree.onCreate((type, id) => {
        if (type === 'File') {
            resetEditorGroup();
            openCreateTab(id!);
        } else if (type === 'Folder') {
            // work through addNode function
            molecule.folderTree.add(
                new TreeNodeModel({
                    id: `${CREATE_FOLDER_PREFIX}_${new Date().getTime()}`,
                    name: '',
                    isLeaf: false,
                    fileType: FileTypes.Folder,
                    isEditable: true,
                    data: {
                        parentId: id,
                    },
                }),
                id
            );
        }
    });
}

function editTreeNodeName() {
    function renameFile(file: any) {
        const { data, name } = file;
        ajax.saveOfflineJobData({
            ...data,
            name,
        }).then((res: any) => {
            if (res.code === 1) {
                updateTree({
                    catalogueType: 'TaskDevelop',
                    parentId: data.parentId,
                });
                molecule.explorer.forceUpdate();
            }
        });
    }

    function createFolder(file: any) {
        const {
            name,
            data: { parentId },
        } = file;
        ajax.addOfflineCatalogue({
            nodeName: name,
            nodePid: parentId,
        }).then((res: any) => {
            if (res.code === 1) {
                updateTree({ catalogueType: 'TaskDevelop', parentId });
                molecule.explorer.forceUpdate();
            }
        });
    }

    function renameFolder(file: any) {
        const {
            name,
            data: { id, parentId },
        } = file;
        ajax.editOfflineCatalogue({
            type: 'folder',
            engineCatalogueType: 0,
            id,
            nodeName: name,
            nodePid: parentId,
        }).then((res: any) => {
            if (res.code === 1) {
                updateTree({ catalogueType: 'TaskDevelop', parentId });
                molecule.explorer.forceUpdate();
            }
        });
    }
    molecule.folderTree.onUpdateFileName((file) => {
        const { fileType, id } = file;
        if (fileType === 'File') {
            renameFile(file);
        } else {
            if (`${id}`.startsWith(CREATE_FOLDER_PREFIX)) {
                createFolder(file);
            } else {
                renameFolder(file);
            }
        }
    });
}

// TODO: refactor, tab 数据可以从molecule中取出，无需存在redux中
export function openTaskInTab(taskId: any, file?: any) {
    if (!file) {
        // 通过id打开任务
        file = molecule.folderTree.get(taskId);
        if (!file) return message.error('此任务不存在');
    }
    if (molecule.editor.isOpened(taskId.toString())) {
        const groupId = molecule.editor.getGroupIdByTab(taskId.toString())!;
        molecule.editor.setActive(groupId, taskId.toString());
        return;
    }

    const { id: fileId, name: fileName, data: fileData, location, icon } = file;
    ajax.getOfflineTaskByID({ id: fileId }).then((res) => {
        if (fileData.taskType === TASK_TYPE.SQL) {
            const { success, data } = res;
            if (success) {
                // save to redux
                store.dispatch({
                    type: workbenchAction.LOAD_TASK_DETAIL,
                    payload: data,
                });
                store.dispatch({
                    type: workbenchAction.OPEN_TASK_TAB,
                    payload: fileId,
                });

                store.dispatch({
                    type: editorAction.GET_TAB,
                    key: fileId,
                });

                // open in molecule
                const tabData = {
                    id: fileId.toString(),
                    name: fileName,
                    data: {
                        ...data,
                        value: data.sqlText,
                        language: 'sparksql',
                        taskDesc: fileData.taskDesc,
                    },
                    icon,
                    breadcrumb:
                        location?.split('/')?.map((item: string) => ({
                            id: item,
                            name: item,
                        })) || [],
                };
                molecule.editor.open(tabData);
                molecule.editor.updateActions([
                    { id: TASK_RUN_ID, disabled: false },
                    { id: TASK_SAVE_ID, disabled: false },
                    { id: TASK_SUBMIT_ID, disabled: false },
                ]);
            }
        } else if (fileData.taskType === TASK_TYPE.SYNC) {
            const { success, data } = res;
            if (success) {
                // save to redux
                store.dispatch({
                    type: workbenchAction.LOAD_TASK_DETAIL,
                    payload: data,
                });
                store.dispatch({
                    type: workbenchAction.OPEN_TASK_TAB,
                    payload: fileId,
                });

                store.dispatch({
                    type: editorAction.GET_TAB,
                    key: fileId,
                });

                // open in molecule
                const tabData = {
                    id: fileId.toString(),
                    name: fileName,
                    data: {
                        ...data,
                        value: data.sqlText,
                        taskDesc: fileData.taskDesc,
                    },
                    icon,
                    breadcrumb:
                        location?.split('/')?.map((item: string) => ({
                            id: item,
                            name: item,
                        })) || [],
                    renderPane: () => {
                        return <DataSync key={fileId} />;
                    },
                };
                molecule.editor.open(tabData);
                molecule.editor.updateActions([
                    { id: TASK_RUN_ID, disabled: false },
                    { id: TASK_SAVE_ID, disabled: false },
                    { id: TASK_SUBMIT_ID, disabled: false },
                ]);
            }
        } else {
            resetEditorGroup();
        }
    });

    molecule.explorer.forceUpdate();
    updateStatusBarLanguage(getStatusBarLanguage(fileData.taskType));
}

function onSelectFile() {
    molecule.folderTree.onSelectFile((file) => {
        molecule.folderTree.setActive(file.id);
        openTaskInTab(file.id, file);
    });
}

function onRemove() {
    molecule.folderTree.onRemove((id) => {
        ajax.delOfflineTask({ taskId: id }).then((res: any) => {
            if (res.code == 1) {
                message.success('删除成功');
                store.dispatch({
                    type: workbenchAction.CLOSE_TASK_TAB,
                    payload: res.data,
                });
            }
            return res;
        });
    });
}

function contextMenu() {
    molecule.folderTree.onContextMenu((menu, treeNode) => {
        switch (menu.id) {
            case 'task.create': {
                resetEditorGroup();

                openCreateTab(treeNode!.data.id);
                break;
            }
            case FOLDERTREE_CONTEXT_EDIT: {
                resetEditorGroup();
                const isFile = treeNode!.fileType === 'File';

                const tabId = isFile
                    ? `${EDIT_TASK_PREFIX}_${new Date().getTime()}`
                    : `${EDIT_FOLDER_PREFIX}_${new Date().getTime()}`;

                const afterSubmit = (params: any, values: any) => {
                    // 更新旧结点所在的文件夹
                    updateTree({
                        parentId: treeNode!.data.parentId,
                        catalogueType: 'TaskDevelop',
                    });

                    // 如果文件的位置发生了移动，则还需要更新新结点所在的文件夹
                    if (treeNode!.data.parentId !== params.nodePid) {
                        updateTree({
                            parentId: params.nodePid,
                            catalogueType: 'TaskDevelop',
                        });
                    }

                    // 确保 editor 的 tab 的 id 和 tree 的 id 保持一致
                    // 同步去更新 tab 的 name
                    const isOpened = molecule.editor.isOpened(treeNode!.id);
                    if (isOpened) {
                        molecule.editor.updateTab({
                            id: treeNode!.id,
                            name: values.name,
                        });
                    }

                    // 关闭当前编辑的 tab
                    const groupId = molecule.editor.getGroupIdByTab(tabId)!;
                    molecule.editor.closeTab(tabId, groupId);

                    // 关闭后编辑任务的 tab 后，需要去更新 actions 的状态
                    const { current } = molecule.editor.getState();
                    if (current?.tab?.data.taskType === TASK_TYPE.SQL) {
                        molecule.editor.updateActions([
                            {
                                id: TASK_RUN_ID,
                                disabled: false,
                            },
                        ]);
                    }
                };

                const onSubmit = (values: any) => {
                    return new Promise<boolean>((resolve) => {
                        const params = {
                            id: treeNode!.data.id,
                            isUseComponent: 0,
                            computeType: 1,
                            lockVersion: 0,
                            version: 0,
                            ...values,
                        };
                        ajax.addOfflineTask(params)
                            .then((res: any) => {
                                if (res.code === 1) {
                                    afterSubmit(params, values);
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
                        ajax.editOfflineCatalogue(params)
                            .then((res: any) => {
                                if (res.code === 1) {
                                    afterSubmit(params, values);
                                }
                            })
                            .finally(() => {
                                resolve(false);
                            });
                    });
                };

                const tabData = {
                    id: tabId,
                    modified: false,
                    data: {},
                    name: isFile
                        ? localize('update task', '编辑任务')
                        : localize('update folder', '编辑文件夹'),
                    renderPane: () => {
                        return (
                            <>
                                {isFile ? (
                                    <OpenView
                                        key={tabId}
                                        tabId={tabId}
                                        record={treeNode!.data}
                                        onSubmit={onSubmit}
                                    />
                                ) : (
                                    <EditFolderView
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
                const isExist = groups.some((group) =>
                    group.data?.some((tab) => tab.id === tabId)
                );
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
    molecule.folderTree.onLoadData((treeNode) => {
        loadTreeNode(treeNode.data!.data, 'task');
    });
}

export default class FolderTreeExtension implements IExtension {
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
        // 重命名
        editTreeNodeName();
    }
}
