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
import type { IEditor } from '@dtinsight/molecule/esm/model';
import { Button, Collapse, Popconfirm } from 'antd';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import { useEffect, useMemo, useState } from 'react';
import { getTimeZoneList } from '../panelData';
import { CODE_TYPE, DATA_SOURCE_ENUM, KAFKA_DATA_TYPE } from '@/constant';
import classNames from 'classnames';
import { Utils } from '@dtinsight/dt-utils/lib';
import stream from '@/api/stream';
import { cloneDeep } from 'lodash';
import { streamTaskActions } from '../../taskFunc';
import { TAB_WITHOUT_DATA } from '@/pages/rightBar';
import { isAvro, isKafka } from '@/utils/enums';
import { changeCustomParams } from '../customParamsUtil';
import { parseColumnText } from '../flinkHelper';
import SourceForm from './form';
import molecule from '@dtinsight/molecule';
import type { IDataSourceUsedInSyncProps, IFlinkSourceProps } from '@/interface';
import type { DefaultOptionType } from 'antd/lib/cascader';
import './index.scss';

const { Panel } = Collapse;
const DEFAULT_TYPE = DATA_SOURCE_ENUM.KAFKA_2X;

/**
 * 创建源表的默认输入内容
 */
const DEFAULT_INPUT_VALUE: Partial<IFlinkSourceProps> = {
	type: DEFAULT_TYPE,
	sourceId: undefined,
	topic: undefined,
	charset: CODE_TYPE.UTF_8,
	table: undefined,
	timeType: 1,
	timeTypeArr: [1],
	timeZone: 'Asia/Shanghai', // 默认时区值
	offset: 0,
	offsetUnit: 'SECOND',
	columnsText: undefined,
	parallelism: 1,
	offsetReset: 'latest',
	sourceDataType: 'dt_nest',
};

/**
 * 等待输入的源表数据，只有 panelkey 是必存在的，其余属性都是待输入
 */
export type PendingInputColumnType = Partial<Omit<IFlinkSourceProps, 'panelKey'>> & {
	panelKey: IFlinkSourceProps['panelKey'];
};

export default function FlinkSourcePanel({ current }: Pick<IEditor, 'current'>) {
	const currentPage = current?.tab?.data || {};
	const [panelActiveKey, setPanelActiveKey] = useState<string[]>([]);
	// 当前源表全部数据
	const [panelColumn, setPanelColumn] = useState<PendingInputColumnType[]>(
		currentPage?.source || [],
	);
	const [originOptionType, setOriginOptionType] = useState<
		Record<number, IDataSourceUsedInSyncProps[]>
	>({});
	const [topicOptionType, setTopicOptionType] = useState<Record<number, string[]>>({});
	const [sync, setSync] = useState(false);
	// 时区数据
	const [timeZoneData, setTimeZoneData] = useState<DefaultOptionType[]>([]);

	const initTimeZoneList = async () => {
		const list = await getTimeZoneList();
		setTimeZoneData(list);
	};

	// 获取数据源
	const getTypeOriginData = async (type?: DATA_SOURCE_ENUM) => {
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

	const getTopicType = async (sourceId?: number) => {
		if (sourceId) {
			// improve the performance
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
	const handlePanelChanged = (type: 'add' | 'delete', panelKey?: string) => {
		let nextPanelActiveKey = panelActiveKey;
		let nextPanelColumn = panelColumn;
		if (type === 'add') {
			const key = Utils.generateAKey();
			nextPanelColumn.push({
				...DEFAULT_INPUT_VALUE,
				panelKey: key,
			});
			getTypeOriginData(DEFAULT_INPUT_VALUE.type);
			getTopicType(DEFAULT_INPUT_VALUE.sourceId);
			nextPanelActiveKey.push(key);
		} else {
			nextPanelColumn = nextPanelColumn.filter((panel) => {
				return panelKey !== panel.panelKey;
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
		const panelColumnSocp = cloneDeep(panelColumn);
		const panel = panelColumnSocp[index];
		let shouldUpdateEditor = false;
		switch (type) {
			case 'type': {
				panelColumnSocp[index] = {
					...DEFAULT_INPUT_VALUE,
					panelKey: panel.panelKey,
					[type]: value,
				};
				getTypeOriginData(value);
				shouldUpdateEditor = true;
				if (isKafka(value)) {
					if (value === DATA_SOURCE_ENUM.KAFKA_CONFLUENT) {
						panel.sourceDataType = KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT;
					} else {
						panel.sourceDataType = KAFKA_DATA_TYPE.TYPE_JSON;
					}
				}

				break;
			}
			case 'sourceId': {
				panelColumnSocp[index] = {
					...DEFAULT_INPUT_VALUE,
					type: panel.type,
					panelKey: panel.panelKey,
					sourceDataType: panel.sourceDataType,
					[type]: value,
				};
				getTopicType(value);
				shouldUpdateEditor = true;
				break;
			}
			case 'customParams': {
				changeCustomParams(panel, value, subValue);
				break;
			}
			case 'columnsText': {
				const columns = parseColumnText(value);
				panel.columnsText = value;
				panel.timeColumn = timeColumnCheck(columns);
				break;
			}
			case 'sourceDataType':
				panel.sourceDataType = value;
				if (!isAvro(value)) {
					panel.schemaInfo = undefined;
				}
				break;
			case 'timeTypeArr':
				panel.timeTypeArr = value;
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
				// @ts-ignore
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
						return c.column === panel.timeColumn;
					})
				) {
					return undefined;
				}
			}
			return panel.timeColumn;
		}
	};

	const renderPanelHeader = (
		index: number,
		panelKey: string,
		panelData: PendingInputColumnType,
	) => {
		const tableName = panelData.table;
		return (
			<div className="input-panel-title">
				<span>{` 源表 ${index + 1} ${tableName ? `(${tableName})` : ''}`}</span>
			</div>
		);
	};

	useEffect(() => {
		initTimeZoneList();
	}, []);

	useEffect(() => {
		if (!isInValidTab) {
			currentPage?.source.forEach((s: IFlinkSourceProps) => {
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
					{panelColumn.map((panelColumnData, index) => {
						const key = panelColumnData.panelKey;
						return (
							<Panel
								header={renderPanelHeader(index, key, panelColumnData)}
								key={key}
								extra={
									<Popconfirm
										placement="topLeft"
										title="你确定要删除此源表吗？"
										onConfirm={() => handlePanelChanged('delete', key)}
										{...{
											onClick: (e: any) => {
												e.stopPropagation();
											},
										}}
									>
										<DeleteOutlined className={classNames('title-icon')} />
									</Popconfirm>
								}
								style={{ position: 'relative' }}
								className="input-panel"
							>
								<SourceForm
									isShow={panelActiveKey.indexOf(key) > -1}
									sync={sync}
									handleInputChange={handleInputChange.bind(undefined, index)}
									panelColumn={panelColumnData}
									topicOptionType={
										topicOptionType[panelColumnData.sourceId || -1] || []
									}
									originOptionType={
										originOptionType[panelColumnData.type || -1] || []
									}
									timeZoneData={timeZoneData}
									currentPage={currentPage}
									textChange={() => {
										setSync(false);
									}}
								/>
							</Panel>
						);
					})}
				</Collapse>
				<Button
					className="stream-btn"
					onClick={() => handlePanelChanged('add')}
					icon={<PlusOutlined />}
				>
					<span>添加源表</span>
				</Button>
			</div>
		</molecule.component.Scrollable>
	);
}
