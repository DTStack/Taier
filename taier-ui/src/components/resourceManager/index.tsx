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
import { message, Modal } from 'antd';
import { Content, Header } from '@dtinsight/molecule/esm/workbench/sidebar';
import { FolderTree } from '@dtinsight/molecule/esm/workbench/sidebar/explore/index';
import type {
	IActionBarItemProps,
	IMenuItemProps,
	ITreeNodeItemProps,
} from '@dtinsight/molecule/esm/components';
import { ActionBar } from '@dtinsight/molecule/esm/components';
import type { IFolderTree, IFolderTreeNodeProps } from '@dtinsight/molecule/esm/model';
import { FileTypes } from '@dtinsight/molecule/esm/model';
import { connect } from '@dtinsight/molecule/esm/react';
import { loadTreeNode } from '@/utils/extensions';
import { CATELOGUE_TYPE, RESOURCE_ACTIONSS_ID_COLLECTION, RESOURCE_ACTION_BAR } from '@/constant';
import resourceManagerTree from '../../services/resourceManagerService';
import ResModal from './resModal';
import ResViewModal from './resViewModal';
import ajax from '../../api';
import { deleteMenu, editMenu } from './menu';
import FolderModal from '../functionManager/folderModal';
import type molecule from '@dtinsight/molecule';
import type { CatalogueDataProps } from '@/interface';
import './index.scss';

const { confirm } = Modal;

const FolderTreeView = connect(resourceManagerTree, FolderTree);

interface IResourceProps {
	panel: molecule.model.IActivityBarItem;
	headerToolBar: IActionBarItemProps[];
}

enum DELETE_SOURCE {
	folder,
	file,
}

export default ({ panel, headerToolBar, entry }: IResourceProps & IFolderTree) => {
	const [isModalShow, setModalShow] = useState(false);
	const [isViewModalShow, setViewModalShow] = useState(false);
	const [resId, setResId] = useState<string | undefined>(undefined);
	const [isCoverUpload, setCoverUpload] = useState(false);
	const [rightClickData, setData] = useState<any>(undefined);
	const [folderVisible, setFolderVisible] = useState(false);
	const [expandKeys, setExpandKeys] = useState<string[]>([]);
	const [folderData, setFolderData] = useState<
		Partial<Pick<CatalogueDataProps, 'id' | 'parentId' | 'name'>> | undefined
	>(undefined);

	const updateNodePid = async (node: ITreeNodeItemProps) => {
		loadTreeNode(node.data, CATELOGUE_TYPE.RESOURCE);
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

	const handleHeaderContextClick = (
		e: React.MouseEvent<Element, MouseEvent>,
		item?: IMenuItemProps,
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
				return [{ ...deleteMenu }];
			}
			case FileTypes.Folder: {
				if (treeNode.name === '资源管理') {
					return RESOURCE_ACTION_BAR.concat();
				}
				return RESOURCE_ACTION_BAR.concat([editMenu, deleteMenu]);
			}
			case FileTypes.RootFolder: {
				// In general, root folder have no contextMenu, because it can't be clicked
				return RESOURCE_ACTION_BAR.concat();
			}
			default:
				break;
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
							const parentNode = resourceManagerTree.get(treeNode.data.parentId)!;
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
							const parentNode = resourceManagerTree.get(treeNode.data.parentId)!;
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
			case deleteMenu.id: {
				handleDelete(treeNode!, treeNode!.fileType === FileTypes.File ? 'file' : 'folder');
				break;
			}
			case editMenu.id: {
				setFolderData(treeNode!.data);
				setFolderVisible(true);
				break;
			}
			case RESOURCE_ACTIONSS_ID_COLLECTION.UPLOAD: {
				setData({
					parentId: treeNode!.data.id,
				});
				handleUpload();
				break;
			}
			case RESOURCE_ACTIONSS_ID_COLLECTION.REPLACE: {
				setData({
					parentId: treeNode!.data.id,
				});
				handleReplace();
				break;
			}
			case RESOURCE_ACTIONSS_ID_COLLECTION.CREATE: {
				setFolderData({ parentId: treeNode!.data.id });
				setFolderVisible(true);
				break;
			}
			default:
				break;
		}
	};

	const loadData = async (treeNode: IFolderTreeNodeProps) => {
		loadTreeNode(treeNode.data, CATELOGUE_TYPE.RESOURCE);
	};

	const handleSelect = (file?: ITreeNodeItemProps) => {
		resourceManagerTree.setActive(file?.id);
		if (file) {
			if (file.isLeaf) {
				setViewModalShow(true);
				setResId(file.data.id);
			}
		}
	};

	const handleCloseViewModal = () => {
		setViewModalShow(false);
		setResId(undefined);
	};

	const handleCloseFolderModal = () => {
		setFolderVisible(false);
		setFolderData(undefined);
	};

	// 添加文件夹
	const handleAddCatalogue = (params: { nodePid: number; nodeName: string }) => {
		return ajax.addOfflineCatalogue(params).then((res) => {
			if (res.code === 1) {
				const parentNode = resourceManagerTree.get(params.nodePid);
				if (parentNode) {
					updateNodePid(parentNode);
				}
				return true;
			}
			return false;
		});
	};

	// 编辑文件夹
	const handleEditCatalogue = (params: {
		nodePid: number;
		nodeName: string;
		id: number;
		type: string;
	}) => {
		return ajax
			.editOfflineCatalogue({ ...params, type: 'folder' }) // 文件夹编辑，新增参数固定为folder
			.then((res) => {
				if (res.code === 1) {
					const currentNode = resourceManagerTree.get(params.id);
					const parentNode = resourceManagerTree.get(params.nodePid);
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
				const parentNode = resourceManagerTree.get(params.nodePid)!;
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
					<ActionBar data={headerToolBar} onContextMenuClick={handleHeaderContextClick} />
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
						onLoadData={loadData}
						onClickContextMenu={handleContextMenu}
						entry={entry}
						panel={panel}
					/>
				</div>
			</Content>
			<ResModal
				isModalShow={isModalShow}
				isCoverUpload={isCoverUpload}
				resourceTreeData={
					resourceManagerTree.getState().folderTree?.data?.[0]?.children?.[0]
				}
				defaultData={rightClickData}
				addResource={handleAddResource}
				replaceResource={handleReplaceResource}
				toggleUploadModal={toggleUploadModal}
			/>
			<ResViewModal
				visible={Boolean(isViewModalShow && resId)}
				resId={resId}
				closeModal={handleCloseViewModal}
			/>
			<FolderModal
				dataType={CATELOGUE_TYPE.RESOURCE}
				isModalShow={folderVisible}
				toggleCreateFolder={handleCloseFolderModal}
				treeData={resourceManagerTree.getState().folderTree?.data?.[0]?.children?.[0].data}
				defaultData={folderData}
				addOfflineCatalogue={handleAddCatalogue}
				editOfflineCatalogue={handleEditCatalogue}
			/>
		</div>
	);
};
