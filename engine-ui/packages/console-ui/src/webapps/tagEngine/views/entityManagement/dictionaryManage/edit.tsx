import * as React from 'react';
import { Form, Input, Select, Card, Button } from 'antd';
import Breadcrumb from '../../../components/breadcrumb';
import SetDictionary from '../../../components/setDictionary';

const FormItem = Form.Item;
const { Option } = Select;

interface IState {
    typeOption: any[];
    ruleFile: any;
}

const formItemLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 3 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 8 }
    }
};

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

    handleSava = () => {
        this.props.form.validateFields(async (err: any, values: any) => {
            if (!err) {
                console.log(values);
            }
        })
    }

    render () {
        const { typeOption, ruleFile } = this.state;
        const { getFieldDecorator } = this.props.form;
        const breadcrumbNameMap = [
            {
                path: '/dictionaryManage',
                name: '字典管理'
            },
            {
                path: '',
                name: '新增字典'
            }
        ];
        return (
            <div className="dictionary-edit tage-dictionary-manage">
                <Breadcrumb breadcrumbNameMap={breadcrumbNameMap} />
                <Card
                    noHovering
                    bordered={false}
                    className="noBorderBottom shadow"
                >
                    <Form style={{ margin: '40px 0px 70px' }}>
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
                            <span className="notice">#维度字典应用于数据源维度名称转译；标签字典应用于原子标签的标签值转译</span>
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
                        <Form.Item style={{ marginBottom: 40 }} {...formItemLayout} label="设置字典规则">
                            <div className="upload-file-box">
                                <label
                                    style={{ lineHeight: '28px' }}
                                    className="upload-file-area"
                                    htmlFor="myFile"
                                >
                                    <img src="public/tagEngine/img/icon_upload.png" />
                                    <span className="text">{ruleFile.files && ruleFile.files[0] ? ruleFile.files[0].name : '单击上传文件'}</span>
                                </label>
                                <input
                                    name="file"
                                    type="file"
                                    id="myFile"
                                    accept=".txt"
                                    onChange={this.handleFileChange}
                                    style={{ display: 'none' }}
                                />
                                <a className="download-btn">下载模版</a>
                            </div>
                            {getFieldDecorator('rule', {
                                rules: [
                                    {
                                        required: true,
                                        message: '字典规则不可为空！'
                                    }
                                ]
                            })(<SetDictionary isEdit={true} />)}
                        </Form.Item>
                        <FormItem {...formItemLayout} label=" " required={false} colon={false}>
                            <Button type='primary' size='default' onClick={this.handleSava}>保存</Button>
                        </FormItem>
                    </Form>
                </Card>
            </div>
        )
    }
}

export default Form.create<any>()(DictionaryEdit)
