import React, { PureComponent } from 'react';
import { Form, Select, Input, InputNumber, message } from 'antd';
import { debounce } from 'lodash';
import api from '../../../../../../api/experiment';
import { formItemLayout } from './index';
const FormItem = Form.Item;
const Option = Select.Option;
// 表选择
class FieldSetting extends PureComponent {
    state = {
        originalColumns: []
    }
    componentDidMount () {
        this.getColumns()
    }
    getColumns = () => {
        const { taskId } = this.props;
        api.getInputTableColumns({ taskId }).then((res) => {
            if (res.code === 1) {
                let originalColumns = [];
                for (const key in res.data) {
                    if (res.data.hasOwnProperty(key)) {
                        const element = res.data[key];
                        originalColumns.push({
                            key,
                            type: element
                        })
                    }
                }
                this.setState({
                    originalColumns
                })
            }
        })
    }
    handleChange = (value) => {
        const { originalColumns } = this.state;
        const object = originalColumns.find(o => o.key === value);
        if (object) {
            this.props.handleSaveComponent('oldLabel', object);
        }
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        const { originalColumns } = this.state;
        return (
            <Form className="params-form">
                <FormItem
                    label='原始标签列列名'
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('oldLabel', {
                        rules: [{ required: true, message: '请选择原始标签列列名' }]
                    })(
                        <Select onChange={this.handleChange}>
                            {originalColumns.map((item, index) => {
                                return <Option key={item.key} value={String(item.key)}>{item.key}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    label='分数列列名'
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('scoreCol', {
                        rules: [{ required: true, message: '请输入分数列列名' }]
                    })(
                        <Input placeholder="请输入分数列列名" />
                    )}
                </FormItem>
                <FormItem
                    label='正样本的标签值'
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('pos', {
                        rules: [{ required: true, message: '请输入正样本的标签值' }]
                    })(
                        <Input placeholder="请输入正样本的标签值" />
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label='计算KS,PR等指标时按等频分成多少个桶'
                    {...formItemLayout}
                >
                    {getFieldDecorator('bin', {
                        initialValue: 100,
                        rules: [
                            { required: true, message: '请填写桶数' },
                            { max: 1000, message: '不可超过1000个桶', type: 'number' }
                        ]
                    })(
                        <InputNumber
                            parser={value => value ? parseInt(value) : value}
                            formatter={value => value ? parseInt(value) : value}
                            style={{ width: '100%' }} />
                    )}
                </FormItem>
            </Form>
        );
    }
}

/* main页面 */
class BinaryClassfication extends PureComponent {
    constructor (props) {
        super(props);
        this.handleSaveComponent = debounce(this.handleSaveComponent, 800);
    }
    handleSaveComponent = (field, filedValue) => {
        const { data, currentTab, componentId, changeContent } = this.props;
        const currentComponentData = currentTab.graphData.find(o => o.data.id === componentId);
        const params = {
            ...currentComponentData.data,
            eveluationComponent: {
                ...data
            }
        }
        if (field) {
            params.eveluationComponent[field] = filedValue
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
        const WrapFieldSetting = Form.create({
            onFieldsChange: (props, changedFields) => {
                for (const key in changedFields) {
                    if (changedFields.hasOwnProperty(key)) {
                        if (key === 'oldLabel') {
                            // label是下拉菜单，在组件里自己触发onChange函数,对数据封装过后再请求
                            continue;
                        }
                        const element = changedFields[key];
                        if (!element.validating && !element.dirty && !element.errors) {
                            props.handleSaveComponent(key, element.value)
                        }
                    }
                }
            },
            mapPropsToFields: (props) => {
                const { data } = props;
                const values = {
                    oldLabel: { value: !data.oldLabel ? '' : data.oldLabel.key },
                    scoreCol: { value: data.scoreCol },
                    pos: { value: data.pos },
                    bin: { value: data.bin }
                }
                return values;
            }
        })(FieldSetting);
        return (
            <div className="params-single-tab">
                <div className="c-panel__siderbar__header">
                    字段设置
                </div>
                <div className="params-single-tab-content">
                    <WrapFieldSetting data={data} handleSaveComponent={this.handleSaveComponent} taskId={taskId} />
                </div>
            </div>
        );
    }
}

export default BinaryClassfication;
