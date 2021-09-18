import React, { useState } from 'react';
import {
    ActionBar,
    IMenuItemProps,
    ITreeNodeItemProps,
} from 'molecule/esm/components';
import { Content, Header } from 'molecule/esm/workbench/sidebar';
import { connect } from 'molecule/esm/react';
import functionManagerService from '../../services/functionManagerService';
import { FileTypes, IFolderTree, TreeNodeModel } from 'molecule/esm/model';
import {
    convertToFunctionsTreeNode,
    getCatalogueViaNode,
} from '../common/utils';
import { FolderTree } from 'molecule/esm/workbench/sidebar/explore';
import FnViewModal from './fnViewModal';
import { LoadEventData } from 'molecule/esm/controller';
import './index.scss';
import {
    FUNCTION_EDIT,
    FUNCTION_NEW_FOLDER,
    FUNCTION_NEW_FUNCTION,
    FUNCTION_REMOVE,
} from './menu';
import { Modal } from 'antd';
import ajax from '../../api';
import FnModal from './fnModal';
import { MENU_TYPE } from '../../comm/const';
import FolderModal from './folderModal';

const confirm = Modal.confirm;
const FolderTreeView = connect(functionManagerService, FolderTree);

interface IFunctionProps {
    panel: any;
    headerToolBar: any[];
}

const supportEngines = [
    {
        name: 'Hadoop',
        value: 1,
    },
];

