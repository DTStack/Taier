import * as React from 'react';
import { Modal, Button, Form, Input, message as Message } from 'antd';
import { FormComponentProps } from 'antd/lib/form/Form';
import { API } from '../../../../../api/api';

import './style.scss';
interface IProps extends FormComponentProps {
    visible: boolean;
    handleOk: () => void;
    handleCancel: () => void;
    data?: any;
    entityId: string;
    type: string;
}

interface IState {

}

const formItemLayout = {
    labelCol: {
        xs: { span: 4 },
        sm: { span: 6 }
    },
    wrapperCol: {
        xs: { span: 18, offset: 3 },
        sm: { span: 16, offset: 4 }
    }
};
class AddDirectpry extends React.PureComponent<IProps, IState> {
    state: IState = {
        entityId: ''
    };
    componentDidUpdate (preProps) {
        const { data, type } = this.props;
        if (data != preProps.data && type == '2') {
            this.props.form.setFieldsValue({ cateName: data.cateName })
        }
    }
    handleCancel = () => {
        this.props.handleCancel();
        this.props.form.resetFields();
    };
    handleOk = () => {
        const that = this;
        this.props.form.validateFields((err, values) => {
            if (!err) {
                that.addOrUpdateTagCate(values)
            }
        });
    };
    addOrUpdateTagCate = (params) => { // 标签引擎-新增/重命名标签层级
        const { entityId, data, type } = this.props;
        const { cateName } = params;
        let { tagCateId, pid } = data;
        let id = '';
        if (type == '1') { // 新增子目录
            pid = tagCateId;
        } else if (type == '0') { // 新增目录
            pid = '-1'
        } else {
            id = tagCateId;
        }
        API.addOrUpdateTagCate({
            id,
            entityId,
            cateName,
            pid
        }).then(res => {
            const { code, message } = res;
            if (code == 1) {
                this.props.handleOk();
                Message.success('操作成功！');
                this.props.form.resetFields();
            } else {
                Message.error(message)
            }
        })
    }
    render () {
        const { visible, form, type } = this.props;
        let title = '新建目录';
        if (type == '1') {
            title = '新建子目录';
        } else if (type == '2') {
            title = '重命名';
        }
        const { getFieldDecorator } = form;
        return (
            <Modal
                visible={visible}
                title={title}
                onOk={this.handleOk}
                onCancel={this.handleCancel}
                footer={[
                    <Button key="back" size="large" onClick={this.handleCancel}>
                        取消
                    </Button>,
                    <Button
                        key="submit"
                        type="primary"
                        size="large"
                        onClick={this.handleOk}
                    >
                        保存
                    </Button>
                ]}
            >
                <Form.Item {...formItemLayout}>
                    {getFieldDecorator('cateName', {
                        rules: [
                            {
                                required: true,
                                message: '请输入目录名称'
                            },
                            {
                                max: 20,
                                message: '最多输入20个字符'
                            },
                            {
                                pattern: /^[\u4E00-\u9FA5A-Za-z0-9_]+$/,
                                message: '姓名只能包括汉字，字母、下划线、数字'
                            }
                        ]
                    })(<Input placeholder="请输入目录名称" />)}
                </Form.Item>
            </Modal>
        );
    }
}
export default Form.create()(AddDirectpry);
