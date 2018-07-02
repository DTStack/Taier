import React, { Component } from 'react';
import { connect } from 'react-redux';
import {
    Input, Button, Table, Form,
    Pagination, Modal, message, Checkbox,
    Tag, Icon, Card, Select, Tabs, DatePicker,
    Spin,Tooltip
} from 'antd';

import { Link,hashHistory } from 'react-router';
import { parse } from 'qs';
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
        return <span>已通过</span>
    }else if(status === 2){
        return <span>未通过</span>
    }else if(status === 3){
        return <span>已过期</span>
    }else if(status === 4){
        return <span>已撤销</span>
    }
}

const revokeStatus = (status) => {
    if (status === 0) {
        return <span>未回收</span>
    } else if (status === 1) {
        return <span>已回收</span>
    }
}

const selectStatusList = [
    {status:0,value: "待审批"},
    {status:1,value: "已通过"},
    {status:2,value: "未通过"},
    {status:3,value: "已过期"},
    {status:4,value: "已撤销"}
];

@connect(state => {
    return {
        projects: state.allProjects,
        user: state.user,
    }
})
class AuthMana extends Component {

    constructor(props) {
        super(props);
        const isAdminAbove = this.props.user&&this.props.user.isAdminAbove || 0;
        const isPermission = isAdminAbove==0 ? "1" : "0";
        const { listType } = this.props.location.search&&parse(this.props.location.search.substr(1))||{listType: isPermission}
        this.state = {
            isAdminAbove,
            table: [],
            editRecord: [],
            checkAll: false,
            selectedRowKeys: [],
            agreeApply: undefined,
            visible: false,
            loading: false,
            rangeTime: [],
            userList:[],
            queryParams: {
                listType,
                pageIndex: 1,
                pageSize: 10,
                resourceName: undefined,
                startTime: undefined,
                endTime: undefined,
                belongProjectId: undefined,
                applyUserId: undefined,
                status: undefined,
            },
        }
    }

    componentDidMount() {
        this.search();
        this.loadCatalogue();
        this.getUsersInTenant();
    }

    getUsersInTenant(){
        ajax.getUsersInTenant().then(res=>{
            console.log("getUsersInTenant",res);
            if(res.code === 1){
                this.setState({
                    userList: res.data || []
                })
            }
            
        })
    }

    componentWillReceiveProps(nextProps) {
        if(nextProps.user != this.props.user){
            this.judgmentAauthority(nextProps)
        }
    }

    judgmentAauthority = (nextProps) => {
        let { queryParams,isAdminAbove } = this.state;
        isAdminAbove = nextProps.user&&nextProps.user.isAdminAbove;
        const isPermission = isAdminAbove==0 ? "1" : "0";
        const { listType } = this.props.location.search&&parse(this.props.location.search.substr(1))||{listType: isPermission}
        queryParams.listType = listType;
        this.setState({queryParams,isAdminAbove})
    }

