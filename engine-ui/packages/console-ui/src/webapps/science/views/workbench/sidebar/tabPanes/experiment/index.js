import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { union } from 'lodash';

import Loading from '../loading';
import ToolBar from '../../toolbar';
import FolderTree from '../../folderTree';
import workbenchActions from '../../../../../actions/workbenchActions';

// const Search = Input.Search;

@connect(
    state => {
        return {
            routing: state.routing,
            files: state.experiment.files
        }
    },
    dispatch => {
        const actions = bindActionCreators(workbenchActions, dispatch);
        return actions;
    })
class ExperimentSidebar extends Component {
    constructor (props) {
        super(props)
    }

    state = {
        expandedKeys: []
    }

    componentDidMount () {
        this.props.loadCatalogue();
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

    renderFolderContent = () => {
        const {
            files
        } = this.props;
        return (
            <div>
                {files.length ? (
                    <FolderTree
                        loadData={this.asynLoadCatalogue}
                        onSelect={this.onNodeSelect}
                        onExpand={this.onExpand}
                        expandedKeys={this.state.expandedKeys}
                        selectedKeys={this.state.selectedKeys}
                        treeData={[{
                            id: 1,
                            name: 'folder1',
                            type: 'folder',
                            children: [{
                                id: 11,
                                name: 'file1',
                                type: 'file'
                            }, {
                                id: 12,
                                name: 'file2',
                                type: 'file'
                            }]
                        }]}
                        nodeClass={(item) => {
                            if (item.type == 'file') {
                                return 'anchor-experiment-file o-tree-icon--normal'
                            }
                            return 'anchor-experiment-folder'
                        }}
                        contextMenus={[
                            {
                                targetClassName: 'anchor-experiment-folder',
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
                                targetClassName: 'anchor-experiment-file',
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
                <ToolBar
                    toolbarItems={[
                        {
                            title: '新建实验',
                            type: 'file-add',
                            onClick: () => {
                                console.log(1)
                            }
                        },
                        {
                            title: '新建文件夹',
                            type: 'folder-add',
                            onClick: () => {
                                console.log(2)
                            }
                        },
                        {
                            title: '搜索并打开实验',
                            type: 'search',
                            onClick: () => {
                                console.log(11)
                            }
                        }
                    ]}
                />
                {
                    this.renderFolderContent()
                }
            </div>
        )
    }
}

export default ExperimentSidebar;
