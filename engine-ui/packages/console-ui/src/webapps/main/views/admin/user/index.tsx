import * as React from 'react';
import { assign, get } from 'lodash';
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

@(connect((state: any) => {
    return {
        user: state.user
    }
}) as any)
class AdminUser extends React.Component<any, any> {
    memberForm: any;
    eidtRoleForm: any;

    state: any = {
        active: '',
        loading: 'success',

        users: {
            data: []
        },

        projects: [],
        streamProjects: [],
        scienceProjects: [],
        selectedProject: undefined,
        streamSelectedProject: undefined,
        scienceSelectedProject: undefined,
        dataBase: [],
        selecteDatabase: undefined,
        notProjectUsers: [],
        roles: [],
        editTarget: '',

        currentPage: 1,
        visible: false,
        visibleEditRole: false,

        searchName: undefined
    }

    componentDidMount () {
        const { apps } = this.props

        if (apps && apps.length > 0) {
            const initialApp = utils.getParameterByName('app');

            const defaultApp = apps.find((app: any) => app.default);
            const appKey = initialApp || defaultApp.id;

            this.setState({ active: appKey }, () => {
                this.loadData();
            })
        }
    }
    hasDatabase (app: any) {
        return app === 'analyticsEngine';
    }
    isProjectExsit () {
        const { active, projects, streamProjects, scienceProjects } = this.state;
        let appMap = {
            [MY_APPS.RDOS]: projects,
            [MY_APPS.STREAM]: streamProjects,
            [MY_APPS.SCIENCE]: scienceProjects
        }
        return appMap[active] && appMap[active].length
    }
    /**
     * 这边加一个isGetProjectsBack，当是getProjects调用的时候，防止服务器返回一个空数组，而不断的重复调用
     */
    loadData = (isGetProjectsBack?: any, isGetDatabaseBack?: any) => {
        const { active, selectedProject, streamSelectedProject, scienceSelectedProject, currentPage, dataBase, selecteDatabase } = this.state;
        const params: any = {
            pageSize: 10,
            currentPage
        }
        const projectsExsit = this.isProjectExsit();
        const databaseExsit = (MY_APPS.ANALYTICS_ENGINE == active && dataBase.length);

        this.setState({
            users: {
                data: []
            }
        })

        if (!projectsExsit && hasProject(active) && !isGetProjectsBack) {
            this.getProjects(active);
        } else if (!databaseExsit && this.hasDatabase(active) && !isGetDatabaseBack) {
            this.getDatabase(active);
        } else if (MY_APPS.ANALYTICS_ENGINE == active) {
            params.databaseId = selecteDatabase;
            this.loadUsers(active, params);
            this.loadRoles(active, assign(params, {
                currentPage: 1
            }));
        } else {
            if (MY_APPS.RDOS == active) {
                params.projectId = selectedProject;
            } else if (MY_APPS.STREAM == active) {
                params.projectId = streamSelectedProject;
            } else if (MY_APPS.SCIENCE == active) {
                params.projectId = scienceSelectedProject;
            }

            this.loadUsers(active, params);
            this.loadRoles(active, assign(params, {
                currentPage: 1
            }));
        }

        this.getOwnRole(active, params);
    }

