import * as React from 'react'
import { connect } from 'react-redux'
import { Link } from 'react-router'
import {
    Card, Table, message,
    Button
} from 'antd'

import utils from 'utils'

import Api from '../../../api/project'

class RoleManagement extends React.Component<any, any> {
    initAddMember: any;
    state: any = {
        roles: [],
        visible: false,
        loading: false
    }

    componentDidMount () {
        this.loadRoles()
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadRoles()
        }
    }

    loadRoles = (page?: any) => {
        const ctx = this
        this.setState({ loading: true })
        Api.getRoleList({
            currentPage: page || 1
        }).then((res: any) => {
            if (res.code === 1) {
                ctx.setState({ roles: res.data, loading: false })
            }
        })
    }

    removeRole = (role: any) => {
        Api.deleteRole({ roleId: role && role.id }).then((res: any) => {
            if (res.code === 1) {
                message.success('移除角色成功！')
                this.loadRoles()
            }
        })
    }

    handleTableChange = (pagination: any) => {
        this.loadRoles(pagination.current)
    }

    initColumns = () => {
        const { location } = this.props
        return [{
            title: '角色',
            dataIndex: 'roleName',
            key: 'roleName',
            width: '120px'
        }, {
            title: '描述',
            dataIndex: 'roleDesc',
            key: 'roleDesc'
        }, {
            title: '最近修改时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: (text: any) => utils.formatDateTime(text),
            width: '140px'
        },
        {
            title: '操作',
            width: 120,
            key: 'operation',
            render: (text: any, record: any) => {
                return (
                    <span key={record.id}>
                        <Link to={`${location.pathname}/edit/${record.id}`}>查看</Link>
                    </span>
                )
            }
        }
        ]
    }

    render () {
        const { roles, loading } = this.state
        const { project, location } = this.props

        const pagination: any = {
            total: roles.totalCount,
            defaultPageSize: 10
        };

        /* eslint-disable */
        const extra = (<Button
            type="primary"
            style={{ marginTop: '10px' }}
            onClick={this.initAddMember}>
            <Link to={`${location.pathname}/add`}>新建角色</Link>
        </Button>
        )
        /* eslint-enable */

        return (
            <div>
                <h1 className="box-title black" style={{ paddingTop: '0' }}>
                    {project.projectName}
                    <span className="box-sub-title">&nbsp;描述：{project.projectDesc}</span>
                </h1>
                <div className="box-2 m-card">
                    <Card
                        noHovering
                        bordered={false}
                        loading={false}
                        title="角色列表"
                        extra={''}
                        className="full-screen-table-70"
                    >
                        <Table
                            rowKey="id"
                            className="dt-ant-table dt-ant-table--border"
                            pagination={pagination}
                            onChange={this.handleTableChange}
                            loading={loading}
                            columns={this.initColumns()}
                            dataSource={roles.data}
                        />
                    </Card>
                </div>
            </div>
        )
    }
}
export default connect((state: any) => {
    return {
        user: state.user,
        project: state.project
    }
})(RoleManagement)
