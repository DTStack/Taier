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
import { Table, Modal, message } from 'antd';

class RecommendTaskModal extends React.Component<any, any> {
	state: any = {
		selectedRows: [],
	};

	resetState() {
		this.setState({
			selectedRows: [],
		});
	}

	onOk() {
		const { selectedRows } = this.state;
		if (selectedRows.length == 0) {
			message.warning('请选择依赖');
			return;
		}
		this.props.onOk(selectedRows);
		this.resetState();
	}

	onCancel() {
		this.resetState();
		this.props.onCancel();
	}

	initColumns() {
		return [
			{
				title: '表名',
				dataIndex: 'tableName',
				width: '200px',
			},
			{
				title: '任务名称',
				dataIndex: 'name',
			},
		];
	}

	rowSelection() {
		const { existTask } = this.props;
		const { selectedRows } = this.state;
		return {
			selectedRowKeys: selectedRows?.map((item: any) => item.id),
			onChange: (selectedRowKeys: any, selectedRows: any) => {
				this.setState({
					selectedRows: selectedRows,
				});
			},
			getCheckboxProps: (record: any) => {
				const id = record.id;
				let isExist = false;
				existTask &&
					existTask.foreach((item: any) => {
						if (item.id == id) {
							isExist = true;
						}
					});
				if (isExist) {
					return { disabled: true };
				}
				return {};
			},
		};
	}

	render() {
		const { visible, taskList } = this.props;
		return (
			<Modal
				title="推荐上游依赖"
				maskClosable={false}
				visible={visible}
				onCancel={this.onCancel.bind(this)}
				onOk={this.onOk.bind(this)}
				okText="确定"
				cancelText="取消"
			>
				<p style={{ margin: '10px 10px' }}>提示：该分析仅基于您已发布过的任务进行分析</p>
				<Table
					className="dt-ant-table dt-ant-table--border select-all-table"
					columns={this.initColumns()}
					dataSource={taskList}
					pagination={false}
					rowSelection={this.rowSelection()}
					scroll={{ y: 400 }}
				/>
			</Modal>
		);
	}
}
export default RecommendTaskModal;
