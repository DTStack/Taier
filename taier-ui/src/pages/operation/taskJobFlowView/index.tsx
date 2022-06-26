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

import React from 'react';
import { cloneDeep, get } from 'lodash';
import { ReloadOutlined } from '@ant-design/icons';
import { Tooltip, Modal, message } from 'antd';

import { TaskInfo } from './taskInfo';
import LogInfo from './taskLog';

import MxFactory from '@/components/mxGraph';
import { taskStatusText } from '@/utils/enums';

import Api from '@/api';
import JobGraphView, { mergeTreeNodes } from './jobGraphView';
import {
	TASK_TYPE_ENUM,
	TASK_STATUS,
	RESTART_STATUS_ENUM,
	FAILED_STATUS,
	PARENTFAILED_STATUS,
	RUN_FAILED_STATUS,
} from '@/constant';
import type { IScheduleTaskProps } from '../schedule';
import type { IUpstreamJobProps } from '@/interface';
import { DIRECT_TYPE_ENUM } from '@/interface';
import { goToTaskDev } from '@/utils';
import './index.scss';

const Mx = MxFactory.create();
const { mxEvent, mxCellHighlight, mxPopupMenu } = Mx;

interface IQueryJobStreamParams {
	jobId: string;
	directType: DIRECT_TYPE_ENUM;
	level?: number;
}

class TaskJobFlowView extends React.Component<any, any> {
	state: any = {
		selectedJob: '', // 选中的Job
		data: {}, // 数据
		loading: 'success',
		lastVertex: '',
		taskLog: {},
		logPage: {
			current: 0,
			total: 0,
		},
		logVisible: false,
		visible: false,
		visibleWorkflow: false, // 是否打开工作流
		workflowData: null, // 选中的工作流节点
		graphData: null, // 图形数据
		frontPeriodsList: [], // 前周期返回数据
		nextPeriodsList: [],
	};
	_originData: any;

	/* eslint-disable-next-line */
	UNSAFE_componentWillReceiveProps(nextProps: any) {
		const currentJob: IScheduleTaskProps = this.props.taskJob;
		const { taskJob, visibleSlidePane } = nextProps;
		if (taskJob && visibleSlidePane && (!currentJob || taskJob.jobId !== currentJob.jobId)) {
			this.loadByJobId(taskJob.jobId);
		}
	}

	isCurrentProjectTask = (node: any) => {
		return !node?.existsOnRule || false;
	};
	renderGraph = (data: IUpstreamJobProps) => {
		const originData = this._originData;
		if (originData) {
			mergeTreeNodes(originData, data);
		} else {
			this._originData = cloneDeep(data);
		}
		const graphData = cloneDeep(this._originData);
		this.setState({
			graphData: graphData,
		});
	};

	resetData = () => {
		this._originData = null; // 清空缓存数据
		this.setState({
			graphData: null,
			logPage: {
				current: 0,
				total: 0,
			},
		});
	};

	refresh = () => {
		this.resetData();
		this.loadTaskChidren({
			jobId: this.props.taskJob.jobId,
		});
	};

	loadByJobId = (jobId: string) => {
		this.resetData();
		this.loadTaskChidren({
			jobId: jobId,
		});
	};

	loadTaskChidren = (params: Partial<IQueryJobStreamParams>) => {
		const ctx = this;
		this.setState({ loading: 'loading' });
		Api.getJobChildren({
			directType: DIRECT_TYPE_ENUM.CHILD,
			...params,
		}).then((res) => {
			if (res.code === 1 && res.data?.rootNode) {
				const data = res.data.rootNode as IUpstreamJobProps;
				ctx.setState({ selectedJob: data, data });
				ctx.renderGraph(data);
			}
			ctx.setState({ loading: 'success' });
		});
	};

