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

import { Row, Col, Table, Form } from 'antd';
import { PlusCircleOutlined } from '@ant-design/icons';
import { openTaskInTab } from '@/extensions/folderTree';
import { TASK_TYPE_ENUM } from '@/constant';
import { useMemo, useState } from 'react';
import type { IOfflineTaskProps, ITaskVOProps } from '@/interface';
import type { ColumnsType } from 'antd/lib/table';
import { getCookie } from '@/utils';
import UpstreamDependentTasks from './upstreamDependentTasks';

interface ITaskDependenceProps {
	tabData: IOfflineTaskProps;
	handleDelVOS?: (record: ITaskVOProps) => void;
	handleAddVOS?: (record: ITaskVOProps) => void;
}

export default function TaskDependence({
	tabData,
	handleDelVOS,
	handleAddVOS,
}: ITaskDependenceProps) {
	const [form] = Form.useForm();
	const [modalVisible, setModalVisible] = useState(false);
	const [currentTenantName] = useState(getCookie('tenant_name'));

	const goEdit = (task: ITaskVOProps) => {
		openTaskInTab(task.id);
	};

	const getSpanBottom = () => {
		if (Array.isArray(tabData.taskVOS) && tabData.taskVOS.length > 5) {
			return 20;
		}
		return tabData?.taskVOS?.length ? 0 : -30;
	};

	const dependencyModalShow = () => {
		setModalVisible((v) => !v);
	};

	const submitData = (task: ITaskVOProps) => {
		const data: ITaskVOProps = {
			...task,
			tenantId: Number(getCookie('tenantId')),
			id: task.taskId,
		};
		dependencyModalShow();
		form.resetFields();
		handleAddVOS?.(data);
	};

	const isSql = tabData.taskType === TASK_TYPE_ENUM.SQL;
	const columns: ColumnsType<ITaskVOProps> = useMemo(
		() => [
			{
				title: '租户',
				dataIndex: 'tenantName',
				key: 'tenantName',
				width: 150,
				render: (tenantName) => {
					return tenantName || currentTenantName;
				},
			},
			{
				title: '任务',
				dataIndex: 'name',
				key: 'name',
				render: (text, record) => {
					if (record.tenantName === currentTenantName) {
						return <a onClick={() => goEdit(record)}>{text}</a>;
					}

					return text;
				},
			},
			{
				title: '操作',
				key: 'action',
				width: 100,
				fixed: 'right',
				render: (_, record) => (
					<span>
						<a
							onClick={() => {
								handleDelVOS?.(record);
							}}
						>
							删除
						</a>
					</span>
				),
			},
		],
		[],
	);

	return (
		<>
			<Row justify={isSql ? 'space-around' : 'start'} style={{ height: 38 }}>
				<Col span={12} style={{ fontSize: 12, lineHeight: '31px', fontWeight: 600 }}>
					上游依赖任务列表
				</Col>
			</Row>
			<Row>
				<Col span={24}>
					<Table
						pagination={
							Array.isArray(tabData.taskVOS) && tabData.taskVOS.length > 5
								? { pageSize: 5, total: tabData.taskVOS.length }
								: false
						}
						className="dt-ant-table dt-ant-table--border"
						style={{
							marginBottom:
								Array.isArray(tabData.taskVOS) && tabData.taskVOS.length > 5
									? 0
									: 20,
							minHeight: 50,
						}}
						columns={columns}
						bordered={false}
						scroll={{ x: 890 }}
						dataSource={tabData.taskVOS || []}
						rowKey="id"
					/>
				</Col>
				<Col span={24}>
					<span
						style={{
							marginBottom: getSpanBottom(),
							color: '#2491F7',
							cursor: 'pointer',
							fontSize: 12,
						}}
						onClick={dependencyModalShow}
					>
						<PlusCircleOutlined style={{ display: 'inline', paddingRight: '4px' }} />
						添加依赖
					</span>
				</Col>
			</Row>
			<UpstreamDependentTasks
				form={form}
				visible={modalVisible}
				onCancel={dependencyModalShow}
				submitData={submitData}
			/>
		</>
	);
}
