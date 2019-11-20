import * as React from 'react';
import { Form, Input, Select, Card, Button, Tooltip, Icon, message as Message } from 'antd';
import Breadcrumb from '../../../components/breadcrumb';
import SetDictionary from '../../../components/setDictionary';
import shortid from 'shortid';

const FormItem = Form.Item;
const { Option } = Select;

interface IState {
    typeOption: any[];
    ruleFile: any;
    ruleFileSource: any;
    charset: string;
    fileData: any[];
    splitSymbol: string;
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
        ruleFile: undefined,
        ruleFileSource: undefined,
        fileData: [],
        splitSymbol: ',',
        charset: 'UTF-8'
    }

    componentDidMount () {
        console.log('location:', this.props.location);
    }

    handleFileChange = (e: any) => {
        const ruleFile = e.target.files[0];
        const sizeLimit = 5 * 1024 * 1024 // 5MB
        if (ruleFile.size > sizeLimit) {
            Message.warning('本地上传文件不可超过5MB!')
        } else {
            this.setState({ ruleFile }, () => {
                this.readFile(ruleFile)
            })
        }
    }

    readFile = (file: any) => {
        const { charset } = this.state
        if (file) {
            const reader = new FileReader();
            reader.onload = ((data: any) => {
                return (e: any) => {
                    this.setState({
                        ruleFileSource: e.target.result
                    })
                    this.parseFile(e.target.result)
                }
            })(file)
            reader.readAsText(file, charset)
        }
    }

    parseFile (data: any) {
        const { splitSymbol } = this.state
        const arr: any = []
        const splitVal = this.parseSplitSymbol(splitSymbol)

        data = data.replace(/\r\n/g, '\n').replace(/\r/g, '\n').split('\n');

        // 防卡死
        if (data && data[0].length > 5000) {
            Message.error('文件内容不正确！');
            return;
        }

        for (let i = 0; i < data.length; i++) {
            const str = data[i].replace(/\r/, '') // 清除无用\r字符
            if (str) {
                arr.push(str.split(splitVal))
            }
        }

        const subArr = arr.slice(1);

        this.setState({
            fileData: subArr || []
        })

        let ruleData = [];
        subArr.forEach((item: any, index: number) => {
            if (index < 500) {
                ruleData.push({
                    name: item[0], value: item[1], key: shortid()
                })
            }
        })
        this.props.form.setFieldsValue({
            rule: ruleData
        });
    }

    parseSplitSymbol (value: any) {
        switch (value) {
            case 'blank':
                value = ' '
                break;
            case 'tab':
                value = '\t'
        }
        return value
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
                                }, {
                                    max: 20,
                                    message: '字典名称不得超过20个字符！'
                                }]
                            })(
                                <Input placeholder="请输入字典中文名称，20字以内" />
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
                                    max: 255,
                                    message: '描述不得超过255个字符！'
                                }]
                            })(
                                <Input type="textarea" placeholder="请输入字典描述信息，长度限制在255个字符以内" />
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
                                    <span className="text">{ruleFile ? ruleFile.name : '单击上传文件'}</span>
                                </label>
                                <input
                                    name="file"
                                    type="file"
                                    id="myFile"
                                    accept=".csv"
                                    onChange={this.handleFileChange}
                                    style={{ display: 'none' }}
                                />
                                <a className="download-btn">下载模版</a>
                                <Tooltip title={(<div>文件要求：<br />&nbsp;&nbsp;1. 仅支持标准的.csv格式文件；<br />&nbsp;&nbsp;2. 文件编码方式仅支持UTF-8；<br />&nbsp;&nbsp;3. 文件大小不超过5M；</div>)}>
                                    <Icon className="notice-icon" type="question-circle" />
                                </Tooltip>
                            </div>
                            <div className="dic-rule-limit-note">字典值数量最大限制为500</div>
                            {getFieldDecorator('rule', {
                                rules: [
                                    {
                                        required: true,
                                        message: '字典值必须大于一项！'
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
