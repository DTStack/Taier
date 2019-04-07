import React from 'react';
import { Tree, Tooltip } from 'antd';

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

    renderNodes = () => {
        const { treeData, nodeClass } = this.props;
        const loop = (data) => {
            return data && data.map(item => {
                const id = `${item.id || item.tableId}`
                const name = item.name || item.tableName
                const isLeaf = item.type == 'file';
                // 用作展示上下文的锚点， 暂时取消
                let className;
                if (nodeClass && typeof nodeClass == 'function') {
                    className = nodeClass(item);
                }

                const nodeTitle = (
                    <Tooltip placement="bottomLeft" mouseEnterDelay={0.5}>
                        <span
                            title={name}
                            style={{ padding: '8px 0px' }}
                        >
                            {name}
                            {typeof this.props.renderNodeHoverButton === 'function' && this.props.renderNodeHoverButton(item)}
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
    renderContextMenus () {
        const { contextMenus = [] } = this.props;
        return contextMenus && contextMenus.map((contextMenu) => {
            return this.renderContextMenu(contextMenu);
        })
    }
    renderContextMenu (contextMenu) {
        const { activeNode } = this.state;
        if (!contextMenu.menuItems || !contextMenu.menuItems.length) {
            return null;
        }
        return <ContextMenu targetClassName={contextMenu.targetClassName}>
            {contextMenu.menuItems.map((menu) => {
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
                {this.renderContextMenus()}
            </div>
        )
    }
}

export default FolderTree;
