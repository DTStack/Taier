import * as React from 'react';
import { connect } from 'react-redux';
import { get } from 'lodash';

import { Form, Select, Input, Spin, Popconfirm } from 'antd';

import {
    operatorSelect,
    operatorSelect1,
    operatorForEnum,
    STATISTICS_FUNC
} from '../../consts';
import HelpDoc from '../helpDoc';

const FormItem = Form.Item;
const Option = Select.Option;

@(connect((state: any) => {
    return {
        verifyTypeList: get(state, 'common.allDict.verifyType', [])
    }
}) as any)
class RuleForm extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            loading: false
        }
    }
    renderFunctionId () {
        const { isEdit, data, functionList } = this.props;
        const { getFieldDecorator } = this.props.form;
        return <FormItem label="统计函数">
            {getFieldDecorator('functionId', {
                rules: [{
                    required: true,
                    message: '请选择统计函数'
                }],
                initialValue: get(data, 'functionId')
            })(
                <Select
                    disabled={!isEdit}
                    style={{ width: '150px' }}
                >
                    {functionList.map((item: any) => {
                        return (
                            <Option
                                key={item.id}
                                value={item.id}
                            >
                                {item.nameZc}
                            </Option>
                        );
                    })}
                </Select>
            )}
        </FormItem>
    }
    renderFilter () {
        const { isEdit, data } = this.props;
        const { getFieldDecorator } = this.props.form;
        return <FormItem className='c-rule__item--single' label="过滤条件" >
            {getFieldDecorator('filter', {
                initialValue: get(data, 'filter')
            })(
                <Input.TextArea disabled={!isEdit} style={{ width: '100%', resize: 'vertical' }} autosize={{ minRows: 2, maxRows: 4 }} />
            )}
        </FormItem>
    }
    renderVerifyType () {
        const { isEdit, data, verifyTypeList = [] } = this.props;
        const { getFieldDecorator } = this.props.form;
        return <FormItem label="校验方法">
            {getFieldDecorator('verifyType', {
                rules: [{
                    required: true,
                    message: '请选择校验方法'
                }],
                initialValue: get(data, 'verifyType')
            })(
                <Select disabled={!isEdit} style={{ width: '150px' }}>
                    {verifyTypeList.map((item: any) => {
                        return (
                            <Option
                                key={item.value}
                                value={item.value}
                            >
                                {item.name}
                            </Option>
                        )
                    })}
                </Select>
            )}
        </FormItem>
    }
    renderOperator (isTypeCheck?: any) {
        const { isEdit, data } = this.props;
        const { getFieldDecorator } = this.props.form;
        const isStringLength = (functionId: any) => {
            return functionId === STATISTICS_FUNC.STRING_MAX_LEN || functionId === STATISTICS_FUNC.STRING_MIN_LEN;
        };

        let operatorMap = operatorSelect;
        if (isStringLength(data.functionId)) {
            operatorMap = operatorSelect1;
        } else if (data.functionId === STATISTICS_FUNC.ENUM) {
            operatorMap = operatorForEnum;
        }

        return <React.Fragment>
            {isTypeCheck ? (
                <FormItem label="期望值">
                    {getFieldDecorator('verifyType', {
                        rules: [{
                            required: true,
                            message: '请选择计算类型'
                        }],
                        initialValue: get(data, 'verifyType')
                    })(
                        <Select disabled={!isEdit} style={{ width: '80px' }}>
                            <Option key='1' value={1} >固定值</Option>
                            <Option key='7' value={7} >占比</Option>
                        </Select>
                    )}
                </FormItem>
            ) : null}
            <FormItem label={isTypeCheck ? null : '期望值'}>
                {getFieldDecorator('operator', {
                    rules: [{
                        required: true,
                        message: '请选择操作符'
                    }],
                    initialValue: get(data, 'operator')
                })(
                    <Select disabled={!isEdit} style={{ width: '80px' }}>
                        {operatorMap.map((item: any) => {
                            return (
                                <Option
                                    key={item.value}
                                    value={item.value}
                                >
                                    {item.text}
                                </Option>
                            );
                        })}
                    </Select>
                )}
            </FormItem>
            <FormItem>
                {getFieldDecorator('threshold', {
                    rules: [{
                        required: true,
                        message: '请填写数值'
                    }, {
                        validator: (rule: any, value: any, callback: any) => {
                            if (!value) {
                                callback();
                                return;
                            }
                            const isPercent = this.isPercentage(data);
                            let errorMsg = '请填写正确的数字'
                            try {
                                let number = parseFloat(value);
                                if (isPercent) {
                                    if (number >= 0 && number <= 100) {
                                        callback();
                                        return;
                                    } else {
                                        callback(errorMsg);
                                        return;
                                    }
                                } else if (Number.isNaN(number) || number < 0) {
                                    callback(errorMsg);
                                    return;
                                } else {
                                    callback();
                                    return;
                                }
                            } catch (e) {
                                callback(errorMsg);
                            }
                        }
                    }].filter(Boolean),
                    initialValue: get(data, 'threshold')
                })(
                    <Input disabled={!isEdit} addonAfter={this.isPercentage(data) ? '%' : null} />
                )}
                <HelpDoc doc='thresholdMsg' />
            </FormItem>
        </React.Fragment>
    }
    isPercentage (data: any) {
        const { type } = this.props;
        if (!data) {
            return false;
        }
        const { isPercentage, verifyType } = data;
        if (type == 'typeCheck') {
            if (verifyType == 7) {
                return true;
            }
            return false;
        }
        return isPercentage;
    }
    renderSqlRule () {
        const { isEdit, data } = this.props;
        const { getFieldDecorator } = this.props.form;
        return <React.Fragment>
            <FormItem className='c-rule__item--single' label="sql" >
                {getFieldDecorator('customizeSql', {
                    rules: [{
                        required: true,
                        message: '请填写sql'
                    }],
                    initialValue: get(data, 'customizeSql')
                })(
                    <Input.TextArea
                        disabled={!isEdit}
                        style={{ width: '90%', resize: 'vertical' }}
                        autosize={{ minRows: 3, maxRows: 5 }}
                        placeholder="查询结果为一个数值类型"
                    />
                )}
            </FormItem>
            {this.renderVerifyType()}
            {this.renderOperator()}
        </React.Fragment>
    }
    renderTableRule () {
        const { data } = this.props;
        const { getFieldDecorator } = this.props.form;
        return <React.Fragment>
            <FormItem label="表">
                {getFieldDecorator('columnName', {
                    rules: [{
                        required: true
                    }],
                    initialValue: get(data, 'columnName')
                })(
                    <Input disabled />
                )}
            </FormItem>
            {this.renderFunctionId()}
            {this.renderFilter()}
            {this.renderVerifyType()}
            {this.renderOperator()}
        </React.Fragment>
    }
    renderColumnRule () {
        const { isEdit, data, tableColumn } = this.props;
        const { getFieldDecorator } = this.props.form;
        return <React.Fragment>
            <FormItem label="字段">
                {getFieldDecorator('columnName', {
                    rules: [{
                        required: true,
                        message: '请选择字段'
                    }],
                    initialValue: get(data, 'columnName')
                })(
                    <Select
                        showSearch
                        disabled={!isEdit}
                        style={{ width: '150px' }}
                        filterOption={(input: any, option: any) =>
                            option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                        }
                    >
                        {tableColumn.map((item: any) => {
                            return (
                                <Option
                                    key={`${item.key}`}
                                    value={`${item.key}`}
                                >
                                    {item.key}
                                </Option>
                            );
                        })}
                    </Select>
                )}
            </FormItem>
            {this.renderFunctionId()}
            {this.renderFilter()}
            {this.renderVerifyType()}
            {this.renderOperator()}
        </React.Fragment>
    }
    renderTypeRule () {
        const { isEdit, data, tableColumn, functionList } = this.props;
        const { getFieldDecorator } = this.props.form;
        return <React.Fragment>
            <FormItem label="字段">
                {getFieldDecorator('columnName', {
                    rules: [{
                        required: true,
                        message: '请选择字段'
                    }],
                    initialValue: get(data, 'columnName')
                })(
                    <Select
                        showSearch
                        disabled={!isEdit}
                        style={{ width: '150px' }}
                        filterOption={(input: any, option: any) =>
                            option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                        }
                    >
                        {tableColumn.map((item: any) => {
                            return (
                                <Option
                                    key={`${item.key}`}
                                    value={`${item.key}`}
                                >
                                    {item.key}
                                </Option>
                            );
                        })}
                    </Select>
                )}
            </FormItem>
            <FormItem label="校验格式">
                {getFieldDecorator('functionId', {
                    rules: [{
                        required: true,
                        message: '请选择校验格式'
                    }],
                    initialValue: get(data, 'functionId')
                })(
                    <Select disabled={!isEdit} style={{ width: '150px' }}>
                        {functionList.map((item: any) => {
                            return (
                                <Option
                                    key={item.id}
                                    value={item.id}
                                >
                                    {item.nameZc}
                                </Option>
                            );
                        })}
                    </Select>
                )}
            </FormItem>
            {this.renderFilter()}
            {this.renderOperator(true)}
        </React.Fragment>
    }
    renderRule () {
        const { type, isEdit, data } = this.props;
        let form: any;
        switch (type) {
            case 'column': {
                form = this.renderColumnRule();
                break;
            }
            case 'table': {
                form = this.renderTableRule();
                break;
            }
            case 'sql': {
                form = this.renderSqlRule();
                break;
            }
            case 'typeCheck': {
                form = this.renderTypeRule();
                break;
            }
        }
        return <React.Fragment>
            <div className='c-rule__buttons'>
                {isEdit ? (
                    <React.Fragment>
                        <a onClick={this.onSave} className='c-rule__button'>保存</a>
                        <a onClick={() => { this.props.onCancel(data.id) }} className='c-rule__button'>取消</a>
                    </React.Fragment>
                ) : (<React.Fragment>
                    <a onClick={() => { this.props.onEdit(data.id) }} className='c-rule__button'>编辑</a>
                    <Popconfirm title="确认删除？" okText="确认" cancelText="取消" onConfirm={() => { this.setLoading(); this.props.onDelete(data.id, this.disableLoading) }}>
                        <a className='c-rule__button'>删除</a>
                    </Popconfirm>
                </React.Fragment>)}

            </div>
            {form}
            <div className='c-rule__buttons'>
                <a onClick={this.props.onClone} className='c-rule__button'>克隆</a>
            </div>
        </React.Fragment>
    }
    onSave = () => {
        this.props.form.validateFields(async (err: any, values: any) => {
            if (!err) {
                this.setLoading();
                this.props.onSave(this.props.data, this.disableLoading);
            }
        })
    }
    setLoading = () => {
        this.setState({
            loading: true
        });
    }
    disableLoading = () => {
        this.setState({
            loading: false
        });
    }
    render () {
        return (
            <Spin spinning={this.state.loading}>
                <Form layout="inline" className='c-rule' >
                    {this.renderRule()}
                </Form>
            </Spin>
        )
    }
}
export default Form.create({
    onValuesChange (props: any, values: any) {
        return props.onValuesChange(values);
    },
    mapPropsToFields (props: any) {
        let v: any = {};
        Object.entries(props.data).forEach(([key, value]) => {
            v[key] = {
                value: value
            }
        });
        return v;
    }
})(RuleForm);
