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
import molecule from '@dtinsight/molecule';
import type { IExtension } from '@dtinsight/molecule/esm/model';
import { runTask, syntaxValidate } from '@/utils/extensions';
import { DRAWER_MENU_ENUM, TASK_LANGUAGE, ID_COLLECTIONS } from '@/constant';
import { history } from 'umi';
import { cloneDeep, debounce } from 'lodash';
import ReactDOM from 'react-dom';
import Publish, { CONTAINER_ID } from '@/components/task/publish';
import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import { createSQLProposals, prettierJSONstring } from '@/utils';
import api from '@/api';
import apiStream from '@/api/stream';
import { TASK_TYPE_ENUM } from '@/constant';
import type { CatalogueDataProps, IOfflineTaskProps } from '@/interface';
import { executeService } from '@/services';
import type { IParamsProps } from '@/services/taskParamsService';
import taskParamsService from '@/services/taskParamsService';
import ImportTemplate from '@/components/task/importTemplate';
import { languages } from '@dtinsight/molecule/esm/monaco';
import saveTask from '@/utils/saveTask';
import { editorActionBarService } from '@/services';
import notification from '@/components/notification';
import { mappingTaskTypeToLanguage } from '@/utils/enums';

function emitEvent() {
	molecule.editor.onActionsClick(async (menuId, current) => {
		const actionDisabled = current?.actions?.find(({ id }) => id === menuId)?.disabled
		if (actionDisabled) return
		switch (menuId) {
			case ID_COLLECTIONS.TASK_RUN_ID: {
				runTask(current);
				break;
			}
			case ID_COLLECTIONS.TASK_STOP_ID: {
				const currentTabData:
					| (CatalogueDataProps & IOfflineTaskProps & { value?: string })
					| undefined = current.tab?.data;
				if (!currentTabData) return;

				if (currentTabData.taskType === TASK_TYPE_ENUM.SYNC) {
					executeService.stopDataSync(currentTabData.id, false);
				} else {
					executeService.stopSql(currentTabData.id, currentTabData, false);
				}
				break;
			}
			case ID_COLLECTIONS.TASK_SAVE_ID: {
				saveTask()
					.then((res) => res?.data?.id)
					.then((id) => {
						if (id !== undefined) {
							api.getOfflineTaskByID({ id }).then((res) => {
								const { code } = res;
								if (code === 1) {
									molecule.editor.updateTab({
										id: current.tab!.id,
										status: undefined,
									});
								}
							});
						}
					})
					.catch((err: Error | undefined) => {
						if (err) {
							message.error(err.message);
						}
					});
				break;
			}
			case ID_COLLECTIONS.TASK_SUBMIT_ID: {
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

			case ID_COLLECTIONS.TASK_OPS_ID: {
				const currentTabData:
					| (CatalogueDataProps & IOfflineTaskProps & { value?: string })
					| undefined = current.tab?.data;
				if (currentTabData) {
					switch (currentTabData.taskType) {
						case TASK_TYPE_ENUM.SPARK_SQL:
						case TASK_TYPE_ENUM.HIVE_SQL:
						case TASK_TYPE_ENUM.SYNC:
							history.push({
								query: {
									drawer: DRAWER_MENU_ENUM.TASK,
									tname: currentTabData.name,
								},
							});
							break;
						case TASK_TYPE_ENUM.DATA_ACQUISITION:
						case TASK_TYPE_ENUM.SQL:
							history.push({
								query: {
									drawer: DRAWER_MENU_ENUM.STREAM_TASK,
									tname: currentTabData.name,
								},
							});
							break;

						default:
							return null;
					}
				}
				break;
			}
			case ID_COLLECTIONS.TASK_CONVERT_SCRIPT: {
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
							switch (currentTabData.taskType) {
								case TASK_TYPE_ENUM.SYNC:
									api.convertDataSyncToScriptMode({ id: currentTabData.id }).then(
										(res) => {
											if (res.code === 1) {
												message.success('转换成功！');
												api.getOfflineTaskByID({
													id: currentTabData.id,
												}).then((result) => {
													if (result.code === 1) {
														molecule.editor.updateTab({
															id: result.data.id.toString(),
															data: {
																...currentTabData,
																...result.data,
																language: 'json',
																value: prettierJSONstring(
																	result.data.sqlText,
																),
															},
															renderPane: undefined,
														});
													}
												});
											}
										},
									);
									break;
								case TASK_TYPE_ENUM.DATA_ACQUISITION:
								case TASK_TYPE_ENUM.SQL: {
									apiStream
										.convertToScriptMode({
											id: currentTabData.id,
											createModel: currentTabData.createModel,
											componentVersion: currentTabData.componentVersion,
										})
										.then((res) => {
											if (res.code === 1) {
												message.success('转换成功！');
												// update current values
												api.getOfflineTaskByID({
													id: currentTabData.id,
												}).then((result) => {
													if (result.code === 1) {
														if (
															currentTabData.taskType ===
															TASK_TYPE_ENUM.DATA_ACQUISITION
														) {
															const nextTabData = current.tab!;
															nextTabData!.data = result.data;
															Reflect.deleteProperty(
																nextTabData,
																'renderPane',
															);
															nextTabData.data.language =
																mappingTaskTypeToLanguage(
																	result.data.taskType,
																);
															nextTabData.data.value =
																result?.data?.sqlText;
															molecule.editor.updateTab(nextTabData);
															return;
														}

														const nextTabData = result.data;
														molecule.editor.updateTab({
															id: nextTabData.id.toString(),
															data: {
																...currentTabData,
																...nextTabData,
															},
														});
														// update the editor's value
														molecule.editor.editorInstance
															.getModel()
															?.setValue(nextTabData.sqlText);
														editorActionBarService.performSyncTaskActions();
													}
												});
											}
										});
									break;
								}
								default:
									break;
							}
						},
					});
				}
				break;
			}
			case ID_COLLECTIONS.TASK_IMPORT_ID: {
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
			// FlinkSQL 语法检查
			case ID_COLLECTIONS.TASK_SYNTAX_ID: {
				syntaxValidate(current);
				break;
			}
			// FlinkSQL 格式化
			case ID_COLLECTIONS.TASK_FORMAT_ID: {
				apiStream.sqlFormat({ sql: current.tab?.data.value }).then((res) => {
					if (res.code === 1) {
						molecule.editor.editorInstance.getModel()?.setValue(res.data);
					}
				});
				break;
			}
			default:
				break;
		}
	});
}

