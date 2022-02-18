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

import { message, Modal, Tag } from 'antd';
import { UploadOutlined, LoginOutlined } from '@ant-design/icons';
import molecule from '@dtinsight/molecule';
import type { IExtension } from '@dtinsight/molecule/esm/model';
import { resetEditorGroup } from '@/utils/extensions';
import {
	TASK_RUN_ID,
	TASK_STOP_ID,
	TASK_SUBMIT_ID,
	TASK_OPS_ID,
	OUTPUT_LOG,
	TASK_SAVE_ID,
	DRAWER_MENU_ENUM,
} from '@/constant';
import { history } from 'umi';
import { cloneDeep, debounce } from 'lodash';
import ReactDOM from 'react-dom';
import Result from '@/components/task/result';
import Publish, { CONTAINER_ID } from '@/components/task/publish';
import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import { filterSql, formatDateTime } from '@/utils';
import api from '@/api';
import { searchById } from '@dtinsight/molecule/esm/common/utils';
import { TASK_TYPE_ENUM } from '@/constant';
import type { CatalogueDataProps, IOfflineTaskProps } from '@/interface';
import taskResultService from '@/services/taskResultService';
import executeService from '@/services/executeService';
import type { IParamsProps } from '@/services/taskParamsService';
import taskParamsService from '@/services/taskParamsService';
import { generateRqtBody } from '@/components/dataSync';

