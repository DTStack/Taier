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

import React, { useContext, useEffect, useMemo, useState } from 'react';
import Context from '@/context';
import { Button, Input, Select, Form, Radio, Spin, Empty, Modal } from 'antd';
import molecule from '@dtinsight/molecule/esm';
import { Scrollable } from '@dtinsight/molecule/esm/components';
import FolderPicker from '../../components/folderPicker';
import {
	CATELOGUE_TYPE,
	DATA_SYNC_MODE,
	CREATE_MODEL_TYPE,
	FLINK_VERSIONS,
	FLINK_VERSION_TYPE,
	formItemLayout,
	tailFormItemLayout,
	TASK_TYPE_ENUM,
} from '@/constant';
import type { CatalogueDataProps } from '@/interface';
import { connect } from '@dtinsight/molecule/esm/react';
import { syncModeHelp, syncTaskHelp } from '../helpDoc/docs';
import api from '@/api';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const { Option } = Select;

interface OpenProps extends molecule.model.IEditor {
	onSubmit?: (values: IFormFieldProps) => Promise<boolean>;
	/**
	 * Only in editing
	 */
	record?: CatalogueDataProps;
}

export interface IFormFieldProps {
	name: string;
	taskType: TASK_TYPE_ENUM;
	nodePid: number;
	taskDesc: string;
	syncModel?: DATA_SYNC_MODE;
	createModel?: Valueof<typeof CREATE_MODEL_TYPE>;
	sqlText?: string;
	componentVersion: string;
}

