import React, { Component } from 'react';
import { connect } from 'react-redux';
import {
    Input, Button, Table, Form,
    Pagination, Modal, message, Checkbox,
    Tag, Icon, Card, Select, Tabs
} from 'antd';

import { Link } from 'react-router';
import moment from 'moment';
import { isEmpty } from 'lodash';

import utils from 'utils';

import ajax from '../../../api/dataManage';
import ApprovalModal from './approvalModal';

const FormItem = Form.Item
const Option = Select.Option
const TabPane = Tabs.TabPane

const ROUTER_BASE = '/data-manage/table';

const applyStatus = (status) => {
    if (status === 0) {
        return <span>待审批</span>
    } else if (status === 1) {
        return <span>通过</span>
    }
}

const revokeStatus = (status) => {
    if (status === 0) {
        return <span>否</span>
    } else if (status === 1) {
        return <span>已回收</span>
    }
}


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
            checkAll: false,
            selectedRowKeys: [],
            agreeApply: undefined,

            queryParams: {
                listType: "1",
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

    approveApply = (params) => {
        ajax.approveApply(params).then(res => {
            if (res.code === 1) {
                message.success('操作成功！')
            }
        })
    }

    revoke = (params) => {
        ajax.revoke(params).then(res => {
            if (res.code === 1) {
                message.success('回收成功！')
            }
        })
    }

    cancelApply = (params) => {
        ajax.cancelApply(params).then(res => {
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

    onSelectChange = (selectedRowKeys) => {
        this.setState({ selectedRowKeys });
    }

    onCheckAllChange = (e) => {
        let selectedRowKeys = []

        if (e.target.checked) {
            selectedRowKeys = this.state.table.data.map(item => item.id )
        }

        this.setState({
            checkAll: e.target.checked,
            selectedRowKeys
        })
    }

    tableFooter = (currentPageData) => {
        const { queryParams } = this.state;

        let operation = '';
        switch (queryParams.listType) {
            case '1': { // 待审批
                return (
                    <div className="ant-table-row  ant-table-row-level-0">
                        <div style={{ padding: '15px 10px 10px 30px', display:"inline-block" }}>
                            <Checkbox
                                checked={ this.state.checkAll }
                                onChange={ this.onCheckAllChange }
                            >
                            </Checkbox>
                        </div>
                        <div style={{display:"inline-block", marginLeft: '15px'}}>
                            <Button type="primary" size="small" onClick={this.approveApply}>批量通过</Button>&nbsp;
                            <Button type="primary" size="small" onClick={this.approveApply}>批量驳回</Button>&nbsp;
                        </div>
                    </div>
                )
            }
            case '4': { // 权限回收
                return (
                    <div className="ant-table-row  ant-table-row-level-0">
                        <div style={{ padding: '15px 10px 10px 30px', display:"inline-block" }}>
                            <Checkbox
                                checked={ this.state.checkAll }
                                onChange={ this.onCheckAllChange }
                            >
                            </Checkbox>
                        </div>
                        <div style={{display:"inline-block", marginLeft: '15px'}}>
                            <Button type="primary" size="small" onClick={this.approveApply}>批量回收</Button>&nbsp;
                        </div>
                    </div>
                )
            }
            case '2': // 申请记录
            case '3':  // 已处理
            default:
                return null;
        }

    }

    initialColumns = () => {
        const ctx = this;
        const { queryParams } = this.state;
        const baseCols = [
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
                title: '申请人',
                key: 'applyPerson',
                dataIndex: 'applyPerson'
            },
        ];

        switch (queryParams.listType) {
            case '1': { // 待审批
                return baseCols.concat(
                    [
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
                                return <span>
                                    <a onClick={() => {
                                        ctx.setState({
                                            visible: true,
                                            agreeApply: true,
                                            editRecord: record,
                                        })
                                    }}>通过</a>
                                    <span className="ant-divider"></span>
                                    <a onClick={() => {
                                        ctx.setState({
                                            visible: true,
                                            agreeApply: false,
                                            editRecord: record,
                                        })
                                    }}>驳回</a>
                                </span>
                            }
                        }
                    ]
                )
            }

            case '2': {  // 申请记录 
                return baseCols.concat(
                    [
                        {
                            title: '申请时间',
                            key: 'applyTime',
                            dataIndex: 'applyTime',
                            render(text, record) {
                                return utils.formatDateTime(text)
                            }
                        },
                        {
                            title: '有效时间',
                            key: 'day',
                            dataIndex: 'day',
                        },
                        {
                            title: '审批状态',
                            key: 'applyStatus',
                            dataIndex: 'applyStatus',
                            render(status) {
                                return applyStatus(status);
                            }
                        },
                        {
                            title: '收回状态',
                            key: 'isRevoke',
                            dataIndex: 'isRevoke',
                            render(status) {
                                return revokeStatus(status);
                            }
                        },
                        {
                            title: '申请详情',
                            key: 'applyReason',
                            dataIndex: 'applyReason',
                        },
                        {
                            title: '操作',
                            key: 'id',
                            width: 120,
                            render(text, record) {
                                return <span>
                                    <a onClick={ctx.cancelApply}>撤销</a>
                                </span>
                            }
                        }
                    ]
                )
            }
            case '3': {  // 已处理 
                return baseCols.concat(
                    [
                        {
                            title: '申请时间',
                            key: 'applyTime',
                            dataIndex: 'applyTime',
                            render(text, record) {
                                return utils.formatDateTime(text)
                            }
                        },
                        {
                            title: '有效时间',
                            key: 'day',
                            dataIndex: 'day',
                        },
                        {
                            title: '审批状态',
                            key: 'applyStatus',
                            dataIndex: 'applyStatus',
                            render(status) {
                                return applyStatus(status);
                            }
                        },
                        {
                            title: '收回状态',
                            key: 'isRevoke',
                            dataIndex: 'isRevoke',
                            render(status) {
                                return revokeStatus(status);
                            }
                        },
                        {
                            title: '审批人',
                            key: 'approvalPerson',
                            dataIndex: 'approvalPerson',
                        },
                        {
                            title: '审批意见',
                            key: 'reply',
                            dataIndex: 'reply',
                        }
                    ]
                )
            }
            case '4': {  // 权限回收 
                return baseCols.concat(
                    [
                        {
                            title: '审批结果',
                            key: 'applyStatus',
                            dataIndex: 'applyStatus',
                            render(status) {
                                return applyStatus(status);
                            }
                        },
                        {
                            title: '审批意见',
                            key: 'reply',
                            dataIndex: 'reply',
                        },
                        {
                            title: '处理时间',
                            key: 'handTime',
                            dataIndex: 'handTime',
                            render(text, record) {
                                return utils.formatDateTime(text)
                            }
                        },
                        {
                            title: '操作',
                            key: 'id',
                            width: 120,
                            render(text, record) {
                                return <span>
                                    <a onClick={ctx.revoke}>收回</a>
                                </span>
                            }
                        }
                    ]
                )
            }
            default: 
                return [];
        }
    }

    renderPane = () => {
        const { table, selectedRowKeys, } = this.state;

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

        const rowSelection = {
            selectedRowKeys,
            onChange: this.onSelectChange,
        };

        return <div className="m-tablelist">
            <div className="m-card card-tree-select" style={{ paddingBottom: 20 }}>
                <Card noHovering bordered={false} title={title}>
                    <div style={{ marginTop: '1px' }}>
                        <Table
                            rowKey="id"
                            className="m-table"
                            rowSelection={rowSelection}
                            columns={this.initialColumns()}
                            dataSource={table.data}
                            pagination={pagination}
                            onChange={this.handleTableChange}
                            footer={this.tableFooter}
                        />
                    </div>
                </Card>
            </div>
        </div>
    }

    render() {
        const { editRecord, visible, agreeApply, } = this.state;
        return (
            <div className="box-1 m-tabs">
                <Tabs 
                    animated={false} 
                    style={{height: 'auto'}} 
                    onChange={value => this.changeParams('listType', value)}
                >
                    <TabPane tab="待我审批" key="1">
                        {this.renderPane()}
                        <ApprovalModal 
                            visible={visible}
                            agreeApply={agreeApply}
                            table={editRecord}
                            onOk={this.approveApply}
                            onCancel={() => {
                                this.setState({
                                    visible: false,
                                    agreeApply: undefined,
                                    editRecord: '',
                                })
                            }}
                        />
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
