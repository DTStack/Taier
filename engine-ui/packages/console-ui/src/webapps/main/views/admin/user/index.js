import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Input, Table, Card, Button } from 'antd'
import { Link } from 'react-router'

import DqApi from 'dataQuality/api'

const Search = Input.Search

class AdminUser extends Component {

    state = {
        active: 'all',
        data: '',
        loading: 'success',
    }

    componentDidMount() {
        this.loadData();
    }

    loadData = () => {
        this.setState({ loading: 'loading' })
        DqApi.queryRole().then(res => {
            this.setState({
                data: res.data,
                loading: 'success'
            })
        })
    }

    onPaneChange = (key) => {
        this.setState({
            active: key,
        })
    }

    initColums = () => {
        return [{
            title: '账号',
            dataIndex: 'account',
            key: 'account',
            render(text, record) {
                return <Link to={`message/detail/${record.id}`}>{text}</Link>
            },
        }, {
            title: '邮箱',
            dataIndex: 'age',
            key: 'age',
        }, {
            title: '邮箱',
            dataIndex: 'email',
            key: 'email',
        }, {
            title: '手机号',
            dataIndex: 'phoneNumber',
            key: 'phoneNumber',
        }, {
            title: '角色',
            dataIndex: 'roles',
            key: 'roles',
        }, {
            title: '加入时间',
            dataIndex: 'address',
            key: 'address',
        }, {
            title: '操作',
            dataIndex: 'id',
            key: 'id',
            render(id, record) {

            }
        }]
    }

    render() {
        const { data, loading } = this.state;

        const rowSelection = {
            onChange: (selectedRowKeys, selectedRows) => {
                console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
            },
            getCheckboxProps: record => ({
                disabled: record.name === 'Disabled User', // Column configuration not to be checked
            }),
        };

        const title = (
            <Search
                placeholder="按项目名称搜索"
                style={{ width: 200 }}
                onSearch={this.onSearch}
            />
        )

        const extra = (
            <Button 
                type="primary" 
                onClick={() => { this.setState({ visible: true }) }}>
                创建项目
            </Button>
        )

        return (
            <div className="box-2">
                <h1 className="box-title">用户管理</h1>
                <Card 
                    title={title} 
                    extra={extra}
                >
                    <Table 
                        rowKey="id"
                        className="m-table"
                        columns={this.initColums()} 
                        loading={loading === 'loading'}
                        dataSource={ data ? data.data : [] } 
                        rowSelection={rowSelection} 
                    />
                </Card>
            </div>
        )
    }
}

export default AdminUser