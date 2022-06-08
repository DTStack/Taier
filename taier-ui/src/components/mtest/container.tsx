import { ReloadOutlined, ZoomInOutlined, ZoomOutOutlined } from '@ant-design/icons';
import { Spin, Tooltip } from 'antd';
import { useEffect, useRef, useState } from 'react';
import MxFactory from '.';
import './container.scss';

const Mx = new MxFactory();

const {
	mxHierarchicalLayout: MxHierarchicalLayout,
	mxCellHighlight: MxCellHighlight,
	mxEdgeStyle,
	mxEvent,
	mxPopupMenu,
	mxEventObject: MxEventObject,
	mxRectangle,
} = Mx.mxInstance;

export interface IContextMenuConfig {
	title: string;
	callback?: () => void;
	children?: IContextMenuConfig[];
	disabled?: boolean;
}

interface IMxGraphData {
	childNode: any[];
	parentNode?: any[];
	isCollapsed?: boolean;

	[key: string]: any;
}

interface IContainerProps<T> {
	/**
	 * 加载中状态
	 */
	loading?: boolean;
	/**
	 * 当前图数据
	 */
	graphData?: T[] | null;
	/**
	 * vertex 的 key 值，默认是 taskId
	 */
	vertexKey?: string;
	/**
	 * Vertex 尺寸，默认宽度 210， 默认高度 50,
	 */
	vertextSize?: { width?: number; height?: number };
	/**
	 * 配置项目
	 */
	config?: { tooltips: boolean; [key: string]: any };
	/**
	 * children 会渲染底部状态栏
	 */
	children?: (current: T | null) => JSX.Element;
	/**
	 * 点击刷新的回调函数
	 */
	onRefresh?: (graph: IMxGraph) => void;
	/**
	 * 渲染 cell 的内容，返回 string 类型
	 */
	onRenderCell?: (cell: IMxCell<T>, graph: IMxGraph) => string;
	/**
	 * 获取 vertex 的 style，由于存在默认样式，所以通常用于设置特殊状态的 vertex
	 */
	onDrawVertex?: (data: T) => string;
	/**
	 * Vertex 的点击回调函数
	 */
	onClick?: (
		cell: IMxCell<T>,
		graph: IMxGraph,
		event: React.MouseEvent<HTMLElement, MouseEvent>,
	) => void;
	/**
	 * 右键菜单的回调函数
	 */
	onContextMenu?: (data: T) => IContextMenuConfig[] | Promise<IContextMenuConfig[]>;
	/**
	 * Vertex 的双击回调事件
	 */
	onDoubleClick?: (data: T) => void;
}

enum ZoomKind {
	In,
	Out,
}

