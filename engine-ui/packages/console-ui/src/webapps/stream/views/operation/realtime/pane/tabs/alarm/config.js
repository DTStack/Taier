import React from "react"
import { connect } from "react-redux";
import utils from "utils";

import { Button, Table, Popconfirm, message } from "antd";


import AlarmForm from "./alarmForm";
import {
    AlarmStatus, AlarmTriggerType, AlarmTypes
} from '../../../../../../components/status'

import Api from '../../../../../../api'
import { AlarmStatusFilter } from '../../../../../../comm/const'


class AlarmConfigList extends React.Component {

    state = {
        pagination: {
            total: 0,
            pageSize: 5,
            current:1
        },
        alarmStatus:undefined,
        visible: false,
        alarmInfo: undefined,
        loading:false,
        configs:[]
    }

    componentDidMount() {
        console.log("AlarmMsgconfig")
        this.loadAlarmRules();
    }
    componentWillReceiveProps(nextProps) {
        const data = nextProps.data
        const oldData = this.props.data
        if (oldData && data && oldData.id !== data.id) {
            this.loadAlarmRules(data)
        }
    }
    handleTableChange = (pagination, filters) => {
        this.setState({
            pagination:{
                ...this.state.pagination,
                current:pagination.current,
            },
            alarmStatus: filters.alarmStatus ? filters.alarmStatus[0] : ''
        }, this.loadAlarmRules)
    }
    /**
     * 删除成功重新加载数据，重新计算current值是否合理
     */
    safeDeleteReLoad(){
        const {pagination} = this.state;
        let {current,total,pageSize} = pagination;
        total=total-1;
        /**
         * 判断current是否超出total边界
         */
        if((current-1)*pageSize>=total&&current>1){
            current=current-1;
        }
        this.setState({
            pagination:{
                ...pagination,
                total,
                current
            }
        },this.loadAlarmRules)
    }
    loadAlarmRules(data){
        const {pagination,alarmStatus} = this.state;
        data=data||this.props.data||{};
        const reqForm = {
            ...pagination,
            pageIndex:pagination.current,
            taskId:data.id,
            alarmStatus:alarmStatus||undefined
        }
        this.setState({ loading: true })
        Api.getAlarmList(reqForm).then((res) => {
            this.setState({
                loading: false
            })
            if(res.code==1){
                this.setState({ 
                    configs: res.data.data || [], 
                    pagination:{
                        ...this.state.pagination,
                        total:res.data.totalCount
                    }
                })
            }
        })
    }
    initConfigListColumns() {
        return [{
            title: '告警名称',
            dataIndex: 'alarmName',
            key: 'alarmName',
            width:"110px",
        }, {
            title: '触发方式',
            dataIndex: 'myTrigger',
            key: 'myTrigger',
            width:"100px",
            render: (text) => {
                return <AlarmTriggerType value={text} />
            },
        }, {
            title: '告警方式',
            dataIndex: 'senderTypes',
            key: 'senderTypes',
            width:"80px",
            render: (data) => {
                return <AlarmTypes value={data} />
            }
        }, {
            title: '接收人',
            dataIndex: 'receiveUsers',
            key: 'receiveUsers',
            width:"150px",
            render: (text, record) => {
                const recivers = record.receiveUsers
                if (recivers.length > 0) {
                    return recivers.map(item => <span>{item.userName};</span>)
                }
                return ''
            },
        }, {
            title: '状态',
            dataIndex: 'alarmStatus',
            key: 'alarmStatus',
            render: (text) => {
                return <AlarmStatus value={text} />
            },
            filters: AlarmStatusFilter,
            filterMultiple: false,
        }, {
            title: '创建时间',
            dataIndex: 'createTime',
            key: 'createTime',
            render: text => utils.formatDateTime(text),
        }, {
            title: '创建人',
            dataIndex: 'createUser',
            key: 'createUser',
        }, {
            title: '操作',
            key: 'operation',
            width:"150px",
            render: (record) => {
                let isOpen = ''
                if (record.alarmStatus === 1) {
                    isOpen = '开启'
                } else if (record.alarmStatus === 0) {
                    isOpen = '关闭'
                }
                return (
                    <div key={record.id}>
                        <a onClick={() => { this.initEdit(record) }}>修改</a>
                        <span  className="ant-divider" />
                        <Popconfirm
                            title="确定删除这条告警吗?"
                            onConfirm={() => { this.deleteAlarm(record) }}
                            okText="确定"
                            cancelText="取消"
                        >
                            <a>删除</a>
                        </Popconfirm>
                        <span  className="ant-divider" />
                        <a onClick={() => { this.updateAlarmStatus(record) }}>{isOpen}</a>
                    </div>
                )
            },
        }]
    }
    initEdit(record){
        this.setState({
            visible:true,
            alarmInfo:record
        })
    }
    addAlarm = (alarm) => {
        const {data={}} = this.props;
        const ctx = this
        alarm.taskId=data.id;
        
        return Api.addAlarm(alarm).then((res) => {
            if (res.code === 1) {
                ctx.setState({ visible: false, alarmInfo:undefined })
                ctx.loadAlarmRules()
                return true;
            }
        })
    }

