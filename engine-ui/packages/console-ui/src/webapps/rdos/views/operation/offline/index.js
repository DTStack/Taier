import React, { Component } from 'react'
import { Link, routeHistory } from 'react-router'
import { connect } from 'react-redux'
import moment from 'moment'
import { cloneDeep } from 'lodash'

import {
    Card, Button, Row,
    Col, Table, DatePicker,
 } from 'antd'

import Api from '../../../api'
import { lineAreaChartOptions } from '../../../comm/const'
import {
    workbenchActions
} from '../../../store/modules/offlineTask/offlineAction' 

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

class OfflineStatistics extends Component {

    state = {
        offline: '',
        topTiming: [],
        topError: [],
        handleTiming: moment(),
        lineChart: '',
    }

    componentDidMount() {
        this.loadOfflineData()
        this.loadChartData()
        this.getTopTaskTime()
        this.getTopJobError()
        this.resizeChart()
    }

    componentWillReceiveProps(nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadOfflineData()
            this.loadChartData()
            this.getTopTaskTime()
            this.getTopJobError()
        }
    }
    
    componentWillUnmount() {
        window.removeEventListener('resize', this.resize, false);
    }

    resize = () => {
        if (this.state.lineChart) this.state.lineChart.resize()
    }

    resizeChart = () => {
        window.addEventListener('resize', this.resize, false)
    }

    loadOfflineData = () => {
        const ctx = this
        Api.getJobStatistics().then((res) => {
            if (res.code === 1) {
                ctx.setState({ offline: res.data})
            }
        })
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
            startTime: handleTiming.set({
                'hour': 0,
                'minute': 0,
                'second': 0,
            }).unix(), 
            endTime: handleTiming.set({
                'hour': 23,
                'minute': 59,
                'second': 59,
            }).unix(),
        }
        Api.getJobTopTime(params).then((res) => {
            if (res.code === 1) {
                ctx.setState({ topTiming: res.data})
            }
        })
    }

    getTopJobError = () => {
        const ctx = this
        Api.getJobTopError().then((res) => {
            if (res.code === 1) {
                ctx.setState({ topError: res.data})
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
                    type:'line',
                    data: data.y[i].data,
                })
            }
        }
        return arr
    }

    initLineChart(chartData) {
        let myChart = echarts.init(document.getElementById('TaskTrend'));
        const option = cloneDeep(lineAreaChartOptions);
        option.title.text = '任务完成情况'
        option.tooltip.axisPointer.label.formatter = '{value}: 00'
        option.yAxis[0].minInterval = 1
        option.legend.data = chartData && chartData.type ? chartData.type.data : []
        option.xAxis[0].data =  chartData && chartData.x ? chartData.x.data : []
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

    topTaskTiming = () => {
        return [{
            title: '任务名称',
            dataIndex: 'taskName',
            key: 'taskName',
            render: (text, record) => {
                return <a onClick={this.props.goToTaskDev.bind(this, record.taskId)}>{text}</a>
            },
        },{
            title: '任务类型',
            dataIndex: 'type',
            key: 'type',
            render: (text, record) => {
                return record.type === 1 ? '补数据' : '周期调度'
            }
        },{
            title: '调度时间',
            dataIndex: 'cycTime',
            key: 'cycTime',
        }, {
            title: '责任人',
            dataIndex: 'createUser',
            key: 'createUser',
        }, {
            title: '执行时长',
            dataIndex: 'runTime',
            key: 'runTime',
        }]
    }

    topTaskError = () => {
        return [{
            title: '任务名称',
            dataIndex: 'taskName',
            key: 'taskName',
            render: (text, record) => {
                return <a onClick={this.props.goToTaskDev.bind(this, record.taskId)}>{text}</a>
            },
        }, {
            title: '责任人',
            dataIndex: 'createUser',
            key: 'createUser',
        }, {
            title: '出错次数',
            dataIndex: 'errorCount',
            key: 'errorCount',
        }]
    }

    disabledDate = (current) => {
        return current && current.valueOf() > moment().add(1, 'days').valueOf();
    }

    render() {
        const { offline, topTiming, topError, handleTiming } = this.state
        return (
            <article className="section" style={{ paddingBottom: '100px' }}>
                <Card title="离线任务">
                    <article id="TaskTrend" style={{width: '100%', height: '300px'}}/>
                </Card>
                <Row style={{marginTop: '20px'}}>
                    <Col span="12" className="section">
                        <Card title="执行时长排行" extra={
                            <DatePicker 
                                style={{ width: 100, float: 'right' }}
                                format="YYYY-MM-DD"
                                value={handleTiming}
                                disabledDate={this.disabledDate}
                                onChange={this.changeHandleTiming}
                            />}>
                            <Table
                                rowKey="id"
                                pagination={false}
                                style={{minHeight: '0'}}
                                columns={this.topTaskTiming()}
                                dataSource={topTiming || []}
                            />
                        </Card>
                    </Col>
                    <Col span="12">
                        <Card title="近30天出错排行">
                            <Table
                                rowKey="id"
                                pagination={false}
                                style={{minHeight: '0'}}
                                columns={this.topTaskError()}
                                dataSource={topError || []}
                            />
                        </Card>
                    </Col>
                </Row>
            </article>
        )
    }
}
export default connect((state) => {
    return {
        project: state.project,
    }
}, dispatch => {
    const actions = workbenchActions(dispatch)
    return {
        goToTaskDev: (id) => {
            actions.openTaskInDev(id)
        }
    }
})(OfflineStatistics)
