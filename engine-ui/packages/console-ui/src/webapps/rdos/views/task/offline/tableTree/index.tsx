import * as React from 'react';
import { connect } from 'react-redux';
import { union } from 'lodash';
import {
    Tree,
    Input, Tooltip, Icon, Select
} from 'antd';

import { debounceEventHander } from 'funcs'

import {
    workbenchActions
} from '../../../../store/modules/offlineTask/offlineAction'

import TableInfoPane from './tableInfoPane';
import { MENU_TYPE } from '../../../../comm/const';

const { TreeNode } = Tree;
const Option = Select.Option;

// 映射State
const stateToProps = (state: any) => {
    return {
        project: state.project
    }
}

@(connect(stateToProps, workbenchActions) as any)
class TableTree extends React.Component<any, any> {
    state: any = {
        displaySearch: false,
        tableId: '',
        projectId: 'all',
        expandedKeys: [],
        searchName: ''
    }
    /* eslint-disable */
    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        if (this.props.project.id != nextProps.project.id) {
            this.setState({
                projectId: 'all',
                expandedKeys: [],
                tableId: '',
                searchName: ''
            })
        }
    }
    /* eslint-disable */

    onExpand = (expandedKeys: any, { expanded }: any) => {
        let keys = expandedKeys;
        if (expanded) {
            keys = union(this.state.expandedKeys, keys)
        }
        this.setState({
            expandedKeys: keys
        })
    }

    handleSelect = (key: any, { node }: any) => {
        const table = node.props.data
        const { expandedKeys } = this.state;
        if (table && table.type !== 'folder') {
            this.setState({ tableId: table.id })
        } else if (table && table.type === 'folder') {
            const { eventKey } = node.props;
            const eventKeyIndex = expandedKeys.indexOf(eventKey);
            if (eventKeyIndex > -1) {
                this.onLoadData(node);
                expandedKeys.splice(eventKeyIndex, 1);
                this.onExpand(expandedKeys, { expanded: false });
            } else {
                this.onLoadData(node)
                expandedKeys.push(eventKey)
                this.onExpand(expandedKeys, { expanded: true })
            }
        }
    }

    onLoadData = (treeNode: any) => {
        const { data } = treeNode.props;
        return new Promise((resolve: any) => {
            if (!data.children || data.children.length === 0) {
                this.doReq(null, data.id);
            }
            resolve();
        });
    }

    search = (e: any) => {
        e.preventDefault();
        const value = e.target.value;
        if (value === '') this.setState({ tableId: '' })
        this.setState({
            searchName: value
        })
        this.doReq(value)
    }

    refresh = () => {
        this.setState({ tableId: '', searchName: '' }, () => {
            this.doReq('')
        })
    }

    doReq = (queryName?: any, id?: any) => {
        const { projectId, searchName } = this.state;
        const { treeData, loadTreeNode, loadTableListNodeByName } = this.props;
        const nodeId = typeof id == 'undefined' ? treeData.id : id
        if (searchName || projectId != 'all') {
            this.setState({
                expandedKeys: [treeData.id + '']
            })
            loadTableListNodeByName(nodeId, {
                tableName: queryName || searchName,
                appointProjectId: projectId == 'all' ? null : projectId,
                isDirtyDataTable: 0
            })
        } else {
            if (typeof id == 'undefined') {
                this.setState({
                    expandedKeys: [treeData.id + '']
                })
            }
            loadTreeNode(nodeId, MENU_TYPE.TABLE, {
                tableName: queryName || searchName,
                appointProjectId: projectId == 'all' ? null : projectId,
                isDirtyDataTable: 0
            })
        }
    }

    onClickSearch = () => {
        this.setState({ displaySearch: true }, () => {
            const input = document.getElementById('tableTreeInput')
            if (input) input.focus()
        })
    }

    tableChange(value: any) {
        this.setState({
            projectId: value
        }, this.doReq)
    }
    render () {
        const { displaySearch, tableId, projectId, expandedKeys } = this.state
        const { project } = this.props;
        const display = displaySearch ? 'block' : 'none';
        return (
            <div className="menu-content" style={{ position: 'relative' }}>
                <header style={{ left: '13px' }}>
                    <Select value={projectId} onChange={this.tableChange.bind(this)} size="small" style={{ width: '103px', float: 'left', margin: '5px 0 0 12px' }}>
                        <Option value="all">全部项目</Option>
                        <Option value={project.id}>{project.projectAlias}</Option>
                    </Select>
                    <Tooltip title="表查询">
                        <Icon
                            type="search"
                            style={{ fontSize: '15px' }}
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
                <div className="contentBox">
                    <div className="folder-box">
                        <div className="tb-list">
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
                    </div>
                </div>
                {
                    tableId && <div className="tb-info tb-info--noBorderBottom">
                        <TableInfoPane tableId={tableId} />
                    </div>
                }
            </div >
        )
    }
}

// 抽离组件防止其他影响的卡顿
class TreeContent extends React.PureComponent<any, any> {
    jumpToDataMap(id: any) {
        window.open(`${location.pathname}#/data-manage/table/view/${id}`);
    }

    renderNodes = () => {
        const { treeData } = this.props;
        const loop = (data: any) => {
            const id = `${data.id || data.tableId}`
            const isFile = data.type === 'file' || !data.type
            const isFolder = data.type === 'folder'
            const name = data.name || data.tableName

            const nodeTitle = isFolder ? name : (
                <Tooltip overlayClassName="table-detail-card" title={(
                    <div className="detail-content">
                        <p className="text-item"><span className="text-item-name">表名</span><span className="text-item-value">{name}</span></p>
                        <p className="text-item"><span className="text-item-name">责任人</span><span className="text-item-value">{data.chargeUser}</span></p>
                        <p className="text-item"><span className="text-item-name">项目名称</span><span className="text-item-value">{data.projectAlias || '-'}</span></p>
                        <p className="text-item"><span className="text-item-name">生命周期</span><span className="text-item-value">{data.lifeDay ? `${data.lifeDay}天` : '-'}</span></p>
                        <p className="text-item"><span className="text-item-name">描述</span>
                            <span className="text-item-value">
                                {!data.tableDesc ? '-' : data.tableDesc.length > 60 ? <Tooltip title={data.tableDesc}>{data.tableDesc.substr(0, 60)}......</Tooltip> : data.tableDesc}
                            </span>
                        </p>
                        <a onClick={this.jumpToDataMap.bind(this, data.id)}>更多详情</a>
                    </div>
                )}
                placement="bottomLeft"
                mouseEnterDelay={0.5}>
                    <span style={{ padding: '8px 0px' }}>{name}</span>
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
                    {data.children && data.children.map((subItem: any) => loop(subItem))}
                </TreeNode>
            ) : null
        }
        const result = loop(treeData)
        return result;
    }

    render () {
        console.log('render')
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
