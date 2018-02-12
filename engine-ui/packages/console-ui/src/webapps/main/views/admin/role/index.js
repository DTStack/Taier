import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Select, Table, Card, Button, Tabs } from 'antd'
import { Link } from 'react-router'

import utils from 'utils'
import { MY_APPS } from 'consts'

import RdosApi from 'rdos/api'
import DqApi from 'dataQuality/api/sysAdmin'

import AppTabs from '../../../components/app-tabs'
import { currentApps } from '../../../consts'

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
        // const { apps } = this.props
        // if (apps && apps.length > 0 ) {
        //     const key = apps[1].id;
        //     this.setState({
        //         active: key
        //     })
        // }
        this.loadData(currentApps[0].id);
    }

    getReqFunc(app) {
        switch( app ) {
            case MY_APPS.RDOS: 
                return RdosApi.getRoleList;
            case MY_APPS.DATA_QUALITY:
                return DqApi.queryRole;
            default:
                return '';
        }
    }

    loadData = (key) => {
        this.setState({ loading: 'loading' })
        const reqFunc = this.getReqFunc(key)
        if (reqFunc) reqFunc().then(res => {
            this.setState({
                data: res.data,
                loading: 'success'
            })
        })
    }

    onPaneChange = (key) => {
        this.setState({
            active: key,
        })
        this.loadData(key)
    }

    initColums = () => {
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
                新建角色
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
        // 融合API管理后
        // const { apps } = this.props
        const content = this.renderPane();
        return (
            <div className="user-admin">
                <h1 className="box-title">角色管理</h1>
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

export default AdminRole