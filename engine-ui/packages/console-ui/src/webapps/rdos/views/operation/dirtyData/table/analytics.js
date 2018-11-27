import React, { Component } from 'react'
import {
    Row, Col, Tabs, Table, Radio, Select, Card
} from 'antd'
import moment from 'moment';

import ajax from '../../../../api/dataManage';

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
        errorType: 'npe',
        currentPage: {
            npe: 1,
            duplicate: 1,
            conversion: 1,
            other: 1
        }
    }

    componentDidMount () {
        const tableId = this.state.tableId;
        this.getTableAnalytics({ tableId, limit: 10, errorType: 'npe' });
        this.getTablePartitions({ tableId, pageSize: 1000 });
        this.getTableCountInfo({ tableId });
    }

    getTableAnalytics (params) {
        this.setState({ loading: true })
        ajax.getDirtyDataAnalytics(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    data: res.data,
                    loading: false
                });
            }
        });
    }

    getTablePartitions (params) {
        ajax.getTablePartition(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    tablePartitions: (res.data && res.data.data) || []
                });
            }
        });
    }

    getTableCountInfo (params) {
        ajax.countDirtyData(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    tableCountInfo: res.data
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
            errorType: value
        })
    }

    onPartitionChange = (value) => {
        const { errorType, tableId } = this.state
        this.setState({ partId: value })
        this.getTableAnalytics({
            tableId,
            errorType,
            partId: value
        })
    }

    generateCols = (data) => {
        const { errorType, currentPage } = this.state;
        if (data && data.length > 0) {
            const arr = [{
                title: '序号',
                key: 't-id',
                width: 80,
                render: (text, item, index) => {
                    return (currentPage[errorType] - 1) * 10 + index + 1
                }
            }]
            data.forEach((item, index) => {
                let titleItem = item;
                arr.push({
                    title: item,
                    key: index + item,
                    width: 200,
                    render: (text, item) => {
                        if (titleItem === 'ts') {
                            return <span>{moment(item[index]).format('YYYY-MM-DD HH:mm:ss')}</span>
                        } else {
                            return <span>{item[index]}</span>
                        }
                    }
                })
            })
            return arr
        }
        return []
    }

    changePage = (pagination) => {
        let { errorType, currentPage } = this.state;
        currentPage[ errorType || 'npe'] = pagination.current;
        this.setState({
            currentPage
        })
    }

    render () {
        console.log(this.state.errorType);

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
            tableCountInfo.conversion + tableCountInfo.duplicate +
            tableCountInfo.npe + tableCountInfo.other
        )

        const tablePane = <Table
            columns={cols}
            className="m-table"
            dataSource={showData}
            loading={this.state.loading}
            scroll={{ x: true, y: 280 }}
            onChange={this.changePage}
        />

        return (
            <Card
                bordered={false}
                noHovering
                title={
                    <span style={{ fontSize: '12px', fontWeight: 'normal' }}>
                        总计：共{tableCountInfo.totalNum || 0}条 脏数据，
                        空指针：{tableCountInfo.npe || 0}条，
                        主键冲突：{tableCountInfo.duplicate || 0}条，
                        类型转换：{tableCountInfo.conversion || 0}条，
                        其他：{tableCountInfo.other || 0}条
                    </span>
                }
                extra={
                    <Select
                        allowClear
                        showSearch
                        placeholder="分区下拉选项"
                        onChange={this.onPartitionChange}
                        style={{ width: '126px', marginTop: '10px' }}
                    >
                        { partitionsOptions }
                    </Select>
                }
            >
                <Row style={{ marginTop: '1px' }}>
                    <Tabs onChange={ this.onTabChange.bind(this) } animated={false}>
                        <TabPane tab={`空指针`} key="npe">
                            {tablePane}
                        </TabPane>
                        <TabPane tab={`主键冲突`} key="duplicate">
                            {tablePane}
                        </TabPane>
                        <TabPane tab={`类型转换`} key="conversion" >
                            {tablePane}
                        </TabPane>
                        <TabPane tab={`其他`} key="other">
                            {tablePane}
                        </TabPane>
                    </Tabs>
                </Row>
            </Card>
        )
    }
}
