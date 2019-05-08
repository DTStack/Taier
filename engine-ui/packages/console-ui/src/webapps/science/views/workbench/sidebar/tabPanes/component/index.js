import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { union, cloneDeep } from 'lodash';

import Loading from '../loading';
import FolderTree from '../../../../../components/folderTree';
import * as fileTreeActions from '../../../../../actions/base/fileTree';
import * as experimentActions from '../../../../../actions/experimentActions';
import Mx from 'widgets/mxGraph';
import { siderBarType, VertexSize, TASK_ENUM } from '../../../../../consts';
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
        return {
            routing: state.routing,
            files: state.component.files,
            graph: state.component.graph,
            currentTabIndex: state.experiment.currentTabIndex,
            tabs: state.experiment.localTabs
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
        this.initDragger();
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
    // Inserts a cell at the given location
    funct = (graph, evt, target, x, y) => {
        const { currentTabIndex, tabs, changeContent } = this.props;
        const ctx = this;
        const currentTab = tabs.find(o => o.id == currentTabIndex);
        const copyCurrentTab = cloneDeep(currentTab);
        const data = this.sourceData;
        const params = {
            taskType: data.taskType,
            componentType: data.componentType,
            flowId: currentTabIndex,
            [TASK_ENUM[data.componentType]]: {}
        }
        api.addOrUpdateTask(params).then(res => {
            if (res.code === 1) {
                // eslint-disable-next-line new-cap
                let cell = new mxCell('', new mxGeometry(x, y, VertexSize.width, VertexSize.height));
                cell.data = res.data;
                cell.vertex = true;
                let cells = graph.importCells([cell], x, y, target);
                if (copyCurrentTab.graphData) {
                    copyCurrentTab.graphData.push(ctx.getCellData(cell));
                } else {
                    copyCurrentTab.graphData = [ctx.getCellData(cell)]
                }
                changeContent(copyCurrentTab, currentTab, true)
                if (cells != null && cells.length > 0) {
                    graph.scrollCellToVisible(cells[0]);
                    graph.setSelectionCells(cells);
                }
            }
        })
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
        const fileNodes = document.querySelectorAll('.anchor-file.o-tree-icon--normal');
        const { graph = {}, files } = this.props;
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
        console.log('fileNodes:', fileNodes);
        this._dragElements.map((item) => {
            item.reset();
        })
        this._dragElements = [];
        fileNodes.forEach((element) => {
            const title = element.getElementsByClassName('ant-tree-title')[0].children[0].innerText;
            let md = mxUtils.makeDraggable(element, this.graphF, this.funct, this.initDragElement(), null, null, graph.autoscroll, true);
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
                            if (item.type == 'file') {
                                return 'anchor-file o-tree-icon--normal'
                            }
                            return 'anchor-folder'
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
