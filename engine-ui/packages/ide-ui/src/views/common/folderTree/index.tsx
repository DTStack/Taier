import React from 'react';
import { message } from 'antd';
import { FileTypes, IExtension, TreeNodeModel } from 'molecule/esm/model';
import { localize } from 'molecule/esm/i18n/localize';
import molecule from 'molecule/esm';
import Open from '../../task/open';
import { convertToTreeNode, resetEditorGroup } from '../utils';
import DataSync from '../../dataSync';
import ajax from '../../../api';
import { TASK_TYPE } from '../../../comm/const';
import {
    FOLDERTREE_CONTEXT_EDIT,
    TASK_RUN_ID,
    TASK_SAVE_ID,
    TASK_SUBMIT_ID,
} from '../utils/const';
import { catalogueTypeToDataType } from '../../../components/func'
import store from '../../../store';
import { workbenchAction } from '../../../controller/dataSync/actionType';
import { editorAction } from '../../../controller/editor/actionTypes';
import {
    taskTreeAction,
    resTreeAction,
} from '../../../controller/catalogue/actionTypes';
import { updateCatalogueData } from '../../../controller/catalogue/actionCreator'
import { cloneDeep } from 'lodash';
import functionManagerService from '../../../services/functionManagerService';
import resourceManagerService from '../../../services/resourceManagerService';
import { getStatusBarLanguage, updateStatusBarLanguage } from '../statusBar';

async function loadTreeNode(treeNode: any) {
    if (!treeNode) return;
    const res = await ajax.getOfflineCatalogue({
        isGetFile: !!1,
        nodePid: treeNode!.id,
        catalogueType: treeNode.catalogueType,
        taskType: 1,
        appointProjectId: 1,
        projectId: 1,
        userId: 1,
    });
    if (res.code === 1) {
        updateCatalogueData(store.dispatch, res.data, catalogueTypeToDataType(treeNode.catalogueType))
        const { id, name, children } = res.data;
        const nextNode = new TreeNodeModel({
            id,
            name: name || '文件夹',
            location: name,
            fileType: FileTypes.Folder,
            isLeaf: false,
            data: res.data,
            children: convertToTreeNode(children),
        });

        molecule.folderTree.update(nextNode);
    }
}

function init() {
    ajax.getOfflineCatalogue({
        nodePid: 0,
        isGetFile: true,
        catalogueType: 1,
        taskType: 1,
        appointProjectId: 1,
        projectId: 1,
        userId: 1,
    }).then((res) => {
        if (res.code === 1) {
            const { children } = res.data;

            // Get the Tasks root folder
            const devData = children.filter(
                (item: any) => item.catalogueType === 'TaskManager'
            )[0].children.find((item: any) => item.catalogueType === 'TaskDevelop');

            const funcData = children.filter(
                (item: any) => item.catalogueType === 'FunctionManager'
            )[0];
            const resourceData = children.filter(
                (item: any) => item.catalogueType === 'ResourceManager'
            )[0];
            const { id, name, children: child } = devData;
            // 根目录
            const taskNode = new TreeNodeModel({
                id,
                name: name || '数据开发',
                location: name,
                fileType: FileTypes.RootFolder,
                data: devData,
                children: convertToTreeNode(child),
            });
            store.dispatch({
                type: taskTreeAction.RESET_TASK_TREE,
                payload: devData,
            });
            // 资源根目录
            const resourceNode = new TreeNodeModel({
                id: resourceData.id,
                name: resourceData.name || '资源管理',
                location: resourceData.name,
                fileType: FileTypes.RootFolder,
                data: resourceData,
                children: convertToTreeNode(resourceData.children),
            });
            store.dispatch({
                type: resTreeAction.RESET_RES_TREE,
                payload: resourceData,
            });
            // 函数根目录
            const functionNode = new TreeNodeModel({
                id: funcData.id,
                name: funcData.name || '函数管理',
                location: funcData.name,
                fileType: FileTypes.RootFolder,
                data: funcData,
                children: convertToTreeNode(funcData.children),
            });

            resourceManagerService.add(resourceNode);
            functionManagerService.add(functionNode);
            // Load Task folder tree 
            molecule.folderTree.add(taskNode);
            loadTreeNode(devData);
        }
    });
    // 文件夹树异步加载
    molecule.folderTree.onLoadData((treeNode) => {
        loadTreeNode(treeNode.data!.data);
    });

    molecule.explorer.onPanelToolbarClick((panel, toolbarId: string) => {

        const getRootNode = () => molecule.folderTree.getState().folderTree?.data![0];
        // 如果是任务刷新，执行重新加载
        if (panel.id === 'sidebar.explore.folders' && toolbarId === 'refresh') {
            const rootNode = getRootNode();
            if (rootNode) {
                loadTreeNode(rootNode.data);
            }
        }
        if (panel.id === 'sidebar.explore.folders' && toolbarId === 'collapse') {
            // TODO implements the reset the ExpandedKeys
            // const rootNode = getRootNode();
            // if (rootNode) {
            //     molecule.folderTree.setExpandedKeys([]);
            // }
        }
    });
}

function updateTree(data: any) {
    ajax.getOfflineCatalogue({
        nodePid: data.parentId,
        isGetFile: true,
        catalogueType: data.catalogueType,
        projectId: 1,
        userId: 1,
    }).then((res) => {
        if (res.code === 1) {
            const { data } = res;
            const { id, name, children } = data;
            // 更新目录
            const taskNode = new TreeNodeModel({
                id,
                name: name || '数据开发',
                location: name,
                fileType: FileTypes.Folder,
                data: data,
                children: convertToTreeNode(children),
            });
            molecule.folderTree.update(taskNode);
        }
    });
}

