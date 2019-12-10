import * as React from 'react'
import { connect } from 'react-redux'
import {
    Table, Button, Card, Input,
    message, Modal, Popconfirm
} from 'antd'

import utils from 'utils'
import Api from '../../../api/project'
import MemberForm from './form'
import EditMemberRoleForm from './editRole'

import * as ProjectAction from '../../../actions/project'

const Search = Input.Search

class ProjectMember extends React.Component<any, any> {
    memberForm: any;
    eidtRoleForm: any;
    state: any = {
        users: {
            data: []
        },
        roles: [],
        notProjectUsers: [],
        editTarget: '',
        loading: false,
        visible: false,
        current: 1,
        visibleEditRole: false
    }

    componentDidMount () {
        this.search()
        this.loadRoles()
        this.loadUsersNotInProject()
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.search(project.id)
            this.loadRoles()
        }
    }

    initAddMember = () => {
        this.setState({ visible: true })
    }

    removeUserFromProject = (member: any) => {
        const ctx = this
        const { project, dispatch } = this.props
        Api.removeProjectUser({
            targetUserId: member.userId,
            projectId: project.id
        }).then((res: any) => {
            if (res.code === 1) {
                ctx.search();
                dispatch(ProjectAction.getProject(project.id))
                message.success('移出成员成功!')
            }
        })
    }

    search = (projectId?: any) => {
        const { name, current } = this.state;
        const { project } = this.props;
        const params: any = {
            projectId: projectId || project.id,
            pageSize: 10,
            currentPage: current || 1,
            name: name || undefined
        };
        this.loadUsers(params)
    }

    loadUsers = (params: any) => {
        const ctx = this
        this.setState({ loading: true })
        if (params.projectId === 0) return;
        Api.getProjectUsers(params).then((res: any) => {
            if (res.code === 1) {
                ctx.setState({ users: res.data, loading: false })
            }
        })
    }

    loadRoles = (page?: any) => {
        const ctx = this
        Api.getRoleList({
            currentPage: page || 1
        }).then((res: any) => {
            if (res.code === 1) {
                ctx.setState({ roles: res.data && res.data.data })
            }
        })
    }

    addMember = () => {
        const ctx = this
        const { notProjectUsers } = this.state;
        const { project, dispatch } = this.props
        const form = this.memberForm.props.form
        const projectRole = form.getFieldsValue()

        // 塞入要添加的用户列表
        const targetUsers: any = [];
        const uids = projectRole.targetUserIds || [];
        for (let i = 0; i < uids.length; i++) {
            const user = notProjectUsers.find((u: any) => `${u.userId}` === uids[i])
            if (user) {
                targetUsers.push(user);
            }
        }
        projectRole.targetUsers = targetUsers;

        projectRole.projectId = project.id
        form.validateFields((err: any) => {
            if (!err) {
                Api.addProjectUser(projectRole).then((res: any) => {
                    if (res.code === 1) {
                        ctx.setState({ visible: false }, () => {
                            form.resetFields()
                        })
                        ctx.search();
                        dispatch(ProjectAction.getProject(project.id))
                        message.success('添加成员成功!')
                    }
                })
            }
        });
    }

    updateMemberRole = (item: any) => {
        const ctx = this
        const { editTarget } = this.state
        const { project } = this.props

        const memberRole = ctx.eidtRoleForm.props.form.getFieldsValue()
        Api.updateUserRole({
            projectId: project.id,
            targetUserId: editTarget.userId,
            roleIds: memberRole.roleIds // 3-管理员，4-普通成员
        }).then((res: any) => {
            if (res.code === 1) {
                message.success('设置成功！')
                ctx.setState({ visibleEditRole: false })
                ctx.search()
            }
        })
    }

    loadUsersNotInProject = () => {
        const params: any = {
            userName: '',
            projectId: this.props.project.id
        }

        Api.searchUICUsers(params).then((res: any) => {
            if (res.code === 1) {
                this.setState({ notProjectUsers: res.data })
            }
        })
    }

    searchUser = (query: any) => {
        this.setState({
            current: 1,
            name: query
        }, this.search)
    }

    handleTableChange = (pagination: any) => {
        this.setState({
            current: pagination.current
        }, this.search)
    }

    onCancel = () => {
        this.setState({
            visible: false,
            visibleEditRole: false
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
        return [{
            title: '账号',
            dataIndex: 'user',
            key: 'userName',
            render: (text: any, record: any) => {
                return record.user ? record.user.userName : ''
            }
        }, {
            title: '邮箱',
            dataIndex: 'user',
            key: 'email',
            render: (text: any, record: any) => {
                return record.user ? record.user.email : ''
            }
        }, {
            title: '手机号',
            dataIndex: 'user',
            key: 'phoneNumber',
            render: (text: any, record: any) => {
                return record.user ? record.user.phoneNumber : ''
            }
        }, {
            title: '角色',
            dataIndex: 'role',
            key: 'role',
            render: (text: any, record: any) => {
                const roles = record.roles ? record.roles.map(
                    (role: any) => role && role.roleName
                ) : []
                return roles.join(',')
            }
        }, {
            title: '加入时间',
            dataIndex: 'gmtCreate',
            key: 'gmtCreate',
            render: (text: any, record: any) => utils.formatDateTime(record.gmtCreate)
        }, {
            title: '操作',
            width: 100,
            key: 'operation',
            render: (text: any, record: any) => {
                const setRemove = record.roles &&
                record.roles.find((role: any) => role && role.id !== 2) ? (
                        <Popconfirm
                            title="确认将该成员从项目中移除？"
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
                            })
                        }
                        }>编辑角色</a></span>
                    </span>
                )
            }
        }]
    }

    render () {
        const {
            visible, users, roles,
            notProjectUsers, visibleEditRole, editTarget
        } = this.state;

        const { project } = this.props

        const pagination: any = {
            total: users.totalCount,
            defaultPageSize: 10
        };

        const title = (
            <div >
                <Search
                    placeholder="搜索账号"
                    style={{ width: 200 }}
                    onSearch={this.searchUser}
                />&nbsp;&nbsp;
            </div>
        )

        const extra = <Button
            type="primary"
            style={{ marginTop: 10 }}
            onClick={this.initAddMember}
        >
                添加成员
        </Button>
        
        return (
            <div>
                <h1 className="box-title" style={{ paddingTop: '0' }}>
                    {utils.textOverflowExchange(project.projectName, 60)}
                    <span className="box-sub-title">&nbsp;描述：{utils.textOverflowExchange(project.projectDesc, 50)}</span>
                </h1>
                <div className="box-2 m-card">
                    <Card
                        noHovering
                        bordered={false}
                        loading={false}
                        title={title}
                        extra={extra}
                        className='full-screen-table-70'
                    >
                        <Table
                            rowKey="userId"
                            className="dt-ant-table dt-ant-table--border"
                            pagination={pagination}
                            onChange={this.handleTableChange}
                            loading={this.state.loading}
                            columns={this.initColumns()}
                            dataSource={users.data}
                        />
                    </Card>
                </div>
                <Modal
                    title="添加项目成员"
                    wrapClassName="vertical-center-modal"
                    visible={visible}
                    onOk={this.addMember}
                    onCancel={this.onCancel}
                    maskClosable={false}
                >
                    <MemberForm
                        wrappedComponentRef={(e: any) => { this.memberForm = e }}
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
                        wrappedComponentRef={(e: any) => { this.eidtRoleForm = e }}
                    />
                </Modal>
            </div>
        )
    }
}
export default connect((state: any) => {
    return {
        user: state.user,
        project: state.project
    }
})(ProjectMember)
