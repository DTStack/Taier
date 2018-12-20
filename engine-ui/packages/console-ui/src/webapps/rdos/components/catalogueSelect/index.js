import React from 'react';
import { connect } from 'react-redux';
import { TreeSelect } from 'antd';

const TreeNode = TreeSelect.TreeNode;

@connect(state => {
    return {
        dataCatalogues: state.dataManage.dataCatalogues
    }
})
class CatalogueSelect extends React.Component {
    renderNode (data) {
        return (data && data.length) ? data.map((item) => {
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
