import React, { Component } from 'react'
import { Link } from 'react-router'
import { connect } from 'react-redux'
import moment from 'moment'

import {
    Card,
 } from 'antd'

import Api from '../../../api'
import { pieChartOptions } from '../../../comm/const'

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入饼状图
require('echarts/lib/chart/pie');
// 引入提示框和标题组件
require('echarts/lib/component/tooltip');
// 引入主题
require('echarts/theme/shine');
require('echarts/lib/component/title');

class RealtimeCount extends Component {

    state = {
        realtime: {},
        chart: '',
    }

    componentDidMount() {
        this.loadRealtimeData()
        this.resizeChart()
    }

    componentWillReceiveProps(nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadRealtimeData()
        }
    }

    loadRealtimeData() {
        const ctx = this
        Api.taskStatistics().then((res) => {
            if (res.code === 1) {
                 ctx.initChart(res.data)
            }
        })
    }

    getSeriesData = (data) => {
        if (!data) return []
        return [{
            name: '失败',
            value: data.FAILED || 0
        }, {
            name: '运行中',
            value: data.RUNNING || 0
        }, {
            name: '停止',
            value: data.CANCELED || 0
        }, {
            name: '等待运行',
            value: data.WAITENGINE || 0
        }, {
            name: '等待提交',
            value: data.UNSUBMIT || 0
        }]
    }

    initChart(chartData) {
        let myChart = echarts.init(document.getElementById('realtimePie'), 'shine');
        const option = pieChartOptions;
        option.title.text = `实时任务数量`
        option.title.subtext = moment().format('YYYY-MM-DD')
        option.legend.data = [
            { name: '失败', icon: 'circle' },
            { name: '运行中', icon: 'circle' },
            { name: '停止', icon: 'circle' },
            { name: '等待运行', icon: 'circle' },
            { name: '等待提交', icon: 'circle' },
        ]
        option.series[0].name = `总量：${chartData.ALL}`
        option.series[0].data = this.getSeriesData(chartData)
        myChart.setOption(option)
        this.setState({ chart: myChart })
    }

    resizeChart = () => {
        window.addEventListener('resize', () => {
            if (this.state && this.state.chart) {
                this.state.chart.resize()
            }
        })
    }

    render() {
        const { realtime } = this.state
        return (
            <Card>
                <div id="realtimePie" style={{ height: '300px' }}></div>
            </Card>
        )
    }
}
export default connect((state) => {
    return {
        project: state.project,
    }
})(RealtimeCount)
