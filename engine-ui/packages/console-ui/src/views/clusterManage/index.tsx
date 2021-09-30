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
import { hashHistory } from 'react-router';
import { Table, Button, message, Popconfirm, Pagination } from 'antd';
import moment from 'moment';
import AddEngineModal from '../../components/addEngineModal';
import Api from '../../api/console';
const PAGE_SIZE = 15;

class ClusterManage extends React.Component<any, any> {
	state: any = {
		dataSource: [],
		table: {
			pageIndex: 1,
			total: 0,
			loading: true,
		},
		newClusterModal: false,
		editModalKey: '',
	};
	componentDidMount() {
		this.getResourceList();
	}
	getResourceList() {
		const { table } = this.state;
		const { pageIndex } = table;
		Api.getClusterList({
			currentPage: pageIndex,
			pageSize: PAGE_SIZE,
		}).then((res: any) => {
			if (res.code == 1) {
				this.setState({
					dataSource: res.data.data,
					table: {
						...table,
						loading: false,
						total: res.data.totalCount,
					},
				});
			} else {
				this.setState({
					table: {
						...table,
						loading: false,
					},
				});
			}
		});
	}
	getPagination() {
		const { pageIndex, total } = this.state.table;
		return {
			current: pageIndex,
			pageSize: PAGE_SIZE,
			total: total,
		};
	}
	initTableColumns() {
		return [
			{
				title: '集群名称',
				dataIndex: 'clusterName',
			},
			{
				title: '修改时间',
				dataIndex: 'gmtModified',
				render(text: any) {
					return moment(text).format('YYYY-MM-DD HH:mm:ss');
				},
			},
			{
				title: '操作',
				dataIndex: 'deal',
				width: '170px',
				render: (text: any, record: any) => {
					return (
						<div>
							<a onClick={this.viewCluster.bind(this, record)}>查看</a>
							<span className="ant-divider"></span>
							<Popconfirm
								placement="topRight"
								title={`删除集群后不可恢复，确认删除集群 ${record.clusterName}?`}
								onConfirm={this.deleteCluster.bind(this, record)}
								okText="确认"
								cancelText="取消"
							>
								<a>删除</a>
							</Popconfirm>
						</div>
					);
				},
			},
		];
	}
	deleteCluster(item: any) {
		Api.deleteCluster({
			clusterId: item.clusterId,
		}).then((res: any) => {
			if (res.code === 1) {
				message.success('集群删除成功');
				this.getResourceList();
			}
		});
	}
	viewCluster(item: any) {
		hashHistory.push({
			pathname: '/console-ui/clusterManage/editCluster',
			state: {
				cluster: item,
				mode: 'view',
			},
		});
	}
	newCluster = () => {
		this.setState({
			editModalKey: Math.random(),
			newClusterModal: true,
		});
	};
	onCancel() {
		this.setState({ newClusterModal: false });
	}

	onSubmit(params: any) {
		Api.addCluster({ ...params }).then((res: any) => {
			if (res.code === 1) {
				this.onCancel();
				hashHistory.push({
					pathname: '/console-ui/clusterManage/editCluster',
					state: {
						mode: 'new',
						cluster: res.data,
					},
				});
				message.success('集群新增成功！');
			}
		});
	}
	handleTableChange = (pagination: any, filters: any, sorter: any) => {
		const queryParams = Object.assign(this.state.table, { loading: true });
		this.setState(
			{
				table: queryParams,
			},
			this.getResourceList,
		);
	};

	onPageChange = (current: any) => {
		this.setState(
			{
				table: Object.assign(this.state.table, { pageIndex: current, loading: true }),
			},
			this.getResourceList,
		);
	};

	render() {
		const { dataSource, table, newClusterModal, editModalKey } = this.state;
		const { loading } = table;
		const columns = this.initTableColumns();

		const pagination: any = {
			total: table.total,
			current: table.pageIndex,
			pageSize: PAGE_SIZE,
			size: 'small',
			showTotal: (total) => (
				<span>
					共<span style={{ color: '#3F87FF' }}>{total}</span>条数据，每页显示{PAGE_SIZE}条
				</span>
			),
			onChange: this.onPageChange,
		};

		return (
			<React.Fragment>
				<div className="c-clusterManage__title">
					<span className="c-clusterManage__title__span">多集群管理</span>
					<Button
						className="c-clusterManage__title__btn"
						type="primary"
						onClick={this.newCluster}
					>
						新增集群
					</Button>
				</div>
				<div className="contentBox">
					<Table
						rowKey={(record: any, index: any) => {
							return `clusterManage-${record.id}`;
						}}
						className="dt-table-fixed-contain-footer"
						scroll={{ y: true }}
						style={{ height: 'calc(100vh - 154px)' }}
						pagination={false}
						loading={loading}
						dataSource={dataSource}
						columns={columns}
						onChange={this.handleTableChange}
						footer={() => {
							return <Pagination {...pagination} />;
						}}
					/>
				</div>
				<AddEngineModal
					key={editModalKey}
					title="新增集群"
					visible={newClusterModal}
					onCancel={this.onCancel.bind(this)}
					onOk={this.onSubmit.bind(this)}
				/>
			</React.Fragment>
		);
	}
}

export default ClusterManage;
