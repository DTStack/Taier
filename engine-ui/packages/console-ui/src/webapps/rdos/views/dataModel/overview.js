import React, { Component } from 'react';
import moment from 'moment'

import {
    Row, Col, Card
} from 'antd';

import utils from 'utils';
import Resize from 'widgets/resize';

import Api from '../../api/dataModel';
import { 
    lineAreaChartOptions, 
    pieChartOptions,
    defaultBarOption,
} from '../../comm/const';

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
require('echarts/lib/chart/pie');
// 引入提示框和标题组件
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

export default class Overview extends Component {

    state = {
        data: '',

        total: '',
        tableRate: '',
        columnRate: '',
        tableTrend: '',
        columnTrend: '',
    }

    componentDidMount() {
        this.statisticTotal();
        this.statisticTableRate();
        this.statisticColumnRate();
        this.statisticTableTrend();
        this.statisticColumnTrend();
    }

    statisticTotal = () => {
        Api.getCheckPartitions().then(res => {
            if (res.code === 1) {
                this.setState({
                    total: res.data
                })
            }
        })
    }

    statisticTableRate = () => {
        Api.statisticTableRate().then(res => {
            if (res.code === 1) {
                this.drawChart1(res.data)
            }
        })
    }

    statisticColumnRate = () => {
        Api.statisticColumnRate().then(res => {
            if (res.code === 1) {
                this.drawChart2(res.data)
            }
        })
    }

    statisticTableTrend = () => {
        Api.statisticTableTrend().then(res => {
            if (res.code === 1) {
                this.drawChart3(res.data)
            }
        })
    }

    statisticColumnTrend = () => {
        Api.statisticColumnTrend().then(res => {
            if (res.code === 1) {
                this.drawChart4(res.data)
            }
        })
    }

    handLinChartData = (data) => {

        let arr = [], xAxis = [], series = [];
        const legend = ['执行时长', '读取数据', '脏数据'];
        const arrData = [];
        if (data) {
            for (let i = data.length - 1; i >= 0; i--) {
                const item = data[i]
                xAxis.push(moment(item.exeStartTime).format('YYYY-MM-DD HH:mm:ss'))
                arrData.push(item.exeTime || 0)
            }
        }
        return {
            legend,
            xAxis,
            series: [
                {
                    name: '执行时长',
                    symbol: 'none',
                    type:'line',
                    yAxisIndex: 0,
                    markLine: {
                        precision: 1,
                    },
                    data: arrData,
                }
            ]
        }
    }

    drawChart1 = (chartData) => {
        
    }

    drawChart2 = (chartData) => {
        const data = this.handLinChartData(chartData);

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
            y: 'bottom',
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
            alignWithLabel: true,
        }

        option.xAxis[0].boundaryGap = ['5%', '5%'];
        option.xAxis[0].axisLabel ={
            align: 'center',
            color: '#666666',
            margin: 12,
            formatter: '{value}'
        }
        option.xAxis[0].type = 'category';
        option.xAxis[0].data = data.xAxis;

        option.yAxis[0].name = '执行时长（秒）'
        option.yAxis[0].axisLabel.formatter = '{value} 秒'

        option.series = data.series;
        // 绘制图表
        myChart.setOption(option);

