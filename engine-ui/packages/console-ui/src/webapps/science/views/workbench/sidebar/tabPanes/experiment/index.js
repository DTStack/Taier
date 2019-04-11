import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { union } from 'lodash';

import Loading from '../loading';
import ToolBar from '../../toolbar';
import FolderTree from '../../folderTree';
import * as fileTreeActions from '../../../../../actions/base/fileTree';

import { siderBarType } from '../../../../../consts';

// const Search = Input.Search;

@connect(
    state => {
        return {
            routing: state.routing,
            files: state.experiment.files
        }
    },
    dispatch => {
        const actions = bindActionCreators(fileTreeActions, dispatch);
        return actions;
    })
class ExperimentSidebar extends Component {
    constructor (props) {
        super(props)
    }

    state = {
        expandedKeys: []
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
        return this.props.loadTreeData(siderBarType.experiment, treeNode.props.data.id)
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
