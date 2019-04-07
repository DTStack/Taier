import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { union } from 'lodash';
import { message } from 'antd';

import utils from 'utils';
import CopyUtils from 'utils/copy';

import ToolBar from '../../toolbar';
import FolderTree from '../../folderTree';
import workbenchActions from '../../../../../actions/workbenchActions';
import { CATALOGUE_TYPE } from '../../../../../consts';

// const Search = Input.Search;

@connect(
    state => {
        return {
            routing: state.routing,
            files: state.notebook.files
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
        expandedKeys: [],
        selectedKeys: []
    }

    componentDidMount () {
        this.props.loadCatalogue();
    }

    refresh = () => {
        this.setState({
            expandedKeys: [],
            selectedKeys: []
        })
        this.props.loadCatalogue();
    }

    searchTable = (value) => {
        const query = utils.trim(value);
        if (!query) {
            this.refresh();
            return;
        };

        this.props.loadCatalogue({
            tableName: query
        }, CATALOGUE_TYPE.SEARCH_TABLE);
    }

    copyName = () => {
        const activeNode = this.state.activeNode;
        if (activeNode) {
            const copyValue = activeNode.name || activeNode.tableName;
            const copyUtil = new CopyUtils();
            copyUtil.copy(copyValue, (success) => {
                if (success) {
                    message.success('复制成功！');
                }
            })
        }
    }

    asynLoadCatalogue = (treeNode) => {
        const ctx = this;
        const { data, fileType } = treeNode.props;
        return new Promise(async (resolve) => {
            ctx.props.loadCatalogue(data, fileType);
            resolve();
        });
    }

    onNodeSelect = (selectedKeys, { node }) => {
        const { expandedKeys } = this.state;
        const { eventKey, fileType, data } = node.props;
        this.setState({
            selectedKeys
        });

        if (fileType === CATALOGUE_TYPE.DATA_MAP) {
            this.props.onGetDataMap({ id: data.id })
            return false;
        }

        const eventKeyIndex = expandedKeys.indexOf(eventKey);
        this.asynLoadCatalogue(node);

        if (eventKeyIndex > -1) {
            expandedKeys.splice(eventKeyIndex, 1);
            this.onExpand(expandedKeys, { expanded: false });
        } else {
            expandedKeys.push(eventKey);
            this.onExpand(expandedKeys, { expanded: true });
        }
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
                {!files.length ? (
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
                ) : <span>暂无数据</span>}
            </div>
        )
    }

    render () {
        const {
            onCreateDB,
            onCreateTable,
            onSQLQuery,
            onEditTable,
            onTableDetail
        } = this.props;

        return (
            <div className="sidebar">
                <ToolBar
                    onRefresh={this.refresh}
                    onCreateDB={() => onCreateDB()}
                    onSQLQuery={() => onSQLQuery()}
                    onEditTable={() => onEditTable()}
                    onCreateTable={() => onCreateTable()}
                    onTableDetail={() => onTableDetail()}
                />
                {
                    this.renderFolderContent()
                }
            </div>
        )
    }
}

export default ExperimentSidebar;
