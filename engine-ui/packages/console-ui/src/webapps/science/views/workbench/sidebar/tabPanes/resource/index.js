import React, { Component } from 'react';
// import moment from 'moment';
import { connect } from 'react-redux';
import { Modal, Dropdown, Menu, Icon } from 'antd';
import { bindActionCreators } from 'redux';
import { union } from 'lodash';

import Loading from '../loading'
import FolderTree from '../../../../../components/folderTree';
import ResUploadModal from '../../../../../components/uploadModal';
import ResViewModal from '../../../../../components/resViewModal';
import ResEditModal from '../../../../../components/resEditModal';
import NewFolder from '../../newFolder';
import * as fileTreeActions from '../../../../../actions/base/fileTree';
import workbenchActions from '../../../../../actions/workbenchActions';
import * as resourceActions from '../../../../../actions/resourceActions'
import * as notebookActions from '../../../../../actions/notebookActions'

import { siderBarType } from '../../../../../consts';
import { resourceTypeIcon } from '../../../../../comm';
@connect(
    state => {
        return {
            routing: state.routing,
            files: state.resource.files,
            expandedKeys: state.resource.expandedKeys
        }
    },
    dispatch => {
        return {
            ...bindActionCreators(fileTreeActions, dispatch),
            ...bindActionCreators(workbenchActions, dispatch),
            ...bindActionCreators(resourceActions, dispatch),
            ...bindActionCreators(notebookActions, dispatch)
        };
    })
class ResourceManage extends Component {
    state = {
        expandedKeys: [],
        newFolderVisible: false,
        notebookSearchVisible: false,
        editParamsVisible: false,
        newFolderData: null,
        editParamsData: null,
        uploadModalVisible: false,
        resourceData: null,
        isCoverUpload: false, // 是否替换资源
        resDetailModal: false,
        resEditModal: false
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
    uploadRes (data, isCoverUpload) {
        this.setState({
            uploadModalVisible: true,
            resourceData: data,
            isCoverUpload
        })
    }
    handleOnCancel = () => {
        this.setState({
            uploadModalVisible: false,
            resourceData: null
        })
    }
    closeModal = () => {
        this.setState({
            resDetailModal: false,
            resourceData: null
        })
    }
    asynLoadCatalogue = (treeNode) => {
        return this.props.loadTreeData(siderBarType.resource, treeNode.props.data.id)
    }

    onExpand = (expandedKeys, { expanded }) => {
        let keys = expandedKeys;
        if (expanded) {
            keys = union(this.state.expandedKeys, keys)
        }
        this.props.updateExpandedKeys(siderBarType.resource, keys);
    }
    onMenuClick = ({ key }) => {
        switch (key) {
            case 'resource:upload': {
                this.uploadRes({}, false);
                return;
            }
            case 'resource:replace': {
                this.uploadRes({}, true);
                return;
            }
            case 'resource:newFolder': {
                this.newFolder();
            }
        }
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
                        expandedKeys={this.props.expandedKeys}
                        treeData={files}
                        dropDownTab={() => {
                            return (
                                <div className='resource_dropdown'>
                                    <Dropdown overlay={
                                        <Menu onClick={this.onMenuClick}>
                                            <Menu.Item key="resource:upload">
                                                上传资源
                                            </Menu.Item>
                                            <Menu.Item key="resource:replace">
                                                替换资源
                                            </Menu.Item>
                                            <Menu.Item key="resource:newFolder">
                                                新建文件夹
                                            </Menu.Item>
                                        </Menu>
                                    } trigger={['click']}>
                                        <Icon type="bars" />
                                    </Dropdown>
                                </div>
                            )
                        }}
                        nodeClass={(item) => {
                            const resClassName = resourceTypeIcon(item.resourceType)
                            if (item.type == 'file') {
                                return `anchor-resource-file ${resClassName}`
                            }
                            if (item.level == 13) {
                                return 'anchor-resource-root'
                            }
                            return 'anchor-resource-folder'
                        }}
                        contextMenus={[{
                            targetClassName: 'anchor-resource-root',
                            menuItems: [{
                                text: '上传资源',
                                onClick: (activeNode) => {
                                    this.uploadRes(activeNode, false);
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
                            targetClassName: 'anchor-resource-folder',
                            menuItems: [{
                                text: '上传资源',
                                onClick: (activeNode) => {
                                    this.uploadRes(activeNode, false);
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
                            targetClassName: 'anchor-resource-file',
                            menuItems: [{
                                text: '属性',
                                onClick: (activeNode) => {
                                    this.setState({
                                        resDetailModal: true,
                                        resourceData: activeNode
                                    })
                                }
                            }, {
                                text: '替换资源',
                                onClick: (activeNode) => {
                                    this.uploadRes(activeNode, true);
                                }
                            }, {
                                text: '重命名',
                                onClick: (activeNode) => {
                                    this.setState({
                                        resEditModal: true,
                                        resourceData: activeNode
                                    })
                                }
                            }, {
                                text: '删除',
                                onClick: (activeNode) => {
                                    Modal.confirm({
                                        title: '确认删除',
                                        content: `确认删除资源？`,
                                        onOk: () => {
                                            this.props.deleteResource(activeNode);
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
        const { newFolderVisible, newFolderData, uploadModalVisible,
            resourceData, isCoverUpload,
            resDetailModal, resEditModal } = this.state;
        return (
            <div className="sidebar" style={{ height: '30%', overflow: 'auto', borderTop: '1px solid #ddd', backgroundColor: '#fff' }}>
                <div>
                    {
                        this.renderFolderContent()
                    }
                    <NewFolder
                        type={siderBarType.resource}
                        data={newFolderData}
                        visible={newFolderVisible}
                        onOk={(values) => {
                            console.dir(values);
                            this.closeNewFolder();
                        }}
                        onCancel={this.closeNewFolder}
                    />
                    <ResUploadModal
                        type={siderBarType.resource}
                        visible={uploadModalVisible}
                        resourceData={resourceData}
                        isCoverUpload={isCoverUpload}
                        onCancel={() => { this.handleOnCancel() }}
                    />
                    <ResViewModal
                        resId={resourceData && resourceData.id}
                        visible={resDetailModal}
                        closeModal={() => { this.closeModal() }}
                    />
                    <ResEditModal
                        resourceData={resourceData}
                        visible={resEditModal}
                        onCancel={() => {
                            this.setState({ resEditModal: false, resourceData: null })
                        }}
                    />
                </div>
            </div>
        )
    }
}

export default ResourceManage;
