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

class TableList extends Component {

    constructor(props) {
        super(props);
        this.state = {
            visible: false,
            current: 1,
            tableName: '',
            filterDropdownVisible: false,
            dataCatalogue: [],
            catalogue: undefined,
            timeSort: '',
            sizeSort: '',
        }
    }

    componentDidMount() {
        this.search();
        this.loadCatalogue();
    }

    componentWillReceiveProps(nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.setState({ current: 1 }, () => {
                this.search();
                this.loadCatalogue();
            })
        }
    }

    search = () => {
        const params = this.getReqParams();
        this.props.searchTable(params);
    }

    getReqParams = () => {
        const { tableName, current, catalogue, timeSort, sizeSort } = this.state;
        const params = {
            pageIndex: current || 1,
            tableName: tableName || '',
        }
        if (catalogue) {
            params.catalogueId = catalogue
        }
        if (timeSort) {
            params.timeSort = timeSort
        }
        if (sizeSort) {
            params.sizeSort = sizeSort
        }
        return params;
    }

    showPage(page, pageSize) {
        this.setState({
            current: page,
        }, this.search)
    }

    cleanSearch() {
        const $input = findDOMNode(this.searchInput).querySelector('input');

        if($input.value.trim() === '') return;

        $input.value = '';
        this.search();
    }

    loadCatalogue = () => {
        ajax.getDataCatalogues().then(res => {
            this.setState({
                dataCatalogue: res.data && [res.data],
                current: 1,
            })
        })
    }

    handleTableChange(pagination, filters, sorter) {
        const params = {
            current: pagination.current,
            timeSort: '',
            sizeSort: '',
        };
        if (sorter) {
            let { field, order } = sorter;
            params[
                field === 'lastDataChangeTime' ? 'timeSort' : 'sizeSort'
            ] = order === 'descend' ? 'desc' : 'asc'
        }
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

    render() {
        const ROUTER_BASE = '/data-manage/table';
        const { tableList } = this.props;
        const { totalCount, currentPage, listData } = tableList;
        const columns = [
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

        return <div className="m-tablelist">
            <h1 className="box-title"> 表管理 </h1>
            <div className="box-2 m-card card-tree-select" style={{ paddingBottom: 20 }}>
                <Card noHovering bordered={false} title={title} extra={extra}>
                    <div style={{ marginTop: '1px' }}>
                        <Table
                            rowKey="id"
                            className="m-table"
                            columns={ columns }
                            dataSource={ listData }
                            pagination={ false }
                            onChange={ this.handleTableChange.bind(this) }
                        />
                        <div className="pager" style={{ float: 'right', margin: '16px 20px 0 0' }}>
                            <Pagination
                                pageSize={ 10 }
                                current={ currentPage }
                                total={ totalCount }
                                onChange={ this.showPage.bind(this) }
                            />
                        </div>
                        <Modal className="m-codemodal"
                            width="750"
                            title="DDL建表"
                            visible={this.state.visible}
                            onOk={this.handleOk.bind(this)}
                            onCancel={this.handleCancel.bind(this)}
                        >
                            <Editor
                                style={{height:"400px"}}
                                placeholder={DDL_placeholder}
                                onChange={ this.handleDdlChange.bind(this) } 
                                value={ this._DDL } ref={(e) => { this.DDLEditor = e }}
                            />
                        </Modal>
                    </div>
                </Card>
            </div>
        </div>
    }

    showModal() {
        this.setState({
            visible: true
        });
    }

    handleOk() {
        if(this._DDL) {
            ajax.createDdlTable({
                sql: this._DDL
            }).then(res => {
                if(res.code === 1) {
                    if(!res.data) {
                        this._DDL = undefined;
                        // 设置值
                        this.DDLEditor.self.doc.setValue('');
                        this.setState({
                            visible: false
                        });
                        message.info('建表成功');
                        this.props.searchTable();
                    }
                    else {
                        message.error(res.data.message)
                    }
                }
            })
        }
        else {
            message.error('请输入建表语句!');
        }
    }
    handleCancel() {
        this._DDL = undefined;
        this.setState({
            visible: false
        })
    }

    handleDdlChange(previous, value) {
        this._DDL = value;
    }
}

const mapDispatch = dispatch => ({
    searchTable(params) {
        const paData = params || {}
        paData.isDeleted = 0 // 添加删除标记
        paData.isDirtyDataTable = 0 // 非脏数据标记
        dispatch(actions.searchTable(paData));
    }
});

export default connect((state) => {
    return {
        project: state.project,
        tableList: state.dataManage.tableManage.tableList
    }
}, mapDispatch)(TableList);