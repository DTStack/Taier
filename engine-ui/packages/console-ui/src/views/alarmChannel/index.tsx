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

import React, { useState, useEffect } from 'react';
import { Table, Button, Form, Pagination, Popconfirm, message, Modal } from 'antd';
import Api from '../../api/console';
import { ALARM_TYPE_TEXT, ALARM_TYPE } from '../../consts';
interface PaginationTypes {
	currentPage: number;
	pageSize: number;
	total?: number;
}
const { confirm } = Modal;

const AlarmChannel: React.FC = (props: any) => {
	const [pagination, setPagination] = useState<PaginationTypes>({
		currentPage: 1,
		total: 0,
		pageSize: 15,
	});
	const [params, setParams] = useState<{
		alertGateType: any[];
		reFreshKey: number | '';
	}>({ alertGateType: [], reFreshKey: '' });
	const useAlarmList = (query, pagination) => {
		const [loading, setLoading] = useState<boolean>(false);
		const [alarmList, setAlarmList] = useState<any[]>([]);
		const { currentPage, pageSize } = pagination;
		const { alertGateType, reFreshKey } = params;
		useEffect(() => {
			const getAlarmRuleList = async () => {
				setLoading(true);
				let res = await Api.getAlarmRuleList({
					currentPage: currentPage,
					pageSize: pageSize,
					alertGateType,
				});
				if (res && res.code == 1) {
					setAlarmList(res.data?.data || []);
					setPagination((state) => ({ ...state, total: res.data.totalCount }));
				}
				setLoading(false);
			};
			getAlarmRuleList().then();
		}, [currentPage, alertGateType, pageSize, reFreshKey]);
		return [{ loading, alarmList }];
	};
	const refreshTable = () => {
		setParams((state) => ({ ...state, reFreshKey: Math.random() }));
	};
	const deleteRule = async (id: number) => {
		let res = await Api.deleteAlarmRule({ id });
		if (res.code === 1) {
			message.success('删除成功！');
			refreshTable();
		}
	};
	const editAlarm = async (id: number) => {
		let res = await Api.getByAlertId({ id });
		if (res.code === 1) {
			props.router.push({
				pathname: '/console-ui/alarmChannel/alarmRule',
				state: {
					id,
					ruleData: res.data || {},
				},
			});
		}
	};
	const initColumns = () => {
		return [
			{
				title: '通道名称',
				dataIndex: 'alertGateName',
				render: (alertGateName: string, record: any) => {
					const showText = `${ALARM_TYPE_TEXT[record.alertGateType].slice(0, 2)}默认通道`;
					return (
						<span className="alarm-name-wrap">
							<span className="alarm-name">{alertGateName}</span>
							{record.isDefault ? (
								<Button className="alarm-btn" disabled>
									{showText}
								</Button>
							) : null}
						</span>
					);
				},
			},
			{
				title: '通道类型',
				dataIndex: 'alertGateType',
				filters: Object.entries(ALARM_TYPE_TEXT).map(([key, value]) => {
					return {
						text: value,
						value: key,
					};
				}),
				render: (text: number) => {
					return ALARM_TYPE_TEXT[text];
				},
			},
			{
				title: '通道标识',
				dataIndex: 'alertGateSource',
			},
			{
				title: '操作',
				dataIndex: 'opera',
				render: (text: any, record) => {
					const { alertId } = record;
					const showText = `${ALARM_TYPE_TEXT[record.alertGateType].slice(0, 2)}默认通道`;
					return (
						<span>
							<a
								onClick={() => {
									editAlarm(alertId);
								}}
							>
								编辑
							</a>
							<span className="ant-divider"></span>
							<Popconfirm
								title="确认删除该告警通道？"
								okText="确定"
								cancelText="取消"
								onConfirm={() => {
									deleteRule(alertId);
								}}
							>
								<a>删除</a>
							</Popconfirm>
							{!record.isDefault && record.alertGateType !== ALARM_TYPE.CUSTOM && (
								<>
									<span className="ant-divider"></span>
									<a
										onClick={() => {
											setDefaultChannel(record);
										}}
									>{`设为${showText}`}</a>
								</>
							)}
						</span>
					);
				},
			},
		];
	};
	const setDefaultChannel = (record) => {
		const { alertId, alertGateType, alertGateName } = record;
		const showText = `${ALARM_TYPE_TEXT[alertGateType].slice(0, 2)}默认通道`;
		confirm({
			title: `确定将“${alertGateName}(通道名称)” 设为${showText}吗`,
			content: '设置为默认告警通道后，各应用的告警信息将走此通道',
			onOk() {
				Api.setDefaultAlert({ alertId, alertGateType }).then((res) => {
					if (res.code === 1) {
						message.success('操作成功');
						refreshTable();
					}
				});
			},
			onCancel() {
				console.log('Cancel');
			},
		});
	};
	const handleTableChange = (paginations: any, filters: any, sorter: any) => {
		setParams((state) => ({ ...state, alertGateType: filters.alertGateType || [] }));
	};

	const onPageChange = (current: number) => {
		setPagination((state) => ({ ...state, currentPage: current || 1 }));
	};

	const [{ loading, alarmList }] = useAlarmList(params, pagination);
	return (
		<div className="alarm__wrapper">
			<Form layout="inline">
				<Form.Item>
					<Button
						className="alarm-btn"
						type="primary"
						onClick={() => {
							props.router.push({ pathname: '/console-ui/alarmChannel/alarmConfig' });
						}}
					>
						SFTP配置
					</Button>
					<Button
						className="alarm-btn"
						type="primary"
						onClick={() => {
							props.router.push({
								pathname: '/console-ui/alarmChannel/alarmRule',
								query: {
									isCreate: true,
								},
							});
						}}
					>
						新增告警通道
					</Button>
				</Form.Item>
			</Form>
			<Table
				className="dt-table-fixed-contain-footer"
				scroll={{ y: true }}
				style={{ height: 'calc(100vh - 154px)' }}
				loading={loading}
				columns={initColumns()}
				dataSource={alarmList}
				pagination={false}
				onChange={handleTableChange}
				footer={() => {
					return (
						<Pagination
							{...{
								current: pagination.currentPage,
								pageSize: pagination.pageSize,
								size: 'small',
								total: pagination.total,
								onChange: onPageChange,
								showTotal: (total) => (
									<span>
										共<span style={{ color: '#3F87FF' }}>{total}</span>
										条数据，每页显示{pagination.pageSize}条
									</span>
								),
							}}
						/>
					);
				}}
			/>
		</div>
	);
};
export default AlarmChannel;
