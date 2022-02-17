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

// eslint-disable-next-line max-classes-per-file
import * as React from 'react';
import { Form } from '@ant-design/compatible';
import { connect } from 'react-redux';
import { InputNumber, Input, Select, Button, AutoComplete, Checkbox, Spin } from 'antd';

import ajax from '../../api';

import HelpDoc from '../../components/helpDoc';
import { settingAction, workbenchAction } from '@/reducer/dataSync/actionType';
import { isRDB } from '@/utils';
import { DATA_SOURCE_ENUM, formItemLayout } from '@/constant';

const FormItem = Form.Item;
const { Option } = Select;

export const UnlimitedSpeed = '不限制传输速率';

class ChannelForm extends React.Component<any, any> {
	state: any = {
		isRecord: false,
		idFields: [], // 标识字段
		isTransTable: true,
		loading: false,
	};

	async componentDidMount() {
		// 开启断点续传功能，需要加载标识字段
		const {
			targetMap: { type: targetType, sourceId: dataSourceId },
			form: { setFieldsValue },
		} = this.props;

		if (this.props.setting.isRestore) {
			this.loadIdFields();
		}
		if (targetType?.type !== DATA_SOURCE_ENUM.INCEPTOR)
			return this.setState({ isTransTable: false });
		this.setState({
			loading: true,
		});
		const res = await ajax.getTableInfoByDataSource({
			dataSourceId,
			tableName: targetType.table,
			schema: targetType.schema,
		});
		this.setState({
			loading: false,
		});
		if (res) {
			const {
				data: { isTransTable },
			} = res;
			if (isTransTable) {
				setFieldsValue({ channel: 1 });
			}
			this.setState({
				isTransTable,
			});
		}
	}

	onLifeDayChange = (val: any) => {
		this.props.changeChannelSetting({
			lifeDay: val,
		});
	};

	loadIdFields = async () => {
		const { sourceMap } = this.props;
		const res = await ajax.getIncrementColumns({
			sourceId: sourceMap.sourceId,
			tableName: sourceMap.type.table,
			schema: sourceMap?.schema ? sourceMap?.schema : sourceMap.type.schema,
		});

		if (res.code === 1) {
			this.setState({
				idFields: res.data || [],
			});
		}
	};

	onEnableContinualTransfer = (e: any) => {
		if (e.target.checked) {
			this.loadIdFields();
		}
	};

	renderBreakpointContinualTransfer = () => {
		const { idFields } = this.state;
		const { form, setting, isIncrementMode, sourceMap, targetMap, isStandeAlone } = this.props;
		const { getFieldDecorator } = form;

		const sourceType = sourceMap?.type?.type;
		const targetType = targetMap?.type?.type;

		const idFieldInitialValue = isIncrementMode
			? sourceMap.increColumn
			: setting.restoreColumnName;

		return isRDB(sourceType) &&
			(isRDB(targetType) ||
				targetType === DATA_SOURCE_ENUM.HIVE1X ||
				targetType === DATA_SOURCE_ENUM.HIVE ||
				targetType === DATA_SOURCE_ENUM.MAXCOMPUTE) ? (
			<div>
				{!isStandeAlone && (
					<FormItem
						{...formItemLayout}
						label={
							<span>
								断点续传
								<HelpDoc doc="breakpointContinualTransferHelp" />
							</span>
						}
						className="txt-left"
					>
						{getFieldDecorator('isRestore', {
							rules: [],
							initialValue: setting.isRestore,
						})(
							<Checkbox
								onChange={this.onEnableContinualTransfer}
								checked={setting.isRestore}
							>
								{' '}
								开启{' '}
							</Checkbox>,
						)}
					</FormItem>
				)}
				{setting.isRestore ? (
					<FormItem {...formItemLayout} label="标识字段" key="restoreColumnName">
						{getFieldDecorator('restoreColumnName', {
							rules: [
								{
									required: true,
									message: '请选择标识字段',
								},
							],
							initialValue: idFieldInitialValue,
						})(
							<Select
								showSearch
								placeholder="请选择标识字段"
								disabled={isIncrementMode} // 增量模式时，默认使用增量字段，此处禁用选项
							>
								{idFields.map((o: any) => (
									<Option key={o.key} value={o.key}>
										{o.key}（{o.type}）
									</Option>
								))}
							</Select>,
						)}
					</FormItem>
				) : null}
			</div>
		) : null;
	};

