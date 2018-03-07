import React, { Component } from 'react'
import { connect } from 'react-redux'
import moment from 'moment'
import { Link } from 'react-router'

import {
    Row, Input, Select, Menu, message,
    Col, Radio, Pagination, Checkbox, Form,
    DatePicker, TimePicker, Table, Card,
} from 'antd'

import utils from 'utils'

import Api from '../../../api'
import { TaskBadgeStatus } from '../../../components/status'
import * as FlowAction from '../../../store/modules/operation/taskflow'

const Search = Input.Search
const RadioGroup = Radio.Group
const Option = Select.Option
const FormItem = Form.Item

function getTimeString(date) {
    return date ? date.format('HH:mm') : ''
}

class PatchDataList extends Component {
    state = {
        loading: false,
        current: 1 ,
        tasks: { data: [] },

        // 参数
        jobName: '',
        runDay: '',
        bizDay: '',
        dutyUserId: '',
    }

    componentDidMount() {
        this.loadPatchData()
    }

    componentWillReceiveProps(nextProps) {
        const { project } = nextProps
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadPatchData()
        }
    }

    loadPatchData(params) {
        const ctx = this
        this.setState({ loading: true })
        let defaultParams = this.getReqParams()
        const reqParams = Object.assign(defaultParams, params)
        Api.getFillData(reqParams).then((res) => {
            if (res.code === 1) {
                ctx.setState({ tasks: res.data })
            }
            this.setState({ loading: false })
        })
    }

    killAllJobs = (job) => {
        Api.stopFillDataJobs({
            fillDataJobName: job.fillDataJobName,
        }).then(res => {
            if (res.code === 1) {
                message.success('已成功杀死所有实例！')
            }
        })
    }

    getReqParams = () => {
        const {
            jobName, runDay, bizDay, dutyUserId, current,
        } = this.state

        let reqParams = { currentPage: current || 1, pageSize: 20 }

        if (jobName) {
            reqParams.jobName = jobName
        }
        if (bizDay) {
            reqParams.bizDay = moment(bizDay).unix()
        }
        if (runDay) {
            reqParams.runDay = moment(runDay).unix()
        }
        if (dutyUserId) {
             reqParams.dutyUserId = dutyUserId
        }
     
        return reqParams
    }

    pageChange = (page) => {
        const params = { currentPage: page.current }
        this.setState({ current: page.current })
        this.loadPatchData(params)
    }

    onBuisTimeChange = (date) => {
        this.setState({ bizDay: date, current: 1 }, this.loadPatchData);
    }

    onRunningTime = (date) => {
        this.setState({ runDay: date, current: 1 }, this.loadPatchData);
    }

    onChangeJobName = (e) => {
        this.setState({ jobName: e.target.value })
    }

    onOwnerChange = (value) => {
        const state = { dutyUserId: value, current: 1, }
        this.setState(state, this.loadPatchData);
    }

    onCheckChange = (checkedList) => {
        const { user } = this.props;
        const conditions = {
            person: '',
            startTime: '',
            endTime: '',
            scheduleStatus: 1,
        };
        checkedList.forEach(item => {
            if (item === 'person') {
                conditions.dutyUserId  = `${user.id}`;
            } else if (item === 'todayUpdate') {
                conditions.startTime = moment().set({
                    'hour': 0,
                    'minute': 0,
                    'second': 0,
                }).unix()
                conditions.endTime = moment().set({
                    'hour': 23,
                    'minute': 59,
                    'second': 59,
                }).unix()
            }
        })

        this.setState(conditions, this.loadPatchData)
    }

    initTaskColumns = () => {
        return [{
            title: '补数据名称',
            dataIndex: 'fillDataJobName',
            key: 'fillDataJobName',
            render: (text, record) => {
                return (
                    <Link to={`/operation/task-patch-data/${text}`}>{text}</Link>
                )
            },
        }, {
            title: '业务日期',
            dataIndex: 'fromDay',
            key: 'fromDay',
            render: (text, record) => {
                return <span>{ record.fromDay} ~ {record.toDay }</span>
            },
        }, {
            title: '开始运行时间',
            dataIndex: 'createTime',
            key: 'createTime',
        }, {
            title: '操作人',
            dataIndex: 'dutyUserName',
            key: 'dutyUserName',
        }, {
            title: '操作',
            dataIndex: 'id',
            key: 'id',
            render: (text, record) => {
                return (
                    <a onClick={this.killAllJobs.bind(this, record)}>杀死所有实例</a>
                )
            }
        }]
    }

    render() {

        const {
            tasks, startTime, endTime, whichTask, current,
            owner, bussinessDate, runningDate, taskName,
        } = this.state

        const { projectUsers } = this.props
        const userItems = projectUsers && projectUsers.length > 0 ?
        projectUsers.map((item) => {
            return (
                <Option key={item.userId} value={`${item.userId}`} name={item.user.userName}>
                {item.user.userName}
            </Option>)
        }) : []

        const showTime = bussinessDate ? 'block' : 'none';

        const pagination = {
            total: tasks.totalCount,
            defaultPageSize: 20,
            current,
        };

        const title =(
            <Form 
                layout="inline"
                style={{marginTop: '10px'}}
                className="m-form-inline" 
            >
                <FormItem>
                    <Search
                        placeholder="按补数据名称搜索"
                        style={{ width: '120px' }}
                        value={taskName}
                        size="default"
                        onChange={this.onChangeJobName}
                        onSearch={this.loadPatchData}
                    />
                </FormItem>
                <FormItem label="业务日期">
                    <DatePicker
                        format="YYYY-MM-DD"
                        placeholder="业务日期"
                        style={{ width: '120px' }}
                        value={bussinessDate}
                        size="default"
                        onChange={this.onBuisTimeChange}
                    />
                </FormItem>
                <FormItem label="运行日期">
                    <DatePicker
                        format="YYYY-MM-DD"
                        placeholder="运行日期"
                        style={{ width: '120px' }}
                        size="default"
                        value={runningDate}
                        onChange={this.onRunningTime}
                    />
                </FormItem>
                <FormItem label="责任人">
                    <Select
                        allowClear
                        showSearch
                        style={{ width: '120px' }}
                        placeholder="责任人"
                        optionFilterProp="name"
                        value={owner}
                        onChange={this.onOwnerChange}
                    >
                        {userItems}
                    </Select>
                </FormItem>
                <FormItem>
                    <Checkbox.Group onChange={this.onCheckChange}>
                        <Checkbox value="person">我的任务</Checkbox>
                        <Checkbox value="todayUpdate">我今天补的</Checkbox>
                    </Checkbox.Group>
                </FormItem>
            </Form>
        )

        return (
            <div className="box-1 m-card">
                <Card
                    title={title}
                    noHovering
                    bordered={false}
                    loading={false}
                >
                    <Table 
                        columns={ this.initTaskColumns() }
                        className="m-table"
                        style={{ marginTop: 1}}
                        pagination={ pagination }
                        dataSource={tasks.data || []}
                        onChange={ this.pageChange }
                    />
                </Card>
            </div>
        )
    }
}
export default connect((state) => {
    return {
        project: state.project,
        user: state.user,
        projectUsers: state.projectUsers,
    }
})(PatchDataList)
