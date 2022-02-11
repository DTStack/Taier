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

import React, { useState, useLayoutEffect } from 'react';
import { Modal, Select, Input, message, Form } from 'antd';
import { PlusCircleOutlined, DeleteOutlined } from '@ant-design/icons';
import type { TASK_TYPE_ENUM } from '@/constant';
import { MEMORY_ITEMS } from '@/constant';
import Api from '../../api/console';
import { formItemLayout, specFormItemLayout } from '@/constant';
import type { IClusterProps } from '../bindCommModal';
import './index.scss';

const { Option } = Select;
const FormItem = Form.Item;
const { confirm } = Modal;

interface IDynamicFormProps {
	lineList: Record<string, string>;
	taskTypeName: string;
	type: number;
	initialList?: Record<string, string>;
	deleteItem: (type: number) => void;
}

const DynamicForm = ({
	lineList,
	taskTypeName,
	deleteItem,
	type,
	initialList,
}: IDynamicFormProps) => {
	const lineTitle = Object.keys(lineList);

	return (
		<>
			<header className="c-header-dynamicform">
				<span>{`${taskTypeName}任务`}</span>
				<span style={{ cursor: 'pointer' }} onClick={() => deleteItem(type)}>
					<DeleteOutlined />
				</span>
			</header>
			<div style={{ paddingBottom: 10 }}>
				{lineTitle.map((item) => (
					<FormItem
						key={item}
						label={item}
						{...specFormItemLayout}
						name={`${type} ${item.replace(/(\.)/g, '-')}`}
						initialValue={initialList?.[item] || undefined}
					>
						<Input
							placeholder={lineList[item]}
							addonAfter={MEMORY_ITEMS.includes(item) ? 'm' : ''}
						/>
					</FormItem>
				))}
			</div>
		</>
	);
};

interface IResourceManageModalProps {
	title?: React.ReactNode;
	visible: boolean;
	isBindTenant?: boolean;
	/**
	 * 资源队列默认值
	 */
	queueId?: number;
	clusterId: number;
	tenantId?: number;
	clusterList: IClusterProps[];
	queueList?: { queueName: string }[];
	onCancel?: () => void;
	onOk?: () => void;
}

/**
 * 任务模版类型
 */
interface IDataListProps {
	taskType: TASK_TYPE_ENUM;
	taskTypeName: string;
	params: Record<string, string>;
}

