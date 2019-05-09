import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { union } from 'lodash';

import Loading from '../loading';
import FolderTree from '../../../../../components/folderTree';
import * as fileTreeActions from '../../../../../actions/base/fileTree';
import * as experimentActions from '../../../../../actions/experimentActions';
import Mx from 'widgets/mxGraph';
import { siderBarType, VertexSize, TASK_ENUM, COMPONENT_TYPE } from '../../../../../consts';
import api from '../../../../../api/experiment';
const {
    mxUtils,
    mxEvent,
    mxCell,
    mxGeometry,
    mxDragSource
} = Mx;
// const Search = Input.Search;

@connect(
    state => {
        const tab = state.experiment.localTabs.find(o => o.id === state.experiment.currentTabIndex);
        return {
            routing: state.routing,
            files: state.component.files,
            graph: state.component.graph,
            tabData: tab
        }
    },
    dispatch => {
        const actions = bindActionCreators({ ...fileTreeActions, ...experimentActions }, dispatch);
        return actions;
    })
class ComponentSidebar extends Component {
    constructor (props) {
        super(props)
    }
    _dragElements = [];
    state = {
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
    componentDidUpdate (prevProps, prevState) {
        this.initDragger();
    }
    getCellData = (cell) => {
        return cell && {
            vertex: cell.vertex,
            edge: cell.edge,
            data: cell.data,
            x: cell.geometry.x,
            y: cell.geometry.y,
            value: cell.value,
            id: cell.id
        }
    }
    // Returns the graph under the mouse
    graphF = (evt) => {
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
        dragElt.style.width = VertexSize.width;
        dragElt.style.height = VertexSize.height;
        return dragElt;
    }
    initDragger = () => {
        const fileNodes = document.querySelectorAll('.anchor-component-file .ant-tree-node-content-wrapper');
        const { graph = {}, files, currentTabIndex } = this.props;
        const ctx = this;
        const findNameInLoop = (name, arr) => {
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
        this._dragElements.map((item) => {
            item.reset();
        })
        this._dragElements = [];
        // Inserts a cell at the given location
        const funct = function (graph, evt, target, x, y) {
            const data = this.sourceData;
            const params = {
                taskType: data.taskType,
                componentType: data.componentType,
                flowId: currentTabIndex,
                [TASK_ENUM[data.componentType]]: {}
            }
            api.addOrUpdateTask(params).then(async res => {
                if (res.code === 1) {
                    /**
                     * 上面那个接口没有返回重要的组件信息
                     * 下面这个请求返回了组件的信息
                     */
                    let response = await api.getExperimentTask({ id: res.data.id });
                    if (response.code === 1) {
                        // eslint-disable-next-line new-cap
                        let cell = new mxCell('', new mxGeometry(0, 0, VertexSize.width, VertexSize.height));
                        cell.data = response.data;
                        cell.vertex = true;
                        graph.importCells([cell], x, y, target);
                        ctx.props.saveExperiment(ctx.props.tabData)
                        graph.clearSelection();
                    }
                }
            })
        };
        fileNodes.forEach((element) => {
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
    onExpand = (expandedKeys, { expanded }) => {
        let keys = expandedKeys;
        if (expanded) {
            keys = union(this.state.expandedKeys, keys)
        }
        this.setState({
            expandedKeys: keys
        })
    }
    asynLoadCatalogue = (treeNode) => {
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
                        nodeClass={(item) => {
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
                                case COMPONENT_TYPE.DATA_PRE_HAND.DATA_SPLIT: {
                                    return 'anchor-component-file o-tree-icon--data-pre-hand'
                                }
                                case COMPONENT_TYPE.MACHINE_LEARNING.LOGISTIC_REGRESSION: {
                                    return 'anchor-component-file o-tree-icon--machine-learning'
                                }
                                case COMPONENT_TYPE.DATA_PREDICT.DATA_PREDICT: {
                                    return 'anchor-component-file o-tree-icon--data-predict'
                                }
                                case COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION: {
                                    return 'anchor-component-file o-tree-icon--data-evaluate'
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