	/**
	 * 加载前后周期数据
	 * @param menu 子菜单
	 * @param params 请求参数
	 * @param periodsType 前后周期类型
	 */
	loadPeriodsData = (menu: any, params: any, periodsType: any) => {
		const ctx = this;
		const isNext = params.isAfter;
		Api.getOfflineTaskPeriods(params).then((res: any) => {
			if (res.code === 1) {
				!isNext
					? ctx.setState(
					{
						frontPeriodsList: res.data || [],
					},
					() => {
						ctx.state.frontPeriodsList.map((item: any) => {
							const statusText = taskStatusText(item.status);
							return menu.addItem(
								`${item.cycTime} (${statusText})`,
								null,
								function () {
									ctx.loadByJobId(item.jobId);
								},
								periodsType,
							);
						});
					},
					)
					: ctx.setState(
					{
						nextPeriodsList: res.data || [],
					},
					() => {
						ctx.state.nextPeriodsList.map((item: any) => {
							const statusText = taskStatusText(item.status);
							return menu.addItem(
								`${item.cycTime} (${statusText})`,
								null,
								function () {
									ctx.loadByJobId(item.jobId);
								},
								periodsType,
							);
						});
					},
					);
			}
		});
	};

	loadWorkflowNodes = async (workflow: any) => {
		const ctx = this;
		this.setState({ loading: 'loading' });
		// 如果折叠状态，加载
		const res = await Api.getTaskJobWorkflowNodes({ jobId: workflow.id });
		if (res.code === 1) {
			if (res.data && res.data.subNodes) {
				ctx.setState({
					visibleWorkflow: true,
					workflowData: res.data,
				});
			} else {
				message.warning('当前工作流没有子节点！');
			}
		}
		this.setState({ loading: 'success' });
	};

	stopTask = (params: any) => {
		Api.batchStopJob(params).then((res: any) => {
			if (res.code === 1) {
				message.success('任务终止运行命令已提交！');
			}
			this.refresh();
		});
	};

	restartAndResume = (params: any, msg: any) => {
		// 重跑并恢复任务
		const { reload } = this.props;
		Api.batchRestartAndResume(params).then((res) => {
			if (res.code === 1) {
				message.success(`${msg}命令已提交!`);
				if (reload) reload();
			}
			this.refresh();
		});
	};

	initContextMenu = (graph: any) => {
		const ctx = this;
		const { isPro } = this.props;
		if (graph) {
			var mxPopupMenuShowMenu = mxPopupMenu.prototype.showMenu;
			mxPopupMenu.prototype.showMenu = function () {
				var cells = graph.getSelectionCells();
				if (cells.length > 0 && cells[0].vertex) {
					mxPopupMenuShowMenu.apply(this, arguments);
				} else return false;
			};
			graph.popupMenuHandler.autoExpand = true;
			graph.popupMenuHandler.factoryMethod = function (menu: any, cell: any, evt: any) {
				if (!cell || !cell.vertex) return;

				const currentNode: IScheduleTaskProps = cell.data;
				// const isCurrentProjectTask = ctx.isCurrentProjectTask(
				// 	currentNode.batchTask,
				// );
				const isWorkflowNode = false;

				menu.addItem('展开上游（6层）', null, function () {
					ctx.loadTaskChidren({
						jobId: currentNode.jobId,
						directType: DIRECT_TYPE_ENUM.FATHER,
						level: 6,
					});
				});
				menu.addItem('展开下游（6层）', null, function () {
					ctx.loadTaskChidren({
						jobId: currentNode.jobId,
						directType: DIRECT_TYPE_ENUM.CHILD,
						level: 6,
					});
				});
				menu.addItem('查看任务日志', null, function () {
					ctx.setState(
						{
							logPage: Object.assign({}, ctx.state.logPage, {
								current: 0,
							}),
						},
						() => {
							ctx.showJobLog(currentNode.jobId);
						},
					);
				});
				menu.addItem('查看任务属性', null, function () {
					ctx.setState({ visible: true, selectedJob: currentNode });
				});
				const frontPeriods = menu.addItem('转到前一周期实例', null, null);
				const frontParams: any = {
					jobId: currentNode.jobId,
					isAfter: false,
					limit: 6,
				};
				ctx.loadPeriodsData(menu, frontParams, frontPeriods);
				const nextPeriods = menu.addItem('转到下一周期实例', null, null);
				const nextParams: any = {
					jobId: currentNode.jobId,
					isAfter: true,
					limit: 6,
				};
				ctx.loadPeriodsData(menu, nextParams, nextPeriods);
				// if (isCurrentProjectTask) {
				menu.addItem(`${isPro ? '查看' : '修改'}任务`, null, () => {
					goToTaskDev({ id: currentNode.taskId });
				});
				// }
				menu.addItem(
					'终止',
					null,
					function () {
						ctx.stopTask({
							jobIds: [currentNode.jobId],
						});
					},
					null,
					null,
					// 显示终止操作
					currentNode.status === TASK_STATUS.WAIT_SUBMIT || // 等待提交
					currentNode.status === TASK_STATUS.SUBMITTING || // 提交中
					currentNode.status === TASK_STATUS.WAIT_RUN || // 等待运行
					currentNode.status === TASK_STATUS.RUNNING, // 运行中
				);

				menu.addItem('刷新任务实例', null, function () {
					if (isWorkflowNode) {
						ctx.loadWorkflowNodes(currentNode);
					} else {
						ctx.resetGraph(cell);
					}
				});

				menu.addItem(
					'置成功并恢复调度',
					null,
					function () {
						ctx.restartAndResume(
							{
								jobIds: [currentNode.jobId],
								restartType: RESTART_STATUS_ENUM.SUCCESSFULLY_AND_RESUME,
							},
							'置成功并恢复调度',
						);
					},
					null,
					null,
					// 所有「失败」任务重跑并恢复调度
					[FAILED_STATUS, PARENTFAILED_STATUS, RUN_FAILED_STATUS].some((collection) =>
						collection.includes(currentNode.status),
					),
				);
			};
		}
	};

