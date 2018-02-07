import React, { Component } from 'react'

import { Card, Input, Radio, Button, Table, message } from 'antd'

import utils from 'utils'
import Api from '../../api'
import { ProjectStatus } from '../../components/status'
import * as ProjectAction from '../../store/modules/project'
import ProjectForm from '../project/form'

const Search = Input.Search

export default class ProjectList extends Component {

    state = {
        choose: '1',
        current: 1,
        loading: 'success',
        visible: false,
        projectList: {
            data: [],
        },
    }

    componentDidMount() {
        this.queryProjectList()
    }

    go = (project, url) => {
        const { router, dispatch } = this.props
        dispatch(ProjectAction.getProject(project.id))
        router.push(url)
    }

    queryProjectList = (params) => {
        const ctx = this
        this.setState({ loading: 'loading' })
        const reqParams = Object.assign({
            projectName: '',
            isAdmin: true,
            currentPage: 1,
            pageSize: 10,
        }, params)
        Api.queryProjects(reqParams).then((res) => {
            if (res.code === 1) {
                ctx.setState({ projectList: res.data, loading: 'success' })
            }
        })
    }

    createProject = (project) => {
        const ctx = this
        const { dispatch } = this.props
        Api.createProject(project).then((res) => {
            if (res.code === 1) {
                ctx.setState({ visible: false })
                ctx.queryProjectList()
                dispatch(ProjectAction.getProjects())
                message.success('创建项目成功！')
            }
        })
    }

    handleChange = (e) => {
        const val = e.target.value
        this.setState({ choose: val, current: 1 });
        this.queryProjectList({
            isAdmin: val === '1',
        })
    }

    handleTableChange = (pagination) => {
        this.setState({ current: pagination.current })
        this.queryProjectList({
            currentPage: pagination.current,
            isAdmin: this.state.choose === '1',
        })
    }

    onSearch = (queryProjectName) => {
        if (queryProjectName !== '') {
            this.queryProjectList({
                projectName: queryProjectName,
                isAdmin: this.state.choose === '1',
            })
        }
    }

    initColumns = () => {
        return [{
            title: '项目名称',
            dataIndex: 'projectAlias',
            key: 'projectAlias',
            render: (text, record) => {
                const name = text || record.projectName
                return record.status === 3 ? <a>{name}</a> :
                    <a onClick={() => { this.go(record, '/offline/task') }}>{name}</a>
            },
        }, {
            title: '创建日期',
            dataIndex: 'gmtCreate',
            key: 'gmtCreate',
            render: text => utils.formateDateTime(text),
        }, {
            title: '项目所有者',
            dataIndex: 'createUser',
            key: 'createUser',
            render: (text, record) => {
                return record.createUser.userName
            },
        }, {
            title: '项目管理员',
            key: 'adminUsers',
            render: (text, record) => {
                const data = record.adminUsers || []
                return data.map(item => <span>{item.userName} </span>)
            },
        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render: (text) => {
                return (<ProjectStatus value={text} />)
            },
        }, {
            title: '操作',
            key: 'action',
            render: (text, record) => {
                const show = record.status === 1 ? 'block' : 'none'
                return (
                    <span>
                        <a style={{ display: show }} onClick={() => { this.go(record, `/project/${record.id}/config`) }}>
                            项目配置
                        </a>
                        <span style={{ display: show }}>
                            <a onClick={() => { this.go(record, '/offline/task') }}>
                                工作台
                            </a>
                        </span>
                    </span>
                )
            },
        }]
    }

    render() {
        const { projectList, visible, loading } = this.state
        const extra = (
            <Button 
                type="primary" 
                onClick={() => { this.setState({ visible: true }) }}>
                创建项目
            </Button>
        )
        const title = (
            <div>
                <Search
                  placeholder="按项目名称搜索"
                  style={{ width: 200 }}
                  onSearch={this.onSearch}
                />&nbsp;&nbsp;
                <Radio.Group value={this.state.choose} onChange={this.handleChange}>
                    <Radio.Button value="1">我管理的</Radio.Button>
                    <Radio.Button value="0">我参与的</Radio.Button>
                </Radio.Group>
            </div>
        )
        const pagination = {
            total: projectList.totalCount,
            defaultPageSize: 10,
            current: this.state.current,
        };
        return (
            <article className="section">
                <h1 className="title">项目列表</h1>
                <Card
                    loading={false}
                    title={title}
                    extra={extra}
                >
                    <Table
                        rowKey="id"
                        columns={this.initColumns()}
                        loading={loading === 'loading'}
                        pagination={pagination}
                        dataSource={projectList.data}
                        onChange={this.handleTableChange}
                    />
                </Card>
                <ProjectForm
                    title="创建项目"
                    onOk={this.createProject}
                    visible={visible}
                    onCancel={() => this.setState({ visible: false })}
                />
            </article>
        )
    }
}
