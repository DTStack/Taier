import React, { Component } from 'react';
import ajax from '../../../api/dataManage';
import { Input, Spin, Table, Button, Card, Popconfirm, message } from 'antd';
import moment from 'moment';
import AddUpdateRules from './addUpdateRules';
const Search = Input.Search;

class RuleManage extends Component {
    state = {
        cardLoading: false,
        addVisible: false, // 添加规则
        table: [], // 表数据
        queryParams: {
            currentPage: 1,
            pageSize: 20,
            name: undefined
        },
        editModalKey: null,
        status: undefined, // 新增或编辑
        source: {} // 编辑规则信息,
        // mock
        // dataSource: [
        //     {
        //         key: '1',
        //         ruleName: '身份证号',
        //         person: 'admin@dtstack.com',
        //         time: '2018-01-01 12:12:12'
        //     }
        // ]
    }
    componentDidMount () {
        this.search();
    }
    search = () => {
        this.setState({
            cardLoading: true
        })
        const { queryParams } = this.state;
        ajax.searchRule(queryParams).then(res => {
            if (res.code === 1) {
                this.setState({
                    table: res.data,
                    cardLoading: false
                })
            } else {
                this.setState({
                    cardLoading: false
                })
            }
        })
    }
    // 添加规则
    addRule = (params) => {
        ajax.addRule(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    addVisible: false
                })
                message.success('添加成功!');
                this.search();
            }
        })
    }
    // 删除规则
    delete = (record) => {
        ajax.delRule({
            id: record.id
        }).then(res => {
            if (res.code === 1) {
                message.success('删除成功!');
                this.search()
            }
        })
    }
    // 编辑规则
    editRule = (record) => {
        ajax.voidCheckPermission().then(res => {
            if (res.code === 1) {
                ajax.editRule({ id: record.id }).then(res => {
                    if (res.code === 1) {
                        this.setState({
                            source: res.data
                        })
                    }
                })
                this.setState({
                    addVisible: true,
                    status: 'edit',
                    source: record,
                    editModalKey: Math.random()
                })
            }
        })
    }
    changeName = (e) => {
        const { queryParams } = this.state;
        this.setState({
            queryParams: Object.assign(queryParams, { name: e.target.value })
        })
    }
    handleTableChange = (pagination, filters, sorter) => {
        const queryParams = Object.assign(this.state.queryParams, { currentPage: pagination.current })
        this.setState({
            queryParams
        }, this.search)
    }
    initialColumns = () => {
        return [
            {
                title: '规则名称',
                width: 140,
                dataIndex: 'name'
            },
            {
                title: '最近修改人',
                width: 200,
                dataIndex: 'modifyUserName'
            },
            {
                title: '最近修改时间',
                width: 200,
                dataIndex: 'gmtModified',
                render (text, record) {
                    return moment(text).format('YYYY-MM-DD HH:mm:ss')
                }
            },
            {
                title: '操作',
                width: 140,
                dataIndex: 'opera',
                render: (text, record) => {
                    return (
                        <span>
                            <a onClick={() => { this.editRule(record) }}>编辑</a>
                            <span className="ant-divider"></span>
                            <Popconfirm
                                title="确定删除此条规则吗?"
                                okText="是"
                                cancelText="否"
                                onConfirm={() => { this.delete(record) }}
                            >
                                <a>删除</a>
                            </Popconfirm>
                        </span>
                    )
                }
            }
        ]
    }
    render () {
        const columns = this.initialColumns();
        const { table, cardLoading, addVisible, status, source, editModalKey } = this.state;
        return (
            <div className='box-1 m-card'>
                <Card
                    noHovering
                    bordered={false}
                    loading={false}
                    title={
                        <Search
                            placeholder='按规则名称搜索'
                            style={{ width: '200px', marginTop: '10px' }}
                            onChange={this.changeName}
                            onSearch={this.search}
                        />
                    }
                    extra={
                        <Button
                            type='primary'
                            style={{ marginTop: '10px' }}
                            onClick={() => { this.setState({ addVisible: true, status: 'add', source: {}, editModalKey: Math.random() }) }}
                        >
                            创建规则
                        </Button>
                    }
                >
                    <Spin tip="正在加载中..." spinning={cardLoading}>
                        <Table
                            className="m-table"
                            columns={columns}
                            dataSource={table}
                            onChange={this.handleTableChange.bind(this)}
                        />
                    </Spin>
                </Card>
                <AddUpdateRules
                    key={editModalKey}
                    visible={addVisible}
                    onCancel={() => { this.setState({ addVisible: false, source: {}, status: undefined }) }}
                    onOk={this.addRule}
                    status={status}
                    dataSource={source}
                />
            </div>
        )
    }
}
export default RuleManage;
