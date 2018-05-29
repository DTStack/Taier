import React, { Component } from 'react';
import { findDOMNode } from 'react-dom';
import { connect } from 'react-redux';
import { 
    Input, Button, Table, Form,
    Pagination, Modal, message, 
    Tag, Icon, Card
} from 'antd';

import { Link } from 'react-router';
import moment from 'moment';
import { isEmpty } from 'lodash';

import Editor from '../../components/code-editor';
import {DDL_placeholder} from "../../comm/DDLCommon"

import actions from '../../store/modules/dataManage/actionCreator';
import CatalogueTree from './catalogTree';
import ajax from '../../api';

const FormItem = Form.Item

const ROUTER_BASE = '/data-manage/table';

class SearchTable extends Component {

    constructor(props) {
        super(props);
        this.state = {
            visible: false,
            table: [],

            queryParams : {
                current: 1,
                catalogue: '',
                authStatus: '',
                project: '',
                tableName: '',
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
        const params = this.getReqParams();
        this.props.searchTable(params);
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
                current: 1,
            })
        })
    }

    handleTableChange = (pagination, filters, sorter) => {
        const params = Object.assign(this.state.params, { 
            currentPage: pagination.current 
        })
        this.setState(params, this.search)
    }

    onTableNameChange = (e) => {
        this.setState({
            tableName: e.target.value,
            current: 1,
        })
    }

    catalogueChange = (value) => {
        this.setState({
            catalogue: value,
        }, this.search)
    }

    showModal() {
        this.setState({
            visible: true
        });
    }

    initialColumns = () => {

        return [
            {
                title: '表名',
                width: 120,
                key: 'tableName',
                dataIndex: 'tableName',
                render(text, record) {
                    return <Link to={`${ROUTER_BASE}/view/${record.tableId}`}>{ text }</Link>
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
                title: '创建者',
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
                title: '最近更新时间',
                key: 'lastDataChangeTime',
                dataIndex: 'lastDataChangeTime',
                sorter: true,
                render(text, record) {
                    return moment(text).format('YYYY-MM-DD HH:mm:ss')
                }
            },
            {
                title: '占用存储',
                key: 'storeSize',
                width: 90,
                dataIndex: 'storeSize',
                sorter: true
            },
            {
                title: '生命周期',
                key: 'lifeDay',
                dataIndex: 'lifeDay',
                render(text) {
                    return text ? <span>{text}天</span> : ''
                }
            },
            {
                title: '操作',
                key: 'action',
                render(text, record) {
                    return <span>
                        <Link to={`${ROUTER_BASE}/edit/${record.tableId}`}>编辑</Link>
                        <span className="ant-divider"></span>
                        <Link to={`/data-manage/log/${record.tableId}/${record.tableName}`}>操作记录</Link>
                    </span>
                }
            }
        ];
    }

    render() {
        const { table, current } = this.state;
        const { totalCount, currentPage, listData } = tableList;

        const marginTop10 = { marginTop: '8px' }

        const title = (
            <Form className="m-form-inline" layout="inline" style={marginTop10}>
                <FormItem>
                    <span style={{ width: '200px', display: 'inline-block'}}>
                        <CatalogueTree
                            id="filter-catalogue"
                            isPicker
                            isFolderPicker
                            value={this.state.catalogue}
                            placeholder="按数据类目查询"
                            onChange={this.catalogueChange}
                            treeData={this.state.dataCatalogue}
                        />
                    </span>
                </FormItem>
                <FormItem>
                    <Input.Search
                        placeholder="按表名搜索"
                        style={{ width: 200 }}
                        size="default"
                        onChange={ this.onTableNameChange }
                        onSearch={ this.search }
                        ref={ el => this.searchInput = el }
                    />
                </FormItem>
            </Form>
        )

        const extra = (
            <div style={marginTop10}>
                <Button type="primary" style={{ float: 'right', marginLeft: 5 }}>
                    <Link to={`${ROUTER_BASE}/create`}>新建表</Link>
                </Button>
                <Button type="primary" style={{ float: 'right', marginLeft: 5 }}>
                    <Link to={`/data-model/table/design`}>根据模型建表</Link>
                </Button>
                <Button type="primary" style={{ float: 'right' }}
                    onClick={ this.showModal.bind(this) }
                >DDL建表</Button>
            </div>
        )

        const pagination = {
            total: table.totalCount,
            defaultPageSize: 10,
        };

        return <div className="m-tablelist">
            <h1 className="box-title"> 表管理 </h1>
            <div className="box-2 m-card card-tree-select" style={{ paddingBottom: 20 }}>
                <Card noHovering bordered={false} title={title} extra={extra}>
                    <div style={{ marginTop: '1px' }}>
                        <Table
                            rowKey="id"
                            className="m-table"
                            columns={ this.initialColumns() }
                            dataSource={ table.data }
                            pagination={ pagination }
                            onChange={ this.handleTableChange.bind(this) }
                        />
                    </div>
                </Card>
            </div>
        </div>
    }

}

export default SearchTable;