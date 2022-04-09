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
import { IEditor } from '@dtinsight/molecule/esm/model';
import { Button, Collapse, Popconfirm } from 'antd';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import LockPanel from '../lockPanel';
import { useEffect, useMemo, useState } from 'react';
import { getCreateTypes, getDataBaseList, getTimeZoneList } from '../panelData';
import { CODE_TYPE, DATA_SOURCE_ENUM, KAFKA_DATA_TYPE, TABLE_SOURCE, TABLE_TYPE } from '@/constant';
import classNames from 'classnames';
import { Utils } from '@dtinsight/dt-utils/lib';
import stream from '@/api/stream';
import { cloneDeep, isEmpty } from 'lodash';
import { streamTaskActions } from '../../taskFunc';
import { TAB_WITHOUT_DATA } from '@/pages/rightBar';
import { isAvro, isKafka } from '@/utils/enums';
import { changeCustomParams } from '../customParamsUtil';
import { parseColumnText } from '../flinkHelper';
import SourceForm from './form';
import molecule from '@dtinsight/molecule';
import type { IDataSourceUsedInSyncProps } from '@/interface';
import './index.scss';

const { Panel } = Collapse;
const DEFAULT_TABLE_SOURCE = TABLE_SOURCE.DATA_CREATE;
const DEFAULT_TYPE = DATA_SOURCE_ENUM.KAFKA_2X;

/**
 * 创建源表的默认输入内容
 */
const DEFAULT_INPUT_VALUE = {
	createType: DEFAULT_TABLE_SOURCE,
	type: DEFAULT_TYPE,
	sourceId: undefined,
	topic: [],
	dbId: undefined,
	tableId: undefined,
	charset: CODE_TYPE.UTF_8,
	table: undefined,
	timeType: 1,
	timeTypeArr: [1],
	timeZone: 'Asia/Shanghai', // 默认时区值
	timeColumn: undefined,
	offset: 0,
	offsetUnit: 'SECOND',
	columnsText: undefined,
	parallelism: 1,
	offsetReset: 'latest',
	timestampOffset: null,
	sourceDataType: 'dt_nest',
};

