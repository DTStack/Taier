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

import type { ITaskStreamProps } from '@/interface';
import { TaskStatus, TaskTimeType, taskTypeText } from '@/utils/enums';
import './taskInfo.scss';

export function TaskInfo(props: { task: ITaskStreamProps }) {
	const { task } = props;
	return (
		<div className="ant-table bd task-detail">
			<table>
				<tbody className="ant-table-tbody">
					<tr>
						<td>任务名称：</td>
						<td>{task.taskName || '-'}</td>
						<td>实例ID：</td>
						<td>{task.jobId || '-'}</td>
					</tr>
					<tr>
						<td>任务类型：</td>
						<td>{taskTypeText(task.taskType)}</td>
						<td>状态：</td>
						<td>
							<TaskStatus value={task.status} />
						</td>
					</tr>
					<tr>
						<td>调度周期：</td>
						<td>
							<TaskTimeType value={task.taskPeriodId} />
						</td>
						<td>计划时间：</td>
						<td>{task.cycTime}</td>
					</tr>
					<tr>
						<td>责任人：</td>
						<td>{task.ownerName}</td>
					</tr>
				</tbody>
			</table>
		</div>
	);
}
