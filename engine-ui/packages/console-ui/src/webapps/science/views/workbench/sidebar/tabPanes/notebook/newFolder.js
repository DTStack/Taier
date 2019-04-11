import React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { get } from 'lodash';
import { Modal, Form, Input } from 'antd';

import { formItemLayout, siderBarType } from '../../../../../consts';
import FolderTree from '../../folderTree';
import * as fileTreeActions from '../../../../../actions/base/fileTree';

const FormItem = Form.Item;

@connect(state => {
    return {
        files: state.notebook.files
    }
}, dispatch => {
    const actions = bindActionCreators(fileTreeActions, dispatch);
    return actions;
})
class NewNotebookFolder extends React.Component {
    state = {
        modalKey: null
    }
    loadData (node) {
        return this.props.loadTreeData(siderBarType.notebook, node.props.data.id);
    }
    onOk = () => {
        const { form } = this.props;
        const { validateFields } = form;
        validateFields((err, values) => {
            if (!err) {
                console.log(values);
                this.props.onOk(values);
                this.resetForm();
            }
        })
    }
    resetForm = () => {
        this.setState({
            modalKey: Math.random()
        })
    }
    onCancel = () => {
        this.resetForm();
        this.props.onCancel();
    }
    render () {
        const { modalKey } = this.state;
        const { form, files, data } = this.props;
        const { getFieldDecorator } = form;
        return (
            <Modal
                title="新建文件夹"
                visible={this.props.visible}
                onOk={this.onOk}
                key={modalKey}
                onCancel={this.onCancel}
            >
                <Form>
                    <FormItem
                        label='文件夹名称'
                        {...formItemLayout}
                    >
                        {getFieldDecorator('folderName', {
                            rules: [{
                                required: true,
                                message: '请输入文件夹名称'
                            }]
                        })(
                            <Input placeholder='名称不超过32个字符' />
                        )}
                    </FormItem>
                    <FormItem
                        label='父节点'
                        {...formItemLayout}
                    >
                        {getFieldDecorator('nodePid', {
                            rules: [{
                                required: true,
                                message: '请选择父节点'
                            }],
                            initialValue: get(data, 'id')
                        })(
                            <FolderTree loadData={this.loadData.bind(this)} treeData={files} isSelect={true} hideFiles={true} />
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
export default Form.create()(NewNotebookFolder);
