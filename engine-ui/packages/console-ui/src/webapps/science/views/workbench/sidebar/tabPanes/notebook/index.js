import React, { Component } from 'react';
import moment from 'moment';
import { connect } from 'react-redux';
import { Modal } from 'antd';
import { bindActionCreators } from 'redux';
import { union } from 'lodash';

import Loading from '../loading'
import ToolBar from '../../toolbar';
import FolderTree from '../../../../../components/folderTree';
import NewFolder from '../../newFolder';
import NotebookSearch from '../../../../../components/searchModal/notebookSearch';
import TaskParamsModal from '../../../../../components/taskParamsModal';
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
        editParamsVisible: false,
        newFolderData: null,
        editParamsData: null
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
                            if (item.level == 1) {
                                return 'anchor-notebook-root'
                            }
                            return 'anchor-notebook-folder'
                        }}
                        contextMenus={[
                            {
                                targetClassName: 'anchor-notebook-root',
                                menuItems: [{
                                    text: '新建任务',
                                    onClick: (activeNode) => {
                                        this.props.openNewNotebook(activeNode);
                                    }
                                }, {
                                    text: '新建文件夹',
                                    onClick: (activeNode) => {
                                        this.newFolder({
                                            nodePid: activeNode.id
                                        });
                                    }
                                }]
                            },
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
                                        this.newFolder({
                                            nodePid: activeNode.id
                                        });
                                    }
                                }, {
                                    text: '重命名',
                                    onClick: (activeNode) => {
                                        this.newFolder({
                                            nodePid: activeNode.parentId,
                                            name: activeNode.name,
                                            id: activeNode.id
                                        });
                                    }
                                }, {
                                    text: '删除',
                                    onClick: (activeNode) => {
                                        Modal.confirm({
                                            title: '确认删除',
                                            content: `确认删除文件夹？`,
                                            onOk: () => {
                                                this.props.deleteNotebookFolder(activeNode);
                                            }
                                        })
                                    }
                                }]
                            },
                            {
                                targetClassName: 'anchor-notebook-file',
                                menuItems: [{
                                    text: '属性',
                                    onClick: (activeNode) => {
                                        this.setState({
                                            editParamsVisible: true,
                                            editParamsData: activeNode
                                        })
                                    }
                                }, {
                                    text: '删除',
                                    onClick: (activeNode) => {
                                        Modal.confirm({
                                            title: '确认删除',
                                            content: `确认删除Notebook ${activeNode.name} ？`,
                                            onOk: () => {
                                                this.props.deleteNotebook(activeNode)
                                            }
                                        })
                                    }
                                }]
                            }]}
                    />
                ) : <Loading />}
            </div>
        )
    }

    render () {
        const { newFolderVisible, notebookSearchVisible, newFolderData, editParamsData, editParamsVisible } = this.state;
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
                <TaskParamsModal
                    key={editParamsData && editParamsData.id}
                    title='Notebook属性'
                    visible={editParamsVisible}
                    onCancel={() => {
                        this.setState({
                            editParamsVisible: false,
                            editParamsData: null
                        })
                    }}
                    onEdit={(editKey, editValue, callback) => {
                        this.props.saveNotebook({
                            ...editParamsData,
                            [editKey]: editValue
                        }).then((res) => {
                            if (res) {
                                this.setState({
                                    editParamsData: res.data
                                })
                                callback();
                            }
                        });
                    }}
                    data={editParamsData && [{
                        key: 'name',
                        label: 'Notebook名称',
                        value: editParamsData.name,
                        edit: true
                    }, {
                        key: 'taskDesc',
                        label: 'Notebook描述',
                        value: editParamsData.taskDesc,
                        editType: 'textarea',
                        edit: true
                    }, {
                        label: '作业类型',
                        value: 'Python3'
                    }, {
                        label: '创建人',
                        value: editParamsData.ownerUser.userName
                    }, {
                        label: '创建时间',
                        value: moment(editParamsData.ownerUser.gmtCreate).format('YYYY-MM-DD HH:mm:ss')
                    }, {
                        label: '最近修改人',
                        value: editParamsData.modifyUser.userName
                    }, {
                        label: '最近修改时间',
                        value: moment(editParamsData.modifyUser.gmtModified).format('YYYY-MM-DD HH:mm:ss')
                    }]}
                />
            </div>
        )
    }
}

export default NotebookSidebar;
