import * as React from 'react';
import GraphEditor from './graphEditor';
import { cloneDeep, isEqual } from 'lodash';
import MxFactory from '@/components/mxGraph';
import './linkDiagram.scss';

const Mx = MxFactory.create();
const { mxConstants } = Mx;
const VertexSize: any = { // 图形单元大小
    width: 280,
    height: 234
}
declare var window: Window & { showSubVertex: Function }
type DataKeys = 'flinkJson' | 'subTreeData';
type AnyArray = {
    [key in DataKeys]?: any[]
}

interface Props {
    showSubVertex: Function;
    targetKey?: string;
    isSubVertex?: boolean;
    loading?: boolean;

    [propName: string]: any;
}

interface State {
    subTreeData: any[];
    treeData: any;
    visible: boolean;
}

export default class LinkDiagram extends React.Component<Props & AnyArray, State> {
    constructor (props: Props & AnyArray) {
        super(props);
        this.state = {
            visible: false,
            treeData: {}, // 树形数据
            subTreeData: this.props.subTreeData // 内层 subVertex
        }
    }

    rootCell: any;
    GraphEditor: any;

    componentDidMount () {
        this.loadTree();
    }

    UNSAFE_componentWillReceiveProps (nextProps) {
        const { subTreeData, flinkJson } = nextProps;
        if (this.props.subTreeData != subTreeData) {
            this.setState({ subTreeData }, this.loadTree)
        }
        if (!isEqual(this.props.flinkJson, nextProps.flinkJson) && flinkJson?.length !== 0) {
            this.loadTree()
        }
    }

    loadTree = () => {
        setTimeout(() => {
            this.renderTree(this.props.flinkJson)
        }, 500);
    }
    getExistCell = (data?: any) => {
        const { graph } = this.GraphEditor;
        const rootCell = graph.getDefaultParent();
        const allCells = graph.getChildCells(rootCell);
        for (let cell of allCells) {
            if (cell.vertex) {
                const val = JSON.parse(cell?.value?.getAttribute('data'));
                if (val?.jobVertexId === data?.jobVertexId) {
                    return cell
                }
            }
        }
    }
    /**
     * 生成节点并插入相关业务数据
     */
    addVertexInfo = (data: any) => {
        const { graph, Mx } = this.GraphEditor;
        const rootCell = graph.getDefaultParent();
        const style = this.GraphEditor.getStyles();
        // 创建节点
        const doc = Mx.mxUtils.createXmlDocument()
        const nodeInfo = doc.createElement('table')
        nodeInfo.setAttribute('data', JSON.stringify(Object.assign(data)))
        const newVertex = graph.insertVertex(rootCell, null,
            nodeInfo, 20, 20,
            VertexSize.width, VertexSize.height, style
        )
        return newVertex
    }
    insertOutVertex = (parent: any, data: any) => {
        const { graph } = this.GraphEditor;
        const rootCell = graph.getDefaultParent();
        const style = this.GraphEditor.getStyles(data);
        let newVertex = ''
        const existCell = this.getExistCell(data)
        // 已存在的节点则不重新生成
        if (existCell) {
            newVertex = existCell
        } else {
            newVertex = this.addVertexInfo(data)
        }
        graph.insertEdge(rootCell, null, '', parent, newVertex, style);
        graph.view.refresh(newVertex);
        return newVertex;
    }

