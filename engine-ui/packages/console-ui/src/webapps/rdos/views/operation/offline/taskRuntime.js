import React, { Component } from 'react'
import { Link } from 'react-router'
import { Button, Row } from 'antd'
import moment from 'moment'
import { cloneDeep } from 'lodash'

import GoBack from 'widgets/go-back'

import Api from '../../../api'
import { defaultBarOption } from '../../../comm/const'

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

const barOption = {
    color: ['#18a689'],
    tooltip : {
        trigger: 'axis',
        formatter: "{a} : {c} 秒",
        axisPointer : {   // 坐标轴指示器，坐标轴触发有效
            type : 'shadow', // 默认为直线，可选为：'line' | 'shadow'
        }
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    legend: {
        right: 0,
        data: ['耗时']
    },
    xAxis : [
        {
            type : 'category',
            data : [],
            axisLine: {
                lineStyle: {
                    color: '#dddddd',
                    width: 1,
                }
            },
            axisTick: {
                show: false,
                alignWithLabel: true
            },
            axisLabel: {
                textStyle: {
                    color: '#333333'
                },
            },
        }
    ],
    yAxis : [
        {   
            name: '耗时',
            type : 'value',
            axisLine: {
                lineStyle: {
                    color: '#dddddd',
                    width: 1,
                }
            },
            position: 'top',
            axisLabel: {
                 formatter: '{value} 秒',
                textStyle: {
                    color: '#333333'
                },
            },
            axisTick: {
                show: false,
            },
        },
    ],
    series : [
        {
            name:'耗时',
            type:'bar',
            barWidth: 30,
            data:[]
        }
    ]
};

export default class TaskLog extends Component {

    state = {
        taskInfo: '',
        chart1: '',
    }

    componentDidMount() {
        this.loadRuntimeInfo()
        this.resizeChart()
    }

    loadRuntimeInfo = () => {
        const ctx = this
        const jobId = this.props.params.jobId
        Api.getJobRuntimeInfo({ taskId: jobId }).then((res) => {
            if (res.code === 1) {
                this.setState({ taskInfo: res.data })
                this.initLineChart(res.data)
            }
        })
    }

    initLineChart(chartData) {

        let myChart = echarts.init(document.getElementById('RunTimeTrend'));
        const data = this.getChartData(chartData)

        const option = barOption;
        option.backgroundColor = '#ffffff'
        option.xAxis[0].data = data.x
        option.series = data.series

        // 绘制图表
        myChart.setOption(option);
        this.setState({ chart1: myChart })
    }

    getChartData = (data) => {
        const x = []
        const seriesData = []
        if (data && data.length > 0) {
            for (let i = 0; i < data.length; i++) {
                x.push(moment(data[i].startTime).format('YYYY-MM-DD'))
                const ms = Math.round(data[i].execTime)
                seriesData.push(ms || 0)
            }
        }
        return {
            x,
            series: [{
                name: '耗时',
                type:'bar',
                barWidth: '60%',
                data: seriesData
            }]
        }
    }

    resizeChart = () => {
        const { chart1 } = this.state
        if (chart1) {
            chart1.resize()
        }
    }

    render() {

        const { taskInfo } = this.state

        const tdStyle = {
            width: '110px',
            background: '#fcfcfc',
        }

        return (
            <div className="runtime-page">
                <header className="bd-bottom">
                    <span className="left">运行时长</span>&nbsp;&nbsp;
                    <GoBack className="right" icon="rollback" size="small" />
                </header>
                <div className="runtime-content">
                    <article className="runtime-section">
                        <h1>运行信息</h1>
                        <div id="RunTimeTrend" className="runtime-trend bd"
                            style={{width: '100%', height: '330px'}}>
                        </div>
                    </article>
                </div>
            </div>
        )
    }
}
