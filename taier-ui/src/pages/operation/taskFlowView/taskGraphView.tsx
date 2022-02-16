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
/* eslint-disable new-cap */
/* eslint-disable func-names */
/* eslint-disable no-param-reassign */
import { useEffect, useRef } from 'react';
import { cloneDeep } from 'lodash';
import { ReloadOutlined, ZoomInOutlined, ZoomOutOutlined } from '@ant-design/icons';
import { Tooltip, Spin } from 'antd';
import MxFactory from '@/components/mxGraph';
import { taskTypeText } from '@/utils/enums';
import { formatDateTime, goToTaskDev } from '@/utils';
import { SCHEDULE_STATUS } from '@/constant';
import type { ITaskStreamProps } from '@/interface';

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

// vertex大小
const VertexSize = {
	width: 210,
	height: 50,
};

/**
 * 合并Tree数据
 */
export const mergeTreeNodes = (treeNodeData: ITaskStreamProps, mergeSource: ITaskStreamProps) => {
	if (treeNodeData) {
		if (treeNodeData.taskId === mergeSource.taskId) {
			if (mergeSource.childNode) {
				treeNodeData.childNode = cloneDeep(mergeSource.childNode);
			}
			if (mergeSource.parentNode) {
				treeNodeData.parentNode = cloneDeep(mergeSource.parentNode);
			}
			return;
		}

		const parentNodes = treeNodeData.parentNode; // 父节点
		const childNodes = treeNodeData.childNode; // 子节点

		// 处理依赖节点
		if (parentNodes && parentNodes.length > 0) {
			for (let i = 0; i < parentNodes.length; i += 1) {
				mergeTreeNodes(parentNodes[i], mergeSource);
			}
		}

		// 处理被依赖节点
		if (childNodes && childNodes.length > 0) {
			for (let i = 0; i < childNodes.length; i += 1) {
				mergeTreeNodes(childNodes[i], mergeSource);
			}
		}
	}
};

interface IGraphFlowNodeProps extends ITaskStreamProps {
	isPushed?: boolean;
}

interface IGraphProps {
	height?: number;
	loading?: boolean;
	hideFooter?: boolean;
	/**
	 * 当前选中的节点，若显示 footer，则在 footer 处会显示该节点信息
	 */
	data: ITaskStreamProps | null | undefined;
	/**
	 * 点击刷新触发事件
	 */
	onRefresh?: () => void;
	/**
	 * 是否是当前项目任务
	 */
	isCurrentProjectTask?: (task: ITaskStreamProps) => boolean;
	/**
	 * 注册图的右键菜单
	 */
	registerContextMenu?: (graph: IMxGraph) => void;
	/**
	 * 注册图的事件
	 */
	registerEvent?: (graph: IMxGraph) => void;
	/**
	 * 图数据
	 */
	graphData?: ITaskStreamProps;
}

export interface IMxCell {
	id: string;
	mxObjectId: string;
	/**
	 * mxGeometry
	 */
	geometry?: any;
	style?: string | null;
	value?: IGraphFlowNodeProps | null;
	parent?: IMxCell;
	/**
	 * Root cell has children
	 */
	children?: IMxCell[];
	edge?: true;
	vertex?: true;
	target?: IMxCell | null;
	source?: IMxCell | null;
	/**
	 * Only vertex cells have edges
	 */
	edges?: IMxCell[];
	/**
	 * Only vertex cells have this property
	 */
	connectable?: boolean;
}

type mxPointProps = {
	x: number;
	y: number;
};

interface IGraphInfoProps {
	view: {
		translate: mxPointProps;
		scale: number;
	} | null;
}

export type IMxGraph = typeof mxGraph;

/**
 * 边框的默认样式
 */
function getDefaultVertexStyle() {
	const style = [];
	style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
	style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
	style[mxConstants.STYLE_STROKECOLOR] = '#A7CDF0';
	style[mxConstants.STYLE_FILLCOLOR] = '#EDF6FF';
	style[mxConstants.STYLE_FONTCOLOR] = '#333333';
	style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
	style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
	style[mxConstants.STYLE_FONTSIZE] = '14';
	style[mxConstants.STYLE_FONTSTYLE] = 1;
	return style;
}

/**
 * 边线的默认样式
 */
