import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { 
    Select, Table, Card, message,
    Button, Tabs, Popconfirm 
} from 'antd'
import { Link } from 'react-router'

import utils from 'utils'

import Api from '../../../api'
import AppTabs from '../../../components/app-tabs'

const Option = Select.Option
const TabPane = Tabs.TabPane

class AdminRole extends Component {

    state = {
        active: '',
        data: '',
        projects: [],
        loading: 'success',
    }

    componentDidMount() {
        const { apps } = this.props
        if (apps && apps.length > 0 ) {
            const key = apps[1].id;
            this.setState({
                active: key
            })
            this.loadData(key);
        }
    }

    loadData = (key) => {
        this.setState({ loading: 'loading' })
        Api.queryRole(key).then(res => {
            this.setState({
                data: res.data,
                loading: 'success'
            })
        })
    }

    removeRole = (role) => {
        const appKey = this.state.active;
        Api.deleteRole(appKey, {roleId: role.id }).then((res) => {
            if (res.code === 1) {
                message.success('移除角色成功！')
                this.loadData(appKey)
            }
        })
    }

    onPaneChange = (key) => {
        this.setState({
            active: key,
        })
        this.loadData(key)
    }

    initColums = () => {
        const { active } = this.state;
        const removeRole = this.removeRole;
        return [{
            title: '角色名称',
            dataIndex: 'roleName',
            key: 'roleName',
            render(text, record) {
                return <Link to={`message/detail/${record.id}`}>{text}</Link>
            },
        }, {
            title: '角色描述',
            dataIndex: 'roleDesc',
            key: 'roleDesc',
        }, {
            title: '最近修改时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render(time) {
                return utils.formatDateTime(time);
            }
        }, {
            title: '最近修改人',
            dataIndex: 'person',
            key: 'person',
            render(text) {
                return text || '-'
            }
        }, {
            title: '操作',
            dataIndex: 'id',
            key: 'id',
            render(id, record) {
                return <span>
                    <Link to={`/admin/role/edit/${id}?app=${active}`}>编辑</Link>
                    <span className="ant-divider" />
                    <Popconfirm
                            title="确认将该角色移除？"
                            okText="确定" cancelText="取消"
                            onConfirm={() => { removeRole(record) }}
                        >
                            <a>删除</a>
                    </Popconfirm>
                </span>
            }
        }]
    }

    renderPane = () => {
        const { data, loading, projects, active } = this.state;

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
            <Button style={{marginTop: '10px'}} type="primary">
                <Link to={`/admin/role/add?app=${active}`}>新建角色</Link>
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
                    rowKey="id"
                    className="m-table"
                    columns={this.initColums()} 
                    loading={loading === 'loading'}
                    dataSource={ data ? data.data : [] } 
                />
            </Card>
        )
    }

    render() {
        // 融合API管理后
        const { apps } = this.props
        const content = this.renderPane();
        return (
            <div className="user-admin">
                <h1 className="box-title">角色管理</h1>
                <div className="box-2 m-card" style={{height: '785px'}}>
                    <AppTabs 
                        apps={apps} 
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