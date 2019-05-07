import React, { PureComponent } from 'react';
import { formItemLayout } from './index';
import { debounce } from 'lodash';
import { Form, Select, Checkbox, InputNumber, message } from 'antd';
import api from '../../../../../../api/experiment';
const FormItem = Form.Item;
const Option = Select.Option;
// 表选择
class ChooseTable extends PureComponent {
    state = {
        tables: []
    }
    constructor (props) {
        super(props);
        this.handleSaveComponent = debounce(this.handleSaveComponent, 800);
        this.handleChange = debounce(this.handleChange, 800);
    }
    handleChange = (value) => {
        this.props.form.setFieldsValue({
            tableName: String(value)
        })
        this.handleSaveComponent();
        api.getTableByName({ tableName: value }).then(res => {
            if (res.code === 1) {
                this.setState({
                    tables: res.data
                })
            }
        })
    }
    handleSaveComponent = () => {
        const { currentTab, componentId, changeContent } = this.props;
        const form = this.props.form;
        const currentComponentData = currentTab.graphData.find(o => o.vertex && o.data.id === componentId);
        const params = {
            ...currentComponentData.data,
            writeTableComponent: {
                ...currentComponentData.data.writeTableComponent,
                tableName: form.getFieldValue('tableName'),
                lifecycle: form.getFieldValue('lifeCycle')
            }
        }
        api.addOrUpdateTask(params).then((res) => {
            if (res.code === 1) {
                currentComponentData.data = { ...params, ...res.data };
                changeContent({}, currentTab);
            } else {
                message.warning('保存失败');
            }
        })
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        const { tables } = this.state;
        return (
            <Form className="params-form">
                <FormItem
                    label={
                        <div style={{ display: 'inline-block' }}>
                            表名
                            <span className="supplementary">支持新建表，若要写入分区表，需提前建好分区表</span>
                        </div>}
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('tableName', {
                        rules: [{ required: true, message: '请选择表名' }]
                    })(
                        <Select
                            mode="combobox"
                            notFoundContent="未找到数据表"
                            placeholder="请选择表名"
                            defaultActiveFirstOption={false}
                            filterOption={false}
                            onChange={this.handleChange}
                        >
                            {tables.map((item, index) => {
                                return <Option key={index} value={item}>{item}</Option>
                            })}
                        </Select>
                    )}
                    {getFieldDecorator('partitionCheck', {
                        valuePropName: 'checked'
                    })(
                        <Checkbox>分区</Checkbox>
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label='设置表生命周期'
                    {...formItemLayout}
                >
                    {getFieldDecorator('lifeCycle', {
                        initialValue: 28,
                        rules: [{ required: true, message: '请填写表生命周期' }]
                    })(
                        <InputNumber style={{ width: '100%' }} onChange={this.handleSaveComponent} />
                    )}
                </FormItem>
            </Form>
        );
    }
}

/* main页面 */
class WriteDatabase extends PureComponent {
    render () {
        const WrapChooseTable = Form.create({
            mapPropsToFields: (props) => {
                const { data } = props;
                const values = {
                    tableName: { value: data.tableName || '' },
                    lifeCycle: { value: data.lifecycle }
                }
                return values;
            }
        })(ChooseTable);
        return (
            <div className="params-single-tab">
                <div className="c-panel__siderbar__header">
                    表选择
                </div>
                <div className="params-single-tab-content">
                    <WrapChooseTable {...this.props} />
                </div>
            </div>
        );
    }
}

export default WriteDatabase;
