import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { union } from 'lodash';

import Loading from '../loading';
import FolderTree from '../../folderTree';
import workbenchActions from '../../../../../actions/workbenchActions';

// const Search = Input.Search;

@connect(
    state => {
        return {
            routing: state.routing,
            files: state.component.files
        }
    },
    dispatch => {
        const actions = bindActionCreators(workbenchActions, dispatch);
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
                        }, {
                            id: 10,
                            name: 'folder11',
                            type: 'folder',
                            children: [{
                                id: 110,
                                name: 'file11',
                                type: 'file'
                            }, {
                                id: 120,
                                name: 'file21',
                                type: 'file'
                            }]
                        }]}
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
