import React from 'react';
import { Tree, Tooltip, Icon } from 'antd';

import { CATALOGUE_TYPE } from '../../../consts';

const TreeNode = Tree.TreeNode;

const getItemClassName = function(type) {
    switch(type) {
        case CATALOGUE_TYPE.DATA_BASE:
            return 's-database';
        case CATALOGUE_TYPE.DATA_MAP:
            return 's-datamap';
        case CATALOGUE_TYPE.TABLE:
            return 's-table';
        case CATALOGUE_TYPE.FOLDER:
        default: return 's-tree-item';
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

    renderNodeHoverButton = (type) => {
        switch(type) {
            case CATALOGUE_TYPE.DATA_MAP:
                return (
                    <span className="tree-node-hover-items">
                        <Icon className="tree-node-hover-item" title="查看详情" type="exclamation-circle-o" />
                    </span>
                )
            case CATALOGUE_TYPE.TABLE:
                return (
                    <span className="tree-node-hover-items">
                        <Icon className="tree-node-hover-item" style={{ fontSize: '15px' }} title="查看详情" type="search" />
                        <Icon className="tree-node-hover-item" title="查看详情" type="exclamation-circle-o" />
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
    
                const itemAnchorName = getContextMenuAnchorName(item.type);
                const className = getItemClassName(item.type);

                const nodeTitle = (
                    <Tooltip placement="bottomLeft" mouseEnterDelay={0.5}>
                        <span
                            className={itemAnchorName}
                            title={name}
                            style={{padding:"8px 0px"}}
                        >
                            {name}
                            { this.renderNodeHoverButton(item.type) }
                        </span>
                    </Tooltip>
                )

                return (
                    <TreeNode
                        title={nodeTitle}
                        key={`${id}`}
                        value={id}
                        isLeaf={isLeaf}
                        data={item}
                        className={className}
                    >
                        {item.children && loop(item.children)}
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