export default ({
	title,
	visible,
	queueId,
	tenantId,
	isBindTenant = false,
	queueList = [],
	onCancel,
	onOk,
}: IResourceManageModalProps) => {
	const [form] = Form.useForm();
	const [isLoading, setLoading] = useState(false);
	const [dataList, setDataList] = useState<IDataListProps[]>([]);
	const [typeList, setTypeList] = useState<number[]>([]);
	const [initialList, setInitialList] = useState<Record<number, Record<string, string>>>({});

	const getServiceParam = () => {
		form.validateFields().then((values) => {
			const taskTypeResourceJson = JSON.stringify(
				typeList
					.map((item) => {
						return dataList.map((task) => {
							if (item === task.taskType) {
								const blockList = Object.keys(task.params);
								const params: Record<string, any> = {
									taskType: item,
									resourceParams: {},
								};
								blockList.forEach((head) => {
									params.resourceParams[head] =
										values?.[`${item} ${head}`.replace(/(\.)/g, '-')];
								});
								return params;
							}
							return undefined;
						});
					})
					.map((arrayItem) => {
						return arrayItem.filter((element) => element !== undefined)[0];
					}),
			);
			setLoading(true);
			Api.switchQueue({
				queueId: values?.queueId,
				tenantId,
				taskTypeResourceJson,
			})
				.then((res) => {
					if (res.code === 1) {
						message.success('提交成功');
						onOk?.();
					}
					message.error('提交失败');
				})
				.finally(() => {
					setLoading(false);
				});
		});
	};

	const handleCancel = () => {
		confirm({
			title: '是否保存配置？',
			content: '若不保存编辑内容，再次打开时会进行重置',
			okText: '保存',
			cancelText: '返回',
			onOk() {
				getServiceParam();
			},
			onCancel() {
				onCancel?.();
			},
		});
	};

	const addTaskType = () => {
		const current = form.getFieldValue('reMEMORY_ITEMS');
		if (typeof current === 'undefined' || Number.isNaN(current)) {
			message.warning('请先选择任务类型');
			return;
		}
		if (typeList.includes(current)) {
			message.warning('该任务的资源限制已存在！');
			return;
		}
		const nextTypeList = typeList.concat();
		nextTypeList.push(current);
		setTypeList(nextTypeList);
	};

	const removeType = (type: number) => {
		if (typeof type !== 'undefined') {
			const deleteIndex = typeList.indexOf(type);
			if (deleteIndex !== -1) {
				setTypeList((tpList) => tpList.filter((numType) => numType !== type));
			}
		}
	};

	// 切换集群
	useLayoutEffect(() => {
		if (visible) {
			Api.queryTaskResourceLimits({ dtUicTenantId: tenantId }).then((res) => {
				const { code, data } = res;
				if (code === 1) {
					const union: number[] = [];
					const biginitial: Record<number, Record<string, string>> = {};
					if (Array.isArray(data)) {
						data.forEach((item) => {
							union.push(item.taskType);
							biginitial[item.taskType] = item.resourceLimit;
						});
					}
					setInitialList(biginitial);
					setTypeList(union);
				}
			});
			Api.getTaskResourceTemplate({}).then((res) => {
				if (res.code === 1) {
					setDataList(res.data);
				}
			});
		} else {
			setTypeList([]);
			setDataList([]);
		}
	}, [visible]);

	return (
		<Modal
			title={title}
			visible={visible}
			onOk={() => getServiceParam()}
			onCancel={handleCancel}
			destroyOnClose
			width="600px"
			confirmLoading={isLoading}
			className={isBindTenant ? 'no-padding-modal' : ''}
		>
			<Form form={form} preserve={false}>
				<FormItem
					label="资源队列"
					tooltip="指Yarn上分配的资源队列，若下拉列表中无全部队列，请前往“多集群管理”页面的具体集群中刷新集群"
					{...formItemLayout}
					name="queueId"
					rules={[
						{
							required: true,
							message: '租户不可为空！',
						},
					]}
					initialValue={queueId || undefined}
				>
					<Select allowClear placeholder="请选择资源队列">
						{queueList.map((item) => {
							return (
								<Option key={item.queueName} value={item.queueName}>
									{item.queueName}
								</Option>
							);
						})}
					</Select>
				</FormItem>
				<FormItem label="资源限制" {...formItemLayout}>
					<FormItem
						noStyle
						tooltip="设置租户下单个离线任务在临时运行和周期运行时能使用的最大资源数，任务的环境参数设置超出此限制将导致任务提交或运行失败。保存变更后立即生效。"
						name="reMEMORY_ITEMS"
					>
						<Select allowClear placeholder="请选择任务类型">
							{dataList.map((item) => {
								return (
									<Option key={item.taskType} value={item.taskType}>
										{item.taskTypeName}
									</Option>
								);
							})}
						</Select>
					</FormItem>
					<div className="o-div--actionDom" onClick={addTaskType}>
						<PlusCircleOutlined className="o-icon--actionDom" />
						添加资源限制
					</div>
				</FormItem>
				{typeList.map((item) => {
					return dataList.map((type, key) => {
						return (
							<div key={key} className="o-block--dynamic">
								{type.taskType === item ? (
									<DynamicForm
										type={item}
										lineList={type.params}
										taskTypeName={type.taskTypeName}
										deleteItem={removeType}
										initialList={initialList[item]}
									/>
								) : null}
							</div>
						);
					});
				})}
			</Form>
		</Modal>
	);
};