export default function MxGraphContainer<T extends IMxGraphData>({
	loading,
	graphData,
	vertexKey = 'taskId',
	vertextSize,
	config,
	children,
	onRefresh,
	onRenderCell,
	onDrawVertex,
	onClick,
	onContextMenu,
	onDoubleClick,
}: IContainerProps<T>) {
	const container = useRef<HTMLDivElement>(null);
	const graph = useRef<IMxGraph>();
	const graphView = useRef<
		| undefined
		| {
				scale: number;
				dx: number;
				dy: number;
		  }
	>(undefined);
	const [current, setCurrent] = useState<null | T>(null);

	const handleRefresh = () => {
		onRefresh?.(graph.current!);
		handleSaveView();
	};

	const handleLayoutZoom = (operator: ZoomKind) => {
		switch (operator) {
			case ZoomKind.In:
				graph.current?.zoomIn();
				break;
			case ZoomKind.Out:
				graph.current?.zoomOut();
				break;
			default:
				break;
		}
	};

	const initGraph = () => {
		graph.current = Mx.create(container.current!, config);
		Mx.createRubberBand();
		// 转换value显示的内容
		Mx.renderVertex<T>((cell) => {
			return onRenderCell?.(cell, graph.current!) || '';
		});

		Mx.layoutEventHandler = () => {
			const parent = graph.current!.getDefaultParent();
			graph.current!.getModel().beginUpdate();
			try {
				const layout2 = new MxHierarchicalLayout(graph.current, 'north');
				layout2.disableEdgeStyle = false;
				layout2.interRankCellSpacing = 40;
				layout2.intraCellSpacing = 60;
				layout2.edgeStyle = mxEdgeStyle.TopToBottom;
				layout2.execute(parent);
			} finally {
				graph.current!.getModel().endUpdate();
			}
		};

		// Init container scroll
		Mx.initContainerScroll();
	};

	const initEvent = () => {
		const highlightEdges: IMxCellHighlight[] = [];
		// Click 事件
		graph.current?.addListener(mxEvent.CLICK, (_, evt) => {
			const cell: IMxCell<T> = evt.getProperty('cell');
			setCurrent(cell?.value || null);
			highlightEdges.forEach((e) => e.destroy());

			if (cell && cell.vertex) {
				// highlight cells and edges
				const outEdges = graph.current?.getOutgoingEdges(cell) || [];
				const inEdges = graph.current?.getIncomingEdges(cell) || [];
				const edges = outEdges.concat(inEdges);
				for (let i = 0; i < edges.length; i += 1) {
					const highlight = new MxCellHighlight(graph.current, '#2491F7', 2);
					const state = graph.current?.view.getState(edges[i]);
					highlight.highlight(state);
					highlightEdges.push(highlight);
				}

				onClick?.(cell, graph.current!, evt.getProperty('event'));
			} else {
				const cells = graph.current?.getSelectionCells();
				graph.current?.removeSelectionCells(cells);
			}
		});

		graph.current?.addListener(mxEvent.DOUBLE_CLICK, (_, evt) => {
			const cell: IMxCell<T> = evt.getProperty('cell');
			if (cell && cell.vertex) {
				onDoubleClick?.(cell.value!);
			}
		});

		// ContextMenu 事件
		const mxPopupMenuShowMenu = mxPopupMenu.prototype.showMenu;
		// Only vertex could show contextMenu
		mxPopupMenu.prototype.showMenu = function (this: { graph: IMxGraph }) {
			const cells = this.graph.getSelectionCells() || [];
			if (cells.length > 0 && cells[0].vertex) {
				// eslint-disable-next-line prefer-rest-params
				mxPopupMenuShowMenu.apply(this, arguments);
			} else return false;
		};
		graph.current!.popupMenuHandler.autoExpand = true;

		// change it to for supporting async factoryMethod
		mxPopupMenu.prototype.popup = async function (
			x: number,
			y: number,
			cell: IMxCell,
			evt: IMxEventSource,
		) {
			if (this.div != null && this.tbody != null && this.factoryMethod != null) {
				this.div.style.left = `${x}px`;
				this.div.style.top = `${y}px`;

				// Removes all child nodes from the existing menu
				while (this.tbody.firstChild != null) {
					mxEvent.release(this.tbody.firstChild);
					this.tbody.removeChild(this.tbody.firstChild);
				}

				this.itemCount = 0;
				await this.factoryMethod(this, cell, evt);

				if (this.itemCount > 0) {
					this.showMenu();
					this.fireEvent(new MxEventObject(mxEvent.SHOW));
				}
			}
		};

		// Reset collapsed image
		graph.current!.collapsedImage = null;

		graph.current!.popupMenuHandler.factoryMethod = async (
			menu: IMxEventSource,
			cell: IMxCell<T>,
		) => {
			if (!cell || !cell.vertex) return;

			await Promise.resolve(onContextMenu?.(cell.value!)).then((payloads) => {
				payloads?.forEach(({ title, disabled, children: subMenu, callback }) => {
					const parent = menu.addItem(title, null, callback, null, null, !disabled);
					// 暂时先支持两层菜单
					if (subMenu?.length) {
						subMenu.forEach((child) => {
							menu.addItem(
								child.title,
								null,
								child.callback,
								parent,
								null,
								!child.disabled,
							);
						});
					}
				});
			});
		};
	};

	const initData = () => {
		if (graphData) {
			if (graphData.length === 1) {
				// default to select the only one graphData
				setCurrent(graphData[0]);
			}

			const stack: { sourceOrTarget: IMxCell<T> | null; data: T }[] = graphData.map((d) => ({
				sourceOrTarget: null,
				data: d,
			}));

			while (stack.length) {
				const { sourceOrTarget, data } = stack.pop()!;
				const style = onDrawVertex?.(data);

				// 如果是折叠的数据(拓扑图)
				if (data.isCollapsed) {
					const vertex = graph.current!.insertVertex(
						graph.current!.getDefaultParent(),
						data[vertexKey],
						data,
						0,
						0,
						vertextSize?.width || MxFactory.VertexSize.width,
						vertextSize?.height || MxFactory.VertexSize.height,
						style,
					);

					vertex.geometry.alternateBounds = new mxRectangle(0, 0, 1000, 550);

					vertex.collapsed = true;
					data.childNode.forEach(
						function (this: { source: IMxCell | null }, child, index) {
							const target = graph.current!.insertVertex(
								vertex,
								child[vertexKey],
								child,
								0,
								0,
								vertextSize?.width || MxFactory.VertexSize.width,
								vertextSize?.height || MxFactory.VertexSize.height,
								style,
							);

							if (this.source) {
								graph.current!.insertEdge(
									vertex,
									null,
									null,
									this.source,
									target,
									null,
								);
							}

							this.source = target;
						},
						{
							source: null,
						},
					);
				} else {
					const vertex = graph.current!.insertVertex(
						graph.current!.getDefaultParent(),
						data[vertexKey],
						data,
						0,
						0,
						vertextSize?.width || MxFactory.VertexSize.width,
						vertextSize?.height || MxFactory.VertexSize.height,
						style,
					);

					if (sourceOrTarget) {
						// 判断 sourceOrTarget 存放的 vertex 是 source 还是 target
						const isSource = !!sourceOrTarget.value?.childNode?.find(
							(i: T) => i[vertexKey] === data[vertexKey],
						);
						graph.current!.insertEdge(
							graph.current!.getDefaultParent(),
							null,
							null,
							isSource ? sourceOrTarget : vertex,
							isSource ? vertex : sourceOrTarget,
							null,
						);
					} else {
						graph.current?.setSelectionCell(vertex);
					}

					if (data.childNode?.length) {
						data.childNode.forEach((i: T) => {
							stack.push({
								sourceOrTarget: vertex,
								data: i,
							});
						});
					}

					if (data.parentNode?.length) {
						data.parentNode.forEach((i: T) => {
							stack.push({
								sourceOrTarget: vertex,
								data: i,
							});
						});
					}
				}
			}

			Mx.layoutEventHandler?.();
			restoreView();
		}
	};

	const restoreView = () => {
		if (graphView.current) {
			Mx.setView({
				scale: graphView.current.scale,
				dx: graphView.current.dx,
				dy: graphView.current.dy,
			});
		}

		// Sets initial scrollbar positions
		window.setTimeout(() => {
			Mx.resetScrollPosition();
		}, 0);
	};

	const handleSaveView = () => {
		const view = graph.current?.getView();
		if (view) {
			const translate = view.getTranslate();
			if (translate.x > 0) {
				graphView.current = {
					dx: translate.x,
					dy: translate.y,
					scale: view.getScale(),
				};
			}
		}
	};

	useEffect(() => {
		initGraph();
		initEvent();
		initData();

		return () => {
			Mx.dispose();
		};
	}, [graphData]);

	return (
		<div className="graph-editor">
			<Spin tip="Loading..." size="large" spinning={loading} wrapperClassName="task-graph">
				<div
					style={{
						position: 'relative',
						overflow: 'auto',
						width: '100%',
						height: '100%',
					}}
					ref={container}
				/>
			</Spin>
			<div className="graph-bottom">{children?.(current)}</div>
			<div className="graph-toolbar">
				<Tooltip placement="bottom" title="刷新">
					<ReloadOutlined onClick={handleRefresh} />
				</Tooltip>
				<Tooltip placement="bottom" title="放大">
					<ZoomInOutlined onClick={() => handleLayoutZoom(ZoomKind.In)} />
				</Tooltip>
				<Tooltip placement="bottom" title="缩小">
					<ZoomOutOutlined onClick={() => handleLayoutZoom(ZoomKind.Out)} />
				</Tooltip>
			</div>
		</div>
	);
}
