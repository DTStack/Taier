import React, { Component } from 'react';
import { Icon, Card, Input, Table, Button } from 'antd';
import NewProject from '../../components/newProject';
import ProjectDetail from './projectDetail';
const Search = Input.Search;
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
            total: 0
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
        const params = Object.assign(this.state.params)
        params.search = value;
        this.setState({
            params
        });
    }
    handleTableChange = (paginations, filters, sorter) => {
        const params = Object.assign(this.state.params)
        const pagination = Object.assign(this.state.pagination)
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
    getTableData = () => {
        this.setState({
            data: [{
                id: 1,
                projectAliaName: '1',
                projectName: '1',
                mb: '1',
                creator: '1',
                createTime: '1'
            }]
        });
    }
    initCol = () => {
        return [{
            title: '项目显示名',
            dataIndex: 'projectAliaName',
            key: 'projectAliaName',
            sorter: true,
            render: (text, record) => {
                return <a href="javascript:void(0)" onClick={() => this.handleCheckProject(record)}>{text}</a>
            }
        }, {
            title: '项目名称',
            dataIndex: 'projectName',
            key: 'projectName'
        }, {
            title: '项目占用存储（MB）',
            dataIndex: 'mb',
            key: 'mb',
            sorter: true
        }, {
            title: '创建人',
            dataIndex: 'creator',
            key: 'creator'
        }, {
            title: '创建时间',
            dataIndex: 'createTime',
            key: 'createTime'
        }, {
            title: '操作',
            dataIndex: 'o',
            key: 'o',
            render: (text, record) => {
                return <a href="javascript:void(0)">开始数据探索</a>
            }
        }]
    }
    render () {
        const { loading, data, pagination, visible, visibleSlidePane, checkProject } = this.state;
        return (
            <div className="projects-list">
                <header className="projects-header"><Icon type="rollback" onClick={() => this.props.toggle()} />项目列表</header>
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
                    visible={visible} />
                <ProjectDetail
                    checkProject={checkProject || {}}
                    onCancel={this.closeSlidePane}
                    visible={visibleSlidePane}/>
            </div>
        );
    }
}

export default ProjectsList;
