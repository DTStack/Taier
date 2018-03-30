import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { 
    Select, Table, Card,
    Button, Tabs, Modal, 
    Popconfirm, message
} from 'antd'
import { Link } from 'react-router'

import utils from 'utils'
import { MY_APPS } from 'consts'
import { hasProject } from 'funcs'

import Api from '../../../api'
import AppTabs from '../../../components/app-tabs'

import MemberForm from './form'
import EditMemberRoleForm from './editRole'

const Option = Select.Option
const TabPane = Tabs.TabPane

class AdminUser extends Component {

    state = {
        active: '',
        loading: 'success',

        users: {
            data: [],
        },

        projects: [],
        selectedProject: '',
        notProjectUsers: [],
        roles: [],
        editTarget: '',

        visible: false,
        visibleEditRole: false,
    }

    componentDidMount() {
        const { apps } = this.props
        
        if (apps && apps.length > 0 ) {
            const initialApp = utils.getParameterByName('app');
            const defaultApp = apps.find(app => app.default);
            const appKey = initialApp || defaultApp.id;
    
            this.setState({ active: appKey }, this.loadData)
        }
    }

    loadData = () => {
        const { active, selectedProject } = this.state;
        const params = {
            pageSize: 10,
            currentPage: 1,
        }
        if (!selectedProject && hasProject(active)) {
            this.getProjects(active)
        } else if (!selectedProject && !hasProject(app)) {
            this.loadUsers(active, params);
            this.loadRoles(active, params);
        } else {
            params.projectId = selectedProject
            this.loadUsers(active, params);
            this.loadRoles(active, params);
        }
    }

    loadUsers = (app, params) => {
        const ctx = this
        this.setState({ loading: true })
        Api.queryUser(app, params).then((res) => {
            if (res.code === 1) {
                ctx.setState({ users: res.data, loading: false })
            }
        })
    }

    loadRoles = (app, params) => {
        const ctx = this
        Api.queryRole(app, params).then((res) => {
            if (res.code === 1) {
                ctx.setState({ roles: res.data && res.data.data })
            }
        })
    }

    getProjects = (app) => {
        const ctx = this
        Api.getProjects(app).then((res) => {
            if (res.code === 1) {
                ctx.setState({ projects: res.data })
                const selectedProject = res.data[0].id
                this.setState({
                    selectedProject 
                }, this.loadData)
            }
        })
    }

    loadUsersNotInProject = () => {
        const { active, selectedProject } = this.state;
        const params = {}
        if (hasProject(active)) {
            params.projectId = selectedProject
        }
        Api.loadUsersNotInProject(active, params).then((res) => {
            if (res.code === 1) {
                this.setState({ notProjectUsers: res.data })
            }
        })
    }

    addMember = () => {
        const ctx = this
        const { active, selectedProject } = this.state
        const form = this.memberForm.props.form
        const projectRole = form.getFieldsValue()
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
                        message.success('添加项目成员成功!')
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
        const { editTarget, active, selectedProject } = this.state
        const memberRole = ctx.eidtRoleForm.props.form.getFieldsValue()
        
        const params = {
            targetUserId: editTarget.userId,
            roleIds: memberRole.roleIds, // 3-管理员，4-普通成员
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

    handleTableChange = (pagination) => {
        this.loadUsers(pagination.current)
    }

    onPaneChange = (key) => {
        this.setState({
            active: key,
        }, this.loadData)
    }

    onProjectSelect = (value) => {
        this.setState({
            selectedProject: value
        }, this.loadData)
    }

    initAddMember = () => {
        const { params } = this.props
        this.loadUsersNotInProject();
        this.setState({ visible: true })
    }

    initColums = () => {
        const ctx = this;

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
            title: '姓名',
            dataIndex: 'user.userName',
            key: 'userName',
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
            dataIndex: 'user.gmtCreate',
            key: 'gmtCreate',
            render(time) {
                return utils.formatDateTime(time);
            }
        }, {
            title: '操作',
            dataIndex: 'id',
            key: 'id',
            render(id, record) {
                return <span>
                    <a onClick={() => {
                        ctx.setState({ 
                            visibleEditRole: true,
                            editTarget: record
                        })
                    }}>编辑</a>
                    <span className="ant-divider" />
                    <Popconfirm
                        title="确认将该成员从项目中移除？"
                        okText="确定" cancelText="取消"
                        onConfirm={() => { ctx.removeUserFromProject(record) }}
                        >
                        <a>删除</a>
                    </Popconfirm>
                </span>
            }
        }]
    }

    renderTitle = () => {

        const { projects, active, selectedProject } = this.state;

        const projectOpts = projects && projects.map(project => 
            <Option value={project.id} key={project.id}>
                { project.projectAlias }
            </Option>
        )

        const title = hasProject(active) && (
            <span
                style={{ marginTop: '10px', position: 'relative' }}
            >
                选择项目：
                <Select
                    showSearch
                    value={ selectedProject }
                    style={ { width: 200 } }
                    placeholder="按项目名称搜索"
                    optionFilterProp="name"
                    onSelect={ this.onProjectSelect }
                >  
                  { projectOpts }
                </Select>
            </span>
        )

        return title
    }

    renderPane = () => {
        const { apps } = this.props
        const { users, loading, active } = this.state;

        const extra = active === MY_APPS.RDOS && (
            <Button 
                style={{marginTop: '10px'}}
                type="primary" 
                onClick={this.initAddMember}>
                添加项目成员
            </Button>
        )

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
                    dataSource={ users.data || [] } 
                />
            </Card>
        )
    }

    render() {
        const { apps } = this.props

        const { 
            visible, roles, notProjectUsers,
            visibleEditRole, editTarget
        } = this.state

        const content = this.renderPane();

        return (
            <div className="user-admin">
                <h1 className="box-title">用户管理</h1>
                <div className="box-2 m-card" style={{height: '785px'}}>
                    <AppTabs 
                        apps={apps} 
                        activeKey={this.state.active}
                        content={content}
                        onPaneChange={this.onPaneChange} 
                    />
                </div>
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

export default AdminUser