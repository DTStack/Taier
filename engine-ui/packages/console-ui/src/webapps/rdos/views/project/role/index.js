import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Link } from 'react-router'
import { 
    Card, Table, message,
    Popconfirm, Button,
} from 'antd'

import utils from 'utils'

import Api from '../../../api'

class RoleManagement extends Component {

    state = {
        roles: [],
        visible: false,
        loading: false,
    }

    componentDidMount() {
        this.loadRoles()
    }

    componentWillReceiveProps(nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadRoles()
        }
    }

    loadRoles = (page) => {
        const ctx = this
        this.setState({ loading: true })
        Api.getRoleList({
            currentPage: page || 1,
        }).then((res) => {
            if (res.code === 1) {
                ctx.setState({ roles: res.data, loading: false })
            }
        })
    }

    removeRole = (role) => {
        Api.deleteRole({roleId: role.id }).then((res) => {
            if (res.code === 1) {
                message.success('移除角色成功！')
                this.loadRoles()
            }
        })
    }

    handleTableChange = (pagination) => {
        this.loadRoles(pagination.current)
    }

    initColumns = () => {
        const { user, location } = this.props
        return [{
            title: '角色',
            dataIndex: 'roleName',
            key: 'roleName',
        }, {
            title: '描述',
            dataIndex: 'roleDesc',
            key: 'roleDesc',
        }, {
            title: '最近修改时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: text => utils.formatDateTime(text),
        }, 
        // {
        //     title: '操作',
        //     width: 120,
        //     key: 'operation',
        //     render: (text, record) => {
        //         return (
        //             <span key={record.id}>
        //                 <Link to={`${location.pathname}/edit/${record.id}`}>编辑</Link>
        //                 <span className="ant-divider" />
        //                 <Popconfirm
        //                     title="确认将该角色移除？"
        //                     okText="确定" cancelText="取消"
        //                     onConfirm={() => { this.removeRole(record) }}
        //                 >
        //                     <a>删除</a>
        //                 </Popconfirm>
        //             </span>
        //         )
        //     },
        // }
        ]
    }

    render() {
        const { visible, roles, loading } = this.state
        const { project, notProjectUsers, location } = this.props

        const pagination = {
            total: roles.totalCount,
            defaultPageSize: 10,
        };

        const extra = (<Button 
            type="primary" 
            style={{ marginTop: '10px' }}
            onClick={this.initAddMember}>
                <Link to={`${location.pathname}/add`}>新建角色</Link>
            </Button>
        )

        return (
            <div>
                <h1 className="box-title black" style={{paddingTop: '0'}}>
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
                    >
                        <Table
                            rowKey="id"
                            className="m-table"
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
export default connect((state) => {
    return {
        user: state.user,
        project: state.project,
    }
})(RoleManagement)
