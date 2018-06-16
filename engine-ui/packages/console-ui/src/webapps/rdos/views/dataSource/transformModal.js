import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Modal, Row, Col, Form, Input, Select, Button, Icon } from 'antd';

import { originTypeTransformRule, targetTypeTransformRule } from '../../comm/const';
import Api from '../../api';

const FormItem = Form.Item;
const Option = Select.Option;

const formItemLayout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16 }
};

const formItemLayoutWithOutLabel = {
    labelCol: { span: 0 },
    wrapperCol: { span: 24 }
};

export default class TransformModal extends Component {

    state = {
    	itemId: 0,
        initialRule: {},
        nameRule: [{id: 1}],
        columnRule: [{id: 1}],
        typeRule: [{id: 1}]
    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.visible && !this.props.visible && nextProps.transformFields.length) {
            let nameRule = [...this.state.nameRule],
                columnRule = [...this.state.columnRule],
                typeRule = [...this.state.typeRule];

            nextProps.transformFields.forEach((item) => {
                switch (item.convertObject) {
                    case 1:
                        nameRule.filter(rule => rule.id == item.id)[0].left = item.convertSrc;
                        nameRule.filter(rule => rule.id == item.id)[0].right = item.convertDest;
                        break;
                    case 2:
                        columnRule.filter(rule => rule.id == item.id)[0].left = item.convertSrc;
                        columnRule.filter(rule => rule.id == item.id)[0].right = item.convertDest;
                        break;
                    case 3:
                        typeRule.filter(rule => rule.id == item.id)[0].left = item.convertSrc;
                        typeRule.filter(rule => rule.id == item.id)[0].right = item.convertDest;
                        break;
                    default:
                        break;
                }

            });
            console.log(nameRule,columnRule,typeRule)

            this.setState({
                nameRule: nameRule.length > 1 ? nameRule.filter(item => item.left && item.right) : nameRule,
                columnRule: columnRule.length > 1 ? columnRule.filter(item => item.left && item.right) : columnRule,
                typeRule: typeRule.length > 1 ? typeRule.filter(item => item.left && item.right) : typeRule
            });

        }
    }

    add = (type, id) => {
        let data = [...this.state[type]],
            ids  = data.map(item => item.id),
            newId = Math.max(...ids) + 1,
            target = data.filter(item => item.id == id)[0],
            index = data.indexOf(target) + 1,
            newRule = {};
        
        
        data.splice(index, 0, { id: newId });

        newRule[type] = data;

        this.setState(newRule);
    }

    remove = (type, id) => {
        let data = [...this.state[type]],
            target = data.filter(item => item.id == id)[0],
            newRule = {};

        data.splice(data.indexOf(target), 1);
        newRule[type] = data;

        this.setState(newRule);
    }

    getRuleId = (str) => {
        return parseInt(str.replace(/[^0-9]/ig, ''));
    }

    saveConfig = () => {
        const { form } = this.props;
        const { nameRule, columnRule, typeRule } = this.state;

        form.validateFields((err, values) => {
            console.log(err,values)

            if(!err) {
                let left = [],
                    fields = [];

                for (let [key, value] of Object.entries(values)) {
                    if (value && key.indexOf('left') > 0) {
                        left.push(key);
                    }
                }
                console.log(left)

                fields = left.map(key => {
                    let type,
                        rightKey = key.replace(/left/, 'right');

                    if (key.indexOf('nameRule') > -1) {
                        type = 1;
                    } else if (key.indexOf('columnRule') > -1) {
                        type = 2;
                    } else {
                        type = 3;
                    }

                    return {
                        convertSrc: values[key],
                        convertDest: values[rightKey],
                        convertObject: type,
                        id: this.getRuleId(key)
                    }
                });

                if (fields.length) {

                    Api.checkSyncConfig({
                        transformFields: fields
                    }).then(res => {
                        if (res.code === 1) {
                            this.props.closeModal();
                            this.props.changeTransformFields(fields);
                        }
                    });
                } else {
                    this.cancel();
                }

            }
        });
    }


    cancel = () => {
        this.props.form.resetFields();
        this.props.closeModal();
    }

    handleInput = (target, e) => {
        const { form } = this.props;

        if (!e.target.value && !form.getFieldValue(target)) {
            form.resetFields([target]);
        }
    }

    renderFormItem = (data, type) => {
        const { form, transformFields } = this.props;
        const { getFieldDecorator } = form;
        console.log(data,type)

        return data.map((item, index) => {
            if (index === 0) {
                let label;

                switch (type) {
                    case 'nameRule':
                        label = '表名转换规则';
                        break;
                    case 'columnRule':
                        label = '字段名转换规则';
                        break;
                    case 'typeRule':
                        label = '字段类型转换规则';
                        break;
                    default:
                        break;
                }

                return <Row className="flex-center m-v-10" key={`${type}-${item.id}`}>
                    <FormItem 
                        {...formItemLayout} 
                        label={label}
                        style={{ flexBasis: '40%' }}>
                        {
                            getFieldDecorator(`${type}-${item.id}-left`, {
                                rules: [{
                                    required: form.getFieldValue(`${type}-${item.id}-right`),
                                    message: '需要填充相应规则'
                                }], 
                                initialValue: item.left
                            })(
                                type === 'typeRule' ?
                                <Select 
                                    size="large">
                                    {
                                        originTypeTransformRule.map(item => {
                                            return <Option key={item}>{item}</Option>
                                        })
                                    }
                                </Select>
                                :
                                <Input 
                                    onChange={this.handleInput.bind(this, `${type}-${item.id}-right`)} 
                                />
                            )
                        }
                    </FormItem>

                    <div className="txt-center font-16" style={{ flexBasis: '5%' }}>
                        <Icon type="right" />
                    </div>

                    <FormItem 
                        style={{ flexBasis: '40%' }} 
                        {...formItemLayoutWithOutLabel}>
                        <div className="flex flex-v-center">
                            {
                                getFieldDecorator(`${type}-${item.id}-right`, {
                                    rules: [{
                                        required: form.getFieldValue(`${type}-${item.id}-left`),
                                        message: '需要填充相应规则'
                                    }],
                                    initialValue: item.right
                                })(
                                    type === 'typeRule' ?
                                    <Select 
                                        size="large" 
                                        className="col-16">
                                        {
                                            targetTypeTransformRule.map(item => {
                                                return <Option key={item}>{item}</Option>
                                            })
                                        }
                                    </Select>
                                    :
                                    <Input 
                                        size="large" 
                                        className="col-16" 
                                        onChange={this.handleInput.bind(this, `${type}-${item.id}-left`)}
                                    />
                                )
                            }
                            <Icon 
                                type="plus-circle-o" 
                                className="edit-icon m-l-8" 
                                onClick={this.add.bind(this, type, item.id)}
                            />
                        </div>
                    </FormItem>
                </Row>
            } else {
                return <Row className="flex-center m-v-10" key={`${type}-${item.id}`}>
                    <FormItem 
                        {...formItemLayoutWithOutLabel}     
                        className="left-item"
                        style={{ flexBasis: '40%' }}>
                        <div className="flex flex-right">
                            {
                                getFieldDecorator(`${type}-${item.id}-left`, {
                                    rules: [{
                                        required: form.getFieldValue(`${type}-${item.id}-right`),
                                        message: '需要填充相应规则'
                                    }], 
                                    initialValue: item.left
                                })(
                                    type === 'typeRule' ?
                                    <Select 
                                        size="large" 
                                        className="col-16">
                                        {
                                            originTypeTransformRule.map(item => {
                                                return <Option key={item}>{item}</Option>
                                            })
                                        }
                                    </Select>
                                    :
                                    <Input 
                                        size="large" 
                                        className="col-16" 
                                        onChange={this.handleInput.bind(this, `${type}-${item.id}-right`)}
                                    />
                                )
                            }
                        </div>
                    </FormItem>

                    <div className="txt-center font-16" style={{ flexBasis: '5%' }}>
                        <Icon type="right" />
                    </div>

                    <FormItem 
                        style={{ flexBasis: '40%' }} 
                        {...formItemLayoutWithOutLabel}>
                        <div className="flex flex-v-center">
                            {
                                getFieldDecorator(`${type}-${item.id}-right`, {
                                    rules: [{
                                        required: form.getFieldValue(`${type}-${item.id}-left`),
                                        message: '需要填充相应规则'
                                    }], 
                                    initialValue: item.right
                                })(
                                    type === 'typeRule' ?
                                    <Select 
                                        size="large" 
                                        className="col-16">
                                        {
                                            targetTypeTransformRule.map(item => {
                                                return <Option key={item}>{item}</Option>
                                            })
                                        }
                                    </Select>
                                    :
                                    <Input 
                                        size="large" 
                                        className="col-16" 
                                        onChange={this.handleInput.bind(this, `${type}-${item.id}-left`)}
                                    />
                                )
                            }
                            <Icon 
                                type="plus-circle-o" 
                                className="edit-icon m-l-8" 
                                onClick={this.add.bind(this, type, item.id)}
                            />
                            <Icon 
                                type="delete" 
                                className="edit-icon m-l-8" 
                                onClick={this.remove.bind(this, type, item.id)}
                            />
                        </div>
                    </FormItem>
                </Row>
            }
        })
    }

    render() {
    	const { form, visible } = this.props;
        const { getFieldDecorator } = form;
        const { nameRule, columnRule, typeRule } = this.state;

    	return (
            <Modal
                title="高级设置"
                width={'70%'}
                visible={visible}
                maskClosable={false}
                okText="保存"
                cancelText="取消"
                onOk={this.saveConfig}
                onCancel={this.cancel}
            >
                <Form layout="inline" className="config-form">
                    {
                        this.renderFormItem(nameRule, 'nameRule')
                    }
                    {
                        this.renderFormItem(columnRule, 'columnRule')
                    }
                    {
                        this.renderFormItem(typeRule, 'typeRule')
                    }
                </Form>
            </Modal>
        )
    }
}
TransformModal = Form.create()(TransformModal);