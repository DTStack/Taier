import * as React from 'react';
import { Modal, Form, Select, Radio, Input } from 'antd';
// import { formItemLayout } from '../../../../comm/const';
import SetDictionary from '../../../../components/setDictionary';

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;

const formItemLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 }
    }
};

class ConfigDictModal extends React.Component<any, any> {
    state: any = {
        dictionaryOption: [
            { label: 'xxx1', value: 'xxx1' },
            { label: 'xxx2', value: 'xxx2' },
            { label: 'xxx3', value: 'xxx3' }
        ]
    }

    componentDidMount () {
        // TODO isLabel ? 请求标签字典下拉 : 请求维度字典下拉
    }

    onCancel = () => {
        this.props.onCancel();
    }

    onOK = () => {
        this.props.onOk();
    }

    render () {
        const { dictionaryOption } = this.state;
        const { visible, isLabel } = this.props;
        const { getFieldDecorator, getFieldValue } = this.props.form;

        let wayOption: any[] = [
            { label: '引用', value: 'refer' }
        ];
        if (isLabel) { // isLabel 判断是否是 原子标签处的配置字典
            wayOption.unshift({ label: '自定义', value: 'auto' })
        }
        return (
            <Modal
                title="配置字典"
                wrapClassName="vertical-center-modal"
                visible={visible}
                onOk={this.onOK}
                onCancel={this.onCancel}
                maskClosable={false}
            >
                <Form>
                    <FormItem {...formItemLayout} label="选择方式" >
                        {getFieldDecorator('way', {
                            rules: [{
                                required: true, message: '选择方式不可为空！'
                            }],
                            initialValue: isLabel ? 'auto' : 'refer'
                        })(
                            <RadioGroup options={wayOption} />
                        )}
                    </FormItem>
                    {getFieldValue('way') == 'refer' && <FormItem {...formItemLayout} label="字典引用" >
                        {getFieldDecorator('dictRef', {
                            rules: [{
                                required: true, message: '字典引用不可为空！'
                            }]
                        })(
                            <Select
                                style={{ width: '100%' }}
                                placeholder={isLabel ? '请选择标签字典' : '请选择维度字典'}
                            >
                                {dictionaryOption.map((item: any) => (
                                    <Option key={item.value} value={item.value}>{item.label}</Option>
                                ))}
                            </Select>
                        )}
                    </FormItem>}
                    {getFieldValue('way') == 'auto' && <FormItem {...formItemLayout} label="字典设置" >
                        {getFieldDecorator('dictSetName', {
                            rules: [{
                                required: true, message: '字典名称不可为空！'
                            }]
                        })(
                            <Input placeholder="请输入字典名称" />
                        )}
                    </FormItem>}
                    {getFieldValue('way') == 'auto' && <Form.Item style={{ marginTop: -14 }} {...formItemLayout} label=" " required={false} colon={false}>
                        {getFieldDecorator('dictSetRule', {
                            rules: [
                                {
                                    required: true,
                                    message: '字典设置不可为空！'
                                }
                            ]
                        })(<SetDictionary isEdit={true} />)}
                    </Form.Item>}
                </Form>
            </Modal>
        )
    }
}

export default Form.create<any>({})(ConfigDictModal)
