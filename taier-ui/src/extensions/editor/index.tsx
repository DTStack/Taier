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

import { UploadOutlined, LoginOutlined } from '@ant-design/icons';
import { Modal, message } from 'antd';
import molecule from '@dtinsight/molecule';
import type { IExtension } from '@dtinsight/molecule/esm/model';
import { performSyncTaskActions, resetEditorGroup, runTask } from '@/utils/extensions';
import {
	TASK_RUN_ID,
	TASK_STOP_ID,
	TASK_SUBMIT_ID,
	TASK_OPS_ID,
	TASK_SAVE_ID,
	DRAWER_MENU_ENUM,
	TASK_SWAP,
	TASK_IMPORT_TEMPALTE,
	TASK_LANGUAGE,
} from '@/constant';
import { history } from 'umi';
import { cloneDeep, debounce } from 'lodash';
import ReactDOM from 'react-dom';
import Publish, { CONTAINER_ID } from '@/components/task/publish';
import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import { createSQLProposals } from '@/utils';
import api from '@/api';
import { searchById } from '@dtinsight/molecule/esm/common/utils';
import { TASK_TYPE_ENUM } from '@/constant';
import type { CatalogueDataProps, IOfflineTaskProps } from '@/interface';
import executeService from '@/services/executeService';
import type { IParamsProps } from '@/services/taskParamsService';
import taskParamsService from '@/services/taskParamsService';
import { saveTask } from '@/components/dataSync/help';
import ImportTemplate from '@/components/task/importTemplate';
import { languages } from '@dtinsight/molecule/esm/monaco';

function initActions() {
	const { builtInEditorInitialActions } = molecule.builtin.getModules();

	molecule.editor.setDefaultActions([
		{
			id: TASK_SAVE_ID,
			name: 'Save Task',
			icon: 'save',
			place: 'outer',
			disabled: true,
			title: '保存',
		},
		{
			id: TASK_RUN_ID,
			name: 'Run Task',
			icon: 'play',
			place: 'outer',
			disabled: true,
			title: '运行',
		},
		{
			id: TASK_STOP_ID,
			name: 'Stop Task',
			icon: 'debug-pause',
			place: 'outer',
			disabled: true,
			title: '停止运行',
		},
		{
			id: TASK_SUBMIT_ID,
			name: '提交至调度',
			icon: <UploadOutlined />,
			place: 'outer',
			disabled: true,
			title: '提交至调度',
		},
		{
			id: TASK_OPS_ID,
			name: '运维',
			title: '运维',
			icon: <LoginOutlined />,
			place: 'outer',
		},
		...builtInEditorInitialActions,
	]);
}

function emitEvent() {
	molecule.editor.onActionsClick(async (menuId, current) => {
		switch (menuId) {
			case TASK_RUN_ID: {
				runTask(current);
				break;
			}
			case TASK_STOP_ID: {
				const currentTabData:
					| (CatalogueDataProps & IOfflineTaskProps & { value?: string })
					| undefined = current.tab?.data;
				if (!currentTabData) return;

				if (currentTabData.taskType === TASK_TYPE_ENUM.SYNC) {
					executeService.stopDataSync(currentTabData.id, false);
				} else {
					executeService.stopSql(currentTabData.id, currentTabData, false);
				}
				molecule.editor.updateActions([
					{
						id: TASK_RUN_ID,
						icon: 'play',
						disabled: false,
					},
					{
						id: TASK_STOP_ID,
						disabled: true,
					},
				]);
				break;
			}
			case TASK_SAVE_ID: {
				saveTask()
					?.then((res) => res?.data?.id)
					.then((id) => {
						if (id !== undefined) {
							api.getOfflineTaskByID({ id }).then((res) => {
								const { success, data } = res;
								if (success) {
									molecule.folderTree.update({
										id,
										data,
									});
									molecule.editor.updateTab({
										id: current.tab!.id,
										status: undefined,
									});
								}
							});
						}
					});
				break;
			}
			case TASK_SUBMIT_ID: {
				const currentTab = current.tab;
				const root = document.getElementById('molecule')!;

				const target = document.getElementById(CONTAINER_ID);
				if (target) {
					target.parentElement?.removeChild(target);
				}
				const node = document.createElement('div');
				node.id = CONTAINER_ID;
				root.appendChild(node);
				if (currentTab) {
					ReactDOM.render(<Publish data={cloneDeep(currentTab.data)} />, node);
				}
				break;
			}

			case TASK_OPS_ID: {
				const currentTabData:
					| (CatalogueDataProps & IOfflineTaskProps & { value?: string })
					| undefined = current.tab?.data;
				if (currentTabData) {
					history.push({
						query: {
							drawer: DRAWER_MENU_ENUM.TASK,
							tname: currentTabData.name,
						},
					});
				}
				break;
			}
			case TASK_SWAP: {
				const currentTabData:
					| (CatalogueDataProps & IOfflineTaskProps & { value?: string })
					| undefined = current.tab?.data;
				if (currentTabData) {
					Modal.confirm({
						title: '转换为脚本',
						content: (
							<div>
								<p style={{ color: '#f04134' }}>此操作不可逆，是否继续？</p>
								<p>
									当前为向导模式，配置简单快捷，脚本模式可灵活配置更多参数，定制化程度高
								</p>
							</div>
						),
						okText: '确认',
						cancelText: '取消',
						onOk() {
							api.convertDataSyncToScriptMode({ id: currentTabData.id }).then(
								(res) => {
									if (res.code === 1) {
										message.success('转换成功！');
										const nextTabData = current.tab!;
										nextTabData.data.language = 'json';
										Reflect.deleteProperty(nextTabData, 'renderPane');
										molecule.editor.updateTab(nextTabData);
									}
								},
							);
						},
					});
				}
				break;
			}
			case TASK_IMPORT_TEMPALTE: {
				const currentTab = current.tab;
				const root = document.getElementById('molecule')!;

				const target = document.getElementById(CONTAINER_ID);
				if (target) {
					target.parentElement?.removeChild(target);
				}
				const node = document.createElement('div');
				node.id = CONTAINER_ID;
				root.appendChild(node);
				if (currentTab) {
					const handleSuccess = (data: string) => {
						// update the editor's content
						const prettierJSON = JSON.stringify(JSON.parse(data), null, 4);
						molecule.editor.editorInstance.getModel()?.setValue(prettierJSON);
					};

					ReactDOM.render(
						<ImportTemplate taskId={currentTab.data.id} onSuccess={handleSuccess} />,
						node,
					);
				}
				break;
			}
			default:
				break;
		}
	});
}