    loopOutTree = (currentNode: any, treeNodeData: any) => {
        if (treeNodeData) {
            const childNodes = treeNodeData.children || [];
            childNodes.forEach((item: any, index: number) => {
                const nodeData = cloneDeep(item)
                const current = this.insertOutVertex(currentNode, nodeData);
                if (item.children && item.children.length > 0) {
                    this.loopOutTree(current, item)
                }
            });
        }
    }
    getNodeChildren = (node, objMap) => {
        if (!node) return node
        node.children = null
        node.output.forEach((item) => {
            if (!node.children) {
                node.children = []
            }
            node.children.push(this.getNodeChildren(objMap[item], objMap))
        })
        return node
    }
    /**
     * 生成 Tree 结构，利用 mxgraph 自带布局方式
     */
    generateToTreeData = (arr) => {
        let objMap = {}
        let result = null
        for (let node of arr) {
            node.inputs.forEach((item) => {
                objMap[item] = node
            })
            if (node.inputs.length === 0) {
                result = node
            }
        }
        this.getNodeChildren(result, objMap)
        return result
    }
    /**
     * 处理多个根节点
     * 生成多条链路
     */
    dealMultipleRootNodes = (arr) => {
        const allRootNodes = arr.filter(cell => cell.inputs.length === 0);
        // 不包含根节点数据
        const notRootNodes = arr.filter(cell => cell.inputs.length !== 0);
        let allMapArr = []
        for (let root of allRootNodes) {
            // 每次保留一个根 root 遍历
            allMapArr.push(this.generateToTreeData([root].concat(notRootNodes)))
        }
        return allMapArr
    }
    renderTree = (treeNodeData: any[]) => {
        const { isSubVertex } = this.props;
        const { subTreeData } = this.state;
        try {
            const { executeLayout, graph } = this.GraphEditor;
            graph.getModel().clear();
            const rootCell = graph.getDefaultParent();
            const treeData = isSubVertex ? subTreeData : this.dealMultipleRootNodes(cloneDeep(treeNodeData))
            if (!treeData) return;
            // 外层节点渲染
            const outLayout = () => {
                for (let data of treeData) {
                    const currentNodeData = cloneDeep(data);
                    const currentNode = this.insertOutVertex(rootCell, currentNodeData);
                    this.rootCell = currentNode;
                    this.loopOutTree(currentNode, data);
                }
            }
            // 内层节点渲染
            const innerLayout = () => {
                const treeMap: any = new Map(treeData.map((item, index) => [index, item]))
                for (let [index, item] of treeMap) {
                    this.loopInnerData(item, index)
                }
            }
            const layoutMethod = isSubVertex ? innerLayout() : outLayout();
            if (executeLayout) {
                executeLayout(layoutMethod, () => {
                    // graph.scrollCellToVisible(this.rootCell, true);
                    graph.center();
                });
            }
        } catch (e) {
            console.log('init graph error~', e)
        }
    }
    /**
     * 获取当前节点前一个节点
     */
    getPrevSourceCell = (newVertex) => {
        const { graph } = this.GraphEditor;
        const rootCell = graph.getDefaultParent();
        const allCells = graph.getChildCells(rootCell);
        const curIndex = allCells.findIndex(cell => cell.id === newVertex.id);
        return {
            prevCell: allCells[curIndex - 1],
            curIndex
        }
    }
    loopInnerData = (data: any, index: number) => {
        const { graph } = this.GraphEditor;
        const rootCell = graph.getDefaultParent();
        const style = this.GraphEditor.getStyles();
        const newVertex = this.addVertexInfo(Object.assign(data, { sortIndex: index }))
        // 内层 subVertex 依次生成 edge
        const { prevCell, curIndex } = this.getPrevSourceCell(newVertex)
        if (curIndex !== 0) { // 第一个节点无 prevCell
            setTimeout(() => {
                graph.insertEdge(rootCell, null, '', prevCell, newVertex, style);
            }, 0)
        }
        graph.view.refresh(newVertex);
    }

