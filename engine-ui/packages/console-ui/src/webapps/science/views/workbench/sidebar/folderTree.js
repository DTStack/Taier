import React from 'react';
import { Tree, Tooltip, Icon } from 'antd';

import { CATALOGUE_TYPE } from '../../../consts';
import MyIcon from '../../../components/icon';

import {
    ContextMenu,
    MenuItem
} from 'widgets/context-menu';

const TreeNode = Tree.TreeNode;

class FolderTree extends React.PureComponent {
    constructor (props) {
        super(props)
        this.state = {
            activeNode: null
        }
    }

    renderNodeHoverButton = (item) => {
        const { onTableDetail, onGetDataMap, onSQLQuery, onGetDB } = this.props;
        switch (item.type) {
            case CATALOGUE_TYPE.DATA_BASE:
                return (
                    <span className="tree-node-hover-items">
                        <MyIcon type="btn_sql_query" className="tree-node-hover-item"
                            title="SQL查询"
                            style={{ width: 15, height: 15 }}
                            onClick={(e) => {
                                e.stopPropagation();
                                onSQLQuery(item);
                            }}
                        />
                        <Icon className="tree-node-hover-item" title="查看详情" type="exclamation-circle-o"
                            onClick={(e) => {
                                e.stopPropagation();
                                onGetDB({ databaseId: item.id });
                            }
                            }
                        />
                    </span>
                )
            case CATALOGUE_TYPE.DATA_MAP:
                return (
                    <span className="tree-node-hover-items">
                        <Icon className="tree-node-hover-item" title="查看详情" type="exclamation-circle-o"
                            onClick={(e) => {
                                e.stopPropagation();
                                onGetDataMap({ id: item.id });
                            }}
                        />
                    </span>
                )
            case CATALOGUE_TYPE.TABLE:
                return (
                    <span className="tree-node-hover-items">
                        <MyIcon type="btn_sql_query" className="tree-node-hover-item"
                            style={{ width: 15, height: 15 }}
                            title="SQL查询"
                            onClick={(e) => {
                                e.stopPropagation();
                                onSQLQuery(item);
                            }}
                        />
                        <Icon
                            className="tree-node-hover-item" title="查看详情" type="exclamation-circle-o"
                            onClick={(e) => {
                                e.stopPropagation();
                                onTableDetail(item);
                            }}
                        />
                    </span>
                )
            case CATALOGUE_TYPE.FOLDER:
            default: return '';
        }
    }

    renderNodes = () => {
        const { treeData, targetClassName } = this.props;
        const loop = (data) => {
            return data && data.map(item => {
                const id = `${item.id || item.tableId}`
                const name = item.name || item.tableName
                const isLeaf = !item.children || item.children.length === 0;
                // 用作展示上下文的锚点， 暂时取消
                const className = targetClassName;

                const nodeTitle = (
                    <Tooltip placement="bottomLeft" mouseEnterDelay={0.5}>
                        <span
                            title={name}
                            style={{ padding: '8px 0px' }}
                        >
                            {name}
                            {this.renderNodeHoverButton(item)}
                        </span>
                    </Tooltip>
                )
                return (
                    <TreeNode
                        title={nodeTitle}
                        key={`${item.type}-${id}`}
                        value={id}
                        isLeaf={isLeaf}
                        data={item}
                        fileType={item.type}
                        className={className}
                    >
                        {
                            item.children && loop(item.children)
                        }
                    </TreeNode>
                )
            })
        }

        const result = loop(treeData)
        return result;
    }
    onRightClick (e, node) {
        const activeNode = e.node.props.data;
        this.setState({
            activeNode
        })
    }
    renderContextMenu () {
        const { contextMenu = [], targetClassName } = this.props;
        const { activeNode } = this.state;
        if (!contextMenu || !contextMenu.length) {
            return null;
        }
        return <ContextMenu targetClassName={targetClassName}>
            {contextMenu.map((menu) => {
                return (<MenuItem key={menu.text} onClick={() => {
                    menu.onClick(activeNode);
                }}>{menu.text}</MenuItem>)
            })}
        </ContextMenu>
    }
    render () {
        return (
            <div className="s-catalogue">
                <Tree
                    showIcon={true}
                    autoExpandParent={false}
                    expandedKeys={this.props.expandedKeys}
                    selectedKeys={this.props.selectedKeys}
                    loadData={this.props.loadData}
                    onSelect={this.props.onSelect}
                    onExpand={this.props.onExpand}
                    onRightClick={this.onRightClick.bind(this)}
                >
                    {this.renderNodes()}
                </Tree>
                {this.renderContextMenu()}
            </div>
        )
    }
}

export default FolderTree;
