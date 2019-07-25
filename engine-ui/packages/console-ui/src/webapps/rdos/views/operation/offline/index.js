import React, { Component } from 'react'
import { hashHistory } from 'react-router'
import { connect } from 'react-redux'
import moment from 'moment'
import { cloneDeep } from 'lodash'

import {
    Card, Row,
    Col, Table, DatePicker
} from 'antd'

import Resize from 'widgets/resize'

import Api from '../../../api'
import { lineAreaChartOptions, TASK_STATUS } from '../../../comm/const'
import {
    workbenchActions
} from '../../../store/modules/offlineTask/offlineAction'

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/legend');
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

class OfflineStatistics extends Component {
    state = {
        offline: '',
        topTiming: [],
        topError: [],
        handleTiming: moment(),
        lineChart: ''
    }

    componentDidMount () {
        this.loadChartData()
        this.getTopTaskTime()
        this.getTopJobError()
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps (nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadChartData()
            this.getTopTaskTime()
            this.getTopJobError()
        }
    }

    resize = () => {
        if (this.state.lineChart) this.state.lineChart.resize()
    }

    loadChartData = () => {
        const ctx = this
        Api.getJobGraph().then((res) => {
            if (res.code === 1) {
                ctx.initLineChart(res.data)
            }
        })
    }

    getTopTaskTime = (dateTime) => {
        const ctx = this
        const { handleTiming } = this.state
        const params = {
            startTime: handleTiming && handleTiming.set({
                'hour': 0,
                'minute': 0,
                'second': 0
            }).unix(),
            endTime: handleTiming && handleTiming.set({
                'hour': 23,
                'minute': 59,
                'second': 59
            }).unix()
        }
        Api.getJobTopTime(handleTiming && params).then((res) => {
            if (res.code === 1) {
                ctx.setState({ topTiming: res.data })
            }
        })
    }

    getTopJobError = () => {
        const ctx = this
        Api.getJobTopError().then((res) => {
            if (res.code === 1) {
                ctx.setState({ topError: res.data })
            }
        })
    }

    getSeries = (data) => {
        const arr = []
        if (data && data.y) {
            const legend = data && data.type ? data.type.data : []
            for (let i = 0; i < legend.length; i++) {
                arr.push({
                    name: legend[i],
                    symbol: 'none',
                    smooth: true,
                    type: 'line',
                    data: data.y[i].data
                })
            }
        }
        return arr
    }

    initLineChart (chartData) {
        const chartContainer = document.getElementById('TaskTrend');
        if (!chartContainer) return;
        let myChart = echarts.init(chartContainer);
        const option = cloneDeep(lineAreaChartOptions);
        option.title.text = ''
        option.tooltip.axisPointer.label.formatter = '{value}: 00'
        option.yAxis[0].minInterval = 1

        option.xAxis[0].axisLabel.formatter = '{value} 时';
        option.legend.data = chartData && chartData.type ? chartData.type.data : []
        option.xAxis[0].data = chartData && chartData.x ? chartData.x.data : []
        option.series = this.getSeries(chartData)
        // 绘制图表
        myChart.setOption(option);
        this.setState({ lineChart: myChart })
    }

    changeHandleTiming = (value) => {
        this.setState({ handleTiming: value }, () => {
            this.getTopTaskTime()
        })
    }

    jumpToOffline (text, date, status) {
        hashHistory.push({
            pathname: '/operation/offline-operation',
            query: {
                job: text,
                status: status,
                date: date
            }
        })
    }

    topTaskTiming = () => {
        return [{
            title: '任务名称',
            dataIndex: 'taskName',
            key: 'taskName',
            render: (text, record) => {
                const content = record.isDeleted === 1 ? `${text}(已删除)`
                    : <a onClick={this.props.goToTaskDev.bind(this, record.taskId)}>{text}</a>
                return content;
            }
        }, {
            title: '任务实例类型',
            dataIndex: 'taskTypeName',
            key: 'taskTypeName'
        }, {
            title: '调度时间',
            dataIndex: 'cycTime',
            key: 'cycTime'
        }, {
            title: '责任人',
            dataIndex: 'createUser',
            key: 'createUser'
        }, {
            title: '执行时长',
            dataIndex: 'runTime',
            key: 'runTime'
        }]
    }

    topTaskError = () => {
        return [{
            title: '任务名称',
            dataIndex: 'taskName',
            key: 'taskName',
            render: (text, record) => {
                const content = record.isDeleted === 1 ? `${text} (已删除)` : text

                return <a onClick={this.jumpToOffline.bind(this, text, 30, TASK_STATUS.RUN_FAILED)}>{content}</a>;
            }
        }, {
            title: '责任人',
            dataIndex: 'createUser',
            key: 'createUser'
        }, {
            title: '出错次数',
            dataIndex: 'errorCount',
            key: 'errorCount'
        }]
    }

    disabledDate = (current) => {
        return current && current.valueOf() > moment().add(1, 'days').valueOf();
    }

    render () {
        const { topTiming, topError, handleTiming } = this.state
        return (
            <div className="box-card" style={{ marginTop: '20px' }}>
                <Card
                    noHovering
                    bordered={false}
                    loading={false}
                    className="shadow"
                    title="今日周期实例完成情况"
                >
                    <Resize onResize={this.resize}>
                        <article id="TaskTrend" style={{ width: '100%', height: '300px' }}/>
                    </Resize>
                </Card>
                <Row className="m-card" style={{ marginTop: '20px' }}>
                    <Col span="14" style={{ paddingRight: '10px' }}>
                        <Card
                            noHovering
                            bordered={false}
                            loading={false}
                            className="shadow"
                            title="执行时长排行" extra={
                                <DatePicker
                                    style={{ width: 100, float: 'right', marginTop: '10px' }}
                                    format="YYYY-MM-DD"
                                    value={handleTiming}
                                    disabledDate={this.disabledDate}
                                    onChange={this.changeHandleTiming}
                                />}>
                            <Table
                                rowKey="id"
                                pagination={false}
                                className="dt-ant-table"
                                style={{ minHeight: '0' }}
                                columns={this.topTaskTiming()}
                                dataSource={topTiming || []}
                            />
                        </Card>
                    </Col>
                    <Col span="10" style={{ paddingLeft: '10px' }}>
                        <Card
                            noHovering
                            bordered={false}
                            loading={false}
                            className="shadow"
                            title="近30天出错排行">
                            <Table
                                rowKey="id"
                                className="dt-ant-table"
                                pagination={false}
                                style={{ minHeight: '0' }}
                                columns={this.topTaskError()}
                                dataSource={topError || []}
                            />
                        </Card>
                    </Col>
                </Row>
            </div>
        )
    }
}
export default connect((state) => {
    return {
        project: state.project
    }
}, dispatch => {
    const actions = workbenchActions(dispatch)
    return {
        goToTaskDev: (id) => {
            actions.openTaskInDev(id)
        }
    }
})(OfflineStatistics)
