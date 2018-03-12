import React from 'react';
import { connect } from 'react-redux';
import { 
    Tree, TreeSelect, Dropdown, Menu,
    Input, Tooltip, Icon,
} from 'antd';

import Api from '../../../../api'
import { debounceEventHander } from 'funcs'

import {
    tableTreeAction,
} from '../../../../store/modules/offlineTask/actionType';

import TableInfoPane from './tableInfoPane';

const { TreeNode } = Tree;

// 映射State
const stateToProps = (state) => {
    return {}
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
    }

    onLoadData = (treeNode) => {
        const { loadTreeNode, dispatch } = this.props;
        const { data } = treeNode.props;
        const params = { pageSize: 1000, isDeleted: 0, isDirtyDataTable: 0 };

        return new Promise((resolve) => {
            if (!data.children || data.children.length === 0) {
                Api.searchTable(params).then(res => {
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
        if (value === "") this.setState({ tableId: ''})
        this.doReq(value)
    }

    refresh = () => {
        this.doReq('')
        this.setState({ tableId: '' })
    }
    
    doReq = (queryName) => {
        const { treeData, loadTreeNode } = this.props;
        Api.searchTable({
            tableName: queryName,
            isDeleted: 0,
            pageSize: 1000,
            isDirtyDataTable: 0
        }).then(res => {
            treeData.children = res.data && res.data.data;
            loadTreeNode(treeData)
        })
    }

    onClickSearch =() => {
        this.setState({ displaySearch: true }, () => {
            const input = document.getElementById('tableTreeInput')
            if (input) input.focus()
        })
    }

    handleSelect = (key, { node }) => {
        const table = node.props.data
        this.setState({ tableId: table.tableId })
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
                    {data.createUser || data.userName}
                </i>
            </span>

            return data ? (
                <TreeNode 
                    title={title}
                    key={id}
                    value={id}
                    isLeaf={isFile}
                    data={data}
                    className={'s-table'}
                >
                    {data.children && data.children.map(subItem => loop(subItem)) }
                </TreeNode>
            ) : null
        }
        return loop(treeData)
    }

    render() {
        const { displaySearch, tableId } = this.state
        const display = displaySearch ? 'block' : 'none';
        return (
            <div className="menu-content">
                <header>
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
                            style={{fontSize: '12px'}}
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
                <div className="tb-list" style={{maxHeight: tableId ? '500px' : '100%' }}>
                    <Tree
                        showIcon={true}
                        loadData={this.onLoadData}
                        onSelect={this.handleSelect}
                    >
                        { this.renderNodes() }
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