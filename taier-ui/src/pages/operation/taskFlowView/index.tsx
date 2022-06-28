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

import { useEffect, useState, useRef } from 'react';
import { cloneDeep } from 'lodash';
import { history } from 'umi';
import Api from '@/api';
import type { IMxCell, IMxGraph } from './taskGraphView';
import TaskGraphView, { mergeTreeNodes } from './taskGraphView';
import MxFactory from '@/components/mxGraph';
import { DRAWER_MENU_ENUM, SCHEDULE_STATUS } from '@/constant';
import type { IUpstreamJobProps, ITaskProps } from '@/interface';
import { DIRECT_TYPE_ENUM } from '@/interface';

const Mx = MxFactory.create();
const { mxEvent, mxCellHighlight: MxCellHightlight, mxPopupMenu } = Mx;

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
	const [graphData, setGraphData] = useState<IUpstreamJobProps | null>(null);
	const [selectedTask, setSelectedTask] = useState<IUpstreamJobProps | null>(null);
	const [loading, setLoading] = useState(false);
	const originData = useRef<IUpstreamJobProps | undefined>();

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
					setSelectedTask(data);
					renderGraph(data);
				}
			})
			.finally(() => {
				setLoading(false);
			});
	};

	const renderGraph = (data: IUpstreamJobProps) => {
		if (originData.current) {
			mergeTreeNodes(originData.current, data);
		} else {
			originData.current = cloneDeep(data);
		}
		const nextGraphData = cloneDeep(originData.current);
		setGraphData(nextGraphData);
	};

	const refresh = () => {
		if (tabData) {
			originData.current = undefined; // 清空缓存数据
			setGraphData(null);
			setSelectedTask(null);
			loadTaskChidren(tabData.taskId);
		}
	};

	const isCurrentProjectTask = () => {
		return false;
	};

	const initContextMenu = (graph: IMxGraph) => {
		const mxPopupMenuShowMenu = mxPopupMenu.prototype.showMenu;
		mxPopupMenu.prototype.showMenu = function () {
			const cells = this.graph.getSelectionCells();
			if (cells.length > 0 && cells[0].vertex) {
				// eslint-disable-next-line prefer-rest-params
				mxPopupMenuShowMenu.apply(this, arguments);
			} else return false;
		};
		// eslint-disable-next-line no-param-reassign
		graph.popupMenuHandler.autoExpand = true;
		// eslint-disable-next-line no-param-reassign
		graph.popupMenuHandler.factoryMethod = function (menu: any, cell: IMxCell) {
			if (!cell || !cell.vertex) return;

			const currentNode = cell.value;
			if (currentNode) {
				menu.addItem('展开上游（6层）', null, () => {
					loadTaskChidren(currentNode.taskId, DIRECT_TYPE_ENUM.FATHER, 6);
				});
				menu.addItem('展开下游（6层）', null, () => {
					loadTaskChidren(currentNode.taskId, DIRECT_TYPE_ENUM.CHILD, 6);
				});
				menu.addItem('补数据', null, () => {
					onPatchData?.(currentNode);
				});
				menu.addItem(
					'冻结',
					null,
					() => onForzenTasks?.(currentNode.taskId, SCHEDULE_STATUS.FORZON),
					null,
					null,
					currentNode.scheduleStatus === SCHEDULE_STATUS.NORMAL,
				);

				menu.addItem(
					'解冻',
					null,
					() => onForzenTasks?.(currentNode.taskId, SCHEDULE_STATUS.NORMAL),
					null,
					null,
					currentNode.scheduleStatus === SCHEDULE_STATUS.STOPPED ||
						currentNode.scheduleStatus === SCHEDULE_STATUS.FORZON,
				);
				menu.addItem('查看实例', null, () => {
					history.push({
						query: {
							drawer: DRAWER_MENU_ENUM.SCHEDULE,
							tName: currentNode.taskName,
						},
					});
				});
			}
		};
	};

	const initGraphEvent = (graph: IMxGraph) => {
		const highlightEdges: typeof MxCellHightlight[] = [];
		let selectedCell: IMxCell | null = null;
		if (graph) {
			// cell 点击事件
			graph.addListener(mxEvent.onClick, async (sender: any, evt: any) => {
				const cell: IMxCell = evt.getProperty('cell');
				if (cell && cell.vertex) {
					graph.clearSelection();
					const data = cell.value!;
					setSelectedTask(data);

					const outEdges = graph.getOutgoingEdges(cell);
					const inEdges = graph.getIncomingEdges(cell);
					const edges = outEdges.concat(inEdges);
					for (let i = 0; i < edges.length; i += 1) {
						const highlight = new MxCellHightlight(graph, '#2491F7', 2);
						const state = graph.view.getState(edges[i]);
						highlight.highlight(state);
						highlightEdges.push(highlight);
					}
					selectedCell = cell;
				} else {
					const cells = graph.getSelectionCells();
					graph.removeSelectionCells(cells);
				}
			});

			// eslint-disable-next-line no-param-reassign
			graph.clearSelection = function () {
				if (selectedCell) {
					for (let i = 0; i < highlightEdges.length; i += 1) {
						highlightEdges[i].hide();
					}
					selectedCell = null;
				}
			};
		}
	};

	useEffect(() => {
		if (tabData) {
			refresh();
		}
	}, [tabData]);

	return (
		graphData && (
			<TaskGraphView
				data={selectedTask}
				isCurrentProjectTask={isCurrentProjectTask}
				graphData={graphData}
				key={`task-graph-view-${graphData && graphData.taskId}`}
				loading={loading}
				onRefresh={refresh}
				registerEvent={initGraphEvent}
				registerContextMenu={initContextMenu}
			/>
		)
	);
};

export default TaskFlowView;
