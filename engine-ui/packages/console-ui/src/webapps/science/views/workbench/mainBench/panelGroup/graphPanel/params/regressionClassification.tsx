/* eslint-disable @typescript-eslint/camelcase */
import * as React from 'react';
import { Form, Tabs, Input, message, Select, InputNumber } from 'antd';
import { formItemLayout } from './index';
import { MemorySetting as BaseMemorySetting } from './typeChange';
import { debounce, isNumber } from 'lodash';
import api from '../../../../../../api/experiment';
import { TASK_ENUM, COMPONENT_TYPE } from '../../../../../../consts';
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const inputStyle: any = {
    width: '100%'
}
/* 字段设置 */
class FieldSetting extends React.PureComponent<any, any> {
    state: any = {
    }
    renderNumberFormItem (options: {
        label: string;
        key: string;
        initialValue?: number;
        min?: number;
        excludeMin?: boolean;
        max: number;
        excludeMax?: boolean;
        step?: number;
        isInt?: boolean;
        isRequired?: boolean;
    }, getFieldDecorator: any): JSX.Element {
        return <FormItem
            colon={false}
            label={<div style={{ display: 'inline-block' }}>{options.label}{options.max != null && (<span className="supplementary">{options.excludeMin ? '(' : '['}{options.min || 0},{options.max}{options.excludeMax ? ')' : ']'}, {options.isInt ? '正整数' : 'float型'}</span>)}</div>}
            {...formItemLayout}
        >
            {getFieldDecorator(options.key, {
                initialValue: options.initialValue,
                rules: [
                    { required: !!options.isRequired },
                    options.max != null && { min: options.min || 0, max: options.max, message: `${options.label}的取值范围为${options.excludeMin ? '(' : '['}${options.min},${options.max}${options.excludeMax ? ')' : ']'}`, type: 'number' }
                ].filter(Boolean)
            })(
                <InputNumber
                    {...{
                        onBlur: (e: any) => this.handleSubmit(options.key, e.target.value)
                    }}
                    step={options.step}
                    formatter={options.isInt ? (value: any) => { return isNumber(value) ? ~~value : value; } : undefined}
                    style={inputStyle}
                />
            )}
        </FormItem>
    }
    handleSubmit (name: any, value: any) {
        this.props.form.validateFieldsAndScroll([name], (err: any, values: any) => {
            if (!err) {
                this.props.handleSaveComponent(name, value);
            }
        });
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        return (
            <Form className="params-form">
                <FormItem
                    colon={false}
                    label='原回归值'
                    {...formItemLayout}
                >
                    {getFieldDecorator('old_value', {
                        rules: [
                            { required: true, message: '请输入原回归值' }
                        ]
                    })(
                        <Select
                            onSelect={(value: any) => this.handleSubmit('old_value', value)}
                            showSearch
                            placeholder="请选择原回归值"
                        >

                        </Select>
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label='预测回归值'
                    {...formItemLayout}
                >
                    {getFieldDecorator('new_value', {
                        rules: [
                            { required: true, message: '请输入预测回归值' },
                            {
                                pattern: /^(\w){0,}$/,
                                message: '预测回归值只能由字母、数字、下划线组成!'
                            }
                        ]
                    })(
                        <Input {...{
                            onBlur: (e: any) => this.handleSubmit('new_value', e.target.value)
                        }} placeholder="请输入预测回归值" />
                    )}
                </FormItem>
                {this.renderNumberFormItem({
                    label: '计算Residual时按等频分成多少个桶',
                    key: 'bin',
                    max: 100,
                    step: 1,
                    initialValue: 100,
                    isRequired: true,
                    isInt: true
                }, getFieldDecorator)}
            </Form>
        )
    }
}
/* 内存设置 */
class MemorySetting extends BaseMemorySetting {
    constructor (props: any) {
        super(props)
    }
}
/* main页面 */
class RegressionClassification extends React.PureComponent<any, any> {
    constructor (props: any) {
        super(props);
        this.handleSaveComponent = debounce(this.handleSaveComponent, 800);
    }
    /**
     * 统一处理保存
     */
    handleSaveComponent = (field: any, filedValue: any) => {
        const { data, currentTab, componentId, changeContent } = this.props;
        const fieldName = TASK_ENUM[COMPONENT_TYPE.DATA_EVALUATE.REGRESSION_CLASSIFICATION];
        const currentComponentData = currentTab.graphData.find((o: any) => o.vertex && o.data.id === componentId);
        const params: any = {
            ...currentComponentData.data,
            [fieldName]: {
                ...data
            }
        }
        if (field) {
            params[fieldName][field] = filedValue
        }
        api.addOrUpdateTask(params).then((res: any) => {
            if (res.code == 1) {
                currentComponentData.data = { ...params, ...res.data };
                changeContent({}, currentTab);
            } else {
                message.warning('保存失败');
            }
        })
    }
    render () {
        const { data, currentTab, componentId } = this.props;
        const WrapFieldSetting = Form.create({
            mapPropsToFields: (props: any) => {
                const { data } = props;
                /* eslint-disable */
                const values: any = {
                    result_col: { value: data.result_col },
                    score_col: { value: data.score_col },
                    detail_col: { value: data.detail_col }
                }
                /* eslint-enable */
                return values;
            }
        })(FieldSetting);
        const WrapMemorySetting = Form.create({
            onFieldsChange: (props: any, changedFields: any) => {
                for (const key in changedFields) {
                    if (changedFields.hasOwnProperty(key)) {
                        const element = changedFields[key];
                        if (!element.validating && !element.dirty) {
                            props.handleSaveComponent(key, element.value)
                        }
                    }
                }
            },
            mapPropsToFields: (props: any) => {
                const { data } = props;
                const values: any = {
                    workerMemory: { value: data.workerMemory },
                    workerCores: { value: data.workerCores }
                }
                return values;
            }
        })(MemorySetting);
        return (
            <Tabs type="card" className="params-tabs">
                <TabPane tab="字段设置" key="1">
                    <WrapFieldSetting data={data} handleSaveComponent={this.handleSaveComponent} currentTab={currentTab} componentId={componentId} />
                </TabPane>
                <TabPane tab="内存设置" key="2">
                    <WrapMemorySetting data={data} handleSaveComponent={this.handleSaveComponent} />
                </TabPane>
            </Tabs>
        );
    }
}

export default RegressionClassification;
