import React, { Component } from 'react';

import { 
    Row, Table, Card, Input, 
    Button, Dropdown, Menu, Icon
} from 'antd';

const Search = Input.Search;

class DatabaseDetail extends Component {

    state = {
        userList: [],
    }

    componentDidMount () {
        
    }
    
    initEdit = () => {

    }

    onSearch = (value) => {
        
    }

    remove = () => {

    }

    onSelectMenu = (key) => {

    }

    initColumns = () => {
        return [{
            title: '账号',
            dataIndex: 'account',
            key: 'account',
        },
        {
            title: '邮箱',
            dataIndex: 'email',
            key: 'email',
        }, {
            title: '手机号',
            dataIndex: 'phoneNumber',
            key: 'phoneNumber',
        }, {
            title: '角色',
            dataIndex: 'roleName',
            key: 'roleName',
        }, {
            title: '加入时间',
            dataIndex: 'joinTime',
            key: 'joinTime',
            width: 100,
        }, {
            title: '操作',
            width: 100,
            key: 'operation',
            render: (text, record) => {
                 // active  '0：未启用，1：使用中'。  只有为0时，可以修改
                return (
                    <span key={record.id}>
                        <a onClick={() => {this.initEdit(record)}}>
                            编辑
                        </a>
                        <span className="ant-divider" />
                        <Popconfirm
                            title="确定移除此用户？"
                            okText="确定" cancelText="取消"
                            onConfirm={() => { this.remove(record) }}
                        >
                            <a>移除</a>
                        </Popconfirm>
                    </span>
                )
            },
        }]
    }

    renderDropMenu = () => {
        return (
            <Dropdown overlay={
                <Menu onSelect={this.onSelectMenu}>
                    <Menu.Item key="RESET">
                        重置密码
                    </Menu.Item>
                    <Menu.Item key="DELETE">
                        删除数据库
                    </Menu.Item>
                </Menu>
            }>
                <Icon type="bars" style={{ 
                    margin: '7 5 0 0', fontSize: 18, color: '#333333',
                    float: 'right',
                }} />
            </Dropdown>
        )
    }

    render () {
        const { data } = this.props;
        const { userList } = this.state;
        return (
            <div className="pane-wrapper" style={{ padding: '0px 20px' }}>
                <Row className="row-content">
                    <h1 className="row-title">
                        数据库信息
                        {this.renderDropMenu()}
                    </h1>
                    <table style={{ marginTop: 5 }} className="table-info" width="100%" cellPadding="0" cellSpacing="0">
                        <tbody>
                            <tr>
                                <td>JDBC信息</td>
                                <td>{data.jdbcUrl}</td>
                                <td>创建时间</td>
                                <td>{data.createTime}</td>
                            </tr>
                            <tr>
                                <td>用户名</td>
                                <td>{data.username}</td>
                                <td>表数量</td>
                                <td>{data.tableCount}</td>
                            </tr>
                            <tr>
                                <td>创建者</td>
                                <td>{data.creator}</td>
                                <td>物理存储量</td>
                                <td>{data.storeSize}</td>
                            </tr>
                        </tbody>
                    </table>
                </Row>
                <Row className="row-content">
                    <h1 className="row-title">权限管理</h1>
                    <Card
                        className="m-card"
                        bordered={false}
                        noHovering={true}
                        bodyStyle={
                            {
                                padding: '0',
                            }
                        }
                        title={
                            <Search
                                placeholder="输入用户名搜索"
                                style={{ width: 160 }}
                                onSearch={this.onSearch} 
                            />
                        }
                        extra={
                            <Button type="primary">添加用户</Button>
                        }
                    >
                        <Table
                            className="m-table bd"
                            rowKey="id"
                            columns={this.initColumns()}
                            dataSource={ userList }
                        />
                    </Card>
                </Row>
            </div>
        )
    }
}

export default DatabaseDetail