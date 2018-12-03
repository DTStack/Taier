import React, { Component } from 'react'
import { Card, Col, Row } from 'antd';
import { cloneDeep } from 'lodash'
import Resize from 'widgets/resize';
import { doubleLineAreaChartOptions } from '../../consts';
import utils from 'utils'

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
    componentDidMount () {
        const data = this.props.chartData || [];

        this.initLineChart(data);
    }

    resize = () => {
        if (this.state.lineChart) this.state.lineChart.resize()
    }
    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps (nextProps) {
        if (this.props.chartData != nextProps.chartData) {
            this.initLineChart(nextProps.chartData)
        }
    }
    initLineChart (chartData) {
        let callCountDate = [];
        let failCountDate = [];
        let times = [];

        for (let i = 0; i < chartData.length; i++) {
            callCountDate.push(chartData[i].callCount)
            failCountDate.push(chartData[i].failRate)
            if (this.props.date) {
                switch (this.props.date) {
                case '1':
                    times.push(utils.formatHours(chartData[i].time));
                    break;
                case '7':
                    times.push(utils.formatDateHours(chartData[i].time));
                    break;
                case '30':
                    times.push(utils.formatDate(chartData[i].time));
                    break;
                }
            }
        }

        let myChart = echarts.init(document.getElementById('CallGraph'));
        const option = cloneDeep(doubleLineAreaChartOptions);
        option.tooltip.formatter = function (params) {
            var relVal = params[0].name;
            for (var i = 0, l = params.length; i < l; i++) {
                let unit = '次'
                if (params[i].seriesName == '失败率') {
                    unit = '%'
                }
                relVal += '<br/><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>' + params[i].seriesName + ' : ' + params[i].value + unit;
            }
            return relVal;
        }
        option.series = [{

            name: '调用次数',
            data: callCountDate,
            type: 'line',
            smooth: true,
            itemStyle: {
                normal: {
                    color: '#1C86EE'
                }
            }
        }, {

            name: '失败率',
            data: failCountDate,
            type: 'line',
            smooth: true,
            yAxisIndex: 1,
            itemStyle: {
                normal: {
                    color: 'rgba(244,67,54,0.9)'
                }
            }
        }];

        option.xAxis[0].data = times;
        console.log(option)

        // 绘制图表
        myChart.setOption(option);
        this.setState({ lineChart: myChart })
    }
    render () {
        return (

            <Card
                noHovering
                className="shadow"
            >
                <Row style={{ width: '100%' }} gutter={130} className="m-count padding-l20 height-101">
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
                    {this.props.userView ? null
                        : (
                            <Col span={6}>
                                <section className="m-count-section margin-t20" style={{ width: 150 }}>
                                    <span className="m-count-title text-left">接口总数</span>
                                    <span className="m-count-content font-black text-left">{this.props.apiNum || '---'}</span>
                                </section>
                            </Col>
                        )}
                </Row>
                <Resize onResize={this.resize.bind(this)}>
                    <article id="CallGraph" style={{ width: '100%', height: '300px' }} />
                </Resize>

            </Card>

        )
    }
}

export default TopCall;
