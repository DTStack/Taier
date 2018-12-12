import React, { Component } from 'react'
import { connect } from 'react-redux'
import moment from 'moment'
import { Link } from 'react-router'

import {
    Input, Select, message,
    Checkbox, Form,
    DatePicker, Table, Card
} from 'antd'

import Api from '../../../api'
const Search = Input.Search
const Option = Select.Option
const FormItem = Form.Item

class PatchDataList extends Component {
    state = {
        loading: false,
        current: 1,
        tasks: { data: [] },

        // 参数
        jobName: '',
        runDay: undefined,
        bizDay: '',
        dutyUserId: undefined,
        checkVals: []
    }

    componentDidMount () {
        this.loadPatchData()
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps (nextProps) {
        const { project } = nextProps
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadPatchData()
        }
    }

    loadPatchData = (params) => {
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
            fillDataJobName: job.fillDataJobName
        }).then(res => {
            if (res.code === 1) {
                message.success('已成功杀死所有实例！')
            }
        })
    }

    getReqParams = () => {
        const {
            jobName, runDay, bizDay,
            dutyUserId, current
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

    onSearchByJobName = () => {
        this.setState({
            current: 1
        }, this.loadPatchData)
    }

    onOwnerChange = (value) => {
        const { user } = this.props
        const { checkVals } = this.state
        const setVals = {
            dutyUserId: value,
            current: 1
        }
        let checkArr = [...checkVals]
        if (value == user.id) {
            if (checkArr.indexOf('person') === -1) {
                checkArr.push('person')
            }
        } else {
            checkArr = []
        }
        setVals.checkVals = checkArr
        this.setState(setVals, this.loadPatchData);
    }

    onCheckChange = (checkedList) => {
        const { user } = this.props;
        const { dutyUserId } = this.state;

        const conditions = {
            checkVals: checkedList,
            runDay: ''
        };

        checkedList.forEach(item => {
            if (item === 'person') {
                conditions.dutyUserId = `${user.id}`;
            } else if (item === 'todayUpdate') {
                conditions.runDay = moment()
                conditions.dutyUserId = `${user.id}`;
            }
        })

        // 清理掉责任人信息
        if (!conditions.dutyUserId && dutyUserId === `${user.id}`) {
            conditions.dutyUserId = '';
        }

        this.setState(conditions, this.loadPatchData)
    }

    initTaskColumns = () => {
        return [{
            title: '补数据名称',
            dataIndex: 'fillDataJobName',
            key: 'fillDataJobName',
            width: 150,
            render: (text, record) => {
                return (
                    <Link to={`/operation/task-patch-data/${text}`}>{text}</Link>
                )
            }
        },
        {
            width: 150,
            title: '成功/已完成/总实例',
            dataIndex: 'doneJobSum',
            key: 'doneJobSum',
            render: (text, record) => {
                const isComplete = record.finishedJobSum == record.doneJobSum && record.doneJobSum == record.allJobSum;
                const style = isComplete ? { color: '#333333' } : { color: '#EF5350' };
                return <span style={style}>
                    {record.finishedJobSum}/{record.doneJobSum}/{record.allJobSum}
                </span>
            }
        },
        {
            width: 150,
            title: '业务日期',
            dataIndex: 'fromDay',
            key: 'fromDay',
            render: (text, record) => {
                return <span>{record.fromDay} ~ {record.toDay}</span>
            }
        }, {
            width: 150,
            title: '实例生成时间',
            dataIndex: 'createTime',
            key: 'createTime'
        }, {
            width: 100,
            title: '操作人',
            dataIndex: 'dutyUserName',
            key: 'dutyUserName'
        }, {
            width: 100,
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

    disabledDate = (current) => {
        return current && current.valueOf() > Date.now();
    }

    render () {
        const {
            tasks, current, checkVals,
            dutyUserId, bizDay,
            runDay, jobName
        } = this.state

        const { projectUsers } = this.props
        const userItems = projectUsers && projectUsers.length > 0
            ? projectUsers.map((item) => {
                return (
                    <Option key={item.userId} value={`${item.userId}`} name={item.user.userName}>
                        {item.user.userName}
                    </Option>)
            }) : []

        const pagination = {
            total: tasks.totalCount,
            defaultPageSize: 20,
            current
        };

        const title = (
            <Form
                layout="inline"
                style={{ marginTop: '10px' }}
                className="m-form-inline"
            >
                <FormItem>
                    <Search
                        placeholder="按任务名称搜索"
                        style={{ width: '200px' }}
                        value={jobName}
                        size="default"
                        onChange={this.onChangeJobName}
                        onSearch={this.onSearchByJobName}
                    />
                </FormItem>
                <FormItem label="业务日期">
                    <DatePicker
                        format="YYYY-MM-DD"
                        placeholder="业务日期"
                        style={{ width: '120px' }}
                        value={bizDay || null}
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
                        value={runDay || null}
                        disabledDate={this.disabledDate}
                        onChange={this.onRunningTime}
                    />
                </FormItem>
                <FormItem label="操作人">
                    <Select
                        allowClear
                        showSearch
                        size='Default'
                        style={{ width: '126px' }}
                        placeholder="操作人"
                        optionFilterProp="name"
                        value={dutyUserId}
                        onChange={this.onOwnerChange}
                    >
                        {userItems}
                    </Select>
                </FormItem>
                <FormItem>
                    <Checkbox.Group value={checkVals} onChange={this.onCheckChange}>
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
                        rowKey="id"
                        columns={this.initTaskColumns()}
                        className="m-table full-screen-table-90"
                        style={{ marginTop: 1 }}
                        pagination={pagination}
                        dataSource={tasks.data || []}
                        onChange={this.pageChange}
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
        projectUsers: state.projectUsers
    }
})(PatchDataList)