    search = () => {
        const { table, loading } = this.state;
        this.setState({ table: [], loading: true })
        const params = this.state.queryParams;
        ajax.getApplyList(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    table: res.data,
                    checkAll: false,
                })
            }
            this.setState({ loading: false })
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
        delete params.tableName;
        const { visible } = this.state;
        ajax.applyReply(params).then(res => {
            if (res.code === 1) {
                message.success('操作成功！')
                this.setState({
                    visible: false,
                }, this.search)
            }
        })
    }

    revoke = (ids = []) => {
        let params;
        if (ids.length > 0) {
            params = { ids };
        } else {
            const { selectedRowKeys, table } = this.state;
            params = { ids: selectedRowKeys }
        }
        if (params.ids.length > 0) {
            ajax.revoke(params).then(res => {
                if (res.code === 1) {
                    message.success('回收成功！')
                    this.search()
                }
            })
        } else {
            message.warning('请勾选要操作的列表')
        }

    }

    cancelApply = (id) => {
        const params = { id }
        ajax.cancelApply(params).then(res => {
            if (res.code === 1) {
                message.success('取消成功！')
                this.search()
            }
        })
    }

    changeParams = (field, value) => {
        let queryParams = Object.assign(this.state.queryParams);
        const pathname = this.props.location.pathname;        
        if (field) {
            queryParams[field] = value;
            queryParams.pageIndex = 1;
            if(field==="listType"){
                hashHistory.push(`${pathname}?listType=${value}`)
            }
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
        console.log("sorter", sorter);
        const queryParams = Object.assign(this.state.queryParams, {
            pageIndex: pagination.current
        })
        if(Object.keys(sorter).length > 0){
            if(sorter.field === "applyTime"){
                queryParams.sort = sorter.order === "descend" ? "desc" : "asc"
            }else{
                queryParams.sortColumn = sorter.order === "descend" ? "gmt_create" : "day"
            }
        }
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
        this.setState({ selectedRowKeys, checkAll });
    }

    onCheckAllChange = (e) => {
        let selectedRowKeys = []
        if (e.target.checked) {
            selectedRowKeys = this.state.table.data.map(item => item.applyId)
        }
        this.setState({
            checkAll: e.target.checked,
            selectedRowKeys
        })
    }

    batchApply(agreeApply) {
        const { selectedRowKeys, table } = this.state;
        const editRecord = [];
        if (selectedRowKeys.length > 0) {
            selectedRowKeys.map(item => {
                table.data.map(v => {
                    if (v.applyId === item) {
                        editRecord.push(v)
                    }
                })
            })
            this.setState({
                agreeApply,
                visible: true,
                editRecord,
            })
        } else {
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
                        <div style={{ padding: '15px 10px 10px 30px', display: "inline-block" }}>
                            <Checkbox
                                checked={this.state.checkAll}
                                onChange={this.onCheckAllChange}
                            >
                            </Checkbox>
                        </div>
                        <div style={{ display: "inline-block", marginLeft: '15px' }}>
                            <Button type="primary" size="small" onClick={this.batchApply.bind(this, true)}>批量通过</Button>&nbsp;
                            <Button type="primary" size="small" onClick={this.batchApply.bind(this, false)}>批量驳回</Button>&nbsp;
                        </div>
                    </div>
                )
            }
            case "3": { // 权限回收
                return (
                    <div className="ant-table-row  ant-table-row-level-0">
                        <div style={{ padding: '15px 10px 10px 30px', display: "inline-block" }}>
                            <Checkbox
                                checked={this.state.checkAll}
                                onChange={this.onCheckAllChange}
                            >
                            </Checkbox>
                        </div>
                        <div style={{ display: "inline-block", marginLeft: '15px' }}>
                            <Button type="primary" size="small" onClick={() => { this.revoke() }}>批量回收</Button>&nbsp;
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

    applyDataSort = (x,y,z)=>{
        console.log(x,y,z);
        
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
                title: '项目名称',
                key: 'projectName',
                dataIndex: 'projectName',
            },
            {
                title: '项目显示名称',
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
                            sorter:true,
                            render(text, record) {
                                return utils.formatDateTime(text)
                            }
                        },
                        {
                            title: '有效期',
                            key: 'day',
                            dataIndex: 'day',
                            sorter:true,
                            render(text, record) {
                                return `${text}天`
                            }
                        },
                        {
                            title: '申请原因',
                            key: 'applyReason',
                            dataIndex: 'applyReason',
                            width: "100px",
                            render : text => this.characterProcess(text,"100px"),
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
                            sorter: true,
                            render(text, record) {
                                return utils.formatDateTime(text)
                            }
                        },
                        {
                            title: '有效期',
                            key: 'day',
                            dataIndex: 'day',
                            sorter:true,
                            render(text, record) {
                                return `${text}天`
                            }
                        },
                        {
                            title: '状态',
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
                            width:"100px",
                            render : text => this.characterProcess(text,"100px"),
                        },
                        {
                            title: '操作',
                            key: 'operation',
                            dataIndex: 'applyStatus',
                            width: 120,
                            render(text, record) {
                                return <span>
                                    {
                                        text == 0 ? <a onClick={() => { ctx.cancelApply(record.applyId) }}>撤销</a> : "撤销"
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
                            sorter:true,
                            render(text, record) {
                                return utils.formatDateTime(text)
                            }
                        },
                        {
                            title: '有效期',
                            key: 'day',
                            dataIndex: 'day',
                            sorter:true,
                            render(text, record) {
                                return `${text}天`
                            }
                        },
                        {
                            title: '状态',
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
                            width:"100px",
                            render : text => this.characterProcess(text,"100px"),
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
                            width:"100px",
                            render : text => this.characterProcess(text,"100px"),
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
                                    <a onClick={() => { ctx.revoke([record.applyId]) }}>收回</a>
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

    onChangeTime = (date, dateString) => {
        let { queryParams, rangeTime } = this.state;
        rangeTime = date;
        const startTime = Date.parse(dateString[0]);
        const endTime = Date.parse(dateString[1]);
        queryParams.startTime = startTime;
        queryParams.endTime = endTime;
        this.setState({
            queryParams, rangeTime
        }, this.search);
    };

    renderPane = (isShowRowSelection = false) => {
        const { table, selectedRowKeys, queryParams, rangeTime, loading, userList, } = this.state;

        const { projects } = this.props;

        const today0 = new Date(new Date().toLocaleDateString()).getTime();
        const today24 = new Date(new Date().toLocaleDateString()).getTime() + 24 * 60 * 60 * 1000 - 1;

        const projectOptions = projects.map(proj => <Option
            title={proj.projectAlias}
            key={proj.id}
            name={proj.projectAlias}
            value={`${proj.id}`}
        >
            {proj.projectAlias}
        </Option>)

        const userOptions = userList.map(v => <Option
            title={v.userName}
            key={v.userId}
            name={v.userName}
            value={`${v.userId}`}
        >
            {v.userName}
        </Option>)

        const selectStatus = selectStatusList.map(v => <Option
            title={v.value}
            key={v.status}
            name={v.value}
            value={`${v.status}`}
        >
            {v.value}
        </Option>)

        const title = (
            <Form className="m-form-inline" layout="inline" style={{ marginTop: '10px' }}>
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
                <FormItem label="申请人">
                    <Select
                        allowClear
                        showSearch
                        optionFilterProp="name"
                        style={{ width: 120 }}
                        placeholder="选择申请人"
                        value={queryParams.applyUserId}
                        onChange={(value) => this.changeParams('applyUserId', value)}
                    >
                        {userOptions}
                    </Select>
                </FormItem>
                {
                    queryParams.listType == 0||queryParams.listType == 3 ? "" : <FormItem label="状态">
                            <Select
                                allowClear
                                showSearch
                                optionFilterProp="status"
                                style={{ width: 120 }}
                                placeholder="选择状态"
                                value={queryParams.status}
                                onChange={(value) => this.changeParams('status', value ? [value] : undefined)}
                            >
                                {selectStatus}
                            </Select>
                        </FormItem>
                }
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
                <FormItem label="申请时间">
                    <RangePicker 
                        onChange={this.onChangeTime} 
                        format="YYYY-MM-DD HH:mm:ss" 
                        value={rangeTime} 
                        ranges={{ Today: [moment(today0), moment(today24)]}}
                    />
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
        } : null;

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
        const { editRecord, visible, agreeApply, queryParams, isAdminAbove} = this.state;
        return (
            <div className="box-1 m-tabs">
                <Tabs
                    activeKey={queryParams.listType}
                    animated={false} 
                    style={{height: 'auto'}} 
                    onChange={value => this.changeParams('listType', value)}

                >
                   {
                        isAdminAbove == 0 ? "" : <TabPane tab="待我审批" key={0}>
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
                   }
                   {    
                       isAdminAbove == 2 ? "" : <TabPane tab="申请记录" key={1}>
                                                    {this.renderPane()}
                                                </TabPane>

                   }
                   
                    {
                        isAdminAbove == 0 ? "" : <TabPane tab="已处理" key={2}>
                                                    {this.renderPane()}
                                                </TabPane> 
                    }
                    {
                        isAdminAbove == 0 ? "" : <TabPane tab="权限回收" key={3}>
                                                    {this.renderPane(true)}
                                                </TabPane> 
                    }

                </Tabs>
            </div>
        )
    }
}

export default AuthMana;
