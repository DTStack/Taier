import React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { Modal, Form, Input } from 'antd';

import { formItemLayout, siderBarType } from '../../consts';
import * as fileTreeActions from '../../actions/base/fileTree';
import * as resourceActions from '../../actions/resourceActions';
const FormItem = Form.Item;

@connect(state => {
    return {
        [siderBarType.notebook]: state.notebook.files,
        [siderBarType.experiment]: state.experiment.files,
        [siderBarType.resource]: state.resource.files
    }
}, dispatch => {
    return {
        ...bindActionCreators(fileTreeActions, dispatch),
        ...bindActionCreators(resourceActions, dispatch)
    }
})
class ResEditModal extends React.Component {
    state = {
        modalKey: null
    }
    onOk = () => {
        const { form, resourceData } = this.props;
        const { validateFields } = form;
        validateFields(async (err, values) => {
            if (!err) {
                let res = await this.props.renameResource(Object.assign({}, resourceData, values));
                if (res) {
                    this.onCancel()
                }
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
        const { form, resourceData = {}, visible } = this.props;
        const { getFieldDecorator } = form;
        return (
            <Modal
                title='资源重命名'
                visible={visible}
                onOk={this.onOk}
                key={modalKey}
                onCancel={this.onCancel}
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="资源名称"
                        hasFeedback
                        key="resourceName"
                    >
                        {getFieldDecorator('resReName', {
                            rules: [{
                                required: true, message: '资源名称不可为空!'
                            }, {
                                pattern: /^[A-Za-z0-9_-]+$/,
                                message: '资源名称只能由字母、数字、下划线组成!'
                            }, {
                                max: 20,
                                message: '资源名称不得超过20个字符!'
                            }],
                            initialValue: resourceData && resourceData.name
                        })(
                            <Input placeholder="请输入资源名称" />
                        )}
                    </FormItem>
                    <FormItem key="resourceId" style={{ display: 'none' }}>
                        {getFieldDecorator('resourceId', {
                            initialValue: resourceData && resourceData.id
                        })(
                            <Input type="hidden"></Input>
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
export default Form.create()(ResEditModal);
