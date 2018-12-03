import React from 'react';

import {
    Row, Col, Input,
    Form
} from 'antd'

const FormItem = Form.Item;

export class CustomParams extends React.Component {
    renderCustomParams () {
        const { customParams, formItemLayout, getFieldDecorator, onChange } = this.props;
        return customParams.map((customParam) => {
            return (
                <Row key={customParam.id}>
                    <Col span={formItemLayout.labelCol.sm.span}>
                        <FormItem key={customParam.id + '-key'}>
                            {getFieldDecorator(customParam.id + '-key', {
                                rules: [{
                                    required: true,
                                    message: '请输入参数名'
                                }]
                            })(
                                <Input onChange={(e) => {
                                    onChange('key', customParam.id, e.target.value);
                                }} style={{ width: 'calc(100% - 12px)' }} />
                            )}
                            :
                        </FormItem>
                    </Col>
                    <Col span={formItemLayout.wrapperCol.sm.span}>
                        <FormItem key={customParam.id + '-value'}>
                            {getFieldDecorator(customParam.id + '-value', {
                                rules: [{
                                    required: true,
                                    message: '请输入参数值'
                                }]
                            })(
                                <Input onChange={(e) => {
                                    onChange('value', customParam.id, e.target.value);
                                }} style={{ width: 'calc(100% - 30px)' }} />
                            )}
                            <a style={{ float: 'right' }} onClick={this.deleteCustomParam.bind(this, customParam.id)}>删除</a>
                        </FormItem>
                    </Col>
                </Row>
            )
        })
    }
    deleteCustomParam (id) {
        this.props.onChange('deleteCustomParam', id);
    }
    addCustomParams () {
        this.props.onChange('newCustomParam');
    }
    render () {
        const { formItemLayout } = this.props;

        return (
            <div>
                {this.renderCustomParams()}
                <Row>
                    <Col offset={formItemLayout.labelCol.sm.span} span={formItemLayout.wrapperCol.sm.span}>
                        <a onClick={this.addCustomParams.bind(this)}>添加自定义参数</a>
                    </Col>
                </Row>
            </div>
        )
    }
}

export * from './customParamsUtil';
