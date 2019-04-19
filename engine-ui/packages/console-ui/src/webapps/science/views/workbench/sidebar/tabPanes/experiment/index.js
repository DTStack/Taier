import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { union } from 'lodash';

import Loading from '../loading';
import ToolBar from '../../toolbar';
import FolderTree from '../../../../../components/folderTree';
import NewFolder from '../../newFolder';
import ExperimentSearch from '../../../../../components/searchModal/experimentSearch';

import * as fileTreeActions from '../../../../../actions/base/fileTree';
import * as experimentActions from '../../../../../actions/experimentActions';
import workbenchActions from '../../../../../actions/workbenchActions';

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
        expandedKeys: [],
        newFolderVisible: false,
        experimentSearchVisible: false,
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
    onSelect = (selectedKeys, e) => {
        const data = e.node.props.data;
        if (data.type != 'file') {
            return;
        }
        this.props.openExperiment(data.id);
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
                                return 'anchor-experiment-file o-tree-icon--experiment'
                            }
                            return 'anchor-experiment-folder'
                        }}
                        contextMenus={[
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
                                        this.props.deleteExperimentFolder(activeNode);
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
                                        this.props.deleteExperiment(activeNode);
                                    }
                                }]
                            }]}
                    />
                ) : <Loading />}
            </div>
        )
    }

    render () {
        const { experimentSearchVisible, newFolderData, newFolderVisible } = this.state;
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
            </div>
        )
    }
}

export default ExperimentSidebar;
