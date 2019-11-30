import * as React from 'react'
import { hashHistory } from 'react-router';
import { Card, Table, Input, Button, message } from 'antd'

import CreateForm from './createForm';
import './style.scss';
import TaskParamsModal from '../../components/taskParamsModal';
import DeleteModal from '../../components/deleteModal';
import { API } from '../../api/apiMap'
import utils from 'utils'
const Search = Input.Search;

interface IState {
    pageNo: number;
    pageSize: number;
    total: number;
    dataSource: any[];
    searchVal: string;
    loading: boolean;
    desc: boolean;
    sorterField: string;
    visible: boolean;
    proModalVisible: boolean;
    viewItem: any;
    deleteVisible: boolean;
    deleteItem: any;
}

export default class Index extends React.Component<any, IState> {
    state: IState = {
        pageNo: 1,
        pageSize: 20,
        total: 0,
        dataSource: [],
        searchVal: undefined,
        loading: false,
        desc: true,
        sorterField: '',
        visible: false,
        proModalVisible: false,
        viewItem: undefined,
        deleteVisible: false,
        deleteItem: {}
    }

    componentDidMount () {
        this.getProjects();
    }

    getProjects = () => {
        const { pageSize, pageNo, desc, sorterField, searchVal } = this.state;
        let params: any = {
            size: pageSize,
            current: pageNo,
            search: searchVal || undefined
        }
        if (sorterField != '') {
            params.orders = [{
                field: sorterField,
                asc: !desc
            }]
        }
        API.getProjects(params).then((res: any) => {
            const { data = [], code } = res;
            if (code === 1) {
                this.setState({
                    dataSource: data.contentList || [],
                    total: +data.total || 0,
                    loading: false
                })
            }
        })
    }

    handleSearch = (query: any) => {
        this.setState({
            searchVal: query,
            pageNo: 1,
            loading: true
        }, this.getProjects)
    }

    handleSearchChange = (e) => {
        this.setState({
            searchVal: e.target.value == '' ? undefined : e.target.value
        })
    }

    handleTableChange = (pagination: any, filters: any, sorter: any) => {
        this.setState({
            pageNo: pagination.current,
            sorterField: sorter.field || '',
            desc: sorter.order == 'descend' || false,
            loading: true
        }, this.getProjects);
    }

    handleCreatProject = (data) => {
        const tenantId = utils.getCookie('dt_tenant_id');
        const createBy = utils.getCookie('dt_user_id');
        API.createProject({
            ...data,
            createBy,
            tenantId
        }).then((res: any) => {
            const { code } = res;
            if (code === 1) {
                message.success('新增成功！')
                this.setState({
                    visible: false,
                    pageNo: 1
                }, () => {
                    this.getProjects();
                })
            }
        })
    }

    handleViewPro = (record) => {
        API.getProjectByID({
            projectId: record.id
        }).then((res: any) => {
            const { data = {}, code } = res;
            if (code === 1) {
                this.setState({
                    viewItem: data,
                    proModalVisible: true
                })
            }
        })
    }

    handleCloseView = () => {
        this.setState({
            proModalVisible: false
        })
    }

    hnadleChangeProject = (key: any, value: any, reset: any) => {
        const { viewItem } = this.state;
        API.updateProjectName({
            id: viewItem.id,
            [key]: value
        }).then((res: any) => {
            const { code } = res;
            if (code === 1) {
                message.success('修改成功！');
                reset();
                this.setState({
                    viewItem: {
                        ...viewItem,
                        [key]: value
                    }
                })
            }
        })
    }

    handleViewDetail = (record) => {
        hashHistory.push({ pathname: '/entityManage', state: { ...record } })
    }

    openDeleteModal = (record) => {
        this.setState({
            deleteVisible: true,
            deleteItem: record
        })
    }

    handleDeleteModel = (type: string) => {
        const { deleteItem } = this.state;
        if (type == 'ok') {
            API.deleteProject({
                projectId: deleteItem.id
            }).then((res: any) => {
                if (res.code === 1) {
                    message.success('删除成功！');
                    this.setState({
                        deleteVisible: false,
                        pageNo: 1
                    }, () => {
                        this.getProjects();
                    })
                }
            })
        } else {
            this.setState({
                deleteVisible: false
            })
        }
    }

