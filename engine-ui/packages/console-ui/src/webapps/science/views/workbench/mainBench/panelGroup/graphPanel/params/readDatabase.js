/* eslint-disable no-template-curly-in-string */
import React, { PureComponent } from 'react';
import { Tabs, Form, Select, Checkbox, Input, Tooltip, Icon, Spin, message } from 'antd';
import { formItemLayout } from './index';
import { debounce } from 'lodash';
import api from '../../../../../../api/experiment';
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const Option = Select.Option;
// 表选择
class ChooseTable extends PureComponent {
    constructor (props) {
        super(props);
        this.fetchTables = debounce(this.fetchTables, 800);
        this.handleSaveComponent = debounce(this.handleSaveComponent, 800);
    }
    state = {
        tables: [],
        fetching: false
    }
    fetchTables = (value) => {
        this.setState({
            fetching: true
        })
        api.getTableByName({ tableName: value }).then(res => {
            if (res.code === 1) {
                this.setState({
                    tables: res.data
                })
            }
        })
    }
    handleChange = (value) => {
        api.isPartitionTable({ tableName: value }).then((res) => {
            if (res.code === 1) {
                this.props.form.setFieldsValue({
                    partitionCheck: res.data
                })
                if (!res.data) {
                    this.handleSaveComponent();
                }
            }
        })
        this.setState({
            tables: [],
            fetching: false
        })
    }
    handleSaveComponent = () => {
        const form = this.props.form;
        const params = {
            readTableComponent: {
                table: form.getFieldValue('tableName'),
                partitions: form.getFieldValue('partitionParam')
            }
        }
        if (form.getFieldValue('partitionCheck') && !params.readTableComponent.partitions) {
            return null;
        } else {
            api.addOrUpdateTask(params).then((res) => {
                if (res.code === 1) {
                    message.success('保存成功');
                } else {
                    message.warning('保存失败');
                }
            })
        }
    }
    renderTooltips = () => {
        const title = '分区配置支持填写动态分区，如下：\n${bdp.system.premonth}，表示yymm-1\n${bdp.system.cyctime}，表示运行时间数据\n${bdp.system.bizdate}，表示yymmdd-1\n${bdp.system.currmonth}，表示当前月数据';
        return <Tooltip overlayClassName="big-tooltip" title={title}>
            <Icon type="question-circle-o" className="supplementary" />
        </Tooltip>
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        const { tables, fetching } = this.state;
        const partitionCheck = this.props.form.getFieldValue('partitionCheck');
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
                            showSearch
                            placeholder="请选择表名"
                            notFoundContent={fetching ? <Spin size="small" /> : '未找到数据表'}
                            filterOption={false}
                            onSearch={this.fetchTables}
                            onChange={this.handleChange}
                            style={{ width: '100%' }}
                        >
                            {tables.map((item, index) => {
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
            </Form>
        );
    }
}
// 字段信息
class TableInfo extends PureComponent {
    state = {
        columnsData: {}
    }
    componentDidUpdate (prevProps) {
        this.getTableInfo();
    }
    componentDidMount () {
        this.getTableInfo();
    }
    getTableInfo = () => {
        const { tableName } = this.props;
        api.getColumnsByTableName({ tableName }).then((res) => {
            if (res.code === 1) {
                this.setState({
                    columnsData: res.data
                });
            }
        })
    }
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
        const { columnsData } = this.state;
        return (
            <table border="1" className="params-table">
                <thead>
                    <tr>
                        <th>字段</th>
                        <th>类型</th>
                    </tr>
                </thead>
                <tbody>
                    {this.initTableBody(columnsData)}
                </tbody>
            </table>
        );
    }
}

class ReadDatabase extends PureComponent {
    state = {
        tableName: ''
    }
    constructor (props) {
        super(props)
        this.state = {
            tableName: props.data.table
        }
    }
    static getDerivedStateFromProps (nextProps, prevState) {
        if (nextProps.data.table === prevState.tableName) {
            return null
        } else {
            return {
                tableName: prevState.tableName
            }
        }
    }
    render () {
        const { tableName } = this.state;
        const { data } = this.props;
        const WrapChooseTable = Form.create({
            onFieldsChange: (props, changedFields) => {
                if (!changedFields.tableName.validating && !changedFields.tableName.dirty) {
                    this.setState({
                        tableName: changedFields.tableName.value
                    })
                }
            },
            mapPropsToFields: (props) => {
                const { data } = props;
                const values = {
                    tableName: { value: data.table },
                    partitionCheck: { value: data.isPartition }
                }
                if (data.isPartition) {
                    values.partitionParam = { value: data.partitions }
                }
                return values;
            }
        })(ChooseTable);
        return (
            <Tabs type="card" className="params-tabs">
                <TabPane tab="表选择" key="1">
                    <WrapChooseTable data={data}/>
                </TabPane>
                <TabPane tab="字段信息" key="2">
                    <TableInfo data={data} tableName={tableName} />
                </TabPane>
            </Tabs>
        );
    }
}

export default ReadDatabase;
