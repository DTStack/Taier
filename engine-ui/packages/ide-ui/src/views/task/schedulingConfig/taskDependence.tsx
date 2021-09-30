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
import { debounce, cloneDeep, isArray } from 'lodash';
import { Row, Col, Form, Button, Table, Select, message } from 'antd';

import RecommendTaskModal from './recommentTaskModal';
import { TASK_TYPE } from '../../../comm/const';
import ajax from '../../../api';
import { openTaskInTab } from '../../common/folderTree';

const FormItem = Form.Item;
const Option = Select.Option;

const formItemLayout: any = {
	// 表单正常布局
	labelCol: {
		xs: { span: 24 },
		sm: { span: 4 },
	},
	wrapperCol: {
		xs: { span: 24 },
		sm: { span: 18 },
	},
};

class TaskDependence extends React.Component<any, any> {
	state: any = {
		loading: false,
		recommendTaskModalVisible: false,
		recommendTaskList: [],
		taskListSearch: [],
	};

	showRecommendTask() {
		const { tabData } = this.props;
		this.setState({
			loading: true,
		});
		ajax.getRecommentTask({
			taskId: tabData.id,
		}).then((res: any) => {
			this.setState({
				loading: false,
			});
			if (res.code == 1) {
				this.setState({
					recommendTaskModalVisible: true,
					recommendTaskList: res.data,
				});
			}
		});
	}

	recommendTaskClose() {
		this.setState({
			recommendTaskModalVisible: false,
		});
	}

	goEdit(task: any) {
		openTaskInTab(task.id);
	}

	initColumn() {
		return [
			{
				title: '任务名称',
				dataIndex: 'name',
				key: 'name',
				render: (text: any, record: any) => {
					return (
						<a href="javascript:void(0)" onClick={this.goEdit.bind(this, record)}>
							{text}
						</a>
					);
				},
			},
			{
				title: '责任人',
				dataIndex: 'createUser.userName',
				key: 'createUser.userName',
			},
			{
				title: '操作',
				key: 'action',
				render: (text: any, record: any) => (
					<span>
						<a
							href="javascript:void(0)"
							onClick={() => {
								this.removeTaskVOS(record);
							}}
						>
							删除
						</a>
					</span>
				),
			},
		];
	}

	onTaskVOSSearch = async (value: any) => {
		const { tabData } = this.props;
		if (value.trim() === '') {
			this.setState({
				taskListSearch: [],
			});
			return;
		}
		const res = await ajax.getOfflineTaskByName({
			name: value,
			taskId: tabData.id,
			searchProjectId: 1,
		});
		if (res.code === 1) {
			this.setState({
				taskListSearch: res.data,
			});
		}
	};

	removeTaskVOS = (record: any) => {
		const { tabData, handleTaskVOSChange } = this.props;
		const { taskVOS } = tabData;
		const newTaskVOS = taskVOS?.filter((item: any) => item.id !== record.id);
		handleTaskVOSChange(newTaskVOS);
	};

	onTaskVOSSelect = async (value: any) => {
		const task = JSON.parse(value);
		const { tabData, handleTaskVOSChange } = this.props;
		const { taskVOS } = tabData;
		if (taskVOS?.find((item: any) => item.id !== task.id)) {
			return message.error('不可重复选择同一任务！');
		}
		// const res: any = ajax.checkIsLoop({
		//     taskId: tabData.id,
		//     dependencyTaskId: task.id,
		// })
		// if (res.code !== 1) {
		//     return message.error('校验循环依赖失败，请稍后再试！');
		// }
		// if (res.data) {
		//     return message.error(
		//         `添加失败，该任务循环依赖任务${res.data.name || ""}!`
		//     );
		// }
		const newTaskVOS = taskVOS?.length ? cloneDeep(taskVOS).push(task) : [task];
		handleTaskVOSChange(newTaskVOS);
	};

	recommendTaskChoose(list: any) {
		const { tabData, handleTaskVOSChange } = this.props;
		const { taskVOS } = tabData;
		const newTaskVOS = isArray(taskVOS) ? [...taskVOS, ...list] : list;
		this.setState({
			recommendTaskModalVisible: false,
		});
		handleTaskVOSChange(newTaskVOS);
	}

	render() {
		const { tabData } = this.props;
		const { loading, recommendTaskModalVisible, recommendTaskList, taskListSearch } =
			this.state;
		const isSql = tabData.taskType == TASK_TYPE.SQL;
		return (
			<React.Fragment>
				{isSql && (
					<Button
						loading={loading}
						type="primary"
						style={{ marginBottom: '20px', marginLeft: '12px' }}
						onClick={this.showRecommendTask.bind(this)}
					>
						自动推荐
					</Button>
				)}
				<FormItem {...formItemLayout} label="上游任务">
					<Select
						onSelect={this.onTaskVOSSelect}
						onSearch={debounce(this.onTaskVOSSearch, 500, {
							maxWait: 2000,
						})}
						value={undefined}
						showSearch
						style={{ width: '100%' }}
					>
						{taskListSearch?.map((item: any) => (
							<Option key={item.id} value={JSON.stringify(item)}>
								{item.name}
							</Option>
						))}
					</Select>
					{tabData.taskVOS && tabData.taskVOS.length > 0 ? (
						<Row>
							<Col>
								<Table
									className="dt-ant-table dt-ant-table--border"
									columns={this.initColumn()}
									bordered={false}
									dataSource={tabData.taskVOS}
									rowKey={(record: any) => record.id}
								/>
							</Col>
						</Row>
					) : (
						''
					)}
				</FormItem>
				<RecommendTaskModal
					visible={recommendTaskModalVisible}
					taskList={recommendTaskList}
					onOk={this.recommendTaskChoose.bind(this)}
					onCancel={this.recommendTaskClose.bind(this)}
					existTask={tabData.taskVOS}
				/>
			</React.Fragment>
		);
	}
}
export default TaskDependence;
