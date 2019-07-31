import * as React from 'react';
import { debounce } from 'lodash';
import {
    Row, Table, Card, Input, Modal, Popconfirm,
    Button, Dropdown, Menu, message, Pagination
} from 'antd';

import CopyToClipboard from 'react-copy-to-clipboard';

import utils from 'utils';
import { MY_APPS, APP_ROLE } from 'main/consts'
import EditUserRole from 'main/views/admin/user/editRole';
import MyIcon from '../../../../components/icon';

import AddUserModal from './addUser';
import UpdateDBModal from './update';

import API from '../../../../api/database';

const Search = Input.Search;
const confirm = Modal.confirm;

class DatabaseDetail extends React.Component<any, any> {
    state: any = {
        loading: 'success',
        selectedItem: undefined,
        visibleAddUser: false,
        visibleResetPwd: false,
        visibleEditRole: false,

        userList: [],
        userRoles: [],
        usersNotInDB: [],
        myRoles: {},

        queryParams: {
            pageSize: 10,
            currentPage: 1,
            name: undefined,
            databaseId: (this.props.data && this.props.data.id) || undefined
        }
    }

    componentDidMount () {
        this.loadDBUsers();
        this.loadDBUserRoles();
        this.getOwnRoles(this.props.user);
        this.onSearchUsers();
    }

    loadDBUsers = async (params: any) => {
        this.setState({ loading: 'loading' });
        const reqParams = Object.assign(this.state.queryParams, params);
        const res = await API.getDBUsers(reqParams);
        if (res.code === 1) {
            this.setState({
                userList: res.data || {}
            })
        }
        this.setState({
            loading: 'success'
        })
    }

    loadDBUserRoles = async () => {
        const res = await API.getDBUserRoles();
        if (res.code === 1) {
            this.setState({
                userRoles: res.data.data || []
            })
        }
    }

    initEdit = (selectedItem: any) => {
        this.setState({
            selectedItem,
            visibleEditRole: true
        })
    }

    addUser = async (data: any) => {
        const { usersNotInDB } = this.state;

        data.targetUsers = [];
        const uids = data.targetUserIds;
        uids.forEach((uid: any) => {
            const user = usersNotInDB.find((user: any) => user.userId == uid);
            if (user) {
                data.targetUsers.push(user);
            }
        })

        const res = await API.addDBUser(data);
        if (res.code === 1) {
            message.success('添加成功！');
            this.setState({
                visibleAddUser: false
            })
            this.loadDBUsers({ name: undefined });
        }
    }

    removeUser = async (removeTarget: any) => {
        const { data } = this.props;
        const res = await API.removeDBUser({
            targetUserId: removeTarget.id,
            databaseId: data.id
        });
        if (res.code === 1) {
            message.success('删除成功！');
            this.loadDBUsers({ name: undefined });
        }
    }

    onSearchUsers = async (value: any) => {
        const res = await API.searchUsersNotInDB({
            userName: value
        });
        if (res.code === 1) {
            this.setState({
                usersNotInDB: res.data || []
            })
        }
    }

    onEditUserRole = async () => {
        const { data } = this.props;
        const ctx = this;
        const { selectedItem } = this.state;
        const editForm = this._eidtRoleForm.props.form;

        editForm.validateFields(async (err: any, values: any) => {
            if (!err) {
                values.targetUserId = selectedItem.userId;
                values.databaseId = data.id;

                const res = await API.updateDBUserRole(values);
                if (res.code === 1) {
                    message.success('更新成功！');
                    ctx.setState({
                        visibleEditRole: false,
                        selectedItem: ''
                    });
                    ctx.loadDBUsers({ name: undefined })
                }
            }
        });
    }

    /**
     * 获取我当前的角色
     */
    getOwnRoles = async (user: any) => {
        let isVisitor = false;

        let isProjectAdmin = false;

        let isProjectOwner = false;

        const reqParams = Object.assign(this.state.queryParams, {
            name: user.userName
        });

        const res = await API.getDBUsers(reqParams);
        if (res.code === 1) {
            const roles = res.data.data && res.data.data.length > 0
                ? res.data.data[0].roles : [];

            for (let role of roles) {
                const roleValue = role.roleValue;
                if (roleValue == APP_ROLE.VISITOR) {
                    isVisitor = true
                } else if (roleValue == APP_ROLE.ADMIN) {
                    isProjectAdmin = true;
                }
            }
        }
        this.setState({
            myRoles: {
                isVisitor,
                isProjectAdmin,
                isProjectOwner
            }
        })
    }

    onSelectMenu = ({ key }) => {
        const { data, onRemoveDB } = this.props;

        if (key === 'RESET') {
            this.setState({
                visibleResetPwd: true
            })
        } else if (key === 'DELETE') {
            confirm({
                title: '警告',
                content: '删除数据库后无法恢复，数据库内的所有数据无法找回，确认删除？',
                okText: '确定',
                okType: 'danger',
                cancelText: '取消',
                onOk () {
                    onRemoveDB({
                        databaseId: data.id
                    });
                },
                onCancel () {
                    console.log('Cancel');
                }
            });
        }
    }

    debounceSearch = debounce(this.onSearchUsers, 300, { 'maxWait': 2000 })

