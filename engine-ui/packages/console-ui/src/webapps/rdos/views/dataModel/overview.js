import React, { Component } from 'react';
import moment from 'moment';
import { cloneDeep } from 'lodash';
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
        Api.statisticTotal().then(res => {
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
                this.drawChart3(res.data)
            }
        })
    }

    statisticTableTrend = () => {
        Api.statisticTableTrend().then(res => {
            if (res.code === 1) {
                this.drawChart2(res.data)
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

    drawChart1 = (chartData) => {
        let myChart = echarts.init(document.getElementById('Chart1'));
        const legendData = ['分层不合理', '主题域不合理', '增量不合理', '引用不合理'];
        const option = cloneDeep(pieChartOptions);
        option.title.text = '';
        option.legend = {
            data: legendData,
            x : 'center',
            y : 'bottom',
        };

        option.series[0].name = '模型不规范原因';
        option.series[0].data = [{
            name: legendData[0],
            value: chartData.grade,
        }, {
            name: legendData[1],
            value: chartData.subject,
        }, {
            name: legendData[2],
            value: chartData.increType,
        },{
            name: legendData[3],
            value: chartData.refreshRate,
        }];
        // 绘制图表
        myChart.setOption(option);

        this.chart1 = myChart;
    }

    drawChart2 = (chartData) => {

        let myChart = echarts.init(document.getElementById('Chart2'));
        const option = cloneDeep(lineAreaChartOptions);

        option.title.text = '';
        option.legend = {  show: false, }

        option.grid = {
            left: '3%',
            right: '4%',
            bottom: 0,
            containLabel: true
        }

        option.xAxis[0].axisTick = {
            show: false,
            alignWithLabel: true,
        }

        option.tooltip.formatter = function (params) {
            return `${utils.formatDate(params[0] && +params[0].axisValue)}: ${params[0] && params[0].value}
                `
        }

        option.xAxis[0].boundaryGap = ['5%', '5%'];
        option.xAxis[0].axisLabel ={
            align: 'center',
            color: '#666666',
            margin: 12,
            formatter: function(value) {
                return value ? utils.formatDate(+value) : null;
            }
        }
        // option.legend.data = ['模型不规范趋势']
        option.xAxis[0].data = chartData && chartData.x ? chartData.x.data : [];
        option.series = [{
            name: '模型不规范趋势',
            type: 'line',
            markLine: {
                precision: 1,
            },
            data: chartData && chartData.y ? chartData.y[0].data : []
        }];

        // 绘制图表
        myChart.setOption(option);
        this._chart2 = myChart;
    }

    drawChart3 = (chartData) => {
        let myChart = echarts.init(document.getElementById('Chart3'));
        const legendData = ['字段名称不规范', '字段类型不规范', '字段描述不规范'];
        const option = cloneDeep(pieChartOptions);
        option.title.text = '';
        option.legend = {
            data: legendData,
            x : 'center',
            y : 'bottom',
        };

        option.series[0].name = '字段不规范原因';
        option.series[0].data = [{
            name: legendData[0],
            value: chartData.name,
        }, {
            name: legendData[1],
            value: chartData.dataType,
        }, {
            name: legendData[2],
            value: chartData.desc,
        }];
        // 绘制图表
        myChart.setOption(option);

        this.chart3 = myChart;
    }

    drawChart4 = (chartData) => {
        let myChart = echarts.init(document.getElementById('Chart4'));
        const option = cloneDeep(lineAreaChartOptions);

        option.title.text = '';
        option.legend = {  show: false, }

        option.grid = {
            left: '3%',
            right: '4%',
            bottom: 0,
            containLabel: true
        }

        option.xAxis[0].axisTick = {
            show: false,
            alignWithLabel: true,
        }

        option.tooltip.formatter = function (params) {
            return `${utils.formatDate(params[0] && +params[0].axisValue )}: ${params[0] && params[0].value}
                `
        }

        option.xAxis[0].boundaryGap = ['5%', '5%'];
        option.xAxis[0].axisLabel ={
            align: 'center',
            color: '#666666',
            margin: 12,
            formatter: function(value) {
                return value ? utils.formatDate(+value) : null;
            }
        }
        option.xAxis[0].data = chartData && chartData.x ? chartData.x.data : [];
        option.series = [{
            name: '字段不规范趋势',
            type: 'line',
            markLine: {
                precision: 1,
            },
            data: chartData && chartData.y ? chartData.y[0].data : []
        }];

        // 绘制图表
        myChart.setOption(option);
        this._chart4 = myChart;
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

        const chartStyle = {
            width: '100%',
            height: '300px',
            paddingBottom: 20,
        }

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
                                                <span className="m-count-content font-black">{data.newTable || 0}</span>
                                            </section>
                                        </Col>
                                        <Col style={flex}>
                                            <section style={countWidth} className="m-count-section">
                                                <span className="m-count-title">今日新增指标</span>
                                                <span className="m-count-content font-red">{data.newColumn || 0}</span>
                                            </section>
                                        </Col>
                                        <Col style={flex}>
                                            <section style={countWidth} className="m-count-section">
                                                <span className="m-count-title">今日不规范模型</span>
                                                <span className="m-count-content font-organge">{data.badTable || 0}</span>
                                            </section>
                                        </Col>
                                        <Col style={flex}>
                                            <section style={countWidth} className="m-count-section">
                                                <span className="m-count-title">今日不规范指标</span>
                                                <span className="m-count-content font-green">{data.badColumn || 0}</span>
                                            </section>
                                        </Col>
                                        <Col style={flex}>
                                            <section style={countWidth} className="m-count-section">
                                                <span className="m-count-title">累计不规范模型</span>
                                                <span className="m-count-content font-gray">{data.sumBadTable || 0}</span>
                                            </section>
                                        </Col>
                                        <Col style={flex}>
                                            <section style={countWidth} className="m-count-section">
                                                <span className="m-count-title">累计不规范指标</span>
                                                <span className="m-count-content font-organge">{data.sumBadColumn || 0}</span>
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
                                    <div id="Chart1" style={chartStyle}></div>
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
                                    <div id="Chart2" style={chartStyle}></div>
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
                                    <div id="Chart3" style={chartStyle}></div>
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
                                    <div id="Chart4" style={chartStyle}></div>
                                </Card>
                            </Col>
                        </Row>
                </div>
            </Resize>
        )
    }
}