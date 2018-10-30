import React, { Component } from 'react';
import { assign } from 'lodash';
import { connect } from 'react-redux';
import {
    Select, Table, Card,
    Button, Modal, Input,
    Popconfirm, message
} from 'antd'

import utils from 'utils'
import { hasProject } from 'funcs'

import { MY_APPS, RDOS_ROLE, APP_ROLE } from '../../../consts'
import Api from '../../../api'
import AppTabs from '../../../components/app-tabs'

import MemberForm from './form'
import EditMemberRoleForm from './editRole'

const Option = Select.Option
const Search = Input.Search;

@connect(state => {
    return {
        user: state.user,
    }
})
class AdminUser extends Component {

    state = {
        active: '',
        loading: 'success',

        users: {
            data: [],
        },

        projects: [],
        selectedProject: null,
        notProjectUsers: [],
        roles: [],
        editTarget: '',

        currentPage: 1,
        visible: false,
        visibleEditRole: false,

        searchName: undefined,
    }

    componentDidMount() {
        const { apps } = this.props

        if (apps && apps.length > 0) {
            const initialApp = utils.getParameterByName('app');

            const defaultApp = apps.find(app => app.default);
            const appKey = initialApp || defaultApp.id;

            this.setState({ active: appKey }, () => {
                this.loadData();
            })
        }
    }

    loadData = () => {
        const { active, selectedProject, currentPage, projects } = this.state;
        const params = {
            pageSize: 10,
            currentPage: currentPage,
        }
        if (projects.length === 0 && hasProject(active)) {
            this.getProjects(active);
        } else if (projects.length === 0 && !hasProject(app)) {
            this.loadUsers(active, params);
            this.loadRoles(active, assign(params, {
                currentPage: 1,
            }));
        } else {
            params.projectId = selectedProject;
            this.loadUsers(active, params);
            this.loadRoles(active, assign(params, {
                currentPage: 1,
            }));
        }
        this.getOwnRole(active, params);
    }
    