function getDefaultEdgeStyle() {
	const style = [];
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

/**
 * 初始化 graph 相关配置
 */
function initContainerScroll(graph: IMxGraph) {
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
		const size = this.pageVisible ? this.getPageSize() : this.scrollTileSize;
		const bounds = this.getGraphBounds();

		if (bounds.width === 0 || bounds.height === 0) {
			return new mxRectangle(0, 0, 1, 1);
		}

		// Computes untransformed graph bounds
		const x = Math.ceil(bounds.x / this.view.scale - this.view.translate.x);
		const y = Math.ceil(bounds.y / this.view.scale - this.view.translate.y);
		const w = Math.floor(bounds.width / this.view.scale);
		const h = Math.floor(bounds.height / this.view.scale);

		const x0 = Math.floor(x / size.width);
		const y0 = Math.floor(y / size.height);
		const w0 = Math.ceil((x + w) / size.width) - x0;
		const h0 = Math.ceil((y + h) / size.height) - y0;

		return new mxRectangle(x0, y0, w0, h0);
	};

	// Fits the number of background pages to the graph
	graph.view.getBackgroundPageBounds = function () {
		const layout = this.graph.getPageLayout();
		const page = this.graph.getPageSize();

		return new mxRectangle(
			this.scale * (this.translate.x + layout.x * page.width),
			this.scale * (this.translate.y + layout.y * page.height),
			this.scale * layout.width * page.width,
			this.scale * layout.height * page.height,
		);
	};

	graph.getPreferredPageSize = function () {
		const pages = this.getPageLayout();
		const size = this.getPageSize();

		return new mxRectangle(0, 0, pages.width * size.width, pages.height * size.height);
	};

	/**
	 * Guesses autoTranslate to avoid another repaint (see below).
	 * Works if only the scale of the graph changes or if pages
	 * are visible and the visible pages do not change.
	 */
	const graphViewValidate = graph.view.validate;
	graph.view.validate = function () {
		if (this.graph.container != null && mxUtils.hasScrollbars(this.graph.container)) {
			const pad = this.graph.getPagePadding();
			const size = this.graph.getPageSize();

			// Updating scrollbars here causes flickering in quirks and is not needed
			// if zoom method is always used to set the current scale on the graph.
			// var tx = this.translate.x;
			// var ty = this.translate.y;
			this.translate.x = pad.x / this.scale - (this.x0 || 0) * size.width;
			this.translate.y = pad.y / this.scale - (this.y0 || 0) * size.height;
		}

		// eslint-disable-next-line prefer-rest-params
		graphViewValidate.apply(this, arguments);
	};

	const graphSizeDidChange = graph.sizeDidChange;
	graph.sizeDidChange = function () {
		if (this.container != null && mxUtils.hasScrollbars(this.container)) {
			const pages = this.getPageLayout();
			const pad = this.getPagePadding();
			const size = this.getPageSize();

			// Updates the minimum graph size
			const minw = Math.ceil((2 * pad.x) / this.view.scale + pages.width * size.width);
			const minh = Math.ceil((2 * pad.y) / this.view.scale + pages.height * size.height);

			const min = graph.minimumGraphSize;

			// LATER: Fix flicker of scrollbar size in IE quirks mode
			// after delayed call in window.resize event handler
			if (min === null || min.width !== minw || min.height !== minh) {
				graph.minimumGraphSize = new mxRectangle(0, 0, minw, minh);
			}

			// Updates auto-translate to include padding and graph size
			const dx = pad.x / this.view.scale - pages.x * size.width;
			const dy = pad.y / this.view.scale - pages.y * size.height;

			if (
				!this.autoTranslate &&
				(this.view.translate.x !== dx || this.view.translate.y !== dy)
			) {
				this.autoTranslate = true;
				this.view.x0 = pages.x;
				this.view.y0 = pages.y;

				// NOTE: THIS INVOKES THIS METHOD AGAIN. UNFORTUNATELY THERE IS NO WAY AROUND THIS SINCE THE
				// BOUNDS ARE KNOWN AFTER THE VALIDATION AND SETTING THE TRANSLATE TRIGGERS A REVALIDATION.
				// SHOULD MOVE TRANSLATE/SCALE TO VIEW.
				const tx = graph.view.translate.x;
				const ty = graph.view.translate.y;

				graph.view.setTranslate(dx, dy);
				graph.container.scrollLeft += (dx - tx) * graph.view.scale;
				graph.container.scrollTop += (dy - ty) * graph.view.scale;

				this.autoTranslate = false;
				return;
			}

			// eslint-disable-next-line prefer-rest-params
			graphSizeDidChange.apply(this, arguments);
		}
	};
}

/**
 * 预处理图数据，获取节点之间的关系
 */
function preHandGraphTree(data: ITaskStreamProps) {
	const relationTree: {
		source: IGraphFlowNodeProps | null;
		target: IGraphFlowNodeProps;
		parent: IGraphFlowNodeProps | null;
	}[] = [];
	const loop = (
		source: IGraphFlowNodeProps | null,
		target: IGraphFlowNodeProps,
		parent: IGraphFlowNodeProps | null,
	) => {
		let node: IGraphFlowNodeProps | null = null;
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
			parent,
			source,
			target,
		});

		// 处理父亲依赖节点
		if (parentNodes) {
			for (let i = 0; i < parentNodes.length; i += 1) {
				const sourceData = parentNodes[i];
				if (sourceData) {
					loop(sourceData, node, parent);
				}
			}
		}

		if (childNodes) {
			// 处理被依赖节点
			for (let i = 0; i < childNodes.length; i += 1) {
				const targetData = childNodes[i];
				if (targetData) {
					loop(node, targetData, parent);
				}
			}
		}
	};

	loop(null, data, null);

	return relationTree;
}

