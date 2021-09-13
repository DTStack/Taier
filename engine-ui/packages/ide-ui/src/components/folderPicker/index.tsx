import React, { Component } from 'react'
import {
    TreeSelect, Input
} from 'antd';
import { omit } from 'lodash'
import { TreeSelectProps } from 'antd/lib/tree-select'

const TreeNode = TreeSelect.TreeNode

interface FolderPickerProps extends TreeSelectProps<any> {
    treeData: any;
    onChange: (value?: any, label?: any, extra?: any) => void;
    showName?: any;
    nodeNameField: string;
    [propName: string]: any;
}
/**
 * 解决使用异步加载数据情况下，模糊搜索时展示id的问题
 * 使用 Input 和 TreeSelect 组件分别承担 数据收集和选择、展示的功能 
 */
class FolderPicker extends Component<FolderPickerProps, any> {

    constructor (props: FolderPickerProps) {
        super(props)
        this.state = {
            realValue: props.value, // 表单收集数据时真正的值，兼容initialValue
            showName: props.showName // 树组件选择框显示的值，兼容initialName
        }
    }

    onTreeChange = (value: any, label: any, extra: any) => {
        // 让 form.getFieldDecorator 正常工作
        const { onChange } = this.props
        onChange(value, label, extra) 
        this.setState({
            realValue: value
        })
    }

    updateShowName = (value: any, node: any) => {
        const { nodeNameField } = this.props
        this.setState({
            showName: node.props?.[nodeNameField]
        })
    }

    // TODO: 将generateTreeNodes暴露出去以兼容不同的数据格式
    generateTreeNodes = () => {
        const { treeData } = this.props
        const loop = (data: any) => {
            const { createUser, id, name } = data;
            return (
                <TreeNode
                    title={
                        <span title={name}>
                            { name }&nbsp;
                            <i className="item-tooltip" title={createUser}>
                                <span style={{ color: '#ccc' }}>{createUser}</span>
                            </i>
                        </span> 
                    }
                    value={id}
                    name={name}
                    dataRef={data}
                    key={id}
                >
                    {data.children && data.children.map((o: any) => loop(o))}
                </TreeNode>
            )
        }
        return loop(treeData)
    }

    render () {
        const { realValue, showName } = this.state
        return (
            <>
                <Input type='hidden' value={realValue}/>
                <TreeSelect
                    value={showName}
                    onChange={ this.onTreeChange }
                    onSelect={ this.updateShowName }
                    {...omit(this.props,['onChange', 'treeData', 'value'])}
                >
                    {this.generateTreeNodes()}
                </TreeSelect>
            </>
        )
    }
}

export default FolderPicker