    getOwnRole (app: any, params: any) {
        const queryParams = {
            ...params,
            currentPage: 1,
            pageSize: 1,
            name: this.props.user.userName
        }

        Api.queryUser(app, queryParams).then((res: any) => {
            if (res.code != 1) {
                return;
            }

            // const roles = res.data && res.data.data[0].roles;
            const roles = res.data.data.length > 0 ? res.data.data[0].roles : [];
            let isVisitor = false;

            let isProjectAdmin = false;

            let isProjectOwner = false;

            for (const role of roles) {
                const roleValue = role.roleValue;

                switch (app) {
                    case MY_APPS.RDOS:
                    case MY_APPS.STREAM:
                    case MY_APPS.SCIENCE: {
                        if (roleValue == RDOS_ROLE.VISITOR) {
                            isVisitor = true
                        } else if (roleValue == RDOS_ROLE.PROJECT_ADMIN) {
                            isProjectAdmin = true;
                        } else if (roleValue == RDOS_ROLE.PROJECT_OWNER) {
                            isProjectOwner = true;
                        }
                        break;
                    }
                    case MY_APPS.API:
                    case MY_APPS.LABEL:
                    case MY_APPS.ANALYTICS_ENGINE:
                    case MY_APPS.DATA_QUALITY: {
                        if (roleValue == APP_ROLE.VISITOR) {
                            isVisitor = true
                        } else if (roleValue == APP_ROLE.ADMIN) {
                            isProjectAdmin = true;
                        }
                        break;
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
    loadUsers = (app: any, params: any) => {
        const { searchName } = this.state;
        const ctx = this

        this.setState({
            loading: true,
            users: {
                data: []
            }
        })
        const queryParams = { ...params }// 复制一份

        queryParams.name = searchName;
        Api.queryUser(app, queryParams).then((res: any) => {
            ctx.setState({
                loading: false
            })

            if (res.code == 1) {
                ctx.setState({ users: res.data })
            }
        })
    }

    loadRoles = (app: any, params: any) => {
        const ctx = this;

        Api.queryRole(app, params).then((res: any) => {
            if (res.code == 1) {
                ctx.setState({ roles: res.data && res.data.data })
            }
        })
    }

    // 获取数据库
    getDatabase = (app: any) => {
        const ctx = this

        Api.getDatabase(app).then((res: any) => {
            if (res.code === 1) {
                if (app == MY_APPS.ANALYTICS_ENGINE) {
                    ctx.setState({
                        dataBase: res.data,
                        selecteDatabase: res.data[0].id
                    }, this.loadData.bind(this, false, true))
                }
            }
        })
    }

    getProjects = (app: any) => {
        const ctx = this

        Api.getProjects(app).then((res: any) => {
            function getNotNullProject (projectId: any, data: any) {
                if (!data || !data.length) {
                    return null;
                }
                return data.find((p: any) => {
                    return p.id == projectId;
                }) ? projectId : null
            }
            if (res.code === 1) {
                let cookiesProject;

                /**
                 * 不同应用设置不同的state
                 */
                const projectId = get(res, 'data[0].id', null);
                if (app == MY_APPS.STREAM) {
                    cookiesProject = getNotNullProject(utils.getCookie('stream_project_id'), res.data)
                    ctx.setState({
                        streamProjects: res.data,
                        streamSelectedProject: cookiesProject || projectId
                    }, this.loadData.bind(this, true))
                } else if (app == MY_APPS.RDOS) {
                    cookiesProject = getNotNullProject(utils.getCookie('project_id'), res.data)
                    ctx.setState({
                        projects: res.data,
                        selectedProject: cookiesProject || projectId
                    }, this.loadData.bind(this, true))
                } else if (app == MY_APPS.SCIENCE) {
                    cookiesProject = getNotNullProject(utils.getCookie('science_project_id'), res.data)
                    ctx.setState({
                        scienceProjects: res.data,
                        scienceSelectedProject: cookiesProject || projectId
                    }, this.loadData.bind(this, true))
                }
            }
        })
    }

    loadUsersNotInProject = (userName?: any) => {
        const { active, selecteDatabase } = this.state;
        const params: any = {
            userName
        }

        if (hasProject(active)) {
            params.projectId = this.getProjectId(active);
        }

        if (this.hasDatabase(active)) {
            if (MY_APPS.ANALYTICS_ENGINE == active) {
                params.databaseId = selecteDatabase;
            }
        }

        Api.loadUsersNotInProject(active, params).then((res: any) => {
            this.setState({ notProjectUsers: res.data })
        })
    }

    addMember = () => {
        const ctx = this
        const { active, notProjectUsers, selecteDatabase } = this.state
        const form = this.memberForm.props.form
        const projectRole = form.getFieldsValue()

        // 塞入要添加的用户列表
        const targetUsers = [];
        const uids = projectRole.targetUserIds;

        for (let i = 0; i < uids.length; i++) {
            const user = notProjectUsers.find((u: any) => `${u.userId}` === uids[i])

            if (user) {
                targetUsers.push(user);
            }
        }

        projectRole.targetUsers = targetUsers;

        form.validateFields((err: any) => {
            if (!err) {
                if (hasProject(active)) {
                    projectRole.projectId = this.getProjectId(active);
                }

                if (this.hasDatabase(active)) {
                    if (active == MY_APPS.ANALYTICS_ENGINE) {
                        projectRole.databaseId = selecteDatabase
                    }
                }

                Api.addRoleUser(active, projectRole).then((res: any) => {
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
    getProjectId (active: any) {
        const { selectedProject, streamSelectedProject, scienceSelectedProject } = this.state;
        let map = {
            [MY_APPS.RDOS]: selectedProject,
            [MY_APPS.STREAM]: streamSelectedProject,
            [MY_APPS.SCIENCE]: scienceSelectedProject
        }
        return map[active];
    }
    removeUserFromProject = (member: any) => {
        const ctx = this
        const { active, selecteDatabase } = this.state
        const params: any = {
            targetUserId: member.userId
        }

        if (hasProject(active)) {
            params.projectId = this.getProjectId(active);
        }

        if (this.hasDatabase(active)) {
            if (active == MY_APPS.ANALYTICS_ENGINE) {
                params.databaseId = selecteDatabase
            }
        }

        Api.removeProjectUser(active, params).then((res: any) => {
            if (res.code === 1) {
                ctx.loadData()
                message.success('移出成员成功!')
            }
        })
    }

    updateMemberRole = (item: any) => {
        const ctx = this
        const { editTarget, active, selecteDatabase } = this.state;

        const memberRole = ctx.eidtRoleForm.props.form.getFieldsValue()

        if (memberRole.roleIds.length === 0) {
            message.error('用户角色不可为空！');

            return;
        }

        const params: any = {
            targetUserId: editTarget.userId,
            roleIds: memberRole.roleIds
        }

        if (hasProject(active)) {
            params.projectId = this.getProjectId(active);
        }

        if (this.hasDatabase(active)) {
            if (active == MY_APPS.ANALYTICS_ENGINE) {
                params.databaseId = selecteDatabase
            }
        }

        Api.updateUserRole(active, params).then((res: any) => {
            if (res.code === 1) {
                message.success('设置成功！')
                ctx.setState({ visibleEditRole: false })
                ctx.loadData()
            }
        })
    }

    onRoleIdsChange = (roleIds: any) => {
        this.setState({
            roleIds
        })
    }

    onCancel = () => {
        this.setState({
            visible: false,
            visibleEditRole: false,
            editTarget: '',
            notProjectUsers: []
        }, () => {
            if (this.eidtRoleForm) {
                this.eidtRoleForm.props.form.resetFields()
            }

            if (this.memberForm) {
                this.memberForm.props.form.resetFields()
            }
        })
    }

    handleTableChange = (pagination: any) => {
        this.setState({
            currentPage: pagination.current
        }, this.loadData)
    }

    onPaneChange = (key: any) => {
        this.setState({
            active: key,
            currentPage: 1,
            roleIds: [],
            notProjectUsers: [],
            projects: [],
            streamProjects: [],
            scienceProjects: [],
            searchName: undefined
        }, this.loadData)
    }

    // 数据库改变
    onDatabaseSelect = (value: any) => {
        this.setState({
            selecteDatabase: value,
            currentPage: 1
        }, this.loadData)
    }

    onProjectSelect = (value: any) => {
        this.setState({
            selectedProject: value,
            currentPage: 1
        }, this.loadData)
    }
    onStreamProjectSelect = (value: any) => {
        this.setState({
            streamSelectedProject: value,
            currentPage: 1
        }, this.loadData)
    }
    onScienceProjectSelect = (value: any) => {
        this.setState({
            scienceSelectedProject: value,
            currentPage: 1
        }, this.loadData)
    }
    initAddMember = () => {
        this.loadUsersNotInProject();
        this.setState({ visible: true })
    }

    initColums = () => {
        const ctx = this;
        const { active } = this.state;
        const hideDel = (active == MY_APPS.RDOS || active == MY_APPS.STREAM || active == MY_APPS.ANALYTICS_ENGINE || active == MY_APPS.SCIENCE);
        const isProject = (active == MY_APPS.RDOS || active == MY_APPS.STREAM || active == MY_APPS.SCIENCE);

        return [{
            title: '账号',
            dataIndex: 'user.userName',
            key: 'account',
            render (text: any, record: any) {
                return <a onClick={() => {
                    ctx.setState({
                        visibleEditRole: true,
                        editTarget: record
                    })
                }}>{text}</a>
            }
        }, {
            title: '邮箱',
            dataIndex: 'user.email',
            key: 'email'
        }, {
            title: '手机号',
            dataIndex: 'user.phoneNumber',
            key: 'phoneNumber'
        }, {
            title: '角色',
            width: 120,
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
            render (time: any) {
                return utils.formatDateTime(time);
            }
        }, {
            title: '操作',
            dataIndex: 'id',
            width: 140,
            key: 'id',
            render (id: any, record: any) {
                return <span>
                    <a onClick={() => {
                        ctx.setState({
                            visibleEditRole: true,
                            editTarget: record
                        })
                    }}>编辑角色</a>
                    {
                        hideDel ? <span>
                            <span className="ant-divider" />
                            <Popconfirm
                                title={isProject ? '确认将该成员从项目中移除？' : '确认将该成员从数据库中移除？'}
                                okText="确定" cancelText="取消"
                                onConfirm={() => { ctx.removeUserFromProject(record) }}
                            >
                                {isProject ? <a>移出项目</a> : <a>移出数据库</a>}
                            </Popconfirm>
                        </span> : ''
                    }
                </span>
            }
        }]
    }

    searchProjectUser = (user: any) => {
        this.setState({
            searchName: user
        }, this.loadData)
    }

    searchNameChange = (e: any) => {
        this.setState({
            searchName: e.target.value
        })
    }

    renderTitle = () => {
        const {
            projects,
            streamProjects,
            active,
            selectedProject,
            searchName,
            streamSelectedProject,
            dataBase,
            selecteDatabase,
            scienceSelectedProject,
            scienceProjects
        } = this.state;

        let selectValue;
        let onSelectChange;
        let projectsOptions = [];

        let databaseOptions = [];

        if (active == MY_APPS.RDOS) {
            selectValue = selectedProject;
            projectsOptions = projects;
            onSelectChange = this.onProjectSelect
        } else if (active == MY_APPS.STREAM) {
            selectValue = streamSelectedProject;
            projectsOptions = streamProjects;
            onSelectChange = this.onStreamProjectSelect
        } else if (active == MY_APPS.ANALYTICS_ENGINE) {
            databaseOptions = dataBase;
            onSelectChange = this.onDatabaseSelect;
        } else if (active == MY_APPS.SCIENCE) {
            selectValue = scienceSelectedProject;
            projectsOptions = scienceProjects;
            onSelectChange = this.onScienceProjectSelect
        }

        const projectOpts = projectsOptions && projectsOptions.map((project: any) =>
            <Option value={`${project.id}`} key={`${project.id}`}>
                {project.projectAlias}
            </Option>
        )
        const databaseOpts = databaseOptions && databaseOptions.map((item: any) =>
            <Option value={`${item.id}`} key={`${item.id}`}>
                {item.name}
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
                                value={selectValue ? `${selectValue}` : selectValue}
                                style={{ width: 200, marginRight: 10 }}
                                placeholder="按项目名称搜索"
                                optionFilterProp="name"
                                onSelect={onSelectChange}
                                filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                            >
                                {projectOpts}
                            </Select>
                        </span>
                    )
                }
                {
                    this.hasDatabase(active) && (
                        <span>
                            选择数据库：
                            <Select
                                showSearch
                                value={selecteDatabase ? `${selecteDatabase}` : selecteDatabase}
                                style={{ width: 200, marginRight: 10 }}
                                placeholder="按数据库名称搜索"
                                optionFilterProp="name"
                                onSelect={onSelectChange}
                                filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                            >
                                {databaseOpts}
                            </Select>
                        </span>
                    )
                }
                <Search
                    placeholder="请输入要搜索的账号"
                    value={searchName}
                    onChange={(e: any) => this.searchNameChange(e)}
                    style={{ width: 200 }}
                    onSearch={(value: any) => this.searchProjectUser(value)}
                />
            </span>
        )

        return title
    }

    renderPane = () => {
        const { users, loading } = this.state;

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
            current: users.currentPage
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

    render () {
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
                        wrappedComponentRef={(e: any) => { this.memberForm = e }}
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
                    width={Math.min(1000, Math.max(520, (400 + (roles ? roles.length * 60 : 0)))) + 'px'}
                >
                    <EditMemberRoleForm
                        myRoles={myRoles}
                        user={editTarget}
                        app={active}
                        roles={roles}
                        loginUser={user}
                        wrappedComponentRef={(e: any) => { this.eidtRoleForm = e }}
                    />
                </Modal>
            </div>
        )
    }
}

export default AdminUser
