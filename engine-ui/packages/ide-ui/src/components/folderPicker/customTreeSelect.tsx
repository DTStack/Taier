import React, { PureComponent } from 'react'
import { omit } from 'lodash'
import {
    TreeSelect, Input
} from 'antd';

import { TreeSelectProps } from 'antd/lib/tree-select'
import { Icon } from '@dtinsight/molecule/esm/components';

const TreeNode = TreeSelect.TreeNode

const getFolderName = (treeData: any, id: any) => {
    let name: any;
    const loop = (arr: any) => {
        arr.forEach((node: any, i: any) => {
            if (node.id === id) {
                name = node.id;
            } else {
                loop(node.children || []);
            }
        });
    };
    loop([treeData]);
    return name;
}

export interface CustomTreeSelectProps extends TreeSelectProps<any> {
    treeData?: any;
    onChange?: (value?: any, label?: any, extra?: any) => void;
    defaultShowName?: any;
    nodeNameField?: string;
    showFile: boolean;
    [propName: string]: any;
}
/**
 * 解决使用异步加载数据情况下，模糊搜索时展示id的问题
 * 使用 Input 和 TreeSelect 组件分别承担 数据收集和选择、展示的功能 
 */
class CustomTreeSelect extends PureComponent<CustomTreeSelectProps, any> {

    componentDidUpdate(prevProps:CustomTreeSelectProps){
        const { value, treeData } = this.props
        if(prevProps.value !== value) {
            const folderName = getFolderName(treeData, value)
            this.setState({
                showName: folderName,
                realValue: value
            })
        }
    }

    constructor (props: CustomTreeSelectProps) {
        super(props)
        this.state = {
            realValue: props.value, // 表单收集数据时真正的值，兼容initialValue
            showName: props.defaultShowName ?? props.value // 树组件选择框显示的值，兼容initialName
        }
    }

    onTreeChange = (value: any, label: any, extra: any) => {
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
