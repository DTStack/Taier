import React, { Component } from 'react'
import {
    Tree, TreeSelect, Icon, message,
    Tooltip, Popconfirm
} from 'antd'

const TreeNode = Tree.TreeNode

class CatalogueTree extends Component {
    state = {
        expendKeys: [],
        autoExpandParent: false,
        disabledAdd: false
    }

    _expendKeys = []
    _expanded = true

    componentDidMount () {}

    componentDidUpdate () {
        if (this._active) {
            this._inputEle = document.getElementById(`node_${this._active}`);
            if (this._inputEle) this.initEdit(this._inputEle)
        }

        if (this._expendKeys.length > 0 && this._expanded) {
            this.setState({
                expendKeys: this._expendKeys,
                autoExpandParent: false
            })
            this._expanded = false;
        }
    }

    onExpand = (expendKeys, state) => {
        this.setState({
            expendKeys,
            autoExpandParent: false
        })
    }

    onEdit = (e) => {
        const inputEle = this._overRoot.querySelector('.normal-node')
        this._inputEle = inputEle;
        this.initEdit(inputEle)
    }

    initEdit = (inputEle) => {
        inputEle.className = 'edit-node';
        inputEle.setAttribute('contenteditable', 'plaintext-only')
        inputEle.focus();

        const textNode = inputEle.firstChild;
        const range = document.createRange();
        const sel = window.getSelection();
        const start = inputEle.innerHTML.length > textNode.length
            ? textNode.length : inputEle.innerHTML.length

        range.setStart(textNode, start)
        range.collapse(true);

        sel.removeAllRanges();
        sel.addRange(range);
    }

    offEdit = (node) => {
        this._inputEle.className = 'normal-node';
        this._inputEle.setAttribute('contenteditable', false)

        const value = this._inputEle.innerHTML;
        const originVal = node.bindData.name;
        const callback = (res) => {
            this.setState({ disabledAdd: false })

            if (res.code !== 1 && this._inputEle) { this._inputEle.innerHTML = originVal }
        }
        if (this.checkVal(value)) {
            if (node.isNew) {
                this.props.onTreeNodeEdit({
                    nodeName: value,
                    nodePid: node.parentId,
                    tmpId: node.nodeId
                }, 'add', callback)
            } else {
                if (originVal !== value) {
                    const editNode = {
                        id: node.nodeId,
                        nodeName: value
                    }
                    this.props.onTreeNodeEdit(editNode, 'edit', callback)
                }
            }
        } else {
            this._inputEle.innerHTML = originVal
        }
    }

    checkVal = (value) => {
        const reg = /^([\w|\u4e00-\u9fa5]{1,20})$/ // [A-Za-z0-9]|汉字 长度1-20
        const isValid = reg.test(value)
        if (!isValid) {
            message.error('类目名称只能不超过20位字母、数字、下划线和汉字组成！')
        }
        return isValid;
    }

    onKeyDown = (e) => {
        const disabled = [13] // 禁用enter换行
        if (disabled.indexOf(e.keyCode) > -1) {
            e.preventDefault();
            return false;
        }
    }

    mouseOver = (e) => {
        this._overRoot = this.getOverRoot(e.target)
        this._opeDom = this._overRoot.querySelector('.node-operation')
        if (this._opeDom) this._opeDom.style.opacity = '1';
    }

    mouseOut = (e) => {
        if (this._opeDom) {
            this._opeDom.style.opacity = '0';
        }
    }

    getOverRoot = (ele) => {
        let node = ele;
        while (node) {
            if (node.className.indexOf('m-over-tag') > -1) {
                return node;
            }
            node = node.parentNode;
        }
    }

    onLoadEditable = (e, data) => {
        if (data.isNew) {
            this.mouseOver(e)
            this.onEdit()
        }
    }

    onInitAdd = (data) => {
        const expendKeys = [...this.state.expendKeys];
        expendKeys.push(`${data.nodeId}`)
        this.setState({
            expendKeys,
            disabledAdd: true
        }, () => {
            this.props.onTreeNodeEdit(data, 'initAdd')
        })
    }

