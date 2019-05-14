import React, { Component } from 'react';
import moment from 'moment';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { union } from 'lodash';

import Loading from '../loading';
import ToolBar from '../../toolbar';
import FolderTree from '../../../../../components/folderTree';
import NewFolder from '../../newFolder';
import ExperimentSearch from '../../../../../components/searchModal/experimentSearch';
import TaskParamsModal from '../../../../../components/taskParamsModal';

import * as fileTreeActions from '../../../../../actions/base/fileTree';
import * as experimentActions from '../../../../../actions/experimentActions';
import workbenchActions from '../../../../../actions/workbenchActions';

import { siderBarType } from '../../../../../consts';
import { Modal, message } from 'antd';

// const Search = Input.Search;

@connect(
    state => {
        return {
            routing: state.routing,
            files: state.experiment.files,
            currentTabIndex: state.experiment.currentTabIndex,
            tabs: state.experiment.localTabs,
            expandedKeys: state.experiment.expandedKeys
        }
    },
    dispatch => {
        return {
            ...bindActionCreators(fileTreeActions, dispatch),
            ...bindActionCreators(workbenchActions, dispatch),
            ...bindActionCreators(experimentActions, dispatch)
        };
    })
class ExperimentSidebar extends Component {
    constructor (props) {
        super(props)
    }

    state = {
        newFolderVisible: false,
        experimentSearchVisible: false,
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
    onExpand = (expandedKeys, { expanded }) => {
        let keys = expandedKeys;
        if (expanded) {
            keys = union(this.state.expandedKeys, keys)
        }
        this.props.updateExpandedKeys(siderBarType.experiment, keys);
    }
    asynLoadCatalogue = (treeNode) => {
        return this.props.loadTreeData(siderBarType.experiment, treeNode.props.data.id)
    }
    onSelect = (selectedKeys, e) => {
        const data = e.node.props.data;
        if (data.type != 'file') {
            return;
        }
        this.props.openExperiment(data.id);
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
                        selectedKeys={selectedKeys}
                        expandedKeys={this.props.expandedKeys}
                        treeData={files}
                        nodeClass={(item) => {
                            if (item.type == 'file') {
                                return 'anchor-experiment-file o-tree-icon--experiment'
                            }
                            if (item.level == 1) {
                                return 'anchor-experiment-root'
                            }
                            return 'anchor-experiment-folder'
                        }}
                        contextMenus={[
                            {
                                targetClassName: 'anchor-experiment-root',
                                menuItems: [{
                                    text: '新建实验',
                                    onClick: (activeNode) => {
                                        this.props.openNewExperiment(activeNode);
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
                                targetClassName: 'anchor-experiment-folder',
                                menuItems: [{
                                    text: '新建实验',
                                    onClick: (activeNode) => {
                                        this.props.openNewExperiment(activeNode);
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
                                        if (activeNode.name == '我的实验') {
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
                                    text: '删除',
                                    onClick: (activeNode) => {
                                        if (activeNode.name == '我的实验') {
                                            message.warn('该文件夹不允许删除');
                                            return;
                                        }
                                        Modal.confirm({
                                            title: '确认删除',
                                            content: '确认删除文件夹？',
                                            onOk: () => {
                                                this.props.deleteExperimentFolder(activeNode);
                                            }
                                        })
                                    }
                                }]
                            },
                            {
                                targetClassName: 'anchor-experiment-file',
                                menuItems: [{
                                    text: '属性',
                                    onClick: (activeNode) => {
                                        this.setState({
                                            editParamsData: activeNode,
                                            editParamsVisible: true
                                        })
                                    }
                                }, {
                                    text: '删除',
                                    onClick: (activeNode) => {
                                        Modal.confirm({
                                            title: '确认删除',
                                            content: `确认删除实验 ${activeNode.name}？`,
                                            onOk: () => {
                                                this.props.deleteExperiment(activeNode);
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
        const { experimentSearchVisible, newFolderData, newFolderVisible, editParamsData, editParamsVisible } = this.state;
        return (
            <div className="sidebar">
                <ToolBar
                    toolbarItems={[
                        {
                            title: '新建实验',
                            type: 'file-add',
                            onClick: () => {
                                this.props.openNewExperiment();
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
                            title: '搜索并打开实验',
                            type: 'search',
                            onClick: () => {
                                this.setState({
                                    experimentSearchVisible: true
                                })
                            }
                        }
                    ]}
                />
                {
                    this.renderFolderContent()
                }
                <NewFolder
                    type={siderBarType.experiment}
                    data={newFolderData}
                    visible={newFolderVisible}
                    onOk={(values) => {
                        console.dir(values);
                        this.closeNewFolder();
                    }}
                    onCancel={this.closeNewFolder}
                />
                <ExperimentSearch
                    visible={experimentSearchVisible}
                    onCancel={() => {
                        this.setState({
                            experimentSearchVisible: false
                        })
                    }}
                />
                <TaskParamsModal
                    key={editParamsData && editParamsData.id}
                    title='实验属性'
                    visible={editParamsVisible}
                    onCancel={() => {
                        this.setState({
                            editParamsVisible: false,
                            editParamsData: null
                        })
                    }}
                    onEdit={(editKey, editValue, callback) => {
                        const { tabs, currentTabIndex } = this.props;
                        const currentTab = tabs.find(o => o.id == currentTabIndex) || {};
                        this.props.saveExperiment({
                            ...currentTab,
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
                                this.props.loadTreeData(siderBarType.experiment, editParamsData.parentId);
                                callback();
                            }
                        });
                    }}
                    data={editParamsData && [{
                        key: 'name',
                        label: '实验名称',
                        value: editParamsData.name,
                        edit: true
                    }, {
                        key: 'taskDesc',
                        label: '实验描述',
                        value: editParamsData.taskDesc,
                        editType: 'textarea',
                        edit: true
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
        )
    }
}

export default ExperimentSidebar;
