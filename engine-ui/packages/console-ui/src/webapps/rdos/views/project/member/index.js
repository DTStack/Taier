import React, { Component } from 'react'
import { connect } from 'react-redux'
import {
    Table, Button, Card, Input,
    message, Modal, Popconfirm,
 } from 'antd'

 import utils from 'utils'
 import Api from '../../../api'
 import MemberForm from './form'
 import EditMemberRoleForm from './editRole'
 
 import * as UserAction from '../../../store/modules/user'

const Search = Input.Search
// const is_ADMIN = 3 // 管理员
// const is_NORMAL = 4 // 普通

class ProjectMember extends Component {

    state = {
        users: {
            data: [],
        },
        roles: [],
        editTarget: '',
        loading: false,
        visible: false,
        visibleEditRole: false,
    }

    componentDidMount() {
        this.loadUsers()
        this.loadRoles()
    }

    componentWillReceiveProps(nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadUsers()
            this.loadRoles()
        }
    }

    initAddMember = () => {
        const { dispatch, params } = this.props
        dispatch(UserAction.getNotProjectUsers({ projectId: params.pid }))
        this.setState({ visible: true })
    }

    removeUserFromProject = (member) => {
        const ctx = this
        Api.removeProjectUser({
            targetUserId: member.userId,
        }).then((res) => {
            if (res.code === 1) {
                ctx.loadUsers()
                message.success('移出成员成功!')
            }
        })
    }

    loadUsers = (page) => {
        const ctx = this
        this.setState({ loading: true })
        const { params } = this.props
        Api.getProjectUsers({
            projectId: params.pid,
            pageSize: 10,
            currentPage: page || 1,
        }).then((res) => {
            if (res.code === 1) {
                ctx.setState({ users: res.data, loading: false })
            }
        })
    }

    loadRoles = (page) => {
        const ctx = this
        Api.getRoleList({
            currentPage: page || 1,
        }).then((res) => {
            if (res.code === 1) {
                ctx.setState({ roles: res.data && res.data.data })
            }
        })
    }

    addMember = () => {
        const ctx = this
        const form = this.memberForm.props.form
        const projectRole = form.getFieldsValue()
        form.validateFields((err) => {
            if (!err) {
                Api.addRoleUser(projectRole).then((res) => {
                    if (res.code === 1) {
                        ctx.setState({ visible: false }, () => {
                            form.resetFields()
                        })
                        ctx.loadUsers()
                        message.success('添加用户成功!')
                    }
                })
            }
        });
    }

    updateMemberRole = (item) => {
        const ctx = this
        const { editTarget } = this.state
        const memberRole = ctx.eidtRoleForm.props.form.getFieldsValue()
        Api.updateUserRole({
            targetUserId: editTarget.userId,
            roleIds: memberRole.roleIds, // 3-管理员，4-普通成员
        }).then((res) => {
            if (res.code === 1) {
                message.success('设置成功！')
                ctx.setState({ visibleEditRole: false })
                ctx.loadUsers()
            }
        })
    }

    searchUser = (query) => {
        const ctx = this
        const { params } = this.props
        this.setState({ loading: true })
        Api.getProjectUsers({
            name: query,
            projectId: params.pid,
        }).then((res) => {
            if (res.code === 1) {
                ctx.setState({ users: res.data, loading: false })
            }
        })
    }

    handleTableChange = (pagination) => {
        this.loadUsers(pagination.current)
    }

    onCancel = () => {
        this.setState({
            visible: false,
            visibleEditRole: false,
        }, () => {
            if (this.eidtRoleForm) {
                this.eidtRoleForm.props.form.resetFields()
            }
            if (this.memberForm) {
                this.memberForm.props.form.resetFields()
            }
        })
    }

    initColumns = () => {
        const user = this.props.user
        return [{
            title: '账号',
            dataIndex: 'user',
            key: 'userName',
            render: (text, record) => {
                return record.user ? record.user.userName : ''
            },
        }, {
            title: '邮箱',
            dataIndex: 'user',
            key: 'email',
            render: (text, record) => {
                return record.user ? record.user.email : ''
            },
        }, {
            title: '手机号',
            dataIndex: 'user',
            key: 'phoneNumber',
            render: (text, record) => {
                return record.user ? record.user.phoneNumber : ''
            },
        }, {
            title: '角色',
            dataIndex: 'role',
            key: 'role',
            render: (text, record) => {
                const roles = record.roles ? record.roles.map(
                    role => role.roleName
                ) : []
                return roles.join(',')
            },
        }, {
            title: '加入时间',
            dataIndex: 'gmtCreate',
            key: 'gmtCreate',
            render: (text, record) => utils.formatDateTime(record.user.gmtCreate),
        }, {
            title: '操作',
            width: 100,
            key: 'operation',
            render: (text, record) => {
                const setRemove = record.roles && 
                record.roles.find((role => role.id !== 2 )) ? (
                <Popconfirm
                  title="确认将该用户从项目中移除？"
                  okText="确定" cancelText="取消"
                  onConfirm={() => { this.removeUserFromProject(record) }}
                >
                    <a>移出项目</a>
                </Popconfirm>) : ''
                return (
                    <span key={record.id}>
                        <span>{setRemove}</span>
                        <br/>
                        <span><a onClick={() => {
                            this.setState({ 
                                visibleEditRole: true,
                                editTarget: record
                             })}
                        }>编辑角色</a></span>
                    </span>
                )
            },
        }]
    }

    render() {
        const { 
            visible, users, roles, 
            visibleEditRole, editTarget 
        } = this.state
        const { project, notProjectUsers } = this.props

        const pagination = {
            total: users.totalCount,
            defaultPageSize: 10,
        };

        const title = (
            <div>
                <Search
                    placeholder="搜索姓名"
                    style={{ width: 200 }}
                    onSearch={this.searchUser}
                />&nbsp;&nbsp;
            </div>
        )

        const extra = <Button type="primary" onClick={this.initAddMember}>添加成员</Button>

        return (
            <div className="project-member">
               <article className="section">
                    <h1 className="title black" style={{paddingTop: '0'}}>
                        {project.projectName}
                        <span className="desc">&nbsp;描述：{project.projectDesc}</span>
                    </h1>
                    <Card title={title} extra={extra}>
                        <Table
                            rowKey="userId"
                            className="section-border"
                            pagination={pagination}
                            onChange={this.handleTableChange}
                            loading={this.state.loading}
                            columns={this.initColumns()}
                            dataSource={users.data}
                        />
                    </Card>
                </article>
                <Modal
                  title="添加项目成员"
                  wrapClassName="vertical-center-modal"
                  visible={visible}
                  onOk={this.addMember}
                  onCancel={this.onCancel}
                >
                    <MemberForm
                      wrappedComponentRef={(e) => { this.memberForm = e }}
                      roles={roles}
                      notProjectUsers={notProjectUsers}
                    />
                </Modal>
                <Modal
                  title="设置用户角色"
                  wrapClassName="vertical-center-modal"
                  visible={visibleEditRole}
                  onOk={this.updateMemberRole}
                  onCancel={this.onCancel}
                >
                    <EditMemberRoleForm
                      user={editTarget}
                      roles={roles}
                      wrappedComponentRef={(e) => { this.eidtRoleForm = e }}
                    />
                </Modal>
            </div>
        )
    }
}
export default connect((state) => {
    return {
        notProjectUsers: state.notProjectUsers,
        user: state.user,
        project: state.project,
    }
})(ProjectMember)
