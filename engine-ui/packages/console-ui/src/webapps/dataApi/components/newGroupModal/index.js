import React from 'react';
import { Modal, Form, TreeSelect, Input, message } from 'antd';
import { connect } from 'react-redux';

import { formItemLayout } from '../../consts';
import { apiMarketActions } from '../../actions/apiMarket'
import { apiManageActions } from '../../actions/apiManage';

const FormItem = Form.Item;
const TreeNode = TreeSelect.TreeNode;

class NewGroupForm extends React.Component {
    getGroupTree (treeData, haveRoot, maxCouldChooseDeep) {
        const root = {
            id: 0,
            catalogueName: 'API管理'
        }
        maxCouldChooseDeep = maxCouldChooseDeep || 999999;

        function loop (treeData, length, maxCouldChooseDeep) {
            if (!treeData || treeData.length == 0) {
                return undefined;
            }
            let groupTree = [];

            for (let i = 0; i < treeData.length; i++) {
                let node = treeData[i];
                let childCatalogue = node.childCatalogue;

                if (!node.api) {
                    groupTree.push(
                        <TreeNode disabled={length > maxCouldChooseDeep} value={node.id} title={node.catalogueName} key={node.id} >
                            {loop(childCatalogue, length + 1, maxCouldChooseDeep)}
                        </TreeNode>
                    )
                }
            }
            return groupTree.length > 0 ? groupTree : undefined;
        }

        if (haveRoot) {
            maxCouldChooseDeep = maxCouldChooseDeep + 1;
            root.childCatalogue = treeData;
            return loop([root], 1, maxCouldChooseDeep);
        }
        return loop(treeData, 1, maxCouldChooseDeep);
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        const { treeData } = this.props;

        return (
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="父分组"
                    required
                >
                    {getFieldDecorator('parentNode', {
                    })(
                        <TreeSelect
                            dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                            placeholder="请选择父分组"
                            onChange={this.onChange}
                        >
                            {this.getGroupTree(treeData, true, 1)}
                        </TreeSelect>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="分组名称"
                    required
                >
                    {getFieldDecorator('groupName', {
                    })(
                        <Input />
                    )}
                </FormItem>
            </Form>
        )
    }
}
const WrapNewGroupForm = Form.create({
    onFieldsChange (props, changedFields) {
        props.onChange(changedFields);
    },
    mapPropsToFields (props) {
        const formData = props.formData;
        return {
            ...formData
        }
    }
})(NewGroupForm)

const mapStateToProps = state => {
    const { apiMarket } = state;
    return { apiCatalogue: apiMarket.apiCatalogue }
};

const mapDispatchToProps = dispatch => ({
    getCatalogue (pid) {
        return dispatch(apiMarketActions.getCatalogue(pid));
    },
    addCatalogue (pid, nodeName) {
        return dispatch(apiManageActions.addCatalogue({ pid, nodeName }));
    }
});

@connect(mapStateToProps, mapDispatchToProps)
class NewGroupModal extends React.Component {
    state = {
        formData: {
            groupName: {
                value: undefined
            },
            parentNode: {
                value: undefined
            }
        }
    }

    // eslint-disable-next-line
	componentWillMount () {
        this.props.getCatalogue(0);
    }

    reset () {
        this.setState({
            formData: {
                groupName: {
                    value: undefined
                },
                parentNode: {
                    value: undefined
                }
            }
        })
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps (nextProps) {
        const { visible: nextVisible } = nextProps;
        const { visible } = this.props;
        if (nextVisible != visible && nextVisible) {
            this.reset();
        }
    }

    formChange (fields) {
        this.setState({
            formData: {
                ...this.state.formData, ...fields
            }
        })
    }

    addCatalogue () {
        const { addCatalogue, cancel, getCatalogue, groupChange } = this.props;
        const { groupName, parentNode } = this.state.formData;
        addCatalogue(parentNode.value, groupName.value)
            .then(
                (res) => {
                    if (res) {
                        const id = res.data.id;
                        message.success('新建成功')
                        getCatalogue(0).then(
                            (res) => {
                                if (res) {
                                    groupChange(id)
                                }
                            }
                        );
                        cancel();
                    }
                }
            )
    }

    render () {
        const { visible, cancel, apiCatalogue } = this.props;
        const { formData } = this.state;

        return (
            <Modal
                visible={visible}
                title="新建分组"
                onOk={this.addCatalogue.bind(this)}
                onCancel={cancel}
                maskClosable={false}
                okText="新建"
            >
                <WrapNewGroupForm treeData={apiCatalogue} formData={formData} onChange={this.formChange.bind(this)} />
            </Modal>

        )
    }
}

export default NewGroupModal;