const updateTaskVariables = debounce((tab: molecule.model.IEditorTab<any>) => {
	// 不同的任务需要解析不同的字段来获取自定义参数
	const currentData: IOfflineTaskProps & { value?: string } = tab.data;
	let sqlText: string = '';
	switch (currentData.taskType) {
		case TASK_TYPE_ENUM.SPARK_SQL:
		case TASK_TYPE_ENUM.HIVE_SQL:
		case TASK_TYPE_ENUM.SQL:
			sqlText = currentData.value || '';
			break;
		case TASK_TYPE_ENUM.SYNC:
			// 需要从以下属性中解析出参数
			sqlText = `
				${currentData.sourceMap.where}
				${currentData.sourceMap.partition}
				${currentData.sourceMap.column
					?.map((col) => `${col.key || col.index}\n${col.value || ''}`)
					.join('\n')}
				${currentData.sourceMap.path}
				${currentData.targetMap?.preSql || ''}
				${currentData.targetMap?.postSql || ''}
				${currentData.targetMap?.partition || ''}
				${currentData.targetMap?.fileName || ''}
				${currentData.targetMap?.path || ''}
				`;
			break;
		default:
			break;
	}
	const nextVariables = taskParamsService.matchTaskParams(sqlText);
	const preVariables: Partial<IParamsProps>[] = currentData?.taskVariables || [];

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
			...currentData,
			taskVariables: data,
		},
	});
}, 300);

// 注册自动补全
function registerCompletion() {
	const COMPLETION_SQL = [
		TASK_LANGUAGE.SPARKSQL,
		TASK_LANGUAGE.HIVESQL,
		TASK_LANGUAGE.SQL,
		TASK_LANGUAGE.FLINKSQL,
	] as const;
	COMPLETION_SQL.forEach((sql) =>
		languages.registerCompletionItemProvider(sql, {
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
		}),
	);
}

export default class EditorExtension implements IExtension {
	id: UniqueId = 'editor';
	name: string = 'editor';
	dispose(): void {
		throw new Error('Method not implemented.');
	}
	activate() {
		emitEvent();
		registerCompletion();

		molecule.editor.onOpenTab(() => {
			// Should delay to performSyncTaskActions
			// because when onOpenTab called, the current tab was not changed
			window.setTimeout(() => {
				editorActionBarService.performSyncTaskActions();
			}, 0);
		});

		molecule.editor.onSelectTab(() => {
			editorActionBarService.performSyncTaskActions();
		});

		molecule.editor.onCloseTab(() => {
			editorActionBarService.performSyncTaskActions();
		});

		molecule.editor.onUpdateTab((tab) => {
			updateTaskVariables(tab);
			// update edited status
			molecule.editor.updateTab({ id: tab.id, status: 'edited' });
		});

		executeService.onEndRun((currentTabId) => {
			if (currentTabId.toString() !== molecule.editor.getState().current?.activeTab) {
				const groupId = molecule.editor.getGroupIdByTab(currentTabId.toString());
				if (groupId === null) return;
				const tab = molecule.editor.getTabById(currentTabId.toString(), groupId);
				if (!tab) return;
				notification.success({
					key: `${currentTabId}-${new Date()}`,
					message: `${tab.name} 任务执行完成!`,
				});
			}
		});
	}
}
