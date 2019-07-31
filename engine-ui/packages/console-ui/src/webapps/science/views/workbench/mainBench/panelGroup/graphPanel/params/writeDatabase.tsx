/* eslint-disable no-template-curly-in-string */
import React, { PureComponent } from 'react';
import { formItemLayout } from './index';
import { debounce } from 'lodash';
import { Form, Select, Checkbox, InputNumber, message, Input, Tooltip, Icon } from 'antd';
import api from '../../../../../../api/experiment';
const FormItem = Form.Item;
const Option = Select.Option;
// 表选择
class ChooseTable extends React.PureComponent<any, any> {
    state: any = {
        tables: [],
        isTableNameChanged: false
    }
    constructor (props: any) {
        super(props);
        this.handleSaveComponent = debounce(this.handleSaveComponent, 500);
        this.handleChange = debounce(this.handleChange, 500);
    }
    componentWillUnmount () {
        if (this.state.isTableNameChanged) {
            this.handleBlur();
        }
    }
    handleBlur = () => {
        const value = this.props.form.getFieldValue('tableName');
        api.isPartitionTable({ tableName: value }).then((res: any) => {
            if (res.code === 1) {
                this.props.form.setFieldsValue({
                    partitionCheck: res.data
                })
            }
            this.handleSaveComponent(value);
        })
    }
    handleChange = (value: any) => {
        this.state.isTableNameChanged || this.setState({
            isTableNameChanged: true
        });
        this.props.form.setFieldsValue({
            tableName: String(value)
        })
        api.getTableByName({ tableName: value }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    tables: res.data
                })
            }
        })
    }
    handleSaveComponent = (value: any) => {
        const { currentTab, componentId, changeContent } = this.props;
        const form = this.props.form;
        const currentComponentData = currentTab.graphData.find((o: any) => o.vertex && o.data.id === componentId);
        const params: any = {
            ...currentComponentData.data,
            writeTableComponent: {
                ...currentComponentData.data.writeTableComponent,
                table: form.getFieldValue('tableName') || value,
                lifecycle: form.getFieldValue('lifeCycle'),
                isPartition: form.getFieldValue('partitionCheck'),
                partitions: form.getFieldValue('partitionParam')
            }
        }
        api.addOrUpdateTask(params).then((res: any) => {
            if (res.code === 1) {
                currentComponentData.data = { ...params, ...res.data };
                changeContent({}, currentTab);
            } else {
                message.warning('保存失败');
            }
        })
    }
    renderTooltips = () => {
        const title = '分区配置支持填写动态分区，如下：\n${bdp.system.premonth}，表示yymm-1\n${bdp.system.cyctime}，表示运行时间数据\n${bdp.system.bizdate}，表示yymmdd-1\n${bdp.system.currmonth}，表示当前月数据';
        return <Tooltip overlayClassName="big-tooltip" title={title}>
            <Icon type="question-circle-o" className="supplementary" />
        </Tooltip>
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        const { tables } = this.state;
        const partitionCheck = this.props.form.getFieldValue('partitionCheck');
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
                            onBlur={this.handleBlur}
                        >
                            {tables.map((item: any, index: any) => {
                                return <Option key={index} value={item}>{item}</Option>
                            })}
                        </Select>
                    )}
                    {getFieldDecorator('partitionCheck', {
                        valuePropName: 'checked'
                    })(
                        <Checkbox disabled>分区</Checkbox>
                    )}
                </FormItem>
                {partitionCheck && <FormItem
                    colon={false}
                    label={<div>分区参数{this.renderTooltips()}</div>}
                    {...formItemLayout}
                >
                    {getFieldDecorator('partitionParam', {})(
                        <Input placeholder='如：ds=20190328， ds=${bdp.system.bizdate}' onChange={this.handleSaveComponent} />
                    )}
                </FormItem>}
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
class WriteDatabase extends React.PureComponent<any, any> {
    render () {
        const WrapChooseTable = Form.create({
            mapPropsToFields: (props: any) => {
                const { data = {} } = props;
                const values: any = {
                    tableName: { value: data.table || '' },
                    partitionCheck: { value: data.isPartition },
                    partitionParam: { value: data.partitions || '' },
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
