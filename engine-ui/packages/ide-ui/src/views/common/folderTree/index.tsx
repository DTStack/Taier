import React from 'react';
import { FileTypes, IExtension, TreeNodeModel } from 'molecule/esm/model';
import { localize } from 'molecule/esm/i18n/localize';
import molecule from 'molecule/esm';
import Open from '../../task/open';
import { resetEditorGroup, updateStatusBarLanguage } from '../utils';
import DataSync from '../../dataSync';
import ajax from '../../../api';
import { TASK_TYPE } from '../../../comm/const';
import { FOLDERTREE_CONTEXT_EDIT, TASK_RUN_ID } from '../utils/const';
import store from '../../../store';
import { workbenchAction } from '../../../controller/dataSync/actionType';
import { editorAction } from '../../../controller/editor/actionTypes';

function convertToTreeNode(data: any[]) {
    if (!data) {
        return;
    }
    return data.map((child) => {
        const { id, name, children, type } = child;
        const node: TreeNodeModel = new TreeNodeModel({
            id,
            name: !name ? '数据开发' : name,
            location: name,
            fileType: type === 'folder' ? FileTypes.Folder : FileTypes.File,
            isLeaf: type !== 'folder',
            data: child,
            children: convertToTreeNode(children),
        });

        return node;
    });
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
            const {  children } = res.data;
            const devData = children.filter((item: any) => item.catalogueType === 'TaskManager')[0].children[0]
            const { id, name, children: child } = devData;
            // 根目录
            const node = new TreeNodeModel({
                id,
                name: name || '数据开发',
                location: name,
                fileType: FileTypes.RootFolder,
                data: devData,
                children: convertToTreeNode(child),
            });

            molecule.folderTree.add(node);
        }
    });
}

// 初始化右键菜单
function initContenxtMenu() {
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

            const onSubmit = (values: any) => {
                return new Promise<boolean>((resolve)=> {
                    // addOfflineTask(values, isEditExist, defaultData)
                    const params = { ...values, nodePid: 233, computeType: 1, isUseComponent: 0, lockVersion: 0, version: 0, componentVersion: '2.1' }
                    ajax.addOfflineTask(params)
                        .then((res: any) => {
                            if (res.code === 1) {
                                const { data } = res;
                                const { id, name } = data;
                                molecule.editor.closeTab(tabId, 1);
                                molecule.explorer.forceUpdate();
                                const node = new TreeNodeModel({
                                    id,
                                    name,
                                    fileType: FileTypes.File,
                                    isLeaf: true,
                                    data: {
                                        ...data,
                                        language: 'sql',
                                    },
                                });
    
                                molecule.folderTree.add(node, 233);
    
                                const { current } = molecule.editor.getState();
                                if (current?.tab?.data.taskType === TASK_TYPE.SQL) {
                                    molecule.editor.updateActions([
                                        { id: TASK_RUN_ID, disabled: false },
                                    ]);
                                }
                            }
                        })
                        .finally(() => {
                            resolve(false)
                        })
                })
                
            };

            const tabId = `createTask_${new Date().getTime()}`;

            const tabData = {
                id: tabId,
                modified: false,
                name: localize('create task', '新建任务'),
                data: {
                    value: id,
                },
                renderPane: () => {
                    return <Open currentId={id} onSubmit={onSubmit} />;
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
        file.data.taskType &&
            updateStatusBarLanguage({
                id: 'language',
                sortIndex: 3,
                name: file.data.taskType,
            });
    });
}

export default class FolderTreeExtension implements IExtension {
    activate() {
        init();
        initContenxtMenu();

        createTask();
        onSelectFile();

        molecule.folderTree.onContextMenu((treeNode, menu) => {
            switch (menu.id) {
                case FOLDERTREE_CONTEXT_EDIT: {
                    console.log('treeNode:', treeNode);
                    resetEditorGroup();

                    const onSubmit = (values: any) => {
                        return new Promise<boolean>((resolve)=> {
                            // addOfflineTask(values, isEditExist, defaultData)
                            const params = { ...values, nodePid: 233, computeType: 1, isUseComponent: 0, lockVersion: 0, version: 0, componentVersion: '2.1' }
                            ajax.addOfflineTask(params)
                                .then((res: any) => {
                                    if (res.code === 1) {
                                        const { data } = res;
                                        const { id, name } = data;
                                        molecule.editor.closeTab(tabId, 1);
                                        molecule.explorer.forceUpdate();
                                        const node = new TreeNodeModel({
                                            id,
                                            name,
                                            fileType: FileTypes.File,
                                            isLeaf: true,
                                            data: {
                                                ...data,
                                                language: 'sql',
                                            },
                                        });
            
                                        molecule.folderTree.add(node, 233);
            
                                        const { current } = molecule.editor.getState();
                                        if (current?.tab?.data.taskType === TASK_TYPE.SQL) {
                                            molecule.editor.updateActions([
                                                { id: TASK_RUN_ID, disabled: false },
                                            ]);
                                        }
                                    }
                                })
                                .finally(() => {
                                    resolve(false)
                                })
                        })
                        
                    };

                    const tabId = `createTask_${new Date().getTime()}`;

                    const tabData = {
                        id: tabId,
                        modified: false,
                        data: {},
                        name: localize('create task', '新建任务'),
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
                    break;
                }
                default:
                    break;
            }
        });
    }
}
