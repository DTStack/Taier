// TODO, refactor
import React from 'react';
import { Button, Form, Input } from 'antd';
import { Scrollable } from 'molecule/esm/components';
import { WrappedFormUtils } from 'antd/lib/form/Form';
import FormItem from 'antd/lib/form/FormItem';

import FolderPicker from '../../components/folderPicker'


const formItemLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 },
    },
};
const tailFormItemLayout = {
    wrapperCol: {
        xs: {
            span: 24,
            offset: 0,
        },
        sm: {
            span: 16,
            offset: 8,
        },
    },
};

interface EditFolderProps {
    currentId?: number;
    onSubmitFolder?: (values: any) => Promise<boolean>;
    record?: any;
    form: WrappedFormUtils<any>;
}

class EditFolder extends React.PureComponent<EditFolderProps, {}> {
    state = {
        loading: false,
    };

    handleSubmit = (e: any) => {
        e.preventDefault();
        this.props.form.validateFieldsAndScroll((err, values) => {
            if (!err) {
                this.setState(
                    {
                        loading: true,
                    },
                    () => {
                        const params = { ...values };
                        this.props.onSubmitFolder?.(params).then((loading) => {
                            this.setState({
                                loading,
                            });
                        });
                    }
                );
            }
        });
    };

    componentDidMount() {
        const { form, record } = this.props;
        if (record) {
            form.setFieldsValue({
                nodeName: record.name,
                nodePid: record.parentId,
            });
        }
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        const { loading } = this.state;
        return (
            <Scrollable>
                <Form onSubmit={this.handleSubmit} className="mo-open-task">
                    <FormItem {...formItemLayout} label="目录名称">
                        {getFieldDecorator('nodeName', {
                            rules: [
                                {
                                    max: 64,
                                    message: '任务名称不得超过20个字符！',
                                },
                                {
                                    required: true,
                                },
                            ],
                        })(<Input autoComplete={'off'} />)}
                    </FormItem>
                    <FormItem {...formItemLayout} label="选择目录位置">
                        {getFieldDecorator('nodePid', {
                            rules: [
                                {
                                    required: true,
                                },
                            ],
                        })(<FolderPicker showFile={false} dataType='task' />)}
                    </FormItem>
                    <FormItem {...tailFormItemLayout}>
                        <Button type="primary" htmlType="submit" loading={loading}>
                        Submit
                        </Button>
                    </FormItem>
                </Form>
            </Scrollable>
        );
    }
}

export default Form.create<EditFolderProps>({ name: 'editFolder' })(EditFolder);
