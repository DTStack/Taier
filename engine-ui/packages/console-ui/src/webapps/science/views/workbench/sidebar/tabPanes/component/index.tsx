import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { union, cloneDeep } from 'lodash';

import Loading from '../loading';
import FolderTree from '../../../../../components/folderTree';
import * as fileTreeActions from '../../../../../actions/base/fileTree';
import * as experimentActions from '../../../../../actions/experimentActions';
import MxFactory from 'widgets/mxGraph';
import { siderBarType, VertexSize, TASK_ENUM, COMPONENT_TYPE } from '../../../../../consts';
import api from '../../../../../api/experiment';

const Mx = MxFactory.create();
const {
    mxUtils,
    mxEvent,
    mxCell,
    mxGeometry,
    mxDragSource
} = Mx;
// const Search = Input.Search;

@(connect(
    (state: any) => {
        const tab = state.experiment.localTabs.find((o: any) => o.id == state.experiment.currentTabIndex);
        const graph = state.component.graph[state.experiment.currentTabIndex] || {};
        return {
            routing: state.routing,
            files: state.component.files,
            graph: graph,
            running: state.editor.running,
            currentTabIndex: state.experiment.currentTabIndex,
            tabData: tab
        }
    },
    (dispatch: any) => {
        const actions = bindActionCreators({ ...fileTreeActions, ...experimentActions }, dispatch);
        return actions;
    }) as any)
