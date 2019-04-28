import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { withRouter } from 'react-router'

import { Icon, Card, Input, Table, Button } from 'antd';
import NewProject from '../../components/newProject';
import ProjectDetail from './projectDetail';

import utils from 'utils';
import * as baseActions from '../../actions/base'
import Api from '../../api';

const Search = Input.Search;

@connect(null, dispatch => {
    return {
        ...bindActionCreators(baseActions, dispatch)
    }
})
class ProjectsList extends Component {
    state = {
        loading: false,
        data: [],
        params: {
            search: '',
            sort: '',
            filed: ''
        },
        pagination: {
            current: 1,
            total: 0,
            pageSize: 10
        },
        checkProject: undefined,
        visible: false,
        visibleSlidePane: false
    }
    componentDidMount () {
        this.getTableData();
    }
    handleCheckProject = (record) => {
        this.setState({
            visibleSlidePane: true,
            checkProject: record
        });
    }
    handleNewProject = () => {
        this.setState({
            visible: true
        });
    }
    handleCancel = () => {
        this.setState({
            visible: false
        });
    }
    closeSlidePane = () => {
        this.setState({
            visibleSlidePane: false
        });
    }
    handleSearch = (value) => {
        const params = Object.assign({}, this.state.params)
        params.search = value;
        this.setState({
            params
        }, this.getTableData);
    }
    handleTableChange = (paginations, filters, sorter) => {
        const params = Object.assign({}, this.state.params)
        const pagination = Object.assign({}, this.state.pagination)
        pagination.current = paginations.current;
        if (sorter.field) {
            params.filed = sorter.field;
            params.sort = sorter.order === 'descend' ? 'desc' : 'asc';
        } else {
            params.filed = '';
            params.sort = '';
        }
        this.setState({
            params,
            pagination
        }, this.getTableData)
    }
    getTableData = async () => {
        this.setState({
            loading: true
        })
        const { pagination, params } = this.state;
        let res = await Api.comm.getProjectList({
            currentPage: pagination.current,
            pageSize: pagination.pageSize,
            searchName: params.search || undefined,
            orderBy: params.filed || undefined,
            sort: params.sort || undefined
        });
        this.setState({
            loading: false
        })
        if (res && res.code == 1) {
            this.setState({
                data: res.data.data,
                pagination: {
                    ...this.state.pagination,
                    total: res.data.totalCount
                }
            })
        }
    }
    initCol = () => {
        return [{
            title: '项目显示名',
            dataIndex: 'projectAlias',
            key: 'projectAlias',
            render: (text, record) => {
                return <a href="javascript:void(0)" onClick={() => this.handleCheckProject(record)}>{text}</a>
            }
        }, {
            title: '项目名称',
            dataIndex: 'projectName',
            key: 'projectName'
        }, {
            title: '项目占用存储',
            dataIndex: 'totalSize',
            key: 'totalSize',
            sorter: true
        }, {
            title: '创建人',
            dataIndex: 'creator',
            key: 'creator'
        }, {
            title: '创建时间',
            dataIndex: 'gmtCreate',
            key: 'gmtCreate',
            render (t) {
                return utils.formatDateTime(t)
            }
        }, {
            title: '操作',
            dataIndex: 'o',
            key: 'o',
            render: (text, record) => {
                return <a onClick={ () => {
                    this.props.setProject(record);
                    setTimeout(() => {
                        this.props.router.push('/science/workbench');
                    })
                }}>
                    开始数据探索
                </a>
            }
        }]
    }
    gotoWelcome = () => {
        this.props.router.push('/science/index')
    }
    render () {
        const { loading, data, pagination, visible, visibleSlidePane, checkProject } = this.state;
        return (
            <div className="projects-list">
                <header className="projects-header"><Icon type="rollback" onClick={this.gotoWelcome} />项目列表</header>
                <Card
                    noHovering
                    bordered={false}
                    title={
                        <Search
                            onSearch={this.handleSearch}
                            placeholder='按项目名称、项目显示名搜索'
                            style={{ width: 267 }} />
                    }
                    extra={<Button type="primary" onClick={this.handleNewProject}>创建项目</Button>}>
                    <Table
                        rowKey="id"
                        className='m-table'
                        loading={loading}
                        onChange={this.handleTableChange}
                        columns={this.initCol()}
                        dataSource={data}
                        pagination={pagination}
                    />
                </Card>
                <NewProject
                    onCancel={this.handleCancel}
                    visible={visible}
                    onOk={this.getTableData}
                />
                <ProjectDetail
                    checkProject={checkProject || {}}
                    onCancel={this.closeSlidePane}
                    visible={visibleSlidePane}/>
            </div>
        );
    }
}

export default withRouter(ProjectsList);
