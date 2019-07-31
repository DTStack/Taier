import * as React from 'react';
import {
    Table,
    Radio,
    Checkbox
} from 'antd'

// const TabPane = Tabs.TabPane;
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;

export default class PaneField extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            paginationParams: {
                current: 1,
                total: 0,
                pageSize: 10
            },
            dataList: [],
            dataType: 'column'
        }
    }
    componentDidMount (props: any) {
        this.initData(this.props);
    }
    changeData = (e: any) => {
        this.setState({
            dataType: e.target.value
        }, () => this.initData(this.props))
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        this.initData(nextProps);
    }

    initData = (props: any) => {
        let {
            paginationParams,
            dataType
        } = this.state;
        const columnData = props.data.columnData || [];
        const partData = props.data.partData || [];

        let data = dataType === 'column' ? columnData : partData;
        if (data && data.length === 0) {
            this.setState({
                paginationParams: {
                    ...paginationParams,
                    total: 0,
                    current: 1
                },
                dataList: []
            })
            return;
        }
        this.setState({
            paginationParams: {
                ...paginationParams,
                total: data.length || 0,
                current: 1
            },
            dataList: data
        });
    }

    handleTableChange = (pagination: any, filters: any, sorter: any) => {
        let {
            paginationParams
        } = this.state
        this.setState({
            paginationParams: {
                ...paginationParams,
                current: pagination.current
            }
        })
    }
    getTableCol () {
        return [{
            title: '字段名称',
            dataIndex: 'name'
        }, {
            title: '倒排索引',
            dataIndex: 'invert',
            render: (text: any, record: any) => (
                text === 0 ? '-'
                    : <Checkbox disabled={true} defaultChecked={text === 1} onChange={(e: any) => this.handleInvert(e, record)}></Checkbox>
            )
        }, {
            title: '字典编码',
            dataIndex: 'dictionary',
            render: (text: any, record: any) => (
                text === 0 ? '-'
                    : <Checkbox disabled={true} defaultChecked={text === 1} onChange={(e: any) => this.handleDictionary(e, record)}></Checkbox>
            )
        }, {
            title: '多维索引',
            dataIndex: 'sortColumn',
            render: (text: any, record: any) => (
                text === 0 ? '-'
                    : <Checkbox disabled={true} defaultChecked={text === 1} onChange={(e: any) => this.handleSortColumn(e, record)}></Checkbox>
            )
        }, {
            title: '类型',
            dataIndex: 'type'
        }, {
            title: '注释',
            dataIndex: 'comment',
            render: (text: any, record: any) => (
                text || '-'
            )
        }];
    }
    getPartitionCol () {
        return [{
            title: '字段名称',
            dataIndex: 'name'
        }, {
            title: '类型',
            dataIndex: 'type'
        }, {
            title: '注释',
            dataIndex: 'comment',
            render: (text: any, record: any) => (
                text || '-'
            )
        }];
    }
    render () {
        const { dataList, paginationParams, dataType } = this.state;
        const tableCOl = this.getTableCol();
        const partitionCol = this.getPartitionCol();
        return (
            <div className="pane-field-container">
                <div className="func-box" style={{ marginBottom: 10 }}>
                    <RadioGroup onChange={this.changeData} defaultValue="column">
                        <RadioButton value="column">非分区字段</RadioButton>
                        <RadioButton value="partition">分区字段</RadioButton>
                    </RadioGroup>

                    <span style={{ color: 'rgb(204, 204, 204)' }}>共{paginationParams.total}个字段</span>
                </div>
                <Table
                    columns={dataType === 'column' ? tableCOl : partitionCol}
                    size="small"
                    dataSource={dataList}
                    rowKey="id"
                    pagination={paginationParams}
                    onChange={this.handleTableChange}>
                </Table>
            </div>
        )
    }
}
