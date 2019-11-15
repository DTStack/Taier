import * as React from 'react';
import { Form, Input, Tooltip, Icon, Upload, Card, Select, Button } from 'antd';
import { FormComponentProps } from 'antd/lib/form/Form';

import { IDataSource } from '../../../../model/dataSource';
import { formItemLayout, tailFormItemLayout } from '../../../../comm/const';

import Breadcrumb from '../../../../components/breadcrumb';

interface IProps extends FormComponentProps {
    dataSourceList: IDataSource[];
};

const FormItem = Form.Item;
const Option = Select.Option;

const breadcrumbNameMap = [{
    path: '/groupAnalyse',
    name: '群组管理'
}, {
    path: '/groupAnalyse/upload',
    name: '新增实体'
}];

class GroupUpload extends React.Component<IProps, any> {
    constructor (props: any) {
        super(props);
    }

    componentDidMount () {

    }

    handleSubmit = (e) => {
        e.preventDefault();
        this.props.form.validateFields((err, values) => {
            if (!err) {
                console.log('Received values of form: ', values);
            }
        });
    }

    onCancel = () => {
    }

    normFile = (e) => {
        console.log('Upload event:', e);
        if (Array.isArray(e)) {
            return e;
        }
        return e && e.fileList;
    }

    render () {
        const { form, dataSourceList } = this.props;
        const { getFieldDecorator } = form;
        return (
            <div>
                <Breadcrumb breadcrumbNameMap={breadcrumbNameMap} />
                <Card
                    noHovering
                    bordered={false}
                    className="noBorderBottom"
                >
                    <Form onSubmit={this.handleSubmit}>
                        <FormItem
                            {...formItemLayout}
                            label={(<span>
                                群组名称
                            </span>)}
                            hasFeedback
                        >
                            {getFieldDecorator('name', {
                                rules: [{
                                    required: true, message: '请输入群组名称!'
                                }, {
                                    pattern: /^[\u4e00-\u9fa5]+$/,
                                    message: '群组名称仅支持中文字符!'
                                }, {
                                    max: 20,
                                    message: '群组名称20字以内的中文字符!'
                                }]
                            })(
                                <Input placeholder="请输入群组中文名称，20字以内的中文字符" />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label={(<span>
                                群组描述
                            </span>)}
                            hasFeedback
                        >
                            {getFieldDecorator('description', {
                                rules: [{
                                    max: 20,
                                    message: '群组名称20字以内的中文字符!'
                                }]
                            })(
                                <Input.TextArea placeholder="请输入群组描述信息，长度限制在20个字符以内" />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="选择匹配维度"
                            hasFeedback
                        >
                            {getFieldDecorator('dimension', {
                                rules: [{
                                    required: true, message: '请选择匹配维度!'
                                }]
                            })(
                                <Select
                                    mode="multiple"
                                    placeholder="请选择匹配维度"
                                    style={{ width: 200 }}
                                >
                                    {dataSourceList && dataSourceList.map((o: IDataSource) => {
                                        return <Option key={o.name} value={o.value}>{o.name}</Option>
                                    })}
                                </Select>
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="上传文件"
                            hasFeedback
                        >
                            <div>
                                <a>生成模板并下载
                                    <Tooltip title="用户选择的实体维度信息生成模板； 选中的匹配维度将作为映射的内容提供下载模版，表头为实体设置映射的属性名称（中文）">
                                        <Icon type="o-question" />
                                    </Tooltip>
                                </a>
                            </div>
                            <div className="dropbox" style={{ height: 200 }}>
                                {getFieldDecorator('dragger', {
                                    valuePropName: 'file',
                                    getValueFromEvent: this.normFile
                                })(
                                    <Upload.Dragger name="files" action="/upload.do">
                                        <p className="ant-upload-drag-icon">
                                            <Icon type="inbox" />
                                        </p>
                                        <p className="ant-upload-text">点击或将文件拖拽到此处上传</p>
                                        <p className="ant-upload-hint">仅支持xlsx，文件大小≤10M</p>
                                    </Upload.Dragger>
                                )}
                            </div>
                        </FormItem>
                        <FormItem
                            {...tailFormItemLayout}
                        >
                            <Button type="primary" htmlType="submit">立即创建</Button>
                            <Button style={{ marginLeft: 20 }} onClick={this.onCancel}>取消</Button>
                        </FormItem>
                    </Form>
                </Card>
            </div>

        )
    }
}

const GroupUploadForm = Form.create()(GroupUpload);

export default GroupUploadForm;
