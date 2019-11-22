import * as React from 'react'
import { hashHistory } from 'react-router';
import { Card, Table, Input, Button } from 'antd'

import CreateForm from './createForm';
import './style.scss';
import TaskParamsModal from '../../components/taskParamsModal';

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
}

export default class Index extends React.Component<any, IState> {
    state: IState = {
        pageNo: 1,
        pageSize: 20,
        total: 2,
        dataSource: [
            { id: 1, alias: '1xxxxx', name: '1yinxxxxxx', desc: '描述描述。。。。', tagSum: 222, groupSum: 333, creator: 'email2122.com', createTime: '2019-12-11 12:23:22', adminUsers: ['xxx1', 'xxx2'] },
            { id: 2, alias: '2xxxxx', name: '2yinxxxxxx', desc: '描述描述。。。。', tagSum: 222, groupSum: 333, creator: 'email2122.com', createTime: '2019-12-11 12:23:22', adminUsers: ['xxx1', 'xxx2'] }
        ],
        searchVal: undefined,
        loading: false,
        desc: true,
        sorterField: '',
        visible: false,
        proModalVisible: false,
        viewItem: undefined
    }

    componentDidMount () {
        this.loadData();
    }

    loadData = () => {
        const { pageSize, pageNo, desc, sorterField, searchVal } = this.state;
        let params = {
            pageSize,
            pageNo,
            searchVal,
            desc,
            sorterField
        }
        console.log(params);
    }

    handleSearch = (query: any) => {
        this.setState({
            searchVal: query,
            pageNo: 1,
            loading: true
        }, this.loadData)
    }

    handleTableChange = (pagination: any, filters: any, sorter: any) => {
        this.setState({
            pageNo: pagination.current,
            sorterField: sorter.field || '',
            desc: sorter.order == 'descend' || false,
            loading: true
        }, this.loadData);
    }

    handleCreatProject = (data) => {
        console.log(data);
        this.setState({
            visible: false
        })
    }

    handleViewPro = (record) => {
        this.setState({
            viewItem: record,
            proModalVisible: true
        })
    }

    handleCloseView = () => {
        this.setState({
            proModalVisible: false
        })
    }

    hnadleChangeProject = (key: any, value: any, reset: any) => {
        const { viewItem } = this.state;
        console.log(viewItem, key, value)
        reset();
    }

    handleViewDetail = (record) => {
        hashHistory.push({ pathname: '/entityManage', state: { ...record } })
    }

    initColumns = () => {
        return [{
            title: '项目显示名',
            dataIndex: 'alias',
            key: 'alias',
            render: (text: any, record: any) => {
                return <a onClick={this.handleViewDetail.bind(this, record)} href="javascript:;">{text}</a>
            }
        }, {
            title: '项目名称',
            dataIndex: 'name',
            key: 'name'
        }, {
            title: '标签总量',
            dataIndex: 'tagSum',
            key: 'tagSum'
        }, {
            title: '群组总量',
            dataIndex: 'groupSum',
            key: 'groupSum'
        }, {
            title: '创建者',
            dataIndex: 'creator',
            key: 'creator'
        }, {
            title: '创建时间',
            dataIndex: 'createTime',
            key: 'createTime',
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
                    </span>
                )
            }
        }]
    }

    render () {
        const { total, pageSize, pageNo, dataSource, loading, searchVal, visible, viewItem, proModalVisible } = this.state;
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
                        value: viewItem.name
                    }, {
                        label: '项目显示名',
                        value: viewItem.alias,
                        key: 'alias',
                        edit: true
                    }, {
                        label: '项目描述',
                        value: viewItem.desc,
                        key: 'desc',
                        editType: 'textarea',
                        edit: true
                    }, {
                        label: '创建时间',
                        value: viewItem.createTime
                    }, {
                        label: '创建人',
                        value: viewItem.creator
                    }, {
                        label: '管理员',
                        value: viewItem.adminUsers.join(', ')
                    }]}
                />
            </div>
        )
    }
}
