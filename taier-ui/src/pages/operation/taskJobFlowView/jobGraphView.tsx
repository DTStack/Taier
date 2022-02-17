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

/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable new-cap */
import * as React from 'react';
import { get, cloneDeep } from 'lodash';

import { CloseOutlined, ReloadOutlined, ZoomInOutlined, ZoomOutOutlined } from '@ant-design/icons';
import { Tooltip, Spin, Card } from 'antd';
import MxFactory from '@/components/mxGraph';
import StatusColumn from '@/components/statusColumn';
import { taskStatusText, taskTypeText } from '@/utils/enums';
import { formatDateTime, goToTaskDev, removeToolTips, getVertxtStyle } from '@/utils';
import type { ITaskStreamProps } from '@/interface';
import type { IScheduleTaskProps } from '../schedule';
import './jobGraphView.scss';
import classNames from 'classnames';

const Mx = MxFactory.create();
const {
	mxGraph,
	mxEvent,
	mxRubberband,
	mxConstants,
	mxEdgeStyle,
	mxPerimeter,
	mxGraphView,
	mxRectangle,
	mxPoint,
	mxUtils,
	mxText,
	mxHierarchicalLayout,
} = Mx;

export const VertexSize: any = {
	// vertex大小
	width: 210,
	height: 50,
};

export const defaultGeo: any = {
	// 默认几何对象;
	count: 1,
	index: 1,
	level: 0,
	x: 10,
	y: 10,
	width: VertexSize.width,
	height: VertexSize.height,
	margin: 50,
};

export const replaceTreeNodeField = (
	treeNode: any,
	sourceField: any,
	targetField: any,
	arrField: any,
) => {
	if (treeNode) {
		treeNode[targetField] = cloneDeep(treeNode[sourceField]);
		treeNode[sourceField] = undefined;
	}
	const children = treeNode[arrField];
	if (children) {
		for (let i = 0; i < children.length; i++) {
			replaceTreeNodeField(children[i], sourceField, targetField, arrField);
		}
	}
};

/**
 * 合并Tree数据
 * @param {*} origin
 * @param {*} target
 */
export const mergeTreeNodes = (treeNodeData: ITaskStreamProps, mergeSource: ITaskStreamProps) => {
	if (treeNodeData) {
		if (treeNodeData.jobId === mergeSource.jobId) {
			if (mergeSource.childNode) {
				treeNodeData.childNode = cloneDeep(mergeSource.childNode);
			}

			if (mergeSource.parentNode) {
				treeNodeData.parentNode = cloneDeep(mergeSource.parentNode);
			}
			return;
		}

		const childNodes = treeNodeData.childNode; // 子节点
		const parentNodes = treeNodeData.parentNode; // 父节点

		// 处理依赖节点
		if (parentNodes && parentNodes.length > 0) {
			for (let i = 0; i < parentNodes.length; i++) {
				mergeTreeNodes(parentNodes[i], mergeSource);
			}
		}

		// 处理被依赖节点
		if (childNodes && childNodes.length > 0) {
			for (let i = 0; i < childNodes.length; i++) {
				mergeTreeNodes(childNodes[i], mergeSource);
			}
		}
	}
};

/**
 * 替换 TreeNode
 */
export const replaceTreeNode = (treeNodeData: any, target: any) => {
	if (treeNodeData) {
		if (treeNodeData.id === target.id) {
			Object.assign({}, treeNodeData, target);
			return;
		}

		const childNodes = treeNodeData.jobVOS; // 子节点
		const parentNodes = treeNodeData.parentNodes; // 父节点

		// 处理依赖节点
		if (parentNodes && parentNodes.length > 0) {
			for (let i = 0; i < parentNodes.length; i++) {
				replaceTreeNode(parentNodes[i], target);
			}
		}

		// 处理被依赖节点
		if (childNodes && childNodes.length > 0) {
			for (let i = 0; i < childNodes.length; i++) {
				replaceTreeNode(childNodes[i], target);
			}
		}
	}
};

