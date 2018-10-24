import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import { Input } from 'antd';

import { debounceEventHander } from 'funcs';

import {
    ContextMenu,
    MenuItem,
} from 'widgets/context-menu';

import ToolBar from './toolbar';
import FolderTree from './folderTree';
import workbenchActions from '../../../actions/workbenchActions';
import MyIcon from '../../../components/icon';

const CTX_ACTION = {
    SHOW_DATA_MAP: 'SHOW_DATA_MAP',
}
const Search = Input.Search;

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

    asynLoadCatalogue(treeNode) {
        const { data, fileType } = treeNode.props;
        return new Promise((resolve) => {
            if (!data.children || data.children.length === 0) {
                this.props.loadCatalogue({
                    nodePid: data.id,
                }, fileType)
            }
            resolve();
        });
    }

    renderFolderContent = () => {

        const {
            folderTree,
            onGetTable,
            onGetDataMap,
            onCreateDB,
            onSQLQuery
        } = this.props;

        if (folderTree && folderTree.children && folderTree.children.length > 0) {
            return (
                <div>
                    <div style={{ position: 'initial', margin: '15px 15px 0 15px' }}>
                        <Search
                            placeholder="输入表名搜索"
                            onSearch={debounceEventHander(this.searchTable, 500, { 'maxWait': 2000 })}
                        />
                    </div>
                    <FolderTree
                        onRightClick={this.onRightClick}
                        loadData={this.asynLoadCatalogue}
                        onSelect={this.onSelectCatalogeuItem}
                        treeData={folderTree.children}
                        onGetTable={onGetTable}
                        onGetDataMap={onGetDataMap}
                        onSQLQuery={onSQLQuery}
                    />
                </div>
            )
        } else {
            return (
                <p style={{
                    padding: '86px 36px', 
                    fontSize: '14px', 
                    color: '#666666',
                    letterSpacing: 0,
                }}>
                    &nbsp;点击上方&nbsp;
                        <MyIcon
                            title="创建数据库"
                            onClick={onCreateDB}
                            type="btn_add_database"
                            style={{cursor: 'pointer'}}
                        />&nbsp;
                        新建数据库或联系管理员获取访问权限
                </p>
            )
        }
    }

    render() {

        const { activeNode } = this.props;

        const {
            onGetDB,
            onGetDataMap,
            onCreateDB,
            onCreateTable,
            onSQLQuery,
            loadCatalogue,
        } = this.props;

        return (
            <div className="sidebar">
                <ToolBar
                    onRefresh={() => loadCatalogue()}
                    onCreateDB={() => onCreateDB()}
                    onSQLQuery={() => onSQLQuery()}
                    onCreateTable={() => onCreateTable()}
                />
                {
                    this.renderFolderContent()
                }
                <ContextMenu targetClassName="anchor-database">
                    <MenuItem onClick={this.initEditTask}>新建表</MenuItem>
                    <MenuItem onClick={() => onGetDB(activeNode)}>
                        查看详情
                    </MenuItem>
                </ContextMenu>
                <ContextMenu targetClassName="anchor-table">
                    <MenuItem>查询</MenuItem>
                    <MenuItem>编辑表</MenuItem>
                    <MenuItem>表详情</MenuItem>
                    <MenuItem>编辑表</MenuItem>
                    <MenuItem>显示建表DDL</MenuItem>
                    <MenuItem>复制表名</MenuItem>
                    <MenuItem>新建DataMap</MenuItem>
                </ContextMenu>
                <ContextMenu targetClassName="anchor-datamap">
                    <MenuItem onClick={() => onGetDataMap(activeNode)}>
                        查看详情
                    </MenuItem>
                </ContextMenu>
            </div>
        )
    }
}

export default Sidebar;
