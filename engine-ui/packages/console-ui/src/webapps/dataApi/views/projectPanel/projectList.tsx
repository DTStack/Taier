import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { withRouter } from 'react-router';

import { Icon, Card, Input, Table, Button } from 'antd';
import NewProject from '../../components/newProject';

import utils from 'utils';
import * as projectActions from '../../actions/project'
import { PROJECT_STATUS } from '../../consts'
import Api from '../../api/project';

const Search = Input.Search;

interface ProjectState {
    loading: boolean;
    visible: boolean;
    data: any[];
    params: {
        search: string;
        sort: string;
        filed: string;
    };
    pagination: {
        current?: number;
        total: number;
        pageSize?: number;
    };
}

@(connect((state: any) => {
    return {
    }
}, (dispatch: any) => {
    return {
        ...bindActionCreators(projectActions, dispatch)
    }
}) as any)
class ProjectsList extends React.Component<any, ProjectState> {
    state: any = {
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
            pageSize: 15
        },
        visible: false
    }
    componentDidMount () {
        this.getTableData();
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
    handleSearch = (value: any) => {
        const params = Object.assign({}, this.state.params)
        params.search = value;
        this.setState({
            params
        }, this.getTableData);
    }
    handleTableChange = (paginations: any, filters: any, sorter: any) => {
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
        let res = await Api.getAllProjects({
            currentPage: pagination.current,
            pageSize: pagination.pageSize,
            searchName: params.search || undefined,
            orderBy: params.filed ? params.filed.replace(/([A-Z])/g, '_$1').toLowerCase() : undefined,
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
            width: '180px',
            render: (text: any, record: any) => {
                if (record.status == PROJECT_STATUS.SUCCESS) {
                    // return <a onClick={() => this.handleCheckProject(record)}>{text}</a>
                }
                return text;
            }
        }, {
            title: '项目名称',
            dataIndex: 'projectName',
            key: 'projectName',
            width: '180px'
        }, {
            title: 'API创建数',
            dataIndex: 'totalSize',
            key: 'totalSize',
            render (t: any) {
                return t
            },
            sorter: true
        }, {
            title: 'API发布数',
            dataIndex: 'totalApiSize',
            key: 'totalApiSize',
            render (t: any) {
                return t
            },
            sorter: true
        }, {
            title: '创建人',
            dataIndex: 'createUser.userName',
            key: 'createUser.userName'
        }, {
            title: '创建时间',
            dataIndex: 'gmtCreate',
            key: 'gmtCreate',
            sorter: true,
            render (t: any) {
                return utils.formatDateTime(t)
            }
        }, {
            title: '操作',
            dataIndex: 'o',
            key: 'o',
            render: (text: any, record: any) => {
                switch (record.status) {
                    case PROJECT_STATUS.CREATING: {
                        return '项目创建中...'
                    }
                    case PROJECT_STATUS.CANCEL:
                    case PROJECT_STATUS.FAILED: {
                        return <a style={{ color: 'red' }}>项目创建失败</a>
                    }
                    default: {
                        return <a onClick={ () => {
                            this.props.setProject(record);
                            setTimeout(() => {
                                this.props.router.push('/science/workbench');
                            })
                        }}>
                            开始数据探索
                        </a>
                    }
                }
            }
        }]
    }
    gotoWelcome = () => {
        this.props.router.push('/')
    }
    render () {
        const { loading, data, pagination, visible } = this.state;
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
                    extra={<Button className='o-font--normal' type="primary" onClick={this.handleNewProject}>创建项目</Button>}>
                    <Table
                        rowKey="id"
                        className='dt-ant-table dt-ant-table--border dt-ant-table--border-lr'
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
            </div>
        );
    }
}

export default withRouter(ProjectsList);