export const getLevelKey = function (node: any) {
	return `${node.batchTask.flowId || ''}-${node._geometry.level}`;
};

class JobGraphView extends React.Component<any, any> {
	state: any = {
		loading: 'success',
		currentInfo: {},
		modalShow: false,
	};

	_view: any = null; // 存储view信息
	Container: any;
	graph: any;
	_cacheLevel: any;
	executeLayout: Function | null = null;

	static getDerivedStateFromProps(props: any, state: any) {
		return {
			loading: props.loading,
		};
	}

	componentDidMount() {
		this.initGraph(this.props.graphData);
		document.addEventListener('click', this.hideMenu, false);
	}

	componentDidUpdate(prevProps: any) {
		const nextGraphData = this.props.graphData;
		const { graphData, currentInfo } = prevProps;
		const newInfo = this.props.currentInfo;
		if (currentInfo !== newInfo) {
			this.setState({
				currentInfo: newInfo,
			});
			if (newInfo && JSON.stringify(newInfo) !== '{}') {
				this.setState({
					modalShow: true,
				});
			}
		}
		if (nextGraphData && nextGraphData !== graphData) {
			this.initGraph(nextGraphData);
		}
	}
	componentWillUnmount() {
		document.removeEventListener('click', this.hideMenu, false);
	}
	initGraph = async (graphData: ITaskStreamProps) => {
		this.Container.innerHTML = ''; // 清理容器内的Dom元素
		this.graph = '';
		const editor = this.Container;
		this.loadEditor(editor);
		this.initRender(graphData);
	};

	loadEditor = (container: any) => {
		mxGraphView.prototype.optimizeVmlReflows = false;
		mxText.prototype.ignoreStringSize = true; // to avoid calling getBBox
		// Disable context menu
		mxEvent.disableContextMenu(container);
		const graph = new mxGraph(container);
		this.graph = graph;
		// 启用绘制
		graph.setPanning(true);
		// 允许鼠标移动画布
		graph.panningHandler.useLeftButtonForPanning = true;
		graph.setConnectable(true);
		graph.setTooltips(true);
		graph.view.setScale(1);
		// Enables HTML labels
		graph.setHtmlLabels(true);

		graph.setAllowDanglingEdges(false);
		// 禁止连接
		graph.setConnectable(false);
		// 禁止Edge对象移动
		graph.isCellsMovable = function () {
			var cell = graph.getSelectionCell();
			return !(cell && cell.edge);
		};
		// 禁止cell编辑
		graph.isCellEditable = function () {
			return false;
		};
		graph.isCellResizable = function (cell: any) {
			return false;
		};
		// 设置Vertex样式
		const vertexStyle = this.getDefaultVertexStyle();
		graph.getStylesheet().putDefaultVertexStyle(vertexStyle);

		// 转换value显示的内容
		graph.convertValueToString = this.corvertValueToString;
		// 默认边界样式
		let edgeStyle = this.getDefaultEdgeStyle();
		graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);

		// anchor styles
		mxConstants.HANDLE_FILLCOLOR = '#ffffff';
		mxConstants.HANDLE_STROKECOLOR = '#2491F7';
		mxConstants.VERTEX_SELECTION_COLOR = '#2491F7';
		mxConstants.STYLE_OVERFLOW = 'hidden';

		// enables rubberband
		// eslint-disable-next-line no-new
		new mxRubberband(graph);

