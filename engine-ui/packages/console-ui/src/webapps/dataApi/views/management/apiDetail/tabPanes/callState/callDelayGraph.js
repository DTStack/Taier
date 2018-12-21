import React, { Component } from 'react';
import { Col, Row } from 'antd';

import Resize from 'widgets/resize';
import { lineAreaChartOptions } from '../../../../../consts';
import api from '../../../../../api/apiManage'
import { cloneDeep } from 'lodash'
import utils from 'utils'

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/legend');
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

class ManageCallDelayGraph extends Component {
    state = {
        infoList: [],
        apiId: '',
        dateType: '',
        avgDelay: ''
    }
    componentDidMount () {
        this.getApiCallTime();
    }
    getApiCallTime () {
        let apiId = this.props.apiId;
        let time = this.props.dateType;

        if (!apiId || !time) {
            return;
        }
        api.getApiCallTime({ apiId, time })
            .then(
                (res) => {
                    if (res) {
                        this.setState({
                            infoList: res.data.infoList,
                            avgDelay: res.data.avgExecuteTime
                        }, () => {
                            this.initLineChart();
                        })
                    }
                }
            )
    }
    resize = () => {
        if (this.state.lineChart) this.state.lineChart.resize()
    }
    initLineChart () {
        let chartData = this.state.infoList
        let callCountDate = [];
        let times = [];
        for (let i = 0; i < chartData.length; i++) {
            const chart = chartData[i];
            const time = chart.invokeTime;
            callCountDate.push(chart.executeTime)
            switch (this.props.dateType) {
                case '1':
                    times.push(utils.formatHours(time));
                    break;
                case '7':
                    times.push(utils.formatDateHours(time));
                    break;
                case '30':
                    times.push(utils.formatDate(time));
                    break;
                case '-1':
                    times.push(utils.formatDate(time));
                    break;
            }
        }
        let myChart = echarts.init(document.getElementById('callDelayGraph'));
        const option = cloneDeep(lineAreaChartOptions);
        option.title = null;
        option.tooltip.formatter = function (params) {
            var relVal = params[0].name;
            for (var i = 0, l = params.length; i < l; i++) {
                let unit = 's'
                relVal += '<br/><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>' + params[i].seriesName + ' : ' + params[i].value + unit;
            }
            return relVal;
        }
        option.series = [{
            symbol: 'none',
            name: '耗时(s)',
            data: callCountDate,
            type: 'line',
            smooth: true,
            itemStyle: {
                normal: {
                    color: '#1C86EE'
                }
            }
        }];
        option.xAxis[0].data = times;

        option.yAxis[0].axisLabel.formatter = null;
        option.yAxis[0].name = '耗时(s)';

        option.grid.left = 40
        option.grid.bottom = 10
        option.grid.top = 40;

        console.log(option)
        // 绘制图表
        myChart.setOption(option);
        this.setState({ lineChart: myChart })
    }
    getDateText () {
        switch (this.props.dateType) {
            case '1':
                return '24小时';
            case '7':
                return '7天';
            case '30':
                return '30天';
            case '-1':
                return '历史以来';
            default:
                return '';
        }
    }
    render () {
        const { avgDelay } = this.state;
        return (
            <div>
                <Row gutter={100} style={{ paddingLeft: '30px' }} className="m-count padding-l20 height-callstate-item">
                    <Col span={6}>
                        <section className="m-count-section margin-t20" style={{ width: 150 }}>
                            <span className="m-count-title text-left">{this.getDateText()}平均耗时</span>
                            <span className="m-count-content font-black text-left">{avgDelay || 0}<span style={{ fontSize: 12 }}>s</span></span>
                        </section>
                    </Col>
                </Row>
                <Resize onResize={this.resize}>
                    <article id="callDelayGraph" style={{ width: '100%', height: '250px' }} />
                </Resize>
            </div>
        )
    }
}
export default ManageCallDelayGraph;