export default connect(molecule.editor, ({ onSubmit, record, current }: OpenProps) => {
	const { supportJobTypes } = useContext(Context);
	const [form] = Form.useForm<IFormFieldProps>();
	const [loading, setLoading] = useState(false);
	const [pageLoading, setPageLoading] = useState(false);

	const getCurrentTaskInfo = () => {
		if (current?.tab) {
			const { data } = current.tab;
			// 数据同步任务和实时采集任务才有额外的配置需要请求
			const EXTRA_INFO_TASK = [TASK_TYPE_ENUM.SYNC, TASK_TYPE_ENUM.DATA_ACQUISITION];
			if (EXTRA_INFO_TASK.includes(data.taskType)) {
				setPageLoading(true);
				api.getOfflineTaskByID({ id: data.id })
					.then((res) => {
						if (res.code === 1) {
							form.setFieldsValue({
								createModel: res.data.createModel,
								syncModel: res.data.sourceMap?.syncModel,
								sqlText: res.data.sqlText,
							});
						}
					})
					.finally(() => {
						setPageLoading(false);
					});
			}
		}
	};

	const handleSubmit = (values: IFormFieldProps) => {
		setLoading(true);
		onSubmit?.({ ...values }).then((success) => {
			setLoading(success);
		});
	};

	const handleValuesChanged = (_: Partial<IFormFieldProps>, values: IFormFieldProps) => {
		if (current?.tab) {
			const { id } = current.tab;
			// Insert form values into tab for preventing losting the values when switch tabs
			molecule.editor.updateTab({
				id,
				data: values,
			});
		}
	};
	const confirmFlink = () => {
		Modal.confirm({
			title: '正在切换引擎版本',
			content: (
				<>
					<span style={{ color: 'red' }}>切换引擎版本后将重置环境参数</span>
					，请确认是否继续？
				</>
			),
			onCancel: () => {
				form.resetFields(['componentVersion']);
			},
		});
	};

	const checkSyncMode = async (_: any, value: DATA_SYNC_MODE) => {
		if (record && value === DATA_SYNC_MODE.INCREMENT) {
			// 当编辑同步任务，且改变同步模式为增量模式时，需要检测任务是否满足增量同步的条件
			const res = await api.checkSyncMode({ id: record.id });
			if (res.code === 1) {
				return res.data ? Promise.resolve() : Promise.reject();
			}

			return Promise.reject(new Error('当前同步任务不支持增量模式！'));
		}
		return Promise.resolve();
	};

	const renderConfig = (taskType: TASK_TYPE_ENUM) => {
		switch (taskType) {
			case TASK_TYPE_ENUM.SYNC: {
				return (
					<>
						<FormItem
							label="配置模式"
							name="createModel"
							tooltip={syncTaskHelp}
							rules={[
								{
									required: true,
									message: '请选择配置模式',
								},
							]}
							initialValue={CREATE_MODEL_TYPE.GUIDE}
						>
							<RadioGroup disabled={!!record}>
								<Radio value={CREATE_MODEL_TYPE.GUIDE}>向导模式</Radio>
								<Radio value={CREATE_MODEL_TYPE.SCRIPT}>脚本模式</Radio>
							</RadioGroup>
						</FormItem>
						<FormItem
							label="同步模式"
							name="syncModel"
							tooltip={syncModeHelp}
							rules={[
								{
									required: true,
									message: '请选择配置模式',
								},
								{
									validator: checkSyncMode,
								},
							]}
							initialValue={DATA_SYNC_MODE.NORMAL}
						>
							<RadioGroup>
								<Radio value={DATA_SYNC_MODE.NORMAL}>无增量标识</Radio>
								<Radio value={DATA_SYNC_MODE.INCREMENT}>有增量标识</Radio>
							</RadioGroup>
						</FormItem>
					</>
				);
			}
			case TASK_TYPE_ENUM.SQL: {
				return (
					<FormItem label="引擎版本" name="componentVersion">
						<Select onChange={confirmFlink}>
							{FLINK_VERSION_TYPE.map(({ value, label }) => (
								<Option key={value} value={value}>
									{label}
								</Option>
							))}
						</Select>
					</FormItem>
				);
			}
			case TASK_TYPE_ENUM.DATA_ACQUISITION: {
				return (
					<React.Fragment>
						<FormItem
							label="配置模式"
							name="createModel"
							tooltip={syncTaskHelp}
							rules={[
								{
									required: true,
									message: '请选择配置模式',
								},
							]}
							initialValue={CREATE_MODEL_TYPE.GUIDE}
						>
							<RadioGroup disabled={!!record}>
								<Radio value={CREATE_MODEL_TYPE.GUIDE}>向导模式</Radio>
								<Radio value={CREATE_MODEL_TYPE.SCRIPT}>脚本模式</Radio>
							</RadioGroup>
						</FormItem>
						<FormItem label="引擎版本" name="componentVersion">
							<Select onChange={confirmFlink}>
								{FLINK_VERSION_TYPE.map(({ value, label }) => (
									<Option key={value} value={value}>
										{label}
									</Option>
								))}
							</Select>
						</FormItem>
					</React.Fragment>
				);
			}
			default:
				break;
		}
	};

	useEffect(() => {
		getCurrentTaskInfo();
	}, []);

	const initialValues = useMemo(() => {
		if (current?.tab) {
			const { data } = current.tab;
			return {
				name: data.name,
				taskType: data.taskType,
				nodePid: data.nodePid?.toString().split('-')[0],
				taskDesc: data.taskDesc,
				componentVersion: data.componentVersion || FLINK_VERSIONS.FLINK_1_12,
			};
		}
		return undefined;
	}, []);

	return (
		<Scrollable>
			<Spin spinning={pageLoading}>
				<Form<IFormFieldProps>
					form={form}
					onFinish={handleSubmit}
					onValuesChange={handleValuesChanged}
					className="mo-open-task"
					initialValues={initialValues}
					autoComplete="off"
					{...formItemLayout}
				>
					<FormItem
						label="任务名称"
						name="name"
						rules={[
							{
								required: true,
								message: `任务名称不可为空！`,
							},
							{
								max: 128,
								message: `任务名称不得超过128个字符！`,
							},
							{
								pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/,
								message: `任务名称只能由字母、数字、中文、下划线组成!`,
							},
						]}
					>
						<Input />
					</FormItem>
					<FormItem
						label="任务类型"
						name="taskType"
						rules={[
							{
								required: true,
								message: `请选择任务类型`,
							},
						]}
					>
						<Select<string>
							disabled={!!record}
							notFoundContent={<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />}
							options={supportJobTypes.map((t) => ({ label: t.value, value: t.key }))}
						/>
					</FormItem>
					<FormItem noStyle dependencies={['taskType']}>
						{({ getFieldValue }) => renderConfig(getFieldValue('taskType'))}
					</FormItem>
					<FormItem
						label="存储位置"
						name="nodePid"
						rules={[
							{
								required: true,
								message: '存储位置必选！',
							},
						]}
						initialValue={molecule.folderTree.getState().folderTree?.data?.[0].id}
					>
						<FolderPicker showFile={false} dataType={CATELOGUE_TYPE.TASK} />
					</FormItem>
					<FormItem
						label="描述"
						hasFeedback
						name="taskDesc"
						rules={[
							{
								max: 200,
								message: '描述请控制在200个字符以内！',
							},
						]}
					>
						<Input.TextArea disabled={false} rows={4} />
					</FormItem>
					<FormItem name="sqlText" hidden />
					<FormItem {...tailFormItemLayout}>
						<Button type="primary" htmlType="submit" loading={loading}>
							确认
						</Button>
					</FormItem>
				</Form>
			</Spin>
		</Scrollable>
	);
});
