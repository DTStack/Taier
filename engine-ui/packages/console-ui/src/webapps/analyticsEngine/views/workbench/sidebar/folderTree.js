import React from 'react';
import { Tree, Tooltip, Icon } from 'antd';

import { CATALOGUE_TYPE } from '../../../consts';
import MyIcon from '../../../components/icon';

const TreeNode = Tree.TreeNode;

const getItemClassName = function(type) {
    switch(type) {
        case CATALOGUE_TYPE.DATA_BASE:
            return 's-database anchor-database';
        case CATALOGUE_TYPE.DATA_MAP:
            return 's-datamap anchor-datamap';
        case CATALOGUE_TYPE.TABLE:
            return 's-table anchor-table';
        case CATALOGUE_TYPE.FOLDER:
        default: return 's-tree-item anchor-tree-item';
    }
}

const getContextMenuAnchorName = function(type) {
    switch(type) {
        case CATALOGUE_TYPE.DATA_BASE:
            return 'anchor-database';
        case CATALOGUE_TYPE.DATA_MAP:
            return 'anchor-datamap';
        case CATALOGUE_TYPE.TABLE:
            return 'anchor-table';
        case CATALOGUE_TYPE.FOLDER:
        default: return 'anchor-tree-item';
    }
}

class FolderTree extends React.PureComponent {

    constructor(props) {
        super(props)
    }

    renderNodeHoverButton = (item) => {
        const { onTableDetail, onGetDataMap, onSQLQuery, onGetDB } = this.props;
        switch(item.type) {
            case CATALOGUE_TYPE.DATA_BASE:
            return (
                <span className="tree-node-hover-items">
                    <MyIcon type="btn_sql_query" className="tree-node-hover-item" 
                        title="SQL查询" 
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
        const { treeData } = this.props;
        const loop = (data) => {

            return data && data.map(item => {

                const id = `${item.id || item.tableId}`
                const name = item.name || item.tableName
                const isLeaf = !item.children;
                // 用作展示上下文的锚点， 暂时取消
                // const itemAnchorName = getContextMenuAnchorName(item.type);
                const className = getItemClassName(item.type);

                const nodeTitle = (
                    <Tooltip placement="bottomLeft" mouseEnterDelay={0.5}>
                        <span
                            title={name}
                            style={{padding:"8px 0px"}}
                        >
                            {name}
                            { this.renderNodeHoverButton(item) }
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

    render() {
        return (
            <div className="s-catalogue">
                <Tree
                    showIcon={true}
                    autoExpandParent={false}
                    expandedKeys={this.props.expandedKeys}
                    loadData={this.props.loadData}
                    onSelect={this.props.onSelect}
                    onExpand={this.props.onExpand}
                    onRightClick={this.props.onRightClick}
                >
                    {this.renderNodes()}
                </Tree>
            </div>
        )
    }
}

export default FolderTree;
