import React, { Component } from 'react';
import moment from 'moment';
import { connect } from 'react-redux';
import { Modal, message } from 'antd';
import { bindActionCreators } from 'redux';
import { union } from 'lodash';

import Loading from '../loading'
import ToolBar from '../../toolbar';
import FolderTree from '../../../../../components/folderTree';
import NewFolder from '../../newFolder';
import MoveModal from '../../moveModal';
import NotebookSearch from '../../../../../components/searchModal/notebookSearch';
import TaskParamsModal from '../../../../../components/taskParamsModal';
import ResourceManage from '../resource';
import * as fileTreeActions from '../../../../../actions/base/fileTree';
import workbenchActions from '../../../../../actions/workbenchActions';
import * as notebookActions from '../../../../../actions/notebookActions'

import { siderBarType, TASK_TYPE, PYTON_VERSION } from '../../../../../consts';

// const Search = Input.Search;

@connect(
    state => {
        return {
            routing: state.routing,
            files: state.notebook.files,
            currentTabIndex: state.notebook.currentTabIndex,
            tabs: state.notebook.localTabs,
            expandedKeys: state.notebook.expandedKeys
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
        moveModalVisible: false,
        newFolderData: null,
        editParamsData: null,
        moveData: null
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
        this.props.updateExpandedKeys(siderBarType.notebook, keys);
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
            files,
            currentTabIndex,
            tabs
        } = this.props;
        const selectedKeys = tabs.filter((tab) => {
            return tab.id == currentTabIndex
        }).map((tab) => {
            return tab.key
        });
        return (
            <div>
                {files.length ? (
                    <FolderTree
                        loadData={this.asynLoadCatalogue}
                        onExpand={this.onExpand}
                        onSelect={this.onSelect}
                        expandedKeys={this.props.expandedKeys}
                        selectedKeys={selectedKeys}
                        treeData={files}
                        nodeClass={(item) => {
                            if (item.type == 'file') {
                                switch (item.taskType) {
                                    case TASK_TYPE.PYTHON: {
                                        if (item.pythonVersion == PYTON_VERSION.PYTHON2) {
                                            return 'anchor-notebook-file o-tree-icon--notebook_python_2'
                                        } else if (item.pythonVersion == PYTON_VERSION.PYTHON3) {
                                            return 'anchor-notebook-file o-tree-icon--notebook_python_3'
                                        } else {
                                            return 'anchor-notebook-file o-tree-icon--notebook_python'
                                        }
                                    }
                                    case TASK_TYPE.PYSPARK: {
                                        return 'anchor-notebook-file o-tree-icon--notebook_pyspark'
                                    }
                                    default: return 'anchor-notebook-file';
                                }
                            }
                            if (item.level == 13) {
                                return 'anchor-notebook-root'
                            }
                            return 'anchor-notebook-folder'
                        }}
                        contextMenus={[
                            {
                                targetClassName: 'anchor-notebook-root',
                                menuItems: [{
                                    text: '新建Notebook',
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
                                    text: '新建Notebook',
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
                                        if (activeNode.name == '我的Notebook') {
                                            message.warn('该文件夹不允许重命名');
                                            return;
                                        }
                                        this.newFolder({
                                            nodePid: activeNode.parentId,
                                            name: activeNode.name,
                                            id: activeNode.id
                                        });
                                    }
                                }, {
                                    text: '移动',
                                    onClick: (activeNode) => {
                                        if (activeNode.name == '我的Notebook') {
                                            message.warn('该文件夹不允许移动');
                                            return;
                                        }
                                        this.setState({
                                            moveData: {
                                                nodePid: activeNode.parentId,
                                                name: activeNode.name,
                                                id: activeNode.id,
                                                key: activeNode.key
                                            },
                                            moveModalVisible: true
                                        });
                                    }
                                }, {
                                    text: '删除',
                                    onClick: (activeNode) => {
                                        if (activeNode.name == '我的Notebook') {
                                            message.warn('该文件夹不允许删除');
                                            return;
                                        }
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
                                    text: '移动',
                                    onClick: (activeNode) => {
                                        this.setState({
                                            moveData: {
                                                nodePid: activeNode.parentId,
                                                name: activeNode.name,
                                                id: activeNode.id,
                                                isFile: true
                                            },
                                            moveModalVisible: true
                                        });
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
        const {
            notebookSearchVisible,
            newFolderData,
            newFolderVisible,
            editParamsData,
            editParamsVisible,
            moveData,
            moveModalVisible
        } = this.state;
        return (
            <>
                <div className="sidebar" style={{ height: '70%' }}>
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
                    <MoveModal
                        type={siderBarType.notebook}
                        data={moveData}
                        visible={moveModalVisible}
                        onOk={(values) => {
                            this.setState({
                                moveModalVisible: false,
                                moveData: null
                            })
                        }}
                        onCancel={() => {
                            this.setState({
                                moveModalVisible: false,
                                moveData: null
                            })
                        }}
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
                                id: editParamsData.id,
                                name: editParamsData.name,
                                taskDesc: editParamsData.taskDesc,
                                [editKey]: editValue,
                                version: editParamsData.version,
                                isEditBaseInfo: true
                            }).then((res) => {
                                if (res) {
                                    this.setState({
                                        editParamsData: res.data
                                    })
                                    this.props.loadTreeData(siderBarType.notebook, editParamsData.parentId);
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
                            value: editParamsData.createUser
                        }, {
                            label: '创建时间',
                            value: moment(editParamsData.gmtCreate).format('YYYY-MM-DD HH:mm:ss')
                        }, {
                            label: '最近修改人',
                            value: editParamsData.modifyUser
                        }, {
                            label: '最近修改时间',
                            value: moment(editParamsData.gmtModified).format('YYYY-MM-DD HH:mm:ss')
                        }]}
                    />
                </div>
                <ResourceManage />
            </>
        )
    }
}

export default NotebookSidebar;
