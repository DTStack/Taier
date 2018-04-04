import React from 'react';
import { Button, message, Form, Input,
    Row, Col, Icon, Select, Radio, Tooltip, InputNumber } from 'antd';
import assign from 'object-assign';
import { isEqual, throttle, range, isObject } from 'lodash';

import ajax from '../../../api';
import { 
    formItemLayout, 
    tableModelRules,
    TABLE_MODEL_RULE,
} from '../../../comm/const';
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
            tableNameRules: tableModelRules // 测试数据
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

    changeRuleValue = (value, index) => {
        const newArrs = [...this.state.tableNameRules];
        newArrs[index].field = value;
        console.log('arguments:', newArrs[index], value, index)
        this.setState({
            tableNameRules: newArrs
        });
    }

    renderTableRules = () => {

        const { tableNameRules } = this.state;
        const { 
            themeFields, modelLevels, 
            incrementCounts, freshFrequencies 
        } = this.props;

        const inlineStyle = { width: 80, display: 'inline-block' }

        const renderRules = (type, index) => {

            let data = []
            switch(type) {
                case TABLE_MODEL_RULE.LEVEL: {
                    data = modelLevels; break;
                }
                case TABLE_MODEL_RULE.THEME: {
                    data = themeFields; break;
                }
                case TABLE_MODEL_RULE.INCREMENT: {
                    data = incrementCounts; break;
                }
                case TABLE_MODEL_RULE.FREQUENCY: {
                    data = freshFrequencies; break;
                }
                default:
                case TABLE_MODEL_RULE.CUSTOM: {
                    return (
                        <Input 
                            onChange={(e) => this.changeRuleValue(e.target.value, index)}
                            style={inlineStyle} 
                        />
                    )
                }
            }

            return (
                <Select
                    style={inlineStyle}
                    onSelect={(value, option) => this.changeRuleValue(value, index)}
                >
                    {
                        data && data.map(item => 
                            <Option
                                id={item.id}
                                value={item.name}
                            >
                                {item.name}
                            </Option>
                        )
                    }
                </Select>
            )
        }

        const rules = tableNameRules && tableNameRules.map((rule, index) => (
            <span key={rule.value} style={{
                height: '60px',
                display: 'inline-block',
                width: '80px',
                marginRight: '5px'
            }}>
                <section style={inlineStyle}>{rule.text}:</section>
                {renderRules(rule.value, index)}
            </span>
        ))

        const tableName = tableNameRules.map(rule => rule.field)

        return <div>
            {rules}
            <span> {tableName.join('_')} </span>
        </div>
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        
        const { 
            tableName, desc, delim, 
            location, lifeDay, catalogueId 
        } = this.props;

        const { type } = this.state;

        return <Form>
            <FormItem
                {...formItemLayout}
                label="表名"
                hasFeedback
            >
                {this.renderTableRules()}
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