const { confirm } = Modal;

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
				const currentTabData:
					| (CatalogueDataProps & IOfflineTaskProps & { value?: string })
					| undefined = current.tab?.data;
				if (currentTabData) {
					// 禁用运行按钮，启用停止按钮
					molecule.editor.updateActions([
						{
							id: TASK_RUN_ID,
							icon: 'loading~spin',
							disabled: true,
						},
						{
							id: TASK_STOP_ID,
							disabled: false,
						},
					]);

					// active 日志 窗口
					const { data } = molecule.panel.getState();
					const {
						panel: { hidden },
					} = molecule.layout.getState();
					if (hidden) {
						molecule.layout.togglePanelVisibility();
					}
					molecule.panel.setState({
						current: data?.find((item) => item.id === OUTPUT_LOG),
					});

					if (currentTabData.taskType === TASK_TYPE_ENUM.SYNC) {
						const params: any = {
							taskId: currentTabData.id,
							name: currentTabData.name,
							taskParams: currentTabData.taskParams,
						};
						executeService.execDataSync(currentTabData.id, params).finally(() => {
							// update the status of buttons
							molecule.editor.updateActions([
								{
									id: TASK_SAVE_ID,
									disabled: false,
								},
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
						});
					} else {
						const params = {
							taskVariables: currentTabData.taskVariables || [],
							// 是否为单 session 模式, 为 true 时，支持batchSession 时，则支持批量SQL，false 则相反
							singleSession: false,
							taskParams: currentTabData.taskParams,
						};

						const value = currentTabData.value || '';
						// 需要被执行的 sql 语句
						const sqls = [];
						const rawSelections = molecule.editor.editorInstance.getSelections() || [];
						// 排除鼠标 focus 在 editor 中的情况
						const selections = rawSelections.filter(
							(s) =>
								s.startLineNumber !== s.endLineNumber ||
								s.startColumn !== s.endColumn,
						);
						// 如果存在选中行，则执行选中行
						if (selections?.length) {
							selections?.forEach((s) => {
								const text = molecule.editor.editorInstance
									.getModel()
									?.getValueInRange(s);
								if (text) {
									sqls.push(...filterSql(text));
								}
							});
						} else {
							sqls.push(...filterSql(value));
						}
						executeService
							.execSql(currentTabData.id, currentTabData, params, sqls)
							.then(() => {
								const allResult = taskResultService.getState().results;
								Object.keys(allResult).forEach((key) => {
									const results = allResult[key];
									const panel = molecule.panel.getPanel(key);

									if (!panel) {
										const panels = molecule.panel.getState().data || [];
										const resultPanles = panels.filter((p) =>
											p.name?.includes('结果'),
										);
										const lastIndexOf = Number(
											resultPanles[resultPanles.length - 1]?.name?.slice(2) ||
												'',
										);

										molecule.panel.open({
											id: key,
											name: `结果 ${lastIndexOf + 1}`,
											closable: true,
											renderPane: () => (
												<Result
													data={results}
													tab={{
														tableType: 0,
													}}
													extraView={null}
												/>
											),
										});
									}
								});
							})
							.finally(() => {
								// update the status of buttons
								molecule.editor.updateActions([
									{
										id: TASK_SAVE_ID,
										disabled: false,
									},
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
							});
					}
				}
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
				const params = {
					...generateRqtBody(),
					sqlText: current.tab?.data.value,
					// taskVos pass through by dependencyTasks
					dependencyTasks: current.tab?.data.taskVOS,
				};
				const uploadTask = () => {
					const { id } = params;
					api.getOfflineTaskByID({ id }).then((res) => {
						const { success, data } = res;
						if (success) {
							molecule.folderTree.update({
								id,
								data,
							});
							molecule.editor.updateActions([
								{
									id: TASK_SAVE_ID,
									disabled: false,
								},
								{
									id: TASK_RUN_ID,
									icon: 'play',
									disabled: false,
								},
								{
									id: TASK_STOP_ID,
									disabled: true,
								},
								{
									id: TASK_SUBMIT_ID,
									disabled: false,
								},
							]);
						}
					});
				};
				const succCallback = (res: any) => {
					if (res.code === 1) {
						const fileData = res.data;
						const lockInfo = fileData.readWriteLockVO;
						const lockStatus = lockInfo?.result; // 1-正常，2-被锁定，3-需同步
						if (lockStatus === 0) {
							message.success('保存成功！');
							uploadTask();
							// 如果是锁定状态，点击确定按钮，强制更新，否则，取消保存
						} else if (lockStatus === 1) {
							// 2-被锁定
							confirm({
								title: '锁定提醒', // 锁定提示
								content: (
									<span>
										文件正在被
										{lockInfo.lastKeepLockUserName}
										编辑中，开始编辑时间为
										{formatDateTime(lockInfo.gmtModified)}。 强制保存可能导致
										{lockInfo.lastKeepLockUserName}
										对文件的修改无法正常保存！
									</span>
								),
								okText: '确定保存',
								okType: 'danger',
								cancelText: '取消',
								onOk() {
									const succCall = (successRes: any) => {
										if (successRes.code === 1) {
											message.success('保存成功！');
											uploadTask();
										}
									};
									api.forceUpdateOfflineTask(params).then(succCall);
								},
							});
							// 如果同步状态，则提示会覆盖代码，
							// 点击确认，重新拉取代码并覆盖当前代码，取消则退出
						} else if (lockStatus === 2) {
							// 2-需同步
							confirm({
								title: '保存警告',
								content: (
									<span>
										文件已经被
										{lockInfo.lastKeepLockUserName}
										编辑过，编辑时间为
										{formatDateTime(lockInfo.gmtModified)}。 点击确认按钮会
										<Tag color="orange">覆盖</Tag>
										您本地的代码，请您提前做好备份！
									</span>
								),
								okText: '确定覆盖',
								okType: 'danger',
								cancelText: '取消',
								onOk() {
									const reqParams: any = {
										id: params.id,
										lockVersion: lockInfo.version,
									};
									// 更新version, getLock信息
									api.getOfflineTaskDetail(reqParams).then((detailRes) => {
										if (detailRes.code === 1) {
											const taskInfo = detailRes.data;
											taskInfo.merged = true;
											uploadTask();
										}
									});
								},
							});
						}
						return res;
					}
				};
				api.saveOfflineJobData(params).then(succCallback);
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

export default class EditorExtension implements IExtension {
	id: UniqueId = 'editor';
	name: string = 'editor';
	dispose(): void {
		throw new Error('Method not implemented.');
	}
	activate() {
		initActions();
		emitEvent();

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
		});

		molecule.editor.onUpdateTab((tab) => {
			updateTaskVariables(tab);
		});
	}
}
