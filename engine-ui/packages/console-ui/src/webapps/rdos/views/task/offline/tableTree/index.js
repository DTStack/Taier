import React from 'react';
import { connect } from 'react-redux';
import { hashHistory } from "react-router";
import { union } from "lodash";
import {
    Tree, TreeSelect, Dropdown, Menu,
    Input, Tooltip, Icon, Select
} from 'antd';

import Api from '../../../../api/index'
import { debounceEventHander } from 'funcs'

import {
    tableTreeAction,
} from '../../../../store/modules/offlineTask/actionType';
import {
    workbenchActions
} from '../../../../store/modules/offlineTask/offlineAction'

import TableInfoPane from './tableInfoPane';
import { MENU_TYPE } from '../../../../comm/const';

const { TreeNode } = Tree;
const Option = Select.Option;


// 映射State
const stateToProps = (state) => {
    return {
        project: state.project
    }
}

@connect(stateToProps, workbenchActions)
class TableTree extends React.Component {

    state = {
        displaySearch: false,
        tableId: '',
        projectId: "all",
        expandedKeys: [],
        searchName:""
    }
    componentWillReceiveProps(nextProps){
        if(this.props.project.id!=nextProps.project.id){
            this.setState({
                projectId:"all",
                expandedKeys:[],
                tableId:'',
                searchName:""
            })
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
    onLoadData = (treeNode) => {
        const { loadTreeNode, dispatch } = this.props;
        const { projectId, searchName } = this.state;
        const { data } = treeNode.props;
        const params = {
            isDeleted: 0,
            isDirtyDataTable: 0,
            appointProjectId: projectId == "all" ? null : projectId,
        };
        return new Promise((resolve) => {
            if (!data.children || data.children.length === 0) {
                if (searchName) {
                    this.doReq();
                } else {
                    loadTreeNode(data.id, MENU_TYPE.TABLE, params)
                }
            }
            resolve();
        });
    }

    search = (e) => {
        e.preventDefault();
        const value = e.target.value;
        if (value === "") this.setState({ tableId: '' })
        this.setState({
            searchName: value
        })
        this.doReq(value)
    }

    refresh = () => {
        this.doReq('')
        this.setState({ tableId: '' })
    }

    doReq = (queryName) => {
        const { projectId, searchName } = this.state;
        const { treeData, loadTreeNode, loadTableListNodeByName } = this.props;
        this.setState({
            expandedKeys: [treeData.id + '']
        })
        if (searchName) {
            loadTableListNodeByName(treeData.id, {
                tableName: queryName || searchName,
                appointProjectId: projectId == "all" ? null : projectId,
                isDirtyDataTable: 0,
            })
        } else {
            loadTreeNode(treeData.id, MENU_TYPE.TABLE, {
                tableName: queryName || searchName,
                appointProjectId: projectId == "all" ? null : projectId,
                isDirtyDataTable: 0,
            })
        }
    }

    onClickSearch = () => {
        this.setState({ displaySearch: true }, () => {
            const input = document.getElementById('tableTreeInput')
            if (input) input.focus()
        })
    }

    handleSelect = (key, { node }) => {
        const table = node.props.data
        if (table && table.type !== 'folder') {
            this.setState({ tableId: table.id })
        }
    }
    jumpToDataMap(id) {
        hashHistory.push({
            pathname: "data-manage/table/view/" + id
        })
    }
    renderNodes = () => {
        const { treeData } = this.props;
        const loop = (data) => {
            const id = `${data.id || data.tableId}`
            const isFile = data.type === 'file' || !data.type
            const isFolder = data.type === 'folder'
            const name = data.name || data.tableName

            const title = <span
                title={name}
                className={isFolder ? 'folder-item' : 'file-item'}
            >
                {name}
                <i style={{ color: 'rgb(217, 217, 217)', fontSize: '12px' }}>
                    {data.chargeUser || data.userName}
                </i>
            </span>
            const nodeTitle = isFolder ? name : (
                <Tooltip overlayClassName="table-detail-card" title={(
                    <div className="detail-content">
                        <p className="text-item"><span className="text-item-name">表名</span><span className="text-item-value">{name}</span></p>
                        <p className="text-item"><span className="text-item-name">责任人</span><span className="text-item-value">{data.chargeUser}</span></p>
                        <p className="text-item"><span className="text-item-name">项目名称</span><span className="text-item-value">{data.projectAlias || "-"}</span></p>
                        <p className="text-item"><span className="text-item-name">生命周期</span><span className="text-item-value">{data.lifeDay ? `${data.lifeDay}天` : '-'}</span></p>
                        <p className="text-item"><span className="text-item-name">描述</span><span className="text-item-value">{data.tableDesc || "-"}</span></p>
                        <a onClick={this.jumpToDataMap.bind(this, data.id)}>更多详情</a>
                    </div>
                )}
                    placement="bottomLeft"
                    mouseEnterDelay={0.5}>
                    {name}
                </Tooltip>
            )

            return data ? (

                <TreeNode
                    title={nodeTitle}
                    key={isFolder ? id : `${id}-file`}
                    value={id}
                    isLeaf={isFile}
                    data={data}
                    className={'s-table'}
                >
                    {data.children && data.children.map(subItem => loop(subItem))}
                </TreeNode>
            ) : null
        }
        const result = loop(treeData)
        return result;
    }
    tableChange(value) {
        this.setState({
            projectId: value
        }, this.doReq)
    }
    render() {
        const { displaySearch, tableId, projectId, expandedKeys } = this.state
        const { project } = this.props;
        const display = displaySearch ? 'block' : 'none';
        return (
            <div className="menu-content" style={{ position: "relative" }}>
                <header style={{ left: "13px" }}>
                    <Select value={projectId} onChange={this.tableChange.bind(this)} size="small" style={{ width: "90px", marginTop: "6.5px", float: "left" }}>
                        <Option value="all">全部项目</Option>
                        <Option value={project.id}>{project.projectAlias}</Option>
                    </Select>
                    <Tooltip title="表查询">
                        <Icon
                            type="search"
                            style={{ marginRight: '8px' }}
                            className="right" onClick={this.onClickSearch}
                        />
                    </Tooltip>

                    <Tooltip title="刷新">
                        <Icon
                            type="sync"
                            style={{ fontSize: '12px' }}
                            className="right" onClick={this.refresh}
                        />
                    </Tooltip>
                    <div className="tab-search" style={{ display }}>
                        <Input
                            id="tableTreeInput"
                            placeholder="按表名称搜索"
                            onChange={debounceEventHander(this.search, 500, { 'maxWait': 2000 })}
                        />
                    </div>
                    <div className="mask-layer" style={{ display }} onClick={() => {
                        this.setState({ displaySearch: false })
                    }}></div>
                </header>
                <div className="tb-list" style={{ maxHeight: tableId ? '500px' : '100%', minHeight: '200px', paddingTop: "30px" }}>
                    <TreeContent
                        showIcon={true}
                        loadData={this.onLoadData}
                        onSelect={this.handleSelect}
                        expandedKeys={expandedKeys}
                        autoExpandParent={false}
                        onExpand={this.onExpand}
                        treeData={this.props.treeData}
                    />

                </div>
                {
                    tableId && <div className="tb-info bd-top">
                        <TableInfoPane tableId={tableId} />
                    </div>
                }
            </div>
        )
    }
}

//抽离组件防止其他影响的卡顿
class TreeContent extends React.PureComponent {
    jumpToDataMap(id) {
        hashHistory.push({
            pathname: "data-manage/table/view/" + id
        })
    }
    renderNodes = () => {
        const { treeData } = this.props;
        const loop = (data) => {
            const id = `${data.id || data.tableId}`
            const isFile = data.type === 'file' || !data.type
            const isFolder = data.type === 'folder'
            const name = data.name || data.tableName

            const title = <span
                title={name}
                className={isFolder ? 'folder-item' : 'file-item'}
            >
                {name}
                <i style={{ color: 'rgb(217, 217, 217)', fontSize: '12px' }}>
                    {data.chargeUser || data.userName}
                </i>
            </span>
            const nodeTitle = isFolder ? name : (
                <Tooltip overlayClassName="table-detail-card" title={(
                    <div className="detail-content">
                        <p className="text-item"><span className="text-item-name">表名</span><span className="text-item-value">{name}</span></p>
                        <p className="text-item"><span className="text-item-name">责任人</span><span className="text-item-value">{data.chargeUser}</span></p>
                        <p className="text-item"><span className="text-item-name">项目名称</span><span className="text-item-value">{data.projectAlias || "-"}</span></p>
                        <p className="text-item"><span className="text-item-name">生命周期</span><span className="text-item-value">{data.lifeDay ? `${data.lifeDay}天` : '-'}</span></p>
                        <p className="text-item"><span className="text-item-name">描述</span><span className="text-item-value">{data.tableDesc || "-"}</span></p>
                        <a onClick={this.jumpToDataMap.bind(this, data.id)}>更多详情</a>
                    </div>
                )}
                    placement="bottomLeft"
                    mouseEnterDelay={0.5}>
                    {name}
                </Tooltip>
            )

            return data ? (

                <TreeNode
                    title={nodeTitle}
                    key={isFolder ? id : `${id}-file`}
                    value={id}
                    isLeaf={isFile}
                    data={data}
                    className={'s-table'}
                >
                    {data.children && data.children.map(subItem => loop(subItem))}
                </TreeNode>
            ) : null
        }
        const result = loop(treeData)
        return result;
    }

    render() {
        console.log("render")
        return (
            <Tree
                showIcon={true}
                loadData={this.props.loadData}
                onSelect={this.props.onSelect}
                expandedKeys={this.props.expandedKeys}
                autoExpandParent={false}
                onExpand={this.props.onExpand}
            >
                {this.renderNodes()}
            </Tree>
        )
    }
}


export default TableTree