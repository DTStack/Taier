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

import React, { useEffect, useState } from 'react';
import type { FormInstance } from 'antd';
import { Modal, Button, Input, message, Select, Form } from 'antd';
import { ExclamationCircleOutlined } from '@ant-design/icons';
import FolderPicker from '../../components/folderPicker';
import { getContainer } from '../resourceManager/resModal';
import { CATELOGUE_TYPE, formItemLayout, TASK_TYPE_ENUM } from '@/constant';
import type { CatalogueDataProps, IFunctionProps } from '@/interface';
import resourceManagerTree from '@/services/resourceManagerService';
import './fnModal.scss';

const { Option } = Select;
const FormItem = Form.Item;

interface IFnFormProps {
	form: FormInstance;
	flags?: string[] | null;
	fnType: IFnModalProps['fnType'];
	defaultData: IFnModalProps['defaultData'];
	isCreateFromMenu: boolean;
	isCreateNormal: boolean;
	functionTreeData: IFnModalProps['functionTreeData'];
	resTreeData: any;
}

function FnForm({
	form,
	flags,
	defaultData,
	isCreateFromMenu,
	isCreateNormal,
	fnType,
	functionTreeData,
	resTreeData,
}: IFnFormProps) {
	const formData = defaultData?.formData?.data;
	const getTaskTypeDefaultValue = () => {
		if (formData?.taskType) {
			return formData?.taskType;
		}
		return Array.isArray(flags) && flags.indexOf('Hadoop') !== -1
			? TASK_TYPE_ENUM.SQL
			: undefined;
	};

	const getNodePidDefaultValue = () => {
		if (formData) return formData.nodePid;
		if (isCreateNormal) return functionTreeData?.id;
		if (isCreateFromMenu) return defaultData?.parentId;
		return undefined;
	};

	const handleResSelectTreeChange = (value: number) => {
		form.setFieldsValue({ resourceId: value });
		form.validateFields(['resourceId']);
	};

	const handleSelectTreeChange = (value: number) => {
		form.setFieldsValue({ nodePid: value });
	};

	const checkNotDir = (_: any, value: number) => {
		let nodeType: any;

		const loop = (arr: any) => {
			arr.forEach((node: any) => {
				if (node.id === value) {
					nodeType = node.type;
				} else {
					loop(node.children || []);
				}
			});
		};

		loop([resTreeData]);

		if (nodeType === 'folder') {
			return Promise.reject(new Error('请选择具体文件, 而非文件夹'));
		}

		return Promise.resolve();
	};

	return (
		<>
			<Form
				form={form}
				autoComplete='off'
				initialValues={{
					taskType: getTaskTypeDefaultValue(),
				}}
				preserve={false}
			>
				<FormItem
					{...formItemLayout}
					label="函数类型"
					name="taskType"
					rules={[
						{
							required: true,
							message: '函数类型不可为空！',
						},
					]}
				>
					<Select
						disabled={!!fnType}
						getPopupContainer={() => document.getElementById('molecule')!}
					>
						{Array.isArray(flags) && flags.indexOf('Hadoop') !== -1 && (
							<Option value={TASK_TYPE_ENUM.SQL}>Spark SQL</Option>
						)}
					</Select>
				</FormItem>
				<>
					<FormItem
						{...formItemLayout}
						label="函数名称"
						name="name"
						rules={[
							{
								required: true,
								message: '函数名称不可为空！',
							},
							{
								pattern: /^[a-z0-9_]+$/,
								message: '函数名称只能由小写字母、数字、下划线组成!',
							},
							{
								max: 20,
								message: '函数名称不得超过20个字符！',
							},
						]}
						initialValue={formData ? formData.name : undefined}
					>
						<Input placeholder="请输入函数名称" disabled={!!formData} />
					</FormItem>
					<FormItem
						{...formItemLayout}
						label="类名"
						name="className"
						rules={[
							{
								required: true,
								message: '类名不能为空',
							},
							{
								pattern: /^[a-zA-Z]+[0-9a-zA-Z_]*(\.[a-zA-Z]+[0-9a-zA-Z_]*)*$/,
								message: '请输入有效的类名!',
							},
						]}
						initialValue={formData ? formData.className : undefined}
					>
						<Input placeholder="请输入类名" />
					</FormItem>
					<FormItem {...formItemLayout} label="资源" required>
						<FormItem
							noStyle
							name="resourceId"
							rules={[
								{
									required: true,
									message: '请选择关联资源',
								},
								{
									validator: checkNotDir,
								},
							]}
							initialValue={formData ? formData.resources : undefined}
						>
							<Input type="hidden" />
						</FormItem>
						<FolderPicker
							dataType={CATELOGUE_TYPE.RESOURCE}
							showFile
							defaultValue={formData ? formData.resources : undefined}
							onChange={handleResSelectTreeChange}
						/>
					</FormItem>
					<FormItem
						{...formItemLayout}
						label="用途"
						name="purpose"
						initialValue={formData ? formData.purpose : undefined}
					>
						<Input placeholder="" />
					</FormItem>
					<FormItem
						{...formItemLayout}
						label="命令格式"
						name="commandFormate"
						rules={[
							{
								required: true,
								message: '请输入命令格式',
							},
							{
								max: 128,
								message: '描述请控制在128个字符以内！',
							},
						]}
						initialValue={formData ? formData.commandFormate : undefined}
					>
						<Input placeholder="" />
					</FormItem>
					<FormItem
						{...formItemLayout}
						label="参数说明"
						name="paramDesc"
						rules={[
							{
								max: 200,
								message: '描述请控制在200个字符以内！',
							},
						]}
						initialValue={formData?.paramDesc ? formData?.paramDesc : undefined}
					>
						<Input.TextArea rows={4} placeholder="请输入函数的参数说明" />
					</FormItem>
					<FormItem {...formItemLayout} label="选择存储位置" required>
						<FormItem
							noStyle
							name="nodePid"
							rules={[
								{
									required: true,
									message: '存储位置必选！',
								},
							]}
							initialValue={getNodePidDefaultValue()}
						>
							<Input type="hidden" />
						</FormItem>
						<FolderPicker
							showFile={false}
							dataType={CATELOGUE_TYPE.FUNCTION}
							onChange={handleSelectTreeChange}
							defaultValue={getNodePidDefaultValue()}
						/>
					</FormItem>
				</>
			</Form>
		</>
	);
}