    renderTreeNodes = () => {
        const { treeData, onTreeNodeEdit, isPicker, isFolderPicker } = this.props
        const { disabledAdd } = this.state
        const loopTree = (tree) => {
            return tree && tree.map(data => {
                const item = data.bindData
                const key = data.nodeId;
                const isFolder = item.type === 'folder'
                const isTable = item.type === 'table'
                const isLeaf = !data.children || data.children.length === 0
                const isRoot = item.parentId === 0;
                if (isFolderPicker && isTable) {
                    return null;
                }
                if (this._expendKeys.indexOf(key + '') == -1) {
                    this._expendKeys.push(`${key}`)
                }
                if (data.isNew) this._active = key
                const title = !isPicker ? <span
                    title={item.name}
                    onMouseOver={this.mouseOver}
                    onMouseOut={this.mouseOut}
                    className={isLeaf ? 'file-item m-over-tag' : 'folder-item m-over-tag'}>
                    &nbsp;
                    <span
                        className="normal-node"
                        contentEditable={false}
                        id={`node_${key}`}
                        onKeyDown={this.onKeyDown}
                        onBlur={this.offEdit.bind(this, data)}>
                        {item.name}
                    </span>
                    &nbsp;
                    <span className="node-operation">
                        {
                            !isTable &&
                        <Tooltip title="添加新目录">
                            {
                                disabledAdd
                                    ? <Icon type="plus-square-o" style={{ color: '#bfbfbf' }}/>
                                    : <Icon type="plus-square-o" onClick={this.onInitAdd.bind(this, data)} />
                            }
                        </Tooltip>
                        }
                        {
                            !isRoot && <span
                            >
                                {
                                    !isTable &&
                                    <Tooltip title="编辑目录">
                                        &nbsp;
                                        {
                                            disabledAdd
                                                ? <Icon type="edit" style={{ color: '#bfbfbf' }}/>
                                                : <Icon type="edit"
                                                    onLoad={(e) => { this.onLoadEditable(e, data) }}
                                                    onClick={this.onEdit} />
                                        }
                                    </Tooltip>
                                }
                                {
                                    isLeaf && !isTable &&
                                    <Tooltip title="删除目录">
                                        &nbsp;
                                        {
                                            disabledAdd
                                                ? <Icon type="minus-square-o" style={{ color: '#bfbfbf' }}/>
                                                : <Popconfirm title="您确认删除当前目录及下面的表吗？"
                                                    onConfirm={onTreeNodeEdit.bind(this, data, 'delete')}
                                                    okText="确定" cancelText="取消">
                                                    <Icon type="minus-square-o" />
                                                </Popconfirm>
                                        }
                                    </Tooltip>
                                }
                            </span>
                        }
                    </span>
                </span>
                    : <span
                        className={isLeaf ? 'file-item' : 'folder-item'}
                        title={item.name}
                    >
                        {item.name}
                    </span>

                return (<TreeNode
                    title={title}
                    key={key}
                    value={key}
                    name={item.name}
                    isLeaf={isLeaf}
                    data={data}
                    className={isTable && 's-table'}
                >
                    {isFolder && loopTree(data.children)}
                </TreeNode >)
            })
        }

        return loopTree(treeData)
    }

    render () {
        let treeContent = ''
        const {
            onSelect, onChange, id, value, showSearch,
            isPicker, placeholder, defaultValue, treeCheckable
        } = this.props;
        if (isPicker) {
            treeContent = (
                <div ref={(ins) => this.selEle = ins } className='org-tree-select-wrap'>
                    <TreeSelect
                        allowClear
                        defaultExpandAll
                        key={id || 'tableCatalogue'}
                        value={value}
                        showSearch={showSearch}
                        treeCheckable={treeCheckable}
                        defaultValue={defaultValue}
                        onChange={onChange}
                        onSelect={onSelect}
                        getPopupContainer={() => this.selEle }
                        style={{ width: '100%' }}
                        treeNodeFilterProp="name"
                        dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                        placeholder={placeholder || '请选择数据类目'}
                    >
                        {
                            this.renderTreeNodes() && this.renderTreeNodes().filter(v => {
                                if (v) return v
                            })
                        }
                        {/* {this.renderTreeNodes()} */}
                    </TreeSelect>
                </div>
            )
        } else {
            treeContent = (
                <Tree
                    showIcon
                    onSelect={onSelect}
                    onChange={onChange}
                    onExpand={this.onExpand}
                    // expandedKeys={ this.state.expendKeys }
                    autoExpandParent={ this.state.autoExpandParent }
                >
                    { this.renderTreeNodes() }
                </Tree>
            )
        }

        return <div
            className="m-catalogue"
            style={{ position: 'relative', display: 'block' }}>
            { treeContent }
        </div>
    }
}

export default CatalogueTree
