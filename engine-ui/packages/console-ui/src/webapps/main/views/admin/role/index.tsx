import * as React from 'react'
import { get } from 'lodash';
import { connect } from 'react-redux';
import {
    Select, Table, Card, message
} from 'antd'
import { Link } from 'react-router'

import utils from 'utils'
import { hasProject } from 'funcs'

import Api from '../../../api'
import AppTabs from '../../../components/app-tabs'
import { MY_APPS } from '../../../consts'

const Option = Select.Option

interface RoleState {
    active: string;
    data: any[];
    projects: any[];
    streamProjects: any[];
    scienceProjects: any[];
    apiProjects: any[];
    dqProjects: any[];
    selectedProject: number;
    streamSelectedProject: number;
    scienceSelectedProject: number;
    apiSelectedProject: number;
    dqSelectedProject: number;
    dataBase: any[];
    selecteDatabase: number;
    currentPage: number;
    loading: 'success' | 'loading';
}

@(connect((state: any) => {
    return {
        user: state.user,
        licenseApps: state.licenseApps
    }
}) as any)
class AdminRole extends React.Component<any, RoleState> {
    state: any = {
        active: '',
        data: '',
        projects: [],
        streamProjects: [],
        scienceProjects: [],
        apiProjects: [],
        dqProjects: [],
        selectedProject: '',
        streamSelectedProject: '',
        scienceSelectedProject: '',
        apiSelectedProject: '',
        dqSelectedProject: '',
        dataBase: [],
        selecteDatabase: undefined,
        currentPage: 1,
        loading: 'success'
    }

    handleData = () => {
        const { apps, licenseApps = [] } = this.props
        if (apps && apps.length > 0 && licenseApps.length > 0) {
            const initialApp = utils.getParameterByName('app');
            const defaultApp = licenseApps.find((licapp: any) => licapp.isShow) || [];
            const appKey = initialApp || defaultApp.id;
            this.setState({ active: appKey }, this.loadData)
        }
    }

    componentDidMount () {
        this.handleData();
    }

    componentDidUpdate (prevProps: any, prevState: any) {
        if (this.props.licenseApps.length > 0 && prevProps.licenseApps !== this.props.licenseApps) {
            this.handleData();
        }
    }
    hasDatabase (app: any) {
        return app === 'analyticsEngine';
    }

    loadData = () => {
        this.setState({ loading: 'loading' })

        const { active, selectedProject, streamSelectedProject, scienceSelectedProject, apiSelectedProject, dqSelectedProject, selecteDatabase, currentPage } = this.state
        const app = active;
        const haveSelected = (MY_APPS.RDOS == active && selectedProject) || (MY_APPS.STREAM == active && streamSelectedProject) ||
        (MY_APPS.SCIENCE == active && scienceSelectedProject) || (MY_APPS.API == active && apiSelectedProject) || (MY_APPS.DATA_QUALITY == active && dqSelectedProject)
        const databaseExsit = (MY_APPS.ANALYTICS_ENGINE == active && selecteDatabase);
        const params: any = {
            pageSize: 10,
            currentPage
        }

        if (!haveSelected && hasProject(app)) {
            this.getProjects(app)
        } else if (!databaseExsit && this.hasDatabase(app)) {
            this.getDatabase(app)
        } else if (MY_APPS.ANALYTICS_ENGINE == active) {
            params.databaseId = selecteDatabase;
            this.loadRoles(app, params)
        } else if (!haveSelected && !hasProject(app)) {
            this.loadRoles(app, params)
        } else {
            if (MY_APPS.RDOS == active) {
                params.projectId = selectedProject
            } else if (MY_APPS.STREAM == active) {
                params.projectId = streamSelectedProject;
            } else if (MY_APPS.SCIENCE == active) {
                params.projectId = scienceSelectedProject;
            } else if (MY_APPS.API == active) {
                params.projectId = apiSelectedProject;
            } else if (MY_APPS.DATA_QUALITY == active) {
                params.projectId = dqSelectedProject;
            }
            this.loadRoles(app, params)
        }
    }