interface IFnModalProps {
	fnType?: string;
	isModalShow: boolean;
	/**
	 * 函数目录树的根节点
	 */
	functionTreeData: CatalogueDataProps;
	greenPlumFuncTreeData?: any;
	engine: { name: string; value: number }[];
	/**
	 * 如果不存在该数据，则说明是根目录新建
	 * 如果存在该数据，且存在 id，name 则为编辑
	 * 如果存在该数据，但不存在 id 而存在 parentId 则表示从某一个目录新建
	 */
	defaultData:
		| Partial<
				Pick<CatalogueDataProps, 'id' | 'parentId' | 'name'> & {
					formData: { data: IFunctionProps };
				}
		  >
		| undefined;
	toggleCreateFn: () => void;
	addFn: (values: any) => Promise<boolean>;
	editFn: (values: any) => Promise<boolean>;
}

let dtcount = 0;
export default function FnModal({
	fnType,
	isModalShow,
	functionTreeData,
	defaultData,
	engine,
	toggleCreateFn,
	addFn,
	editFn,
}: IFnModalProps) {
	const [form] = Form.useForm();
	const [flags, setFlags] = useState<string[]>([]);

	const handleCancel = () => {
		closeModal();
	};

	const closeModal = () => {
		dtcount += 1;
		toggleCreateFn();
		setFlags([]);
	};

	const handleSubmit = () => {
		form.validateFields().then((values) => {
			const formData = defaultData?.formData?.data;
			const id = formData?.id;
			if (id) {
				editFn(
					Object.assign(values, {
						id,
					}),
				).then((res) => {
					if (res) {
						message.success('编辑成功');
						closeModal();
						form.resetFields();
					}
				});
			} else {
				addFn(values).then((res) => {
					if (res) {
						message.success('创建成功');
						closeModal();
						form.resetFields();
					}
				});
			}
		});
	};

	useEffect(() => {
		const engineArray: string[] = [];
		if (flags.length === 0 && isModalShow === true) {
			engine.forEach((item) => {
				engineArray.push(item.name);
			});
			setFlags(engineArray);
		}
	}, [isModalShow, engine]);

	const isCreateNormal = typeof defaultData === 'undefined';
	const isCreateFromMenu = !isCreateNormal && typeof defaultData.id === 'undefined';
	const flag = defaultData?.formData?.data?.name;
	const title = flag ? '修改自定义函数' : '新建自定义函数';

	const resTreeData: CatalogueDataProps | undefined =
		resourceManagerTree.getState().folderTree?.data?.[0]?.data;

	return (
		<div id="JS_func_modal">
			<Modal
				title={title}
				visible={isModalShow}
				footer={[
					<Button key="back" size="large" onClick={handleCancel}>
						取消
					</Button>,
					<Button key="submit" type="primary" size="large" onClick={handleSubmit}>
						确认
					</Button>,
				]}
				destroyOnClose
				key={dtcount}
				onCancel={handleCancel}
				getContainer={() => getContainer('JS_func_modal')}
			>
				{flag && (
					<div className="task_offline_message">
						<ExclamationCircleOutlined style={{ marginRight: 7 }} />
						替换资源时，如果资源的新文件与现有文件名称保持一致，那么替换后关联函数对应任务可立即生效，否则关联函数对应任务需重新提交才可生效。
					</div>
				)}
				<FnForm
					form={form}
					functionTreeData={functionTreeData}
					resTreeData={resTreeData}
					fnType={fnType}
					flags={flags}
					isCreateFromMenu={isCreateFromMenu}
					isCreateNormal={isCreateNormal}
					defaultData={defaultData}
				/>
			</Modal>
		</div>
	);
}
