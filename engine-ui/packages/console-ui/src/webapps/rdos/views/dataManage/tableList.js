import React, { Component } from 'react';
import { findDOMNode } from 'react-dom';
import { connect } from 'react-redux';
import {
    Input, Button, Table, Form,
    Pagination, Modal, message,
    Tag, Icon, Card, Select, Tabs,
    Spin,
} from 'antd';

import { Link } from 'react-router';
import moment from 'moment';
import { isEmpty } from 'lodash';

import utils from 'utils';

import CatalogueTree from './catalogTree';
import ajax from '../../api/dataManage';

const FormItem = Form.Item
const Option = Select.Option
const TabPane = Tabs.TabPane

const ROUTER_BASE = '/data-manage/table';

@connect(state => {
    return {
        projects: state.projects,
    }
})
class TableList extends Component {

    constructor(props) {
        super(props);
        this.state = {
            table: [],
            editRecord: {},
            loading:false,
            queryParams: {
                listType: "1",
                pageIndex: 1,
                pageSize: 10,
                catalogueId: undefined,
                pId: undefined,
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
        const { queryParams } = this.state;
        this.setState({table:[],loading: true})
        ajax.newSearchTable(queryParams).then(res => {
            if (res.code === 1) {
                this.setState({
                    table: res.data,
                    loading: false,
                })
            }else{
                this.setState({
                    loading: false,
                })
            }
        })
    }

    cancleMark = (tableId) => {
        const params = {tableId};
        ajax.cancelMark(params).then(res => {
            if (res.code === 1) {
                message.success('取消成功！')
                this.search()
            }
        })
    }

    changeParams = (field, value) => {
        let queryParams = Object.assign(this.state.queryParams);
        if (field) {
            queryParams[field] = value;
            queryParams.pageIndex = 1 ;
        }
        this.setState({
            queryParams,
        }, this.search)
    }

    loadCatalogue = () => {
        ajax.getDataCatalogues().then(res => {
            this.setState({
                dataCatalogue: res.data && [res.data],
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
    
    cursorActivity(){
        console.log(arguments)
    }

    initialColumns = () => {
        const ctx = this;
        const { queryParams } = this.state
        console.log('initialColumns',queryParams);
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
                title: '创建时间',
                key: 'createTime',
                dataIndex: 'createTime',
                render(text, record) {
                    return utils.formatDateTime(text)
                }
            },
            {
                title: '占用存储',
                key: 'tableSize',
                dataIndex: 'tableSize',
            },
            {
                title: '生命周期',
                key: 'lifeDay',
                dataIndex: 'lifeDay',
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
                                <Link to={`${ROUTER_BASE}/edit/${record.id}`}>编辑</Link>
                                <span className="ant-divider"></span>
                                <Link to={`/data-manage/log/${record.id}/${record.tableName}`}>操作记录</Link>
                            </span>
                        case '5':
                        return <span>
                                <a onClick={() => ctx.cancleMark(record.id)}>取消收藏</a>
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
        const { table, queryParams, editRecord,loading } = this.state;
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
                <FormItem label="类目">
                    <span style={{ width: 120, display: 'inline-block' }}>
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
                <FormItem label="项目">
                    <Select
                        allowClear
                        showSearch
                        optionFilterProp="name"
                        style={{ width: 120 }}
                        placeholder="选择项目"
                        value={queryParams.pId}
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
                        value={queryParams.tableName}
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
                <Spin spinning={loading} tip="正在加载数据...">
                    <Card noHovering bordered={false} title={title}>
                        <div style={{ marginTop: '1px' }}>
                            <Table
                                rowKey="id"
                                className="m-table"
                                columns={this.initialColumns()}
                                dataSource={table.data}
                                pagination={pagination}
                                onChange={this.handleTableChange}
                            />
                        </div>
                    </Card>
                </Spin>
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
                    <TabPane tab="我近期操作的表" key="1">
                        {this.renderPane()}
                    </TabPane>
                    <TabPane tab="个人账号的表" key="2">
                        {this.renderPane()}
                    </TabPane>
                    <TabPane tab="我管理的表" key="3">
                        {this.renderPane()}
                    </TabPane>
                    <TabPane tab="被授权的表" key="4">
                        {this.renderPane()}
                    </TabPane>
                    <TabPane tab="我收藏的表" key="5">
                        {this.renderPane()}
                    </TabPane>
                </Tabs>
            </div>
        )
    }
}

export default TableList;