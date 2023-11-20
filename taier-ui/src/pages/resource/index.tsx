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

import { useState } from 'react';
import type molecule from '@dtinsight/molecule';
import type { IActionBarItemProps, IMenuItemProps, ITreeNodeItemProps } from '@dtinsight/molecule/esm/components';
import { ActionBar } from '@dtinsight/molecule/esm/components';
import type { IFolderTree } from '@dtinsight/molecule/esm/model';
import { FileTypes } from '@dtinsight/molecule/esm/model';
import { connect } from '@dtinsight/molecule/esm/react';
import { Content, Header } from '@dtinsight/molecule/esm/workbench/sidebar';
import { FolderTree } from '@dtinsight/molecule/esm/workbench/sidebar/explore/index';
import { message, Modal } from 'antd';
import { debounce } from 'lodash';

import { DetailInfoModal } from '@/components/detailInfo';
import { CATALOGUE_TYPE, ID_COLLECTIONS, RESOURCE_ACTIONS } from '@/constant';
import type { CatalogueDataProps, IResourceProps } from '@/interface';
import { catalogueService } from '@/services';
import ajax from '../../api';
import resourceManagerTree from '../../services/resourceManagerService';
import FolderModal from '../function/folderModal';
import type { IFormFieldProps } from './resModal';
import ResModal from './resModal';
import './index.scss';

const { confirm } = Modal;

const FolderTreeView = connect(resourceManagerTree, FolderTree);

interface IResourceViewProps {
    panel: molecule.model.IActivityBarItem;
    headerToolBar: IActionBarItemProps[];
}

enum DELETE_SOURCE {
    folder,
    file,
}

type IRightClickDataProps = IFormFieldProps | undefined;