    loadRoles = (app: any, params: any) => {
        if (params.projectId == '无项目') {
            this.setState({ loading: 'success' })
            return;
        }
        this.setState({
            data: [],
            loading: 'loading'
        })
        Api.queryRole(app, params).then((res: any) => {
            if (res.code == 1) {
                this.setState({
                    data: res.data
                })
            }
            this.setState({
                loading: 'success'
            })
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
                    }, this.loadData.bind(true))
                }
            }
            this.setState({
                loading: 'success'
            })
        })
    }

    getProjects = (app: any) => {
        const ctx = this

        Api.getProjects(app).then((res: any) => {
            if (res.code === 1) {
                const selectedProject = get(res, 'data[0].id', '无项目')

                if (MY_APPS.RDOS == app) {
                    ctx.setState({
                        projects: res.data,
                        selectedProject
                    }, this.loadData)
                } else if (MY_APPS.STREAM == app) {
                    ctx.setState({
                        streamProjects: res.data,
                        streamSelectedProject: selectedProject
                    }, this.loadData)
                } else if (MY_APPS.SCIENCE == app) {
                    ctx.setState({
                        scienceProjects: res.data,
                        scienceSelectedProject: selectedProject
                    }, this.loadData)
                } else if (MY_APPS.API == app) {
                    ctx.setState({
                        apiProjects: res.data,
                        apiSelectedProject: selectedProject
                    }, this.loadData)
                } else if (MY_APPS.DATA_QUALITY == app) {
                    ctx.setState({
                        dqProjects: res.data,
                        dqSelectedProject: selectedProject
                    }, this.loadData)
                }
            }
        })
    }

    removeRole = (role: any) => {
        const appKey = this.state.active;

        Api.deleteRole(appKey, { roleId: role.id }).then((res: any) => {
            if (res.code === 1) {
                message.success('移除角色成功！')
                this.loadData()
            }
        })
    }

    onPaneChange = (key: any) => {
        this.setState({
            active: key,
            currentPage: 1
        }, () => {
            this.props.router.replace('/admin/role?app=' + key)
            this.loadData();
        })
    }

    handleTableChange = (pagination: any, filters: any) => {
        this.setState({
            currentPage: pagination.current
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
    onApiProjectSelect = (value: any) => {
        this.setState({
            apiSelectedProject: value,
            currentPage: 1
        }, this.loadData)
    }

    onDqProjectSelect = (value: any) => {
        this.setState({
            apiSelectedProject: value,
            currentPage: 1
        }, this.loadData)
    }
    initColums = () => {
        const { active } = this.state;

        return [{
            title: '角色名称',
            dataIndex: 'roleName',
            key: 'roleName',
            render (text: any, record: any) {
                return <Link to={`/admin/role/edit/${record.id}?app=${active}`}>{text}</Link>
            },
            width: '150px'
        }, {
            title: '角色描述',
            dataIndex: 'roleDesc',
            key: 'roleDesc'
        }, {
            title: '最近修改时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render (time: any) {
                return utils.formatDateTime(time);
            }
        }, {
            title: '最近修改人',
            dataIndex: 'person',
            key: 'person',
            width: 120,
            render (text: any) {
                return text || '-'
            }
        }, {
            title: '操作',
            dataIndex: 'id',
            key: 'id',
            width: 80,
            render (id: any, record: any) {
                return <span>
                    <Link to={`/admin/role/edit/${id}?app=${active}`}>查看</Link>
                    {/* <span className="ant-divider" />
                    <Popconfirm
                            title="确认将该角色移除？"
                            okText="确定" cancelText="取消"
                            onConfirm={() => { removeRole(record) }}
                        >
                            <a>删除</a>
                    </Popconfirm> */}
                </span>
            }
        }]
    }

    renderPane = () => {
        const {
            data, loading, projects, streamProjects,
            active, selectedProject, streamSelectedProject, dataBase, selecteDatabase,
            scienceSelectedProject, scienceProjects, apiProjects, apiSelectedProject, dqProjects, dqSelectedProject
        } = this.state;
        let projectsOptions = [];

        let databaseOptions = [];
        let selectValue;
        let onSelectChange;

        if (active == MY_APPS.RDOS) {
            selectValue = selectedProject;
            projectsOptions = projects;
            onSelectChange = this.onProjectSelect
        } else if (active == MY_APPS.STREAM) {
            selectValue = streamSelectedProject;
            projectsOptions = streamProjects;
            onSelectChange = this.onStreamProjectSelect
        } else if (active == MY_APPS.SCIENCE) {
            selectValue = scienceSelectedProject;
            projectsOptions = scienceProjects;
            onSelectChange = this.onScienceProjectSelect
        } else if (active == MY_APPS.API) {
            selectValue = apiSelectedProject;
            projectsOptions = apiProjects;
            onSelectChange = this.onApiProjectSelect
        } else if (active == MY_APPS.DATA_QUALITY) {
            selectValue = dqSelectedProject;
            projectsOptions = dqProjects;
            onSelectChange = this.onDqProjectSelect
        } else if (active == MY_APPS.ANALYTICS_ENGINE) {
            databaseOptions = dataBase;
            onSelectChange = this.onDatabaseSelect;
        }

        const projectOpts = projectsOptions && projectsOptions.map((project: any) =>
            <Option value={project.id} key={project.id}>
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
                        <span
                            style={{ marginTop: '10px' }}
                        >
                            选择项目：
                            <Select
                                showSearch
                                value={selectValue}
                                style={{ width: 200 }}
                                placeholder="按项目名称搜索"
                                optionFilterProp="name"
                                onSelect={onSelectChange}
                            >
                                {projectOpts}
                            </Select>
                        </span>
                    )
                }

                {
                    this.hasDatabase(active) && (
                        <span
                            style={{ marginTop: '10px' }}
                        >
                            选择数据库：
                            <Select
                                showSearch
                                value={selecteDatabase ? `${selecteDatabase}` : selecteDatabase}
                                style={{ width: 200, marginRight: 10 }}
                                placeholder="按数据库名称搜索"
                                optionFilterProp="name"
                                onSelect={onSelectChange}
                            >
                                {databaseOpts}
                            </Select>
                        </span>
                    )
                }

            </span>
        )

        const pagination = {
            total: data && data.totalCount,
            defaultPageSize: 10,
            current: data.currentPage
        };

        return (
            <Card
                bordered={false}
                noHovering
                title={title}
                extra={''}
            >
                <Table
                    rowKey="id"
                    className="m-table"
                    columns={this.initColums()}
                    loading={loading === 'loading'}
                    dataSource={data ? data.data : []}
                    pagination={pagination}
                    onChange={this.handleTableChange}
                />
            </Card>
        )
    }

    render () {
        // 融合API管理后
        const { apps, licenseApps } = this.props
        const content = this.renderPane();

        return (
            <div className="user-admin">
                <h1 className="box-title">角色管理</h1>
                <div className="box-2 m-card" style={{ height: '785px' }}>
                    <AppTabs
                        apps={apps}
                        licenseApps={licenseApps}
                        activeKey={this.state.active}
                        content={content}
                        onPaneChange={this.onPaneChange}
                    />
                </div>
            </div>
        )
    }
}

export default AdminRole