        this._chart = myChart;
    }

    drawChart3 = (chartData) => {

    }

    drawChart4 = (chartData) => {
        const data = this.handLinChartData(chartData);

        let myChart = echarts.init(document.getElementById('Chart4'));
        const option = cloneDeep(lineAreaChartOptions);

        option.grid = {
            left: '3%',
            right: '4%',
            bottom: '10%',
            containLabel: true
        }

        option.title.text = '';

        option.legend = {
            data: data.legend,
            y: 'bottom',
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
            alignWithLabel: true,
        }

        option.xAxis[0].boundaryGap = ['5%', '5%'];
        option.xAxis[0].axisLabel ={
            align: 'center',
            color: '#666666',
            margin: 12,
            formatter: '{value}'
        }
        option.xAxis[0].type = 'category';
        option.xAxis[0].data = data.xAxis;

        option.yAxis[0].name = '执行时长（秒）'
        option.yAxis[0].axisLabel.formatter = '{value} 秒'

        option.series = data.series;
        // 绘制图表
        myChart.setOption(option);

        this.chart4 = myChart;
    }

    resizeChart = () => {
        if (this.chart1) {
            this.chart1.resize()
            this.chart2.resize()
            this.chart3.resize()
            this.chart4.resize()
        }
    }

    render() {
        const { data } = this.state;
        const flex = {
            flexGrow: 1,
            flex: 1
        };

        const countWidth = {
            width: '100px',
        }
        return (
            <Resize onResize={this.resizeChart}>
                <div className="data-model-overview" style={{ background: '#f2f7fa' }}>
                        <Row style={{marginTop: '10px'}}>
                            <h1 className="box-title box-title-bolder">
                                模型汇总信息
                            </h1>
                            <div className="box-4 m-card m-card-small">
                                <Card
                                    noHovering
                                    bordered={false}
                                    loading={false} 
                                    title="今日任务完成情况"
                                >
                                    <Row className="m-count" style={{display: 'flex'}}>
                                        <Col style={flex}>
                                            <section style={countWidth} className="m-count-section">
                                                <span className="m-count-title">今日新增模型</span>
                                                <span className="m-count-content font-black">{data.ALL || 0}</span>
                                            </section>
                                        </Col>
                                        <Col style={flex}>
                                            <section style={countWidth} className="m-count-section">
                                                <span className="m-count-title">今日新增指标</span>
                                                <span className="m-count-content font-red">{data.FAILED || 0}</span>
                                            </section>
                                        </Col>
                                        <Col style={flex}>
                                            <section style={countWidth} className="m-count-section">
                                                <span className="m-count-title">今日不规范模型</span>
                                                <span className="m-count-content font-organge">{data.RUNNING || 0}</span>
                                            </section>
                                        </Col>
                                        <Col style={flex}>
                                            <section style={countWidth} className="m-count-section">
                                                <span className="m-count-title">今日不规范指标</span>
                                                <span className="m-count-content font-green">{data.FINISHED || 0}</span>
                                            </section>
                                        </Col>
                                        <Col style={flex}>
                                            <section style={countWidth} className="m-count-section">
                                                <span className="m-count-title">累计不规范模型</span>
                                                <span className="m-count-content font-gray">{data.UNSUBMIT || 0}</span>
                                            </section>
                                        </Col>
                                        <Col style={flex}>
                                            <section style={countWidth} className="m-count-section">
                                                <span className="m-count-title">累计不规范指标</span>
                                                <span className="m-count-content font-organge">{data.SUBMITTING || 0}</span>
                                            </section>
                                        </Col>
                                    </Row>
                                </Card>
                            </div>
                        </Row>
                        <Row className="box-card" style={{marginTop: '20px'}}>
                            <Col span={12} className="m-card m-card-small" style={{paddingRight: '10px'}}>
                                <Card 
                                    noHovering
                                    bordered={false}
                                    loading={false}  
                                    className="shadow"
                                    title="模型不规范原因分布"
                                >
                                    <div id="Chart1"></div>
                                </Card>
                            </Col>
                            <Col span={12} className="m-card m-card-small" style={{paddingLeft: '10px'}}>
                                <Card
                                    noHovering
                                    bordered={false}
                                    loading={false}  
                                    className="shadow"
                                    title="模型不规范趋势分析"
                                >
                                    <div id="Chart2"></div>
                                </Card>
                            </Col>
                        </Row>
                        <Row className="box-card">
                            <Col span={12} className="m-card m-card-small" style={{paddingRight: '10px'}}>
                                <Card
                                    noHovering
                                    bordered={false}
                                    loading={false}  
                                    className="shadow"
                                    title="字段不规范原因分布"
                                >
                                    <div id="Chart3"></div>
                                </Card>
                            </Col>
                            <Col span={12} className="m-card m-card-small" style={{paddingLeft: '10px'}}>
                                <Card
                                    noHovering
                                    bordered={false}
                                    loading={false}  
                                    className="shadow"
                                    title="字段不规范趋势分析"
                                >
                                    <div id="Chart4"></div>
                                </Card>
                            </Col>
                        </Row>
                </div>
            </Resize>
        )
    }
}