import React, { Component } from 'react';
import { connect } from 'react-redux';
import {
    Input, Button, Table, Form,
    message, Checkbox,Card, Select,
    Tabs, DatePicker,Spin,Tooltip
} from 'antd';

import { Link,hashHistory } from 'react-router';
import { parse } from 'qs';
import moment from 'moment';

import utils from 'utils';

import ajax from '../../../api/dataManage';
import ApprovalModal from './approvalModal';
import DetailPermission from './detailPermission';
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
        allProjects: state.allProjects,
        user: state.user,
    }
})
class AuthMana extends Component {

    constructor(props) {
        super(props);
        const isAdminAbove = this.props.user && this.props.user.isAdminAbove || 0;
        const isPermission = isAdminAbove==0 ? "1" : "0";
        const { listType, pageIndex, resourceName, startTime, endTime ,belongProjectId, applyUserId, status } = this.props.location.query;
        this.state = {
            isAdminAbove,
            table: [],
            editRecord: [],
            checkAll: false,
            selectedRowKeys: [],
            agreeApply: undefined,
            visible: false,
            isShowPermission: false,
            loading: false,
            rangeTime: startTime&&endTime&&[moment(Number(startTime)),moment(Number(endTime))]||[],
            userList:[],
            queryParams: {
                listType: listType || isPermission,
                pageIndex: pageIndex || 1,
                pageSize: 20,
                resourceName,
                startTime,
                endTime,
                belongProjectId,
                applyUserId,
                status: status&&[status]||undefined,
            },
            permissionParams: {},
            applyReason: undefined, //申请意见传给子组件
            reply: undefined, //审批意见
        }
    }

