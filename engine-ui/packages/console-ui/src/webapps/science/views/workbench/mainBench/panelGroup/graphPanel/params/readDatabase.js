/* eslint-disable no-template-curly-in-string */
import React, { PureComponent } from 'react';
import { Tabs, Form, Select, Checkbox, Input, Tooltip, Icon } from 'antd';
const formItemLayout = {
    labelCol: {
        span: 24
    },
    wrapperCol: {
        span: 24
    }
};
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const Option = Select.Option;
// 表选择
class ChooseTable extends PureComponent {
    state = {
        tables: [],
        partitionCheck: false // 选择的数据表是否为分区表
    }
    handleChange = (value) => {
        this.props.form.setFieldsValue({
            tableName: String(value)
        })
        this.setState({
            tables: [{
                name: '11111'
            }]
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
        const { tables, partitionCheck } = this.state;
        return (
            <Form className="params-form">
                <FormItem
                    label="表名"
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
                                return <Option key={index} value={item.name}>{item.name}</Option>
                            })}
                        </Select>
                    )}
                    <Checkbox disabled checked={partitionCheck}>分区</Checkbox>
                </FormItem>
                {partitionCheck && <FormItem
                    colon={false}
                    label={<div>分区参数{this.renderTooltips()}</div>}
                    {...formItemLayout}
                >
                    {getFieldDecorator('partitionParam', {})(
                        <Input placeholder='如：ds=20190328， ds=${bdp.system.bizdate}' />
                    )}
                </FormItem>}
            </Form>
        );
    }
}
// 字段信息
class TableInfo extends PureComponent {
    initTableBody (data) {
        let str = [];
        for (const key in data) {
            if (data.hasOwnProperty(key)) {
                const element = data[key];
                str.push(<tr key={key}><th>{key}</th><th>{element}</th></tr>);
            }
        }
        return str;
    }
    render () {
        // TODO
        const testData = {
            id: 'String',
            amount: 'Double',
            ISBN: 'String'
        }
        return (
            <table border="1" className="params-table">
                <thead>
                    <tr>
                        <th>字段</th>
                        <th>类型</th>
                    </tr>
                </thead>
                <tbody>
                    {this.initTableBody(testData)}
                </tbody>
            </table>
        );
    }
}

class ReadDatabase extends PureComponent {
    render () {
        const WrapChooseTable = Form.create()(ChooseTable);
        return (
            <Tabs type="card" className="params-tabs">
                <TabPane tab="表选择" key="1">
                    <WrapChooseTable/>
                </TabPane>
                <TabPane tab="字段信息" key="2">
                    <TableInfo />
                </TabPane>
            </Tabs>
        );
    }
}

export default ReadDatabase;
