import React, { PureComponent } from 'react';
import { Form, Tabs, Select, InputNumber, Input, message } from 'antd';
import { formItemLayout } from './index';
import { MemorySetting as BaseMemorySetting } from './typeChange';
import { cloneDeep, debounce, isEmpty } from 'lodash';
import api from '../../../../../../api/experiment';
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const Option = Select.Option;
const inputStyle = {
    width: '100%'
}
/* 字段设置 */
class ParamSetting extends PureComponent {
    constructor (props) {
        super(props);
        this.state = {
            tableData: [],
            params: cloneDeep(props.data)
        }
        this.handleChange = debounce(this.handleChange, 800);
    }
    componentDidMount () {
        this.getTableColumns()
    }
    getTableColumns = () => {
        const { taskId } = this.props;
        api.getInputTableColumns({ taskId }).then(res => {
            if (res.code === 1) {
                let tableData = [];
                for (const key in res.data) {
                    if (res.data.hasOwnProperty(key)) {
                        const element = res.data[key];
                        tableData.push({
                            key,
                            type: element
                        })
                    }
                }
                this.setState({
                    tableData
                })
            }
        })
    }
    handleChange = (field, value) => {
        const { params } = this.state;
        if (field === 'splitType') {
            const mewParams = value === '1' ? {
                split_type: 1,
                split_percent: undefined,
                random_seed: undefined
            } : {
                split_type: 2,
                thresholdCol: {},
                operator: '>',
                threshold: undefined
            }
            this.setState({
                params: mewParams
            })
        } else {
            params[field] = value
            this.setState({
                params
            })
            this.props.form.validateFieldsAndScroll((err, values) => {
                if (!err) {
                    this.props.handleSaveComponent(params);
                }
            });
        }
    }
    validatRate = (rule, value, callback) => {
        if (value > 0 && value < 1) {
            callback()
        } else {
            callback(new Error('取值范围为（0，1）'));
        }
    }
    normalize = (value) => {
        if (parseInt(value) == value) {
            return parseInt(value)
        } else {
            return value
        }
    }
    renderFormItme = () => {
        const { tableData } = this.state;
        const { getFieldDecorator } = this.props.form;
        const selectedValue = this.props.form.getFieldValue('splitType');
        const prefixSelector = getFieldDecorator('operator', {
            initialValue: '>'
        })(
            <Select onChange={(value) => this.handleChange('operator', value)} style={{ width: 48 }}>
                <Option value=">">{`>`}</Option>
                <Option value=">=">{`>=`}</Option>
                <Option value="<">{`<`}</Option>
                <Option value="<=">{`<=`}</Option>
                <Option value="=">{`=`}</Option>
            </Select>
        );
        switch (selectedValue) {
            case '1':
                return (
                    <>
                        <FormItem
                            label={<div style={{ display: 'inline-block' }}>拆分比例<span className="supplementary">表1占原数据的比例，取值范围为（0，1）</span></div>}
                            colon={false}
                            {...formItemLayout}
                        >
                            {getFieldDecorator('splitPercent', {
                                rules: [
                                    { required: true, message: '请输入拆分比例' },
                                    { validator: this.validatRate }
                                ]
                            })(
                                <InputNumber onChange={(value) => this.handleChange('splitPercent', value)} style={inputStyle} />
                            )}
                        </FormItem>
                        <FormItem
                            label={<div style={{ display: 'inline-block' }}>随机数种子<span className="supplementary">系统可默认生成</span></div>}
                            colon={false}
                            {...formItemLayout}
                        >
                            {getFieldDecorator('randomSeed', {
                                rules: [
                                    { required: false },
                                    {
                                        type: 'number', min: 1
                                    }
                                ]
                            })(
                                <InputNumber
                                    onChange={(value) => this.handleChange('randomSeed', value)}
                                    parser={value => value ? parseInt(value) : value}
                                    formatter={value => value ? parseInt(value) : value}
                                    style={inputStyle} />
                            )}
                        </FormItem>
                    </>
                )
            case '2':
            default: return (
                <>
                    <FormItem
                        label={<div style={{ display: 'inline-block' }}>阈值列<span className="supplementary">选择单列，不支持String列</span></div>}
                        colon={false}
                        {...formItemLayout}
                    >
                        {getFieldDecorator('thresholdCol', {
                            rules: [
                                { required: true, message: '请选择阈值列' }
                            ]
                        })(
                            <Select onChange={(value) => this.handleChange('thresholdCol', value)}>
                                {
                                    tableData.filter(o => o.type !== 'string').map((item, index) => {
                                        return (
                                            <Option value={item.key} key={item.key}>{item.key}</Option>
                                        )
                                    })
                                }
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        label={<div style={{ display: 'inline-block' }}>阈值<span className="supplementary">表1数据拆分结果</span></div>}
                        colon={false}
                        {...formItemLayout}
                    >
                        {getFieldDecorator('threshold', {
                            rules: [
                                { required: true, message: '请输入阈值' },
                                { type: 'number', message: '只允许输入数字' }
                            ],
                            normalize: this.normalize
                        })(
                            <Input onChange={(e) => this.handleChange('threshold', e.target.value)} addonBefore={prefixSelector} style={inputStyle} />
                        )}
                    </FormItem>
                </>
            )
        }
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        return (
            <Form className="params-form">
                <FormItem
                    label='拆分方式'
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('splitType', {
                        initialValue: '1',
                        rules: [{ required: true, message: '请选择拆分方式' }]
                    })(
                        <Select onChange={(value) => this.handleChange('splitType', value)}>
                            <Option value='1'>按比例拆分</Option>
                            <Option value='2'>按阈值拆分</Option>
                        </Select>
                    )}
                </FormItem>
                {this.renderFormItme()}
            </Form>
        )
    }
}
/* 内存设置 */
class MemorySetting extends BaseMemorySetting {
    constructor (props) {
        super(props)
    }
}
class DataSplit extends PureComponent {
    constructor (props) {
        super(props);
        this.handleSaveComponent = debounce(this.handleSaveComponent, 800);
    }
    handleSaveComponent = (dataSplitComponent) => {
        const { currentTab, componentId, changeContent } = this.props;
        const currentComponentData = currentTab.graphData.find(o => o.data.id === componentId);
        const params = {
            ...currentComponentData.data,
            dataSplitComponent: {
                ...dataSplitComponent
            }
        }
        api.addOrUpdateTask(params).then((res) => {
            if (res.code == 1) {
                currentComponentData.data = { ...params, ...res.data };
                changeContent({}, currentTab);
            } else {
                message.warning('保存失败');
            }
        })
    }
    render () {
        const { data, taskId } = this.props;
        const WrapParamSetting = Form.create({
            mapPropsToFields: (props) => {
                const { data } = props;
                const values = {
                    splitType: { value: data.splitType ? data.splitType.toString() : '1' },
                    splitPercent: { value: data.splitPercent },
                    randomSeed: { value: data.randomSeed },
                    thresholdCol: { value: isEmpty(data.thresholdCol) ? '' : data.thresholdCol.key },
                    threshold: { value: data.threshold },
                    operator: { value: data.operator }
                }
                return values;
            }
        })(ParamSetting);
        const WrapMemorySetting = Form.create({
            onFieldsChange: (props, changedFields) => {
                for (const key in changedFields) {
                    if (changedFields.hasOwnProperty(key)) {
                        const element = changedFields[key];
                        if (!element.validating && !element.dirty) {
                            const { data } = props;
                            data[key] = element.value
                            props.handleSaveComponent(data)
                        }
                    }
                }
            },
            mapPropsToFields: (props) => {
                const { data } = props;
                const values = {
                    workerMemory: { value: data.workerMemory },
                    workerCores: { value: data.workerCores }
                }
                return values;
            }
        })(MemorySetting);
        return (
            <Tabs type="card" className="params-tabs">
                <TabPane tab="参数设置" key="1">
                    <WrapParamSetting data={data} handleSaveComponent={this.handleSaveComponent} taskId={taskId} />
                </TabPane>
                <TabPane tab="内存设置" key="2">
                    <WrapMemorySetting data={data} handleSaveComponent={this.handleSaveComponent} />
                </TabPane>
            </Tabs>
        );
    }
}

export default DataSplit;
