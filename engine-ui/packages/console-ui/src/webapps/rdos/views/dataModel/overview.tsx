import * as React from 'react';
import { hashHistory } from 'react-router';

import { cloneDeep } from 'lodash';
import {
    Row, Col, Card
} from 'antd';
import { connect } from 'react-redux';

import utils from 'utils';
import Resize from 'widgets/resize';

import Api from '../../api/dataModel';
import {
    lineAreaChartOptions,
    pieChartOptions
} from '../../comm/const';

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
require('echarts/lib/chart/pie');
// 引入提示框和标题组件
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

class Overview extends React.Component<any, any> {
    state: any = {
        data: '',
        tableRate: '',
        columnRate: '',
        tableTrend: '',
        columnTrend: ''
    }
    _chart1: any;
    _chart2: any;
    _chart3: any;
    _chart4: any;
    componentDidMount () {
        this.statisticTotal();
        this.statisticTableRate();
        this.statisticColumnRate();
        this.statisticTableTrend();
        this.statisticColumnTrend();
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.componentDidMount();
        }
    }

    statisticTotal = () => {
        Api.statisticTotal().then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    data: res.data
                })
            }
        })
    }

    statisticTableRate = () => {
        Api.statisticTableRate().then((res: any) => {
            if (res.code === 1) {
                this.drawChart1(res.data)
            }
        })
    }

    statisticColumnRate = () => {
        Api.statisticColumnRate().then((res: any) => {
            if (res.code === 1) {
                this.drawChart3(res.data)
            }
        })
    }

    statisticTableTrend = () => {
        Api.statisticTableTrend().then((res: any) => {
            if (res.code === 1) {
                this.drawChart2(res.data)
            }
        })
    }

    statisticColumnTrend = () => {
        Api.statisticColumnTrend().then((res: any) => {
            if (res.code === 1) {
                this.drawChart4(res.data)
            }
        })
    }

    drawChart1 = (chartData: any) => {
        let myChart = echarts.init(document.getElementById('Chart1'));
        const legendData: any = ['分层不合理', '主题域不合理', '增量不合理', '刷新频率不合理'];
        const option = cloneDeep(pieChartOptions);
        option.title.text = '';
        option.legend = {
            data: legendData,
            x: 'center',
            y: 'bottom'
        };

        option.series[0].name = '模型不规范原因';
        option.series[0].data = [{
            name: legendData[0],
            value: chartData.grade
        }, {
            name: legendData[1],
            value: chartData.subject
        }, {
            name: legendData[2],
            value: chartData.increType
        }, {
            name: legendData[3],
            value: chartData.refreshRate
        }];
        // 绘制图表
        myChart.setOption(option);

        this._chart1 = myChart;
    }

    drawChart2 = (chartData: any) => {
        let myChart = echarts.init(document.getElementById('Chart2'));
        const option = cloneDeep(lineAreaChartOptions);

        option.title.text = '';
        option.legend = { show: false }

        option.grid = {
            left: '3%',
            right: '4%',
            bottom: 0,
            containLabel: true
        }

        option.xAxis[0].axisTick = {
            show: true,
            alignWithLabel: true
        }

        option.tooltip.formatter = function (params: any) {
            return `${utils.formatDate(params[0] && +params[0].axisValue)} <br> 数量：${params[0] && params[0].value}
                `
        }

        option.xAxis[0].boundaryGap = ['5%', '5%'];
        option.xAxis[0].axisLabel = {
            align: 'center',
            color: '#666666',
            margin: 12,
            formatter: function (value: any) {
                return value ? utils.formatDate(+value) : null;
            }
        }
        // option.legend.data = ['模型不规范趋势']
        option.xAxis[0].data = chartData && chartData.x ? chartData.x.data : [];
        option.yAxis[0].minInterval = 1;

        option.series = [{
            name: '模型不规范趋势',
            type: 'line',
            markLine: {
                precision: 1
            },
            data: chartData && chartData.y ? chartData.y[0].data : []
        }];

        // 绘制图表
        myChart.setOption(option);
        this._chart2 = myChart;
    }

    drawChart3 = (chartData: any) => {
        let myChart = echarts.init(document.getElementById('Chart3'));
        const legendData: any = ['字段名称不规范', '字段类型不规范', '字段描述不规范'];
        const option = cloneDeep(pieChartOptions);
        option.title.text = '';
        option.legend = {
            data: legendData,
            x: 'center',
            y: 'bottom'
        };

        option.series[0].name = '字段不规范原因';
        option.series[0].data = [{
            name: legendData[0],
            value: chartData.name
        }, {
            name: legendData[1],
            value: chartData.dataType
        }, {
            name: legendData[2],
            value: chartData.desc
        }];
        // 绘制图表
        myChart.setOption(option);

        this._chart3 = myChart;
    }

    drawChart4 = (chartData: any) => {
        let myChart = echarts.init(document.getElementById('Chart4'));
        const option = cloneDeep(lineAreaChartOptions);

        option.title.text = '';
        option.legend = { show: false }

        option.grid = {
            left: '3%',
            right: '4%',
            bottom: 0,
            containLabel: true
        }

        option.xAxis[0].axisTick = {
            show: true,
            alignWithLabel: true
        }

        option.tooltip.formatter = function (params: any) {
            return `${utils.formatDate(params[0] && +params[0].axisValue)} <br> 数量：${params[0] && params[0].value}
                `
        }

        option.xAxis[0].boundaryGap = ['5%', '5%'];
        option.xAxis[0].axisLabel = {
            align: 'center',
            color: '#666666',
            margin: 12,
            formatter: function (value: any) {
                return value ? utils.formatDate(+value) : null;
            }
        }
        option.xAxis[0].data = chartData && chartData.x ? chartData.x.data : [];
        option.yAxis[0].minInterval = 1;
        option.series = [{
            name: '字段不规范趋势',
            type: 'line',
            markLine: {
                precision: 1
            },
            data: chartData && chartData.y ? chartData.y[0].data : []
        }];

        // 绘制图表
        myChart.setOption(option);
        this._chart4 = myChart;
    }

    resizeChart = () => {
        if (this._chart1) {
            this._chart1.resize()
            this._chart2.resize()
            this._chart3.resize()
            this._chart4.resize()
        }
    }

    getTodayRange = () => {
        const today0 = new Date(new Date().toLocaleDateString()).getTime();// 当天0点
        const today24 = new Date(new Date().toLocaleDateString()).getTime() + 24 * 60 * 60 * 1000 - 1;// 当天24点
        return { today0, today24 }
    }

    linkToCheckPage = (type: any) => {
        const { today0, today24 } = this.getTodayRange();
        const pathName = '/data-model/check';
        const triggerType1 = '1,2,3,4';
        const triggerType2 = '1,2,3';

        let src: any;
        switch (type) {
            case 'todayAddModel':
                src = `${pathName}?currentTab=1&startTime1=${today0}&endTime1=${today24}`;
                break;
            case 'todayAddPoint':
                src = `${pathName}?currentTab=2&startTime2=${today0}&endTime2=${today24}`
                break;
            case 'todayNonstandardModel':
                src = `${pathName}?currentTab=1&startTime1=${today0}&endTime1=${today24}&triggerType1=${triggerType1}`
                break;
            case 'todayNonstandardPoint':
                src = `${pathName}?currentTab=2&startTime2=${today0}&endTime2=${today24}&triggerType2=${triggerType2}`
                break;
            case 'accumulativeNonstandardModel':
                src = `${pathName}?currentTab=1&triggerType1=${triggerType1}`
                break;
            default:
                src = `${pathName}?currentTab=2&triggerType2=${triggerType2}`
                break;
        }
        hashHistory.push(src);
    }

    render () {
        const { data } = this.state;
        const flex: any = {
            flexGrow: 1,
            flex: 1
        };

        const chartStyle: any = {
            width: '100%',
            height: '300px',
            paddingBottom: 20
        }

        const countWidth: any = {
            width: '100px'
        }
        return (
            <Resize onResize={this.resizeChart}>
                <div className="data-model-overview" style={{ background: '#f2f7fa' }}>
                    <Row style={{ marginTop: '10px' }}>
                        <h1 className="box-title box-title-bolder">
                                模型汇总信息
                        </h1>
                        <div className="box-4 m-card m-card-small">
                            <Card
                                noHovering
                                bordered={false}
                                loading={false}
                                title="今日模型建立情况"
                            >
                                <Row className="m-count" style={{ display: 'flex' }}>
                                    <Col style={flex}>
                                        <section style={countWidth} className="m-count-section">
                                            <span className="m-count-title">今日新增模型</span>
                                            <span className="m-count-content font-black" onClick={() => { this.linkToCheckPage('todayAddModel') }}>{data.todayNewTable || 0}</span>
                                        </section>
                                    </Col>
                                    <Col style={flex}>
                                        <section style={countWidth} className="m-count-section">
                                            <span className="m-count-title">今日新增指标</span>
                                            <span className="m-count-content font-red" onClick={() => { this.linkToCheckPage('todayAddPoint') }}>{data.todayNewColumn || 0}</span>
                                        </section>
                                    </Col>
                                    <Col style={flex}>
                                        <section style={countWidth} className="m-count-section">
                                            <span className="m-count-title">今日不规范模型</span>
                                            <span className="m-count-content font-organge" onClick={() => { this.linkToCheckPage('todayNonstandardModel') }}>{data.todayBadTable || 0}</span>
                                        </section>
                                    </Col>
                                    <Col style={flex}>
                                        <section style={countWidth} className="m-count-section">
                                            <span className="m-count-title">今日不规范指标</span>
                                            <span className="m-count-content font-green" onClick={() => { this.linkToCheckPage('todayNonstandardPoint') }}>{data.todayBadColumn || 0}</span>
                                        </section>
                                    </Col>
                                    <Col style={flex}>
                                        <section style={countWidth} className="m-count-section">
                                            <span className="m-count-title" >累计不规范模型</span>
                                            <span className="m-count-content font-gray" onClick={() => { this.linkToCheckPage('accumulativeNonstandardModel') }}>{data.sumBadTable || 0}</span>
                                        </section>
                                    </Col>
                                    <Col style={flex}>
                                        <section style={countWidth} className="m-count-section">
                                            <span className="m-count-title" >累计不规范指标</span>
                                            <span className="m-count-content font-organge" onClick={() => { this.linkToCheckPage('accumulativeNonstandardPoint') }}>{data.sumBadColumn || 0}</span>
                                        </section>
                                    </Col>
                                </Row>
                            </Card>
                        </div>
                    </Row>
                    <Row className="box-card" style={{ marginTop: '20px' }}>
                        <Col span={12} className="m-card m-card-small" style={{ paddingRight: '10px' }}>
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
                        <Col span={12} className="m-card m-card-small" style={{ paddingLeft: '10px' }}>
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
                        <Col span={12} className="m-card m-card-small" style={{ paddingRight: '10px' }}>
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
                        <Col span={12} className="m-card m-card-small" style={{ paddingLeft: '10px' }}>
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

export default connect((state: any) => ({
    project: state.project
}), null)(Overview);
