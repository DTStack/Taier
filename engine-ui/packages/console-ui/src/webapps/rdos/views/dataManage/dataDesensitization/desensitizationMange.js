import React, { Component } from 'react';
import { connect } from 'react-redux';
import ajax from '../../../api/dataManage';
import { Input, Spin, Table, Button, Card } from 'antd';
import '../../../styles/pages/dataManage.scss';
import AddDesensitization from './addDesensitization';
const Search = Input.Search;

@connect(state => {
    return {
        allProjects: state.allProjects,
        user: state.user
    }
}, null)

class DesensitizationMange extends Component {
    state = {
        cardLoading: false,
        addVisible: false,
        table: [],
        queryParams: {
            pageIndex: 1,
            pageSize: 20,
            desensitizationName: undefined
        },
        // mock
        dataSource: [
            {
                key: '1',
                desensitizationName: '身份证号脱敏',
                tableNum: 12,
                desensitizationRule: '身份证号',
                person: 'admin@dtstack.com',
                time: '2018-01-01 12:12:12',
                deal: '删除'
            }
        ]
    }
    componentDidMonut () {
        // this.search();
    }
    search = () => {
        this.setState({
            cardLoading: false
        })
        const { queryParams } = this.state;
        ajax.searchDesensitization(queryParams).then(res => {
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
    changeName = (e) => {
        const { queryParams } = this.state;
        this.setState({
            queryParams: Object.assign(queryParams, { desensitizationName: e.target.value })
        })
    }
    handleTableChange = (pagination, filters, sorter) => {
        const queryParams = Object.assign(this.state.queryParams, { pageIndex: pagination.current })
        this.setState({
            queryParams
        }, this.search)
    }
    // 添加脱敏
    addDesensitization = (desensitization) => {
        ajax.addDesensitization(desensitization).then(res => {
            if (res.code === 1) {
                this.setState({
                    addVisible: false
                })
                this.search();
            }
        })
    }
    initialColumns = () => {
        return [
            {
                title: '脱敏名称',
                width: 140,
                dataIndex: 'desensitizationName',
                render () {
                    return <a>身份证号脱敏</a>
                }
            },
            {
                title: '关联表数量',
                width: 140,
                dataIndex: 'tableNum'
            },
            {
                title: '脱敏规则',
                width: 140,
                dataIndex: 'desensitizationRule'
            },
            {
                title: '最近修改人',
                width: 200,
                dataIndex: 'person'
            },
            {
                title: '最近修改时间',
                width: 200,
                dataIndex: 'time'
            },
            {
                title: '操作',
                width: 140,
                dataIndex: 'deal',
                render (text, record) {
                    return <a>删除</a>
                }
            }
        ]
    }
    render () {
        const columns = this.initialColumns();
        const { cardLoading, dataSource, addVisible } = this.state;
        return (
            <div className='box-1 m-card'>
                <Card
                    noHovering
                    bordered={false}
                    loading={false}
                    title={
                        <Search
                            placeholder='按脱敏名称搜索'
                            style={{ width: '200px', marginTop: '10px' }}
                            onChange={this.changeName}
                            onSearch={this.search}
                        />
                    }
                    extra={
                        <Button
                            type='primary'
                            style={{ marginTop: '10px' }}
                            onClick={() => { this.setState({ addVisible: true }) }}
                        >
                            添加脱敏
                        </Button>
                    }
                >
                    <Spin tip="正在加载中..." spinning={cardLoading}>
                        <Table
                            className="m-table"
                            columns={columns}
                            dataSource={dataSource}
                            onChange={this.handleTableChange.bind(this)}
                        />
                    </Spin>
                </Card>
                <AddDesensitization
                    visible={addVisible}
                    onCancel={() => { this.setState({ addVisible: false }) }}
                    onOk={this.addDesensitization}
                />
            </div>
        )
    }
}
export default DesensitizationMange;