// 初始化右键菜单
function initContextMenu() {
    const { folderTree } = molecule.folderTree.getState();
    const contextMenu = folderTree?.contextMenu?.concat() || [];
    contextMenu.push({
        id: FOLDERTREE_CONTEXT_EDIT,
        name: '编辑',
    });
    molecule.folderTree.setState({
        folderTree: {
            ...folderTree,
            contextMenu,
        },
    });
}

function createTask() {
    molecule.folderTree.onCreate((type, id) => {
        if (type === 'File') {
            resetEditorGroup();

            const tabId = `createTask_${new Date().getTime()}`;

            const onSubmit = (values: any) => {
                return new Promise<boolean>((resolve) => {
                    const params = {
                        ...values,
                        nodePid: 233,
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
                                if (
                                    current?.tab?.data.taskType ===
                                    TASK_TYPE.SQL
                                ) {
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

            const tabData = {
                id: tabId,
                modified: false,
                name: localize('create task', '新建任务'),
                data: {
                    value: id,
                },
                renderPane: () => {
                    return <Open onSubmit={onSubmit} />;
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
        } else if (type === 'Folder') {
            // work through addNode function
            molecule.folderTree.add(
                new TreeNodeModel({
                    id: new Date().getTime(),
                    name: '',
                    isLeaf: false,
                    fileType: FileTypes.Folder,
                    isEditable: true,
                }),
                id
            );
        }
    });
}

function onSelectFile() {
    molecule.folderTree.onSelectFile((file) => {
        if (file.data.taskType === TASK_TYPE.SQL) {
            const id = file.id;
            ajax.getOfflineTaskByID({ id }).then((res) => {
                const { success, data } = res;
                if (success) {
                    // save to redux
                    store.dispatch({
                        type: workbenchAction.LOAD_TASK_DETAIL,
                        payload: data,
                    });
                    store.dispatch({
                        type: workbenchAction.OPEN_TASK_TAB,
                        payload: id,
                    });

                    store.dispatch({
                        type: editorAction.GET_TAB,
                        key: id,
                    });

                    // open in molecule
                    const tabData = {
                        id: id.toString(),
                        name: file.name,
                        data: {
                            ...data,
                            value: data.sqlText,
                            language: 'sparksql',
                            taskDesc: file.data.taskDesc,
                        },
                        breadcrumb:
                            file.location?.split('/')?.map((item: string) => ({
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
            });
        } else if (file.data.taskType === TASK_TYPE.SYNC) {
            const id = file.id;
            ajax.getOfflineTaskByID({ id }).then((res) => {
                const { success, data } = res;
                if (success) {
                    // save to redux
                    store.dispatch({
                        type: workbenchAction.LOAD_TASK_DETAIL,
                        payload: data,
                    });
                    store.dispatch({
                        type: workbenchAction.OPEN_TASK_TAB,
                        payload: id,
                    });

                    // open in molecule
                    const tabData = {
                        id,
                        name: file.name,
                        data: {
                            ...data,
                            value: data.sqlText,
                            taskDesc: file.data.taskDesc,
                        },
                        breadcrumb:
                            file.location?.split('/')?.map((item: string) => ({
                                id: item,
                                name: item,
                            })) || [],
                        renderPane: () => {
                            return <DataSync currentTabData={tabData} />;
                        },
                    };
                    molecule.editor.open(tabData);
                    molecule.editor.updateActions([
                        { id: TASK_RUN_ID, disabled: false },
                    ]);
                }
            });
        } else {
            resetEditorGroup();
        }

        molecule.explorer.forceUpdate();
        updateStatusBarLanguage(getStatusBarLanguage(file.data.taskType));
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
            case FOLDERTREE_CONTEXT_EDIT: {
                resetEditorGroup();

                const tabId = `createTask_${new Date().getTime()}`;

                const onSubmit = (values: any) => {
                    return new Promise<boolean>((resolve) => {
                        const params = {
                            id: treeNode!.data.id,
                            ...values,
                            isUseComponent: 0,
                            nodePid: 233,
                            computeType: 1,
                            lockVersion: 0,
                            version: 0,
                        };
                        ajax.addOfflineTask(params)
                            .then((res: any) => {
                                if (res.code === 1) {
                                    const nextTreeData = cloneDeep(treeNode!);

                                    nextTreeData.data = params;
                                    nextTreeData.name = values.name;

                                    molecule.folderTree.update(treeNode!);

                                    // 确保 editor 的 tab 的 id 和 tree 的 id 保持一致
                                    // 同步去更新 tab 的 name
                                    const isOpened = molecule.editor.isOpened(
                                        treeNode!.id
                                    );
                                    if (isOpened) {
                                        molecule.editor.updateTab({
                                            id: treeNode!.id,
                                            name: values.name,
                                        });
                                    }

                                    molecule.editor.closeTab(tabId, 1);
                                    molecule.explorer.forceUpdate();

                                    // 关闭后编辑任务的 tab 后，需要去更新 actions 的状态
                                    const { current } =
                                        molecule.editor.getState();
                                    if (
                                        current?.tab?.data.taskType ===
                                        TASK_TYPE.SQL
                                    ) {
                                        molecule.editor.updateActions([
                                            {
                                                id: TASK_RUN_ID,
                                                disabled: false,
                                            },
                                        ]);
                                    }
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
                    name: localize('update task', '编辑任务'),
                    renderPane: () => {
                        return (
                            <Open record={treeNode!.data} onSubmit={onSubmit} />
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

export default class FolderTreeExtension implements IExtension {
    activate() {
        init();
        initContextMenu();
        createTask();
        onSelectFile();
        contextMenu();
        onRemove();
    }
}
