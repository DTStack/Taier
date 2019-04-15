import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { union } from 'lodash';

import Loading from '../loading';
import FolderTree from '../../../../../components/folderTree';
import * as fileTreeActions from '../../../../../actions/base/fileTree';
import Mx from 'widgets/mxGraph';
import { siderBarType, VertexSize } from '../../../../../consts';
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
            graph: state.component.graph
        }
    },
    dispatch => {
        const actions = bindActionCreators(fileTreeActions, dispatch);
        return actions;
    })
class ComponentSidebar extends Component {
    constructor (props) {
        super(props)
    }

    state = {
        expandedKeys: []
    }
    componentDidMount () {
        this.initDragger();
    }
    componentDidUpdate (prevProps, prevState) {
        if (this.props.graph !== prevProps.graph || this.state.expandedKeys.length !== prevState.expandedKeys.length) {
            this.initDragger();
        }
    }
    initDragger = () => {
        const files = document.querySelectorAll('.anchor-file.o-tree-icon--normal');
        const { graph = {} } = this.props;
        // Returns the graph under the mouse
        var graphF = function (evt) {
            var x = mxEvent.getClientX(evt);
            var y = mxEvent.getClientY(evt);
            var elt = document.elementFromPoint(x, y);
            if (mxUtils.isAncestorNode(graph.container, elt)) {
                return graph;
            }
            return null;
        };
        // Inserts a cell at the given location
        var funct = function (graph, evt, target, x, y) {
            const values = {
                'id': 11,
                'name': 'startddddd_mx',
                'type': 'file',
                'taskType': 1,
                'parentId': 1953,
                'catalogueType': 'TaskDevelop',
                'nodePid': 1953,
                'submitStatus': 0,
                'version': 0,
                'readWriteLockVO': {
                    'id': 2723,
                    'gmtCreate': null,
                    'gmtModified': 1553485670537,
                    'isDeleted': 0,
                    'lockName': '2510_120_BATCH_TASK',
                    'modifyUserId': 221,
                    'version': 1,
                    'projectId': 120,
                    'relationId': 2510,
                    'type': 'BATCH_TASK',
                    'lastKeepLockUserName': 'user_test@dtstack.com',
                    'result': 0,
                    'getLock': true
                }
            }
            // eslint-disable-next-line new-cap
            var cell = new mxCell('', new mxGeometry(0, 0, VertexSize.width, VertexSize.height));
            cell.data = values;
            cell.vertex = true;
            var cells = graph.importCells([cell], x, y, target);

            if (cells != null && cells.length > 0) {
                graph.scrollCellToVisible(cells[0]);
                graph.setSelectionCells(cells);
            }
        };
        // Creates the element that is being for the actual preview.
        var dragElt = document.createElement('div');
        dragElt.style.border = 'solid #90d5ff 1px';
        dragElt.style.width = VertexSize.width;
        dragElt.style.height = VertexSize.height;
        files.forEach((element) => {
            let md = mxUtils.makeDraggable(element, graphF, funct, dragElt, null, null, graph.autoscroll, true);
            md.isGuidesEnabled = function () {
                return graph.graphHandler.guidesEnabled;
            };
            md.createDragElement = mxDragSource.prototype.createDragElement;
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
                        contextMenus={[
                            {
                                targetClassName: 'anchor-folder',
                                menuItems: [{
                                    text: '新建实验',
                                    onClick: (activeNode) => {
                                        console.log(activeNode);
                                    }
                                }, {
                                    text: '新建文件夹',
                                    onClick: (activeNode) => {
                                        console.log(activeNode);
                                    }
                                }, {
                                    text: '重命名',
                                    onClick: (activeNode) => {
                                        console.log(activeNode);
                                    }
                                }, {
                                    text: '删除',
                                    onClick: (activeNode) => {
                                        console.log(activeNode);
                                    }
                                }]
                            },
                            {
                                targetClassName: 'anchor-file',
                                menuItems: [{
                                    text: '属性',
                                    onClick: (activeNode) => {
                                        console.log(activeNode);
                                    }
                                }, {
                                    text: '删除',
                                    onClick: (activeNode) => {
                                        console.dir({ databaseId: activeNode.id })
                                    }
                                }]
                            }]}
                    />
                ) : <Loading />}
            </div>
        )
    }

    render () {
        return (
            <div className="sidebar">
                {
                    this.renderFolderContent()
                }
            </div>
        )
    }
}

export default ComponentSidebar;
