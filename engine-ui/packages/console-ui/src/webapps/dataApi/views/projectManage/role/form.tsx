import * as React from 'react'

import {
    Input,
    Form,
    Tree
} from 'antd'

import Api from '../../../api/project'
import { formItemLayout } from '../../../consts'

const FormItem = Form.Item
const TreeNode = Tree.TreeNode

class RoleForm extends React.Component<any, any> {
    state: any = {
        expandedKeys: [],
        autoExpandParent: true,
        checkedKeys: [],
        roleTree: []
    }

    componentDidMount () {
        Api.getRoleTree().then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    roleTree: (res.data && res.data.children) || []
                })
            }
        })
    }

    // eslint-disable-next-line
	UNSAFE_componentWillMount () {
        this.props.form.resetFields()
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        if (nextProps.roleInfo !== this.props.roleInfo) {
            let ids = (nextProps.roleInfo && nextProps.roleInfo.permissionIds) || []
            ids = ids && ids.map((id: any) => `${id}`)
            this.onCheck(ids)
        }
    }

    onCheck = (checkedKeys: any) => {
        this.setState({ checkedKeys }, () => {
            // 只需要叶子节点即可
            const arr = this.getLeafNodes(checkedKeys)
            this.props.form.setFieldsValue({ permissionIds: arr })
        });
    }

    getLeafNodes = (checkedKeys: any) => {
        let arr: any = [...checkedKeys]
        const { roleTree } = this.state

        const loop = (data: any) => {
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

    renderTreeNodes = (data: any) => {
        const { isDisabled = false } = this.props;
        return data && data.map((item: any) => {
            const role = item.bindData
            const key = `${item.nodeId}`
            if (item.children && item.children.length > 0) {
                return (
                    <TreeNode
                        key={key}
                        dataRef={role}
                        title={role.display}
                        disabled={isDisabled}
                        disableCheckbox={isDisabled}
                    >
                        {this.renderTreeNodes(item.children)}
                    </TreeNode>
                )
            }
            return <TreeNode disableCheckbox={isDisabled} disabled={isDisabled} key={key} title={role.display}/>;
        })
    }

    render () {
        const { roleTree, checkedKeys } = this.state
        const { roleInfo, form } = this.props;
        const { getFieldDecorator } = form;
        return (
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="角色名称"
                    hasFeedback
                >
                    {getFieldDecorator('roleName', {
                        rules: [{
                            required: true, message: '角色名称不可为空！'
                        }],
                        initialValue: (roleInfo && roleInfo.roleName) || ''
                    })(
                        <Input />
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="角色描述"
                >
                    {getFieldDecorator('roleDesc', {
                        rules: [],
                        initialValue: (roleInfo && roleInfo.roleDesc) || ''
                    })(
                        <Input type="textarea" />
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
                            message: '请选择相应的角色权限！'
                        }],
                        initialValue: (roleInfo && roleInfo.permissionIds) || ''
                    })(
                        <Tree
                            style={{ marginTop: '-10px' }}
                            checkable
                            defaultExpandAll
                            onCheck={this.onCheck}
                            checkedKeys={checkedKeys}
                        >
                            {this.renderTreeNodes(roleTree)}
                        </Tree>
                    )}
                </FormItem>
            </Form>
        )
    }
}

const RoleFormWrapper = Form.create<any>()(RoleForm)
export default RoleFormWrapper;
