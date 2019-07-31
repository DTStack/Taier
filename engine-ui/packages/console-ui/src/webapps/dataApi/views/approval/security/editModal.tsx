import * as React from 'react';

import { Modal, Button, Form, Input, Select, message } from 'antd';

import { formItemLayout, SECURITY_TYPE } from '../../../consts'
import api from '../../../api/approval';

const FormItem = Form.Item;
const TextArea = Input.TextArea;
const Option = Select.Option;

class EditModal extends React.Component<any, any> {
    state: any = {
        loading: false
    }
    getModelTitle () {
        const { mode } = this.props;
        switch (mode) {
            case 'edit': {
                return '编辑'
            }
            case 'view': {
                return '查看详情'
            }
            case 'new': {
                return '新建安全组'
            }
            default: {
                return '';
            }
        }
    }
    getFooter () {
        const { mode } = this.props;
        switch (mode) {
            case 'edit': {
                return undefined
            }
            case 'view': {
                return <Button onClick={this.props.onCancel} type="primary">关闭</Button>
            }
            case 'new': {
                return undefined
            }
            default: {
                return '';
            }
        }
    }
    updateOrAddSecurity () {
        const { mode, record, form } = this.props;
        form.validateFields(null, (err: any, values: any) => {
            if (!err) {
                let params = values;
                let func: any;
                let successMsg: any;
                if (mode == 'edit') {
                    params.id = record.id;
                    func = 'updateSecurity';
                    successMsg = '修改成功'
                } else if (mode == 'new') {
                    func = 'addSecurity';
                    successMsg = '新增成功'
                }
                this.setState({
                    loading: true
                })
                api[func](params).then((res: any) => {
                    this.setState({
                        loading: false
                    })
                    if (res.code == 1) {
                        message.success(successMsg, 2)
                        this.props.onok();
                    }
                })
            }
        });
    }
    onOk () {
        this.updateOrAddSecurity()
    }
    render () {
        const { visible, onCancel, form, mode, record } = this.props;
        const { loading } = this.state;
        const { getFieldDecorator } = form;
        const isView = mode == 'view';
        return (
            <Modal
                confirmLoading={loading}
                visible={visible}
                onCancel={onCancel}
                onOk={this.onOk.bind(this)}
                maskClosable={false}
                title={this.getModelTitle()}
                footer={this.getFooter()}
            >
                <Form>
                    <FormItem
                        label="类型"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('type', {
                            initialValue: record.type == undefined ? SECURITY_TYPE.WHITE : record.type
                        })(
                            <Select disabled={isView} style={{ width: '100%' }}>
                                <Option value={SECURITY_TYPE.WHITE} key={SECURITY_TYPE.WHITE}>白名单</Option>
                                <Option value={SECURITY_TYPE.BLACK} key={SECURITY_TYPE.BLACK}>黑名单</Option>
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        label="名称"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('name', {
                            rules: [
                                { required: true, message: '请输入安全组名称' },
                                { max: 64, message: '最大字数不能超过64' }
                            ],
                            initialValue: record.name
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="IP地址"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('ip', {
                            rules: [{ required: true, message: '请输入IP地址' }],
                            initialValue: record.ip
                        })(
                            <TextArea placeholder="每行一个ip地址段或ip地址，例如&#10;192.168.1.20-192.168.1.30&#10;192.168.2.1" disabled={isView} rows={4} />
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}

export default Form.create<any>()(EditModal);
