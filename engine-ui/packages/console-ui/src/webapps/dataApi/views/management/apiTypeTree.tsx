import * as React from 'react';
import { Tree, Tooltip, Icon, Popconfirm, message, Spin } from 'antd';
import { cloneDeep, get } from 'lodash';
import { API_TYPE } from '../../consts';
import ApiSlidePane from './apiDetail/apiSlide';
import { AntTreeNodeEvent } from 'antd/lib/tree';

const TreeNode = Tree.TreeNode;
class ApiTypeTree extends React.Component<any, any> {
    state: any = {
        maxDeepLength: 2,
        expandedKeys: [],
        selectedNode: null,
        editNode: null,
        mode: '',
        addPid: '',
        autoExpandParent: true,
        slidePaneShow: false
    }
    public editInput: any;
    constructor (props: any) {
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
    UNSAFE_componentWillReceiveProps (nextProps: any) {
        if (nextProps.maxDeepLength && this.props.maxDeepLength != nextProps.maxDeepLength) {
            this.setState({
                maxDeepLength: nextProps.maxDeepLength
            })
        }
    }
    getTreeView () {
        // 树遍历渲染
        function renderTree (data: any, deepLength: any) {
            let arr: any = [];
            for (let i = 0; i < data.length; i++) {
                let item = data[i];
                let isLeaf = !(item.childCatalogue.length > 0)

                if (item.api) { // 该节点为api
                    let iconClassName = item.apiType == API_TYPE.NORMAL ? 'u-tree__node--normal' : 'u-tree__node--register'
                    arr.push(
                        (
                            <TreeNode data={item} className={iconClassName} title={item.catalogueName} key={item.id + '_api'}>

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
        let expandedKeys: any = [];
        if (!data) {
            return null;
        }
        const view = renderTree.call(this, data, 1)

        return { view, expandedKeys }
    }
    onSelect = (selectedKeys: any, e: AntTreeNodeEvent) => {
        const data = get(e, 'node.props.data');
        if (data) {
            this.setState({
                selectedNode: data,
                slidePaneShow: true
            })
        }
    }
    onCheck = (checkedKeys: any, info: any) => {
        console.log('onCheck', checkedKeys, info);
    }
    deleteNode (id: any) {
        this.props.deleteCatalogue(id);
    }
    // 校验字段合法
    checkVal = (value: any) => {
        const reg = /^([\w|\u4e00-\u9fa5]{1,20})$/ // [A-Za-z0-9]|汉字 长度1-20
        const isValid = reg.test(value)
        if (!isValid) {
            message.error('类目名称不能超过20个字符，只能以字母、数字、下划线和汉字组成！')
        }
        return isValid;
    }
    getTreeNodeTitle (id: any, text: any, isRoot: any, isLeaf: any, deepLength: any, isTmp?: any) {
        const maxDeepLength = this.state.maxDeepLength;
        let item: any;
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
    setRange (q: any) {
        if (!q) {
            return;
        }
        const pos = q.value.length;
        q.setSelectionRange(pos, pos);
        (this as any).editInput = q;
    }
    editOver (id: any, oldText: any, e: any) {
        const nodeName = (this as any).editInput.value;

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
    editNode (id: any) {
        this.setState({
            editNode: id,
            mode: 'edit'
        });
    }
    addNode (id: any) {
        const tree = cloneDeep(this.props.tree);
        const tmpId = Math.random();
        const { expandedKeys } = this.state;
        function addTreeNode (data: any, id: any) {
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
    onExpands = (onExpands: any, info?: any) => {
        this.setState({ expandedKeys: onExpands, autoExpandParent: false });
    }
    closeSlidePane () {
        this.setState({
            slidePaneShow: false,
            selectedNode: null
        })
    }
    render () {
        const { view, expandedKeys: TreeExpandedKeys } = this.getTreeView();
        const { selectedNode, slidePaneShow } = this.state;
        const expandedKeys = this.state.expandedKeys.length > 0 ? this.state.expandedKeys : TreeExpandedKeys;

        return (
            <React.Fragment>
                <Tree
                    showIcon
                    expandedKeys={expandedKeys}
                    selectedKeys={selectedNode ? [selectedNode.id + '_api'] : []}
                    onSelect={this.onSelect}
                    onCheck={this.onCheck}
                    onExpand={this.onExpands}
                    autoExpandParent={this.state.autoExpandParent}
                >
                    <TreeNode title={this.getTreeNodeTitle(0, 'API管理', true, false, 0)} key={0}>
                        {view}
                    </TreeNode>

                </Tree>
                <div style={{ position: 'fixed', top: 50, bottom: 0, left: 0, right: 20, pointerEvents: 'none' }}>
                    <div style={{ pointerEvents: 'auto' }}>
                        <ApiSlidePane
                            simple={true}
                            showRecord={selectedNode || {}}
                            slidePaneShow={slidePaneShow}
                            closeSlidePane={this.closeSlidePane.bind(this)}
                        />
                    </div>
                </div>
            </React.Fragment>
        )
    }
}
export default ApiTypeTree;
