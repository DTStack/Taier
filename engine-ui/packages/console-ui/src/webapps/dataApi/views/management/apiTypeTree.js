import React, { Component } from 'react';
import { Tree, Tooltip, Icon, Popconfirm, message, Spin } from 'antd';
import { cloneDeep } from 'lodash';
const TreeNode = Tree.TreeNode;
class ApiTypeTree extends Component {
    state = {
        maxDeepLength: 2,
        expandedKeys: [],
        editNode: null,
        mode: '',
        addPid: '',
        autoExpandParent: true
    }
    constructor (props) {
        super(props);
        this.editInput = null;
    }
    componentDidMount () {
        if (this.props.maxDeepLength) {
            this.setState({
                maxDeepLength: this.props.maxDeepLength
            })
        }
    }
    // eslint-disable-next-line
    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps (nextProps) {
        if (nextProps.maxDeepLength && this.props.maxDeepLength != nextProps.maxDeepLength) {
            this.setState({
                maxDeepLength: nextProps.maxDeepLength
            })
        }
    }
    getTreeView () {
        // 树遍历渲染
        function renderTree (data, deepLength) {
            let arr = [];
            for (let i = 0; i < data.length; i++) {
                let item = data[i];
                let isLeaf = !(item.childCatalogue.length > 0)

                if (item.api) { // 该节点为api
                    arr.push(
                        (
                            <TreeNode title={item.catalogueName} key={item.id}>

                            </TreeNode>
                        )
                    )
                    continue;
                }
                expandedKeys.push(item.id.toString());
                arr.push(
                    (
                        <TreeNode title={this.getTreeNodeTitle(item.id, item.catalogueName, false, isLeaf, deepLength, item.isTmp)} key={item.id}>
                            {renderTree.call(this, item.childCatalogue, deepLength + 1)}
                        </TreeNode>
                    )
                )
            }

            return arr;
        }
        const data = this.props.tree;
        let expandedKeys = [];
        if (!data) {
            return null;
        }
        const view = renderTree.call(this, data, 1)

        return { view, expandedKeys }
    }
    onSelect = (selectedKeys, info) => {
        console.log('selected', selectedKeys, info);
    }
    onCheck = (checkedKeys, info) => {
        console.log('onCheck', checkedKeys, info);
    }
    deleteNode (id) {
        this.props.deleteCatalogue(id);
    }
    // 校验字段合法
    checkVal = (value) => {
        const reg = /^([\w|\u4e00-\u9fa5]{1,20})$/ // [A-Za-z0-9]|汉字 长度1-20
        const isValid = reg.test(value)
        if (!isValid) {
            message.error('类目名称不能超过20个字符，只能以字母、数字、下划线和汉字组成！')
        }
        return isValid;
    }
    getTreeNodeTitle (id, text, isRoot, isLeaf, deepLength, isTmp) {
        const maxDeepLength = this.state.maxDeepLength;
        let item;
        const disAble = isTmp && this.state.editNode != id;
        // 根节点
        if (disAble) {
            item = (
                <span className="tree-hover-show-item tree-item" style={{ marginLeft: '3px' }}>
                    <Tooltip title="加载中" >
                        <Spin size="small" />
                    </Tooltip>
                </span>
            )
        } else if (isRoot) {
            item = (
                <span className="tree-hover-show-item tree-item" style={{ marginLeft: '3px' }}>
                    <Tooltip title="添加新分类" >
                        <Icon type="plus-square-o" onClick={this.addNode.bind(this, id)} />
                    </Tooltip>
                </span>
            );
        } else if (!isLeaf) {
            // 非叶子节点
            if (deepLength < maxDeepLength) {
                item = (
                    <span className="tree-hover-show-item tree-item" style={{ marginLeft: '3px' }}>
                        <Tooltip title="添加新分类" >
                            <Icon type="plus-square-o" onClick={this.addNode.bind(this, id)} />
                        </Tooltip>
                        <Tooltip title="编辑">
                            <Icon type="edit" onClick={this.editNode.bind(this, id)} />
                        </Tooltip>
                    </span>
                )
            } else {
                item = (
                    <span className="tree-hover-show-item tree-item" style={{ marginLeft: '3px' }}>
                        <Tooltip title="编辑">
                            <Icon type="edit" onClick={this.editNode.bind(this, id)} />
                        </Tooltip>
                    </span>
                )
            }
        } else if (deepLength >= maxDeepLength) {
            // 叶子节点，且达到deepLength
            item = (
                <span className="tree-hover-show-item tree-item" style={{ marginLeft: '3px' }}>
                    <Tooltip title="编辑">
                        <Icon type="edit" onClick={this.editNode.bind(this, id)} />
                    </Tooltip>
                    <Tooltip title="删除">
                        <Popconfirm title="您确定删除该分类吗"
                            onConfirm={this.deleteNode.bind(this, id)}
                            okText="确定"
                            cancelText="取消">
                            <Icon type="minus-square-o" />
                        </Popconfirm>
                    </Tooltip>
                </span>
            )
        } else {
            item = (
                <span className="tree-hover-show-item tree-item" style={{ marginLeft: '3px' }}>
                    <Tooltip title="添加新分类" >
                        <Icon type="plus-square-o" onClick={this.addNode.bind(this, id)} />
                    </Tooltip>
                    <Tooltip title="编辑">

                        <Icon type="edit" onClick={this.editNode.bind(this, id)} />

                    </Tooltip>
                    <Tooltip title="删除">
                        <Popconfirm title="您确定删除该分类吗"
                            onConfirm={this.deleteNode.bind(this, id)}
                            okText="确定"
                            cancelText="取消">
                            <Icon type="minus-square-o" />
                        </Popconfirm>
                    </Tooltip>
                </span>
            )
        }

        return (
            <span
                className="tree-hover-show"
            >

                {this.state.editNode == id ? (
                    <input ref={this.setRange.bind(this)} autoFocus={true} defaultValue={text} onBlur={this.editOver.bind(this, id, text)} />
                ) : (text)}
                {item}

            </span>
        )
    }
    setRange (q) {
        if (!q) {
            return;
        }
        const pos = q.value.length;
        q.setSelectionRange(pos, pos);
        this.editInput = q;
    }
    editOver (id, oldText, e) {
        const nodeName = this.editInput.value;

        if (!this.checkVal(nodeName)) {
            this.props.getCatalogue(0);
            this.setState({
                editNode: null
            })
            return;
        }
        this.setState({
            editNode: null
        }, () => {
            if (this.state.mode == 'add') {
                this.props.addCatalogue(this.state.addPid, nodeName)
                return;
            }
            if (oldText != nodeName) {
                this.props.updateCatalogue(id, nodeName)
            }
        })
    }
    editNode (id) {
        this.setState({
            editNode: id,
            mode: 'edit'
        });
    }
    addNode (id) {
        const tree = cloneDeep(this.props.tree);
        const tmpId = Math.random();
        const { expandedKeys } = this.state;
        function addTreeNode (data, id) {
            if (id == 0) {
                data.push({
                    id: tmpId,
                    catalogueName: '新建分类名',
                    childCatalogue: [],
                    isTmp: true
                });
                return;
            }
            for (let i = 0; i < data.length; i++) {
                let item = data[i];

                if (item.id == id) {
                    item.childCatalogue.push({
                        id: tmpId,
                        catalogueName: '新建分类名' + item.childCatalogue.length,
                        childCatalogue: [],
                        isTmp: true
                    })
                    return;
                }
                addTreeNode(item.childCatalogue, id)
            }
        }
        addTreeNode(tree, id)
        if (expandedKeys) {
            if (expandedKeys.indexOf(id) == -1) {
                this.onExpands(expandedKeys.concat(id + ''));
            }
        }
        this.setState({
            editNode: tmpId,
            mode: 'add',
            addPid: id
        })
        this.props.addCatalogueEdit(tree)
    }
    onExpands = (onExpands, info) => {
        this.setState({ expandedKeys: onExpands, autoExpandParent: false });
    }
    render () {
        const { view, expandedKeys: TreeExpandedKeys } = this.getTreeView();
        const expandedKeys = this.state.expandedKeys.length > 0 ? this.state.expandedKeys : TreeExpandedKeys;

        return (

            <Tree

                showIcon
                expandedKeys={expandedKeys}
                onSelect={this.onSelect}
                onCheck={this.onCheck}
                onExpand={this.onExpands}
                autoExpandParent={this.state.autoExpandParent}
            >
                <TreeNode title={this.getTreeNodeTitle(0, 'API管理', true, false, 0)} key={0}>
                    {view}
                </TreeNode>

            </Tree>

        )
    }
}
export default ApiTypeTree;
