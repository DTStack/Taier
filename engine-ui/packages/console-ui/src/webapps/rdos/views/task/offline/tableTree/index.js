import React from 'react';
import { connect } from 'react-redux';
import {hashHistory} from "react-router";
import {
    Tree, TreeSelect, Dropdown, Menu,
    Input, Tooltip, Icon, Select
} from 'antd';

import Api from '../../../../api/dataManage'
import { debounceEventHander } from 'funcs'

import {
    tableTreeAction,
} from '../../../../store/modules/offlineTask/actionType';

import TableInfoPane from './tableInfoPane';

const { TreeNode } = Tree;
const Option = Select.Option;

// 映射State
const stateToProps = (state) => {
    return {
        project:state.project
    }
}

// 映射Props
const actionToProps = (dispatch) => {
    return {
        loadTreeNode(data) {
            dispatch({
                type: tableTreeAction.ADD_FOLDER_CHILD,
                payload: data,
            });
        },
    }
}

@connect(stateToProps, actionToProps)
class TableTree extends React.Component {

    state = {
        displaySearch: false,
        tableId: '',
        projectId:"all",
    }

    onLoadData = (treeNode) => {
        const { loadTreeNode, dispatch } = this.props;
        const { data } = treeNode.props;
        const params = { pageSize: 1000, isDeleted: 0, isDirtyDataTable: 0 };
        return new Promise((resolve) => {
            if (!data.children || data.children.length === 0) {
                Api.queryTable(params).then(res => {
                    data.children = res.data && res.data.data;
                    loadTreeNode(data)
                })
            }
            resolve();
        });
    }

    search = (e) => {
        e.preventDefault();
        const value = e.target.value;
        if (value === "") this.setState({ tableId: '' })
        this.doReq(value)
    }

    refresh = () => {
        this.doReq('')
        this.setState({ tableId: '' })
    }

    doReq = (queryName) => {
        const {projectId,tableId} = this.state;
        const { treeData, loadTreeNode } = this.props;
        Api.queryTable({
            tableName: queryName||tableId,
            pageSize: 1000,
            pageIndex: 1,
            appointProjectId:projectId=="all"?null:projectId
        }).then(res => {
            treeData.children = res.data && res.data.data;
            loadTreeNode(treeData)
        })
    }

    onClickSearch = () => {
        this.setState({ displaySearch: true }, () => {
            const input = document.getElementById('tableTreeInput')
            if (input) input.focus()
        })
    }

    handleSelect = (key, { node }) => {
        const table = node.props.data
        this.setState({ tableId: table.id })
    }
    jumpToDataMap(id){
        hashHistory.push({
            pathname:"data-manage/table/view/"+id
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
            const nodeTitle=isFolder?name:(
                <Tooltip overlayClassName="table-detail-card" title={(
                    <div className="detail-content">
                        <p className="text-item"><span className="text-item-name">表名</span><span className="text-item-value">{name}</span></p>
                        <p className="text-item"><span className="text-item-name">责任人</span><span className="text-item-value">{data.chargeUser}</span></p>
                        <p className="text-item"><span className="text-item-name">项目名称</span><span className="text-item-value">{data.projectAlias||"-"}</span></p>
                        <p className="text-item"><span className="text-item-name">生命周期</span><span className="text-item-value">{data.lifeDay?`${data.lifeDay}天`:'-'}</span></p>
                        <p className="text-item"><span className="text-item-name">描述</span><span className="text-item-value">{data.tableDesc||"-"}</span></p>
                        <a onClick={this.jumpToDataMap.bind(this,data.id)}>更多详情</a>
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
                    key={id}
                    value={id}
                    isLeaf={isFile}
                    data={data}
                    className={'s-table'}
                >
                    {data.children && data.children.map(subItem => loop(subItem))}
                </TreeNode>
            ) : null
        }
        return loop(treeData)
    }
    tableChange(value){
        this.setState({
            projectId:value
        },this.doReq)
    }
    render() {
        const { displaySearch, tableId, projectId } = this.state
        const {project} = this.props;
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
                    <Tree
                        showIcon={true}
                        loadData={this.onLoadData}
                        onSelect={this.handleSelect}
                    >
                        {this.renderNodes()}
                    </Tree>

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

export default TableTree