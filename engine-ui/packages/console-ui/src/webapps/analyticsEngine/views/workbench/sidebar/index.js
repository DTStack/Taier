import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { union } from 'lodash';
import { Input, message } from 'antd';

import utils from 'utils';
import CopyUtils from 'utils/copy';

import {
    ContextMenu,
    MenuItem,
} from 'widgets/context-menu';

import ToolBar from './toolbar';
import FolderTree from './folderTree';
import workbenchActions from '../../../actions/workbenchActions';
import MyIcon from '../../../components/icon';
import { CATALOGUE_TYPE } from '../../../consts';
import { onTableDetail } from '../../../actions/workbenchActions/table';


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
        expandedKeys: [],
        selectedKeys: [],
    }

    componentDidMount() {
        this.props.loadCatalogue();
    }

    refresh = () => {
        this.setState({
            activeNode: null,
            expandedKeys: [],
            selectedKeys: [],
        })
        this.props.loadCatalogue();
    }

    searchTable = (value) => {
        const query = utils.trim(value);
        if (!query) {
            this.props.loadCatalogue();
        };

        this.props.loadCatalogue({
            tableName: value,
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

    onRightClick = ({ node }) => {
        const activeNode = node.props.data;
        this.setState({ activeNode: activeNode })
    }

    asynLoadCatalogue = (treeNode) => {
        const ctx = this;
        const { data, fileType } = treeNode.props;
        return new Promise(async (resolve) => {
            if (!data.children || data.children.length === 0) {
                ctx.props.loadCatalogue(data, fileType);
            }
            resolve();
        });
    }

    onNodeSelect = (selectedKeys, { node }) => {

        const { expandedKeys } = this.state;
        const { eventKey, fileType } = node.props;
        if (fileType === CATALOGUE_TYPE.DATA_MAP ) return false;

        this.setState({
            selectedKeys, 
        });

        const eventKeyIndex = expandedKeys.indexOf(eventKey);
        this.asynLoadCatalogue(node);

        if (eventKeyIndex > -1) {
            expandedKeys.splice(eventKeyIndex, 1);
            this.onExpand(expandedKeys, { expanded: false })
        } else {
            expandedKeys.push(eventKey);
            this.onExpand(expandedKeys, { expanded: true })
        }
    }

    onExpand = (expandedKeys, { expanded }) => {
        let keys = expandedKeys;
        if (expanded) {
            keys = union(this.state.expandedKeys, keys)
        }
        this.setState({
            expandedKeys: keys,
        })
    }

    renderFolderContent = () => {

        const {
            folderTree,
            onGetTable,
            onGetDataMap,
            onCreateDB,
            onSQLQuery,
            onGetDB
        } = this.props;

        if (folderTree && folderTree.children && folderTree.children.length > 0) {
            return (
                <div>
                    <div style={{ position: 'initial', margin: '15px 15px 0 15px' }}>
                        <Search
                            placeholder="输入表名搜索"
                            onSearch={this.searchTable}
                        />
                    </div>
                    <FolderTree
                        onRightClick={this.onRightClick}
                        loadData={this.asynLoadCatalogue}
                        onSelect={this.onNodeSelect}
                        onExpand={this.onExpand}
                        expandedKeys={this.state.expandedKeys}
                        selectedKeys={this.state.selectedKeys}
                        treeData={folderTree.children}
                        onGetDB={onGetDB}
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

        const { activeNode } = this.state;

        const {
            onGetDB,
            onGetDataMap,
            onCreateDB,
            onCreateTable,
            onSQLQuery,
            onCreateDataMap,
            onGenerateCreateSQL,
            onEditTable,
            onTableDetail,
        } = this.props;

        return (
            <div className="sidebar">
                <ToolBar
                    onRefresh={this.refresh}
                    onCreateDB={() => onCreateDB()}
                    onSQLQuery={() => onSQLQuery()}
                    onEditTable = {()=>onEditTable()}
                    onCreateTable={() => onCreateTable()}
                    onTableDetail={()=>onTableDetail()}
                />
                {
                    this.renderFolderContent()
                }
                <ContextMenu targetClassName="anchor-database">
                    <MenuItem onClick={()=>onCreateTable(activeNode)}>新建表</MenuItem>
                    <MenuItem onClick={() => onGetDB({ databaseId: activeNode.id })}>
                        查看详情
                    </MenuItem>
                </ContextMenu>
                <ContextMenu targetClassName="anchor-table">
                    <MenuItem onClick={() => onSQLQuery(activeNode) }>查询</MenuItem>
                    <MenuItem onClick={()=> onEditTable(activeNode)}>编辑表</MenuItem>
                    <MenuItem onClick={()=> onTableDetail(activeNode)}>表详情</MenuItem>
                    <MenuItem onClick={() => onGenerateCreateSQL({
                        tableId: activeNode.id,
                        databaseId: activeNode.databaseId,
                    })}>
                        显示建表DDL
                    </MenuItem>
                    <MenuItem onClick={this.copyName}>复制表名</MenuItem>
                    <MenuItem onClick={() => onCreateDataMap(activeNode) }>新建DataMap</MenuItem>
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
