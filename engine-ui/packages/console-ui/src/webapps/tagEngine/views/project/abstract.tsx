import * as React from 'react'
import { connect } from 'react-redux'
import { Table, Input, message } from 'antd'
import { Link } from 'react-router'

import utils from 'utils'
import Api from '../../api'
import { taskStatusFilter } from '../../comm/const'
import { TaskStatus } from '../../components/status'

const Search = Input.Search

class Abstract extends React.Component<any, any> {
    state: any = {
        tasks: {
            data: []
        },
        loading: false
    }

    componentDidMount () {
        this.loadTaskList()
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const nowId = nextProps.params.pid
        if (nowId && nowId !== this.props.params.pid) {
            this.loadTaskList()
        }
    }

    initProjectInfo = () => {
        return [{
            title: '项目管理员',
            dataIndex: 'adminUsers',
            key: 'adminUsers',
            render: (text: any, record: any, index: any) => {
                const data = record.adminUsers || []
                return data && data.length > 0
                    ? data.map((item: any) => <span key={index}>{item.userName}; </span>) : '无'
            }
        }, {
            title: '项目成员',
            dataIndex: 'memberUsers',
            key: 'memberUsers',
            render: (text: any, record: any, index: any) => {
                const data = record.memberUsers || []
                return data && data.length > 0
                    ? data.map((item: any) => <span key={index}>{item.userName}</span>) : '无'
            }
        }, {
            title: '项目描述',
            dataIndex: 'projectDesc',
            key: 'projectDesc'
        }]
    }

    initTaskColumns = () => {
        return [{
            title: '任务名称',
            dataIndex: 'name',
            key: 'name',
            render: (text: any) => {
                return <Link to={'/operation'}>{text}</Link>
            }
        }, {
            title: '运行时间',
            dataIndex: 'createUserId',
            key: 'createUserId',
            render: () => '0'
        }, {
            title: '全部状态',
            dataIndex: 'status',
            key: 'status',
            render: (text: any) => {
                return <TaskStatus value={text} />
            },
            filters: taskStatusFilter,
            filterMultiple: false
        }, {
            title: '责任人',
            dataIndex: 'createUser',
            key: 'createUser',
            render: (text: any, record: any) => {
                return (<span>{(record.createUser && record.createUser.userName) || '-'}</span>)
            }
        }, {
            title: '最近操作时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: (text: any) => utils.formatDateTime(text),
            sorter: (a: any, b: any) => a.gmtModified - b.gmtModified
        }, {
            title: '最近操作人',
            dataIndex: 'modifyUser',
            key: 'modifyUser',
            render: (text: any, record: any) => {
                return (<span>{record.modifyUser && record.modifyUser.userName}</span>)
            }
        }, {
            title: '操作',
            key: 'operation',
            render: (text: any, record: any) => {
                let name = ''
                switch (record.status) {
                    case 0:
                        name = '提交'
                        break;
                    case 7:
                        name = '续跑'
                        break;
                    case 8:
                        name = '重试'
                        break;
                    case 4:
                        name = '停止'
                        break;
                    default:
                        break;
                }
                return (
                    <div key={record.id}>
                        <Link to={'/operation'}>查看</Link>
                        <span className="ant-divider" />
                        <a onClick={() => { this.updateTaskStatus(record) }}>{name}</a>
                    </div>
                )
            }
        }]
    }

    loadTaskList (currentPage?: any, pageSize?: any, isTimeSortDesc?: any, status?: any) {
        const ctx = this
        this.setState({ loading: true })
        const params: any = {
            pageIndex: currentPage || 1,
            pageSize: pageSize || 10
        }
        if (isTimeSortDesc) params.isTimeSortDesc = isTimeSortDesc
        if (status) params.status = status
        Api.getTasks(params).then((res: any) => {
            if (res.code === 1) {
                ctx.setState({ tasks: res.data, loading: false })
            }
        })
    }

    searchTask = (query: any) => {
        const ctx = this
        this.setState({ loading: true })
        Api.getTasks({
            taskName: query,
            pageIndex: 1
        }).then((res: any) => {
            if (res.code === 1) {
                ctx.setState({ tasks: res.data, loading: false })
            }
        })
    }

    updateTaskStatus = (task: any) => {
        const ctx = this
        const status = task.status
        const isRestore = status === 8 ? 1 : 0
        if (status === 0 || status === 7 || status === 8) {
            Api.startTask({
                id: task.id,
                isRestoration: isRestore
            }).then((res: any) => {
                if (res.code === 1) {
                    message.success('任务已经成功启动！')
                    ctx.loadTaskList()
                }
            })
        } else if (status === 4) {
            Api.stopTask({
                id: task.id
            }).then((res: any) => {
                if (res.code === 1) {
                    message.success('任务已执行停止！')
                    ctx.loadTaskList()
                }
            })
        }
    }

    handleTableChange = (pagination: any, filters: any) => {
        if (filters.status) {
            this.loadTaskList(1, '', '', filters.status[0])
        } else {
            this.loadTaskList(pagination.current)
        }
    }

    render () {
        const { tasks } = this.state
        const project = this.props.project
        const pagination: any = {
            total: tasks.totalCount,
            defaultPageSize: 10
        };
        const arr = project ? [project] : []
        return (
            <div className="project-abstract bg-w">
                <article className="section">
                    <h1 className="title black">
                        {project.projectName}
                        <span className="desc">
                            &nbsp;{project.createUser ? project.createUser.userName : ''}
                            &nbsp;创建于：{utils.formatDateTime(project.gmtCreate)}
                        </span>
                    </h1>
                    <Table
                        className="section-border"
                        bordered={false}
                        pagination={false}
                        columns={this.initProjectInfo()}
                        dataSource={arr}
                    />
                </article>
                <article className="section">
                    <h1 className="title black">实时任务</h1>
                    <div style={{ paddingBottom: '15px' }}>
                        <Search
                            placeholder="按任务名称搜索"
                            style={{ width: 200 }}
                            onSearch={this.searchTask}
                        />
                    </div>
                    <Table
                        className="section-border"
                        pagination={pagination}
                        columns={this.initTaskColumns()}
                        loading={this.state.loading}
                        dataSource={tasks.data || []}
                        onChange={this.handleTableChange}
                    />
                </article>
            </div>
        )
    }
}
export default connect((state: any) => state)(Abstract)
