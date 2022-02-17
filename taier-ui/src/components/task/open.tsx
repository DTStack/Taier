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

import { useLayoutEffect, useState } from 'react';
import { Button, Input, Select, Form } from 'antd';
import molecule from '@dtinsight/molecule/esm';
import { Scrollable } from '@dtinsight/molecule/esm/components';
import FolderPicker from '../../components/folderPicker';
import { CATELOGUE_TYPE, formItemLayout, tailFormItemLayout, TASK_TYPE_ENUM } from '@/constant';
import type { CatalogueDataProps } from '@/interface';
import { connect } from '@dtinsight/molecule/esm/react';

const { Option } = Select;
const FormItem = Form.Item;

interface OpenProps extends molecule.model.IEditor {
	onSubmit?: (values: IFormFieldProps) => Promise<boolean>;
	/**
	 * Only in editing
	 */
	record?: CatalogueDataProps;
}

const TASK_TYPE_OPTIONS = [
	{
		value: TASK_TYPE_ENUM.SQL,
		text: 'SparkSQL',
	},
	{
		value: TASK_TYPE_ENUM.SYNC,
		text: '数据同步',
	},
];

interface IFormFieldProps {
	name: string;
	taskType: TASK_TYPE_ENUM;
	nodePid: number;
	taskDesc: string;
}

export default connect(molecule.editor, ({ onSubmit, record, current }: OpenProps) => {
	const [form] = Form.useForm<IFormFieldProps>();
	const [loading, setLoading] = useState(false);

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

	useLayoutEffect(() => {
		if (current?.tab) {
			const { data } = current.tab;
			form.setFieldsValue({
				name: data.name,
				taskType: data.taskType,
				nodePid: data.nodePid?.toString().split('-')[0],
				taskDesc: data.taskDesc,
			});
		}
	}, []);

	return (
		<Scrollable>
			<Form<IFormFieldProps>
				form={form}
				onFinish={handleSubmit}
				onValuesChange={handleValuesChanged}
				className="mo-open-task"
				autoComplete="off"
			>
				<FormItem
					{...formItemLayout}
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
					{...formItemLayout}
					label="任务类型"
					name="taskType"
					rules={[
						{
							required: true,
							message: `请选择任务类型`,
						},
					]}
				>
					<Select disabled={!!record}>
						{TASK_TYPE_OPTIONS.map((type) => (
							<Option key={type.value} value={type.value}>
								{type.text}
							</Option>
						))}
					</Select>
				</FormItem>
				<FormItem
					{...formItemLayout}
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
					{...formItemLayout}
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
				<FormItem {...tailFormItemLayout}>
					<Button type="primary" htmlType="submit" loading={loading}>
						确认
					</Button>
				</FormItem>
			</Form>
		</Scrollable>
	);
});
