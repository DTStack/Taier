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

import { message, Modal } from 'antd';
import type { IExtension, IFolderTreeNodeProps } from '@dtinsight/molecule/esm/model';
import { FileTypes, TreeNodeModel } from '@dtinsight/molecule/esm/model';
import { localize } from '@dtinsight/molecule/esm/i18n/localize';
import molecule from '@dtinsight/molecule/esm';
import Open from '@/components/task/open';
import EditFolder from '@/components/task/editFolder';
import DataSync from '@/components/dataSync';
import {
	transformCatalogueToTree,
	loadTreeNode,
	resetEditorGroup,
	getCatalogueViaNode,
	fileIcon,
	performSyncTaskActions,
} from '@/utils/extensions';
import api from '@/api';
import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import {
	CATELOGUE_TYPE,
	MENU_TYPE_ENUM,
	TASK_TYPE_ENUM,
	CREATE_FOLDER_PREFIX,
	CREATE_TASK_PREFIX,
	EDIT_FOLDER_PREFIX,
	EDIT_TASK_PREFIX,
	FOLDERTREE_CONTEXT_EDIT,
	TASK_RUN_ID,
	TASK_SAVE_ID,
	TASK_SUBMIT_ID,
	DATA_SYNC_TYPE,
} from '@/constant';
import type { CatalogueDataProps, IOfflineTaskProps } from '@/interface';
import { mappingTaskTypeToLanguage } from '@/utils/enums';
import StreamCollection from '@/components/streamCollection';

/**
 * Update task tree node
 * @param data the back-end data
 */
function updateTree(data: Partial<CatalogueDataProps>) {
	return getCatalogueViaNode({
		id: data.parentId,
		catalogueType: MENU_TYPE_ENUM.TASK_DEV,
	}).then((treeData) => {
		const nextNode = transformCatalogueToTree(treeData, CATELOGUE_TYPE.TASK);
		if (nextNode) {
			molecule.folderTree.update(nextNode);
		}
	});
}

/**
 * Open a tab for creating task
 */
function openCreateTab(id?: string) {
	const onSubmit = (values: any) => {
		return new Promise<boolean>((resolve) => {
			const params = {
				...values,
				computeType: 1,
				parentId: values.nodePid,
				version: 0,
			};
			api.addOfflineTask(params)
				.then((res) => {
					if (res.code === 1) {
						const { data } = res;
						const groupId = molecule.editor.getGroupIdByTab(tabId);
						if (!groupId) return;
						molecule.editor.closeTab(tabId, groupId);
						molecule.explorer.forceUpdate();
						// open this brand-new task
						updateTree(data).then(() => {
							openTaskInTab(data.id);
						});
					}
				})
				.finally(() => {
					resolve(false);
				});
		});
	};

	const tabId = `${CREATE_TASK_PREFIX}_${new Date().getTime()}`;
	const { folderTree } = molecule.folderTree.getState();
	if (!folderTree?.current && !folderTree?.data?.length) return;
	const tabData = {
		id: tabId,
		modified: false,
		name: localize('create task', '新建任务'),
		icon: 'file-add',
		data: {
			// always create task under the root node or create task from context menu
			nodePid: id || folderTree.data?.[0].id,
		},
		renderPane: () => {
			return <Open key={tabId} onSubmit={onSubmit} />;
		},
	};

	molecule.editor.open(tabData);
	molecule.explorer.forceUpdate();
}

function init() {
	molecule.explorer.onPanelToolbarClick((panel, toolbarId: string) => {
		const getRootNode = () => molecule.folderTree.getState().folderTree?.data![0];
		const { SAMPLE_FOLDER_PANEL_ID } = molecule.builtin.getConstants();
		// 如果是任务刷新，执行重新加载
		if (panel.id === SAMPLE_FOLDER_PANEL_ID && toolbarId === 'refresh') {
			const rootNode = getRootNode();
			if (rootNode) {
				loadTreeNode(rootNode.data, CATELOGUE_TYPE.TASK);
			}
		}
	});
}