class ComponentSidebar extends React.Component<any, any> {
    constructor (props: any) {
        super(props)
    }
    _dragElements: any[] = [];
    state: any = {
        expandedKeys: []
    }
    componentDidMount () {
        this.initDragEvent();
        this.initDragger();
    }
    initDragEvent = () => {
        mxDragSource.prototype.reset = function () {
            if (this.currentGraph != null) {
                this.dragExit(this.currentGraph);
                this.currentGraph = null;
            }
            mxEvent.removeAllListeners(this.element);
            this.element = null;
            this.removeDragElement();
            this.removeListeners();
            this.stopDrag();
        };
    }
    componentDidUpdate (prevProps: any, prevState: any) {
        this.initDragger();
    }
    // Returns the graph under the mouse
    graphF = (evt: any) => {
        const { graph = {} } = this.props;
        const x = mxEvent.getClientX(evt);
        const y = mxEvent.getClientY(evt);
        const elt = document.elementFromPoint(x, y);
        if (mxUtils.isAncestorNode(graph.container, elt)) {
            return graph;
        }
        return null;
    };
    // Creates the element that is being for the actual preview.
    initDragElement = () => {
        var dragElt = document.createElement('div');
        dragElt.style.border = 'solid #90d5ff 1px';
        dragElt.style.width = VertexSize.width + 'px';
        dragElt.style.height = VertexSize.height + 'px';
        return dragElt;
    }
    initDragger = () => {
        const fileNodes = document.querySelectorAll('.anchor-component-file .ant-tree-node-content-wrapper');
        const { graph = {}, files, currentTabIndex } = this.props;
        const ctx = this;
        const findNameInLoop = (name: any, arr: any): any => {
            if (!name) return false;
            for (let index = 0; index < arr.length; index++) {
                const element = arr[index];
                if (element.children) {
                    if (findNameInLoop(name, element.children)) {
                        return findNameInLoop(name, element.children)
                    }
                } else {
                    if (element.name == name) {
                        return element;
                    }
                }
            }
        }
        this._dragElements.map((item: any) => {
            item.reset();
        })
        this._dragElements = [];
        // Inserts a cell at the given location
        const funct = function (graph: any, evt: any, target: any, x: any, y: any) {
            const data = this.sourceData;
            const isRunning = ctx.props.running.indexOf(String(currentTabIndex)) > -1;
            if (isRunning) {
                // 如果正在运行，无法拖进组件
                return;
            }

            const params: any = {
                taskType: data.taskType,
                componentType: data.componentType,
                flowId: currentTabIndex,
                [TASK_ENUM[data.componentType]]: {}
            }
            api.addOrUpdateTask(params).then(async (res: any) => {
                if (res.code === 1) {
                    /**
                     * 上面那个接口没有返回重要的组件信息
                     * 下面这个请求返回了组件的信息
                     */
                    let response = await api.getExperimentTask({ id: res.data.id });
                    if (response.code === 1) {
                        // eslint-disable-next-line new-cap
                        let cell = new mxCell('', new mxGeometry(x, y, VertexSize.width, VertexSize.height));
                        cell.data = ctx.handleSimplify(response.data);
                        cell.vertex = true;
                        let cells = graph.importCells([cell], 0, 0, target);
                        cell = ctx.getCellData(cells[0]);
                        /**
                         * 拉进来之后保存一下
                         */
                        const tabData = cloneDeep(ctx.props.tabData);
                        tabData.graphData.push(cell);
                        ctx.props.saveExperiment(tabData, false)
                        graph.clearSelection();
                    }
                }
            })
        };
        fileNodes.forEach((element: any) => {
            const title = element.getElementsByClassName('ant-tree-title')[0].children[0].innerText;
            let md = mxUtils.makeDraggable(element, this.graphF, funct, this.initDragElement(), null, null, graph.autoscroll, true);
            md.sourceData = findNameInLoop(title, files) ? findNameInLoop(title, files) : null;
            md.isGuidesEnabled = function () {
                return graph.graphHandler.guidesEnabled;
            };
            md.createDragElement = mxDragSource.prototype.createDragElement;
            this._dragElements.push(md);
        })
    }
    /* 精简掉一些没用的属性 */
    handleSimplify = (object = {}) => {
        if (Object.keys(object).length === 0) {
            return {};
        }
        const copyObject = cloneDeep(object);
        const deleteAttr = (obj: any, attr: any) => {
            delete obj[attr]
        }
        deleteAttr(copyObject, 'isDeleted')
        deleteAttr(copyObject, 'tenantId')
        deleteAttr(copyObject, 'engineType')
        deleteAttr(copyObject, 'taskParams')
        deleteAttr(copyObject, 'exeArgs')
        deleteAttr(copyObject, 'targetId')
        deleteAttr(copyObject, 'componentStatus')
        deleteAttr(copyObject, 'nodePName')
        deleteAttr(copyObject, 'readWriteLockVO')
        deleteAttr(copyObject, 'cron')
        deleteAttr(copyObject, 'scheduleConf')
        return copyObject;
    }
    getCellData = (cell: any) => {
        return cell && {
            vertex: cell.vertex,
            edge: cell.edge,
            data: cell.data,
            x: cell.geometry.x,
            y: cell.geometry.y,
            value: cell.value,
            id: cell.id,
            style: cell.style
        }
    }
    onExpand = (expandedKeys: any[], { expanded }: { expanded: any }) => {
        let keys = expandedKeys;
        if (expanded) {
            keys = union(this.state.expandedKeys, keys)
        }
        this.setState({
            expandedKeys: keys
        })
    }
    asynLoadCatalogue = (treeNode: any) => {
        return this.props.loadTreeData(siderBarType.component, treeNode.props.data.id)
    }
    renderFolderContent = () => {
        const {
            files
        } = this.props;
        return (
            <div>
                {files.length ? (
                    <FolderTree
                        loadData={this.asynLoadCatalogue}
                        onExpand={this.onExpand}
                        expandedKeys={this.state.expandedKeys}
                        treeData={files}
                        nodeClass={(item: any) => {
                            switch (item.componentType) {
                                case COMPONENT_TYPE.DATA_SOURCE.WRITE_DATABASE:
                                case COMPONENT_TYPE.DATA_SOURCE.READ_DATABASE: {
                                    return `anchor-component-file o-tree-icon--data-source`
                                }
                                case COMPONENT_TYPE.DATA_TOOLS.SQL_SCRIPT: {
                                    return 'anchor-component-file o-tree-icon--data-tools'
                                }
                                case COMPONENT_TYPE.DATA_MERGE.TYPE_CHANGE:
                                case COMPONENT_TYPE.DATA_MERGE.NORMALIZE: {
                                    return 'anchor-component-file o-tree-icon--data-merge'
                                }
                                case COMPONENT_TYPE.DATA_MERGE.STANDARD: {
                                    return 'anchor-component-file o-tree-icon--data-merge-standard'
                                }
                                case COMPONENT_TYPE.DATA_MERGE.MISS_VALUE: {
                                    return 'anchor-component-file o-tree-icon--data-merge-missval'
                                }
                                case COMPONENT_TYPE.DATA_PRE_HAND.DATA_SPLIT: {
                                    return 'anchor-component-file o-tree-icon--data-pre-hand'
                                }
                                case COMPONENT_TYPE.MACHINE_LEARNING.LOGISTIC_REGRESSION: {
                                    return 'anchor-component-file o-tree-icon--machine-learning'
                                }
                                case COMPONENT_TYPE.MACHINE_LEARNING.GBDT_REGRESSION: {
                                    return 'anchor-component-file o-tree-icon--machine-gbdt'
                                }
                                case COMPONENT_TYPE.MACHINE_LEARNING.KMEANS_UNION: {
                                    return 'anchor-component-file o-tree-icon--machine-kmeans'
                                }
                                case COMPONENT_TYPE.MACHINE_LEARNING.GBDT_CLASS: {
                                    return 'anchor-component-file o-tree-icon--machine-gbdt'
                                }
                                case COMPONENT_TYPE.MACHINE_LEARNING.SVM: {
                                    return 'anchor-component-file o-tree-icon--machine-svm'
                                }
                                case COMPONENT_TYPE.DATA_PREDICT.DATA_PREDICT: {
                                    return 'anchor-component-file o-tree-icon--data-predict'
                                }
                                case COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION: {
                                    return 'anchor-component-file o-tree-icon--data-evaluate'
                                }
                                case COMPONENT_TYPE.DATA_EVALUATE.REGRESSION_CLASSIFICATION: {
                                    return 'anchor-component-file o-tree-icon--data-rc'
                                }
                                case COMPONENT_TYPE.DATA_EVALUATE.UNION_CLASSIFICATION: {
                                    return 'anchor-component-file o-tree-icon--data-um'
                                }
                                case COMPONENT_TYPE.DATA_EVALUATE.CONFUSION_MATRIX: {
                                    return 'anchor-component-file o-tree-icon--confusion-matrix'
                                }
                                case COMPONENT_TYPE.FEATURE_ENGINEER.ONE_HOT: {
                                    return 'anchor-component-file o-tree-icon--one-hot'
                                }
                                case COMPONENT_TYPE.DATA_TOOLS.PYTHON_SCRIPT: {
                                    return 'anchor-component-file o-tree-icon--python'
                                }
                                default: return 'anchor-folder';
                            }
                        }}
                    />
                ) : <Loading />}
            </div>
        )
    }

    render () {
        return (
            <div className="sidebar c-component-siderbar">
                {
                    this.renderFolderContent()
                }
            </div>
        )
    }
}

export default ComponentSidebar;