export default function FlinkSourcePanel({ current }: Pick<IEditor, 'current'>) {
	const currentPage = current?.tab?.data || {};
	const [panelActiveKey, setPanelActiveKey] = useState<string[]>([]);
	const [panelColumn, setPanelColumn] = useState(currentPage?.source || []);
	const [originOptionType, setOriginOptionType] = useState<
		Record<number, IDataSourceUsedInSyncProps[]>
	>({});
	const [assetTableOptionType, setAssetTableOptionType] = useState<any>({});
	const [topicOptionType, setTopicOptionType] = useState<Record<number, string[]>>({});
	const [sync, setSync] = useState(false);
	const [timeZoneData, setTimeZoneData] = useState<string[]>([]);
	const [dataBaseOptionType, setDataBaseOptionType] = useState<any>([]);

	const initTimeZoneList = async () => {
		const list = await getTimeZoneList();
		setTimeZoneData(list);
	};

	// 获取数据源
	const getTypeOriginData = async (type: DATA_SOURCE_ENUM) => {
		if (type !== undefined) {
			const existData = originOptionType[type];
			if (existData) {
				return;
			}
			const res = await stream.getTypeOriginData({ type });
			if (res.code === 1) {
				originOptionType[type] = res.data;
			} else {
				originOptionType[type] = [];
			}
			// 没有新建对象来 setState，当有多个源表同时请求数据源的话，新建对象的话会导致旧对象会被新对象覆盖掉
			setOriginOptionType({ ...originOptionType });
		}
	};

	// 获取元数据下对应表
	const getAssetTableList = async (dbId: any) => {
		// if (dbId) {
		// 	const existData = assetTableOptionType[dbId];
		// 	if (existData) {
		// 		return;
		// 	}
		// 	let v = await stream.getAssetTableList({
		// 		dbId: dbId,
		// 		tableType: TABLE_TYPE.SOURCE_TABLE,
		// 	});
		// 	if (v.code == 1) {
		// 		setAssetTableOptionType({
		// 			...assetTableOptionType,
		// 			[dbId]: v.data,
		// 		});
		// 	}
		// }
	};

	// 获取元数据下所有信息
	const getAssetData = async (tableId: any) => {
		// let params = {};
		// const res = await stream.getAssetTableDetail({
		// 	tableId,
		// });
		// if (res.code === 1 && !isEmpty(res.data)) {
		// 	const { sourceTableParam, columns, charset } = res.data;
		// 	params = Object.assign(sourceTableParam, {
		// 		columns: columns,
		// 		charset,
		// 	});
		// }
		// return params;
	};

	const getTopicType = async (sourceId?: number) => {
		if (sourceId) {
			const existTopic = topicOptionType[sourceId];
			if (existTopic) {
				return;
			}
			const res = await stream.getTopicType({ sourceId });
			if (res.code === 1) {
				setTopicOptionType({
					...topicOptionType,
					[sourceId]: res.data,
				});
			}
		}
	};

	/**
	 * 添加或删除源表
	 * @param panelKey 删除的时候需要带上 panelKey
	 */
	const changeInputTabs = (type: 'add' | 'delete', panelKey?: string) => {
		let nextPanelActiveKey = panelActiveKey;
		let nextPanelColumn = panelColumn;
		if (type === 'add') {
			const key = Utils.generateAKey();
			nextPanelColumn.push({
				...DEFAULT_INPUT_VALUE,
				_panelKey: key,
			});
			getTypeOriginData(DEFAULT_INPUT_VALUE.type);
			getTopicType(DEFAULT_INPUT_VALUE.sourceId);
			nextPanelActiveKey.push(key);
		} else {
			nextPanelColumn = nextPanelColumn.filter((panel: any) => {
				return panelKey !== panel._panelKey;
			});
			nextPanelActiveKey = nextPanelActiveKey.filter((key) => {
				return panelKey !== key;
			});
		}
		streamTaskActions.setCurrentPageValue('source', nextPanelColumn, true);
		setPanelActiveKey(nextPanelActiveKey);
		setPanelColumn(nextPanelColumn);
	};

	// 时区不做处理
	const handleInputChange = async (index: any, type: any, value: any, subValue: any) => {
		// 监听数据改变
		let panelColumnSocp = cloneDeep(panelColumn);
		let panel = panelColumnSocp[index];
		let shouldUpdateEditor = false;
		switch (type) {
			case 'createType': {
				panel = panelColumnSocp[index] = {
					...DEFAULT_INPUT_VALUE,
					_panelKey: panel._panelKey,
					sourceDataType: panel.sourceDataType,
				};
				panel[type] = value;
				if (value === TABLE_SOURCE.DATA_CREATE) {
					panelColumnSocp['type'] = DEFAULT_TYPE;
					getTypeOriginData(DEFAULT_TYPE);
				}
				shouldUpdateEditor = true;
				break;
			}
			case 'dbId': {
				const db = dataBaseOptionType.find((item: any) => item.dbId === value);
				panel = panelColumnSocp[index] = {
					...DEFAULT_INPUT_VALUE,
					createType: panel.createType,
					[type]: value,
					assetsDbName: db?.dbName,
					_panelKey: panel._panelKey,
					sourceDataType: panel.sourceDataType,
				};
				getAssetTableList(value);
				shouldUpdateEditor = true;
				break;
			}
			case 'tableId': {
				// 获取当前元数据的类型，并设置
				const tableList = assetTableOptionType[panel.dbId] || [];
				const tableData = tableList.find((item: any) => item.tableId === value);
				const params = await getAssetData(value);
				panel = panelColumnSocp[index] = {
					...DEFAULT_INPUT_VALUE,
					...params,
					createType: panel.createType,
					dbId: panel.dbId,
					assetsDbName: panel.assetsDbName,
					[type]: value,
					assetsTableName: tableData?.tableName,
					_panelKey: panel._panelKey,
					sourceDataType: panel.sourceDataType,
				};
				shouldUpdateEditor = true;
				break;
			}
			case 'targetCol': {
				// 去除空格汉字
				const reg = /[\u4E00-\u9FA5]|[\uFE30-\uFFA0]/gi;
				let val = subValue;
				if (subValue) {
					val = Utils.trimAll(subValue);
					if (reg.test(val)) {
						val = subValue.replace(reg, '');
					}
				} else {
					val = undefined;
				}
				panel['columns'][value].targetCol = val;

				let columns = panel.columns.map(({ column, targetCol }: any) => ({
					column: targetCol || column,
				}));
				panel.timeColumn = timeColumnCheck(columns);
				break;
			}
			case 'type': {
				panel = panelColumnSocp[index] = {
					...DEFAULT_INPUT_VALUE,
					createType: panel.createType,
					_panelKey: panel._panelKey,
				};
				panel[type] = value;
				getTypeOriginData(value);
				shouldUpdateEditor = true;
				if (isKafka(value)) {
					if (value === DATA_SOURCE_ENUM.KAFKA_CONFLUENT) {
						panel['sourceDataType'] = KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT;
					} else {
						panel['sourceDataType'] = KAFKA_DATA_TYPE.TYPE_JSON;
					}
				}

				break;
			}
			case 'sourceId': {
				panel = panelColumnSocp[index] = {
					...DEFAULT_INPUT_VALUE,
					createType: panel.createType,
					type: panel.type,
					_panelKey: panel._panelKey,
					sourceDataType: panel.sourceDataType,
				};
				panel[type] = value;
				getTopicType(value);
				shouldUpdateEditor = true;
				break;
			}
			case 'customParams': {
				changeCustomParams(panel, value, subValue);
				break;
			}
			case 'columnsText': {
				let columns = parseColumnText(value);
				panel[type] = value;
				panel.timeColumn = timeColumnCheck(columns);
				break;
			}
			case 'sourceDataType':
				panel[type] = value;
				if (!isAvro(value)) {
					panel.schemaInfo = undefined;
				}
				break;
			case 'timeTypeArr':
				panel[type] = value;
				// timeTypeArr 这个字段只有前端用，根据 timeTypeArr ，清空相应字段
				// 不勾选 ProcTime，不传 procTime 名称字段
				// 不勾选 EventTime，不传时间列、最大延迟时间字段
				if (currentPage.componentVersion === '1.12') {
					if (!value.includes(1)) {
						panel.procTime = undefined;
					}
					if (!value.includes(2)) {
						panel.timeColumn = undefined;
						panel.offset = undefined;
					}
				}
				break;
			default: {
				panel[type] = value;
			}
		}
		streamTaskActions.setCurrentPageValue('source', panelColumnSocp, true);
		setSync(shouldUpdateEditor);
		setPanelColumn(panelColumnSocp);

		// timeColumn 是否需要重置
		function timeColumnCheck(columns: any) {
			if (panel.timeColumn) {
				if (
					!columns.find((c: any) => {
						return c.column == panel.timeColumn;
					})
				) {
					return undefined;
				}
			}
			return panel.timeColumn;
		}
	};

	useEffect(() => {
		initTimeZoneList();
	}, []);

	const panelHeader = (index: number, panelKey: string, panelData: any) => {
		const tableName = panelData.table;
		return (
			<div className="input-panel-title">
				<span>{` 源表 ${index + 1} ${tableName ? `(${tableName})` : ''}`}</span>
				<Popconfirm
					placement="topLeft"
					title="你确定要删除此源表吗？"
					onConfirm={() => changeInputTabs('delete', panelKey)}
					{...{
						onClick: (e: any) => {
							e.stopPropagation();
						},
					}}
				>
					<DeleteOutlined className={classNames('title-icon')} />
				</Popconfirm>
			</div>
		);
	};

	useEffect(() => {
		if (!isInValidTab) {
			currentPage?.source.forEach((s) => {
				getTypeOriginData(s.type);
			});
		}
	}, [current]);

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
			<div className="m-taksdetail panel-content">
				<Collapse
					activeKey={panelActiveKey}
					bordered={false}
					onChange={(key) => setPanelActiveKey(key as string[])}
				>
					{panelColumn.map((panelColumnData: any, index: any) => {
						const key = panelColumnData._panelKey;
						return (
							<Panel
								header={panelHeader(index, key, panelColumnData)}
								key={key}
								style={{ position: 'relative' }}
								className="input-panel"
							>
								<SourceForm
									isShow={panelActiveKey.indexOf(key) > -1}
									sync={sync}
									handleInputChange={handleInputChange.bind(undefined, index)}
									panelColumn={panelColumnData}
									topicOptionType={
										topicOptionType[panelColumnData.sourceId] || []
									}
									originOptionType={originOptionType[panelColumnData.type] || []}
									timeZoneData={timeZoneData}
									currentPage={currentPage}
									textChange={() => {
										setSync(false);
									}}
								/>
								<LockPanel lockTarget={currentPage} />
							</Panel>
						);
					})}
				</Collapse>
				<Button
					className="stream-btn"
					onClick={() => changeInputTabs('add')}
					icon={<PlusOutlined />}
				>
					<span>添加源表</span>
				</Button>
			</div>
		</molecule.component.Scrollable>
	);
}
