import * as React from 'react';
import { Link } from 'react-router';
import { Form, Input, Select, Breadcrumb } from 'antd';

import { formItemLayout } from '../../../comm/const';

const FormItem = Form.Item;
const { Option } = Select;

interface IState {
    typeOption: any[];
    ruleFile: any;
}

class DictionaryEdit extends React.Component<any, IState> {
    state: IState = {
        typeOption: [
            { label: '标签字典', value: 'label' },
            { label: '维度字典', value: 'dimension' }
        ],
        ruleFile: {}
    }

    componentDidMount () {
        console.log('location:', this.props.location);
    }

    handleFileChange = (e: any) => {
        const ruleFile = e.target
        this.setState({ ruleFile })
    }

    render () {
        const { typeOption, ruleFile } = this.state;
        const { getFieldDecorator } = this.props.form;
        return (
            <div className="dictionary-edit">
                <Breadcrumb>
                    <Breadcrumb.Item><Link to="/dictionaryManage">字典管理</Link></Breadcrumb.Item>
                    <Breadcrumb.Item>新增字典</Breadcrumb.Item>
                </Breadcrumb>
                <Form>
                    <FormItem {...formItemLayout} label="字典名称" >
                        {getFieldDecorator('name', {
                            rules: [{
                                required: true,
                                message: '字典名称不可为空！'
                            }]
                        })(
                            <Input placeholder="请输入字典中文名称，20字以内的中文字符" />
                        )}
                    </FormItem>
                    <FormItem {...formItemLayout} label="字典类型" >
                        {getFieldDecorator('type', {
                            rules: [{
                                required: true,
                                message: '字典类型不可为空！'
                            }]
                        })(
                            <Select placeholder='请选择字典类型' style={{ width: '100%' }}>
                                {typeOption.map((item: any) => (
                                    <Option key={item.value} value={item.value}>{item.label}</Option>
                                ))}
                            </Select>
                        )}
                    </FormItem>
                    <FormItem {...formItemLayout} label="字典描述" >
                        {getFieldDecorator('desc', {
                            rules: [{
                                max: 20,
                                message: '描述不得超过20个字符！'
                            }]
                        })(
                            <Input type="textarea" placeholder="请输入字典描述信息，长度限制在20个字符以内" />
                        )}
                    </FormItem>

                    <FormItem {...formItemLayout} label="设置字典规则" >
                        {getFieldDecorator('rule', {
                            rules: [{
                                required: true, message: '字典规则不可为空！'
                            }]
                        })(
                            <div>
                                <label
                                    style={{ lineHeight: '28px' }}
                                    className="ant-btn btn-upload"
                                    htmlFor="myFile">选择文件</label>
                                <span> {ruleFile.files && ruleFile.files[0] && ruleFile.files[0].name}</span>
                                <input
                                    name="file"
                                    type="file"
                                    id="myFile"
                                    accept=".jar"
                                    onChange={this.handleFileChange}
                                    style={{ display: 'none' }}
                                />
                                <a>下载模版</a>
                            </div>
                        )}
                    </FormItem>
                </Form>
            </div>
        )
    }
}

export default Form.create<any>()(DictionaryEdit)