    initColumns = () => {
        const { myRoles } = this.state;
        return [{
            title: '账号',
            dataIndex: 'user.userName',
            key: 'account'
        },
        {
            title: '邮箱',
            dataIndex: 'user.email',
            key: 'email'
        }, {
            title: '手机号',
            dataIndex: 'user.phoneNumber',
            key: 'phoneNumber',
            width: 100
        }, {
            title: '角色',
            dataIndex: 'roles',
            key: 'roles',
            render (roles: any) {
                const roleNames = roles.map((role: any) => role && role.roleName)
                return roleNames.join(',')
            }
        }, {
            title: '加入时间',
            dataIndex: 'gmtCreate',
            key: 'gmtCreate',
            width: 150,
            render (time: any) {
                return utils.formatDateTime(time);
            }
        }, {
            title: '操作',
            dataIndex: 'id',
            width: 140,
            key: 'id',
            render: (id: any, record: any) => {
                // active '0：未启用，1：使用中'。 只有为0时，可以修改
                const canRemove = myRoles.isProjectAdmin ||
                myRoles.isProjectOwner; // 项目管理员，所有者可移除
                return (
                    <span key={id}>
                        <a onClick={() => { this.initEdit(record) }}>
                            编辑角色
                        </a>
                        {
                            canRemove
                                ? <span>
                                    <span className="ant-divider" />
                                    <Popconfirm
                                        title="确定移出此用户？"
                                        okText="确定" cancelText="取消"
                                        onConfirm={() => { this.removeUser(record) }}
                                    >
                                        <a>移出数据库</a>
                                    </Popconfirm>
                                </span>
                                : ''
                        }
                    </span>
                )
            }
        }]
    }

    copyOk = () => {
        message.success('复制成功！');
    }

    renderDropMenu = () => {
        return (
            <Dropdown trigger={['click']} overlay={
                <Menu onClick={this.onSelectMenu}>
                    <Menu.Item key="RESET">
                        重置密码
                    </Menu.Item>
                    <Menu.Item key="DELETE">
                        删除数据库
                    </Menu.Item>
                </Menu>
            }>
                <MyIcon type="more" style={{
                    margin: '8 10 0 0',
                    fontSize: 18,
                    color: '#333333',
                    float: 'right',
                    cursor: 'pointer'
                }}
                />
            </Dropdown>
        )
    }

    render () {
        const { data, user } = this.props;

        const {
            loading, userList, userRoles, queryParams,
            usersNotInDB, selectedItem, myRoles
        } = this.state;

        const pagination: any = {
            total: userList.totalCount,
            defaultPageSize: 10,
            current: queryParams.currentPage,
            style: {
                float: 'right',
                marginTop: 15
            }
        };

        return (
            <div className="pane-wrapper" style={{ padding: '0px 20px 50px 20px' }}>
                <Row className="row-content">
                    <h1 className="row-title">
                        数据库信息
                        {this.renderDropMenu()}
                    </h1>
                    <table style={{ marginTop: 5 }} className="table-info" width="100%" cellPadding="0" cellSpacing="0">
                        <tbody>
                            <tr>
                                <td>JDBC信息</td>
                                <td>
                                    {data.jdbcUrl}
                                    <CopyToClipboard key="copy" text={data.jdbcUrl}
                                        onCopy={this.copyOk}>
                                        <a style={{ marginLeft: 4 }}>复制</a>
                                    </CopyToClipboard>
                                </td>
                                <td>创建时间</td>
                                <td>{utils.formatDateTime(data.gmtCreate)}</td>
                            </tr>
                            <tr>
                                <td>用户名</td>
                                <td>
                                    {data.dbUserName}
                                    <CopyToClipboard key="copy" text={data.dbUserName}
                                        onCopy={this.copyOk}>
                                        <a style={{ marginLeft: 4 }}>复制</a>
                                    </CopyToClipboard>
                                </td>
                                <td>表数量</td>
                                <td>{data.tableNum}</td>
                            </tr>
                            <tr>
                                <td>创建者</td>
                                <td>{data.createUserName}</td>
                                <td>物理存储量</td>
                                <td>{utils.convertBytes(data.size)}</td>
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
                                padding: '0'
                            }
                        }
                        title={
                            <Search
                                placeholder="输入用户名搜索"
                                style={{ width: 160 }}
                                onSearch={(value: any) => this.loadDBUsers({ name: value })}
                            />
                        }
                        extra={
                            <Button
                                type="primary"
                                onClick={() => {
                                    this.setState({
                                        visibleAddUser: true
                                    })
                                }}
                                style={{ marginRight: 8 }}
                            >
                                添加用户
                            </Button>
                        }
                    >
                        <Table
                            className="m-table bd db-users"
                            rowKey="id"
                            columns={ this.initColumns() }
                            dataSource={ userList.data }
                            loading={ loading === 'loading' }
                            pagination={ false }
                        />
                        <Pagination
                            {...pagination}
                            onChange={(current: any) => {
                                this.loadDBUsers({
                                    currentPage: current,
                                    name: undefined
                                })
                            }}
                        />
                    </Card>
                </Row>
                <AddUserModal
                    user={user}
                    roles={userRoles}
                    myRoles={myRoles}
                    userList={usersNotInDB}
                    initialData={data}
                    onSubmit={this.addUser}
                    onSearch={this.debounceSearch}
                    onCancel={() => {
                        this.setState({
                            visibleAddUser: false
                        })
                    }}
                    visible={this.state.visibleAddUser}
                />
                <Modal
                    title="编辑角色"
                    visible={this.state.visibleEditRole}
                    onOk={this.onEditUserRole}
                    onCancel={() => {
                        this.setState({
                            visibleEditRole: false
                        })
                    }}
                >
                    <EditUserRole
                        user={selectedItem}
                        roles={userRoles}
                        myRoles={myRoles}
                        loginUser={user}
                        wrappedComponentRef={(e: any) => { this._eidtRoleForm = e }}
                        app={MY_APPS.ANALYTICS_ENGINE}
                    />
                </Modal>
                <UpdateDBModal
                    defaultData={data}
                    visible={this.state.visibleResetPwd}
                    onCancel={() => {
                        this.setState({
                            visibleResetPwd: false
                        })
                    }}
                />
            </div>
        )
    }
}

export default DatabaseDetail;
