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

import actions from '../../store/modules/dataManage/actionCreator';
import CatalogueTree from './catalogTree';
import ajax from '../../api';

const FormItem = Form.Item

class TableList extends Component {

    constructor(props) {
        super(props);
        this.state = {
            visible: false,
            filterDropdownVisible: false,
            dataCatalogue: [],
            catalogue: undefined,
        }
    }

    componentDidMount() {
        this.props.searchTable();
        this.loadCatalogue();
    }

    loadCatalogue = () => {
        ajax.getDataCatalogues().then(res => {
            this.setState({
                dataCatalogue: res.data && [res.data],
            })
        })
    }

    catalogueChange = (value) => {
        this.setState({
            catalogue: value,
        }, () => {
            this.props.searchTable({
                catalogueId: value
            })
        })
    }

    render() {
        const ROUTER_BASE = '/data-manage/table';
        const { tableList } = this.props;
        const { totalCount, currentPage, listData } = tableList;
        const columns = [
            {
                title: '表名',
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
                        <span> | </span>
                        <Link to={`/data-manage/log/${record.tableId}/${record.tableName}`}>操作记录</Link>
                    </span>
                }
            }
        ];

        return <div className="m-tablelist section">
            <h1 className="title black" style={{ paddingTop: '0' }}>
                表管理
            </h1>
            <Card>
            <div className="search-bar" style={{ marginBottom: 10 }}>
                <Button type="primary" style={{ float: 'right', marginLeft: 5 }}>
                    <Link to={`${ROUTER_BASE}/create`}>新建表</Link>
                </Button>
                <Button type="primary" style={{ float: 'right' }}
                    onClick={ this.showModal.bind(this) }
                >DDL建表</Button>

                <Form layout="inline">
                    <FormItem>
                        <span style={{ width: '200px', display: 'inline-block' }}>
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
                            onSearch={ this.searchTableByName.bind(this) }
                            ref={ el => this.searchInput = el }
                        />
                    </FormItem>
                </Form>
                
                {/* <Button type="default"
                    onClick={ this.cleanSearch.bind(this) }
                    style={{ marginLeft: 5 }}
                >清除</Button> */}
            </div>
            <Table
                rowKey="id"
                columns={ columns }
                dataSource={ listData }
                pagination={ false }
                onChange={ this.handleTableChange.bind(this) }
            />
            <div className="pager" style={{ float: 'right', marginTop: 10 }}>
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
                    onChange={ this.handleDdlChange.bind(this) } 
                    value={ this._DDL } ref={(e) => { this.DDLEditor = e }}
                />
            </Modal>
            </Card>
        </div>
    }

    showModal() {
        this.setState({
            visible: true
        });
    }

    handleTableChange(pagination, filters, sorter) {
        if(isEmpty(sorter)) this.props.searchTable();
        else {
            let { field, order } = sorter;

            this.props.searchTable({
                [field === 'lastDataChangeTime' ? 'timeSort' : 'sizeSort']:
                    order === 'descend' ? 'desc' : 'asc'
            });
        }
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

    showPage(page, pageSize) {
        this.props.searchTable({
            pageIndex: page
        });
    }

    searchTableByName(name) {
        this.props.searchTable({
            tableName: name
        });
    }

    cleanSearch() {
        const $input = findDOMNode(this.searchInput).querySelector('input');

        if($input.value.trim() === '') return;

        $input.value = '';
        this.props.searchTable();
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
        tableList: state.dataManage.tableManage.tableList
    }
}, mapDispatch)(TableList);