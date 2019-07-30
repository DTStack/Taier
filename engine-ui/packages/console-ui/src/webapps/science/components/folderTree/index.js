import React from 'react';
import { Tree, Tooltip, TreeSelect } from 'antd';

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
        const { treeData, nodeClass, hideFiles, disabledNode = [] } = this.props;
        const loop = (data) => {
            return data && data.map(item => {
                const id = `${item.id}`
                if (disabledNode.includes(id)) {
                    return null;
                }
                const name = item.name
                const isLeaf = item.type == 'file';
                if (isLeaf && hideFiles) {
                    return null;
                }
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
                        name={name}
                        key={item.key}
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
        return contextMenus && contextMenus.map((contextMenu, index) => {
            return this.renderContextMenu(contextMenu);
        })
    }
    renderContextMenu (contextMenu) {
        const { activeNode } = this.state;
        if (!contextMenu.menuItems || !contextMenu.menuItems.length) {
            return null;
        }
        return <ContextMenu key={contextMenu.targetClassName} targetClassName={contextMenu.targetClassName}>
            {contextMenu.menuItems.map((menu) => {
                return (<MenuItem key={menu.text} onClick={() => {
                    menu.onClick(activeNode);
                }}>{menu.text}</MenuItem>)
            })}
        </ContextMenu>
    }
    render () {
        const { isSelect, value, onChange, disabled, dropDownTab, isShowFixResource } = this.props;
        // ant-select-dropdown ant-select-tree-dropdown ant-select-dropdown--single ant-select-dropdown-placement-bottomLeft
        // ant-select-dropdown dt-tree-select ant-select-dropdown--single ant-select-dropdown-placement-bottomLeft
        const extClassName = dropDownTab ? 's-resource-catalogue__cus' : 's-catalogue__comm';
        const fixResClassName = isShowFixResource && 's-resource-floder-fixed';
        return isSelect ? (
            <TreeSelect
                dropdownClassName='ant-select-tree-dropdown dt-tree-select'
                showSearch
                disabled={disabled}
                placeholder='选择一个父节点'
                dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                loadData={this.props.loadData}
                value={typeof value == 'undefined' ? undefined : `${value}`}
                onChange={onChange}
                treeNodeFilterProp='name'
            >
                {this.renderNodes()}
            </TreeSelect>
        ) : (
            <div className={`s-catalogue ${extClassName}`}>
                <Tree
                    showIcon={true}
                    autoExpandParent={false}
                    expandedKeys={this.props.expandedKeys}
                    selectedKeys={this.props.selectedKeys}
                    loadData={this.props.loadData}
                    onSelect={this.props.onSelect}
                    onExpand={this.props.onExpand}
                    onRightClick={this.onRightClick.bind(this)}
                    className={fixResClassName}
                >
                    {this.renderNodes()}
                </Tree>
                { dropDownTab && dropDownTab()}
                {this.renderContextMenus()}
            </div>
        )
    }
}

export default FolderTree;
