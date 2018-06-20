import React, { Component } from 'react';
import { findDOMNode } from 'react-dom';
import { connect } from 'react-redux';
import {
    Input, Button, Table, Form,
    Pagination, Modal, message,
    Tag, Icon, Card, Select,Spin,
    Tooltip
} from 'antd';

import { Link } from 'react-router';
import moment from 'moment';
import { isEmpty } from 'lodash';

import utils from 'utils';
import { APPLY_RESOURCE_TYPE } from "../../../comm/const";
import CatalogueTree from '../catalogTree';
import TableApplyModal from './tableApply';
import ajax from '../../../api/dataManage';

const FormItem = Form.Item
const Option = Select.Option

const ROUTER_BASE = '/data-manage/table';

@connect(state => {
    return {
        projects: state.allProjects,
    }
})
class SearchTable extends Component {

    constructor(props) {
        super(props);
        this.state = {
            visible: false,
            table: [],
            editRecord: {},
            cardLoading:false,
            queryParams: {
                pageIndex: 1,
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
        const { cardLoading } = this.state;
        this.setState({
            cardLoading: true,
            table: [],
        })
        const { queryParams } = this.state;
        ajax.newSearchTable(queryParams).then(res => {
            if (res.code === 1) {
                this.setState({
                    table: res.data,
                    cardLoading: false,
                })
            }else{
                this.setState({
                    cardLoading: false,
                })
            }
        })
    }

    apply = (applyData) => {
        const { editRecord } = this.state;
        const params = {...applyData};
        params.applyResourceType = APPLY_RESOURCE_TYPE.TABLE;
        params.resourceId = editRecord.id;
        ajax.applyTable(params).then(res => {
            if (res.code === 1) {
                message.success('申请成功！')
                this.setState({visible:false},this.search)
            }
        })
    }

    changeParams = (field, value) => {
        let queryParams = Object.assign(this.state.queryParams);
        queryParams.pageIndex = 1;
        if (field) {
            queryParams[field] = value === "all" ? "" : value;
        }
        this.setState({
            queryParams,
        }, this.search)
    }

    loadCatalogue = () => {
        ajax.getDataCatalogues().then(res => {
            this.setState({
                dataCatalogue: res.data && [res.data],
                pageIndex: 1,
            })
        })
    }

    handleTableChange = (pagination, filters, sorter) => {
        const queryParams = Object.assign(this.state.queryParams, {
            pageIndex: pagination.current
        })
        this.setState({
            queryParams,
        }, this.search)
    }

    onTableNameChange = (e) => {
        this.setState({
            queryParams: Object.assign(this.state.queryParams, {
                tableName: e.target.value,
                pageIndex: 1,
            }),
        })
    }

    showModal = (editRecord) => {
        this.setState({
            visible: true,
            editRecord,
        });
    }

    characterProcess = (text="",maxWidth="300px") => {
        const style ={overflow: "hidden",
            maxWidth,
            textOverflow: "ellipsis",
            whiteSpace: "nowrap"}
        const content = (
        <Tooltip title={text} >
            <div style ={style}>{text}</div>
        </Tooltip>
        )
       
        return content
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
                    return <Link to={`${ROUTER_BASE}/view/${record.id}`}>{text}</Link>
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
                key: 'project',
                dataIndex: 'project',
            },
            {
                title: '负责人',
                key: 'chargeUser',
                dataIndex: 'chargeUser',
                render(text, record) {
                    return text
                }
            },
            {
                title: '描述',
                width: 150,
                key: 'tableDesc',
                dataIndex: 'tableDesc',
                render : text => this.characterProcess(text,"150px"),
               
            },
            {
                title: '创建时间',
                key: 'gmtCreate',
                dataIndex: 'gmtCreate',
                render(text, record) {
                    return utils.formatDateTime(text)
                }
            },
            {
                title: '表结构最后变更时间',
                key: 'lastDdlTime',
                dataIndex: 'lastDdlTime',
                render(text) {
                    return utils.formatDateTime(text)
                },
            },
            {
                title: '数据最后变更时间',
                key: 'lastDmlTime',
                dataIndex: 'lastDmlTime',
                render(text) {
                    return utils.formatDateTime(text)
                },
            },
            {
                title: '操作',
                key: 'id',
                dataIndex: 'permissionStatus',
                width: 100,
                render(status, record) {
                    // 授权状态 0-未授权，1-已授权,2-待审批
                    switch(status) {
                        case 0:
                            return <span><a onClick={() => ctx.showModal(record)}>申请授权</a></span>
                        case 1:
                            return <span>授权成功</span>
                        case 2:
                            return <span>等待授权</span>
                        default: return '-';
                    }
                }
            }
        ];
    }



    render() {
        const { table, queryParams, visible, editRecord, cardLoading } = this.state;
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
                    <span style={{ width: 200, display: 'inline-block' }}>
                        <CatalogueTree
                            id="filter-catalogue"
                            isPicker
                            isFolderPicker
                            value={queryParams.catalogueId}
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
                        <Option value="all">全部表</Option>
                        <Option value="0">申请授权</Option>
                        <Option value="1">授权成功</Option>
                        <Option value="2">等待授权</Option>
                    </Select>
                </FormItem>
                <FormItem label="项目">
                    <Select
                        allowClear
                        showSearch
                        optionFilterProp="name"
                        style={{ width: 120 }}
                        placeholder="选择项目"
                        onChange={(value) => this.changeParams('pId', value)}
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
                        <Card noHovering bordered={false} title={title} >
                            <Spin tip="正在加载中..." spinning={cardLoading}>
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
                            </Spin>
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