// 初始化右键菜单
function initContextMenu() {
	// remove folderTree default contextMenu
	molecule.folderTree.setState({
		folderTree: { ...molecule.folderTree.getState().folderTree, folderPanelContextMenu: [] },
	});
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
		if (!id && !molecule.folderTree.getState().folderTree?.data?.length) {
			message.error('获取任务开发目录失败，请联系管理员');
			return;
		}
		if (type === 'File') {
			resetEditorGroup();
			openCreateTab();
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
				id,
			);
		}
	});
}

function editTreeNodeName() {
	function renameFile(file: any) {
		const { data, name } = file;
		if (!name) {
			updateTree({
				parentId: data.parentId,
			});
			return;
		}
		api.saveOfflineJobData({
			...data,
			name,
		}).then((res: any) => {
			if (res.code === 1) {
				updateTree({
					parentId: data.parentId,
				});
				molecule.explorer.forceUpdate();
			}
		});
	}

	// 创建文件夹
	function createFolder(file: IFolderTreeNodeProps) {
		const {
			name,
			data: { parentId },
			id,
		} = file;
		if (!name) {
			return;
		}
		const [nodePid] = parentId.split('-');
		api.addOfflineCatalogue({
			nodeName: name,
			nodePid,
		}).then((res: any) => {
			if (res.code === 1) {
				updateTree({ parentId: nodePid });
				molecule.explorer.forceUpdate();
			} else {
				molecule.folderTree.remove(id);
			}
		});
	}

	function renameFolder(file: any) {
		const {
			name,
			data: { id, parentId },
		} = file;
		if (!name) {
			updateTree({ parentId });
			return;
		}
		api.editOfflineCatalogue({
			type: 'folder',
			engineCatalogueType: 0,
			id,
			nodeName: name,
			nodePid: parentId,
		}).then((res: any) => {
			if (res.code === 1) {
				updateTree({ parentId: id });
				molecule.explorer.forceUpdate();
			}
		});
	}
	molecule.folderTree.onUpdateFileName((file) => {
		const { fileType, id } = file;
		if (fileType === 'File') {
			renameFile(file);
		} else if (`${id}`.startsWith(CREATE_FOLDER_PREFIX)) {
			createFolder(file);
		} else {
			renameFolder(file);
		}
	});
}

