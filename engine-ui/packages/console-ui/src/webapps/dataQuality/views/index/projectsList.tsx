import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { withRouter } from 'react-router'
import { get } from 'lodash';

import { Icon, Card, Input, Table, Button, message } from 'antd';
import NewProject from '../../components/newProject';
import TaskParamsModal from '../../components/taskParamsModal';

import utils from 'utils';
import * as baseActions from '../../actions/project'
import { PROJECT_STATUS } from '../../consts'
import Api from '../../api/project';

const Search = Input.Search;

@(connect((state: any) => {
    return {
        currentProject: state.project.currentProject
    }
}, (dispatch: any) => {
    return {
        ...bindActionCreators(baseActions, dispatch)
    }
}) as any)
class ProjectsList extends React.Component<any, any> {
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
        checkProject: undefined,
        visible: false,
        visibleSlidePane: false
    }
    componentDidMount () {
        this.getTableData();
    }
    handleCheckProject = (record: any) => {
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
        let res = await Api.getProjectList({
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
                    return <a onClick={() => this.handleCheckProject(record)}>{text}</a>
                }
                return text;
            }
        }, {
            title: '项目名称',
            dataIndex: 'projectName',
            key: 'projectName',
            width: '180px'
        }, {
            title: '项目占用存储',
            dataIndex: 'totalSize',
            key: 'totalSize',
            render (t: any) {
                return utils.convertBytes(t)
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
                                this.props.router.push('/dq/workbench');
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
        this.props.router.push('/dq/index')
    }
    changeProject = async (key: any, value: any, reset: any) => {
        const { checkProject } = this.state;
        const { currentProject } = this.props;
        let res = await Api.updateProject({
            projectId: checkProject.id,
            [key]: value
        })
        if (res && res.code == 1) {
            this.setState({
                checkProject: {
                    ...checkProject,
                    [key]: value
                }
            })
            if (currentProject && currentProject.id == checkProject.id) {
                this.props.initCurrentProject();
            }
            this.getTableData();
            message.success('修改成功')
            reset();
        }
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
                <TaskParamsModal
                    title='项目属性'
                    onCancel={this.closeSlidePane}
                    visible={visibleSlidePane}
                    onEdit={this.changeProject}
                    data={checkProject && [{
                        label: '项目名称',
                        value: checkProject.projectName
                    }, {
                        label: '项目显示名',
                        value: checkProject.projectAlias,
                        key: 'projectAlias',
                        edit: true
                    }, {
                        label: '项目描述',
                        value: checkProject.projectDesc,
                        key: 'projectDesc',
                        editType: 'textarea',
                        edit: true
                    }, {
                        label: '关联离线计算中的项目',
                        value: checkProject.refProjectName
                    }, {
                        label: '创建时间',
                        value: utils.formatDateTime(checkProject.gmtCreate)
                    }, {
                        label: '创建人',
                        value: get(checkProject, 'createUser.userName')
                    }, {
                        label: '管理员',
                        value: get(checkProject, 'adminUsers', []).map((user: any) => {
                            return user.userName
                        }).join(', ')
                    }]}
                />
            </div>
        );
    }
}

export default withRouter(ProjectsList);
