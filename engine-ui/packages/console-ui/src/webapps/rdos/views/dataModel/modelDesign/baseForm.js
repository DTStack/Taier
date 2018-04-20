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

import CatalogueTree from '../../dataManage/catalogTree';
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
            tableNameArr: [],
        };
    }

    lifeCycleChange = (value) => {
        this.props.form.setFieldsValue({'lifeDay': value})
    }

    changeTableName = (value, index, modelType) => {
        const newArrs = [...this.state.tableNameArr];
        newArrs[index] = value;
        this.setState({ tableNameArr: newArrs });
        const fields = { tableName: newArrs.join('_') };
        if (modelType && modelType === TABLE_MODEL_RULE.LEVEL) {
            fields.subject = value;
        } else if (modelType && modelType === TABLE_MODEL_RULE.SUBJECT) {
            fields.grade = value;
        }
        this.props.form.setFieldsValue(fields)
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

        const valArr = value.split('_');
        if (valArr.length !== this.props.tableNameRules.length) {
            callback('请按要求设置表名！');
        } else {
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
        callback();
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

    renderTableRules = () => {

        const { 
            subjectFields, modelLevels, changeRuleValue,
            incrementCounts, freshFrequencies, tableNameRules,
        } = this.props;

        const { tableNameArr } = this.state;

        const inlineStyle = { width: 100, display: 'inline-block' }

        const renderRules = (rule, index) => {

            let data = [];
            const defaultVal = tableNameArr[index];

            switch(rule.value) {
                case TABLE_MODEL_RULE.LEVEL: {
                    data = modelLevels; break;
                }
                case TABLE_MODEL_RULE.SUBJECT: {
                    data = subjectFields; break;
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
                            placeholder="自定义"
                            onChange={(e) => this.changeTableName(e.target.value, index)}
                            style={inlineStyle} 
                        />
                    )
                }
            }

            return (
                <Select
                    placeholder="请选择"
                    style={inlineStyle}
                    onSelect={(value, option) => this.changeTableName(value, index, rule.value)}
                >
                    {
                        data && data.map(item =>
                            <Option
                                id={item.id}
                                title={item.prefix}
                                value={item.prefix}
                            >
                                {item.prefix}
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
                width: '100px',
                marginRight: '5px'
            }}>
                <section style={inlineStyle}>{rule.name}:</section>
                {renderRules(rule, index)}
            </span>
        ))

        return <div>
            {rules}
            <span> {tableNameArr.length > 0 && tableNameArr.join('_')} </span>
        </div>
    }

    render() {
        const { getFieldDecorator } = this.props.form;

        const { 
            tableName, desc, delim, dataCatalogue,
            location, lifeDay, catalogueId, grade,
            subject,
        } = this.props;

        const { type } = this.state;

        return <Form>
            <FormItem
                {...formItemLayout}
                label="表名"
            >
                 {getFieldDecorator('tableName', {
                    rules: [{
                        required: true,
                        message: '表名不可为空！'
                    }, {
                        validator: this.validateTableName.bind(this)
                    }],
                    validateTrigger: 'onBlur',
                })(
                    <Input type="hidden" />,
                )}
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
                    validateTrigger: 'onBlur',
                })(
                    <Input placeholder="外部表地址"/>,
                )}
            </FormItem>}
            <FormItem
                {...formItemLayout}
                label="所属类目"
            >
                {getFieldDecorator('catalogueId', {
                    rules: [{
                        required: true,
                        message: '表所在类目不可为空！'
                    }],
                    initialValue: catalogueId || undefined,
                })(
                    <CatalogueTree
                        isPicker
                        isFolderPicker
                        placeholder="请选择类目"
                        treeData={dataCatalogue}
                    />
                )}
            </FormItem>
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
            <FormItem
                {...formItemLayout}
                label="主题域"
                style={{display: 'none'}}
                hasFeedback
            >
                {getFieldDecorator('subject', {
                    rules: [],
                    initialValue: subject,
                })(
                    <Input />
                )}
            </FormItem>
            <FormItem
                {...formItemLayout}
                label="模型层级"
                style={{display: 'none'}}
                hasFeedback
            >
                {getFieldDecorator('grade', {
                    rules: [],
                    initialValue: grade,
                })(
                    <Input />
                )}
            </FormItem>
        </Form>
    }
}
