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
import type { IFormFieldProps } from '@/components/task/open';
import Open from '@/components/task/open';
import EditFolder from '@/components/task/editFolder';
import DataSync from '@/components/dataSync';
import { fileIcon, getParentNode } from '@/utils/extensions';
import api from '@/api';
import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import { CATELOGUE_TYPE, TASK_TYPE_ENUM, CREATE_MODEL_TYPE, ID_COLLECTIONS } from '@/constant';
import type { IOfflineTaskProps } from '@/interface';
import { IComputeType } from '@/interface';
import { mappingTaskTypeToLanguage } from '@/utils/enums';
import StreamCollection from '@/components/streamCollection';
import { breadcrumbService, catalogueService, editorActionBarService } from '@/services';
import { prettierJSONstring } from '@/utils';
import notification from '@/components/notification';

/**
 * 	实时采集和FlinkSql任务的computeType返回0
 * @param type 任务类型
 * @returns
 */
function getComputeType(type: TASK_TYPE_ENUM): number {
	if (type === TASK_TYPE_ENUM.DATA_ACQUISITION || type === TASK_TYPE_ENUM.SQL) {
		return IComputeType.STFP;
	}
	return IComputeType.HDFS;
}

/**
 * Open a tab for creating task
 */
function openCreateTab(id?: string) {
	const onSubmit = (values: IFormFieldProps) => {
		const { syncModel, resourceIdList, ...restValues } = values;
		return new Promise<boolean>((resolve) => {
			const params: Record<string, any> = {
				...restValues,
				resourceIdList: resourceIdList ? [resourceIdList] : [],
				computeType: getComputeType(values.taskType),
				parentId: values.nodePid,
			};

			// syncModel 需要被放置到 sourceMap 中
			if (syncModel !== undefined) {
				params.sourceMap = { syncModel };
			}
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
							catalogueService
								.loadTreeNode(parentNode.data, CATELOGUE_TYPE.TASK)
								.then(() => {
									// open this brand-new task
									openTaskInTab(data.id);
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
				id: catalogueService.getRootFolder(CATELOGUE_TYPE.TASK)!.id,
				name: catalogueService.getRootFolder(CATELOGUE_TYPE.TASK)!.name,
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
			return <Open key={tabId} onSubmit={onSubmit} />;
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
			catalogueService.loadTreeNode(parentNode.data, CATELOGUE_TYPE.TASK).then(() => {
				// TODO: don't need it after fix the issue https://github.com/DTStack/molecule/issues/724
				if (molecule.folderTree.getState().folderTree?.current?.id !== undefined) {
					document
						.querySelector<HTMLDivElement>('.mo-tree__treenode--active')
						?.classList.remove('mo-tree__treenode--active');
					const dom = document.querySelector<HTMLDivElement>(
						`div.mo-tree__treenode[data-key="${
							molecule.folderTree.getState().folderTree?.current?.id
						}"]`,
					);
					dom?.classList.add('mo-tree__treenode--active');
				}
			});
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
			// remove rename action
			const idx = menu.findIndex(
				(m) => m.id === molecule.builtin.getConstants().RENAME_COMMAND_ID,
			);
			menu.splice(idx, 1);
			// insert these menus into folder context
			menu.splice(0, 1, { id: ID_COLLECTIONS.TASK_CREATE_ID, name: '新建任务' });
			menu.splice(2, 0, {
				id: ID_COLLECTIONS.FOLDERTREE_CONTEXT_EDIT,
				name: '编辑',
			});
		} else {
			// remove rename action
			const idx = menu.findIndex(
				(m) => m.id === molecule.builtin.getConstants().RENAME_COMMAND_ID,
			);
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
		if (!id && !molecule.folderTree.getState().folderTree?.data?.length) {
			message.error('请先配置集群并进行绑定!');
			return;
		}
		if (type === 'File') {
			openCreateTab();
		} else if (type === 'Folder') {
			// work through addNode function
			molecule.folderTree.add(
				new TreeNodeModel({
					id: `${ID_COLLECTIONS.CREATE_FOLDER_PREFIX}_${new Date().getTime()}`,
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
					CATELOGUE_TYPE.TASK,
				);
				molecule.explorer.forceUpdate();
			} else {
				molecule.folderTree.remove(id);
			}
		});
	});
}

const afterSubmit = (params: Record<string, any>, parentId: number, tabId: string) => {
	// 等待更新的文件夹目录
	const pendingUpdateFolderId = new Set([
		// 当前节点变更之前所在的文件夹
		parentId,
		// 当前节点变更后所在的文件夹
		params.nodePid,
	]);

	Promise.all(
		Array.from(pendingUpdateFolderId).map((id) => {
			const folderNode = molecule.folderTree.get(`${id}-folder`);
			if (folderNode) {
				return catalogueService.loadTreeNode(
					{
						id: folderNode.data?.id,
						catalogueType: folderNode.data?.catalogueType,
					},
					CATELOGUE_TYPE.TASK,
				);
			}
			return Promise.resolve();
		}),
	).then(() => {
		const isOpened = molecule.editor.isOpened(params.id.toString());
		if (isOpened) {
			molecule.editor.updateTab({
				id: params.id.toString(),
				name: params.name as string,
				breadcrumb: breadcrumbService.getBreadcrumb(params.id),
			});
		}

		// 关闭当前编辑的 tab
		const groupId = molecule.editor.getGroupIdByTab(tabId)!;
		molecule.editor.closeTab(tabId, groupId);
	});
};

export function openTaskInTab(
	taskId: UniqueId,
	file?: Pick<IFolderTreeNodeProps, 'id' | 'location'> | null,
) {
	if (!file) {
		// 通过id打开任务
		// eslint-disable-next-line no-param-reassign
		file = molecule.folderTree.get(taskId);
		if (!file) return message.error('此任务不存在');
	}
	if (molecule.editor.isOpened(taskId.toString())) {
		const groupId = molecule.editor.getGroupIdByTab(taskId.toString())!;
		molecule.editor.setActive(groupId, taskId.toString());
		window.setTimeout(() => {
			editorActionBarService.performSyncTaskActions();
		}, 0);
		return;
	}

	const { id: fileId } = file;
	api.getOfflineTaskByID({ id: fileId }).then((res) => {
		const { success, data } = res as { success: boolean; data: IOfflineTaskProps };
		if (success) {
			switch (data.taskType) {
				case TASK_TYPE_ENUM.HIVE_SQL:
				case TASK_TYPE_ENUM.SPARK_SQL: {
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
						breadcrumb: breadcrumbService.getBreadcrumb(fileId),
					};
					molecule.editor.open(tabData);
					break;
				}

				case TASK_TYPE_ENUM.SYNC: {
					// open in molecule
					const tabData: molecule.model.IEditorTab = {
						id: fileId.toString(),
						name: data.name,
						data: {
							...data,
							value: prettierJSONstring(data.sqlText),
							language: mappingTaskTypeToLanguage(data.taskType),
							taskDesc: data.taskDesc,
						},
						icon: fileIcon(data.taskType, CATELOGUE_TYPE.TASK),
						breadcrumb: breadcrumbService.getBreadcrumb(fileId),
					};

					// 向导模式渲染数据同步任务，脚本模式渲染编辑器
					if (data.createModel === CREATE_MODEL_TYPE.GUIDE) {
						tabData.renderPane = () => {
							return <DataSync key={fileId} />;
						};
						// 向导模式不需要设置编辑器语言
						Reflect.deleteProperty(tabData.data!, 'language');
					}

					molecule.editor.open(tabData);
					break;
				}

				case TASK_TYPE_ENUM.FLINK: {
					const handleSubmit = ({ resourceIdList, ...restValues }: IFormFieldProps) => {
						return new Promise<boolean>((resolve) => {
							const params = {
								id: res.data.id,
								computeType: res.data.computeType,
								updateSource: false,
								preSave: false,
								resourceIdList: resourceIdList ? [resourceIdList] : [],
								...restValues,
							};
							api.addOfflineTask(params)
								.then((result) => {
									if (result.code === 1) {
										message.success('编辑成功');
										afterSubmit(
											params,
											result.data.parentId,
											fileId.toString(),
										);
									}
								})
								.finally(() => {
									resolve(false);
								});
						});
					};

					// open in molecule
					const tabData: molecule.model.IEditorTab = {
						id: fileId.toString(),
						name: data.name,
						data: {
							id: res.data.id,
							name: res.data.name,
							taskType: res.data.taskType,
							nodePid: `${res.data.nodePid}-folder`,
							taskDesc: res.data.taskDesc,
							mainClass: res.data.mainClass,
							exeArgs: res.data.exeArgs,
							resourceIdList: res.data.resourceList?.[0].id,
						},
						icon: fileIcon(data.taskType, CATELOGUE_TYPE.TASK),
						breadcrumb: breadcrumbService.getBreadcrumb(fileId),
						renderPane: () => <Open onSubmit={handleSubmit} />,
					};
					molecule.editor.open(tabData);
					break;
				}

				case TASK_TYPE_ENUM.DATA_ACQUISITION: {
					const tabData: molecule.model.IEditorTab = {
						id: fileId.toString(),
						name: data.name,
						data: {
							...data,
							value: prettierJSONstring(data.sqlText),
						},
						icon: fileIcon(data.taskType, CATELOGUE_TYPE.TASK),
						breadcrumb: breadcrumbService.getBreadcrumb(fileId),
					};
					if (data.createModel === CREATE_MODEL_TYPE.GUIDE) {
						tabData.renderPane = () => <StreamCollection key={fileId} />;
					} else {
						tabData.data!.language = mappingTaskTypeToLanguage(data.taskType);
					}
					molecule.editor.open(tabData);
					break;
				}

				case TASK_TYPE_ENUM.SQL: {
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
						breadcrumb: breadcrumbService.getBreadcrumb(fileId),
					};
					molecule.editor.open(tabData);
					break;
				}
				default:
					break;
			}
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
			case ID_COLLECTIONS.TASK_CREATE_ID: {
				openCreateTab(treeNode!.data.id);
				break;
			}
			case ID_COLLECTIONS.FOLDERTREE_CONTEXT_EDIT: {
				const isFile = treeNode!.fileType === 'File';

				const tabId = isFile
					? `${ID_COLLECTIONS.EDIT_TASK_PREFIX}_${new Date().getTime()}`
					: `${ID_COLLECTIONS.EDIT_FOLDER_PREFIX}_${new Date().getTime()}`;

				const onSubmit = (values: any) => {
					return new Promise<boolean>((resolve) => {
						const params = {
							id: treeNode!.data.id,
							computeType: getComputeType(values.taskType),
							version: 0,
							...values,
							updateSource: false,
							preSave: false,
						};
						api.addOfflineTask(params)
							.then((res) => {
								if (res.code === 1) {
									message.success('编辑成功');
									afterSubmit(params, treeNode!.data.parentId, tabId);
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
									afterSubmit(params, treeNode!.data.parentId, tabId);
								}
							})
							.finally(() => {
								resolve(false);
							});
					});
				};

				const breadcrumb = [
					{
						id: catalogueService.getRootFolder(CATELOGUE_TYPE.TASK)!.id,
						name: catalogueService.getRootFolder(CATELOGUE_TYPE.TASK)!.name,
					},
					{
						id: tabId,
						name: isFile
							? localize('update task', '编辑任务')
							: localize('update folder', '编辑文件夹'),
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
		catalogueService.loadTreeNode(treeNode.data, CATELOGUE_TYPE.TASK).then((res) => {
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
		// 新建文件夹
		editTreeNodeName();
	}
}