		this.executeLayout = function (layoutTarget: any, change: any, post: any) {
			const parent = layoutTarget || graph.getDefaultParent();
			graph.getModel().beginUpdate();
			try {
				const layout2 = new mxHierarchicalLayout(graph, 'north');
				layout2.disableEdgeStyle = false;
				layout2.interRankCellSpacing = 40;
				layout2.intraCellSpacing = 60;
				layout2.edgeStyle = mxEdgeStyle.TopToBottom;
				if (change != null) {
					change();
				}
				layout2.execute(parent);
			} catch (e) {
				throw e;
			} finally {
				graph.getModel().endUpdate();
				if (post != null) {
					post();
				}
			}
		};
	};

	corvertValueToString = (cell: any) => {
		const { isCurrentProjectTask } = this.props;

		if (cell.vertex && cell.value) {
			const task = cell.value as ITaskStreamProps;
			const taskType = taskTypeText(task.taskType);
			if (task) {
				return `<div class="vertex">
                <span class="vertex-title blood-title-flag">
                    ${task.taskName}
                </span>
                ${
					!isCurrentProjectTask(task)
						? "<img class='vertex-across-logo' src='/batch/public/img/across.svg' />"
						: ''
				}
                <br>
                <span class="vertex-desc">${taskType}</span>
                </div>`.replace(/(\r\n|\n)/g, '');
			}
		}
		return '';
	};

	getNodeDisplayName(node: IScheduleTaskProps | null) {
		const taskName = node?.taskName || '';
		return taskName;
	}
	/**
	 * 该方法渲染cell内容为纯字符串，
	 * 而非HTML渲染，可以提高绘制效率
	 */
	getShowStr = (data: any) => {
		const task = data.batchTask;
		if (!task) return '';
		const taskType = taskTypeText(task.taskType);
		const taskStatus = taskStatusText(data.status);
		const taskName = task.name.length > 12 ? `${task.name.substring(0, 10)}...` : task.name;
		const str = `${taskName || ''} \n ${taskType}(${taskStatus})`;
		return str;
	};

	preHandGraphTree = (data: ITaskStreamProps) => {
		const relationTree: any = [];

		const loop = (
			source: (ITaskStreamProps & { isPushed?: boolean }) | null,
			target: ITaskStreamProps & { isPushed?: boolean },
			parent: (ITaskStreamProps & { isPushed?: boolean }) | null,
		) => {
			let node: (ITaskStreamProps & { isPushed?: boolean }) | null = null;
			if (source && !source.isPushed) {
				node = source;
			} else if (target && !target.isPushed) {
				node = target;
			} else return;

			const childNodes = node.childNode; // 子节点
			const parentNodes = node.parentNode; // 父节点
			// Assign geo
			node.isPushed = true;

			relationTree.push({
				parent: parent,
				source: source,
				target: target,
			});

			// 处理父亲依赖节点
			if (parentNodes) {
				for (let i = 0; i < parentNodes.length; i++) {
					const sourceData = parentNodes[i];
					if (!sourceData) continue;
					loop(sourceData, node, parent);
				}
			}

			if (childNodes) {
				// 处理被依赖节点
				for (let i = 0; i < childNodes.length; i++) {
					const targetData = childNodes[i];
					if (!targetData) continue;
					loop(node, targetData, parent);
				}
			}
		};

		loop(null, data, null);

		return relationTree;
	};

	initRender = (data: ITaskStreamProps) => {
		const graph = this.graph;
		// Clean data;
		this._cacheLevel = {};
		graph.getModel().clear();
		const cells = graph.getChildCells(graph.getDefaultParent());
		graph.removeCells(cells);
		// Init container scroll
		this.initContainerScroll(graph);
		this.initContextMenu(graph);
		this.initGraphEvent(graph);
		this.renderGraph(data);
	};

	renderGraph = (originData: ITaskStreamProps) => {
		const cellCache: any = {};
		const graph = this.graph;
		const defaultParent = graph.getDefaultParent();
		const dataArr: {
			parent: null | ITaskStreamProps;
			source: null | ITaskStreamProps;
			target: null | ITaskStreamProps;
		}[] = this.preHandGraphTree(originData);
		
		const getVertex = (parentCell: any, data: ITaskStreamProps) => {
			if (!data) return null;

			let style = getVertxtStyle(data.status);
			// const showText = this.getShowStr(data);

			const isWorkflow = false;
			const isWorkflowNode = false;

			if (isWorkflowNode) {
				style += 'rounded=1;arcSize=60;';
			}
			const cell = graph.insertVertex(
				isWorkflow ? null : parentCell,
				data.jobId,
				data,
				0,
				0,
				VertexSize.width,
				VertexSize.height,
				style,
			);
			if (isWorkflow) {
				cell.collapsed = true;
				// Mock node
				graph.insertVertex(
					cell,
					null,
					'',
					0,
					50,
					VertexSize.width,
					VertexSize.height,
					style,
				);
				cell.geometry.alternateBounds = new mxRectangle(
					10,
					10,
					VertexSize.width,
					VertexSize.height,
				);
			}

			cell.data = data; // 添加 data 属性，便于节点操作的时候使用
			cell.isPart = isWorkflowNode;

			return cell;
		};

		if (dataArr) {
			for (let i = 0; i < dataArr.length; i++) {
				const { source, target, parent } = dataArr[i];

				let sourceCell = source ? cellCache[source.jobId] : undefined;
				let targetCell = target ? cellCache[target.jobId] : undefined;
				let parentCell = defaultParent;
				const isWorkflowNode = false;

				if (parent) {
					const existCell = cellCache[parent.jobId];
					if (existCell) {
						parentCell = existCell;
					} else {
						parentCell = getVertex(defaultParent, parent);
						cellCache[parent.jobId] = parentCell;
					}
				}

				if (source && !sourceCell) {
					sourceCell = getVertex(parentCell, source);
					cellCache[source.jobId] = sourceCell;
				}

				if (target && !targetCell) {
					targetCell = getVertex(parentCell, target);
					cellCache[target.jobId] = targetCell;
				}

				if (sourceCell && targetCell) {
					const edges = graph.getEdgesBetween(sourceCell, targetCell);
					const edgeStyle = !isWorkflowNode ? null : 'strokeColor=#B7B7B7;';

					if (edges.length === 0) {
						graph.insertEdge(
							defaultParent,
							null,
							'',
							sourceCell,
							targetCell,
							edgeStyle,
						);
					}
				}
			}
		}
		this.executeLayout?.();
		this.layoutView();
		removeToolTips();
	};

	initContextMenu = (graph: any) => {
		const { registerContextMenu } = this.props;
		if (registerContextMenu) registerContextMenu(graph);
	};

	initGraphEvent = (graph: any) => {
		const { registerEvent } = this.props;
		if (registerEvent) registerEvent(graph);
	};

	saveViewInfo = () => {
		const view = this.graph.getView();
		const translate = view.getTranslate();
		if (translate.x > 0) {
			this._view = {
				translate: translate,
				scale: view.getScale(),
			};
		}
	};

	layoutView = () => {
		const view = this._view;
		const graph = this.graph;
		if (view) {
			const scale = view.scale;
			const dx = view.translate.x;
			const dy = view.translate.y;
			graph.view.setScale(scale);
			graph.view.setTranslate(dx, dy);
		}
		// Sets initial scrollbar positions
		window.setTimeout(function () {
			var bounds = graph.getGraphBounds();
			var width = Math.max(bounds.width, graph.scrollTileSize.width * graph.view.scale);
			var height = Math.max(bounds.height, graph.scrollTileSize.height * graph.view.scale);
			graph.container.scrollTop = Math.floor(
				Math.max(0, bounds.y - Math.max(20, (graph.container.clientHeight - height) / 2)),
			);
			graph.container.scrollLeft = Math.floor(
				Math.max(0, bounds.x - Math.max(0, (graph.container.clientWidth - width) / 2)),
			);
		}, 0);
	};

	graphEnable() {
		const status = this.graph.isEnabled();
		this.graph.setEnabled(!status);
	}

	refresh = () => {
		this.saveViewInfo();
		this.props.refresh();
	};

	zoomIn = () => {
		this.graph.zoomIn();
	};

	zoomOut = () => {
		this.graph.zoomOut();
	};

	hideMenu = () => {
		const popMenus = document.querySelector('.mxPopupMenu');
		if (popMenus) {
			document.body.removeChild(popMenus);
		}
	};
	closeModal = () => {
		this.setState({
			modalShow: false,
		});
	};
	renderInfo = () => {
		const { currentInfo, modalShow } = this.state;
		if (currentInfo && JSON.stringify(currentInfo) !== '{}') {
			const name = currentInfo.name || '';
			const tenantName = currentInfo?.tenantName || '';
			const taskType = taskTypeText(currentInfo.taskType);
			const ruleType = ['无规则', '弱规则', '强规则'];
			// TASK_ALL_TYPE.forEach((item) => {
			// 	if (item.type === currentInfo.taskType) {
			// 		taskType = item.text;
			// 	}
			// });
			const taskExist =
				Array.isArray(currentInfo.scheduleDetailsVOList) &&
				currentInfo.scheduleDetailsVOList?.length > 0;
			return (
				modalShow && (
					<Card className="graphInfo">
						<p style={{ float: 'right' }}>
							<CloseOutlined onClick={this.closeModal} />
						</p>
						<h2 style={{ marginTop: 5 }}>{name}</h2>
						<p>
							<span>任务类型：</span>
							{taskType}
						</p>
						<p>
							<span>所属租户：</span>
							{tenantName}
						</p>
						{taskExist && (
							<p style={{ display: 'flex' }}>
								<span style={{ width: 60 }}>已绑定数据质量任务：</span>
								<span
									style={{
										width: 190,
										lineHeight: '20px',
										overflowY: 'scroll',
										maxHeight: 100,
										paddingLeft: 12,
									}}
								>
									{currentInfo.scheduleDetailsVOList.map(
										(item: any, index: number) => {
											const content = `${item.name} (租户：${
												item.tenantName
											}，项目：${item.projectName}${
												item.taskRule !== 0
													? `${
															ruleType[item.taskRule] === '强规则'
																? `，含${ruleType[item.taskRule]}`
																: ''
													  }`
													: ''
											})`;
											return (
												<p
													key={index}
													style={{
														marginTop: 0,
														marginBottom:
															index <
															currentInfo.scheduleDetailsVOList
																.length -
																1
																? 12
																: 0,
													}}
												>
													{content}
												</p>
											);
										},
									)}
								</span>
							</p>
						)}
					</Card>
				)
			);
		}
	};

	render() {
		const { data, isPro, showJobLog } = this.props;
		return (
			<div
				className="graph-editor"
				style={{
					position: 'relative',
					height: '100%',
				}}
			>
				{this.renderInfo()}
				<Spin
					tip="Loading..."
					size="large"
					spinning={this.state.loading === 'loading'}
					wrapperClassName={classNames('job-graph', 'c-jobGraph__spin-box')}
				>
					<div
						className="editor pointer"
						style={{
							position: 'relative',
							overflow: 'auto',
							width: '100%',
							height: 'calc(100% - 35px)',
						}}
						ref={(e: any) => {
							this.Container = e;
						}}
					></div>
				</Spin>
				<div className="graph-toolbar">
					<Tooltip placement="bottom" title="刷新">
						<ReloadOutlined onClick={this.refresh} style={{ color: '#333333' }} />
					</Tooltip>
					<Tooltip placement="bottom" title="放大">
						<ZoomInOutlined onClick={this.zoomIn} style={{ color: '#333333' }} />
					</Tooltip>
					<Tooltip placement="bottom" title="缩小">
						<ZoomOutOutlined onClick={this.zoomOut} style={{ color: '#333333' }} />
					</Tooltip>
				</div>
				<StatusColumn />
				<div
					className="box-title graph-info"
					style={{
						bottom: 0,
						height: '35px',
						lineHeight: '35px',
					}}
				>
					<span>{this.getNodeDisplayName(data)}</span>
					<span style={{ marginLeft: '15px' }}>{get(data, 'operatorName', '-')}</span>
					&nbsp;
					{isPro ? '发布' : '提交'}于&nbsp;
					<span>{formatDateTime(data.taskGmtCreate)}</span>
					&nbsp;
					<a
						title="双击任务可快速查看日志"
						onClick={() => {
							showJobLog(get(data, 'jobId'));
						}}
						style={{ marginRight: '8' }}
					>
						查看日志
					</a>
					&nbsp;
					<a
						onClick={() => {
							goToTaskDev({ id: data.taskId });
						}}
					>
						查看代码
					</a>
				</div>
			</div>
		);
	}

	initContainerScroll(graph: any) {
		/**
		 * Specifies the size of the size for "tiles" to be used for a graph with
		 * scrollbars but no visible background page. A good value is large
		 * enough to reduce the number of repaints that is caused for auto-
		 * translation, which depends on this value, and small enough to give
		 * a small empty buffer around the graph. Default is 400x400.
		 */
		graph.scrollTileSize = new mxRectangle(0, 0, 200, 200);

		/**
		 * Returns the padding for pages in page view with scrollbars.
		 */
		graph.getPagePadding = function () {
			return new mxPoint(
				Math.max(0, Math.round(graph.container.offsetWidth - 34)),
				Math.max(0, Math.round(graph.container.offsetHeight - 34)),
			);
		};

		/**
		 * Returns the size of the page format scaled with the page size.
		 */
		graph.getPageSize = function () {
			return this.pageVisible
				? new mxRectangle(
						0,
						0,
						this.pageFormat.width * this.pageScale,
						this.pageFormat.height * this.pageScale,
				  )
				: this.scrollTileSize;
		};

		/**
		 * Returns a rectangle describing the position and count of the
		 * background pages, where x and y are the position of the top,
		 * left page and width and height are the vertical and horizontal
		 * page count.
		 */
		graph.getPageLayout = function () {
			var size = this.pageVisible ? this.getPageSize() : this.scrollTileSize;
			var bounds = this.getGraphBounds();

			if (bounds.width == 0 || bounds.height == 0) {
				return new mxRectangle(0, 0, 1, 1);
			} else {
				// Computes untransformed graph bounds
				var x = Math.ceil(bounds.x / this.view.scale - this.view.translate.x);
				var y = Math.ceil(bounds.y / this.view.scale - this.view.translate.y);
				var w = Math.floor(bounds.width / this.view.scale);
				var h = Math.floor(bounds.height / this.view.scale);

				var x0 = Math.floor(x / size.width);
				var y0 = Math.floor(y / size.height);
				var w0 = Math.ceil((x + w) / size.width) - x0;
				var h0 = Math.ceil((y + h) / size.height) - y0;

				return new mxRectangle(x0, y0, w0, h0);
			}
		};

		// Fits the number of background pages to the graph
		graph.view.getBackgroundPageBounds = function () {
			var layout = this.graph.getPageLayout();
			var page = this.graph.getPageSize();

			return new mxRectangle(
				this.scale * (this.translate.x + layout.x * page.width),
				this.scale * (this.translate.y + layout.y * page.height),
				this.scale * layout.width * page.width,
				this.scale * layout.height * page.height,
			);
		};

		graph.getPreferredPageSize = function (bounds: any, width: any, height: any) {
			var pages = this.getPageLayout();
			var size = this.getPageSize();

			return new mxRectangle(0, 0, pages.width * size.width, pages.height * size.height);
		};

		/**
		 * Guesses autoTranslate to avoid another repaint (see below).
		 * Works if only the scale of the graph changes or if pages
		 * are visible and the visible pages do not change.
		 */
		var graphViewValidate = graph.view.validate;
		graph.view.validate = function () {
			if (this.graph.container != null && mxUtils.hasScrollbars(this.graph.container)) {
				var pad = this.graph.getPagePadding();
				var size = this.graph.getPageSize();

				// Updating scrollbars here causes flickering in quirks and is not needed
				// if zoom method is always used to set the current scale on the graph.
				// var tx = this.translate.x;
				// var ty = this.translate.y;
				this.translate.x = pad.x / this.scale - (this.x0 || 0) * size.width;
				this.translate.y = pad.y / this.scale - (this.y0 || 0) * size.height;
			}

			graphViewValidate.apply(this, arguments);
		};

		var graphSizeDidChange = graph.sizeDidChange;
		graph.sizeDidChange = function () {
			if (this.container != null && mxUtils.hasScrollbars(this.container)) {
				var pages = this.getPageLayout();
				var pad = this.getPagePadding();
				var size = this.getPageSize();

				// Updates the minimum graph size
				var minw = Math.ceil((2 * pad.x) / this.view.scale + pages.width * size.width);
				var minh = Math.ceil((2 * pad.y) / this.view.scale + pages.height * size.height);

				var min = graph.minimumGraphSize;

				// LATER: Fix flicker of scrollbar size in IE quirks mode
				// after delayed call in window.resize event handler
				if (min == null || min.width != minw || min.height != minh) {
					graph.minimumGraphSize = new mxRectangle(0, 0, minw, minh);
				}

				// Updates auto-translate to include padding and graph size
				var dx = pad.x / this.view.scale - pages.x * size.width;
				var dy = pad.y / this.view.scale - pages.y * size.height;

				if (
					!this.autoTranslate &&
					(this.view.translate.x != dx || this.view.translate.y != dy)
				) {
					this.autoTranslate = true;
					this.view.x0 = pages.x;
					this.view.y0 = pages.y;

					// NOTE: THIS INVOKES THIS METHOD AGAIN. UNFORTUNATELY THERE IS NO WAY AROUND THIS SINCE THE
					// BOUNDS ARE KNOWN AFTER THE VALIDATION AND SETTING THE TRANSLATE TRIGGERS A REVALIDATION.
					// SHOULD MOVE TRANSLATE/SCALE TO VIEW.
					var tx = graph.view.translate.x;
					var ty = graph.view.translate.y;

					graph.view.setTranslate(dx, dy);
					graph.container.scrollLeft += (dx - tx) * graph.view.scale;
					graph.container.scrollTop += (dy - ty) * graph.view.scale;

					this.autoTranslate = false;
					return;
				}

				graphSizeDidChange.apply(this, arguments);
			}
		};
	}

	getDefaultVertexStyle() {
		let style: any = [];
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
		style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
		style[mxConstants.STYLE_STROKECOLOR] = '#A7CDF0';
		style[mxConstants.STYLE_FILLCOLOR] = '#EDF6FF';
		style[mxConstants.STYLE_FONTCOLOR] = '#333333';
		style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
		style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
		style[mxConstants.STYLE_FONTSIZE] = '12';
		style[mxConstants.STYLE_FONTFAMILY] = 'PingFangSC-Regular';
		style[mxConstants.FONT_BOLD] = 'normal';
		style[mxConstants.STYLE_WHITE_SPACE] = 'nowrap';
		style[mxConstants.STYLE_FONTSTYLE] = 1;
		// style[mxConstants.STYLE_OVERFLOW] = 'hidden';

		return style;
	}

	getDefaultEdgeStyle() {
		let style: any = [];
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
		style[mxConstants.STYLE_STROKECOLOR] = '#2491F7';
		style[mxConstants.STYLE_STROKEWIDTH] = 1;
		style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
		style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
		style[mxConstants.STYLE_EDGE] = mxEdgeStyle.TopToBottom;
		style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_BLOCK;
		style[mxConstants.STYLE_FONTSIZE] = '10';
		style[mxConstants.STYLE_ROUNDED] = false;
		return style;
	}
}
export default JobGraphView;
