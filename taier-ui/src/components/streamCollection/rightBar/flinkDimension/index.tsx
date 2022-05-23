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

import { useEffect, useMemo, useState } from 'react';
import { Button, Collapse, message, Popconfirm } from 'antd';
import classNames from 'classnames';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import { DATA_SOURCE_ENUM } from '@/constant';
import stream from '@/api/stream';
import {
	haveSchema,
	haveTableColumn,
	haveTableList,
	isCacheExceptLRU,
	isTaskTab,
} from '@/utils/enums';
import molecule from '@dtinsight/molecule';
import type { IDataColumnsProps, IDataSourceUsedInSyncProps, IFlinkSideProps } from '@/interface';
import DimensionForm from './form';

const { Panel } = Collapse;

type IFlinkDimensionProps = molecule.model.IEditor;

const defualtPanelData: Partial<IFlinkSideProps> = {
	type: DATA_SOURCE_ENUM.MYSQL,
	columns: [],
	sourceId: undefined,
	table: undefined,
	tableName: undefined,
	primaryKey: undefined,
	hbasePrimaryKey: undefined,
	hbasePrimaryKeyType: undefined,
	errorLimit: undefined,
	esType: undefined,
	index: undefined,
	parallelism: 1,
	cache: 'LRU',
	cacheSize: 10000,
	cacheTTLMs: 60000,
	asyncPoolSize: 5,
};