export default ({ panel, headerToolBar, entry }: IResourceViewProps & IFolderTree) => {
    const [isModalShow, setModalShow] = useState(false);
    const [isViewModalShow, setViewModalShow] = useState(false);
    const [detailLoading, setDetailLoading] = useState(false);
    const [detailData, setDetailData] = useState<Record<string, any> | undefined>(undefined);
    const [isCoverUpload, setCoverUpload] = useState(false);
    const [rightClickData, setData] = useState<IRightClickDataProps>(undefined);
    const [folderVisible, setFolderVisible] = useState(false);
    const [expandKeys, setExpandKeys] = useState<string[]>([]);
    const [folderData, setFolderData] = useState<
        Partial<Pick<CatalogueDataProps, 'id' | 'parentId' | 'name'>> | undefined
    >(undefined);

    const updateNodePid = async (node: ITreeNodeItemProps) => {
        catalogueService.loadTreeNode(node.data, CATALOGUE_TYPE.RESOURCE);
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

    const handleCreate = () => {
        setFolderVisible(true);
        setFolderData(undefined);
    };

    const debounceRefreshNode = debounce(() => {
        const { folderTree } = resourceManagerTree.getState();
        if (folderTree?.current) {
            if (folderTree?.current.fileType === FileTypes.File) {
                const parentNode = resourceManagerTree.get(`${folderTree?.current.data.parentId}-folder`);
                // 更新父节点
                updateNodePid(parentNode!);
            } else {
                // 更新 update 目录
                updateNodePid(folderTree.current);
            }
        } else {
            const rootFolder = catalogueService.getRootFolder(CATALOGUE_TYPE.RESOURCE);
            if (rootFolder) {
                updateNodePid(rootFolder);
            }
        }
    }, 300);

    const handleHeaderClick = (e: React.MouseEvent<Element, MouseEvent>, item: IMenuItemProps) => {
        e.preventDefault();
        switch (item?.id) {
            // 刷新
            case 'refresh':
                debounceRefreshNode();
                break;
            default:
                break;
        }
    };

    const handleHeaderContextClick = (e: React.MouseEvent<Element, MouseEvent>, item?: IMenuItemProps) => {
        e.preventDefault();
        switch (item?.id) {
            case ID_COLLECTIONS.RESOURCE_UPLOAD:
                handleUpload();
                break;
            case ID_COLLECTIONS.RESOURCE_REPLACE:
                handleReplace();
                break;
            case ID_COLLECTIONS.RESOURCE_CREATE:
                handleCreate();
                break;
            default:
                break;
        }
    };

    const handleRightClick = (treeNode: ITreeNodeItemProps) => {
        const ROOT_FOLDER_ACTIONS = [RESOURCE_ACTIONS.UPLOAD, RESOURCE_ACTIONS.CREATE];
        switch (treeNode.fileType) {
            case FileTypes.File: {
                return [RESOURCE_ACTIONS.REPLACE, RESOURCE_ACTIONS.DELETE];
            }
            case FileTypes.Folder: {
                if (treeNode.name === '资源管理') {
                    return ROOT_FOLDER_ACTIONS;
                }
                return ROOT_FOLDER_ACTIONS.concat([RESOURCE_ACTIONS.EDIT, RESOURCE_ACTIONS.DELETE]);
            }
            case FileTypes.RootFolder: {
                // In general, root folder have no contextMenu, because it can't be clicked
                return ROOT_FOLDER_ACTIONS;
            }
            default:
                return [];
        }
    };

    const handleExpandKeys = (keys: string[]) => {
        setExpandKeys(keys);
    };

    const handleDelete = (treeNode: ITreeNodeItemProps, source: keyof typeof DELETE_SOURCE) => {
        if (source === 'file') {
            confirm({
                title: '确认要删除此资源吗?',
                content: '删除的资源无法找回！',
                onOk() {
                    const params = {
                        resourceId: treeNode.data.id,
                    };
                    ajax.delOfflineRes(params).then((res) => {
                        if (res.code === 1) {
                            message.success('资源删除成功');
                            const parentNode = resourceManagerTree.get(`${treeNode.data.parentId}-folder`)!;
                            // update the parent
                            updateNodePid(parentNode);
                        }
                    });
                },
                onCancel() {},
            });
        } else {
            confirm({
                title: '确认要删除此文件夹吗?',
                content: '删除的文件夹无法恢复!',
                onOk() {
                    const params = {
                        id: treeNode.data.id,
                    };
                    ajax.delOfflineFolder(params).then((res) => {
                        if (res.code === 1) {
                            message.success('文件夹删除成功');
                            const parentNode = resourceManagerTree.get(`${treeNode.data.parentId}-folder`)!;
                            // update the parent
                            updateNodePid(parentNode);
                        }
                    });
                },
                onCancel() {},
            });
        }
    };

    const handleContextMenu = (contextMenu: IMenuItemProps, treeNode?: ITreeNodeItemProps) => {
        const menuId = contextMenu.id;
        switch (menuId) {
            case ID_COLLECTIONS.RESOURCE_DELETE: {
                handleDelete(treeNode!, treeNode!.fileType === FileTypes.File ? 'file' : 'folder');
                break;
            }
            case ID_COLLECTIONS.RESOURCE_EDIT: {
                setFolderData(treeNode!.data);
                setFolderVisible(true);
                break;
            }
            case ID_COLLECTIONS.RESOURCE_UPLOAD: {
                setData({
                    nodePid: treeNode!.data.id,
                });
                handleUpload();
                break;
            }
            case ID_COLLECTIONS.RESOURCE_REPLACE: {
                ajax.getOfflineRes({
                    resourceId: treeNode?.data.id,
                }).then((res) => {
                    if (res.code === 1) {
                        const { originFileName, resourceType, computeType, resourceDesc } = res.data as IResourceProps;
                        setData({
                            id: treeNode?.data.id,
                            originFileName,
                            resourceType,
                            computeType,
                            resourceDesc,
                        });
                        handleReplace();
                    }
                });
                break;
            }
            case ID_COLLECTIONS.RESOURCE_CREATE: {
                setFolderData({ parentId: treeNode!.data.id });
                setFolderVisible(true);
                break;
            }
            default:
                break;
        }
    };

    const handleSelect = (file?: ITreeNodeItemProps) => {
        resourceManagerTree.setActive(file?.id);
        if (file) {
            if (file.isLeaf) {
                setViewModalShow(true);
                setDetailLoading(true);
                ajax.getOfflineRes({
                    resourceId: file.data.id,
                })
                    .then((res) => {
                        if (res.code === 1) {
                            setDetailData(res.data);
                        }
                    })
                    .finally(() => {
                        setDetailLoading(false);
                    });
            }
        }
    };

    const handleCloseViewModal = () => {
        setViewModalShow(false);
        setDetailData(undefined);
    };

    const handleCloseFolderModal = () => {
        setFolderVisible(false);
        setFolderData(undefined);
    };

    // 添加文件夹
    const handleAddCatalogue = (params: { nodePid: number; nodeName: string }) => {
        return ajax.addOfflineCatalogue(params).then((res) => {
            if (res.code === 1) {
                const parentNode = resourceManagerTree.get(`${params.nodePid}-folder`);
                if (parentNode) {
                    updateNodePid(parentNode);
                }
                return true;
            }
            return false;
        });
    };

    // 编辑文件夹
    const handleEditCatalogue = (params: { nodePid: number; nodeName: string; id: number; type: string }) => {
        return ajax
            .editOfflineCatalogue({ ...params, type: 'folder' }) // 文件夹编辑，新增参数固定为folder
            .then((res) => {
                if (res.code === 1) {
                    const currentNode = resourceManagerTree.get(`${params.id}-folder`);
                    const parentNode = resourceManagerTree.get(`${params.nodePid}-folder`);
                    // the saving position has been changed
                    if (currentNode?.data.parentId !== params.nodePid) {
                        const nextParentNode = resourceManagerTree.get(currentNode?.data.parentId);
                        updateNodePid(nextParentNode!);
                    }
                    if (parentNode) {
                        updateNodePid(parentNode);
                    }
                    return true;
                }

                return false;
            });
    };

    // 新增资源
    const handleAddResource = (params: any) => {
        return ajax.addOfflineResource(params).then((res) => {
            if (res.code === 1) {
                message.success('资源上传成功！');
                const parentNode = resourceManagerTree.get(`${params.nodePid}-folder`)!;
                updateNodePid(parentNode);
                return true;
            }

            return false;
        });
    };

    // 替换资源
    const handleReplaceResource = (params: any) => {
        return ajax.replaceOfflineResource(params).then((res) => {
            if (res.code === 1) {
                message.success('资源替换成功！');

                return true;
            }
            return false;
        });
    };

    return (
        <div className="resourceManager-container">
            <Header
                title="资源管理"
                toolbar={
                    <ActionBar
                        data={headerToolBar}
                        onClick={handleHeaderClick}
                        onContextMenuClick={handleHeaderContextClick}
                    />
                }
            />
            <Content>
                <div tabIndex={0} className="resourceManager-content">
                    <FolderTreeView
                        onExpandKeys={handleExpandKeys}
                        expandKeys={expandKeys}
                        onRightClick={handleRightClick}
                        draggable={false}
                        onSelectFile={handleSelect}
                        onLoadData={updateNodePid}
                        onClickContextMenu={handleContextMenu}
                        entry={entry}
                        panel={panel}
                    />
                </div>
            </Content>
            <ResModal
                visible={isModalShow}
                defaultValue={rightClickData}
                isCoverUpload={isCoverUpload}
                onAddResource={handleAddResource}
                onReplaceResource={handleReplaceResource}
                onClose={toggleUploadModal}
            />
            <DetailInfoModal
                title="资源详情"
                data={detailData}
                type={CATALOGUE_TYPE.RESOURCE}
                loading={detailLoading}
                visible={isViewModalShow}
                onCancel={handleCloseViewModal}
            />
            <FolderModal
                dataType={CATALOGUE_TYPE.RESOURCE}
                isModalShow={folderVisible}
                toggleCreateFolder={handleCloseFolderModal}
                treeData={catalogueService.getRootFolder(CATALOGUE_TYPE.RESOURCE)?.data}
                defaultData={folderData}
                addOfflineCatalogue={handleAddCatalogue}
                editOfflineCatalogue={handleEditCatalogue}
            />
        </div>
    );
};