    getOwnRole(app, params) {
        const queryParams = {
            ...params,
            currentPage: 1,
            pageSize: 1,
            name: this.props.user.userName
        }
        Api.queryUser(app, queryParams).then((res) => {
            const roles = res.data && res.data.data[0].roles;
            let isVisitor = false,
                isProjectAdmin = false,
                isProjectOwner = false;
            for (let role of roles) {
                const roleValue = role.roleValue;
                switch (app) {
                    case MY_APPS.RDOS: {
                        if (roleValue == RDOS_ROLE.VISITOR) {
                            isVisitor = true
                        } else if (roleValue == RDOS_ROLE.PROJECT_ADMIN) {
                            isProjectAdmin = true;
                        } else if (roleValue == RDOS_ROLE.PROJECT_OWNER) {
                            isProjectOwner = true;
                        }
                    }
                    case MY_APPS.API:
                    case MY_APPS.LABEL:
                    case MY_APPS.DATA_QUALITY: {
                        if (roleValue == APP_ROLE.VISITOR) {
                            isVisitor = true
                        } else if (roleValue == APP_ROLE.ADMIN) {
                            isProjectAdmin = true;
                        }
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
        })
    }
    loadUsers = (app, params) => {
        const { searchName } = this.state;
        const ctx = this
        this.setState({ loading: true })
        const queryParams = { ...params }//复制一份
        queryParams.name = searchName
        Api.queryUser(app, queryParams).then((res) => {
            ctx.setState({ users: res.data, loading: false })
        })
    }

    loadRoles = (app, params) => {
        const ctx = this;
        Api.queryRole(app, params).then((res) => {
            ctx.setState({ roles: res.data && res.data.data })
        })
    }

    getProjects = (app) => {
        const ctx = this
        Api.getProjects(app).then((res) => {
            if (res.code === 1) {
                ctx.setState({ projects: res.data })
                const selectedProject = utils.getCookie('project_id') || res.data[0].id
                this.setState({
                    selectedProject
                }, this.loadData)
            }
        })
    }

    loadUsersNotInProject = (userName) => {
        const { active, selectedProject } = this.state;
        const params = {
            userName,
        }
        if (hasProject(active)) {
            params.projectId = selectedProject
        }
        Api.loadUsersNotInProject(active, params).then((res) => {
            this.setState({ notProjectUsers: res.data })
        })
    }

    addMember = () => {
        const ctx = this
        const { active, selectedProject, notProjectUsers } = this.state
        const form = this.memberForm.props.form
        const projectRole = form.getFieldsValue()

        // 塞入要添加的用户列表
        const targetUsers = [];
        const uids = projectRole.targetUserIds;
        for (let i = 0; i < uids.length; i++) {
            const user = notProjectUsers.find(u => `${u.userId}` === uids[i])
            if (user) {
                targetUsers.push(user);
            }
        }
        projectRole.targetUsers = targetUsers;

        form.validateFields((err) => {
            if (!err) {
                if (hasProject(active)) {
                    projectRole.projectId = selectedProject
                }
                Api.addRoleUser(active, projectRole).then((res) => {
                    if (res.code === 1) {
                        ctx.setState({ visible: false }, () => {
                            form.resetFields()
                        })
                        ctx.loadData()
                        message.success('添加成员成功!')
                    }
                })
            }
        });
    }

    removeUserFromProject = (member) => {
        const ctx = this
        const { active, selectedProject } = this.state
        const params = {
            targetUserId: member.userId,
        }
        if (hasProject(active)) {
            params.projectId = selectedProject
        }
        Api.removeProjectUser(active, params).then((res) => {
            if (res.code === 1) {
                ctx.loadData()
                message.success('移出项目成员成功!')
            }
        })
    }

    updateMemberRole = (item) => {
        const ctx = this
        const { editTarget, active, selectedProject } = this.state;

        const memberRole = ctx.eidtRoleForm.props.form.getFieldsValue()

        if (memberRole.roleIds.length === 0) {
            message.error('用户角色不可为空！');
            return;
        }

        const params = {
            targetUserId: editTarget.userId,
            roleIds: memberRole.roleIds,
        }

        if (hasProject(active)) {
            params.projectId = selectedProject
        }

        Api.updateUserRole(active, params).then((res) => {
            if (res.code === 1) {
                message.success('设置成功！')
                ctx.setState({ visibleEditRole: false })
                ctx.loadData()
            }
        })
    }

    onRoleIdsChange = (roleIds) => {
        this.setState({
            roleIds,
        })
    }

    onCancel = () => {
        this.setState({
            visible: false,
            visibleEditRole: false,
            editTarget: '',
            notProjectUsers: [],
        }, () => {
            if (this.eidtRoleForm) {
                this.eidtRoleForm.props.form.resetFields()
            }
            if (this.memberForm) {
                this.memberForm.props.form.resetFields()
            }
        })
    }

    handleTableChange = (pagination) => {
        this.setState({
            currentPage: pagination.current,
        }, this.loadData)
    }

    onPaneChange = (key) => {
        this.setState({
            active: key,
            currentPage: 1,
            roleIds: [],
            notProjectUsers: [],
            searchName: undefined,
        }, this.loadData)
    }

    onProjectSelect = (value) => {
        this.setState({
            selectedProject: value,
            currentPage: 1,
        }, this.loadData)
    }

    initAddMember = () => {
        const { params } = this.props
        this.loadUsersNotInProject();
        this.setState({ visible: true })
    }

    initColums = () => {
        const ctx = this;
        const hideDel = this.state.active !== MY_APPS.RDOS;
        return [{
            title: '账号',
            dataIndex: 'user.userName',
            key: 'account',
            render(text, record) {
                return <a onClick={() => {
                    ctx.setState({
                        visibleEditRole: true,
                        editTarget: record
                    })
                }}>{text}</a>
            },
        }, {
            title: '邮箱',
            dataIndex: 'user.email',
            key: 'email',
        }, {
            title: '手机号',
            dataIndex: 'user.phoneNumber',
            key: 'phoneNumber',
        }, {
            title: '角色',
            width: 120,
            dataIndex: 'roles',
            key: 'roles',
            render(roles) {
                const roleNames = roles.map(role => role && role.roleName)
                return roleNames.join(',')
            }
        }, {
            title: '加入时间',
            dataIndex: 'gmtCreate',
            key: 'gmtCreate',
            render(time) {
                return utils.formatDateTime(time);
            }
        }, {
            title: '操作',
            dataIndex: 'id',
            width: 135,
            key: 'id',
            render(id, record) {
                return <span>
                    <a onClick={() => {
                        ctx.setState({
                            visibleEditRole: true,
                            editTarget: record
                        })
                    }}>编辑角色</a>
                    {
                        hideDel ? '' : <span>
                            <span className="ant-divider" />
                            <Popconfirm
                                title="确认将该成员从项目中移除？"
                                okText="确定" cancelText="取消"
                                onConfirm={() => { ctx.removeUserFromProject(record) }}
                            >
                                <a>移出项目</a>
                            </Popconfirm>
                        </span>
                    }
                </span>
            }
        }]
    }

    searchProjectUser = (user) => {
        this.setState({
            searchName: user
        }, this.loadData)
    }

    searchNameChange = (e) => {
        this.setState({
            searchName: e.target.value
        })
    }

    renderTitle = () => {

        const { projects, active, selectedProject, searchName } = this.state;

        const projectOpts = projects && projects.map(project =>
            <Option value={`${project.id}`} key={`${project.id}`}>
                {project.projectAlias}
            </Option>
        )

        const title = (
            <span>
                {
                    hasProject(active) && (
                        <span>
                            选择项目：
                            <Select
                                showSearch
                                value={`${selectedProject}`}
                                style={{ width: 200, marginRight: 10 }}
                                placeholder="按项目名称搜索"
                                optionFilterProp="name"
                                onSelect={this.onProjectSelect}
                                filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                            >
                                {projectOpts}
                            </Select>
                        </span>
                    )
                }
                <Search
                    placeholder="请输入要搜索的账号"
                    value={searchName}
                    onChange={e => this.searchNameChange(e)}
                    style={{ width: 200, marginTop: 10 }}
                    onSearch={value => this.searchProjectUser(value)}
                />
            </span>
        )

        return title
    }

    renderPane = () => {
        const { apps } = this.props
        const { users, loading, active } = this.state;

        const extra = (
            <Button
                style={{ marginTop: '10px' }}
                type="primary"
                onClick={this.initAddMember}>
                添加用户
            </Button>
        )

        const pagination = {
            total: users.totalCount,
            defaultPageSize: 10,
            current: users.currentPage,
        };

        return (
            <Card
                bordered={false}
                noHovering
                title={this.renderTitle()}
                extra={extra}
            >
                <Table
                    rowKey="userId"
                    className="m-table"
                    columns={this.initColums()}
                    onChange={this.handleTableChange}
                    loading={loading === 'loading'}
                    pagination={pagination}
                    dataSource={users.data || []}
                />
            </Card>
        )
    }

    render() {
        const { apps, user } = this.props

        const {
            visible, roles, notProjectUsers,
            visibleEditRole, editTarget, active, myRoles
        } = this.state

        const content = this.renderPane();
        return (
            <div className="user-admin">
                <h1 className="box-title">用户管理</h1>
                <div className="box-2 m-card" style={{ height: '785px' }}>
                    <AppTabs
                        apps={apps}
                        activeKey={active}
                        content={content}
                        onPaneChange={this.onPaneChange}
                    />
                </div>
                <Modal
                    title="添加用户"
                    wrapClassName="vertical-center-modal"
                    visible={visible}
                    onOk={this.addMember}
                    onCancel={this.onCancel}
                >
                    <MemberForm
                        myRoles={myRoles}
                        wrappedComponentRef={(e) => { this.memberForm = e }}
                        roles={roles}
                        app={active}
                        user={user}
                        onSearchUsers={this.loadUsersNotInProject}
                        notProjectUsers={notProjectUsers}
                    />
                </Modal>
                <Modal
                    title="设置用户角色"
                    wrapClassName="vertical-center-modal"
                    visible={visibleEditRole}
                    onOk={this.updateMemberRole}
                    onCancel={this.onCancel}
                    width={Math.min(1000, Math.max(520, (400 + (roles ? roles.length * 60 : 0)))) + "px"}
                >
                    <EditMemberRoleForm
                        myRoles={myRoles}
                        user={editTarget}
                        app={active}
                        roles={roles}
                        loginUser={user}
                        wrappedComponentRef={(e) => { this.eidtRoleForm = e }}
                    />
                </Modal>
            </div>
        )
    }
}

export default AdminUser