export default function FlinkDimensionPanel({ current }: IFlinkDimensionProps) {
	const [panelActiveKey, setPanelKey] = useState<string[]>([]);
	const [panelColumn, setPanelColumn] = useState<Partial<IFlinkSideProps>[]>(
		current?.tab?.data.side || [],
	);
	// 数据源下拉列表，用 key 值缓存结果
	const [dataSourceOptions, setDataSourceOptions] = useState<
		Record<string, IDataSourceUsedInSyncProps[]>
	>({});
	/**
	 * 表下拉列表,以 soureceId-schema 拼接作 key 值，后以 searchKey 作为 key 值，最后是搜索结果
	 * {
	 * 	[`${sourceId}-${schema}`]: {[`${searchKey}`]: string[]}}
	 * }
	 */
	const [tableOptions, setTableOptions] = useState<Record<string, Record<string, string[]>>>({});
	// 表字段列下拉列表
	const [columnsOptions, setColOptions] = useState<Record<string, IDataColumnsProps[]>>({});

	const getTableColumns = (sourceId: number, tableName: string, schema = '') => {
		if (!sourceId || !tableName) {
			return;
		}
		const uniqKey = `${sourceId}-${tableName}-${schema}`;
		if (!columnsOptions[uniqKey]) {
			stream
				.getStreamTableColumn({
					sourceId,
					tableName,
					schema,
					flinkVersion: current?.tab?.data?.componentVersion,
				})
				.then((v) => {
					if (v.code === 1) {
						setColOptions((options) => {
							const next = options;
							next[uniqKey] = v.data;
							return { ...next };
						});
					}
				});
		}
	};

	/**
	 * 获取Schema列表
	 * @deprecated 暂时没有数据源会有请求 schema 列表
	 */
	const getSchemaData = async () => {};

	const getTypeOriginData = (type: DATA_SOURCE_ENUM) => {
		if (!dataSourceOptions[type]) {
			stream.getTypeOriginData({ type }).then((v) => {
				if (v.code === 1) {
					setDataSourceOptions((options) => {
						const next = options;
						next[type] = v.data;
						return { ...next };
					});
				}
			});
		}
	};

	// 获取表
	const getTableType = async (
		type: DATA_SOURCE_ENUM,
		sourceId: number,
		schema?: string,
		searchKey?: string,
	) => {
		const uniqKey = `${sourceId}-${schema}`;
		// postgresql kingbasees8 schema必填处理
		const disableReq =
			(type === DATA_SOURCE_ENUM.POSTGRESQL || type === DATA_SOURCE_ENUM.KINGBASE8) &&
			!schema;
		if (disableReq) {
			return;
		}
		stream
			.listTablesBySchema({
				sourceId,
				schema: schema || '',
				isSys: false,
				searchKey,
			})
			.then((res) => {
				if (res.code === 1) {
					setTableOptions((options) => {
						const next = options;
						if (next[uniqKey]) {
							next[uniqKey][searchKey || ''] = res.data;
						} else {
							next[uniqKey] = { [searchKey || '']: res.data };
						}
						return { ...next };
					});
				}
			});
	};

	const handleActiveKey = (key: string | string[]) => {
		setPanelKey(key as string[]);
	};

	const handleFormChanged = (
		preVal: Partial<IFlinkSideProps>,
		nextVal: Partial<IFlinkSideProps>,
	) => {
		const checkedKeys = Object.keys(nextVal).filter(
			(key) => nextVal[key as keyof IFlinkSideProps] !== preVal[key as keyof IFlinkSideProps],
		) as (keyof IFlinkSideProps)[];

		let doNextVal = nextVal;
		if (checkedKeys.includes('type')) {
			// reset all fields
			doNextVal = {
				...defualtPanelData,
				type: nextVal.type,
				cache: isCacheExceptLRU(doNextVal.type) ? 'ALL' : 'LRU',
			};
			getTypeOriginData(doNextVal.type!);
		}

		if (checkedKeys.includes('sourceId')) {
			doNextVal = {
				...defualtPanelData,
				type: nextVal.type,
				sourceId: nextVal.sourceId,
				cache: isCacheExceptLRU(doNextVal.type) ? 'ALL' : 'LRU',
			};

			if (haveTableList(doNextVal.type)) {
				getTableType(doNextVal.type!, doNextVal.sourceId!, doNextVal.schema);

				if (haveSchema(doNextVal.type)) {
					getSchemaData();
				}
			}
		}

		if (checkedKeys.includes('schema')) {
			doNextVal = {
				...defualtPanelData,
				type: nextVal.type,
				sourceId: nextVal.sourceId,
				customParams: nextVal.customParams,
				schema: nextVal.schema,
				cache: isCacheExceptLRU(doNextVal.type) ? 'ALL' : 'LRU',
			};

			if (haveTableList(doNextVal.type)) {
				getTableType(doNextVal.type!, doNextVal.sourceId!, doNextVal.schema);
			}
		}

		if (checkedKeys.includes('table')) {
			doNextVal = {
				...defualtPanelData,
				type: nextVal.type,
				sourceId: nextVal.sourceId,
				customParams: nextVal.customParams,
				schema: nextVal.schema,
				table: nextVal.table,
				cache: isCacheExceptLRU(doNextVal.type) ? 'ALL' : 'LRU',
			};

			if (haveTableColumn(doNextVal.type)) {
				getTableColumns(doNextVal.sourceId!, doNextVal.table!, doNextVal.schema);
			}
		}

		setPanelColumn((cols) => {
			const nextCols = cols.concat();
			const idx = nextCols.indexOf(preVal);
			if (idx !== -1) {
				nextCols[idx] = doNextVal;
			}
			return nextCols;
		});
	};

	const changeInputTabs = (type: 'add' | 'delete', index?: number) => {
		switch (type) {
			case 'add': {
				const length = panelColumn.push({ ...defualtPanelData });
				setPanelColumn(panelColumn.concat());
				setPanelKey((keys) => {
					keys.push(length.toString());
					return keys.concat();
				});
				getTypeOriginData(defualtPanelData.type!);
				break;
			}
			case 'delete': {
				if (index === undefined) {
					message.error('删除失败');
					return;
				}
				panelColumn.splice(index, 1);
				setPanelKey((keys) => {
					const idx = keys.indexOf(index.toString());
					if (idx !== -1) {
						keys.splice(idx, 1);
					}
					return keys.concat();
				});
				setPanelColumn(panelColumn.concat());
				break;
			}
			default:
				break;
		}
	};

	useEffect(() => {
		if (panelColumn.length && current?.tab) {
			molecule.editor.updateTab({
				id: current.tab.id,
				data: {
					...current.tab.data,
					side: panelColumn,
				},
			});
		}
	}, [panelColumn]);

	useEffect(() => {
		if (!isInValidTab) {
			current?.tab?.data?.side?.forEach((item: IFlinkSideProps) => {
				getTypeOriginData(item.type);
				getTableType(item.type, item.sourceId, item.schema);
				getTableColumns(item.sourceId, item.table || '', item.schema);
			});
		}
	}, [current]);

	/**
	 * 当前的 tab 是否不合法，如不合法则展示 Empty
	 */
	const isInValidTab = useMemo(() => !isTaskTab(current?.tab?.id), [current]);
	if (isInValidTab) {
		return <div className={classNames('text-center', 'mt-10px')}>无法获取任务详情</div>;
	}

	return (
		<molecule.component.Scrollable>
			<div className="m-taksdetail panel-content">
				<Collapse activeKey={panelActiveKey} bordered={false} onChange={handleActiveKey}>
					{panelColumn.map((col, index) => {
						return (
							<Panel
								header={`维表 ${index + 1} ${
									col.tableName ? `(${col.tableName})` : ''
								}`}
								key={index.toString()}
								className="input-panel"
								extra={
									<Popconfirm
										placement="topLeft"
										title="你确定要删除此维表吗？"
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
								<DimensionForm
									data={col}
									sourceOptions={dataSourceOptions[col.type!]}
									tableOptions={tableOptions[`${col.sourceId}-${col.schema}`]}
									columnsOptions={
										columnsOptions[
											`${col.sourceId}-${col.table}-${col.schema || ''}`
										]
									}
									onTableSearch={getTableType}
									onValuesChange={handleFormChanged}
								/>
							</Panel>
						);
					})}
				</Collapse>
				<Button
					className="stream-btn"
					onClick={() => {
						changeInputTabs('add');
					}}
					style={{ borderRadius: 5 }}
				>
					<PlusOutlined />
					<span>添加维表</span>
				</Button>
			</div>
		</molecule.component.Scrollable>
	);
}
