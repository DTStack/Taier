import React, { Component } from 'react'

import {
    Table, Input, Button, Card,
    Select, Form, message,
    Radio, Modal, Tree
 } from 'antd'

 import utils from 'utils'
 import { formItemLayout } from '../../../consts'

 import Api from '../../../api'

 const FormItem = Form.Item
 const Option = Select.Option
 const TreeNode = Tree.TreeNode

 class RoleForm extends Component {

    state = {
        expandedKeys: [],
        autoExpandParent: true,
        checkedKeys: [],
        roleTree: [],
    }

    componentDidMount() {
        const app = utils.getParameterByName('app')
        Api.getRoleTree(app).then(res => {
            if (res.code === 1) {
                this.setState({
                    roleTree: (res.data && res.data.children) || []
                })
            }
        })
    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.roleInfo !== this.props.roleInfo) {
            let ids = nextProps.roleInfo && nextProps.roleInfo.permissionIds || []
            ids = ids && ids.map(id => `${id}`)
            this.onCheck(ids)
        }
    }

    onCheck = (checkedKeys) => {
        console.log('onCheck:', checkedKeys)
        this.setState({ checkedKeys }, () => {
            // 只需要叶子节点即可
            // const arr = this.getLeafNodes(checkedKeys)
            this.props.form.setFieldsValue({ permissionIds: checkedKeys })
        });
    }

    getLeafNodes = (checkedKeys) => {
        let arr = [...checkedKeys]
        const { roleTree } = this.state
        
        const loop = (data) => {
            
            for (let i = 0; i < data.length; i++) {
                const item = data[i]
                
                // 有子节点的需要移除
                if (item.children && item.children.length > 0) {

                    const index = arr.indexOf(`${item.nodeId}`)
                    if (index > -1) {
                        arr.splice(index, 1) // remove nouse 
                    }

                    loop(item.children)
                }
            }
        }

        loop(roleTree)

        return arr;
    }

    renderTreeNodes = (data) => {
        return data && data.map(item => {
            const role = item.bindData
            const key = `${item.nodeId}`
            
            const checkedItem = this.state.checkedKeys.find(checked => checked === key)
            const checked = checkedItem ? true : false;
            
            if (checkedItem) {
                console.log('checkedItem', key, role.name)
            }
            
            if (item.children && item.children.length > 0) {
                return (
                    <TreeNode
                        key={key}
                        checked={checked}
                        dataRef={role}
                        title={role.display} 
                    >
                        {this.renderTreeNodes(item.children)}
                    </TreeNode>
                )
            }
            return <TreeNode key={key} checked={checked} dataRef={role} title={role.display}/>;
        })
    }

    render() {
        const { roleTree, checkedKeys } = this.state
        const { roleInfo, form } = this.props;
        const { getFieldDecorator } = form;

        console.log('checked:', checkedKeys)

        return (
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="角色名称"
                    hasFeedback
                >
                    {getFieldDecorator('roleName', {
                        rules: [{
                            required: true, message: '角色名称不可为空！',
                        }],
                        initialValue: roleInfo && roleInfo.roleName || '',
                    })(
                        <Input />,
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="角色描述"
                >
                    {getFieldDecorator('roleDesc', {
                        rules: [],
                        initialValue: roleInfo && roleInfo.roleDesc || '',
                    })(
                        <Input type="textarea" />,
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="权限点"
                    className="dstree"
                >
                    {getFieldDecorator('permissionIds', {
                        rules: [{
                            required: true,
                            message: '请选择相应的角色权限！',
                        }],
                        initialValue: [],
                    })(
                        <Tree
                            style={{ marginTop: '-10px' }}
                            checkable
                            defaultExpandAll
                            onCheck={this.onCheck}
                            // checkedKeys={checkedKeys}
                        >
                            {this.renderTreeNodes(roleTree)}
                        </Tree>,
                    )}
                </FormItem>
            </Form>
        )
    }
 }

 const RoleFormWrapper = Form.create()(RoleForm)
 export default RoleFormWrapper;