const FunctionManagerView = ({
    headerToolBar,
    panel,
}: IFunctionProps & IFolderTree) => {
    const [viewVisible, setViewVisible] = useState(false);
    const [isModalShow, setModalShow] = useState(false);
    const [folderVisible, setFolderVisible] = useState(false);
    const [currentMenuData, setMenuData] = useState<any>(undefined);
    const [editData, setEditData] = useState<any>(undefined);
    const [resId, setResId] = React.useState(null);

    const updateNodePid = async (node: ITreeNodeItemProps) => {
        const data = await getCatalogueViaNode(node.data);
        const { id, name, children, type } = data;
        const nextNode = new TreeNodeModel({
            id: `${id}-${type}`,
            name,
            location: name,
            fileType: FileTypes.Folder,
            isLeaf: false,
            data: data,
            children: convertToFunctionsTreeNode(children),
        });

        functionManagerService.update(nextNode);
    };

    const loadData = async (treeNode: LoadEventData) => {
        updateNodePid(treeNode.data!);
    };

    const handleSelect = (file: ITreeNodeItemProps) => {
        functionManagerService.setActive(file.id);
        if (file.isLeaf) {
            setViewVisible(true);
            setResId(file.data.id);
        }
    };

    const handleCloseViewModal = () => {
        setViewVisible(false);
        setResId(null);
    };

    const handleContextMenu = (
        contextMenu: IMenuItemProps,
        treeNode?: ITreeNodeItemProps
    ) => {
        const menuId = contextMenu.id;
        switch (menuId) {
            case FUNCTION_NEW_FUNCTION.id: {
                setModalShow(true);
                setMenuData({ parentId: treeNode!.data.id });
                break;
            }
            case FUNCTION_NEW_FOLDER.id: {
                setFolderVisible(true);
                setMenuData(treeNode!.data);
                break;
            }
            case FUNCTION_EDIT.id: {
                if (treeNode!.data.type === 'file') {
                    ajax.getOfflineFn({
                        functionId: treeNode!.data.id,
                    }).then((res: any) => {
                        if (res.code === 1) {
                            setMenuData({
                                parentId: treeNode!.data.id,
                                formData: { data: res.data },
                            });
                            setModalShow(true);
                        }
                    });
                } else {
                    setFolderVisible(true);
                    setEditData(treeNode!.data);
                }
                break;
            }
            case FUNCTION_REMOVE.id: {
                const isFolder = treeNode?.isLeaf;
                confirm({
                    title: isFolder
                        ? '确认要删除此文件夹吗?'
                        : '确认要删除此函数吗',
                    content: isFolder
                        ? '删除的文件夹无法恢复!'
                        : '删除的函数无法找回！',
                    onOk() {
                        const fun = isFolder
                            ? ajax.delOfflineFn({
                                  functionId: treeNode!.data.id,
                              })
                            : ajax.delOfflineFolder({
                                  id: treeNode!.data.id,
                              });
                        fun.then((res) => {
                            if (res.code) {
                                const parentNode = functionManagerService.get(
                                    `${treeNode!.data.parentId}-folder` as any
                                );
                                if (parentNode) {
                                    updateNodePid(parentNode);
                                }
                            }
                        });
                    },
                    onCancel() {},
                });
                break;
            }
            default:
                break;
        }
    };

    const handleHeaderContextClick = (
        e: React.MouseEvent<Element, MouseEvent>,
        item?: IMenuItemProps
    ) => {
        e.preventDefault();
        switch (item?.id) {
            case FUNCTION_NEW_FUNCTION.id: {
                setModalShow(true);
                break;
            }
            default:
                break;
        }
    };

    const handleToggleCreatFn = () => {
        setModalShow(false);
        setMenuData(undefined);
    };

    const handleCloseFolderModal = () => {
        setFolderVisible(false);
        setEditData(false);
    };

    const handleRightClick = (treeNode: ITreeNodeItemProps) => {
        // Only custom function can have contextmenu
        if (treeNode.data.catalogueType === MENU_TYPE.COSTOMFUC) {
            if (treeNode.data.type === 'file') {
                return [FUNCTION_EDIT, FUNCTION_REMOVE];
            }

            const baseContextMenu = [
                FUNCTION_NEW_FUNCTION,
                FUNCTION_NEW_FOLDER,
            ];
            // root folder can't edit and remove
            if (treeNode.data.level === 1) {
                return baseContextMenu;
            }

            return baseContextMenu.concat([FUNCTION_EDIT, FUNCTION_REMOVE]);
        }
    };

    // 添加文件夹
    const handleAddCatalogue = (params: any) => {
        return ajax.addOfflineCatalogue(params).then((res: any) => {
            if (res.code === 1) {
                const parentNode = functionManagerService.get(
                    `${params.nodePid}-folder` as any
                );
                if (parentNode) {
                    updateNodePid(parentNode);
                }
                return true;
            }
        });
    };

    const handleAddFunction = (params: any) => {
        return ajax.addOfflineFunction(params).then((res: any) => {
            if (res.code === 1) {
                const parentNode = functionManagerService.get(
                    `${params.nodePid}-folder` as any
                );
                if (parentNode) {
                    updateNodePid(parentNode);
                }
                return res;
            }
        });
    };

    const handleEditCatalogue = (params: any) => {
        return ajax
            .editOfflineCatalogue({ ...params, type: 'folder' }) // 文件夹编辑，新增参数固定为folder
            .then((res: any) => {
                if (res.code === 1) {
                    const currentNode = functionManagerService.get(
                        `${params.id}-${params.type}` as any
                    );
                    const parentNode = functionManagerService.get(
                        `${params.nodePid}-${params.type}` as any
                    );
                    // the saving position has been changed
                    if (currentNode?.data.parentId !== params.nodePid) {
                        const parentNode = functionManagerService.get(
                            `${currentNode?.data.parentId}-folder` as any
                        );
                        updateNodePid(parentNode!);
                    }
                    if (parentNode) {
                        updateNodePid(parentNode);
                    }
                    return true;
                }
            });
    };

    const handleEditFunction = (params: any) => {
        return ajax.addOfflineFunction(params).then((res: any) => {
            if (res.code === 1) {
                const currentNode = functionManagerService.get(
                    `${params.id}-file` as any
                );
                // the saving position has been changed
                if (currentNode?.data.parentId !== params.nodePid) {
                    const parentNode = functionManagerService.get(
                        `${currentNode?.data.parentId}-folder` as any
                    );
                    updateNodePid(parentNode!);
                }

                // update the parent node
                const parentNode = functionManagerService.get(
                    `${params.nodePid}-folder` as any
                );
                if (parentNode) {
                    updateNodePid(parentNode);
                }
                return res;
            }
        });
    };

    return (
        <div className="functionManager-container">
            <Header
                title={'函数管理'}
                toolbar={
                    <ActionBar
                        data={headerToolBar}
                        onContextMenuClick={handleHeaderContextClick}
                    />
                }
            />
            <Content>
                <div tabIndex={0} className="functionManager-content">
                    <FolderTreeView
                        onRightClick={handleRightClick}
                        draggable={false}
                        onSelectFile={handleSelect}
                        onLoadData={loadData}
                        onClickContextMenu={handleContextMenu}
                        panel={panel}
                    />
                </div>
            </Content>
            <FnViewModal
                visible={viewVisible}
                fnId={resId}
                closeModal={handleCloseViewModal}
            />
            <FnModal
                isModalShow={isModalShow}
                functionTreeData={
                    functionManagerService.getState().folderTree?.data?.[0]
                        ?.children?.[0]?.children?.[1]?.data
                }
                toggleCreateFn={handleToggleCreatFn}
                engine={supportEngines}
                fnType={currentMenuData ? 'Hadoop' : undefined}
                defaultData={currentMenuData}
                addFn={handleAddFunction}
                editFn={handleEditFunction}
            />
            <FolderModal
                isModalShow={folderVisible}
                cateType={MENU_TYPE.COSTOMFUC}
                toggleCreateFolder={handleCloseFolderModal}
                treeData={currentMenuData}
                defaultData={editData}
                addOfflineCatalogue={handleAddCatalogue}
                editOfflineCatalogue={handleEditCatalogue}
            />
        </div>
    );
};

export default connect(functionManagerService, FunctionManagerView);