const updateTaskVariables = debounce((tab) => {
	const nextVariables = taskParamsService.matchTaskParams(tab.data?.value || '');
	const preVariables: Partial<IParamsProps>[] = tab.data.taskVariables || [];

	// Prevent reset the value of the exist params
	const data = nextVariables.map((i) => {
		const existedVar = preVariables.find((v) => v.paramName === i.paramName);
		if (existedVar) {
			return existedVar;
		}
		return i;
	});

	molecule.editor.updateTab({
		id: tab.id,
		data: {
			...tab.data,
			taskVariables: data,
		},
	});
}, 300);

// 注册自动补全
function registerCompletion() {
	const sqlProvider: languages.CompletionItemProvider = {
		provideCompletionItems(model, position) {
			const word = model.getWordUntilPosition(position);
			const range = {
				startLineNumber: position.lineNumber,
				endLineNumber: position.lineNumber,
				startColumn: word.startColumn,
				endColumn: word.endColumn,
			};
			return {
				suggestions: createSQLProposals(range),
			};
		},
	};
	languages.registerCompletionItemProvider(TASK_LANGUAGE.SPARKSQL, sqlProvider);
	languages.registerCompletionItemProvider(TASK_LANGUAGE.HIVESQL, sqlProvider);
}

export default class EditorExtension implements IExtension {
	id: UniqueId = 'editor';
	name: string = 'editor';
	dispose(): void {
		throw new Error('Method not implemented.');
	}
	activate() {
		initActions();
		emitEvent();
		registerCompletion();

		molecule.editor.onSelectTab((tabId, groupId) => {
			const { current } = molecule.editor.getState();
			if (!current) return;
			const group = molecule.editor.getGroupById(groupId || current.id!);
			if (group) {
				const targetTab = group.data?.find(searchById(tabId));
				if (targetTab?.data?.taskType === TASK_TYPE_ENUM.SQL) {
					molecule.editor.updateActions([
						{ id: TASK_RUN_ID, disabled: false },
						{ id: TASK_SAVE_ID, disabled: false },
						{ id: TASK_SUBMIT_ID, disabled: false },
						{ id: TASK_OPS_ID, disabled: false },
					]);
				} else {
					resetEditorGroup();
				}
			}
			performSyncTaskActions();
		});

		molecule.editor.onCloseTab(() => {
			const { current } = molecule.editor.getState();
			if (current?.tab?.data.taskType === TASK_TYPE_ENUM.SQL) {
				molecule.editor.updateActions([
					{ id: TASK_RUN_ID, disabled: false },
					{ id: TASK_SAVE_ID, disabled: false },
					{ id: TASK_SUBMIT_ID, disabled: false },
					{ id: TASK_OPS_ID, disabled: false },
				]);
			} else {
				resetEditorGroup();
			}

			performSyncTaskActions();
		});

		molecule.editor.onUpdateTab((tab) => {
			updateTaskVariables(tab);
			// update edited status
			molecule.editor.updateTab({ id: tab.id, status: 'edited' });
		});
	}
}
