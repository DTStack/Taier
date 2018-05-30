import React, { Component } from 'react';
import { connect } from 'react-redux';
import {
    Input, Button, Table, Form,
    Pagination, Modal, message,
    Tag, Icon, Card, Select, Tabs
} from 'antd';

import { Link } from 'react-router';
import moment from 'moment';
import { isEmpty } from 'lodash';

import utils from 'utils';

import ajax from '../../../api';

const FormItem = Form.Item
const Option = Select.Option
const TabPane = Tabs.TabPane

const ROUTER_BASE = '/data-manage/table';

@connect(state => {
    return {
        projects: state.projects,
    }
})
class AuthMana extends Component {

    constructor(props) {
        super(props);
        this.state = {
            table: [],
            editRecord: {},

            queryParams: {
                listType: 0,
                currentPage: 1,
                pageSize: 10,
                catalogueId: undefined,
                projectId: undefined,
                tableName: undefined,
            },
        }
    }

    componentDidMount() {
        this.search();
        this.loadCatalogue();
    }

    componentWillReceiveProps(nextProps) {

    }

    search = () => {
        const params = this.state.queryParams;
        ajax.searchTable(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    table: res.data,
                })
            }
        })
    }

    cancleMark = (applyData) => {
        ajax.cancleMark(params).then(res => {
            if (res.code === 1) {
                message.success('取消成功！')
            }
        })
    }

    changeParams = (field, value) => {
        let queryParams = Object.assign(this.state.queryParams);
        if (field) {
            queryParams[field] = value;
        }
        this.setState({
            queryParams,
        }, this.search)
    }

    loadCatalogue = () => {
        ajax.getDataCatalogues().then(res => {
            this.setState({
                dataCatalogue: res.data && [res.data],
                currentPage: 1,
            })
        })
    }

    handleTableChange = (pagination, filters, sorter) => {
        const queryParams = Object.assign(this.state.queryParams, {
            currentPage: pagination.current
        })
        this.setState({
            queryParams,
        }, this.search)
    }

    onTableNameChange = (e) => {
        this.setState({
            queryParams: Object.assign(this.state.queryParams, {
                tableName: e.target.value,
                currentPage: 1,
            }),
        })
    }

    showModal = (editRecord) => {
        this.setState({
            visible: true,
            editRecord,
        });
    }

    initialColumns = () => {
        const ctx = this;
        const { queryParams } = this.state
        return [
            {
                title: '表名',
                width: 120,
                key: 'tableName',
                dataIndex: 'tableName',
                render(text, record) {
                    return <Link to={`${ROUTER_BASE}/view/${record.tableId}`}>{text}</Link>
                }
            },
            {
                title: '类目',
                key: 'catalogue',
                dataIndex: 'catalogue',
                render(text, record) {
                    return text
                },
            },
            {
                title: 'project',
                key: 'project',
                dataIndex: 'project',
            },
            {
                title: '项目名',
                key: 'projectAlias',
                dataIndex: 'projectAlias',
            },
            {
                title: '类型',
                key: 'resourceType',
                dataIndex: 'resourceType',
                render(text) {
                    if (text === '0') {
                        return '表'
                    } else if (text === '1') {
                        return '函数'
                    } else if (text === '2') {
                        return '资源'
                    } else return '-'
                }
            },
            {
                title: '申请时间',
                key: 'applyTime',
                dataIndex: 'applyTime',
                render(text, record) {
                    return utils.formatDateTime(text)
                }
            },
            {
                title: '申请原因',
                key: 'applyReason',
                dataIndex: 'applyReason',
            },
            {
                title: '操作',
                key: 'id',
                width: 120,
                render(text, record) {
                    switch (queryParams.listType) {
                        case '1':
                        case '2':
                        case '3':
                            return <span>
                                <Link to={`${ROUTER_BASE}/edit/${record.tableId}`}>通过</Link>
                                <span className="ant-divider"></span>
                                <Link to={`/data-manage/log/${record.tableId}/${record.tableName}`}>驳回</Link>
                            </span>
                        case '5':
                        return <span>
                                <a onClick={() => this.cancleMark(record)}>取消收藏</a>
                            </span>
                        case '4':
                        default: 
                            return '--';
                    }
                }
            }
        ];
    }


    renderPane = () => {
        const { table, queryParams, editRecord } = this.state;
        const { projects } = this.props;

        const projectOptions = projects.map(proj => <Option
            title={proj.projectAlias}
            key={proj.id}
            name={proj.projectAlias}
            value={`${proj.id}`}
        >
            {proj.projectAlias}
        </Option>)

        const title = (
            <Form className="m-form-inline" layout="inline" style={{marginTop: '10px'}}>
                <FormItem label="项目">
                    <Select
                        allowClear
                        showSearch
                        optionFilterProp="name"
                        style={{ width: 120 }}
                        placeholder="选择项目"
                        onChange={(value) => this.changeParams('projectId', value)}
                    >
                        {projectOptions}
                    </Select>
                </FormItem>
                <FormItem>
                    <Input.Search
                        placeholder="按表名搜索"
                        style={{ width: 200 }}
                        size="default"
                        onChange={this.onTableNameChange}
                        onSearch={this.search}
                    />
                </FormItem>
            </Form>
        )

        const pagination = {
            total: table.totalCount,
            defaultPageSize: 10,
        };

        return <div className="m-tablelist">
            <div className="m-card card-tree-select" style={{ paddingBottom: 20 }}>
                <Card noHovering bordered={false} title={title}>
                    <div style={{ marginTop: '1px' }}>
                        <Table
                            rowKey="applyId"
                            className="m-table"
                            columns={this.initialColumns()}
                            dataSource={table.data}
                            pagination={pagination}
                            onChange={this.handleTableChange}
                        />
                    </div>
                </Card>
            </div>
        </div>
    }

    render() {


        return (
            <div className="box-1 m-tabs">
                <Tabs 
                    animated={false} 
                    style={{height: 'auto'}} 
                    onChange={value => this.changeParams('listType', value)}
                >
                    <TabPane tab="待我审批" key="1">
                        {this.renderPane()}
                    </TabPane>
                    <TabPane tab="申请记录" key="2">
                        {this.renderPane()}
                    </TabPane>
                    <TabPane tab="已处理" key="3">
                        {this.renderPane()}
                    </TabPane>
                    <TabPane tab="权限回收" key="4">
                        {this.renderPane()}
                    </TabPane>
                </Tabs>
            </div>
        )
    }
}

export default AuthMana;
