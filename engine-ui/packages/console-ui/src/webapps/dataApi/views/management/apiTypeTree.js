import React, { Component } from "react";
import { Card, Tree, Tooltip, Icon, Popconfirm,message } from "antd";
import { cloneDeep } from 'lodash';
const TreeNode = Tree.TreeNode;
class ApiTypeTree extends Component {
    state = {
        maxDeepLength: 2,
        expandedKeys: [],
        editNode: null,
        mode:"",
        addPid:"",
        autoExpandParent:true
    }
    constructor(props) {
        super(props);
        this.editInput = null;
    }
    componentDidMount() {
        if (this.props.maxDeepLength) {
            this.state.maxDeepLength = this.props.maxDeepLength;
        }
    }
    componentWillReceiveProps(nextProps) {
        if (nextProps.maxDeepLength && this.props.maxDeepLength != nextProps.maxDeepLength) {
            this.setState({
                maxDeepLength: nextProps.maxDeepLength
            })
        }
        
    }
    getTreeView() {
        //树遍历渲染
        function renderTree(data, deepLength) {

            let arr = [];
            for (let i = 0; i < data.length; i++) {
                let item = data[i];
                let isLeaf = item.childCatalogue.length > 0 ? false : true

                if (item.api) {//该节点为api
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
                        <TreeNode title={this.getTreeNodeTitle.call(this, item.id, item.catalogueName, false, isLeaf, deepLength)} key={item.id}>
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
        
        return { view,expandedKeys }


    }
    onSelect = (selectedKeys, info) => {
        console.log('selected', selectedKeys, info);
    }
    onCheck = (checkedKeys, info) => {
        console.log('onCheck', checkedKeys, info);
    }
    deleteNode(id) {
        this.props.deleteCatalogue(id);
    }
    //校验字段合法
    checkVal = (value) => {
        const reg = /^([\w|\u4e00-\u9fa5]{1,20})$/ // [A-Za-z0-9]|汉字 长度1-20
        const isValid = reg.test(value)
        if (!isValid) {
            message.error('类目名称只能不超过20位字母、数字、下划线和汉字组成！')
        }
        return isValid;
    }
    getTreeNodeTitle(id, text, isRoot, isLeaf, deepLength) {
        const maxDeepLength = this.state.maxDeepLength;
        let item;
        //根节点
        if (isRoot) {
            item = (
                <span className="tree-hover-show-item tree-item" style={{ marginLeft: "3px" }}>
                    <Tooltip title="添加新分类" >
                        <Icon type="plus-square-o" onClick={this.addNode.bind(this,id)} />
                    </Tooltip>
                </span>
            );
        }
        //非叶子节点（必然不处于deepLength的限制）
        else if (!isLeaf) {
            item = (
                <span className="tree-hover-show-item tree-item" style={{ marginLeft: "3px" }}>
                    <Tooltip title="添加新分类" >
                        <Icon type="plus-square-o" onClick={this.addNode.bind(this,id)} />
                    </Tooltip>
                    <Tooltip title="编辑">
                        <Icon type="edit" onClick={this.editNode.bind(this, id)} />
                    </Tooltip>
                </span>
            )
        }
        //叶子节点，且达到deepLength
        else if (deepLength >= maxDeepLength) {
            item = (
                <span className="tree-hover-show-item tree-item" style={{ marginLeft: "3px" }}>
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
        else {
            item = (
                <span className="tree-hover-show-item tree-item" style={{ marginLeft: "3px" }}>
                    <Tooltip title="添加新分类" >
                        <Icon type="plus-square-o" onClick={this.addNode.bind(this,id)} />
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
                    <input ref={this.setRange.bind(this)} autoFocus={true} defaultValue={text} onBlur={this.editOver.bind(this,id)} />
                ) : (
                        text
                    )}
                {item}

            </span>
        )
    }
    setRange(q) {
        if (!q) {
            return;
        }
        const pos = q.value.length;
        q.setSelectionRange(pos, pos);
        this.editInput = q;
    }
    editOver(id,e) {

        const nodeName=this.editInput.value;
        if(!this.checkVal(nodeName)){
            this.props.getCatalogue(0);
            this.setState({
                editNode:null
            })
            return;
        }
        this.setState({
            editNode: null
        },
            () => {
                if(this.state.mode=="add"){
                    this.props.addCatalogue(this.state.addPid,nodeName)
                    return;
                }
                this.props.updateCatalogue(id,nodeName)
            })
    }
    editNode(id) {
        this.setState({
            editNode: id,
            mode:"edit"
        });
    }
    addNode(id){
        const tree=cloneDeep(this.props.tree);
        const tmpId=Math.random();
        function addTreeNode(data,id){
            if(id==0){
                data.push({
                    id:tmpId,
                    catalogueName:"新建分类名",
                    childCatalogue:[]
                });
                return;
            }
            for(let i=0;i<data.length;i++){
                let item=data[i];
                
                if(item.id==id){
                    
                    item.childCatalogue.push({
                        id:tmpId,
                        catalogueName:"新建分类名"+item.childCatalogue.length,
                        childCatalogue:[]
                    })
                    return;
                }
                addTreeNode(item.childCatalogue,id)
            }
            return;
        }
        addTreeNode(tree,id)
        this.setState({
            editNode:tmpId,
            mode:"add",
            addPid:id
        })
        this.props.addCatalogueEdit(tree)
        

        
    }
    onExpands = (onExpands, info) => {
       
        this.setState({ expandedKeys: onExpands,autoExpandParent:false });
      }
    render() {
        const { view,expandedKeys:TreeExpandedKeys } = this.getTreeView();
        const expandedKeys=this.state.expandedKeys.length>0?this.state.expandedKeys:TreeExpandedKeys;

        return (

            <Tree

                showIcon
                expandedKeys={expandedKeys}
                onSelect={this.onSelect}
                onCheck={this.onCheck}
                onExpand={this.onExpands}
                autoExpandParent={this.state.autoExpandParent}
            >
                <TreeNode title={this.getTreeNodeTitle.call(this, 0, "API管理", true, false, 0)} key={0}>
                    {view}
                </TreeNode>

            </Tree>


        )
    }
}
export default ApiTypeTree;