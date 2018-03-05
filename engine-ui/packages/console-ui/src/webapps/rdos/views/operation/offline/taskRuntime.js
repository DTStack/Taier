import React, { Component } from 'react'
import { Link } from 'react-router'
import { Button, Row, Col, Card, Radio } from 'antd'
import moment from 'moment'
import { cloneDeep } from 'lodash'

import GoBack from 'widgets/go-back'
import Resize from 'widgets/resize'

import Api from '../../../api'
import { lineAreaChartOptions } from '../../../comm/const'

import { 
    OfflineTaskStatus, TaskTimeType, TaskType 
} from '../../../components/status'

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;

export default class TaskLog extends Component {

    state = {
        data: {},
    }

    componentDidMount() {
        const currentTask = this.props.tabData
        if (currentTask) {
            this.loadRuntimeInfo(currentTask.id)
        }
    }

    componentWillReceiveProps(nextProps) {
        const currentTask = this.props.tabData
        const nextTask = nextProps.tabData
        if (nextTask) {
            this.loadRuntimeInfo(nextTask.id)
        } else if (currentTask) {
            this.loadRuntimeInfo(currentTask.id)
        }
    }

    loadRuntimeInfo = (taskId) => {
        const ctx = this
        // Test API
        Api.getJobGraph().then((res) => {
            if (res.code === 1) {
                ctx.initLineChart(res.data)
            }
        })
        // Api.getJobRuntimeInfo({ taskId }).then((res) => {
        //     if (res.code === 1) {
        //         this.setState({ data: res.data || {} })
        //         this.initLineChart(res.data)
        //     }
        // })
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

        let myChart = echarts.init(document.getElementById('RunTimeTrend'));
        const option = cloneDeep(lineAreaChartOptions);
        
        option.grid = {
            left: 70,
            right: 50
        }

        option.title.text = ''
        option.tooltip.axisPointer.label.formatter = '{value}: 00'
 
        option.legend.data = chartData && chartData.type ? chartData.type.data : []
        option.xAxis[0].data =  chartData && chartData.x ? chartData.x.data : []
        option.series = this.getSeries(chartData)

        option.yAxis[0].minInterval = 1
        option.yAxis[0].name = '执行时长（秒）'

        option.yAxis[1] = cloneDeep(option.yAxis[0])
        option.yAxis[1].name = '数据量（条）'
        option.yAxis[1].axisLine.show = false
        option.yAxis[1].splitLine.show = false

        // 绘制图表
        myChart.setOption(option);

        this._chart = myChart;
    }

    resizeChart = () => {
        if (this._chart) {
            this._chart.resize()
        }
    }

    render() {

        const { data } = this.state

        const tStyle = {
            width: '50px',
            margin: 0,
            marginTop: '10px',
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
                                <span className="m-count-content font-black">{data.FAILED || 0}</span>
                            </section>
                        </Col>
                        <Col span={4}>
                            <section className="m-count-section" style={tStyle}>
                                <span className="m-count-title">补数据</span>
                                <span className="m-count-content font-black">{data.RUNNING || 0}</span>
                            </section>
                        </Col>
                        <Col span={4}>
                            <section className="m-count-section" style={tStyle}>
                                <span className="m-count-title">失败次数</span>
                                <span className="m-count-content font-red">{data.WAITENGINE || 0}</span>
                            </section>
                        </Col>
                    </Row>
                    <Resize onResize={this.resizeChart}>
                        <div id="RunTimeTrend" style={{width: '100%', height: '330px'}}></div>
                    </Resize>
                </Card>
            </div>
        )
    }
}
