import React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { get } from 'lodash';

import { Modal, Form, Input } from 'antd';
import FolderTree from '../folderTree';

import { formItemLayout, siderBarType, modalType } from '../../consts'
import * as fileTreeActions from '../../actions/base/fileTree'
import * as experimentActions from '../../actions/experimentActions'
import workbenchActions from '../../actions/workbenchActions'

const FormItem = Form.Item;

@connect(state => {
    return {
        files: state.experiment.files,
        modal: state.modal
    }
}, dispatch => {
    return {
        ...bindActionCreators(fileTreeActions, dispatch),
        ...bindActionCreators(experimentActions, dispatch),
        ...bindActionCreators(workbenchActions, dispatch)
    };
})
class NewExperimentModal extends React.Component {
    form = React.createRef();
    onSubmit = () => {
        this.form.props.form.validateFields(async (err, values) => {
            if (!err) {
                let res = await this.props.addExperiment(values);
                if (res) {
                    this.props.onOk && this.props.onOk(res);
                    this.props.openExperiment(res.data.id);
                    this.props.resetModal();
                }
            }
        })
    }
    render () {
        const { modal, loadTreeData, files } = this.props;
        const visible = modal.visibleModal == modalType.newExperiment;
        return <Modal
            title='新建实验'
            visible={visible}
            onCancel={this.props.resetModal}
            onOk={this.onSubmit}
        >
            {visible && (
                <WrapNewExperimentModalForm
                    loadTreeData={loadTreeData}
                    modal={modal}
                    files={files}
                    wrappedComponentRef={(_form) => { this.form = _form }}
                />
            )}
        </Modal>
    }
}
class NewExperimentModalForm extends React.Component {
    loadData (node) {
        return this.props.loadTreeData(siderBarType.experiment, node.props.data.id);
    }
    getRootNode () {
        const { files } = this.props;
        if (files && files.length) {
            const children = files[0].children;
            const myFolder = children.find((node) => {
                return node.name == '我的实验'
            })
            return myFolder && myFolder.id
        }
        return null;
    }
    render () {
        const { files, form, modal } = this.props;
        const { getFieldDecorator } = form;
        const { modalData = {} } = modal;
        return (
            <Form>
                <FormItem
                    label='实验名称'
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
                        initialValue: get(modalData, 'id') || this.getRootNode()
                    })(
                        <FolderTree loadData={this.loadData.bind(this)} treeData={files} isSelect={true} hideFiles={true} />
                    )}
                </FormItem>
                <FormItem
                    label='实验描述'
                    {...formItemLayout}
                >
                    {getFieldDecorator('desc', {
                        rules: [{
                            max: 64,
                            message: '最大字符不能超过64'
                        }]
                    })(
                        <Input.TextArea />
                    )}
                </FormItem>
            </Form>
        )
    }
}
const WrapNewExperimentModalForm = Form.create()(NewExperimentModalForm);
export default NewExperimentModal;
