import React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { get } from 'lodash';

import { Modal, Form, Input } from 'antd';
import FolderTree from '../folderTree';

import { formItemLayout, siderBarType, modalType } from '../../consts'
import * as fileTreeActions from '../../actions/base/fileTree'
import * as notebookActions from '../../actions/notebookActions'
import workbenchActions from '../../actions/workbenchActions'

const FormItem = Form.Item;

@connect(state => {
    return {
        files: state.notebook.files,
        modal: state.modal
    }
}, dispatch => {
    return {
        ...bindActionCreators(fileTreeActions, dispatch),
        ...bindActionCreators(notebookActions, dispatch),
        ...bindActionCreators(workbenchActions, dispatch)
    };
})
class NewNotebookModal extends React.Component {
    form = React.createRef();
    onSubmit = () => {
        this.form.props.form.validateFields(async (err, values) => {
            if (!err) {
                let res = await this.props.addNotebook(values);
                if (res) {
                    this.props.onOk && this.props.onOk(res);
                    this.props.resetModal();
                }
            }
        })
    }
    render () {
        const { modal, loadTreeData, files } = this.props;
        const visible = modal.visibleModal == modalType.newNotebook;
        return <Modal
            title='新建Notebook'
            visible={visible}
            onCancel={this.props.resetModal}
            onOk={this.onSubmit}
        >
            {visible && (
                <WrapNewNotebookModalForm
                    loadTreeData={loadTreeData}
                    modal={modal}
                    files={files}
                    wrappedComponentRef={(_form) => { this.form = _form }}
                />
            )}
        </Modal>
    }
}
class NewNotebookModalForm extends React.Component {
    loadData (node) {
        return this.props.loadTreeData(siderBarType.notebook, node.props.data.id);
    }
    render () {
        const { files, form, modal } = this.props;
        const { getFieldDecorator } = form;
        const { modalData = {} } = modal;
        return (
            <Form>
                <FormItem
                    label='Notebook名称'
                    {...formItemLayout}
                >
                    {getFieldDecorator('name', {
                        rules: [{
                            required: true,
                            message: '请输入名字'
                        }, {
                            pattern: /^[\w\d_]{1,32}$/,
                            message: '名字不超过32个字符,只支持字母、数字、下划线'
                        }]
                    })(
                        <Input />
                    )}
                </FormItem>
                <FormItem
                    label='存储位置'
                    {...formItemLayout}
                >
                    {getFieldDecorator('nodePid', {
                        rules: [{
                            required: true,
                            message: '请选择存储位置'
                        }],
                        initialValue: get(modalData, 'id')
                    })(
                        <FolderTree loadData={this.loadData.bind(this)} treeData={files} isSelect={true} hideFiles={true} />
                    )}
                </FormItem>
                <FormItem
                    label='Notebook描述'
                    {...formItemLayout}
                >
                    {getFieldDecorator('desc')(
                        <Input.TextArea />
                    )}
                </FormItem>
            </Form>
        )
    }
}
const WrapNewNotebookModalForm = Form.create()(NewNotebookModalForm);
export default NewNotebookModal;