	render() {
		const { isTransTable, loading } = this.state;
		const {
			setting,
			navtoStep,
			targetMap,
			isStandeAlone,
			sourceMap,
			form: { getFieldDecorator },
		} = this.props;
		const sourceType = sourceMap?.type?.type;
		const targetType = targetMap?.type?.type;
		const isClickHouse =
			targetType === DATA_SOURCE_ENUM.CLICKHOUSE ||
			targetType === DATA_SOURCE_ENUM.S3 ||
			sourceType === DATA_SOURCE_ENUM.PHOENIX ||
			isTransTable;

		const speedOption: any = [];
		const channelOption: any = [];
		const unLimitedOption: any[] = [
			{
				value: UnlimitedSpeed,
			},
		];

		for (let i = 1; i <= 20; i++) {
			speedOption.push(
				{
					value: i.toString(),
				},
			);
		}
		for (let i = 1; i <= 5; i++) {
			channelOption.push(
				{
					value: i.toString(),
					label: i,
				},
			);
		}

		return (
			<div className="g-step4">
				<Spin spinning={loading}>
					<Form>
						<FormItem
							{...formItemLayout}
							label={
								<span>
									作业速率上限
									<HelpDoc doc="jobSpeedLimit" />
								</span>
							}
							style={{ height: '32px' }}
						>
							{getFieldDecorator('speed', {
								rules: [
									{
										required: true,
									},
								],
								initialValue: `${
									setting.speed === -1 ? UnlimitedSpeed : setting.speed
								}`,
							})(
								<AutoComplete options={unLimitedOption.concat(speedOption)}>
									<Input suffix="MB/s" />
								</AutoComplete>,
							)}
						</FormItem>
						<FormItem
							{...formItemLayout}
							label={
								<span>
									作业并发数
									<HelpDoc
										doc={
											targetType === DATA_SOURCE_ENUM.S3
												? 'S3Concurrence'
												: isTransTable
												? 'transTableConcurrence'
												: 'jobConcurrence'
										}
									/>
								</span>
							}
							style={{ height: '32px' }}
						>
							{getFieldDecorator('channel', {
								rules: [
									{
										required: true,
									},
								],
								initialValue: `${!isClickHouse ? setting.channel : 1}`,
							})(
								<AutoComplete
									disabled={isClickHouse}
									options={channelOption}
									optionFilterProp="value"
								/>,
							)}
						</FormItem>
						{!isStandeAlone && (
							<>
								{/* <FormItem
									{...formItemLayout}
									label={
										<span>
											错误记录管理
											<HelpDoc doc="recordDirtyData" />
										</span>
									}
									className="txt-left"
								>
									{getFieldDecorator('isSaveDirty', {
										rules: [],
										initialValue: !!setting.isSaveDirty,
									})(<Checkbox>记录保存</Checkbox>)}
								</FormItem> */}
								<FormItem
									{...formItemLayout}
									label={
										<span>
											错误记录数超过
											<HelpDoc doc="errorCount" />
										</span>
									}
								>
									{getFieldDecorator('record', {
										rules: [],
										initialValue: setting.record,
									})(<InputNumber style={{ float: 'left' }} />)}
									<span style={{ float: 'left' }}>条, 任务自动结束</span>
								</FormItem>
								<FormItem
									{...formItemLayout}
									label={
										<span>
											错误记录比例配置
											<HelpDoc doc="errorPercentConfig" />
										</span>
									}
								>
									<span style={{ float: 'left' }}>
										任务执行结束后，统计错误记录占比，大于
									</span>
									{getFieldDecorator('percentage', {
										rules: [],
										initialValue: setting.percentage,
									})(<InputNumber style={{ float: 'left' }} />)}
									<span style={{ float: 'left' }}>%时，任务置为失败</span>
								</FormItem>
							</>
						)}
						{this.renderBreakpointContinualTransfer()}
					</Form>
					{!this.props.readonly && (
						<div className="steps-action">
							<Button style={{ marginRight: 8 }} onClick={() => this.prev(navtoStep)}>
								上一步
							</Button>
							<Button type="primary" onClick={() => this.next(navtoStep)}>
								下一步
							</Button>
						</div>
					)}
				</Spin>
			</div>
		);
	}

	prev(cb: any) {
		// eslint-disable-next-line
		cb.call(null, 2);
	}

	next(cb: any) {
		const { form, saveDataSyncToTab, dataSync, currentTabData } = this.props;
		form.validateFields((err: any, values: any) => {
			if (!err) {
				saveDataSyncToTab({
					id: currentTabData.id,
					data: dataSync,
				});
				// eslint-disable-next-line
				cb.call(null, 4);
			}
		});
	}
}

const ChannelFormWrap = Form.create({
	onValuesChange: function (props: any, values: any) {
		const { changeChannelSetting, setting, isIncrementMode, sourceMap } = props;
		if (setting.isSaveDirty && !setting.lifeDay) {
			values.lifeDay = 90;
		}
		if (!setting.isSaveDirty) {
			values.tableName = null;
		}
		if (values.isRestore === false) {
			values.restoreColumnName = undefined;
		}
		// 增量模式下，开启断点续传字段默认与增量字段保存一致
		if (isIncrementMode && values.isRestore === true) {
			values.restoreColumnName = sourceMap.increColumn;
		}
		values.isSaveDirty = values.isSaveDirty ? 0 : 1;

		console.log('isSaveDirty:', values.isSaveDirty);

		// Remove no use
		setting.syncModel = undefined;
		changeChannelSetting(values);
	},
})(ChannelForm);

class Channel extends React.Component<any, any> {
	render() {
		return (
			<div>
				<ChannelFormWrap {...this.props} />
			</div>
		);
	}
}

const mapState = (state: any) => {
	const { dataSync } = state.dataSync;
	const { setting, targetMap, sourceMap } = dataSync;

	return { setting, targetMap, sourceMap, dataSync };
};

const mapDispatch = (dispatch: any) => {
	return {
		changeChannelSetting(params: any) {
			dispatch({
				type: settingAction.CHANGE_CHANNEL_FIELDS,
				payload: params,
			});
			dispatch({
				type: workbenchAction.MAKE_TAB_DIRTY,
			});
		},
		saveDataSyncToTab: (params: any) => {
			dispatch({
				type: workbenchAction.SAVE_DATASYNC_TO_TAB,
				payload: {
					id: params.id,
					data: params.data,
				},
			});
		},
	};
};

export default connect(mapState, mapDispatch)(Channel);
