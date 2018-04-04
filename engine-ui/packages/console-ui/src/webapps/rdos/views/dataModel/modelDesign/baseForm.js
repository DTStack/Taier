import React from 'react';
import { connect } from 'react-redux';
import { Button, message, Form, Input,
    Row, Col, Icon, Select, Radio, Tooltip, InputNumber } from 'antd';
import assign from 'object-assign';
import { isEqual, throttle, range, isObject } from 'lodash';

import ajax from '../../../api';
import { formItemLayout } from '../../../comm/const';
import LifeCycle from '../../dataManage/lifeCycle';

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;

/**
 * @description step1:基本信息
 * @class BaseForm
 * @extends {React.Component}
 */
export default class BaseForm extends React.Component {
    
    constructor(props) {
        super(props);

        this.state = {
            type: '1', // 1: 内部表 2:外部表
        };
    }

    lifeCycleChange = (value) => {
        this.props.form.setFieldsValue({'lifeDay': value})
    }

    validateDelim(rule, value, callback) {
        value = value.trim();

        if(value[0] === '\\') {
            if(value.length > 2) {
                callback('分隔符长度只能为1（不包括转义字符"\\"）')
            }
        }
        else {
            if(value.length > 1) {
                callback('分隔符长度只能为1')
            }
        }
        callback();
    }

    validateTableName(rule, value, callback) {
        const ctx = this;
        value ? ajax.checkTableExist({
            tableName: value
        }).then(res => {
            if(res.code === 1) {
                // 转换为小写
                ctx.props.form.setFieldsValue({ tableName: value.toLowerCase() })
                if(res.data) callback('该表已经存在！');
            }
        })
        .then(callback) : callback();
    }

    validateLoc(rule, value, callback) {
        value ? ajax.checkHdfsLocExist({
            hdfsUri: 'hdfs://' + value
        }).then(res => {
            if(res.code === 1) {
                if(!res.data) callback('此目录不存在');
            }
        })
        .then(callback) : callback();
    }

    handleChange(e) {
        const type = e.target.value;

        this.setState({type});
        type === '1' && this.props.resetLoc();
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        const { tableName, desc, delim, location, lifeDay, catalogueId } = this.props;
        const { type } = this.state;

        return <Form>
            <FormItem
                {...formItemLayout}
                label="表名"
                hasFeedback
            >
                {getFieldDecorator('tableName', {
                    rules: [{
                        required: true, message: '表名不可为空！',
                    }, {
                        pattern: /^([A-Za-z0-9_]{1,64})$/,
                        message: '表名称只能的字母、数字、下划线组成，且长度不超过64个字符!',
                    }, {
                        validator: this.validateTableName.bind(this)
                    }],
                    validateTrigger: 'onBlur',
                    initialValue: tableName
                })(
                    <Input placeholder="请输入表名" autoComplete="off" />,
                )}
            </FormItem>
            <FormItem
                {...formItemLayout}
                label="分隔符"
                hasFeedback
            >
                {getFieldDecorator('delim', {
                    rules: [],
                    initialValue: delim,
                })(
                    <Input placeholder="分隔符" autoComplete="off" />
                )}
            </FormItem>
            <FormItem
                {...formItemLayout}
                label="类型"
                hasFeedback
            >
                <RadioGroup value={ this.state.type }
                    onChange={ this.handleChange.bind(this) }
                >
                    <Radio value={'1'}>内部表</Radio>
                    <Radio value={'2'}>外部表</Radio>
                </RadioGroup>
            </FormItem>
            {type == 2 && <FormItem
                {...formItemLayout}
                label="外部表地址"
                hasFeedback
            >
                {getFieldDecorator('location', {
                    rules: [{
                        required: true,
                        message: '外部表地址不可为空！',
                    }, {
                        validator: this.validateLoc.bind(this)
                    }],
                    initialValue: location,
                    validateTrigger: 'onBlur'
                })(
                    <Input placeholder="外部表地址"/>
                )}
            </FormItem>}
            <FormItem
                {...formItemLayout}
                label="生命周期"
            >
                {getFieldDecorator('lifeDay', {
                    rules: [{
                        required: true,
                        message: '生命周期不可为空！'
                    }],
                    initialValue: lifeDay || 90
                })(
                    <LifeCycle
                        onChange={this.lifeCycleChange} 
                    />
                )}
            </FormItem>
            <FormItem
                {...formItemLayout}
                label="存储格式"
                hasFeedback
            >
                {getFieldDecorator('storedType', {
                    rules: [{
                        required: true, message: '存储格式不可为空！',
                    }],
                    initialValue: 'textfile',
                })(
                    <Select>
                        <Option value="textfile">textfile</Option>
                        <Option value="orc">orc</Option>
                    </Select>
                )}
            </FormItem>
            <FormItem
                {...formItemLayout}
                label="描述"
            >
                {getFieldDecorator('desc', {
                    rules: [{
                        max: 200,
                        message: '描述不得超过200个字符！',
                    }],
                    initialValue: desc
                })(
                    <Input type="textarea" placeholder="描述信息" />
                )}
            </FormItem>
        </Form>
    }

}
