import React, { Component } from 'react'
import {
    Row, Col, Tabs, Table, Radio, Select, Card
} from 'antd'

import ajax from '../../../../api';
import TitleBar from '../../../../components/title-bar' 

const TabPane = Tabs.TabPane;
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;
const Option = Select.Option


export default class TableAnalytics extends Component {

    state = {
        data: [],
        tableId: this.props.routeParams.tableId,
        tablePartitions: [],
        tableCountInfo: '',
        loading: false,
    }

    componentDidMount() {
        const tableId = this.state.tableId;
        this.getTableAnalytics({ tableId, limit: 10, errorType: 0 });
        this.getTablePartitions({ tableId, pageSize: 1000 });
        this.getTableCountInfo({ tableId });
    }

    getTableAnalytics(params) {
        this.setState({ loading: true })
        ajax.getDirtyDataAnalytics(params).then(res => {
            if(res.code === 1) {
                this.setState({
                    data: res.data,
                    loading: false,
                });
            }
        });
    }

    getTablePartitions(params) {
        ajax.getTablePartition(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    tablePartitions: (res.data && res.data.data) || [],
                });
            }
        });
    }

    getTableCountInfo(params) {
        ajax.countDirtyData(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    tableCountInfo: res.data,
                });
            }
        });
    }

    onTabChange = (value) => {
        const { partId, tableId } = this.state
        this.setState({ errorType: value })
        this.getTableAnalytics({
            tableId,
            partId,
            errorType: value,
        })
    }

    onPartitionChange = (value) => {
        const { errorType, tableId } = this.state
        this.setState({ partId: value, })
        this.getTableAnalytics({
            tableId,
            errorType,
            partId: value,
        })
    }

    generateCols = (data) => {
        if (data && data.length > 0) {
            const arr = [{
                title: '序号',
                key: 't-id',
                render: (text, item, index) => {
                    return index + 1
                },
            }]
            data.forEach((item, index) => {
                arr.push({
                    title: item,
                    key: index + item,
                    render: (text, item) => {
                        return <span>{item[index]}</span>
                    },
                })
            })
            return arr
        }
        return []
    }

    render() {
        const { tableData } = this.props;
        const { data, tablePartitions, tableCountInfo } = this.state
  
        const partitionsOptions = tablePartitions && tablePartitions.map(p => 
            <Option id={p.partId} value={`${p.partId}`} title={p.name}>
                {p.name}
            </Option>
        )

        const cols = this.generateCols(data[0])
        const showData = data.slice(1, data.length)
        const dirtyDataCount = tableCountInfo && (
            tableCountInfo.conversion + tableCountInfo.duplicate 
            + tableCountInfo.npe + tableCountInfo.other
        )

        const tablePane = <Table 
            columns={cols} 
            className="m-table"
            dataSource={showData} 
            loading={this.state.loading}
        />

        return (
            <Card 
                bordered={false}
                noHovering
                title={
                    <span> 总计：共{tableCountInfo.totalNum}条 脏数据</span>
                }
                extra={
                    <Select 
                        allowClear
                        showSearch
                        placeholder="分区下拉选项"
                        onChange={this.onPartitionChange} 
                        style={{width: '200px', marginTop: '10px'}}
                    >
                        { partitionsOptions }
                    </Select>
                }
            >
                <Row style={{ marginTop: '1px' }}>
                    <Tabs onChange={ this.onTabChange.bind(this) } >
                        <TabPane tab={`空指针 (${tableCountInfo.npe || 0}条)`} key="npe">
                            {tablePane}
                        </TabPane>
                        <TabPane tab={`主键冲突 (${tableCountInfo.duplicate || 0}条)`} key="duplicate">
                            {tablePane}
                        </TabPane>
                        <TabPane tab={`类型转换 (${tableCountInfo.conversion || 0}条)`} key="conversion">
                            {tablePane}
                        </TabPane>
                        <TabPane tab={`其他 (${tableCountInfo.other || 0}条)`} key="other">
                            {tablePane}
                        </TabPane>
                    </Tabs>
                </Row>
            </Card>
        )
    }
}
