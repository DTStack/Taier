import React, { Component } from 'react'
import { Link } from 'react-router'
import { connect } from 'react-redux'
import moment from 'moment'

import {
    Card, Button, Row,
    Col, Table, DatePicker,
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
require('echarts/lib/component/title');

class OfflineCount extends Component {

    state = {
        offline: '',
        chart: '',
    }

    componentDidMount() {
        this.loadOfflineData()
        window.addEventListener('resize',  this.resizeChart, false)
    }

    componentWillUnmount() {
        document.removeEventListener('resize', this.resizeChart, false)
    }

    componentWillReceiveProps(nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadOfflineData()
        }
    }

    loadOfflineData = () => {
        const ctx = this
        Api.getJobStatistics().then((res) => {
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
            name: '已完成',
            value: data.FINISHED || 0
        }, {
            name: '等待运行',
            value: data.WAITENGINE || 0
        }, {
            name: '提交中',
            value: data.SUBMITTING || 0
        }, {
            name: '等待提交',
            value: data.UNSUBMIT || 0
        }, {
            name: '冻结',
            value: data.FROZEN || 0
        }]
    }

    initChart(chartData) {
        const ele = document.getElementById('offlinePie')
        if (!ele) return;
        let myChart = echarts.init(ele);
        const option = Object.assign({}, pieChartOptions);
        option.title.text = `离线任务数量`
        option.title.subtext = moment().format('YYYY-MM-DD')
        option.legend.data = [
            { name: '失败', icon: 'circle' },
            { name: '运行中', icon: 'circle' },
            { name: '已完成', icon: 'circle' },
            { name: '等待运行', icon: 'circle' },
            { name: '提交中', icon: 'circle' },
            { name: '等待提交', icon: 'circle' },
            { name: '冻结', icon: 'circle' },
        ]
        option.series[0].name = `总量:${chartData.ALL || 0}`
        option.series[0].cursor = 'initial'
        option.series[0].data = this.getSeriesData(chartData)
        myChart.setOption(option);
        this.setState({ chart: myChart })
    }

    resizeChart = () => {
        if (this.state && this.state.chart) {
            this.state.chart.resize()
        }
    }

    render() {
        const { offline } = this.state
        return (
            <Card>
                <div id="offlinePie" style={{ height: '300px' }}></div>
            </Card>
        )
    }
}
export default connect((state) => {
    return {
        project: state.project,
    }
})(OfflineCount)
