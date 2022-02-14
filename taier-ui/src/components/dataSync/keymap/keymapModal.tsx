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

import * as React from 'react';
import { Form } from '@ant-design/compatible';
import { Modal, Select, Input, InputNumber } from 'antd';
import HelpDoc from '../../../components/helpDoc';
import {
	DATA_SOURCE_ENUM,
	formItemLayout,
	HDFS_FIELD_TYPES,
	HBASE_FIELD_TYPES,
} from '@/constant';

const FormItem = Form.Item;
const Option = Select.Option;

export const isValidFormatType = (type: any) => {
	if (!type) return false;
	const typeStr = type.toUpperCase();
	return (
		typeStr === 'STRING' || typeStr === 'VARCHAR' || typeStr === 'VARCHAR2'
	);
};

const renderHDFSOptions = () => {
	return HDFS_FIELD_TYPES.map((type: any) => (
		<Option key={type} value={type}>
			{type}
		</Option>
	));
};

const renderHbaseOptions = () => {
	return HBASE_FIELD_TYPES.map((type) => (
		<Option key={type} value={type}>
			{type}
		</Option>
	));
};

const getHBaseTypeItem = (getFieldDecorator: any, editField: any) => {
	return (
		<FormItem {...formItemLayout} label="选择类型" key="type">
			{getFieldDecorator('type', {
				rules: [
					{
						required: true,
					},
				],
				initialValue: (editField && editField.type) || 'STRING',
			})(
				<Select placeholder="请选择类型">
					{renderHbaseOptions()}
				</Select>,
			)}
		</FormItem>
	);
};

// 添加字段表单.
class KeyForm extends React.Component<any, any> {
	shouldComponentUpdate(nextProps: any) {
		if (this.props !== nextProps) {
			return true;
		}
		return false;
	}

	columnFamily = (data: any) => {
		return (
			data &&
			data.map((item: any) => (
				<Option key={item} value={item}>
					{item}
				</Option>
			))
		);
	};

	renderFormItems = () => {
		const { keyModal, dataType, sourceColumnFamily, targetColumnFamily } =
			this.props;

		const { isReader, editField } = keyModal;
		const { getFieldDecorator } = this.props.form;

		const initialKeyValue = editField
			? editField.key !== undefined
				? editField.key
				: editField.index !== undefined
				? editField.index
				: undefined
			: undefined;
		if (editField?.value) {
			// 常量额外处理
			return [
				<FormItem {...formItemLayout} label="名称" key="key">
					{getFieldDecorator('key', {
						rules: [
							{
								required: true,
								type: 'string',
							},
						],
						initialValue: (editField && editField.key) || '',
					})(<Input style={{ width: '100%' }} disabled />)}
				</FormItem>,
				<FormItem {...formItemLayout} label="值" key="value">
					{getFieldDecorator('value', {
						rules: [
							{
								required: true,
							},
						],
						initialValue:
							(editField && editField.value) || undefined,
					})(<Input style={{ width: '100%' }} disabled />)}
				</FormItem>,
				<FormItem {...formItemLayout} label="类型" key="type">
					{getFieldDecorator('type', {
						rules: [
							{
								required: true,
							},
						],
						initialValue:
							(editField && editField.type) || undefined,
					})(<Input style={{ width: '100%' }} disabled />)}
				</FormItem>,
			];
		}
		if (isReader) {
			// 数据源
			switch (dataType) {
				case DATA_SOURCE_ENUM.FTP:
				case DATA_SOURCE_ENUM.HDFS:
				case DATA_SOURCE_ENUM.S3: {
					return [
						<FormItem {...formItemLayout} label="索引值" key="key">
							{getFieldDecorator('key', {
								rules: [
									{
										required: true,
										type: 'integer',
										message: '请按要求填写索引值！',
									},
								],
								initialValue: initialKeyValue,
							})(
								<InputNumber
									placeholder="请输入索引值"
									style={{ width: '100%' }}
									min={0}
								/>,
							)}
						</FormItem>,
						<FormItem {...formItemLayout} label="类型" key="type">
							{getFieldDecorator('type', {
								rules: [
									{
										required: true,
									},
								],
								initialValue:
									(editField && editField.type) || 'STRING',
							})(
								<Select placeholder="请选择类型">
									{renderHDFSOptions()}
								</Select>,
							)}
						</FormItem>,
					];
				}
				case DATA_SOURCE_ENUM.HBASE: {
					const disabledEdit =
						editField && editField.key === 'rowkey';
					return [
						<FormItem {...formItemLayout} label="列名" key="key">
							{getFieldDecorator('key', {
								rules: [
									{
										required: true,
										type: 'string',
									},
								],
								initialValue:
									(editField && editField.key) || '',
							})(
								<Input
									placeholder="请输入列名"
									style={{ width: '100%' }}
									disabled={disabledEdit}
								/>,
							)}
						</FormItem>,
						<FormItem {...formItemLayout} label="列族" key="cf">
							{getFieldDecorator('cf', {
								rules: [
									{
										required: true,
									},
								],
								initialValue:
									(editField && editField.cf) || undefined,
							})(
								<Select
									placeholder="请选择列族"
									disabled={disabledEdit}
								>
									{this.columnFamily(sourceColumnFamily)}
								</Select>,
							)}
						</FormItem>,
						getHBaseTypeItem(getFieldDecorator, editField),
					];
				}
				default: {
					return [
						<FormItem {...formItemLayout} label="字段名" key="key">
							{getFieldDecorator('key', {
								rules: [
									{
										required: true,
										message: '请按要求填写字段名！',
									},
								],
								initialValue:
									(editField && editField.key) || '',
							})(
								<Input
									disabled={true}
									placeholder="请输入字段名"
									style={{ width: '100%' }}
								/>,
							)}
						</FormItem>,
						<FormItem {...formItemLayout} label="类型" key="type">
							{getFieldDecorator('type', {
								rules: [
									{
										required: true,
									},
								],
								initialValue:
									(editField && editField.type) || 'STRING',
							})(<Input disabled={true} />)}
						</FormItem>,
					];
				}
			}
		} else {
			// 目标表
			switch (dataType) {
				case DATA_SOURCE_ENUM.FTP:
				case DATA_SOURCE_ENUM.HDFS:
				case DATA_SOURCE_ENUM.S3: {
					return [
						<FormItem
							{...formItemLayout}
							label="字段名"
							key="keyName"
						>
							{getFieldDecorator('key', {
								rules: [
									{
										required: true,
									},
								],
								initialValue: initialKeyValue,
							})(<Input placeholder="请输入字段名" />)}
						</FormItem>,
						<FormItem
							{...formItemLayout}
							label="选择类型"
							key="type"
						>
							{getFieldDecorator('type', {
								rules: [
									{
										required: true,
									},
								],
								initialValue:
									(editField && editField.type) || 'STRING',
							})(
								<Select placeholder="请选择类型">
									{renderHDFSOptions()}
								</Select>,
							)}
						</FormItem>,
					];
				}
				case DATA_SOURCE_ENUM.HBASE: {
					return [
						<FormItem {...formItemLayout} label="列名" key="key">
							{getFieldDecorator('key', {
								rules: [
									{
										required: true,
										type: 'string',
									},
								],
								initialValue:
									(editField && editField.key) || undefined,
							})(
								<Input
									placeholder="请输入列名"
									style={{ width: '100%' }}
								/>,
							)}
						</FormItem>,
						<FormItem {...formItemLayout} label="列族" key="cf">
							{getFieldDecorator('cf', {
								rules: [
									{
										required: true,
									},
								],
								initialValue:
									(editField && editField.cf) || undefined,
							})(
								<Select placeholder="请选择列族">
									{this.columnFamily(targetColumnFamily)}
								</Select>,
							)}
						</FormItem>,
						getHBaseTypeItem(getFieldDecorator, editField),
					];
				}
				default:
					break;
			}
		}
		return [];
	};

