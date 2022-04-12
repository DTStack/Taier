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
import {
	CODE_TYPE,
	DATA_SOURCE_ENUM,
	DATA_SOURCE_TEXT,
	DATA_SOURCE_VERSION,
	formItemLayout,
	KAFKA_DATA_LIST,
	KAFKA_DATA_TYPE,
	SOURCE_TIME_TYPE,
} from '@/constant';
import { isAvro, isKafka, showTimeForOffsetReset } from '@/utils/enums';
import {
	Button,
	Cascader,
	Checkbox,
	Col,
	DatePicker,
	Form,
	Input,
	InputNumber,
	message,
	Radio,
	Row,
	Select,
} from 'antd';
import { UpOutlined, DownOutlined } from '@ant-design/icons';
import React, { useMemo, useState } from 'react';
import { generateSourceValidDes, parseColumnText } from '../flinkHelper';
import Editor from '@/components/editor';
import { debounce, isString } from 'lodash';
import { CustomParams } from '../component/customParams';
import DataPreviewModal from '../../source/dataPreviewModal';
import { generateMapValues } from '../customParamsUtil';
import type { IDataSourceUsedInSyncProps } from '@/interface';
import type { PendingInputColumnType } from '.';
import type { DefaultOptionType } from 'antd/lib/cascader';
import CodeEditor from '@/components/codeEditor';

const FormItem = Form.Item;
const { Option } = Select;

/**
 * 源表类型下拉菜单
 */
const DATASOURCE_OPTIONS_TYPE = [
	DATA_SOURCE_ENUM.KAFKA_2X,
	DATA_SOURCE_ENUM.KAFKA,
	DATA_SOURCE_ENUM.KAFKA_11,
	DATA_SOURCE_ENUM.KAFKA_10,
];

interface ISourceFormProps {
	/**
	 * 控制 editor 是否展示
	 */
	isShow: boolean;
	sync: boolean;
	/**
	 * 当前数据
	 */
	panelColumn: PendingInputColumnType;
	topicOptionType: string[];
	originOptionType: IDataSourceUsedInSyncProps[];
	timeZoneData: DefaultOptionType[];
	currentPage: any;
	handleInputChange: (type: any, value: any, subValue?: any) => void;
	textChange: () => void;
}

/**
 * 源表表单数据类型
 */
interface ISourceFormFieldProps {
	type: DATA_SOURCE_ENUM;
	sourceId: string;
	topic: string;
	charset: CODE_TYPE;
	sourceDataType?: typeof KAFKA_DATA_TYPE;
	schemaInfo?: string;
	table: string;
	offsetReset: 'latest' | 'earliest' | 'custom' | 'timestamp';
	timestampOffset?: string;
	timeType?: 1 | 2;
	timeTypeArr?: number[];
	procTime?: string;
	timeColumn?: string;
	offset?: number;
	parallelism?: number;
	timeZone?: string;
	// 自定义参数
	[key: string]: any;
}

