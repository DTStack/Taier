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

import { useEffect, useState } from 'react';
import { history } from 'umi';
import Api from '@/api';
import { DRAWER_MENU_ENUM, SCHEDULE_STATUS } from '@/constant';
import type { IUpstreamJobProps, ITaskProps } from '@/interface';
import { DIRECT_TYPE_ENUM } from '@/interface';
import type { IContextMenuConfig } from '@/components/mxGraph/container';
import MxGraphContainer from '@/components/mxGraph/container';
import { formatDateTime, goToTaskDev } from '@/utils';
import { taskTypeText } from '@/utils/enums';

interface ITaskFlowViewProps {
	tabData: ITaskProps | null;
	onPatchData?: (task: IUpstreamJobProps) => void;
	onForzenTasks?: (taskId: number, status: SCHEDULE_STATUS) => void;
}

interface IGetTaskChildrenParams {
	taskId: number;
	directType: DIRECT_TYPE_ENUM;
	/**
	 * 不传的话默认是 2 层
	 */
	level?: number;
}

const TaskFlowView = ({ tabData, onPatchData, onForzenTasks }: ITaskFlowViewProps) => {
	const [graphData, setGraphData] = useState<[IUpstreamJobProps] | null>(null);
	const [loading, setLoading] = useState(false);

	/**
	 * 获取任务上下游关系
	 */
	const loadTaskChidren = (
		taskId: number,
		directType: DIRECT_TYPE_ENUM = DIRECT_TYPE_ENUM.CHILD,
		level?: number,
	) => {
		setLoading(true);
		Api.getTaskChildren({
			taskId,
			directType,
			level,
		} as IGetTaskChildrenParams)
			.then((res) => {
				if (res.code === 1) {
					const data: IUpstreamJobProps = res.data?.rootTaskNode || {};

					// 不同的 directType 取不同的字段
					const property =
						directType === DIRECT_TYPE_ENUM.CHILD ? 'childNode' : 'parentNode';

					setGraphData((graph) => {
						if (graph) {
							const stack = [graph[0]];
							while (stack.length) {
								const item = stack.pop()!;
								if (item.taskId === data?.taskId) {
									item[property] = data[property];
									break;
								}

								stack.push(...(item?.[property] || []));
							}

							return [...graph];
						}

						return [data];
					});
				}
			})
			.finally(() => {
				setLoading(false);
			});
	};

	const refresh = () => {
		if (tabData) {
			setGraphData(null);
			loadTaskChidren(tabData.taskId);
		}
	};

	const handleContextMenu = (data: IUpstreamJobProps): IContextMenuConfig[] => {
		return [
			{
				title: '展开上游（6层）',
				callback: () => loadTaskChidren(data.taskId, DIRECT_TYPE_ENUM.FATHER, 6),
			},
			{
				title: '展开下游（6层）',
				callback: () => loadTaskChidren(data.taskId, DIRECT_TYPE_ENUM.CHILD, 6),
			},
			{
				title: '补数据',
				callback: () => onPatchData?.(data),
			},
			{
				title: '冻结',
				callback: () => onForzenTasks?.(data.taskId, SCHEDULE_STATUS.FORZON),
				disabled: data.scheduleStatus !== SCHEDULE_STATUS.NORMAL,
			},
			{
				title: '解冻',
				callback: () => onForzenTasks?.(data.taskId, SCHEDULE_STATUS.NORMAL),
				disabled:
					data.scheduleStatus !== SCHEDULE_STATUS.STOPPED &&
					data.scheduleStatus !== SCHEDULE_STATUS.FORZON,
			},
			{
				title: '查看实例',
				callback: () =>
					history.push({
						query: {
							drawer: DRAWER_MENU_ENUM.SCHEDULE,
							tName: data.taskName,
						},
					}),
			},
		];
	};

	const handleRenderCell = (cell: IMxCell) => {
		const task = cell.value;
		if (task) {
			const taskType = taskTypeText(task.taskType);
			return `<div class="vertex" >
			<span class='vertex-title'>
				${task.taskName}
			</span>
			<br>
			<span class="vertex-desc">${taskType}</span>
			</div>`.replace(/(\r\n|\n)/g, '');
		}

		return '';
	};

	useEffect(() => {
		if (tabData) {
			refresh();
		}
	}, [tabData?.taskId]);

	return (
		<MxGraphContainer<IUpstreamJobProps>
			graphData={graphData}
			loading={loading}
			onRefresh={refresh}
			onRenderCell={handleRenderCell}
			onContextMenu={handleContextMenu}
			onDrawVertex={(data) => {
				if (data.scheduleStatus === SCHEDULE_STATUS.FORZON) {
					return 'whiteSpace=wrap;fillColor=#EFFFFE;strokeColor=#26DAD1;';
				}
				return 'whiteSpace=wrap;fillColor=#EDF6FF;strokeColor=#A7CDF0;';
			}}
		>
			{(data) => (
				<>
					<div className="graph-info">
						<span>{data?.taskName || '-'}</span>
						<span className="mx-2">{data?.operatorName || '-'}</span>
						发布于
						{data && (
							<>
								<span>{formatDateTime(data.gmtCreate)}</span>
								<a
									className="mx-2"
									onClick={() => {
										goToTaskDev({ id: data.taskId });
									}}
								>
									查看代码
								</a>
							</>
						)}
					</div>
				</>
			)}
		</MxGraphContainer>
	);
};

export default TaskFlowView;
