import * as React from 'react';
import { connect } from 'react-redux';
import { TreeSelect } from 'antd';

// const TreeNode = TreeSelect.TreeNode;
const { TreeNode }: any = TreeSelect;

@(connect((state: any) => {
    return {
        dataCatalogues: state.dataManage.dataCatalogues
    }
}) as any)
class CatalogueSelect extends React.Component<any, any> {
    renderNode (data: any) {
        return (data && data.length) ? data.map((item: any) => {
            return (<TreeNode
                value={item.bindData.id}
                title={item.bindData.name}
                key={item.nodeId}
            >
                {this.renderNode(item.children)}
            </TreeNode>)
        }) : undefined
    }
    render () {
        const { dataCatalogues, ...others } = this.props;
        return (
            <TreeSelect
                dropdownStyle={{ maxHeight: 300, overflow: 'auto' }}
                treeNodeFilterProp="title"
                placeholder={'请选择数据类目'}
                {...others}
            >
                {this.renderNode(dataCatalogues.children)}
            </TreeSelect>
        )
    }
}
export default CatalogueSelect;
