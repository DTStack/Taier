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
        nameRule: [{ id: 1 }],
        columnRule: [{ id: 1 }],
        typeRule: [{ id: 1 }]
    }

    componentWillReceiveProps (nextProps) {
        if (nextProps.visible && !this.props.visible) {
            let nameRule = [...this.state.nameRule];

            let columnRule = [...this.state.columnRule];

            let typeRule = [...this.state.typeRule];

            if (nextProps.transformFields.length) {
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

                // 去除没有数据的空行
                this.setState({
                    nameRule: nameRule.length > 1 ? nameRule.filter(item => item.left && item.right) : nameRule,
                    columnRule: columnRule.length > 1 ? columnRule.filter(item => item.left && item.right) : columnRule,
                    typeRule: typeRule.length > 1 ? typeRule.filter(item => item.left && item.right) : typeRule
                });
            } else {
                this.setState({
                    nameRule: [{ id: 1 }],
                    columnRule: [{ id: 1 }],
                    typeRule: [{ id: 1 }]
                });
            }
        }
    }

    // 增加设置
    add = (type, id) => {
        let data = [...this.state[type]];

        let ids = data.map(item => item.id);

        let newId = Math.max(...ids) + 1;

        let target = data.filter(item => item.id == id)[0];

        let index = data.indexOf(target) + 1;

        let newRule = {};

        data.splice(index, 0, { id: newId });
        newRule[type] = data;

        this.setState(newRule);
    }

    // 移除
    remove = (type, id) => {
        let data = [...this.state[type]];

        let target = data.filter(item => item.id == id)[0];

        let newRule = {};

        data.splice(data.indexOf(target), 1);
        newRule[type] = data;

        this.setState(newRule);
    }

    // 取得id
    getRuleId = (str) => {
        return parseInt(str.replace(/[^0-9]/ig, ''));
    }

    // 保存配置，先检查，通过了再保存
    saveConfig = () => {
        const { form } = this.props;

        form.validateFields((err, values) => {
            // console.log(err,values)

            if (!err) {
                let leftKeys = [];

                let fields = [];

                // 拿到所有左侧的值
                for (let [key, value] of Object.entries(values)) {
                    if (value && key.indexOf('left') > 0) {
                        leftKeys.push(key);
                    }
                }

                fields = leftKeys.map(key => {
                    let type;

                    let rightKey = key.replace(/left/, 'right');

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
                        }
                    });
                } else {
                    this.cancel();
                }

                this.props.changeTransformFields(fields);
            }
        });
    }

    // 取消操作
    cancel = () => {
        this.props.form.resetFields();
        this.props.closeModal();
    }

    // 输入为空时重置对应item
    handleInput = (target, e) => {
        const { form } = this.props;

        if (!e.target.value && !form.getFieldValue(target)) {
            let data = {};

            data[target] = undefined;
            form.setFieldsValue(data);
        }
    }

    // 选择为空时重置对应item
    handleSelect = (target, value) => {
        console.log(target, value)
        const { form } = this.props;

        if (!value && !form.getFieldValue(target)) {
            let data = {};

            data[target] = undefined;
            form.setFieldsValue(data);
        }
    }

    renderFormItem = (data, type) => {
        const { form } = this.props;
        const { getFieldDecorator } = form;

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
                        label={label}
                        {...formItemLayout}
                        style={{ flexBasis: '40%' }}
                        className="cell-center"
                    >
                        {
                            getFieldDecorator(`${type}-${item.id}-left`, {
                                rules: [{
                                    required: form.getFieldValue(`${type}-${item.id}-right`),
                                    message: '需要填充相应规则'
                                }],
                                initialValue: item.left
                            })(
                                type === 'typeRule'
                                    ? <Select
                                        allowClear
                                        size="large"
                                        onChange={this.handleSelect.bind(this, `${type}-${item.id}-right`)}>
                                        {
                                            originTypeTransformRule.map(item => {
                                                return <Option key={item}>{item}</Option>
                                            })
                                        }
                                    </Select>
                                    : <Input
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
                        {...formItemLayoutWithOutLabel}
                        className="cell-center"
                    >
                        <div className="flex flex-v-center">
                            {
                                getFieldDecorator(`${type}-${item.id}-right`, {
                                    rules: [{
                                        required: form.getFieldValue(`${type}-${item.id}-left`),
                                        message: '需要填充相应规则'
                                    }],
                                    initialValue: item.right
                                })(
                                    type === 'typeRule'
                                        ? <Select
                                            allowClear
                                            size="large"
                                            className="col-16"
                                            onChange={this.handleSelect.bind(this, `${type}-${item.id}-left`)}>
                                            {
                                                targetTypeTransformRule.map(item => {
                                                    return <Option key={item}>{item}</Option>
                                                })
                                            }
                                        </Select>
                                        : <Input
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
                        className="left-item cell-center"
                        {...formItemLayoutWithOutLabel}
                        style={{ flexBasis: '40%' }}
                    >
                        <div className="flex flex-right">
                            {
                                getFieldDecorator(`${type}-${item.id}-left`, {
                                    rules: [{
                                        required: form.getFieldValue(`${type}-${item.id}-right`),
                                        message: '需要填充相应规则'
                                    }],
                                    initialValue: item.left
                                })(
                                    type === 'typeRule'
                                        ? <Select
                                            allowClear
                                            size="large"
                                            className="col-16"
                                            onChange={this.handleSelect.bind(this, `${type}-${item.id}-right`)}>
                                            {
                                                originTypeTransformRule.map(item => {
                                                    return <Option key={item}>{item}</Option>
                                                })
                                            }
                                        </Select>
                                        : <Input
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
                        {...formItemLayoutWithOutLabel}
                        className="cell-center"
                    >
                        <div className="flex flex-v-center">
                            {
                                getFieldDecorator(`${type}-${item.id}-right`, {
                                    rules: [{
                                        required: form.getFieldValue(`${type}-${item.id}-left`),
                                        message: '需要填充相应规则'
                                    }],
                                    initialValue: item.right
                                })(
                                    type === 'typeRule'
                                        ? <Select
                                            size="large"
                                            allowClear
                                            className="col-16"
                                            onChange={this.handleSelect.bind(this, `${type}-${item.id}-left`)}>
                                            {
                                                targetTypeTransformRule.map(item => {
                                                    return <Option key={item}>{item}</Option>
                                                })
                                            }
                                        </Select>
                                        : <Input
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

    // 清除所有设置
    clearData = () => {
        this.setState((prevState) => {
            this.props.form.resetFields();

            return {
                nameRule: [{ id: 1 }],
                columnRule: [{ id: 1 }],
                typeRule: [{ id: 1 }]
            }
        });
    }

    render () {
    	const { visible } = this.props;
        const { nameRule, columnRule, typeRule } = this.state;

    	return (
            <Modal
                title="高级设置"
                width={'70%'}
                visible={visible}
                maskClosable={false}
                onCancel={this.cancel}
                footer={[
                    <Button
                        key="clear"
                        type="primary"
                        className="left"
                        onClick={this.clearData}>
                        清除设置
                    </Button>,
                    <Button
                        key="cancel"
                        onClick={this.cancel}>
                        取消
                    </Button>,
                    <Button
                        key="save"
                        type="primary"
                        onClick={this.saveConfig}>
                        保存
                    </Button>
                ]}
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
