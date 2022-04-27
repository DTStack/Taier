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

import { useEffect, useMemo, useReducer } from 'react';
import { Button, Collapse, Popconfirm } from 'antd';
import classNames from 'classnames';
import { cloneDeep } from 'lodash';
import molecule from '@dtinsight/molecule';
import { TAB_WITHOUT_DATA } from '@/pages/rightBar';
import type { IEditor } from '@dtinsight/molecule/esm/model';
import { DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import { DATA_SOURCE_ENUM } from '@/constant';
import stream from '@/api/stream';
import {
	haveCollection,
	havePartition,
	haveSchema,
	haveTableColumn,
	haveTableList,
	haveTopic,
	isSqlServer,
} from '@/utils/enums';
import { streamTaskActions } from '../../taskFunc';
import { initCustomParam } from '../customParamsUtil';
import ResultForm from './form';
import type { IDataSourceUsedInSyncProps, IFlinkSinkProps } from '@/interface';

const { Panel } = Collapse;

interface IResultState {
	panelActiveKey: string[]; // 输出源是打开或关闭状态
	popoverVisible: any[]; // 删除显示按钮状态
	panelColumn: Partial<IFlinkSinkProps>[]; // 存储数据
	/**
	 * 数据源选择数据，以 type 作为键值
	 */
	originOptionType: Record<string, IDataSourceUsedInSyncProps[]>;
	/**
	 * 表选择数据，第一层对象以 sourceId 和 schema 为键值，第二层以 searchKey 为键值
	 * @example
	 * 搜索 s 的结果:
	 * ```js
	 * {
	 * 	[sourceId-schema]:{
	 * 	  's': any[]
	 * 	}
	 * }
	 * ```
	 */
	tableOptionType: Record<string, Record<string, any[]>>;
	topicOptionType: Record<string, any[]>; // topic 列表，以 sourceId 作为键值
	/**
	 * 表字段选择的类型，以 table-schem 作为键值
	 */
	tableColumnOptionType: Record<string, { key: string; type: string }[]>;
	sync: boolean;
}

export default function FlinkResultPanel({ current }: Pick<IEditor, 'current'>) {
	const currentPage = current?.tab?.data || {};
	const [state, dispathState] = useReducer(
		(preState: IResultState, data: any) => {
			const newState = cloneDeep(preState);
			Object.assign(newState, data);
			return newState;
		},
		{
			panelActiveKey: [],
			popoverVisible: [],
			panelColumn: [],
			originOptionType: {},
			tableOptionType: {},
			topicOptionType: {},
			tableColumnOptionType: {},
			sync: false,
		},
	);
	const {
		panelActiveKey,
		panelColumn,
		originOptionType,
		popoverVisible,
		tableOptionType,
		tableColumnOptionType,
		topicOptionType,
		sync,
	} = state;

	const setOutputData = (data: any, notSynced: boolean = false) => {
		const dispatchSource: any = { ...state, ...data };
		streamTaskActions.setCurrentPageValue('sink', dispatchSource.panelColumn, notSynced);
	};

	const handleActiveKey = (key: any) => {
		setOutputData({ panelActiveKey: key });
		dispathState({
			panelActiveKey: key,
		});
	};

	/**
	 * 获取数据源列表
	 */
	const getTypeOriginData = (type?: DATA_SOURCE_ENUM) => {
		if (type !== undefined) {
			stream.getTypeOriginData({ type }).then((v) => {
				if (v.code === 1) {
					originOptionType[type] = v.data;
					setOutputData({ originOptionType });
					dispathState({
						originOptionType,
					});
				}
			});
		}
	};

	/**
	 * 获取Schema列表
	 * @deprecated 暂时不需要去请求 schema 数据
	 */
	// eslint-disable-next-line @typescript-eslint/no-unused-vars
	const getSchemaData = (..._args: any[]) => {};

	/**
	 * 获取表列表
	 */
	const getTableType = async (
		params: { sourceId?: number; type: DATA_SOURCE_ENUM; schema?: string },
		searchKey: string = '',
	) => {
		// postgresql schema必填处理
		const disableReq =
			(params.type === DATA_SOURCE_ENUM.POSTGRESQL ||
				params.type === DATA_SOURCE_ENUM.KINGBASE8 ||
				isSqlServer(params.type)) &&
			!params.schema;

		if (params.sourceId && !disableReq) {
			const res = await stream.listTablesBySchema({
				sourceId: params.sourceId,
				schema: params.schema || '',
				isSys: false,
				searchKey,
			});

			if (!tableOptionType[`${params.sourceId}-${params.schema || ''}`]) {
				tableOptionType[`${params.sourceId}-${params.schema || ''}`] = {};
			}
			tableOptionType[`${params.sourceId}-${params.schema || ''}`][searchKey] =
				res.code === 1 ? res.data : [];
			setOutputData({ tableOptionType });
			dispathState({
				tableOptionType,
			});
		}
	};

	/**
	 * 获取表字段列表
	 */
	const getTableColumns = (sourceId?: number, tableName?: string, schema = '') => {
		if (!sourceId || !tableName) {
			return;
		}
		stream
			.getStreamTableColumn({
				sourceId,
				tableName,
				schema,
				flinkVersion: currentPage?.componentVersion,
			})
			.then((v) => {
				tableColumnOptionType[`${tableName}-${schema}`] = v.code === 1 ? v.data : [];
				setOutputData({ tableColumnOptionType });
				dispathState({
					tableColumnOptionType,
				});
			});
	};

	/**
	 * @deprecated 暂时不需要请求分区
	 */
	// eslint-disable-next-line @typescript-eslint/no-unused-vars
	const loadPartitions = async (...args: any[]) => {};

	/**
	 * 获取 topic 列表
	 */
	const getTopicType = (sourceId?: number) => {
		if (sourceId) {
			stream.getTopicType({ sourceId }).then((v) => {
				topicOptionType[sourceId] = v.code === 1 ? v.data : [];
				dispathState({
					topicOptionType,
				});
			});
		}
	};

	/**
	 * 删除导致key改变,处理被改变key的值
	 * @param index 索引
	 * @returns
	 */
	const changeActiveKey = (index: any) => {
		const deleteActiveKey = `${index + 1}`;
		const deleteActiveKeyIndex = panelActiveKey.indexOf(deleteActiveKey);
		if (deleteActiveKeyIndex > -1) {
			panelActiveKey.splice(deleteActiveKeyIndex, 1);
		}
		return panelActiveKey.map((v: any) => {
			return Number(v) > Number(index) ? `${Number(v) - 1}` : v;
		});
	};

	const changeInputTabs = (type: 'add' | 'delete', index?: number) => {
		const inputData: Partial<IFlinkSinkProps> = {
			type: DATA_SOURCE_ENUM.MYSQL,
			columns: [],
			parallelism: 1,
			bulkFlushMaxActions: 100,
			batchWaitInterval: 1000,
			batchSize: 100,
			enableKeyPartitions: false,
			updateMode: 'append',
			allReplace: 'false',
		};

		let nextPanelAcitveKey = panelActiveKey.concat();
		if (type === 'add') {
			const length = panelColumn.push(inputData);
			getTypeOriginData(inputData.type);
			nextPanelAcitveKey.push(length.toString());
		} else {
			panelColumn.splice(index!, 1);
			nextPanelAcitveKey = changeActiveKey(index!);
			popoverVisible[index!] = false;
		}
		setOutputData(
			{
				panelActiveKey,
				popoverVisible,
				panelColumn,
			},
			true,
		);
		dispathState({
			panelActiveKey,
			popoverVisible,
			panelColumn,
		});
	};

	/**
	 * 表单数据修改
	 */
	const onFormValuesChanged = (
		prev: Partial<IFlinkSinkProps>,
		next: Partial<IFlinkSinkProps>,
	) => {
		const idx = panelColumn.indexOf(prev);
		if (idx !== -1) {
			panelColumn[idx] = { ...prev, ...next };
			setOutputData({ panelColumn }, true);
		}

		const changedFields = Object.keys(next).filter(
			(key) => prev[key as keyof IFlinkSinkProps] !== next[key as keyof IFlinkSinkProps],
		);

		// 表单数据改变的副作用
		const sourceType = next.type!;
		if (changedFields.includes('type')) {
			getTypeOriginData(next.type);
		}

		if (changedFields.includes('sourceId')) {
			const value = next.sourceId;
			if (haveCollection(sourceType)) {
				getTableType({ sourceId: value, type: sourceType, schema: next.schema });
			}
			if (haveTableList(sourceType)) {
				getTableType({ sourceId: value, type: sourceType, schema: next.schema });
				if (haveSchema(sourceType)) {
					getSchemaData();
				}
			}
			if (haveTopic(sourceType)) {
				getTopicType(value);
			}
		}

		if (changedFields.includes('schema')) {
			const value = next.schema;
			if (haveTableList(sourceType)) {
				getTableType({ sourceId: next.sourceId, type: sourceType, schema: value });
			}
		}

		if (changedFields.includes('table')) {
			if (haveTableColumn(sourceType)) {
				getTableColumns(next.sourceId, next.table, next.schema);
			}
			if (havePartition(sourceType)) {
				loadPartitions();
			}
		}

		if (changedFields.includes('collection')) {
			if (haveTableColumn(sourceType)) {
				getTableColumns(next.sourceId, next.collection, next.schema);
			}
			if (havePartition(sourceType)) {
				loadPartitions();
			}
		}
	};

	const currentInitData = (sink: IFlinkSinkProps[]) => {
		sink.forEach((v, index) => {
			initCustomParam(v);
			panelColumn.push(v);
			getTypeOriginData(v.type);
			if (haveCollection(v.type)) {
				getTableType({ sourceId: v.sourceId, type: v.type, schema: v.schema });
			}
			if (haveTableList(v.type)) {
				getTableType({ sourceId: v.sourceId, type: v.type, schema: v.schema });

				if (haveSchema(v.type)) {
					getSchemaData(index, v.sourceId);
				}

				if (haveTableColumn(v.type)) {
					getTableColumns(v.sourceId, v.table, v?.schema);
				}
			}

			if (haveTopic(v.type)) {
				getTopicType(v.sourceId);
			}

			if (havePartition(v.type)) {
				loadPartitions(index, v.sourceId, v.table);
			}
		});
		setOutputData({ panelColumn });
		dispathState({
			panelColumn,
		});
	};

	const panelHeader = (index: any) => {
		const tableName = panelColumn[index]?.tableName; // 映射表名称
		return (
			<div className="input-panel-title">
				{` 结果表 ${index + 1} ${tableName ? `(${tableName})` : ''}`}
			</div>
		);
	};

	useEffect(() => {
		const { sink } = currentPage;
		if (sink && sink.length > 0) {
			currentInitData(sink);
		}
		dispathState({ sync: true });
	}, [current?.id]);

	/**
	 * 当前的 tab 是否不合法，如不合法则展示 Empty
	 */
	const isInValidTab = useMemo(
		() =>
			!current ||
			!current.activeTab ||
			TAB_WITHOUT_DATA.some((prefix) => current.activeTab?.toString().includes(prefix)),
		[current],
	);
	if (isInValidTab) {
		return <div className={classNames('text-center', 'mt-10px')}>无法获取任务详情</div>;
	}

	return (
		<molecule.component.Scrollable>
			<div className="m-taksdetail panel-content ouput-panel">
				<Collapse activeKey={panelActiveKey} bordered={false} onChange={handleActiveKey}>
					{panelColumn.map((col, index) => {
						return (
							<Panel
								header={panelHeader(index)}
								key={index.toString()}
								style={{ position: 'relative' }}
								className="input-panel"
								extra={
									<Popconfirm
										placement="topLeft"
										title="你确定要删除此源表吗？"
										onConfirm={() => changeInputTabs('delete', index)}
										onCancel={(e) => e?.stopPropagation()}
										{...{
											onClick: (e: any) => {
												e.stopPropagation();
											},
										}}
									>
										<DeleteOutlined className={classNames('title-icon')} />
									</Popconfirm>
								}
							>
								<ResultForm
									isShow={panelActiveKey.includes(index.toString())}
									sync={sync}
									getTableType={getTableType}
									onFormValuesChanged={onFormValuesChanged}
									data={panelColumn[index]}
									dataSourceOptionList={originOptionType[col.type!] || []}
									tableOptionType={
										tableOptionType[`${col.sourceId}-${col.schema || ''}`] || {}
									}
									tableColumnOptionType={
										tableColumnOptionType[`${col.table}-${col.schema || ''}`] ||
										[]
									}
									topicOptionType={topicOptionType[col.sourceId || -1] || []}
									textChange={() => {
										dispathState({ sync: false });
									}}
									componentVersion={currentPage.componentVersion}
								/>
							</Panel>
						);
					})}
				</Collapse>
				<Button
					className="stream-btn"
					onClick={() => changeInputTabs('add')}
					icon={<PlusOutlined />}
				>
					添加结果表
				</Button>
			</div>
		</molecule.component.Scrollable>
	);
}
