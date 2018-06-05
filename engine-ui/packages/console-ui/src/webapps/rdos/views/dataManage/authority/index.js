import React, { Component } from 'react';
import { connect } from 'react-redux';
import {
    Input, Button, Table, Form,
    Pagination, Modal, message, Checkbox,
    Tag, Icon, Card, Select, Tabs,DatePicker
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
const { RangePicker } = DatePicker;


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
            editRecord: [],
            checkAll: false,
            selectedRowKeys: [],
            agreeApply: undefined,
            visible:false,
            queryParams: {
                listType: "0",
                pageIndex: 1,
                pageSize: 10,
                resourceName: undefined,
                startTime: undefined,
                endTime: undefined,
                belongProjectId: undefined,
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
        const { table } = this.state;
        this.setState({table:[]})
        const params = this.state.queryParams;
        ajax.getApplyList(params).then(res => {
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
        delete params.tableName ;
        const { visible } = this.state;
        ajax.applyReply(params).then(res => {
            if (res.code === 1) {
                message.success('操作成功！')
                this.setState({visible: false})                
            }
        })
    }

    revoke = (ids=[]) => {
        let params;
        if(ids.length > 0){
            params = {ids};
        }else{
            const { selectedRowKeys,table } = this.state;
            console.log('ids',ids);
            
            
            selectedRowKeys.map(v=>{
                table.data.map(item=>{
                    if(item.applyId === v){
                        ids.push(item.resourceId)
                    }
                })
            })
        }
        console.log(params);
        
        ajax.revoke(params).then(res => {
            if (res.code === 1) {
                message.success('回收成功！')
            }
        })
    }

    cancelApply = (id) => {
        const params = {id}
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
            checkAll: false,
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
                resourceName: e.target.value,
                currentPage: 1,
            }),
        })
    }

    onSelectChange = (selectedRowKeys) => {
        this.setState({ selectedRowKeys });
    }

    onCheckAllChange = (e) => {
        let selectedRowKeys = []
        if (e.target.checked) {
            selectedRowKeys = this.state.table.data.map(item => item.applyId )
        }
        this.setState({
            checkAll: e.target.checked,
            selectedRowKeys
        })
    }

    batchApply(agreeApply){
        const { selectedRowKeys,table } = this.state;
        const editRecord = [];
        selectedRowKeys.map(item=>{
            table.data.map(v=>{
               if(v.applyId === item ){
                editRecord.push(v)
               }
            })
        })
        
        this.setState({
            agreeApply,
            visible: true,
            editRecord,
        })
    }

    tableFooter = (currentPageData) => {
        const { queryParams } = this.state;

        let operation = '';
        switch (queryParams.listType) {
            case "0": { // 待审批
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
                            <Button type="primary" size="small" onClick={this.batchApply.bind(this,true)}>批量通过</Button>&nbsp;
                            <Button type="primary" size="small" onClick={this.batchApply.bind(this,false)}>批量驳回</Button>&nbsp;
                        </div>
                    </div>
                )
            }
            case "3": { // 权限回收
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
                            <Button type="primary" size="small" onClick={()=>{this.revoke()}}>批量回收</Button>&nbsp;
                        </div>
                    </div>
                )
            }
            case "1": // 申请记录
            case "2":  // 已处理
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
                key: 'resourceName',
                dataIndex: 'resourceName',
                render(text, record) {
                    return <Link to={`${ROUTER_BASE}/view/${record.resourceId}`}>{text}</Link>
                }
            },
            {
                title: 'project',
                key: 'projectName',
                dataIndex: 'projectName',
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
                key: 'applyUser',
                dataIndex: 'applyUser'
            },
        ];

        switch (queryParams.listType) {
            case "0": { // 待审批
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
                                            editRecord: [record],
                                        })
                                    }}>通过</a>
                                    <span className="ant-divider"></span>
                                    <a onClick={() => {
                                        ctx.setState({
                                            visible: true,
                                            agreeApply: false,
                                            editRecord: [record],
                                        })
                                    }}>驳回</a>
                                </span>
                            }
                        }
                    ]
                )
            }

            case "1": {  // 申请记录 
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
                                    <a onClick={()=>{ctx.cancelApply(record.resourceId)}}>撤销</a>
                                </span>
                            }
                        }
                    ]
                )
            }
            case "2": {  // 已处理 
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
            case "3": {  // 权限回收 
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
                                    <a onClick={()=>{ ctx.revoke([record.resourceId]) }}>收回</a>
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

    onChangeTime = (date, dateString)=> {
        const { queryParams } = this.state;
        const startTime = Date.parse(dateString[0]);
        const endTime =  Date.parse(dateString[1]);
        queryParams.startTime = startTime;
        queryParams.endTime = endTime;
        this.setState(queryParams,this.search);
    };
      
    renderPane = (isShowRowSelection=false) => {
        const { table, selectedRowKeys,queryParams } = this.state;

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
                        value={queryParams.belongProjectId}
                        onChange={(value) => this.changeParams('belongProjectId', value)}
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
                <FormItem label="时间选择">
                    <RangePicker onChange={this.onChangeTime} format="YYYY-MM-DD HH:mm:ss"/>
                </FormItem>
            </Form>
        )

        const pagination = {
            total: table.totalCount,
            defaultPageSize: 10,
        };
        
        const rowSelection = isShowRowSelection ? {
            selectedRowKeys,
            onChange: this.onSelectChange,
        }: null;

        return <div className="m-tablelist">
            <div className="m-card card-tree-select" style={{ paddingBottom: 20 }}>
                <Card noHovering bordered={false} title={title}>
                    <div style={{ marginTop: '1px' }}>
                        <Table
                            rowKey="applyId"
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
                    <TabPane tab="待我审批" key={0}>
                        {this.renderPane(true)}
                        <ApprovalModal 
                            visible={visible}
                            agreeApply={agreeApply}
                            table={editRecord}
                            onOk={this.approveApply}
                            onCancel={() => {
                                this.setState({
                                    visible: false,
                                    agreeApply: undefined,
                                    editRecord: [],
                                })
                            }}
                        />
                    </TabPane>
                    <TabPane tab="申请记录" key={1}>
                        {this.renderPane()}
                    </TabPane>
                    <TabPane tab="已处理" key={2}>
                        {this.renderPane()}
                    </TabPane>
                    <TabPane tab="权限回收" key={3}>
                        {this.renderPane(true)}
                    </TabPane>
                </Tabs>
            </div>
        )
    }
}

export default AuthMana;