    componentDidMount() {
        this.search();
        //this.loadCatalogue();
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
        this.setState({ table: [], loading: true })
        const { queryParams } = this.state;
        const pathname = this.props.location.pathname;
        hashHistory.push({
            pathname,
            query: queryParams,
        })
        ajax.getApplyList(queryParams).then(res => {
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

    // 批量通过
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
    // 单个审批通过
    approveApplySingle = (params) => {
        delete params.tableName;
        const { isShowPermission } = this.state;
        ajax.applyReply(params).then(res => {
            if (res.code === 1) {
                message.success('操作成功！')
                this.setState({
                    isShowPermission: false,
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


    // 请求数据
    getPermissionData = (record) => {
        ajax.getApplyDetail({
            tableId: record.resourceId,
            tableName: record.resourceName,
            applyId: record.applyId,
        }).then(res => {
            if(res.code ===1 ) {
                console.log(res.data);
                const data = res.data;
                const fullDdls = data.fullDdls;
                const fullDmls = data.fullDmls;
                const fullColumns = data.fullColumnList;
                // ddl数据转化成checkboxGroup可用数据
                const fullDdlsData = fullDdls.map(item => {
                    return {
                        label: item.name,
                        value: item.value,
                        status: item.status
                    }
                });
                const ddlCheck = fullDdlsData.filter(item => {
                    return item.status === true
                })
                const ddlCheckArray = ddlCheck.map(item => {
                    return item.value
                })
                // dml数据转化成checkboxGroup可用数据
                const fullDmlsData = fullDmls.map(item => {
                    return {
                        label: item.name,
                        value: item.value,
                        status: item.status
                    }
                });
                const dmlCheck = fullDmlsData.filter(item => {
                    return item.status === true
                })
                const dmlCheckArray = dmlCheck.map(item => {
                    return item.value
                })
                // 字段名数据转化成checkboxGroup可用数据
                const fullColumnsData = fullColumns.map(item => {
                    return item.column
                });
                const fullColumnsCheckArray = fullColumns.filter(item => {
                    return item.status === true
                })
                const ids = fullColumnsCheckArray.map(item => {
                    return item.column
                })
                const total = fullColumnsData.length;
                // 判断是否全选
                const ischeckAll = (fullDdlsData.length + fullDmlsData.length) == (ddlCheck.length + dmlCheck.length);
                const idCheckIds = (fullColumns.length == fullColumnsCheckArray.length);
                const params = {
                    fullDdlsData,
                    ddlCheckArray,
                    fullDmlsData,
                    dmlCheckArray,
                    fullColumnsData,
                    fullColumns,
                    ids,
                    total,
                    ischeckAll,
                    idCheckIds,
                }
                this.setState({
                    permissionParams: params
                })
            }
        })
    }
   // 通过
    passClick = (record) => {
        this.getPermissionData(record);
        const {queryParams} = this.state;
        console.log(queryParams.listType)
        this.setState({
            isShowPermission: true,
            agreeApply: queryParams.listType ==0 ? true : undefined,
            editRecord: [record],
            applyReason:record.applyReason,
            reply: record.reply,
        });
    }
    // 驳回
    rejectClick = (record) => {
        this.getPermissionData(record);
        this.setState({
            isShowPermission: true,
            agreeApply: false,
            editRecord: [record],
        });
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
                                    <a onClick={() => ctx.passClick(record)}>通过</a>
                                    <span className="ant-divider"></span>
                                    <a onClick={() => ctx.rejectClick(record)}>驳回</a>
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
                        // {
                        //     title: '申请详情',
                        //     key: 'applyReason',
                        //     dataIndex: 'applyReason',
                        //     width:"100px",
                        //     render : text => this.characterProcess(text,"100px"),
                        // },
                        {
                            title: '操作',
                            key: 'operation',
                            dataIndex: 'applyStatus',
                            width: 120,
                            render(text, record) {
                                return <span>
                                    <a onClick={() => ctx.passClick(record)}>查看详情</a>
                                    <span className="ant-divider"></span>
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
                        // {
                        //     title: '审批意见',
                        //     key: 'reply',
                        //     dataIndex: 'reply',
                        //     width:"100px",
                        //     render : text => this.characterProcess(text,"100px"),
                        // },
                        {
                            title: '操作',
                            key: 'operation',
                            width:"100px",
                            render(record) {
                                return <span>
                                    <a onClick={() => ctx.passClick(record)}>查看详情</a>
                                </span>
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
                                    <a onClick={() => ctx.passClick(record)}>查看详情</a>
                                    <span className="ant-divider"></span>
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
        const { queryParams } = this.state;
        const startTime = dateString&&Date.parse(dateString[0])||undefined;
        const endTime = dateString&&Date.parse(dateString[1])||undefined;
        queryParams.startTime = startTime;
        queryParams.endTime = endTime;
        this.setState({
            queryParams, rangeTime: date
        }, this.search);
    };

    renderPane = (isShowRowSelection = false) => {
        const { table, selectedRowKeys, queryParams, rangeTime, loading, userList, } = this.state;

        const { allProjects } = this.props;

        const today0 = new Date(new Date().toLocaleDateString()).getTime();
        const today24 = new Date(new Date().toLocaleDateString()).getTime() + 24 * 60 * 60 * 1000 - 1;

        const projectOptions = allProjects.map(proj => <Option
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
        console.log('rangeTime',rangeTime);
    
        const title = (
            <Form className="m-form-inline" layout="inline" style={{ marginTop: '10px' }}>
                <FormItem label="项目">
                    <Select
                        allowClear
                        showSearch
                        optionFilterProp="name"
                        style={{ width: 126 }}
                        placeholder="选择项目"
                        value={queryParams.belongProjectId}
                        onChange={(value) => this.changeParams('belongProjectId', value)}
                    >
                        {projectOptions}
                    </Select>
                </FormItem>
                {
                     queryParams.listType == 1 ? "" : <FormItem label="申请人">
                        <Select
                            allowClear
                            showSearch
                            optionFilterProp="name"
                            style={{ width: 126}}
                            placeholder="选择申请人"
                            value={queryParams.applyUserId}
                            onChange={(value) => this.changeParams('applyUserId', value)}
                        >
                            {userOptions}
                        </Select>
                    </FormItem>
                }
                {
                    queryParams.listType == 0||queryParams.listType == 3 ? "" : <FormItem label="状态">
                            <Select
                                allowClear
                                showSearch
                                optionFilterProp="status"
                                style={{ width: 126 }}
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
            total: Number(table.totalCount),
            defaultPageSize: 20,
            current: Number(queryParams.pageIndex)

        };

        const rowSelection = isShowRowSelection ? {
            selectedRowKeys,
            onChange: this.onSelectChange,
        } : null;
        const selectCalssName = isShowRowSelection ? "m-table-fix m-table" : "m-table"
        return <div className="m-tablelist">
            <div className="m-card card-tree-select">
                <Card noHovering bordered={false} title={title} className="full-screen-table-80">
                    <Spin spinning={loading} tip="正在加载中...">
                        <div style={{ marginTop: '1px' }}>
                            <Table
                                rowKey="applyId"
                                className={selectCalssName}
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
        const { editRecord, visible, agreeApply, queryParams, isAdminAbove, isShowPermission, permissionParams, applyReason, reply} = this.state;
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
                                                    <DetailPermission
                                                        visible={isShowPermission}
                                                        agreeApply={agreeApply}
                                                        table={editRecord}
                                                        applyReason={applyReason}
                                                        reply={reply}
                                                        permissionParams={permissionParams}
                                                        listType={queryParams.listType}
                                                        onOk={this.approveApplySingle}
                                                        onCancel={() => {
                                                            this.setState({
                                                                isShowPermission: false,
                                                                agreeApply: undefined,
                                                                editRecord: [],
                                                            })
                                                        }}
                                                    >
                                                    </DetailPermission>
                                                </TabPane> 
                    }
                    {    
                       isAdminAbove == 2 ? "" : <TabPane tab="申请记录" key={1}>
                                                    {this.renderPane()}
                                                    <DetailPermission
                                                        visible={isShowPermission}
                                                        agreeApply={agreeApply}
                                                        table={editRecord}
                                                        applyReason={applyReason}
                                                        reply={reply}
                                                        permissionParams={permissionParams}
                                                        listType={queryParams.listType}
                                                        onOk={this.approveApplySingle}
                                                        onCancel={() => {
                                                            this.setState({
                                                                isShowPermission: false,
                                                                agreeApply: undefined,
                                                                editRecord: [],
                                                            })
                                                        }}
                                                    >
                                                    </DetailPermission>
                                                </TabPane>

                    }
                    {
                        isAdminAbove == 0 ? "" : <TabPane tab="已处理" key={2}>
                                                    {this.renderPane()}
                                                    <DetailPermission
                                                        visible={isShowPermission}
                                                        agreeApply={agreeApply}
                                                        table={editRecord}
                                                        applyReason={applyReason}
                                                        reply={reply}
                                                        permissionParams={permissionParams}
                                                        listType={queryParams.listType}
                                                        onOk={this.approveApplySingle}
                                                        onCancel={() => {
                                                            this.setState({
                                                                isShowPermission: false,
                                                                agreeApply: undefined,
                                                                editRecord: [],
                                                            })
                                                        }}
                                                    >
                                                    </DetailPermission>
                                                </TabPane> 
                    }
                    {
                        isAdminAbove == 0 ? "" : <TabPane tab="权限回收" key={3}>
                                                    {this.renderPane(true)}
                                                    <DetailPermission
                                                        visible={isShowPermission}
                                                        agreeApply={agreeApply}
                                                        table={editRecord}
                                                        applyReason={applyReason}
                                                        reply={reply}
                                                        permissionParams={permissionParams}
                                                        listType={queryParams.listType}
                                                        onOk={this.approveApplySingle}
                                                        onCancel={() => {
                                                            this.setState({
                                                                isShowPermission: false,
                                                                agreeApply: undefined,
                                                                editRecord: [],
                                                            })
                                                        }}
                                                    >
                                                    </DetailPermission>
                                                </TabPane> 
                    }
                </Tabs>
            </div>
        )
    }
}

export default AuthMana;
