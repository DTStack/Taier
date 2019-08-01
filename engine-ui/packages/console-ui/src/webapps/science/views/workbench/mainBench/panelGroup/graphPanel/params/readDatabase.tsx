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
class ChooseTable extends React.PureComponent<any, any> {
    constructor (props: any) {
        super(props);
        this.fetchTables = debounce(this.fetchTables, 500);
        this.handleSaveComponent = debounce(this.handleSaveComponent, 500);
    }
    state: any = {
        tables: [],
        fetching: false,
        isTableNameChanged: false
    }
    componentWillUnmount () {
        if (this.state.isTableNameChanged) {
            this.handleBlur();
        }
    }
    fetchTables = (value: any) => {
        this.setState({
            fetching: true
        })
        api.getTableByName({ tableName: value }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    tables: res.data,
                    fetching: false
                })
            } else {
                this.setState({
                    fetching: false
                })
            }
        })
    }
    handleBlur = () => {
        const value = this.props.form.getFieldValue('tableName');
        this.props.toggleLock();
        api.isPartitionTable({ tableName: value }).then((res: any) => {
            if (res.code === 1) {
                this.props.form.setFieldsValue({
                    partitionCheck: res.data,
                    tableName: value
                })
                this.handleSaveComponent(value);
            } else {
                this.props.toggleLock();
            }
            this.setState({
                tables: [],
                fetching: false
            })
        })
    }
    handleSaveComponent = (value: any) => {
        const { currentTab, changeContent, componentId } = this.props;
        const form = this.props.form;
        const currentComponentData = currentTab.graphData.find((o: any) => o.vertex && o.data.id === componentId);
        const params: any = {
            ...currentComponentData.data,
            readTableComponent: {
                ...currentComponentData.data.readTableComponent,
                table: form.getFieldValue('tableName') || value,
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
            this.props.toggleLock();
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
                            mode="combobox"
                            showSearch
                            placeholder="请选择表名"
                            {...{
                                showArrow: false
                            }}
                            notFoundContent={fetching ? <Spin size="small" /> : null}
                            filterOption={false}
                            onSearch={this.fetchTables}
                            onBlur={this.handleBlur}
                            style={{ width: '100%' }}
                            onChange={() => (
                                this.state.isTableNameChanged || this.setState({
                                    isTableNameChanged: true
                                })
                            )}
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
            </Form>
        );
    }
}
// 字段信息
class TableInfo extends React.PureComponent<any, any> {
    state: any = {
        columnsData: {},
        loading: false
    }
    componentDidUpdate (prevProps: any) {
        if (prevProps.tableName !== this.props.tableName) {
            this.getTableInfo();
        }
    }
    componentDidMount () {
        this.getTableInfo();
    }
    getTableInfo = () => {
        const { tableName } = this.props;
        this.setState({
            loading: true
        })
        api.getColumnsByTableName({ tableName }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    columnsData: res.data
                });
            }
            this.setState({
                loading: false
            })
        })
    }
    initTableBody (data: any) {
        let str: any = [];
        for (const key in data) {
            if (data.hasOwnProperty(key)) {
                const element = data[key];
                str.push(<tr key={key}><th>{key}</th><th>{element}</th></tr>);
            }
        }
        return str;
    }
    render () {
        const { columnsData, loading } = this.state;
        return (
            <Spin spinning={loading}>
                <table style={{ border: '1' }} className="params-table">
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
            </Spin>
        );
    }
}

class ReadDatabase extends React.PureComponent<any, any> {
    tableName: any;
    constructor (props: any) {
        super(props)
        this.tableName = { value: props.data.table };
    }
    render () {
        const { data = {}, currentTab } = this.props;
        const WrapChooseTable = Form.create({
            onFieldsChange: (props: any, changedFields: any) => {
                if (changedFields.tableName && !changedFields.tableName.validating && !changedFields.tableName.dirty) {
                    /**
                     * 此处监听form表单change事件，通过获取tableName缓存下来
                     * 不放在state里面是为了不触发钩子函数
                     * 放在对象里面是为了保证字段信息tab能实时获取到tableName
                     */
                    this.tableName.value = changedFields.tableName.value;
                }
            },
            mapPropsToFields: (props: any) => {
                const { data } = props;
                if (typeof (data.isPartition) === 'string') {
                    data.isPartition = data.isPartition === 'true'
                }
                const values: any = {
                    tableName: { value: data.table || '' },
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
                    <WrapChooseTable data={data} currentTab={currentTab} {...this.props}/>
                </TabPane>
                <TabPane tab="字段信息" key="2">
                    <TableInfo data={data} tableName={this.tableName.value} />
                </TabPane>
            </Tabs>
        );
    }
}

export default ReadDatabase;