export default function SourceForm({
	isShow,
	handleInputChange,
	sync,
	panelColumn,
	currentPage,
	topicOptionType = [],
	originOptionType = [],
	timeZoneData = [],
	textChange,
}: ISourceFormProps) {
	const [form] = Form.useForm<ISourceFormFieldProps>();
	const { componentVersion } = currentPage || {};

	const [visible, setVisible] = useState(false);
	const [params, setParams] = useState({});
	const [showAdvancedParams, setShowAdvancedParams] = useState(false);

	const originOption = (type: any, arrData: any) => {
		switch (type) {
			case 'eventTime':
				return arrData.map((v: any, index: any) => {
					return (
						<Option key={index} value={`${v.column}`}>
							{v.column}
						</Option>
					);
				});

			default:
				return null;
		}
	};
	// 获取时间列
	const getEventTimeOptionTypes = () => {
		const { columnsText } = panelColumn;
		return parseColumnText(columnsText) || [];
	};
	const showPreviewModal = () => {
		const { sourceId, topic } = panelColumn;
		let nextParam = {};
		if (!sourceId || !topic) {
			message.error('数据预览需要选择数据源和Topic！');
			return;
		}
		nextParam = { sourceId, topic };
		setVisible(true);
		setParams(nextParam);
	};

	const editorParamsChange = (type: any, value: string) => {
		textChange();
		handleInputChange(type, value);
	};

	const debounceEditorChange = debounce(editorParamsChange, 300, { maxWait: 2000 });

	const changeTimeTypeArr = (timeTypeArr: any[]) => {
		let nextTimeType = timeTypeArr.concat();
		// 勾选 EventTime 时需同时勾选 ProcTime
		if (
			!panelColumn?.timeTypeArr?.includes(SOURCE_TIME_TYPE.EVENT_TIME) &&
			nextTimeType?.includes(SOURCE_TIME_TYPE.EVENT_TIME)
		) {
			nextTimeType = [1, 2];
		}
		form.setFieldsValue({ timeTypeArr: nextTimeType });
		handleInputChange('timeTypeArr', nextTimeType);
	};

	const initialValues = useMemo(() => {
		const { timeZone, customParams, ...restCols } = panelColumn;

		const initialTimeZoneValue =
			timeZone && isString(timeZone) ? timeZone.split('/') : ['Asia', 'Shanghai'];

		return {
			timeZone: initialTimeZoneValue,
			...generateMapValues(customParams),
			...restCols,
		};
	}, [panelColumn]);

	const eventTimeOptionType = originOption('eventTime', getEventTimeOptionTypes());

	const validDes = generateSourceValidDes(panelColumn, componentVersion);

	return (
		<Row className="title-content">
			<Form<ISourceFormFieldProps>
				{...formItemLayout}
				form={form}
				initialValues={initialValues}
			>
				<FormItem label="类型" name="type" rules={validDes.type}>
					<Select<DATA_SOURCE_ENUM>
						placeholder="请选择类型"
						className="right-select"
						onChange={(v) => handleInputChange('type', v)}
						showSearch
						filterOption={(input, option) =>
							!!option?.value?.toString().toUpperCase().includes(input.toUpperCase())
						}
						options={DATASOURCE_OPTIONS_TYPE.map((t) => ({
							label: DATA_SOURCE_TEXT[t],
							value: t,
						}))}
					/>
				</FormItem>
				<FormItem label="数据源" name="sourceId" rules={validDes.sourceId}>
					<Select
						showSearch
						placeholder="请选择数据源"
						className="right-select"
						onChange={(v: any) => {
							handleInputChange('sourceId', v);
						}}
						filterOption={(input, option) =>
							!!option?.value?.toString().toUpperCase().includes(input.toUpperCase())
						}
					>
						{originOptionType.map((v) => (
							<Option key={v.dataInfoId} value={`${v.dataInfoId}`}>
								{v.dataName}
								{DATA_SOURCE_VERSION[v.dataTypeCode] &&
									` (${DATA_SOURCE_VERSION[v.dataTypeCode]})`}
							</Option>
						))}
					</Select>
				</FormItem>
				<FormItem label="Topic" name="topic" rules={validDes.topic}>
					<Select<string>
						placeholder="请选择Topic"
						className="right-select"
						onChange={(v) => {
							handleInputChange('topic', v);
						}}
						showSearch
						filterOption={(input, option) =>
							!!option?.value?.toString().toUpperCase().includes(input.toUpperCase())
						}
					>
						{topicOptionType.map((v) => (
							<Option key={v} value={`${v}`}>
								{v}
							</Option>
						))}
					</Select>
				</FormItem>
				<FormItem
					label="编码类型"
					style={{ marginBottom: '10px' }}
					name="charset"
					tooltip="编码类型：指Kafka数据的编码类型"
				>
					<Select
						placeholder="请选择编码类型"
						className="right-select"
						onChange={(v: any) => {
							handleInputChange('charset', v);
						}}
						showSearch
					>
						<Option value={CODE_TYPE.UTF_8}>{CODE_TYPE.UTF_8}</Option>
						<Option value={CODE_TYPE.GBK_2312}>{CODE_TYPE.GBK_2312}</Option>
					</Select>
				</FormItem>
				<FormItem noStyle dependencies={['type']}>
					{({ getFieldValue }) =>
						isKafka(getFieldValue('type')) && (
							<FormItem
								{...formItemLayout}
								label="读取类型"
								className="right-select"
								name="sourceDataType"
								rules={validDes.sourceDataType}
							>
								<Select
									onChange={(v: any) => {
										handleInputChange('sourceDataType', v);
									}}
								>
									{getFieldValue('type') === DATA_SOURCE_ENUM.KAFKA_CONFLUENT ? (
										<Option
											value={KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT}
											key={KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT}
										>
											{KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT}
										</Option>
									) : (
										KAFKA_DATA_LIST.map(({ text, value }) => (
											<Option value={value} key={text + value}>
												{text}
											</Option>
										))
									)}
								</Select>
							</FormItem>
						)
					}
				</FormItem>
				<FormItem noStyle dependencies={['sourceDataType']}>
					{({ getFieldValue }) =>
						isAvro(getFieldValue('sourceDataType')) && (
							<FormItem
								{...formItemLayout}
								label="Schema"
								name="schemaInfo"
								rules={validDes.schemaInfo}
							>
								<Input.TextArea
									rows={9}
									placeholder={`填写Avro Schema信息，示例如下：\n{\n\t"name": "testAvro",\n\t"type": "record",\n\t"fields": [{\n\t\t"name": "id",\n\t\t"type": "string"\n\t}]\n}`}
									onChange={(e: any) =>
										handleInputChange('schemaInfo', e.target.value)
									}
								/>
							</FormItem>
						)
					}
				</FormItem>
				<FormItem
					wrapperCol={{
						sm: {
							offset: formItemLayout.labelCol.sm.span,
							span: formItemLayout.wrapperCol.sm.span,
						},
					}}
				>
					<Button block type="link" onClick={showPreviewModal}>
						数据预览
					</Button>
				</FormItem>
				<FormItem
					label="映射表"
					name="table"
					rules={validDes.table}
					tooltip="该表是kafka中的topic映射而成，可以以SQL的方式使用它。"
				>
					<Input
						placeholder="请输入映射表名"
						className="right-input"
						onChange={(e: any) => handleInputChange('table', e.target.value)}
					/>
				</FormItem>
				<FormItem noStyle dependencies={['type']}>
					{({ getFieldValue }) => (
						<Row>
							<div className="ant-form-item-label ant-col-xs-24 ant-col-sm-6">
								<label className="required-tip">字段</label>
							</div>
							<Col span={18} style={{ marginBottom: 20, height: 202 }}>
								{isShow && (
									<CodeEditor
										style={{ minHeight: 202, height: '100%' }}
										className="bd"
										sync={sync}
										placeholder={`字段 类型, 比如 id int 一行一个字段${
											getFieldValue('type') !==
											DATA_SOURCE_ENUM.KAFKA_CONFLUENT
												? '\n\n仅支持JSON格式数据源，若为嵌套格式，\n字段名称由JSON的各层级key组合隔，例如：\n\nkey1.keya INT AS columnName \nkey1.keyb VARCHAR AS columnName'
												: ''
										}`}
										value={panelColumn.columnsText}
										onChange={(val: string) =>
											debounceEditorChange('columnsText', val)
										}
									/>
								)}
							</Col>
						</Row>
					)}
				</FormItem>
				<FormItem
					label="Offset"
					tooltip={
						<div>
							<p>latest：从Kafka Topic内最新的数据开始消费</p>
							<p>earliest：从Kafka Topic内最老的数据开始消费</p>
						</div>
					}
					name="offsetReset"
				>
					<Radio.Group
						className="right-select"
						onChange={(v: any) => {
							handleInputChange('offsetReset', v.target.value);
						}}
					>
						<Row>
							<Col span={12}>
								<Radio value="latest">latest</Radio>
							</Col>
							<Col span={12}>
								<Radio value="earliest">earliest</Radio>
							</Col>
							{showTimeForOffsetReset(panelColumn.type) && (
								<Col span={12}>
									<Radio value="timestamp">time</Radio>
								</Col>
							)}
							<Col span={12}>
								<Radio value="custom">自定义参数</Radio>
							</Col>
						</Row>
					</Radio.Group>
				</FormItem>
				<FormItem noStyle dependencies={['offsetReset']}>
					{({ getFieldValue }) =>
						getFieldValue('offsetReset') === 'timestamp' && (
							<FormItem
								label="选择时间"
								style={{ textAlign: 'left' }}
								name="timestampOffset"
								rules={[{ required: true, message: '请选择时间' }]}
							>
								<DatePicker
									onChange={(v: any) => {
										handleInputChange('timestampOffset', v.valueOf());
									}}
									showTime
									placeholder="请选择起始时间"
									format={'YYYY-MM-DD HH:mm:ss'}
									style={{ width: '100%' }}
								/>
							</FormItem>
						)
					}
				</FormItem>
				<FormItem noStyle dependencies={['offsetReset', 'offsetValue']}>
					{({ getFieldValue }) =>
						getFieldValue('offsetReset') === 'custom' && (
							<Row>
								<div className="ant-form-item-label ant-col-xs-24 ant-col-sm-6">
									<label>偏移量</label>
								</div>
								<Col span={18} style={{ marginBottom: 20, height: 202 }}>
									{isShow && (
										<Editor
											style={{ minHeight: 202, height: '100%' }}
											className="bd"
											sync={sync}
											placeholder="分区 偏移量，比如pt 2 一行一对值"
											value={getFieldValue('offsetValue')}
											onChange={debounceEditorChange.bind(
												undefined,
												'offsetValue',
											)}
										/>
									)}
								</Col>
							</Row>
						)
					}
				</FormItem>
				<FormItem
					label="时间特征"
					tooltip={
						<div>
							<p>ProcTime：按照Flink的处理时间处理</p>
							<p>EventTime：按照流式数据本身包含的业务时间戳处理</p>
							<p>
								Flink1.12后，基于事件时间的时态表 Join 开发需勾选ProcTime详情可参考
								<a
									href="https://ci.apache.org/projects/flink/flink-docs-release-1.12/zh/dev/table/streaming/joins.html"
									target="_blank"
									rel="noopener noreferrer"
								>
									帮助文档
								</a>
							</p>
						</div>
					}
					name={componentVersion !== '1.12' ? 'timeType' : 'timeTypeArr'}
				>
					{componentVersion !== '1.12' ? (
						<Radio.Group
							className="right-select"
							onChange={(v: any) => {
								handleInputChange('timeType', v.target.value);
							}}
						>
							<Radio value={1}>ProcTime</Radio>
							<Radio value={2}>EventTime</Radio>
						</Radio.Group>
					) : (
						<Checkbox.Group
							options={[
								{ label: 'ProcTime', value: 1 },
								{ label: 'EventTime', value: 2 },
							]}
							onChange={(value) => {
								changeTimeTypeArr(value);
							}}
						/>
					)}
				</FormItem>
				<FormItem noStyle dependencies={['timeTypeArr']}>
					{({ getFieldValue }) =>
						componentVersion === '1.12' &&
						getFieldValue('timeTypeArr')?.includes?.(SOURCE_TIME_TYPE.PROC_TIME) && (
							<FormItem
								label="ProcTime 名称"
								name="procTime"
								rules={[
									{ pattern: /^\w*$/, message: '仅支持输入英文、数字、下划线' },
								]}
							>
								<Input
									className="right-input"
									maxLength={64}
									placeholder="自定义ProcTime名称，为空时默认为 proc_time"
									onChange={(e) => {
										handleInputChange('procTime', e.target.value);
									}}
								/>
							</FormItem>
						)
					}
				</FormItem>
				<FormItem noStyle dependencies={['timeType', 'timeTypeArr']}>
					{({ getFieldValue }) =>
						((componentVersion !== '1.12' &&
							getFieldValue('timeType') === SOURCE_TIME_TYPE.EVENT_TIME) ||
							(componentVersion === '1.12' &&
								getFieldValue('timeTypeArr')?.includes?.(
									SOURCE_TIME_TYPE.EVENT_TIME,
								))) && (
							<React.Fragment>
								<FormItem
									label="时间列"
									name="timeColumn"
									rules={validDes.timeColumn}
								>
									<Select
										placeholder="请选择"
										className="right-select"
										onChange={(v: any) => {
											handleInputChange('timeColumn', v);
										}}
										showSearch
										filterOption={(input: any, option: any) =>
											option.props.children
												.toLowerCase()
												.indexOf(input.toLowerCase()) >= 0
										}
									>
										{eventTimeOptionType}
									</Select>
								</FormItem>
								<FormItem
									label="最大延迟时间"
									tooltip="当event time超过最大延迟时间时，系统自动丢弃此条数据"
									name="offset"
									rules={validDes.offset}
								>
									<InputNumber
										min={0}
										className="number-input"
										style={{
											width: componentVersion === '1.12' ? '100%' : '90%',
											height: '32px',
										}}
										onChange={(value: any) =>
											handleInputChange('offset', value)
										}
										addonAfter={
											componentVersion === '1.12' ? (
												<Form.Item
													name="offsetUnit"
													noStyle
													initialValue="SECOND"
												>
													<Select
														className="right-select"
														style={{ width: 80 }}
														onChange={(value) => {
															handleInputChange('offsetUnit', value);
														}}
													>
														<Option value="SECOND">sec</Option>
														<Option value="MINUTE">min</Option>
														<Option value="HOUR">hour</Option>
														<Option value="DAY">day</Option>
														<Option value="MONTH">mon</Option>
														<Option value="YEAR">year</Option>
													</Select>
												</Form.Item>
											) : (
												'ms'
											)
										}
									/>
								</FormItem>
							</React.Fragment>
						)
					}
				</FormItem>
				{/* 高级参数按钮 */}
				<FormItem wrapperCol={{ span: 24 }}>
					<Button
						block
						type="link"
						onClick={() => setShowAdvancedParams(!showAdvancedParams)}
					>
						高级参数{showAdvancedParams ? <UpOutlined /> : <DownOutlined />}
					</Button>
				</FormItem>
				{/* 高级参数抽屉 */}
				<FormItem hidden={!showAdvancedParams} noStyle>
					<FormItem name="parallelism" label="并行度">
						<InputNumber
							className="number-input"
							min={1}
							onChange={(value: any) => handleInputChange('parallelism', value)}
						/>
					</FormItem>
					<FormItem
						label="时区"
						tooltip="注意：时区设置功能目前只支持时间特征为EventTime的任务"
						name="timeZone"
					>
						<Cascader
							allowClear={false}
							onChange={(value: any) =>
								handleInputChange('timeZone', value.join('/'))
							}
							placeholder="请选择时区"
							showSearch
							options={timeZoneData}
						/>
					</FormItem>
					<CustomParams
						customParams={panelColumn.customParams || []}
						onChange={(type: any, id?: any, value?: any) => {
							handleInputChange('customParams', value, { id, type });
						}}
					/>
				</FormItem>
			</Form>
			<DataPreviewModal
				visible={visible}
				type={panelColumn?.type}
				onCancel={() => {
					setVisible(false);
				}}
				params={params}
			/>
		</Row>
	);
}
