import React, { Component } from 'react';
import { findDOMNode } from 'react-dom';
import { connect } from 'react-redux';
import {
    Input, Button, Table, Form,
    Pagination, Modal, message,
    Tag, Icon, Card, Select
} from 'antd';

import { Link } from 'react-router';
import moment from 'moment';
import { isEmpty } from 'lodash';

import utils from 'utils';

import CatalogueTree from '../catalogTree';
import TableApplyModal from './tableApply';
import ajax from '../../../api';

const FormItem = Form.Item
const Option = Select.Option

const ROUTER_BASE = '/data-manage/search';

@connect(state => {
    return {
        projects: state.projects,
    }
})
class SearchTable extends Component {

    constructor(props) {
        super(props);
        this.state = {
            visible: false,
            table: [],
            editRecord: {},

            queryParams: {
                currentPage: 1,
                catalogueId: undefined,
                permissionStatus: undefined,
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

    apply = (applyData) => {
        ajax.applyTable(params).then(res => {
            if (res.code === 1) {
                message.success('申请成功！')
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
                title: '项目',
                key: 'projectAlias',
                dataIndex: 'projectAlias',
            },
            {
                title: '负责人',
                key: 'userName',
                dataIndex: 'userName',
                render(text, record) {
                    return text
                }
            },
            {
                title: '描述',
                width: 150,
                key: 'tableDesc',
                dataIndex: 'tableDesc',
                render(text, record) {
                    return text
                }
            },
            {
                title: '创建时间',
                key: 'createTime',
                dataIndex: 'createTime',
                render(text, record) {
                    return utils.formatDateTime(text)
                }
            },
            {
                title: 'DDL最后变更时间',
                key: 'lastDDLTime',
                dataIndex: 'lastDDLTime',
                render(text) {
                    return utils.formatDateTime(text)
                },
            },
            {
                title: '数据最后变更时间',
                key: 'lastDataChangeTime',
                dataIndex: 'lastDataChangeTime',
                render(text) {
                    return utils.formatDateTime(text)
                },
            },
            {
                title: '操作',
                key: 'id',
                width: 100,
                render(text, record) {
                    return <span>
                        <a onClick={() => ctx.showModal(record)}>申请授权</a>
                    </span>
                }
            }
        ];
    }

    render() {
        const { table, queryParams, visible, editRecord } = this.state;
        const { projects } = this.props;

        const marginTop10 = { marginTop: '8px' };

        const projectOptions = projects.map(proj => <Option
            title={proj.projectAlias}
            key={proj.id}
            name={proj.projectAlias}
            value={`${proj.id}`}
        >
            {proj.projectAlias}
        </Option>)

        const title = (
            <Form className="m-form-inline" layout="inline" style={marginTop10}>
                <FormItem label="类目">
                    <span style={{ width: 120, display: 'inline-block' }}>
                        <CatalogueTree
                            id="filter-catalogue"
                            isPicker
                            isFolderPicker
                            value={queryParams.catalogue}
                            placeholder="按数据类目查询"
                            onChange={(value) => this.changeParams('catalogueId', value)}
                            treeData={this.state.dataCatalogue}
                        />
                    </span>
                </FormItem>
                <FormItem label="授权状态">
                    <Select
                        allowClear
                        style={{ width: 120 }}
                        placeholder="选择指标类型"
                        onChange={(value) => this.changeParams('permissionStatus', value)}
                    >
                        <Option value="1">全部表</Option>
                        <Option value="2">授权成功</Option>
                        <Option value="3">需要授权</Option>
                    </Select>
                </FormItem>
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
            <div className="box-1 m-card card-tree-select" style={{ paddingBottom: 20 }}>
                <Card noHovering bordered={false} title={title}>
                    <div style={{ marginTop: '1px' }}>
                        <Table
                            rowKey="id"
                            className="m-table"
                            columns={this.initialColumns()}
                            dataSource={table.data}
                            pagination={pagination}
                            onChange={this.handleTableChange.bind(this)}
                        />
                    </div>
                </Card>
                <TableApplyModal 
                    visible={visible}
                    table={editRecord}
                    onOk={this.apply}
                    onCancel={() => {this.setState({visible: false, editRecord: {} })}}
                />
            </div>
        </div>
    }

}

export default SearchTable;