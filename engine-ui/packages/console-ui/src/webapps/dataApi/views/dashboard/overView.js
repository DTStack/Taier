import React, { Component } from 'react'
import { Card, Col, Row, Table } from 'antd';
import { cloneDeep } from "lodash"
import Resize from 'widgets/resize';
import { doubleLineAreaChartOptions } from '../../consts';
import utils from "utils"

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/legend');
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

class TopCall extends Component {
    state = {

    }
    componentDidMount() {
        const data = this.props.chartData || [];

        this.initLineChart(data);
    }

    resize = () => {
        if (this.state.lineChart) this.state.lineChart.resize()
    }
    componentWillReceiveProps(nextProps) {
        if (this.props.chartData != nextProps.chartData) {
            this.initLineChart(nextProps.chartData)
        }
    }
    initLineChart(chartData) {
        const date = this.props.date;
        const mini=this.props.mini;
        let callCountDate = [];
        let failCountDate = [];
        let times = [];
        const dateDic={
            "1":{
                interval:mini?3600*1000*2:3600  * 1000,
                times:[new Date().getTime() - 3600 * 24 * 1000, new Date().getTime()]

            },
            "7":{
                interval:3600  * 1000*24,
                times:[new Date().getTime() - 3600 * 24 * 1000*7, new Date().getTime()]
            },
            "30":{
                interval:3600 * 24 * 1000*3,
                times:[new Date().getTime() - 3600 * 24 * 1000*30, new Date().getTime()]
            },
        }
        
        times = dateDic[date].times;
        for (let i = 0; i < chartData.length; i++) {
            callCountDate.push([chartData[i].time, chartData[i].callCount])
            failCountDate.push([chartData[i].time, chartData[i].failRate * 100])
            // if (this.props.date) {
            //     switch (this.props.date) {
            //         case "1":
            //             times.push(utils.formatHours(chartData[i].time));
            //             break;
            //         case "7":
            //             times.push(utils.formatDateHours(chartData[i].time));
            //             break;
            //         case "30":
            //             times.push(utils.formatDate(chartData[i].time));
            //             break;
            //     }
            // }
            
        }

        let myChart = echarts.init(document.getElementById('CallGraph'));
        const option = cloneDeep(doubleLineAreaChartOptions);
        option.xAxis.type = "value"
        option.series = [{

            name: "调用次数",
            data: callCountDate,
            type: 'line',
            smooth: true,
            itemStyle: {
                normal: {
                    color: '#1C86EE'
                }
            }
        }, {

            name: "失败率",
            data: failCountDate,
            type: 'line',
            smooth: true,
            yAxisIndex: 1,
            itemStyle: {
                normal: {
                    color: '#EE0000'
                }
            },
        }];

        option.xAxis.min = times[0];
        option.xAxis.max = times[times.length - 1];
        option.xAxis.interval = dateDic[date].interval;
        option.tooltip.formatter = (function () {
            
            return function (params) {
                
                let text = "";
                let axisValue = "";
                for (let i in params) {
                    axisValue = params[i].axisValue;
                    let name = params[i].seriesName;
                    let value = params[i].value[params[i].value.length - 1];
                    let marker = params[i].marker
                    text += `${marker}${name}:${value}<br/>`
                }
                if (date) {
                    switch (date) {
                        case "1":
                            axisValue = utils.formatHours(axisValue);
                            break;
                        case "7":
                            axisValue = utils.formatDateHours(axisValue);
                            break;
                        case "30":
                            axisValue = utils.formatDate(axisValue);
                            break;
                    }
                }
                return axisValue + "<br/>" + text;
            }
        }.bind(this))()

        
        option.xAxis.axisLabel.formatter = (function () {
            const date = this.props.date;
            return function (value, index) {
                if (date) {
                    switch (date) {
                        case "1":
                            return utils.formatHours(value);
                            break;
                        case "7":
                            return utils.formatDateHours(value);
                            break;
                        case "30":
                            return utils.formatDate(value);
                            break;
                    }
                }
            }

        }.bind(this))()
        console.log(option)

        // 绘制图表
        myChart.setOption(option);
        this.setState({ lineChart: myChart })
    }
    render() {
        return (

            <Card
                noHovering
            >
                <Row gutter={130} className="m-count padding-l20 height-101">
                    <Col span={6}>
                        <section className="m-count-section margin-t20" style={{ width: 150 }}>
                            <span className="m-count-title text-left">累计调用</span>
                            <span className="m-count-content font-black text-left">{this.props.callCount || 0}<span style={{ fontSize: 12 }}>次</span></span>
                        </section>
                    </Col>
                    <Col span={4}>
                        <section className="m-count-section margin-t20" style={{ width: 100 }}>
                            <span className="m-count-title text-left">失败率</span>
                            <span className="m-count-content font-red text-left">{this.props.failPercent || 0}<span style={{ fontSize: 12 }}>%</span></span>
                        </section>
                    </Col>
                    <Col span={6}>
                        <section className="m-count-section margin-t20" style={{ width: 150 }}>
                            <span className="m-count-title text-left">TOP调用接口</span>
                            <span className="m-count-content font-black text-left">{this.props.topCallFunc || '---'}</span>
                        </section>
                    </Col>
                </Row>
                <Resize onResize={this.resize.bind(this)}>
                    <article id="CallGraph" style={{ width: '100%', height: '300px' }} />
                </Resize>

            </Card>


        )
    }
}

export default TopCall;