import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Select, Table, Card, Button, Tabs } from 'antd'
import { Link } from 'react-router'

import RdosApi from 'rdos/api'
import DqApi from 'dataQuality/api/sysAdmin'

import { MY_APPS } from 'consts'
import { currentApps } from '../../../consts'
import AppTabs from '../../../components/app-tabs'

const Option = Select.Option
const TabPane = Tabs.TabPane

class AdminUser extends Component {

    state = {
        active: '',
        data: '',
        projects: [],
        loading: 'success',
    }

    componentDidMount() {
        // const { apps } = this.props
        // if (apps && apps.length > 0 ) {
        //     const key = apps[1].id;
        //     this.setState({
        //         active: key
        //     })
        // }
        this.loadData(currentApps[0].id);
    }

    loadData = (key) => {
        this.setState({ loading: 'loading' })
        const reqFunc = this.getReqFunc(key)
        reqFunc().then(res => {
            this.setState({
                data: res.data,
                loading: 'success'
            })
        })
    }

    getReqFunc(app) {
        switch( app ) {
            case MY_APPS.RDOS: 
                return RdosApi.getProjectUsers;
            case MY_APPS.DATA_QUALITY:
                return DqApi.getProjectUsers;
            default:
                return '';
        }
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
            dataIndex: 'account',
            key: 'account',
            render(text, record) {
                return <Link to={`message/detail/${record.id}`}>{text}</Link>
            },
        }, {
            title: '邮箱',
            dataIndex: 'age',
            key: 'age',
        }, {
            title: '邮箱',
            dataIndex: 'email',
            key: 'email',
        }, {
            title: '手机号',
            dataIndex: 'phoneNumber',
            key: 'phoneNumber',
        }, {
            title: '姓名',
            dataIndex: 'userName',
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
            dataIndex: 'address',
            key: 'address',
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

        const rowSelection = {
            onChange: (selectedRowKeys, selectedRows) => {
                console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
            },
            getCheckboxProps: record => ({
                disabled: record.name === 'Disabled User', // Column configuration not to be checked
            }),
        };

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
                    rowKey="id"
                    className="m-table"
                    columns={this.initColums()} 
                    loading={loading === 'loading'}
                    dataSource={ data ? data.data : [] } 
                    rowSelection={rowSelection} 
                />
            </Card>
        )
    }


    render() {
        // const { apps } = this.props
        const content = this.renderPane();

        return (
            <div className="user-admin">
                <h1 className="box-title">用户管理</h1>
                <div className="box-2 m-card" style={{height: '785px'}}>
                    <AppTabs 
                        apps={currentApps} 
                        activeKey={this.state.active}
                        content={content}
                        onPaneChange={this.onPaneChange} 
                    />
                </div>
            </div>
        )
    }
}

export default AdminUser