import React, { Component } from 'react'
import { Row, Col, Card, Radio } from 'antd'
import moment from 'moment'
import { cloneDeep } from 'lodash'

import Resize from 'widgets/resize'

import Api from '../../../api'
import { lineAreaChartOptions } from '../../../comm/const'

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/tooltip');
require('echarts/lib/component/legend');
require('echarts/lib/component/title');

const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;

export default class TaskLog extends Component {
    state = {
        data: {}
    }

    componentDidMount () {
        const currentTask = this.props.tabData
        if (currentTask) {
            this.loadRuntimeInfo({
                taskId: currentTask.id,
                count: 30
            })
        }
    }

    componentWillReceiveProps (nextProps) {
        const currentTask = this.props.tabData
        const { tabData, visibleSlidePane } = nextProps
        if (tabData && currentTask && visibleSlidePane && tabData.id !== currentTask.id) {
            this.loadRuntimeInfo({
                taskId: tabData.id,
                count: 30
            })
        }
    }

    loadRuntimeInfo = (params) => {
        const ctx = this
        Api.statisticsTaskRunTime(params).then((res) => {
            if (res.code === 1) {
                this.setState({ data: res.data })
                const chartData = res.data.jobInfoList;
                ctx.initLineChart(chartData)
            }
        })
    }

    onRadioChange = (e) => {
        const { tabData } = this.props
        this.loadRuntimeInfo({
            taskId: tabData.id,
            count: e.target.value
        })
    }

    handData = (data) => {
        let arr = []; let xAxis = []; let series = [];
        const legend = ['执行时长', '读取数据', '脏数据'];
        const stayTiming = []; const readData = []; const dirtyData = [];

        if (data) {
            for (let i = data.length - 1; i >= 0; i--) {
                const item = data[i]
                xAxis.push(moment(item.exeStartTime).format('YYYY-MM-DD HH:mm:ss'))
                stayTiming.push(item.exeTime || 0)
                readData.push(item.totalCount || 0)
                dirtyData.push(item.dirtyNum || 0)
            }
        }
        return {
            legend,
            xAxis,
            series: [
                {
                    name: '执行时长',
                    type: 'line',
                    yAxisIndex: 0,
                    markLine: {
                        precision: 1
                    },
                    data: stayTiming
                }, {
                    name: '读取数据',
                    type: 'line',
                    yAxisIndex: 1,
                    markLine: {
                        precision: 1
                    },
                    data: readData
                }, {
                    name: '脏数据',
                    yAxisIndex: 1,
                    markLine: {
                        precision: 1
                    },
                    type: 'line',
                    data: dirtyData
                }
            ]
        }
    }

    handNullData = (data) => {
        const xAxis = [moment(new Date()).format('YYYY-MM-DD HH:mm:ss')];
        const legend = ['无数据'];
        const noData = [0]
        return {
            legend,
            xAxis,
            series: [
                {
                    name: '无数据',
                    type: 'line',
                    yAxisIndex: 0,
                    markLine: {
                        precision: 1
                    },
                    data: noData
                }
            ]
        }
    }

    initLineChart (chartData) {
        let data;
        if (chartData.length > 0) {
            data = this.handData(chartData);
        } else {
            data = this.handNullData();
        }

        let myChart = echarts.init(document.getElementById('RunTimeTrend'));
        const option = cloneDeep(lineAreaChartOptions);

        option.grid = {
            left: '3%',
            right: '4%',
            bottom: '10%',
            containLabel: true
        }

        option.title.text = ''

        option.legend = {
            data: data.legend,
            y: 'bottom'
        }

        option.tooltip.formatter = function (params) {
            return `${params[0] && params[0].axisValue}
                <br />${params[0] && params[0].seriesName}: ${params[0] && params[0].value} 秒
                <br />${params[1] && params[1].seriesName}: ${params[1] && params[1].value} 条
                <br />${params[2] && params[2].seriesName}: ${params[2] && params[2].value} 条
                `
        }
        option.xAxis[0].axisTick = {
            show: false,
            alignWithLabel: true
        }

        option.xAxis[0].boundaryGap = ['5%', '5%'];
        option.xAxis[0].axisLabel = {
            align: 'center',
            color: '#666666',
            margin: 12,
            formatter: '{value}'
        }
        option.xAxis[0].type = 'category';
        option.xAxis[0].data = data.xAxis;

        option.yAxis[0].name = '执行时长（秒）'
        option.yAxis[0].axisLabel.formatter = '{value}'

        option.yAxis[1] = cloneDeep(option.yAxis[0])
        option.yAxis[1].name = '数据量（条）'
        option.yAxis[1].axisLine.show = false
        option.yAxis[1].splitLine.show = false
        option.yAxis[1].minInterval = 1// 刻度为整数
        option.yAxis[1].axisLabel.formatter = '{value}'

        option.series = data.series;
        // 绘制图表
        myChart.setOption(option);

        this._chart = myChart;
    }

    resizeChart = () => {
        if (this._chart) {
            this._chart.resize()
        }
    }

    render () {
        const { data } = this.state

        const tStyle = {
            width: '55px',
            margin: 0,
            marginTop: '10px'
        }

        return (
            <div className="m-card m-radio-group">
                <Card
                    noHovering
                    bordered={false}
                    loading={false}
                    title="执行时长分析"
                    extra={
                        <RadioGroup
                            defaultValue={30}
                            className="no-bd"
                            onChange={this.onRadioChange}
                            style={{ marginTop: '8.5px' }}
                        >
                            <RadioButton value={7}>近7次</RadioButton>
                            <RadioButton value={30}>近30次</RadioButton>
                            <RadioButton value={60}>近60次</RadioButton>
                        </RadioGroup>
                    }
                >
                    <Row className="m-count" style={{ height: '70px', padding: '0 20px' }}>
                        <Col span={4}>
                            <section className="m-count-section" style={tStyle}>
                                <span className="m-count-title">周期执行</span>
                                <span className="m-count-content font-black">{data.cronExeNum || 0}</span>
                            </section>
                        </Col>
                        <Col span={4}>
                            <section className="m-count-section" style={tStyle}>
                                <span className="m-count-title">补数据</span>
                                <span className="m-count-content font-black">{data.fillDataExeNum || 0}</span>
                            </section>
                        </Col>
                        <Col span={4}>
                            <section className="m-count-section" style={tStyle}>
                                <span className="m-count-title">失败次数</span>
                                <span className="m-count-content font-red">{data.failNum || 0}</span>
                            </section>
                        </Col>
                    </Row>
                    <Resize onResize={this.resizeChart}>
                        <div id="RunTimeTrend" style={{ width: '100%', height: '350px' }}></div>
                    </Resize>
                </Card>
            </div>
        )
    }
}
