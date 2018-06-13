import React, { Component } from 'react';
import { connect } from 'react-redux';
import {
    Input, Button, Table, Form,
    Pagination, Modal, message, Checkbox,
    Tag, Icon, Card, Select, Tabs,DatePicker,
    Spin
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
    }else if(status === 2){
        return <span>不通过</span>
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
        user: state.user,
    }
})
class AuthMana extends Component {

    constructor(props) {
        super(props);
        this.isAdminAbove = this.props.user&&this.props.user.isAdminAbove;
        // this.isAdminAbove = 0;
        this.state = {
            table: [],
            editRecord: [],
            checkAll: false,
            selectedRowKeys: [],
            agreeApply: undefined,
            visible:false,
            loading:false,
            rangeTime:[],
            descModel: {
                visible: false,
                descInfo: "",
            },
            queryParams: {
                listType: this.isAdminAbove==1 ? "0" : "1",
                pageIndex: 1,
                pageSize: 10,
                resourceName: undefined,
                startTime: undefined,
                endTime: undefined,
                belongProjectId: undefined,
            },
        }
        console.log('this.isAdminAbove------------',this.isAdminAbove);
        
    }

    componentDidMount() {
        this.search();
        this.loadCatalogue();
    }

    componentWillReceiveProps(nextProps) {
    }

    search = () => {
        const { table,loading } = this.state;
        this.setState({table:[],loading:true})
        const params = this.state.queryParams;
        ajax.getApplyList(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    table: res.data,
                    loading: false,
                    checkAll: false,
                })
            }else{
                this.setState({loading:false})
            }
        })
    }

    cancleMark = (applyData) => {
        ajax.cancleMark(params).then(res => {
            if (res.code === 1) {
                message.success('取消成功！')
                this.search()
            }
        })
    }

    approveApply = (params) => {
        delete params.tableName ;
        const { visible } = this.state;
        ajax.applyReply(params).then(res => {
            if (res.code === 1) {
                message.success('操作成功！')
                this.setState({
                    visible: false,
                },this.search)                
            }
        })
    }

    revoke = (ids=[]) => {
        let params;
        if(ids.length > 0){
            params = {ids};
        }else{
            const { selectedRowKeys,table } = this.state;
            params = {ids:selectedRowKeys}
        }
        if(params.ids.length > 0){
            ajax.revoke(params).then(res => {
                if (res.code === 1) {
                    message.success('回收成功！')
                    this.search()
                }
            })
        }else{
            message.warning('请勾选要操作的列表')
        }
        
    }

    cancelApply = (id) => {
        const params = {id}
        ajax.cancelApply(params).then(res => {
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
            queryParams.pageIndex = 1;
        }
        this.setState({
            queryParams,
            selectedRowKeys: [],
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
            selectedRowKeys: [],
        }, this.search)
    }

    onTableNameChange = (e) => {
        this.setState({
            queryParams: Object.assign(this.state.queryParams, {
                resourceName: e.target.value,
                pageIndex: 1,
            }),
        })
    }

    onSelectChange = (selectedRowKeys) => {
        const checkAll = selectedRowKeys.length === this.state.table.data.length;
        this.setState({ selectedRowKeys,checkAll });
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
        if(selectedRowKeys.length > 0){
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
        }else{
            message.warning('请勾选要操作的列表')
        }  
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
                            <Button type="primary" size="small"  onClick={()=>{this.revoke()}}>批量回收</Button>&nbsp;
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
                            render(text){
                                return  text&&text.length > 10 ? <span><a onClick={() => ctx.showDescModal(text)}>查看详情</a></span> : text ? text : "无"
                            }
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
                            render(text, record) {
                                return `${text}天`
                            }
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
                            render(text){
                                return  text&&text.length > 10 ? <span><a onClick={() => ctx.showDescModal(text)}>查看详情</a></span> : text ? text : "无"
                            }
                        },
                        {
                            title: '操作',
                            key: 'isCancel',
                            dataIndex: 'isCancel',
                            width: 120,
                            render(text, record) {
                                return <span>
                                    {
                                        text == 0 ? <a onClick={()=>{ctx.cancelApply(record.applyId)}}>撤销</a> : "撤销"
                                    }
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
                            render(text, record) {
                                return `${text}天`
                            }
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
                            key: 'dealUser',
                            dataIndex: 'dealUser',
                        },
                        {
                            title: '审批意见',
                            key: 'reply',
                            dataIndex: 'reply',
                            render(text){
                                return  text&&text.length > 10 ? <span><a onClick={() => ctx.showDescModal(text)}>查看详情</a></span> : text ? text : "无"
                            }
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
                            render(text){
                                return  text&&text.length > 10 ? <span><a onClick={() => ctx.showDescModal(text)}>查看详情</a></span> : text ? text : "无"
                            }
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
                                    <a onClick={()=>{ ctx.revoke([record.applyId]) }}>收回</a>
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

    closeDescModal = () => {
        const { descModel } = this.state; 
        descModel.descInfo = "";
        descModel.visible = false;
        this.setState({
            descModel
        })
    }

    showDescModal = (text) => {
        const { descModel } = this.state; 
        descModel.descInfo = text;
        descModel.visible = true;
        this.setState({
            descModel
        });
    }

    onChangeTime = (date, dateString)=> {
        let { queryParams,rangeTime } = this.state;
        rangeTime = date;
        const startTime = Date.parse(dateString[0]);
        const endTime =  Date.parse(dateString[1]);
        queryParams.startTime = startTime;
        queryParams.endTime = endTime;
        this.setState({
            queryParams,rangeTime
        },this.search);
    };
      
    renderPane = (isShowRowSelection=false) => {
        const { table, selectedRowKeys,queryParams,rangeTime,loading } = this.state;

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
                        value={queryParams.resourceName}
                        onChange={this.onTableNameChange}
                        onSearch={this.search}
                    />
                </FormItem>
                <FormItem label="时间选择">
                    <RangePicker onChange={this.onChangeTime} format="YYYY-MM-DD HH:mm:ss" value={rangeTime}/>
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
                    <Spin spinning={loading} tip="正在加载中...">
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
                    </Spin>
                </Card>
            </div>
        </div>
    }

    render() {
        const { editRecord, visible, agreeApply, descModel} = this.state;
        return (
            <div className="box-1 m-tabs">
                <Tabs
                    animated={false} 
                    style={{height: 'auto'}} 
                    onChange={value => this.changeParams('listType', value)}
                >
                   {
                       this.isAdminAbove == 1 ? <TabPane tab="待我审批" key={0}>
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
                                                </TabPane> : ""
                   }
                    <TabPane tab="申请记录" key={1}>
                        {this.renderPane()}
                    </TabPane>
                    {
                        this.isAdminAbove == 1 ? <TabPane tab="已处理" key={2}>
                                                    {this.renderPane()}
                                                </TabPane> : ""
                    }
                    {
                        this.isAdminAbove == 1 ?<TabPane tab="权限回收" key={3}>
                                                    {this.renderPane(true)}
                                                </TabPane> : ""
                    }
                    
                </Tabs>
                <div>
                    <Modal
                    title="详情信息"
                    visible={descModel.visible}
                    onCancel={this.closeDescModal}
                    footer={null}
                    >
                        <div style={{textIndent: "16px"}}>{descModel.descInfo}</div>
                    </Modal>
                </div>
            </div>
        )
    }
}

export default AuthMana;
