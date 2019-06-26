import React from 'react';
import {
    Form, Input,
    Select, Radio
} from 'antd';
import { isEmpty, cloneDeep } from 'lodash';
import EngineSelect from '../../../components/engineSelect';
import ajax from '../../../api/dataManage'

import {
    formItemLayout,
    TABLE_MODEL_RULE, TABLE_TYPE
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
    constructor (props) {
        super(props);

        const { tableName, location } = this.props;
        const tableNamePropArr = tableName ? tableName.split('_') : [];

        this.state = {
            type: location ? '2' : '1', // 1: 内部表 2:外部表
            tableNameArr: cloneDeep(tableNamePropArr),
            storedType: props.storedType,
            tableType: props.tableType
        };
    }

    lifeCycleChange = (value) => {
        this.props.form.setFieldsValue({ 'lifeDay': value })
    }

    changeTableName = (value, index, modelType, option) => {
        const newArrs = [...this.state.tableNameArr];
        newArrs[index] = value;
        this.setState({ tableNameArr: newArrs });

        // 更新form字段值
        const fields = { tableName: newArrs.join('_') };
        if (modelType && modelType === TABLE_MODEL_RULE.LEVEL) {
            fields.grade = value;
            fields.lifeDay = option.props.data.lifeDay;
        } else if (modelType && modelType === TABLE_MODEL_RULE.SUBJECT) {
            fields.subject = value;
        }

        this.props.form.setFieldsValue(fields)
    }

    validateDelim (rule, value, callback) {
        value = value.trim();
        let error;
        if (value[0] === '\\') {
            if (value.length > 2) {
                error = '分隔符长度只能为1（不包括转义字符"\\"）'
                callback(error)
            }
        } else {
            if (value.length > 1) {
                error = '分隔符长度只能为1'
                callback(error)
            }
        }
        callback();
    }

    validateTableName (rule, value, callback) {
        const { tableNameArr } = this.state;
        const haveFilledTables = tableNameArr.filter((item) => {
            return !isEmpty(item);
        })

        if (haveFilledTables.length !== this.props.tableNameRules.length) {
            let error = '所有规则必须设置！'
            callback(error);
        } else {
            value ? ajax.checkTableExist({
                tableName: value
            }).then(res => {
                if (res.code === 1) {
                    // 如果true 则存在
                    if (res.data) {
                        let error = '该表已经存在！'
                        callback(error);
                    }
                }
            })
                .then(callback) : callback();
        }
        callback();
    }

    validateLoc (rule, value, callback) {
        value ? ajax.checkHdfsLocExist({
            hdfsUri: 'hdfs://' + value
        }).then(res => {
            if (res.code === 1) {
                if (!res.data) {
                    const error = '此目录不存在'
                    callback(error);
                }
            }
        })
            .then(callback) : callback();
    }

    handleChange (e) {
        const type = e.target.value;

        this.setState({ type });
        type === '1' && this.props.resetLoc();
    }

    renderTableRules = () => {
        const {
            subjectFields, modelLevels,
            incrementCounts, freshFrequencies, tableNameRules
        } = this.props;
        const { tableNameArr } = this.state;
        const inlineStyle = { width: 100, display: 'inline-block' }
        const renderRules = (rule, index) => {
            let data = [];
            switch (rule.value) {
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
                case TABLE_MODEL_RULE.CUSTOM:
                default: {
                    return (
                        <Input
                            placeholder="自定义"
                            value={tableNameArr[index]}
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
                    value={tableNameArr[index]}
                    onSelect={(value, option) => this.changeTableName(value, index, rule.value, option)}
                >
                    {
                        data && data.map(item =>
                            <Option
                                id={item.id}
                                key={item.id}
                                data={item}
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
        const style = {
            width: '100%',
            wordBreak: 'break-all',
            lineHeight: '18px',
            display: 'inline-block',
            marginTop: '5px'
        }

        return <div>
            {rules}
            <span style={style}> {tableNameArr.length > 0 && tableNameArr.join('_')} </span>
        </div>
    }

    render () {
        const { getFieldDecorator, getFieldValue } = this.props.form;

        const {
            tableDesc, delim, dataCatalogue,
            location, lifeDay, catalogueId, grade,
            subject, tableType
        } = this.props;

        const { type, tableNameArr, storedType } = this.state;
        const isShowDelim = storedType == 'textfile';
        const isHiveTable = getFieldValue('tableType') == TABLE_TYPE.HIVE; // hive表
        return <Form>
            <FormItem
                {...formItemLayout}
                label="表类型"
            >
                {getFieldDecorator('tableType', {
                    rules: [{
                        required: true,
                        message: '表类型不可为空！'
                    }],
                    initialValue: tableType
                })(
                    <EngineSelect
                        allowClear
                        placeholder="表类型"
                        tableTypes={this.props.projectTableTypes}
                        onChange={(value) => {
                            this.setState({
                                tableType: value
                            })
                        }}
                    />
                )}
            </FormItem>
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
                    initialValue: tableNameArr.join('_'),
                    validateTrigger: 'onBlur'
                })(
                    <Input type="hidden" />
                )}
                {this.renderTableRules()}
            </FormItem>
            <FormItem
                {...formItemLayout}
                label="类型"
                hasFeedback
            >
                <RadioGroup value={this.state.type}
                    onChange={this.handleChange.bind(this)}
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
                        message: '外部表地址不可为空！'
                    }, {
                        validator: this.validateLoc.bind(this)
                    }],
                    initialValue: location,
                    validateTrigger: 'onBlur'
                })(
                    <Input placeholder="外部表地址" />
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
                    initialValue: catalogueId || undefined
                })(
                    <CatalogueTree
                        isPicker
                        isFolderPicker
                        placeholder="请选择类目"
                        treeData={dataCatalogue}
                    />
                )}
            </FormItem>
            {
                isHiveTable && (
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
                )
            }
            <FormItem
                {...formItemLayout}
                label="存储格式"
                hasFeedback
            >
                {getFieldDecorator('storedType', {
                    rules: [{
                        required: true, message: '存储格式不可为空！'
                    }],
                    initialValue: storedType
                })(
                    <Select onChange={(value) => {
                        this.setState({
                            storedType: value
                        })
                    }}>
                        <Option value="textfile">textfile</Option>
                        <Option value="orc">orc</Option>
                        <Option value="parquet">parquet</Option>
                    </Select>
                )}
            </FormItem>
            {isShowDelim && (
                <FormItem
                    {...formItemLayout}
                    label="分隔符"
                    hasFeedback
                >
                    {getFieldDecorator('delim', {
                        rules: [],
                        initialValue: delim
                    })(
                        <Input placeholder="分隔符" autoComplete="off" />
                    )}
                </FormItem>
            )}
            <FormItem
                {...formItemLayout}
                label="描述"
            >
                {getFieldDecorator('tableDesc', {
                    rules: [{
                        max: 200,
                        message: '描述不得超过200个字符！'
                    }],
                    initialValue: tableDesc
                })(
                    <Input type="textarea" placeholder="描述信息" />
                )}
            </FormItem>
            <FormItem
                {...formItemLayout}
                label="主题域"
                style={{ display: 'none' }}
                hasFeedback
            >
                {getFieldDecorator('subject', {
                    rules: [],
                    initialValue: subject
                })(
                    <Input />
                )}
            </FormItem>
            <FormItem
                {...formItemLayout}
                label="模型层级"
                style={{ display: 'none' }}
                hasFeedback
            >
                {getFieldDecorator('grade', {
                    rules: [],
                    initialValue: grade
                })(
                    <Input />
                )}
            </FormItem>
        </Form>
    }
}