	initGraphEvent = (graph: any) => {
		const ctx = this;
		let highlightEdges: any = [];
		let selectedCell: any = null;

		if (graph) {
			// Double event
			graph.addListener(mxEvent.DOUBLE_CLICK, function (sender: any, evt: any) {
				const cell = evt.getProperty('cell');
				if (cell && cell.vertex) {
					const currentNode = cell.data;
					ctx.setState(
						{
							logPage: Object.assign({}, ctx.state.logPage, {
								current: 0,
							}),
						},
						() => {
							ctx.showJobLog(currentNode.jobId);
						},
					);
				}
			});

			graph.addListener(mxEvent.CELLS_FOLDED, function (sender: any, evt: any) {
				const cells = evt.getProperty('cells');
				const cell = cells && cells[0];
				const collapse = evt.getProperty('collapse');
				const isWorkflow = get(cell, 'data.batchTask.taskType') === TASK_TYPE_ENUM.WORKFLOW;

				if (cell && isWorkflow && !collapse) {
					ctx.loadWorkflowNodes(cell.data);
					// 始终保持折叠状态
					cell.collapsed = true;
				}
			});

			// Click event
			graph.addListener(mxEvent.CLICK, async function (sender: any, evt: any) {
				const cell = evt.getProperty('cell');
				if (cell && cell.vertex) {
					//   const currentNode = cell.data;
					graph.clearSelection();
					//   ctx.updateSelected(currentNode);

					const outEdges = graph.getOutgoingEdges(cell);
					const inEdges = graph.getIncomingEdges(cell);
					const edges = outEdges.concat(inEdges);
					for (let i = 0; i < edges.length; i++) {
						/* eslint-disable-next-line */
						const highlight = new mxCellHighlight(graph, '#2491F7', 2);
						const state = graph.view.getState(edges[i]);
						highlight.highlight(state);
						highlightEdges.push(highlight);
					}
					selectedCell = cell;
				} else if (cell === undefined) {
					const cells = graph.getSelectionCells();
					graph.removeSelectionCells(cells);
				}
			});

			graph.clearSelection = function (evt: any) {
				if (selectedCell) {
					for (let i = 0; i < highlightEdges.length; i++) {
						highlightEdges[i].hide();
					}
					selectedCell = null;
				}
			};
		}
	};

	resetGraph = (cell: any) => {
		const { taskJob } = this.props;
		if (taskJob) {
			this.loadTaskChidren({
				jobId: taskJob.jobId,
				directType: DIRECT_TYPE_ENUM.CHILD,
				level: 6,
			});
		}
	};