/**
 * 特殊 vertex 的样式
 */
function getVertxtStyles(data: ITaskStreamProps) {
	if (data.scheduleStatus === SCHEDULE_STATUS.FORZON) {
		return 'whiteSpace=wrap;fillColor=#EFFFFE;strokeColor=#26DAD1;';
	}
	return 'whiteSpace=wrap;fillColor=#EDF6FF;strokeColor=#A7CDF0;';
}

const TaskGraphView = ({
	height,
	loading,
	hideFooter,
	data,
	graphData,
	onRefresh,
	isCurrentProjectTask,
	registerEvent,
	registerContextMenu,
}: IGraphProps) => {
	const container = useRef<HTMLDivElement>(null);
	const graphRef = useRef<IMxGraph>();
	const executeLayoutFn = useRef<(...args: any[]) => void>(() => {});
	const graphInfo = useRef<IGraphInfoProps>({
		view: null,
	});

	const refresh = () => {
		saveViewInfo();
		onRefresh?.();
	};

	const saveViewInfo = () => {
		const view = graphRef.current.getView();
		const translate: mxPointProps = view.getTranslate();
		if (translate.x > 0) {
			graphInfo.current.view = {
				translate,
				scale: view.getScale(),
			};
		}
	};

	const handleLayoutZoom = (flag: 'in' | 'out') => {
		graphRef.current[flag === 'in' ? 'zoomIn' : 'zoomOut']();
	};

	const initGraph = async (initData: ITaskStreamProps) => {
		if (container.current) {
			// 清理容器内的Dom元素
			container.current.innerHTML = '';
			graphRef.current = '';
			initialGraph(container.current);
			initRender(initData);
		}
	};

	const initRender = (renderData: ITaskStreamProps) => {
		const graph = graphRef.current;
		graph.getModel().clear();
		const cells: IMxCell[] = graph.getChildCells(graph.getDefaultParent());
		// Clean data;
		graph.removeCells(cells);

		// Init container scroll
		initContainerScroll(graph);
		initContextMenu(graph);
		initGraphEvent(graph);

		renderGraph(renderData);
	};

	const renderGraph = (originData: ITaskStreamProps) => {
		const cellCache: Record<number, IMxCell> = {};
		const graph = graphRef.current;
		const defaultParent: IMxCell = graph.getDefaultParent();
		const dataArr = preHandGraphTree(originData);

		const getVertex = (parentCell: IMxCell, vertexData: ITaskStreamProps) => {
			if (!vertexData) return null;
			const style = getVertxtStyles(vertexData);
			const cell: IMxCell = graph.insertVertex(
				parentCell,
				vertexData.taskId,
				vertexData,
				0,
				0,
				VertexSize.width,
				VertexSize.height,
				style,
			);
			return cell;
		};

		if (dataArr) {
			for (let i = 0; i < dataArr.length; i += 1) {
				const { source, target, parent } = dataArr[i];

				let sourceCell = source ? cellCache[source.taskId] : null;
				let targetCell = target ? cellCache[target.taskId] : null;
				let parentCell = defaultParent;

				if (parent) {
					const existCell = cellCache[parent.taskId];
					if (existCell) {
						parentCell = existCell;
					} else {
						parentCell = getVertex(defaultParent, parent)!;
						cellCache[parent.taskId] = parentCell;
					}
				}

				if (source && !sourceCell) {
					sourceCell = getVertex(parentCell, source)!;
					cellCache[source.taskId] = sourceCell;
				}

				if (target && !targetCell) {
					targetCell = getVertex(parentCell, target)!;
					cellCache[target.taskId] = targetCell;
				}

				if (sourceCell && targetCell) {
					const edges = graph.getEdgesBetween(sourceCell, targetCell);
					const edgeStyle = null;
					if (edges.length === 0) {
						graph.insertEdge(
							defaultParent,
							null,
							null,
							sourceCell,
							targetCell,
							edgeStyle,
						);
					}
				}
			}
		}
		executeLayoutFn.current?.();
		layoutView();
	};

	const layoutView = () => {
		const { view } = graphInfo.current;
		const graph = graphRef.current;
		if (view) {
			const { scale } = view;
			const dx = view.translate.x;
			const dy = view.translate.y;
			graph.view.setScale(scale);
			graph.view.setTranslate(dx, dy);
		}

		// Sets initial scrollbar positions
		window.setTimeout(() => {
			const bounds = graph.getGraphBounds();
			const boundsWidth = Math.max(
				bounds.width,
				graph.scrollTileSize.width * graph.view.scale,
			);
			const boundsHeight = Math.max(
				bounds.height,
				graph.scrollTileSize.height * graph.view.scale,
			);
			graph.container.scrollTop = Math.floor(
				Math.max(
					0,
					bounds.y - Math.max(20, (graph.container.clientHeight - boundsHeight) / 2),
				),
			);
			graph.container.scrollLeft = Math.floor(
				Math.max(
					0,
					bounds.x - Math.max(0, (graph.container.clientWidth - boundsWidth) / 2),
				),
			);
		}, 0);
	};

	/**
	 * 初始化右键菜单
	 */
	const initContextMenu = (graph: IMxGraph) => {
		registerContextMenu?.(graph);
	};

	const initGraphEvent = (graph: IMxGraph) => {
		registerEvent?.(graph);
	};

	/**
	 * 初始化画布
	 */
	const initialGraph = (containerDom: HTMLDivElement) => {
		mxGraphView.prototype.optimizeVmlReflows = false;
		// to avoid calling getBBox
		mxText.prototype.ignoreStringSize = true;
		// Disable context menu
		mxEvent.disableContextMenu(containerDom);
		const graph = new mxGraph(containerDom);
		graphRef.current = graph;

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
			const cell = graph.getSelectionCell();
			return !(cell && cell.edge);
		};
		// 禁止cell编辑
		graph.isCellEditable = () => false;
		graph.isCellResizable = () => false;

		const vertexStyle = getDefaultVertexStyle();
		graph.getStylesheet().putDefaultVertexStyle(vertexStyle);
		// 转换value显示的内容
		graph.convertValueToString = renderVertexHTML;
		// 默认边界样式
		const edgeStyle = getDefaultEdgeStyle();
		graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);

		// anchor styles
		mxConstants.HANDLE_FILLCOLOR = '#ffffff';
		mxConstants.HANDLE_STROKECOLOR = '#2491F7';
		mxConstants.VERTEX_SELECTION_COLOR = '#2491F7';
		mxConstants.STYLE_OVERFLOW = 'hidden';

		// eslint-disable-next-line no-new
		new mxRubberband(graph);

		executeLayoutFn.current = function (
			layoutTarget: IMxCell,
			change: (() => void) | null,
			post: (() => void) | null,
		) {
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
			} finally {
				graph.getModel().endUpdate();
				if (post != null) {
					post();
				}
			}
		};
	};

	/**
	 * Vertex 渲染的 HTML 样式
	 */
	const renderVertexHTML = (cell: IMxCell) => {
		if (cell.vertex && cell.value) {
			const task = cell.value || {};
			const taskType = taskTypeText(task.taskType);
			if (task) {
				return `<div class="vertex" >
                <span class='blood-vertex-title blood-title-flag'>
                    ${task.taskName}
                </span>
                ${
					!isCurrentProjectTask?.(task)
						? "<img class='vertex-across-logo' src='images/across.svg' />"
						: ''
				}
                <br>
                <span class="vertex-desc">${taskType}</span>
                </div>`.replace(/(\r\n|\n)/g, '');
			}
		}
		return '';
	};

	useEffect(() => {
		if (graphData) {
			initGraph(graphData);
		}
	}, [graphData]);

	const editorHeight = hideFooter ? height || '800px' : '100%';

	return (
		<div className="graph-editor">
			<Spin tip="Loading..." size="large" spinning={loading} wrapperClassName="task-graph">
				<div
					style={{
						position: 'relative',
						overflow: 'auto',
						width: '100%',
						height: editorHeight,
					}}
					ref={container}
				/>
			</Spin>
			{!hideFooter && data && (
				<>
					<div className="graph-info">
						<span>{data.taskName}</span>
						<span style={{ marginLeft: '15px' }}>{data.operatorName || '-'}</span>
						&nbsp; 发布于
						<span>{formatDateTime(data.gmtCreate)}</span>
						&nbsp;
						<a
							onClick={() => {
								goToTaskDev({ id: data.taskId });
							}}
						>
							查看代码
						</a>
					</div>
				</>
			)}
			<div className="graph-toolbar">
				<Tooltip placement="bottom" title="刷新">
					<ReloadOutlined onClick={refresh} style={{ color: '#333333' }} />
				</Tooltip>
				<Tooltip placement="bottom" title="放大">
					<ZoomInOutlined onClick={() => handleLayoutZoom('in')} style={{ color: '#333333' }} />
				</Tooltip>
				<Tooltip placement="bottom" title="缩小">
					<ZoomOutOutlined onClick={() => handleLayoutZoom('out')} style={{ color: '#333333' }} />
				</Tooltip>
			</div>
		</div>
	);
};

export default TaskGraphView;
