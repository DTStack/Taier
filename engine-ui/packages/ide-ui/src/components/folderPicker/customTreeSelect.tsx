import React, { PureComponent } from 'react'
import { omit } from 'lodash'
import {
    TreeSelect, Input
} from 'antd';

import { TreeSelectProps } from 'antd/lib/tree-select'
import { Icon } from 'molecule/esm/components';

const TreeNode = TreeSelect.TreeNode

export interface CustomTreeSelectProps extends TreeSelectProps<any> {
    treeData?: any;
    onChange?: (value?: any, label?: any, extra?: any) => void;
    showName?: any;
    nodeNameField?: string;
    showFile: boolean;
    [propName: string]: any;
}
/**
 * 解决使用异步加载数据情况下，模糊搜索时展示id的问题
 * 使用 Input 和 TreeSelect 组件分别承担 数据收集和选择、展示的功能 
 */
class CustomTreeSelect extends PureComponent<CustomTreeSelectProps, any> {

    constructor (props: CustomTreeSelectProps) {
        super(props)
        this.state = {
            realValue: props.value, // 表单收集数据时真正的值，兼容initialValue
            showName: props.showName // 树组件选择框显示的值，兼容initialName
        }
    }

    onTreeChange = (value: any, label: any, extra: any) => {
        console.log(value)
        // 让 form.getFieldDecorator 正常工作
        const { onChange } = this.props
        onChange && onChange(value, label, extra) 
        this.setState({
            realValue: value
        })
    }

    updateShowName = (value: any, node: any) => {
        let { nodeNameField } = this.props
        nodeNameField = nodeNameField ?? 'name'
        this.setState({
            showName: node.props?.[nodeNameField]
        })
    }

    // TODO: 将generateTreeNodes暴露出去以兼容不同的数据格式
    generateTreeNodes = () => {
        const { treeData, showFile } = this.props
        const loop = (data: any) => {
            const { createUser, id, name, type } = data;
            const isLeaf = type === 'file'
            if(!showFile && type === 'file') return null
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
                    isLeaf={isLeaf}
                    icon={showFile ? (type === 'file' ? <Icon type='file'/> : <Icon type='folder'/>) : null}
                >
                    {data?.children?.map((o: any) => loop(o))}
                </TreeNode>
            )
        }
        return loop(treeData)
    }

    render () {
        const { realValue, showName } = this.state
        const { showFile } = this.props
        return (
            <>
                <Input type='hidden' value={realValue}/>
                <TreeSelect
                    {...omit(this.props,['onChange', 'treeData', 'value'])}
                    value={showName}
                    onChange={ this.onTreeChange }
                    onSelect={ this.updateShowName }
                    treeIcon={showFile}
                >
                    {this.generateTreeNodes()}
                </TreeSelect>
            </>
        )
    }
}

export default CustomTreeSelect
