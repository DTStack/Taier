import React, { Component } from 'react';
import { debounce } from 'lodash';
import { 
    Row, Table, Card, Input, Modal,
    Button, Dropdown, Menu, Icon
} from 'antd';

import utils from 'utils';
import { MY_APPS, } from 'main/consts'
import EditUserRole from 'main/views/admin/user/editRole';

import AddUserModal from './addUser';
import UpdateDBModal from './update';

import API from '../../../../api/database';

const Search = Input.Search;
const confirm = Modal.confirm;

class DatabaseDetail extends Component {

    state = {
        selectedItem: undefined,
        visibleAddUser: false,
        visibleResetPwd: false,
        visibleEditRole: false,

        userList: [],
        userRoles: [],
        usersNotInDB: [],

        queryParams: {
            pageSize: 10,
            currentPage: 1,
            databaseId: (this.props.data && this.props.data.id) || undefined,
        },
    }

    componentDidMount () {
        this.loadDBUserRoles();
    }

    loadDBUsers = async (params) => {
        const reqParams = Object.assign(this.state.queryParams, params);
        const res = await API.getDBUsers(reqParams);
        if (res.code === 1) {
            this.setState({
                userList: res.data || [],
            })
        }
    }

    loadDBUserRoles = async () => {
        const res = await API.getDBUserRoles();
        if (res.code === 1) {
            this.setState({
                userRoles: res.data || [],
            })
        }
    }

    initEdit = (selectedItem) => {
        this.setState({
            selectedItem,
        })
    }

    removeUser = async (removeTarget) => {
        const res = await API.removeDBUser({
            userId: removeTarget.id,
        });
        if (res.code === 1) {
            message.success('删除成功！');
            this.loadDBUsers();
        }
    }

    onSearchUsers = async (value) => {
        const res = await API.searchUsersNotInDB({
            userName: value,
        });
        if (res.code === 1) {
            this.setState({
                usersNotInDB: res.data || [],
            })
        }
    }

    onEditUserRole = async () => {

    }

    onSelectMenu = ({ key }) => {
        console.log('onClick:', key)
        const { data, onRemoveDB } = this.props;
        
        if (key === 'RESET') {
            this.setState({
                visibleResetPwd: true,
            })
        } else if (key === 'DELETE') {
            confirm({
                title: '警告',
                content: '删除数据库后无法恢复，数据库内的所有数据无法找回，确认删除？',
                okText: '确定',
                okType: 'danger',
                cancelText: '取消',
                onOk() {
                    onRemoveDB({
                        id: data.id,
                    });
                },
                onCancel() {
                  console.log('Cancel');
                },
            });
        }
    }

    debounceSearch = debounce(this.onSearchUsers, 300, { 'maxWait': 2000 })

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
                 // active '0：未启用，1：使用中'。 只有为0时，可以修改
                return (
                    <span key={record.id}>
                        <a onClick={() => { this.initEdit(record) }}>
                            编辑
                        </a>
                        <span className="ant-divider" />
                        <Popconfirm
                            title="确定移除此用户？"
                            okText="确定" cancelText="取消"
                            onConfirm={() => { this.removeUser(record) }}
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
                <Menu onClick={this.onSelectMenu}>
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
        const { data, user } = this.props;
        const { userList, userRoles, usersNotInDB, selectedItem } = this.state;
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
                                <td>{utils.formatDateTime(data.gmtCreate)}</td>
                            </tr>
                            <tr>
                                <td>用户名</td>
                                <td>{data.dbUserName}</td>
                                <td>表数量</td>
                                <td>{data.tableNum}</td>
                            </tr>
                            <tr>
                                <td>创建者</td>
                                <td>{data.createUserName}</td>
                                <td>物理存储量</td>
                                <td>{data.size}</td>
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
                                onSearch={(value) => this.loadDBUsers({ name: value })} 
                            />
                        }
                        extra={
                            <Button
                                type="primary"
                                onClick={() => {
                                    this.setState({
                                        visibleAddUser: true,
                                    })
                                }}
                            >
                                添加用户
                            </Button>
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
                <AddUserModal
                    roles={userRoles}
                    userList={usersNotInDB}
                    initialData={data}
                    onSubmit={this.addUser}
                    onSearch={this.debounceSearch}
                    onCancel={() => {this.setState({
                        visibleAddUser: false,
                    })}}
                    visible={this.state.visibleAddUser}
                />
                <UpdateDBModal 
                    defaultData={data}
                    visible={this.state.visibleResetPwd}
                    onCancel={() => {this.setState({
                        visibleResetPwd: false,
                    })}}
                />
                <Modal
                    visible={this.state.visibleEditRole}
                    onOk={this.onEditUserRole}
                    onCancel={() => {this.setState({
                        visibleEditRole: false,
                    })}}
                >
                    <EditUserRole
                        user={selectedItem}
                        roles={userRoles}
                        myRoles={user.roles}
                        loginUser={user}
                        app={MY_APPS.ANALYTICS_ENGINE}
                    />
                </Modal>
            </div>
        )
    }
}

export default DatabaseDetail;