	showJobLog = (jobId: any) => {
		Api.getOfflineTaskLog({
			jobId: jobId,
			pageInfo: this.state.logPage.current,
		}).then((res: any) => {
			if (res.code === 1) {
				this.setState({
					taskLog: res.data,
					logVisible: true,
					taskLogId: jobId,
					logPage: {
						current: res.data.pageIndex,
						total: res.data.pageSize,
					},
				});
			}
		});
	};

	onCloseWorkflow = () => {
		this.setState({ visibleWorkflow: false, workflowData: null });
	};

	render() {
		const { selectedJob, taskLog, workflowData, graphData, loading, logPage, currentInfo } =
			this.state;
		const { taskJob, isPro } = this.props;
		const heightFix = {
			height: 600,
		};
		return (
			<div
				style={{
					position: 'relative',
					height: '100%',
				}}
			>
				<JobGraphView
					data={selectedJob}
					isPro={isPro}
					graphData={graphData}
					isCurrentProjectTask={this.isCurrentProjectTask}
					loading={loading}
					showJobLog={this.showJobLog}
					refresh={this.refresh}
					registerEvent={this.initGraphEvent}
					key={`graph-${graphData && graphData.id}`}
					registerContextMenu={this.initContextMenu}
					currentInfo={currentInfo}
				/>
				<Modal
					width={900}
					{...heightFix}
					zIndex={999}
					footer={null}
					maskClosable={true}
					visible={this.state.visibleWorkflow}
					title={`工作流-${get(workflowData, 'batchTask.name', '')}`}
					wrapClassName="vertical-center-modal modal-body-nopadding c-jobflow__modal"
					onCancel={this.onCloseWorkflow}
				>
					<JobGraphView
						isPro={isPro}
						isCurrentProjectTask={this.isCurrentProjectTask}
						loading={loading}
						data={selectedJob}
						showJobLog={this.showJobLog}
						registerEvent={this.initGraphEvent}
						registerContextMenu={this.initContextMenu}
						graphData={workflowData && workflowData.subNodes}
						key={`graph-workflow-${workflowData && workflowData.id}`}
						refresh={this.loadWorkflowNodes.bind(this, workflowData)}
						currentInfo={currentInfo}
					/>
				</Modal>
				<Modal
					title="查看属性"
					width="60%"
					wrapClassName="vertical-center-modal"
					visible={this.state.visible}
					onCancel={() => {
						this.setState({ visible: false });
					}}
					footer={null}
				>
					<TaskInfo task={selectedJob} />
				</Modal>
				<Modal
					key={taskJob && taskJob.jobId}
					width={800}
					title={
						<span>
							任务日志
							<Tooltip placement="right" title="刷新">
								<ReloadOutlined
									style={{
										cursor: 'pointer',
										marginLeft: '5px',
									}}
									onClick={() => {
										this.showJobLog(this.state.taskLogId);
									}}
								/>
							</Tooltip>
						</span>
					}
					wrapClassName="vertical-center-modal m-log-modal no-padding-modal"
					visible={this.state.logVisible}
					onCancel={() => {
						this.setState({
							logVisible: false,
							logPage: { current: 0 },
						});
					}}
					footer={null}
					maskClosable={true}
				>
					<LogInfo
						log={taskLog.logInfo || taskLog.engineLog}
						sqlText={taskLog.sqlText}
						syncLog={taskLog.syncLog}
						downLoadUrl={taskLog.downLoadUrl}
						syncJobInfo={taskLog.syncJobInfo}
						downloadLog={taskLog.downloadLog}
						subNodeDownloadLog={taskLog.subNodeDownloadLog}
						page={logPage}
						onChangePage={(page: number, pageSize: number) => {
							this.setState(
								{
									logPage: {
										...logPage,
										current: page,
									},
								},
								() => {
									this.showJobLog(this.state.taskLogId);
								},
							);
						}}
						height="520px"
					/>
				</Modal>
			</div>
		);
	}
}
export default TaskJobFlowView;
