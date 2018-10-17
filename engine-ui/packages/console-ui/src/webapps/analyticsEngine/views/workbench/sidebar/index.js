import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { Popconfirm } from 'antd';

import {
    ContextMenu,
    MenuItem,
} from 'widgets/context-menu';

import ToolBar from './toolbar';
import FolderTree from './folderTree';
import * as workbenchActions from '../../../actions/workbenchActions';

const CTX_ACTION = {
    SHOW_DATA_MAP: 'SHOW_DATA_MAP',
}

@connect(
state => {
    const { folderTree } = state.workbench;
    return {
        routing: state.routing,
        folderTree,
    }
},
dispatch => {
    const actions = bindActionCreators(workbenchActions, dispatch);
    return actions;
})
class Sidebar extends Component {

    constructor(props) {
        super(props)
    }

    state = {
        activeNode: null,
    }

    componentDidMount() {
        this.props.loadCatalogue();
    }

    searchTable = (value) => {
        console.log('search:', value);
        this.props.loadCatalogue({
            tableName: value,
        })
    }

    onRightClick = ({ node }) => {
        console.log('rightClick-node', node);
        const activeNode = node.props.data;
        this.setState({ activeNode: activeNode })
    }

    onLoadData = (treeNode) => {
        const { data } = treeNode.props;
        return new Promise((resolve) => {
            if (!data.children || data.children.length === 0) {
                this.props.loadCatalogue({
                    nodePid: data.id,
                })
            }
            resolve();
        });
    }

    onSelectCatalogeuItem = () => {

    }

    onCxtMenuItem = (catelogueType) => {

    }

    asynLoadCatalogue(treeNode) {
        const { data } = treeNode.props;
        return new Promise((resolve) => {
            if (!data.children || data.children.length === 0) {
                this.props.loadCatalogue({
                    nodePid: data.id,
                })
            }
            resolve();
        });
    }

    render() {
        const {
            folderTree,
            onCreateDb,
            onSQLQuery,
            loadCatalogue,
        } = this.props;
        return (
            <div className="sidebar">
                <ToolBar 
                    onSearch={this.searchTable}
                    onRefresh={() => loadCatalogue()}
                    onCreateDb={() => onCreateDb()}
                    onSQLQuery={() => onSQLQuery()}
                />
                <FolderTree
                    onRightClick={this.onRightClick}
                    loadData={this.asynLoadCatalogue}
                    onSelect={this.onSelectCatalogeuItem}
                    treeData={folderTree.children}
                />
                <ContextMenu targetClassName="anchor-database">
                    <MenuItem onClick={this.initEditTask}>新建表</MenuItem>
                    <MenuItem onClick={
                        this.onCxtMenuItem.bind(CTX_ACTION.SHOW_DATA_MAP)}
                    >
                        查看详情
                    </MenuItem>
                </ContextMenu>
                <ContextMenu targetClassName="anchor-table">
                    <MenuItem>查询</MenuItem>
                    <MenuItem>编辑表</MenuItem>
                    <MenuItem onClick={
                        this.onCxtMenuItem.bind(CTX_ACTION.SHOW_DATA_MAP)}
                    >
                        表详情
                    </MenuItem>
                    <MenuItem>编辑表</MenuItem>
                    <MenuItem>显示建表DDL</MenuItem>
                    <MenuItem>复制表名</MenuItem>
                    <MenuItem>新建DataMap</MenuItem>
                </ContextMenu>
                <ContextMenu targetClassName="anchor-datamap">
                    <MenuItem onClick={
                        this.onCxtMenuItem.bind(CTX_ACTION.SHOW_DATA_MAP)}
                    >
                        查看详情
                    </MenuItem>
                </ContextMenu>
            </div>
        )
    }
}

export default Sidebar;
