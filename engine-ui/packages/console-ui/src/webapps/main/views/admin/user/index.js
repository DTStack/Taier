import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { 
    Select, Table, Card,
    Button, Tabs, Modal 
} from 'antd'
import { Link } from 'react-router'

import utils from 'utils'
import { MY_APPS } from 'consts'

import Api from '../../../api'
import AppTabs from '../../../components/app-tabs'

import MemberForm from './form'
import EditMemberRoleForm from './editRole'

const Option = Select.Option
const TabPane = Tabs.TabPane

class AdminUser extends Component {

    state = {
        active: '',
        data: '',
        users: {
            data: [],
        },
        projects: [],
        project: '',
        notProjectUsers: [],
        loading: 'success',
        roles: [],
        visible: false,
        editTarget: '',
        visibleEditRole: false,
    }

    componentDidMount() {
        const { apps } = this.props
        if (apps && apps.length > 0 ) {
            const key = apps[1].id;
            this.setState({
                active: key
            })
            this.loadUsers(key);
            this.loadRoles(key);
        }
    }

    loadUsers = (app, page) => {
        const ctx = this
        this.setState({ loading: true })
        const { params } = this.props
        Api.queryUser({
            projectId: params.pid,
            pageSize: 10,
            currentPage: page || 1,
        }).then((res) => {
            if (res.code === 1) {
                ctx.setState({ users: res.data, loading: false })
            }
        })
    }

    loadRoles = (app, page) => {
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
        })
        this.loadData(key)
    }

    initColums = () => {
        return [{
            title: '账号',
            dataIndex: 'user.userName',
            key: 'account',
            render(text, record) {
                return <Link to={`message/detail/${record.id}`}>{text}</Link>
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
            dataIndex: 'roles',
            key: 'roles',
            render(roles) {
                return '-'
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
                    <a>编辑</a>
                    <span className="ant-divider" />
                    <a>删除</a>
                </span>
            }
        }]
    }

    renderPane = () => {
        const { data, loading, projects } = this.state;

        const projectOpts = projects && projects.map(project => 
            <Option value={project.id} key={project.id}>
                { project.name }
            </Option>
        )

        const title = (
            <span
                style={{ marginTop: '10px' }}
            >
                选择项目：
                <Select
                    showSearch
                    style={{ width: 200 }}
                    placeholder="按项目名称搜索"
                    optionFilterProp="name"
                >  
                  { projectOpts }
                </Select>
            </span>
        )

        const extra = (
            <Button 
                style={{marginTop: '10px'}}
                type="primary" 
                onClick={() => { this.setState({ visible: true }) }}>
                添加用户
            </Button>
        )

        return (
            <Card 
                bordered={false}
                noHovering
                title={title} 
                extra={extra}
            >
                <Table 
                    rowKey="userId"
                    className="m-table"
                    columns={this.initColums()} 
                    onChange={this.handleTableChange}
                    loading={loading === 'loading'}
                    dataSource={ data ? data.data : [] } 
                />
            </Card>
        )
    }


    render() {
        const { apps } = this.props

        const { 
            visible, users, roles, notProjectUsers,
            visibleEditRole, editTarget, project
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