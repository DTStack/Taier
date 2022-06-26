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

import molecule from '@dtinsight/molecule/esm';
import type { IFolderTreeNodeProps } from '@dtinsight/molecule/esm/model';
import {
	FlinkSQLIcon,
	SyntaxIcon,
	HiveSQLIcon,
	SparkSQLIcon,
	ResourceIcon,
	DataCollectionIcon,
} from '@/components/icon';
import type { RESOURCE_TYPE } from '@/constant';
import { ID_COLLECTIONS } from '@/constant';
import { CATELOGUE_TYPE, TASK_TYPE_ENUM } from '@/constant';
import type { CatalogueDataProps, IOfflineTaskProps } from '@/interface';
import { executeService } from '@/services';
import taskResultService, { createLog } from '@/services/taskResultService';
import Result from '@/components/task/result';
import { filterSql } from '.';
import stream from '@/api';
import { TreeViewUtil } from '@dtinsight/molecule/esm/common/treeUtil';
import { transformTabDataToParams } from './saveTask';

/**
 * 根据不同任务渲染不同的图标
 */
export function fileIcon(
	type: TASK_TYPE_ENUM | RESOURCE_TYPE | null,
	source: CATELOGUE_TYPE,
): string | JSX.Element {
	switch (source) {
		case CATELOGUE_TYPE.TASK: {
			switch (type) {
				case TASK_TYPE_ENUM.SPARK_SQL:
					return <SparkSQLIcon style={{ color: '#519aba' }} />;
				case TASK_TYPE_ENUM.SYNC:
					return 'sync';
				case TASK_TYPE_ENUM.HIVE_SQL:
					return <HiveSQLIcon style={{ color: '#4291f0' }} />;
				case TASK_TYPE_ENUM.SQL:
					return <FlinkSQLIcon style={{ color: '#5655d8' }} />;
				case TASK_TYPE_ENUM.DATA_ACQUISITION:
					return <DataCollectionIcon style={{ color: '#3F87FF' }} />;
				default:
					return 'file';
			}
		}
		case CATELOGUE_TYPE.RESOURCE: {
			return <ResourceIcon style={{ color: '#0065f6' }} />;
		}
		case CATELOGUE_TYPE.FUNCTION:
		default:
			return 'code';
	}
}

/**
 * 运行任务
 */
export function runTask(current: molecule.model.IEditorGroup) {
	const currentTabData:
		| (CatalogueDataProps & IOfflineTaskProps & { value?: string })
		| undefined = current.tab?.data;
	if (currentTabData) {
		// active 日志 窗口
		const { data } = molecule.panel.getState();
		const {
			panel: { hidden },
		} = molecule.layout.getState();
		if (hidden) {
			molecule.layout.togglePanelVisibility();
		}
		molecule.panel.setState({
			current: data?.find((item) => item.id === ID_COLLECTIONS.OUTPUT_LOG_ID),
		});

		if (currentTabData.taskType === TASK_TYPE_ENUM.SYNC) {
			const params: any = {
				taskId: currentTabData.id,
				name: currentTabData.name,
				taskParams: currentTabData.taskParams,
			};
			executeService.execDataSync(currentTabData.id, params);
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
				(s) => s.startLineNumber !== s.endLineNumber || s.startColumn !== s.endColumn,
			);
			// 如果存在选中行，则执行选中行
			if (selections?.length) {
				selections?.forEach((s) => {
					const text = molecule.editor.editorInstance.getModel()?.getValueInRange(s);
					if (text) {
						sqls.push(...filterSql(text));
					}
				});
			} else {
				sqls.push(...filterSql(value));
			}
			executeService.execSql(currentTabData.id, currentTabData, params, sqls).then(() => {
				const allResult = taskResultService.getState().results;
				Object.keys(allResult).forEach((key) => {
					const results = allResult[key];
					const panel = molecule.panel.getPanel(key);

					if (!panel) {
						const panels = molecule.panel.getState().data || [];
						const resultPanles = panels.filter((p) => p.name?.includes('结果'));
						const lastIndexOf = Number(
							resultPanles[resultPanles.length - 1]?.name?.slice(2) || '',
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
			});
		}
	}
}

/**
 * 语法检查
 */
export function syntaxValidate(current: molecule.model.IEditorGroup) {
	const currentTabData: IOfflineTaskProps | undefined = current.tab?.data;
	if (!currentTabData) return;
	// 禁用语法检查
	molecule.editor.updateActions([
		{
			id: ID_COLLECTIONS.TASK_SYNTAX_ID,
			icon: 'loading~spin',
			disabled: true,
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
		current: data?.find((item) => item.id === ID_COLLECTIONS.OUTPUT_LOG_ID),
	});

	const logId = currentTabData.id.toString();
	taskResultService.clearLogs(logId);
	taskResultService.appendLogs(logId, createLog('语法检查开始', 'info'));

	const params = transformTabDataToParams(currentTabData);

	let isSuccess = false;
	stream
		.checkSyntax(params)
		.then((res) => {
			if (res.message) {
				taskResultService.appendLogs(logId, createLog(res.message, 'error'));
			}
			if (res && res.code === 1) {
				if (res.data.code === 1) {
					taskResultService.appendLogs(logId, createLog('语法检查通过', 'info'));
					isSuccess = true;
				} else {
					taskResultService.appendLogs(logId, createLog(res.data.errorMsg, 'error'));
				}
			}
		})
		.catch((e) => {
			// eslint-disable-next-line no-console
			console.trace(e);
		})
		.finally(() => {
			if (!isSuccess) {
				taskResultService.appendLogs(logId, createLog('语法检查失败！', 'error'));
			}
			// 恢复语法检查按钮
			molecule.editor.updateActions([
				{
					id: ID_COLLECTIONS.TASK_SYNTAX_ID,
					icon: <SyntaxIcon />,
					disabled: false,
				},
			]);
		});
}

export function getParentNode(treeList: IFolderTreeNodeProps, currentNode: IFolderTreeNodeProps) {
	const treeView = new TreeViewUtil(treeList);
	const parentNode = treeView.getHashMap(currentNode.id)?.parent;
	if (parentNode) {
		return treeView.getNode(parentNode);
	}
	return null;
}
