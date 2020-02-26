import * as React from 'react';
import { get } from 'lodash';
import { Modal, Form, Select, Alert, Button, Input } from 'antd';
import { FormProps } from 'antd/lib/form/Form';
import { formItemLayout } from '../../../consts';

const Option = Select.Option;

interface IProps extends FormProps {
    title: string;
    data: object;
    userList: any[];
    visible: boolean;
    engineText: string;
    onCancel: (e: any) => any;
    onOk: (e: any) => any;
    onUnbind: (e: any) => any;
}

class BindAccountModal extends React.Component<IProps, any> {
    constructor (props: any) {
        super(props);
        this.state = {}
    }

    onSubmit = (callback: (values: any) => {}) => {
        const { form, userList } = this.props;
        form.validateFields((err, user) => {
            if (!err) {
                const selectedUser = userList.find(u => u.userId == user.bindUserId);
                if (selectedUser) {
                    user.username = selectedUser.userName;
                    user.email = selectedUser.userName;
                }
                // 此处主要是由于后端字段不一致的原因所致
                if (user.id) {
                    user.bindUserId = user.id;
                }

                if (callback) {
                    callback(user);
                    setTimeout(() => form.resetFields(), 0);
                }
            }
        })
    }

    onOk = () => {
        const { onOk } = this.props;
        this.onSubmit(onOk)
    }

    onUnbind = () => {
        const { onUnbind } = this.props;
        this.onSubmit(onUnbind)
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const { visible, onCancel, title, data, engineText, userList } = this.props;
        const isEdit = data !== null && data !== undefined;
        const footer = (
            <div style={{ height: '30px' }}>
                { data
                    ? <span className="left">
                        <Button onClick={this.onUnbind}>解除绑定</Button>
                    </span>
                    : null
                }
                <span className="right">
                    <Button onClick={onCancel} style={{ marginRight: 10 }}>取消</Button>
                    <Button type="primary" disabled={isEdit} onClick={this.onOk}>确定</Button>
                </span>
            </div>
        )

        return (
            <Modal
                closable
                title={title}
                visible={visible}
                footer={footer}
                onCancel={onCancel}
                className={'no-padding-modal'}
            >
                <React.Fragment>
                    <Alert
                        message=""
                        style={{ margin: '10px' }}
                        description={`每个产品账号绑定一个${engineText}用户，任务提交运行、测试时，使用绑定的${engineText}用户执行`}
                        type="info"
                        showIcon
                    />
                    <Form>
                        { !isEdit
                            ? <Form.Item
                                key="bindUserId"
                                label="产品账号"
                                {...formItemLayout}
                            >
                                {getFieldDecorator('bindUserId', {
                                    rules: [{
                                        required: true,
                                        message: '租户不可为空！'
                                    }],
                                    initialValue: ''
                                })(
                                    <Select
                                        allowClear
                                        showSearch
                                        placeholder='请选择产品账号'
                                        optionFilterProp="title"
                                    >
                                        {userList && userList.map((user: any) => {
                                            const uid = `${user.userId}`;
                                            const uname = user.userName;
                                            return <Option key={uid} title={uname} value={uid}>{uname}</Option>
                                        })}
                                    </Select>
                                )}
                            </Form.Item>
                            : (
                                <React.Fragment>
                                    <Form.Item
                                        key="username"
                                        label="产品账号"
                                        {...formItemLayout}
                                    >
                                        {getFieldDecorator('username', {
                                            rules: [{
                                                required: true,
                                                message: '租户不可为空！'
                                            }],
                                            initialValue: get(data, 'username', '')
                                        })(
                                            <Input disabled={isEdit}/>
                                        )}
                                    </Form.Item>
                                    <Form.Item
                                        key="id"
                                        label="产品账号"
                                        style={{ display: 'none' }}
                                        {...formItemLayout}
                                    >
                                        {getFieldDecorator('id', {
                                            initialValue: get(data, 'id', undefined)
                                        })(
                                            <Input />
                                        )}
                                    </Form.Item>
                                </React.Fragment>
                            )
                        }
                        <Form.Item
                            label="数据库账号"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('name', {
                                rules: [{
                                    required: true,
                                    message: '集群不可为空！'
                                }],
                                initialValue: ``
                            })(
                                <Input placeholder="请输入数据库账号" />
                            )}
                        </Form.Item>
                        <Form.Item
                            label="数据库密码"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('password', {
                                rules: [{
                                    required: true,
                                    message: '集群不可为空！'
                                }],
                                initialValue: ``
                            })(
                                <Input type="password" placeholder="请输入数据库密码" />
                            )}
                        </Form.Item>
                    </Form>
                </React.Fragment>
            </Modal>
        )
    }
}
export default Form.create<IProps>()(BindAccountModal);
