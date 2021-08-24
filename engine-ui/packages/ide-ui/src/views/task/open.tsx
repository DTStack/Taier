// TODO, refactor
import React from 'react';
import { Button, Form, Input, Select } from 'antd';
import { WrappedFormUtils } from 'antd/lib/form/Form';
import FormItem from 'antd/lib/form/FormItem';
import { TASK_TYPE } from '../../comm/const';

const Option = Select.Option;

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

interface OpenProps {
    currentId?: number;
    onSubmit?: (values: any) => Promise<boolean>;
    form: WrappedFormUtils<any>;
}

const taskType = [
    {
        value: TASK_TYPE.SQL,
        text: 'SparkSql',
    },
    {
        value: TASK_TYPE.SYNC,
        text: '数据同步',
    },
];

class Open extends React.PureComponent<OpenProps, {}> {
    state = {
        loading: false
    }

    handleSubmit = (e: any) => {
        e.preventDefault();
        this.props.form.validateFieldsAndScroll((err, values) => {
            if (!err) {
                this.setState({
                    loading: true
                }, () => {
                    this.props.onSubmit?.(values)
                        .then((loading) => {
                            this.setState({
                                loading
                            })
                        })
                   
                })
                
            }
        });
    };

    render() {
        const { getFieldDecorator } = this.props.form;
        const { loading } = this.state;
        return (
            <Form onSubmit={this.handleSubmit} className="mo-open-task">
                <FormItem {...formItemLayout} label="任务名称">
                    {getFieldDecorator('name', {
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
                <FormItem {...formItemLayout} label="任务类型">
                    {getFieldDecorator('taskType', {
                        rules: [
                            {
                                required: true,
                            },
                        ],
                    })(
                        <Select>
                            {taskType.map((type) => (
                                <Option key={type.value} value={type.value}>
                                    {type.text}
                                </Option>
                            ))}
                        </Select>
                    )}
                </FormItem>
                <FormItem {...formItemLayout} label="存储位置">
                    {getFieldDecorator('nodePid', {
                        rules: [
                            {
                                required: true,
                            },
                        ],
                    })(
                        <Input />
                    )}
                </FormItem>
                <FormItem {...formItemLayout} label="描述" hasFeedback>
                    {getFieldDecorator('taskDesc', {
                        rules: [
                            {
                                max: 200,
                                message: '描述请控制在200个字符以内！',
                            },
                        ],
                    })(<Input.TextArea disabled={false} rows={4} />)}
                </FormItem>
                <FormItem {...tailFormItemLayout}>
                    <Button type="primary" htmlType="submit" loading={loading}>
                        Submit
                    </Button>
                </FormItem>
            </Form>
        );
    }
}

export default Form.create<OpenProps>({ name: 'open' })(Open);
