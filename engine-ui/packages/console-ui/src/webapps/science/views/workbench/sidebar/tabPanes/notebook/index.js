import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { union } from 'lodash';

import Loading from '../loading'
import ToolBar from '../../toolbar';
import FolderTree from '../../../../../components/folderTree';
import NewFolder from '../../newFolder';
import NotebookSearch from '../../../../../components/searchModal/notebookSearch';
import * as fileTreeActions from '../../../../../actions/base/fileTree';
import workbenchActions from '../../../../../actions/workbenchActions';
import * as notebookActions from '../../../../../actions/notebookActions'

import { siderBarType } from '../../../../../consts';

// const Search = Input.Search;

@connect(
    state => {
        return {
            routing: state.routing,
            files: state.notebook.files
        }
    },
    dispatch => {
        return {
            ...bindActionCreators(fileTreeActions, dispatch),
            ...bindActionCreators(workbenchActions, dispatch),
            ...bindActionCreators(notebookActions, dispatch)
        };
    })
class NotebookSidebar extends Component {
    state = {
        expandedKeys: [],
        newFolderVisible: false,
        notebookSearchVisible: false,
        newFolderData: null
    }

    newFolder (folder) {
        this.setState({
            newFolderVisible: true,
            newFolderData: folder
        })
    }
    closeNewFolder = () => {
        this.setState({
            newFolderVisible: false,
            newFolderData: null
        })
    }
    asynLoadCatalogue = (treeNode) => {
        return this.props.loadTreeData(siderBarType.notebook, treeNode.props.data.id)
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
    onSelect = (selectedKeys, e) => {
        const data = e.node.props.data;
        if (data.type != 'file') {
            return;
        }
        this.props.openNotebook(data.id);
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
                        onSelect={this.onSelect}
                        expandedKeys={this.state.expandedKeys}
                        treeData={files}
                        nodeClass={(item) => {
                            if (item.type == 'file') {
                                return 'anchor-notebook-file o-tree-icon--notebook'
                            }
                            return 'anchor-notebook-folder'
                        }}
                        contextMenus={[
                            {
                                targetClassName: 'anchor-notebook-folder',
                                menuItems: [{
                                    text: '新建任务',
                                    onClick: (activeNode) => {
                                        this.props.openNewNotebook(activeNode);
                                    }
                                }, {
                                    text: '新建文件夹',
                                    onClick: (activeNode) => {
                                        this.newFolder(activeNode);
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
                                targetClassName: 'anchor-notebook-file',
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
        const { newFolderVisible, notebookSearchVisible, newFolderData } = this.state;
        return (
            <div className="sidebar">
                <ToolBar
                    toolbarItems={[
                        {
                            title: '新建Notebook',
                            type: 'file-add',
                            onClick: () => {
                                this.props.openNewNotebook();
                            }
                        },
                        {
                            title: '新建文件夹',
                            type: 'folder-add',
                            onClick: () => {
                                this.newFolder();
                            }
                        },
                        {
                            title: '上传本地文件',
                            type: 'upload',
                            onClick: () => {
                                console.log(3)
                            }
                        },
                        {
                            title: '搜索并打开Notebook',
                            type: 'search',
                            onClick: () => {
                                this.setState({
                                    notebookSearchVisible: true
                                })
                            }
                        }
                    ]}
                />
                {
                    this.renderFolderContent()
                }
                <NewFolder
                    type={siderBarType.notebook}
                    data={newFolderData}
                    visible={newFolderVisible}
                    onOk={(values) => {
                        console.dir(values);
                        this.closeNewFolder();
                    }}
                    onCancel={this.closeNewFolder}
                />
                <NotebookSearch
                    visible={notebookSearchVisible}
                    onCancel={() => {
                        this.setState({
                            notebookSearchVisible: false
                        })
                    }}
                />
            </div>
        )
    }
}

export default NotebookSidebar;