    loadEditor = (graph, Mx) => {
        const { mxEvent } = Mx
        graph.getLabel = this.renderLabel;
        graph.htmlLabels = true
        graph.addListener(mxEvent.CLICK, this.onClickMaxGraph);
    }
    setHighlightStyle = (graph, cell, color) => {
        const cellState = graph.view.getState(cell);
        const style: any = {}
        const applyCellStyle = (cellState: any, style: any) => {
            if (cellState) {
                cellState.style = Object.assign(cellState.style, style);
                cellState.shape.apply(cellState);
                cellState.shape.redraw();
            }
        }
        style[mxConstants.STYLE_STROKECOLOR] = color;
        applyCellStyle(cellState, style);
    }
    clearHighlight = () => {
        const { graph } = this.GraphEditor;
        const rootCell = graph.getDefaultParent();
        const allCells = graph.getChildCells(rootCell);
        if (!allCells) return;
        for (let cell of allCells) {
            if (cell.edge) {
                this.setHighlightStyle(graph, cell, '#95AFC7')
            }
        }
    }
    onClickMaxGraph = (sender: any, evt: any) => {
        const { graph } = this.GraphEditor;
        const cell = evt.getProperty('cell')
        const edges = cell?.edges;
        if (cell && edges) {
            this.clearHighlight()
            for (let edge of edges) {
                this.setHighlightStyle(graph, edge, '#2491F7')
            }
        } else {
            this.clearHighlight()
        }
    }
    removeString = (str: string) => {
        // eslint-disable-next-line
        return str.replace(/[\"|\']/g, '')
    }
    removeStringOfName = (data) => {
        const { backPressureMap, subJobVertices } = data;
        let arr = subJobVertices?.map(item => {
            return {
                ...item,
                name: this.removeString(item.name),
                backPressureMap
            }
        })
        return arr
    }
    renderLabel = (cell: any) => {
        const { Mx } = this.GraphEditor;
        const { showSubVertex, isSubVertex } = this.props;
        const { mxUtils } = Mx;
        if (mxUtils.isNode(cell.value)) {
            if (cell.value.nodeName.toLowerCase() == 'table') {
                window.showSubVertex = showSubVertex;
                const data = cell.getAttribute('data');
                const chainData = data ? JSON.parse(data) : '';
                let expandImgStyle = 'width: 16px; height: 16px; float: right; margin: 10px 11px 0 0;';
                if (chainData) {
                    let vertexName = isSubVertex ? chainData.name : chainData.jobVertexName;
                    vertexName = this.removeString(vertexName);
                    let expandImgDom = isSubVertex ? '' : `<div><img onclick='showSubVertex(${JSON.stringify(this.removeStringOfName(chainData))})' src='public/img/expand.svg' style="${expandImgStyle}" /></div>`;
                    // 获取更多指标数据以title形式展示
                    const getMoreIndex = (indexData = {}, indexType: string): string => {
                        if (indexType === 'Delay') {
                            const titleData = Object.entries(indexData)
                            let strArr = []
                            Object.entries(indexData).forEach((item, index) => {
                                strArr.push(`${indexType} ${index + 1}:    ${item[1]}(${item[0]})${index != titleData.length - 1 ? '&#13&#13' : ''}`)
                            })
                            return strArr.join('')
                        } else {
                            const titleData: any[] = Object.values(indexData);
                            const titleString = titleData.map((item, index) => {
                                return `${indexType} ${index + 1}:     ${item}${index != titleData.length - 1 ? '&#13&#13' : ''}`
                            }).join('')
                            return titleString
                        }
                    }
                    const getBackPressureColor = (backPressure: number, type: string) => {
                        let color = ''
                        if (backPressure >= 0 && backPressure <= 0.1) { // 正常
                            if (type === 'title') {
                                color = 'linear-gradient(298deg,rgba(90,203,255,1) 0%,rgba(36,145,247,1) 100%)'
                            } else {
                                color = '#F1FAFF'
                            }
                        } else if (backPressure >= 0.1 && backPressure <= 0.5) { // 低反压
                            if (type === 'title') {
                                color = 'linear-gradient(270deg,rgba(255,190,76,1) 0%,rgba(255,160,41,1) 100%)'
                            } else {
                                color = '#fff9f0'
                            }
                        } else if (backPressure > 0.5 && backPressure <= 1) { // 高反压
                            if (type === 'title') {
                                color = 'linear-gradient(270deg,#FF5F5C 0%,#ea8785 100%)'
                            } else {
                                color = '#ff5f5c4d'
                            }
                        }
                        return color
                    }
                    // 获取最大反压数据
                    const backPressureMap: number[] = Object.values(chainData.backPressureMap || {}) || []
                    const maxBackPressure = Math.max(...backPressureMap)
                    const loopIndexData = () => {
                        const delayData = isSubVertex ? chainData.delayMapList : chainData.delayMap
                        const indexArr = [{
                            indexName: 'Delay',
                            imgSrc: 'public/img/delay.svg',
                            indexTitle: getMoreIndex(delayData, 'Delay') || '',
                            indexData: `${Object.entries(delayData)?.[0]?.[1] || 0}ms`
                        }, {
                            indexName: 'Parallelism',
                            imgSrc: 'public/img/parallelism.svg',
                            indexTitle: '',
                            indexData: chainData.parallelism
                        }, {
                            indexName: 'Record Received',
                            imgSrc: 'public/img/received.svg',
                            indexTitle: getMoreIndex(chainData.recordsReceivedMap, 'Record Received') || '',
                            indexData: isSubVertex ? Object.values(chainData.recordsReceivedMap)?.[0] : chainData.recordsReceived
                        }, {
                            indexName: 'Record Sent',
                            imgSrc: 'public/img/send.svg',
                            indexTitle: getMoreIndex(chainData.recordsSentMap, 'Record Sent') || '',
                            indexData: isSubVertex ? Object.values(chainData.recordsSentMap)?.[0] : chainData.recordsSent
                        }, {
                            indexName: 'BackPressured(max)',
                            imgSrc: 'public/img/dashboard.svg',
                            indexTitle: 'BackPressured(max)',
                            indexData: (maxBackPressure * 100).toFixed(0) + '%'
                        }]
                        return indexArr.map(item => {
                            return `<div class='t-text-col' title="${item.indexTitle}">
                                <span class='t-text-col-key'>
                                    <img src=${item.imgSrc} class='t-text-col_img' />
                                    ${item.indexName}
                                </span>
                                <span class='t-text-col-value'>${item.indexData}</span>
                            </div>`
                        }).join('')
                    }
                    const vertexTitleName = isSubVertex ? `Operators ${chainData.sortIndex + 1}` : `Chain（ ${chainData.subJobVertices?.length || 0} Operators ）`;
                    return `<div class='vertex-wrap'>
                        <div class='vertex-title' style='background: ${getBackPressureColor(maxBackPressure, 'title')}'>
                        <div><div class='t_title'>${vertexTitleName}</div></div>
                            ${expandImgDom}
                        </div>
                        <div class='vertex-content' style='background: ${getBackPressureColor(maxBackPressure, 'content')}'>
                            <div class='tcolumn' title="${vertexName}">${vertexName}</div>
                            ${loopIndexData()}
                        </div>
                    </div>`.replace(/(\r\n|\n)/g, '');
                }
            }
        }
    }

    render () {
        const { targetKey, flinkJson } = this.props;

        return (
            <div className="tableRelation_graph" id={`${targetKey + 1}`}>
                {
                    flinkJson?.length === 0 ? <span className="graph-text__center">暂未生成拓扑图</span> : <GraphEditor
                        {...this.props}
                        ref={(node) => this.GraphEditor = node}
                        rootCell={this.rootCell}
                        onInit={this.loadEditor}
                    />
                }
            </div>
        )
    }
}
