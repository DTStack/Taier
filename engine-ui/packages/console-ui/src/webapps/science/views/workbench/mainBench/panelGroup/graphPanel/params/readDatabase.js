import React, { PureComponent } from 'react';
import { Tabs, Form, Select, Checkbox, Input } from 'antd';
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
    render () {
        const { getFieldDecorator } = this.props.form;
        const { tables, partitionCheck } = this.state;
        return (
            <Form className="choose-table-form">
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
                <FormItem
                    colon={false}
                    label={<div>分区参数<span className="supplementary">{`例如 dt=@@{yyyymmdd - 1d}`}</span></div>}
                    {...formItemLayout}
                >
                    {getFieldDecorator('partitionParam', {})(
                        <Input placeholder="如：dt=20190328， dt=@@{yyyymmdd-1d}" />
                    )}
                </FormItem>
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
                str.push(<tr><th>{key}</th><th>{element}</th></tr>);
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
            <table border="1" className="table-info">
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
            <Tabs type="card" className="read-database">
                <TabPane tab="表选择" key="1">
                    <WrapChooseTable />
                </TabPane>
                <TabPane tab="字段信息" key="2">
                    <TableInfo />
                </TabPane>
            </Tabs>
        );
    }
}

export default ReadDatabase;