export function openTaskInTab(taskId: any, file?: any) {
	if (!file) {
		// 通过id打开任务
		// eslint-disable-next-line no-param-reassign
		file = molecule.folderTree.get(taskId);
		if (!file) return message.error('此任务不存在');
	}
	if (molecule.editor.isOpened(taskId.toString())) {
		const groupId = molecule.editor.getGroupIdByTab(taskId.toString())!;
		molecule.editor.setActive(groupId, taskId.toString());
		return;
	}

	const { id: fileId, location } = file;
	api.getOfflineTaskByID({ id: fileId }).then((res) => {
		const { success, data } = res as { success: boolean; data: IOfflineTaskProps };
		if (success) {
			switch (data.taskType) {
				case TASK_TYPE_ENUM.SQL:
				case TASK_TYPE_ENUM.HIVESQL: {
					const tabData = {
						id: fileId.toString(),
						name: data.name,
						data: {
							...data,
							// set sqlText into value so that molecule-editor could read from this
							value: data.sqlText,
							language: mappingTaskTypeToLanguage(data.taskType),
						},
						icon: fileIcon(data.taskType, CATELOGUE_TYPE.TASK),
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
					break;
				}

				case TASK_TYPE_ENUM.SYNC: {
					// open in molecule
					const tabData: molecule.model.IEditorTab = {
						id: fileId.toString(),
						name: data.name,
						data: {
							...data,
							value: data.sqlText,
							language: 'json',
							taskDesc: data.taskDesc,
						},
						icon: fileIcon(data.taskType, CATELOGUE_TYPE.TASK),
						breadcrumb:
							location?.split('/')?.map((item: string) => ({
								id: item,
								name: item,
							})) || [],
					};

					// 向导模式渲染数据同步任务，脚本模式渲染编辑器
					if (data.createModel === DATA_SYNC_TYPE.GUIDE) {
						tabData.renderPane = () => {
							return <DataSync key={fileId} />;
						};
						// 向导模式不需要设置编辑器语言
						Reflect.deleteProperty(tabData.data!, 'language');
					}

					molecule.editor.open(tabData);
					performSyncTaskActions();
					molecule.editor.updateActions([
						{ id: TASK_RUN_ID, disabled: false },
						{ id: TASK_SAVE_ID, disabled: false },
						{ id: TASK_SUBMIT_ID, disabled: false },
					]);
					break;
				}

				case TASK_TYPE_ENUM.DATA_COLLECTION: {
					const tabData: molecule.model.IEditorTab = {
						id: fileId.toString(),
						name: data.name,
						data: {
							...data,
							value: data.sqlText,
						},
						icon: fileIcon(data.taskType, CATELOGUE_TYPE.TASK),
						breadcrumb:
							location?.split('/')?.map((item: string) => ({
								id: item,
								name: item,
							})) || [],
					};
					if (data.createModel === DATA_SYNC_TYPE.GUIDE) {
						tabData.renderPane = () => <StreamCollection key={fileId} />;
					} else {
						tabData.data = { ...tabData.data, language: 'sql' };
					}
					molecule.editor.open(tabData);
					performSyncTaskActions();
					molecule.editor.updateActions([
						{ id: TASK_SAVE_ID, disabled: false },
						{ id: TASK_SUBMIT_ID, disabled: false },
					]);
					break;
				}

				case TASK_TYPE_ENUM.FLINKSQL: {
					const tabData = {
						id: fileId.toString(),
						name: data.name,
						data: {
							...data,
							// set sqlText into value so that molecule-editor could read from this
							value: data.sqlText,
							language: 'flinksql',
						},
						icon: fileIcon(data.taskType, CATELOGUE_TYPE.TASK),
						breadcrumb:
							location?.split('/')?.map((item: string) => ({
								id: item,
								name: item,
							})) || [],
					};
					molecule.editor.open(tabData);
					molecule.editor.updateActions([
						{ id: TASK_SAVE_ID, disabled: false },
						{ id: TASK_SUBMIT_ID, disabled: false },
					]);
					break;
				}
				default:
					break;
			}
		} else {
			resetEditorGroup();
		}
	});

	molecule.explorer.forceUpdate();
}

function onSelectFile() {
	molecule.folderTree.onSelectFile((file) => {
		molecule.folderTree.setActive(file.id);
		openTaskInTab(file.id, file);
	});
}

function onRemove() {
	molecule.folderTree.onRemove((id) => {
		const treeNode = molecule.folderTree.get(id);
		const type = treeNode?.data?.type;
		Modal.confirm({
			title: `确认要删除此${type === 'file' ? '任务' : '文件夹'}吗?`,
			content: `删除的${type === 'file' ? '任务' : '文件夹'}无法${
				type === 'file' ? '找回' : '恢复'
			}！`,
			onOk() {
				if (treeNode?.data?.type === 'folder') {
					api.delOfflineFolder({ id: treeNode.data.id }).then((res) => {
						if (res.code === 1) {
							message.success('删除成功');
							molecule.folderTree.remove(id);
						}
						return res;
					});
				} else if (treeNode?.data?.type === 'file') {
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
				}
			},
			onCancel() {},
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
					});

					// 如果文件的位置发生了移动，则还需要更新新结点所在的文件夹
					if (treeNode!.data.parentId !== params.nodePid) {
						updateTree({
							parentId: params.nodePid,
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
					if (current?.tab?.data.taskType === TASK_TYPE_ENUM.SQL) {
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
							version: 0,
							...values,
						};
						api.addOfflineTask(params)
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
						api.editOfflineCatalogue(params)
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
					breadcrumb:
						treeNode?.location?.split('/')?.map((item: string) => ({
							id: item,
							name: item,
						})) || [],
					name: isFile
						? localize('update task', '编辑任务')
						: localize('update folder', '编辑文件夹'),
					renderPane: () => {
						return (
							<>
								{isFile ? (
									<Open key={tabId} record={treeNode!.data} onSubmit={onSubmit} />
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
		loadTreeNode(treeNode.data, CATELOGUE_TYPE.TASK).then((res) => {
			if (res) {
				callback(res);
			}
		});
	});
}

export default class FolderTreeExtension implements IExtension {
	id: UniqueId = 'folderTree';
	name: string = 'folderTree';
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
		// 重命名
		editTreeNodeName();
	}
}