	render() {
		const { keyModal } = this.props;
		const { getFieldDecorator } = this.props.form;

		const { editField, isReader } = keyModal;
		// 如果源数据类型为字符串，则支持字符串格式化
		const canFormat =
			editField && (isValidFormatType(editField.type) || editField.value);
		const text = editField?.value ? '格式' : '格式化';
		return (
			<Form>
				{this.renderFormItems()}
				{canFormat && isReader && (
					<FormItem {...formItemLayout} label={text} key="format">
						{getFieldDecorator('format', {
							rules: [],
							initialValue:
								(editField && editField.format) || undefined,
						})(<Input placeholder="格式化, 例如：yyyy-MM-dd" />)}
						<HelpDoc doc="stringColumnFormat" />
					</FormItem>
				)}
			</Form>
		);
	}
}

const KeyFormWrapper = Form.create<any>()(KeyForm);

class KeyMapModal extends React.Component<any, any> {
	submit = () => {
		const { onOk } = this.props;
		this.Form.validateFields((err: any, values: any) => {
			if (!err) {
				setTimeout(() => {
					this.Form.resetFields();
				}, 200);
				onOk(values);
			} else {
				onOk(null, err);
			}
		});
	};

	Form: any;
	cancel = () => {
		const { onCancel } = this.props;
		onCancel();
		this.Form.resetFields();
	};

	render() {
		const {
			title,
			visible,
			keyModal,
			dataType,
			sourceColumnFamily,
			targetColumnFamily,
		} = this.props;
		return (
			<Modal
				title={title}
				visible={visible}
				onOk={this.submit}
				onCancel={this.cancel}
			>
				<KeyFormWrapper
					sourceColumnFamily={sourceColumnFamily}
					targetColumnFamily={targetColumnFamily}
					dataType={dataType}
					keyModal={keyModal}
					// eslint-disable-next-line no-return-assign
					ref={(el: any) => (this.Form = el)}
				/>
			</Modal>
		);
	}
}

export default KeyMapModal;
