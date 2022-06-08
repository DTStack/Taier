import { useRef } from 'react';
import GraphEditor, { GraphEditorRef } from './graphEditor';
import { cloneDeep } from 'lodash';
import MxFactory from '@/components/mxGraph';
import './linkDiagram.scss';

const Mx = MxFactory.create();
const { mxConstants } = Mx;
const VertexSize: any = {
	// 图形单元大小
	width: 280,
	height: 234,
};
interface ILinkDiagramProps {
	targetKey: string;
	flinkJson?: any[];
	subTreeData?: any[];
	isSubVertex?: boolean;
	loading?: boolean;
	showSubVertex: (data: string) => void;
	refresh?: () => void;
}

const removeString = (str: string) => str.replace(/[\"|\']/g, '');

function removeStringOfName(data) {
	const { backPressureMap, subJobVertices } = data;
	let arr = subJobVertices?.map((item) => {
		return {
			...item,
			name: removeString(item.name),
			backPressureMap,
		};
	});
	return arr;
}

/**
 * 生成 Tree 结构，利用 mxgraph 自带布局方式
 */
function generateToTreeData(arr) {
	let objMap = {};
	let result = null;
	for (let node of arr) {
		node.inputs.forEach((item) => {
			objMap[item] = node;
		});
		if (node.inputs.length === 0) {
			result = node;
		}
	}
	getNodeChildren(result, objMap);
	return result;
}

function getNodeChildren(node, objMap) {
	if (!node) return node;
	node.children = null;
	node.output.forEach((item) => {
		if (!node.children) {
			node.children = [];
		}
		node.children.push(getNodeChildren(objMap[item], objMap));
	});
	return node;
}

export default function LinkDiagram({
	targetKey,
	flinkJson,
	subTreeData,
	isSubVertex,
	showSubVertex,
	loading,
	refresh,
}: ILinkDiagramProps) {
	const graphEditor = useRef<GraphEditorRef>(null);
	const rootCellRef = useRef<string>('');

	const insertOutVertex = (graph: any, parent: any, data: any) => {
		const rootCell = graph.getDefaultParent();
		const style = graphEditor.current!.getStyles();
		let newVertex = '';
		const existCell = getExistCell(graph, data);
		// 已存在的节点则不重新生成
		if (existCell) {
			newVertex = existCell;
		} else {
			newVertex = addVertexInfo(graph, data);
		}
		console.log('newVertex:', newVertex);
		graph.insertEdge(rootCell, null, '', parent, newVertex, style);
		graph.view.refresh(newVertex);
		return newVertex;
	};

	/**
	 * 生成节点并插入相关业务数据
	 */
	const addVertexInfo = (graph: any, data: any) => {
		const { Mx } = graphEditor.current!;
		const rootCell = graph.getDefaultParent();
		const style = graphEditor.current!.getStyles();
		// 创建节点
		const doc = Mx.mxUtils.createXmlDocument();
		const nodeInfo = doc.createElement('table');
		nodeInfo.setAttribute('data', JSON.stringify(Object.assign(data)));
		const newVertex = graph.insertVertex(
			rootCell,
			null,
			nodeInfo,
			20,
			20,
			VertexSize.width,
			VertexSize.height,
			style,
		);
		return newVertex;
	};

	const loopOutTree = (graph: any, currentNode: any, treeNodeData: any) => {
		if (treeNodeData) {
			const childNodes = treeNodeData.children || [];
			childNodes.forEach((item: any) => {
				const nodeData = cloneDeep(item);
				const current = insertOutVertex(graph, currentNode, nodeData);
				if (item.children && item.children.length > 0) {
					loopOutTree(graph, current, item);
				}
			});
		}
	};

	const getExistCell = (graph: any, data?: any) => {
		const rootCell = graph.getDefaultParent();
		const allCells = graph.getChildCells(rootCell);
		for (let cell of allCells) {
			if (cell.vertex) {
				const val = JSON.parse(cell?.value?.getAttribute('data'));
				if (val?.jobVertexId === data?.jobVertexId) {
					return cell;
				}
			}
		}
	};

	/**
	 * 获取当前节点前一个节点
	 */
	const getPrevSourceCell = (graph, newVertex) => {
		const rootCell = graph.getDefaultParent();
		const allCells = graph.getChildCells(rootCell);
		const curIndex = allCells.findIndex((cell) => cell.id === newVertex.id);
		return {
			prevCell: allCells[curIndex - 1],
			curIndex,
		};
	};

	const loopInnerData = (graph: any, data: any, index: number) => {
		const rootCell = graph.getDefaultParent();
		const style = graphEditor.current!.getStyles();
		const newVertex = addVertexInfo(graph, Object.assign(data, { sortIndex: index }));
		// 内层 subVertex 依次生成 edge
		const { prevCell, curIndex } = getPrevSourceCell(graph, newVertex);
		if (curIndex !== 0) {
			// 第一个节点无 prevCell
			setTimeout(() => {
				graph.insertEdge(rootCell, null, '', prevCell, newVertex, style);
			}, 0);
		}
		graph.view.refresh(newVertex);
	};

	const renderTree = (graph: any) => {
		try {
			if (graphEditor.current) {
				const treeNodeData = flinkJson;
				console.log('treeNodeData:', treeNodeData);
				const { executeLayout } = graphEditor.current;
				graph.getModel().clear();
				const rootCell = graph.getDefaultParent();
				const treeData = isSubVertex
					? subTreeData
					: dealMultipleRootNodes(cloneDeep(treeNodeData));
				if (!treeData) return;
				// 外层节点渲染
				const outLayout = () => {
					for (let data of treeData) {
						const currentNodeData = cloneDeep(data);
						const currentNode = insertOutVertex(graph, rootCell, currentNodeData);
						rootCellRef.current = currentNode;
						loopOutTree(graph, currentNode, data);
					}
				};
				// 内层节点渲染
				const innerLayout = () => {
					const treeMap: any = new Map(treeData.map((item, index) => [index, item]));
					for (let [index, item] of treeMap) {
						loopInnerData(graph, item, index);
					}
				};
				const layoutMethod = isSubVertex ? innerLayout() : outLayout();
				if (executeLayout) {
					executeLayout(layoutMethod, () => {
						// graph.scrollCellToVisible(this.rootCell, true);
						graph.center();
					});
				}
			}
		} catch (e) {
			console.log('init graph error~', e);
		}
	};

	const renderLabel = (cell: any) => {
		const { Mx } = graphEditor.current!;
		const { mxUtils } = Mx;
		if (mxUtils.isNode(cell.value)) {
			if (cell.value.nodeName.toLowerCase() == 'table') {
				window.showSubVertex = showSubVertex;
				const data = cell.getAttribute('data');
				const chainData = data ? JSON.parse(data) : '';
				let expandImgStyle =
					'width: 16px; height: 16px; float: right; margin: 10px 11px 0 0;';
				if (chainData) {
					let vertexName = isSubVertex ? chainData.name : chainData.jobVertexName;
					vertexName = removeString(vertexName);
					let expandImgDom = isSubVertex
						? ''
						: `<div><img onclick='showSubVertex(${JSON.stringify(
								removeStringOfName(chainData),
						  )})' src='images/expand.svg' style="${expandImgStyle}" /></div>`;
					// 获取更多指标数据以title形式展示
					const getMoreIndex = (indexData = {}, indexType: string): string => {
						if (indexType === 'Delay') {
							const titleData = Object.entries(indexData);
							let strArr = [];
							Object.entries(indexData).forEach((item, index) => {
								strArr.push(
									`${indexType} ${index + 1}:    ${item[1]}(${item[0]})${
										index != titleData.length - 1 ? '&#13&#13' : ''
									}`,
								);
							});
							return strArr.join('');
						} else {
							const titleData: any[] = Object.values(indexData);
							const titleString = titleData
								.map((item, index) => {
									return `${indexType} ${index + 1}:     ${item}${
										index != titleData.length - 1 ? '&#13&#13' : ''
									}`;
								})
								.join('');
							return titleString;
						}
					};
					const getBackPressureColor = (backPressure: number, type: string) => {
						let color = '';
						if (backPressure >= 0 && backPressure <= 0.1) {
							// 正常
							if (type === 'title') {
								color =
									'linear-gradient(298deg,rgba(90,203,255,1) 0%,rgba(36,145,247,1) 100%)';
							} else {
								color = '#F1FAFF';
							}
						} else if (backPressure >= 0.1 && backPressure <= 0.5) {
							// 低反压
							if (type === 'title') {
								color =
									'linear-gradient(270deg,rgba(255,190,76,1) 0%,rgba(255,160,41,1) 100%)';
							} else {
								color = '#fff9f0';
							}
						} else if (backPressure > 0.5 && backPressure <= 1) {
							// 高反压
							if (type === 'title') {
								color = 'linear-gradient(270deg,#FF5F5C 0%,#ea8785 100%)';
							} else {
								color = '#ff5f5c4d';
							}
						}
						return color;
					};
					// 获取最大反压数据
					const backPressureMap: number[] =
						Object.values(chainData.backPressureMap || {}) || [];
					const maxBackPressure = Math.max(...backPressureMap);
					const loopIndexData = () => {
						const delayData = isSubVertex ? chainData.delayMapList : chainData.delayMap;
						const indexArr = [
							{
								indexName: 'Delay',
								imgSrc: 'images/delay.svg',
								indexTitle: getMoreIndex(delayData, 'Delay') || '',
								indexData: `${Object.entries(delayData)?.[0]?.[1] || 0}ms`,
							},
							{
								indexName: 'Parallelism',
								imgSrc: 'images/parallelism.svg',
								indexTitle: '',
								indexData: chainData.parallelism,
							},
							{
								indexName: 'Record Received',
								imgSrc: 'images/received.svg',
								indexTitle:
									getMoreIndex(chainData.recordsReceivedMap, 'Record Received') ||
									'',
								indexData: isSubVertex
									? Object.values(chainData.recordsReceivedMap)?.[0]
									: chainData.recordsReceived,
							},
							{
								indexName: 'Record Sent',
								imgSrc: 'images/send.svg',
								indexTitle:
									getMoreIndex(chainData.recordsSentMap, 'Record Sent') || '',
								indexData: isSubVertex
									? Object.values(chainData.recordsSentMap)?.[0]
									: chainData.recordsSent,
							},
							{
								indexName: 'BackPressured(max)',
								imgSrc: 'images/dashboard.svg',
								indexTitle: 'BackPressured(max)',
								indexData: (maxBackPressure * 100).toFixed(0) + '%',
							},
						];
						return indexArr
							.map((item) => {
								return `<div class='t-text-col' title="${item.indexTitle}">
                                <span class='t-text-col-key'>
                                    <img src=${item.imgSrc} class='t-text-col_img' />
                                    ${item.indexName}
                                </span>
                                <span class='t-text-col-value'>${item.indexData}</span>
                            </div>`;
							})
							.join('');
					};
					const vertexTitleName = isSubVertex
						? `Operators ${chainData.sortIndex + 1}`
						: `Chain（ ${chainData.subJobVertices?.length || 0} Operators ）`;
					return `<div class='vertex-wrap'>
                        <div class='vertex-title' style='background: ${getBackPressureColor(
							maxBackPressure,
							'title',
						)}'>
                        <div><div class='t_title'>${vertexTitleName}</div></div>
                            ${expandImgDom}
                        </div>
                        <div class='vertex-content' style='background: ${getBackPressureColor(
							maxBackPressure,
							'content',
						)}'>
                            <div class='tcolumn' title="${vertexName}">${vertexName}</div>
                            ${loopIndexData()}
                        </div>
                    </div>`.replace(/(\r\n|\n)/g, '');
				}
			}
		}
	};

	const clearHighlight = () => {
		const { graph } = graphEditor.current!;
		const rootCell = graph.getDefaultParent();
		const allCells = graph.getChildCells(rootCell);
		if (!allCells) return;
		for (let cell of allCells) {
			if (cell.edge) {
				setHighlightStyle(graph, cell, '#95AFC7');
			}
		}
	};

	/**
	 * 处理多个根节点
	 * 生成多条链路
	 */
	const dealMultipleRootNodes = (arr) => {
		const allRootNodes = arr.filter((cell) => cell.inputs.length === 0);
		// 不包含根节点数据
		const notRootNodes = arr.filter((cell) => cell.inputs.length !== 0);
		let allMapArr = [];
		for (let root of allRootNodes) {
			// 每次保留一个根 root 遍历
			allMapArr.push(generateToTreeData([root].concat(notRootNodes)));
		}
		return allMapArr;
	};

	const setHighlightStyle = (graph, cell, color) => {
		const cellState = graph.view.getState(cell);
		const style: any = {};
		const applyCellStyle = (cellState: any, style: any) => {
			if (cellState) {
				cellState.style = Object.assign(cellState.style, style);
				cellState.shape.apply(cellState);
				cellState.shape.redraw();
			}
		};
		style[mxConstants.STYLE_STROKECOLOR] = color;
		applyCellStyle(cellState, style);
	};

	const onClickMaxGraph = (sender: any, evt: any) => {
		const { graph } = graphEditor.current!;
		const cell = evt.getProperty('cell');
		const edges = cell?.edges;
		if (cell && edges) {
			clearHighlight();
			edges.forEach((edge) => {
				setHighlightStyle(graph, edge, '#2491F7');
			});
		} else {
			clearHighlight();
		}
	};

	const loadEditor = (graph, Mx) => {
		const { mxEvent } = Mx;
		graph.getLabel = renderLabel;
		graph.htmlLabels = true;
		graph.addListener(mxEvent.CLICK, onClickMaxGraph);

		renderTree(graph);
	};

	return (
		<div className="tableRelation_graph" id={targetKey}>
			{flinkJson?.length === 0 ? (
				<span className="graph-text__center">暂未生成拓扑图</span>
			) : (
				<GraphEditor
					ref={graphEditor}
					targetKey={targetKey}
					loading={loading}
					isSubVertex={isSubVertex}
					refresh={refresh}
					onInit={loadEditor}
				/>
			)}
		</div>
	);
}