    deleteAlarm(alarm) {
        const ctx = this
        Api.deleteAlarm(alarm).then((res) => {
            if (res.code === 1) {
                message.success('删除告警成功！')
                ctx.safeDeleteReLoad();
            }
        })
    }

    updateAlarmStatus(alarm) {
        const ctx = this
        const params = { alarmId: alarm.alarmId }
        if (alarm.alarmStatus === 0) {
            Api.closeAlarm(params).then((res) => {
                if (res.code === 1) {
                    message.success('关闭告警成功！')
                    ctx.loadAlarmRules()
                }
            })
        } else if (alarm.alarmStatus === 1) {
            Api.openAlarm(params).then((res) => {
                if (res.code === 1) {
                    message.success('开启告警成功！')
                    ctx.loadAlarmRules()
                }
            })
        }
    }

    updateAlarm = (alarm) => {
        const {data={}} = this.props;
        const {alarmInfo} = this.state;
        const ctx = this
        alarm.taskId=data.id;

        alarm.id = alarmInfo.alarmId
       return Api.updateAlarm(alarm).then((res) => {
            if (res.code === 1) {
                message.success('告警更新成功！')
                ctx.setState({ visible: false, alarmInfo:undefined })
                ctx.loadAlarmRules()
                return true;
            }
        })
    }
    render() {
        const { pagination, visible, alarmInfo, loading, configs } = this.state;
        const { projectUsers, data = {} } = this.props;
        return (
            <section className="pane-alarm-configList">
                <header>
                    告警配置
                        <Button onClick={() => { this.setState({ visible: true }) }} type="primary" >添加告警</Button>
                </header>
                <Table
                    rowKey="alarmId"
                    className="m-table"
                    loading={loading}
                    columns={this.initConfigListColumns()}
                    dataSource={configs||[]}
                    pagination={pagination}
                    onChange={this.handleTableChange.bind(this)}
                />
                <AlarmForm
                    data={data}
                    projectUsers={projectUsers}
                    taskName={data.name}
                    title={!alarmInfo ? '新建告警规则' : '修改告警规则'}
                    alarmInfo={alarmInfo}
                    wrapClassName="vertical-center-modal"
                    visible={visible}
                    updateAlarm={this.updateAlarm}
                    addAlarm={this.addAlarm}
                    onCancel={() => { this.setState({ visible: false, alarmInfo: undefined }) }}
                />
            </section>
        )
    }
}

export default connect((state) => {
    return {
        projectUsers: state.projectUsers,
        project: state.project,
        user: state.user,
    }
})(AlarmConfigList);