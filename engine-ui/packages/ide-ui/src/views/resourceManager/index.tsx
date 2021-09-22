import React from 'react';
import { Content, Header } from 'molecule/esm/workbench/sidebar';
import { FolderTree } from 'molecule/esm/workbench/sidebar/explore/index';
import {
    ActionBar,
    IMenuItemProps,
    ITreeNodeItemProps,
} from 'molecule/esm/components';
import './index.scss';
import { FileTypes, TreeNodeModel } from 'molecule/esm/model';
import { LoadEventData } from 'molecule/esm/controller';
import { connect } from 'molecule/esm/react';
import resourceManagerTree from '../../services/resourceManagerService';
import ResModal from './resModal';
import ResViewModal from './resViewModal';
import ajax from '../../api';
import { loadTreeNode } from '../common/utils';
import { folderMenu } from '../common/sidebar';
import { deleteMenu, editMenu } from './menu';
import { message, Modal } from 'antd';

const FolderTreeView = connect(resourceManagerTree, FolderTree);

interface IResourceProps {
    panel: any;
    headerToolBar: any[];
}

enum DELETE_SOURCE {
    folder,
    file,
}

export default ({ panel, headerToolBar }: IResourceProps) => {
    const [isModalShow, setModalShow] = React.useState(false);
    const [isViewModalShow, setViewModalShow] = React.useState(false);
    const [resId, setResId] = React.useState(null);
    const [isCoverUpload, setCoverUpload] = React.useState(false);
    const [rightClickData, setData] = React.useState<any>(undefined);

    const updateNodePid = async (node: ITreeNodeItemProps) => {
        loadTreeNode(node.data, 'resource');
    };

    const handleUpload = () => {
        setModalShow(true);
        setCoverUpload(false);
    };

    const handleReplace = () => {
        setModalShow(true);
        setCoverUpload(true);
    };

    const toggleUploadModal = () => {
        setModalShow(false);
        setData(undefined);
    };

    const getCurrentSelectNode = () => {
        const folderTreeState = resourceManagerTree.getState();
        const { data, current } = folderTreeState?.folderTree || {};
        // The current selected node id or the first root node
        const nodeId = current?.id || data?.[0]?.id;
        return nodeId;
    };

    const handleCreate = () => {
        const nodeId = getCurrentSelectNode();
        resourceManagerTree.add(
            new TreeNodeModel({
                // temporary id
                id: new Date().getTime(),
                name: '',
                fileType: FileTypes.Folder,
                icon: 'file-code',
                isLeaf: false,
                isEditable: true,
            }),
            nodeId
        );
    };

    const handleHeaderContextClick = (
        e: React.MouseEvent<Element, MouseEvent>,
        item?: IMenuItemProps
    ) => {
        e.preventDefault();
        switch (item?.id) {
            case 'upload':
                handleUpload();
                break;
            case 'replace':
                handleReplace();
                break;
            case 'create-folder':
                handleCreate();
                break;
            default:
                break;
        }
    };

    const handleRightClick = (treeNode: ITreeNodeItemProps) => {
        switch (treeNode.fileType) {
            case FileTypes.File: {
                return [Object.assign({}, deleteMenu)];
            }
            case FileTypes.Folder: {
                if (treeNode.name === '资源管理') {
                    return folderMenu.concat();
                }
                return folderMenu.concat([editMenu, deleteMenu]);
            }
            case FileTypes.RootFolder: {
                // In general, root folder have no contextMenu, because it can't be clicked
                return folderMenu.concat();
            }
            default:
                break;
        }
    };

    const handleDelete = (
        treeNode: ITreeNodeItemProps,
        source: keyof typeof DELETE_SOURCE
    ) => {
        if (source === 'file') {
            Modal.confirm({
                title: '确认要删除此资源吗?',
                content: '删除的资源无法找回！',
                onOk() {
                    const params = {
                        resourceId: treeNode.data.id,
                    };
                    ajax.delOfflineRes(params).then((res: any) => {
                        if (res.code === 1) {
                            const parentNode = resourceManagerTree.get(
                                treeNode.data.parentId
                            )!;
                            // update the parent
                            updateNodePid(parentNode);
                        }
                    });
                },
                onCancel() {},
            });
        } else {
            Modal.confirm({
                title: '确认要删除此文件夹吗?',
                content: '删除的文件夹无法恢复!',
                onOk() {
                    const params = {
                        id: treeNode.data.id,
                    };
                    ajax.delOfflineFolder(params).then((res: any) => {
                        if (res.code === 1) {
                            const parentNode = resourceManagerTree.get(
                                treeNode.data.parentId
                            )!;
                            // update the parent
                            updateNodePid(parentNode);
                        }
                    });
                },
                onCancel() {},
            });
        }
    };

    const handleContextMenu = (
        contextMenu: IMenuItemProps,
        treeNode?: ITreeNodeItemProps
    ) => {
        const menuId = contextMenu.id;
        switch (menuId) {
            case deleteMenu.id: {
                handleDelete(
                    treeNode!,
                    treeNode!.fileType === FileTypes.File ? 'file' : 'folder'
                );
                break;
            }
            case editMenu.id: {
                treeNode!.isEditable = true;
                resourceManagerTree.update(treeNode!);
                break;
            }
            case 'upload': {
                setData({
                    parentId: treeNode!.data.id,
                });
                handleUpload();
                break;
            }
            case 'replace': {
                setData({
                    parentId: treeNode!.data.id,
                });
                handleReplace();
                break;
            }
            default:
                break;
        }
    };

    const loadData = async (treeNode: LoadEventData) => {
        loadTreeNode(treeNode.data!.data, 'resource');
    };

    const handleRename = (node: ITreeNodeItemProps) => {
        if (!node.name) {
            resourceManagerTree.remove(node.id);
            return;
        }
        // 自增的结点不存在 data 属性，故可以通过这个属性来区分新增还是编辑
        const isUpdate = !!node.data;
        if (isUpdate) {
            ajax.editOfflineCatalogue({
                type: 'folder',
                engineCatalogueType: 0,
                id: node.data.id,
                nodeName: node.name,
                nodePid: node.data.parentId,
            }).then((res: any) => {
                if (res.code === 1) {
                    resourceManagerTree.remove(node.id);
                    const parentNode = resourceManagerTree.get(
                        node.data.parentId
                    );
                    if (parentNode) {
                        updateNodePid(parentNode);
                    }
                }
            });
        } else {
            const nodeId = getCurrentSelectNode();
            const params = {
                nodeName: node.name,
                nodePid: nodeId,
            };
            ajax.addOfflineCatalogue(params).then((res: any) => {
                if (res.code === 1) {
                    resourceManagerTree.remove(node.id);
                    const parentNode = resourceManagerTree.get(nodeId);
                    if (parentNode) {
                        updateNodePid(parentNode);
                    }
                }
            });
        }
    };

    const handleSelect = (file: ITreeNodeItemProps) => {
        resourceManagerTree.setActive(file.id);
        if (file.isLeaf) {
            setViewModalShow(true);
            setResId(file.data.id);
        }
    };

    const handleCloseViewModal = () => {
        setViewModalShow(false);
        setResId(null);
    };

    // 新增资源
    const handleAddResource = (params: any) => {
        return new Promise((resolve, reject) => {
            ajax.addOfflineResource(params).then((res: any) => {
                if (res.code === 1) {
                    message.success('资源上传成功！');
                    const parentNode = resourceManagerTree.get(params.nodePid)!;
                    updateNodePid(parentNode);
                    resolve(true);
                }
            });
        });
    };

    // 替换资源
    const handleReplaceResource = (params: any) => {
        return new Promise((resolve) => {
            ajax.replaceOfflineResource(params).then((res: any) => {
                if (res.code === 1) {
                    message.success('资源替换成功！');
                    resolve(true);
                }
            });
        });
    };

    return (
        <div className="resourceManager-container">
            <Header
                title={'资源管理'}
                toolbar={
                    <ActionBar
                        data={headerToolBar}
                        onContextMenuClick={handleHeaderContextClick}
                    />
                }
            />
            <Content>
                <div tabIndex={0} className="resourceManager-content">
                    <FolderTreeView
                        onRightClick={handleRightClick}
                        draggable={false}
                        onSelectFile={handleSelect}
                        onLoadData={loadData}
                        onUpdateFileName={handleRename}
                        onClickContextMenu={handleContextMenu}
                        panel={panel}
                    />
                </div>
            </Content>
            <ResModal
                isModalShow={isModalShow}
                isCoverUpload={isCoverUpload}
                resourceTreeData={
                    resourceManagerTree.getState().folderTree?.data?.[0]
                        ?.children?.[0]
                }
                defaultData={rightClickData}
                addResource={handleAddResource}
                replaceResource={handleReplaceResource}
                toggleUploadModal={toggleUploadModal}
            />
            <ResViewModal
                visible={isViewModalShow && resId}
                resId={resId}
                closeModal={handleCloseViewModal}
            />
        </div>
    );
};
