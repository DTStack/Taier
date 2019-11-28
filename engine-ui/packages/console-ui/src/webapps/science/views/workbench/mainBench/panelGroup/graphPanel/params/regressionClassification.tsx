/* eslint-disable @typescript-eslint/camelcase */
import * as React from 'react';
import { Form, Tabs, Input, message, Select, Spin } from 'antd';
import { formItemLayout } from './index';
import { MemorySetting as BaseMemorySetting } from './typeChange';
import { debounce, isEmpty } from 'lodash';
import api from '../../../../../../api/experiment';
import { TASK_ENUM, COMPONENT_TYPE } from '../../../../../../consts';
import { renderNumberFormItem } from './helper';

const TabPane = Tabs.TabPane;
const Option = Select.Option;
const FormItem = Form.Item;
/* 字段设置 */
class FieldSetting extends React.PureComponent<any, any> {
    state: any = {
        columns: [],
        fetching: false
    }
    getColumns = () => {
        if (this.state.columns.length > 0) {
            /**
             * 此处是为了减少请求次数
             */
            return;
        }
        const { currentTab, componentId } = this.props;
        const targetEdge = currentTab.graphData.find((o: any) => {
            return o.edge && o.target.data.id == componentId
        })
        if (targetEdge) {
            this.setState({
                fetching: true
            })
            api.getInputTableColumns({ taskId: componentId, inputType: targetEdge.inputType }).then((res: any) => {
                if (res.code === 1) {
                    let columns: any = [];
                    for (const key in res.data) {
                        if (res.data.hasOwnProperty(key)) {
                            const element = res.data[key];
                            columns.push({
                                key,
                                type: element
                            })
                        }
                    }
                    this.setState({
                        columns
                    })
                }
                this.setState({
                    fetching: false
                })
            })
        }
    }
    handleChange = (value: any) => {
        const { columns } = this.state;
        const object = columns.find((o: any) => o.key === value);
        if (object) {
            this.props.handleSaveComponent('label', object);
        }
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
        const { columns, fetching } = this.state;
        return (
            <Form className="params-form">
                <FormItem
                    colon={false}
                    label='原回归值'
                    {...formItemLayout}
                >
                    {getFieldDecorator('label', {
                        rules: [
                            { required: true, message: '请输入原回归值' }
                        ]
                    })(
                        <Select
                            onSelect={this.handleChange}
                            notFoundContent={fetching ? <Spin size="small" /> : '未找到数据表'}
                            onFocus={this.getColumns}
                            showSearch
                            placeholder="请选择原回归值"
                        >
                            {columns.map((item: any, index: any) => {
                                return <Option key={item.key} value={item.key}>{item.key}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label='预测回归值'
                    {...formItemLayout}
                >
                    {getFieldDecorator('pre', {
                        rules: [
                            { required: true, message: '请输入预测回归值' },
                            {
                                pattern: /^(\w){0,}$/,
                                message: '预测回归值只能由字母、数字、下划线组成!'
                            }
                        ]
                    })(
                        <Input {...{
                            onBlur: (e: any) => this.handleSubmit('pre', e.target.value)
                        }} placeholder="请输入预测回归值" />
                    )}
                </FormItem>
                {renderNumberFormItem({
                    handleSubmit: this.handleSubmit.bind(this),
                    label: '计算Residual时按等频分成多少个桶',
                    key: 'bucket',
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
                    label: { value: (!data.label || isEmpty(data.label)) ? '' : data.label.key },
                    pre: { value: data.pre },
                    bucket: { value: data.bucket }
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
                        if (!element.errors && !element.validating && !element.dirty) {
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