    initColumns = () => {
        return [{
            title: '项目显示名',
            dataIndex: 'projectAlias',
            key: 'projectAlias',
            render: (text: any, record: any) => {
                return <a onClick={this.handleViewDetail.bind(this, record)} href="javascript:;">{text}</a>
            }
        }, {
            title: '项目名称',
            dataIndex: 'projectName',
            key: 'projectName'
        }, {
            title: '标签总量',
            dataIndex: 'tagTotal',
            key: 'tagTotal'
        }, {
            title: '群组总量',
            dataIndex: 'groupTotal',
            key: 'groupTotal'
        }, {
            title: '创建者',
            dataIndex: 'createBy',
            key: 'createBy'
        }, {
            title: '创建时间',
            dataIndex: 'createAt',
            key: 'createAt',
            sorter: true
        }, {
            title: '操作',
            dataIndex: 'operation',
            key: 'operation',
            width: '150px',
            render: (text: any, record: any) => {
                return (
                    <span key={record.id}>
                        <a onClick={this.handleViewPro.bind(this, record)} href="javascript:;">
                            查看属性
                        </a>
                        <span className="ant-divider" />
                        <a onClick={this.openDeleteModal.bind(this, record)}>
                            删除
                        </a>
                    </span>
                )
            }
        }]
    }

    render () {
        const { total, pageSize, pageNo, dataSource, loading, searchVal, visible, viewItem, proModalVisible, deleteVisible } = this.state;
        const pagination: any = {
            total: total,
            pageSize: pageSize,
            current: pageNo,
            showTotal: () => (
                <div>
                    总共 <a>{total}</a> 条数据,每页显示{pageSize}条
                </div>
            )
        };
        const title = (
            <div>
                <Search
                    value={searchVal}
                    placeholder="搜索项目名称、项目显示名"
                    style={{ width: 200, padding: 0 }}
                    onSearch={this.handleSearch}
                    onChange={this.handleSearchChange}
                />
                &nbsp;&nbsp;
            </div>
        )
        const extra = (
            <Button
                type="primary"
                style={{ marginTop: 10 }}
                className="right"
                onClick={() => this.setState({ visible: true })}
            >创建项目</Button>
        )
        return (
            <div className="tage-project inner-container">
                <div className="shadow tage-project-manage">
                    <Card
                        title={title}
                        extra={extra}
                        noHovering
                        bordered={false}
                        className="noBorderBottom"
                    >
                        <Table
                            rowKey="id"
                            className="dt-ant-table--border"
                            pagination={pagination}
                            onChange={this.handleTableChange}
                            loading={loading}
                            columns={this.initColumns()}
                            dataSource={dataSource}
                        />
                    </Card>
                </div>
                <CreateForm
                    title="创建项目"
                    onOk={this.handleCreatProject}
                    visible={visible}
                    onCancel={() => this.setState({ visible: false })}
                />
                <TaskParamsModal
                    title='项目属性'
                    onCancel={this.handleCloseView}
                    visible={proModalVisible}
                    onEdit={this.hnadleChangeProject}
                    data={viewItem && [{
                        label: '项目名称',
                        value: viewItem.projectName
                    }, {
                        label: '项目显示名',
                        value: viewItem.projectAlias,
                        key: 'projectAlias',
                        edit: true
                    }, {
                        label: '项目描述',
                        value: viewItem.projectDesc,
                        key: 'projectDesc',
                        editType: 'textarea',
                        edit: true
                    }, {
                        label: '创建时间',
                        value: viewItem.gmtCreate
                    }, {
                        label: '创建人',
                        value: viewItem.createUserName
                    }, {
                        label: '管理员',
                        value: viewItem.manager
                    }]}
                />

                <DeleteModal
                    title={'删除项目'}
                    content={'项目全部内容将同步删除无法恢复，请谨慎操作！'}
                    visible={deleteVisible}
                    onCancel={this.handleDeleteModel.bind(this, 'cancel')}
                    onOk={this.handleDeleteModel.bind(this, 'ok')}
                />
            </div>
        )
    }
}
