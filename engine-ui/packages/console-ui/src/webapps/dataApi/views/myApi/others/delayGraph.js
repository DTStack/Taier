import React, { Component } from 'react';
import { Col, Row } from 'antd';
import Resize from 'widgets/resize';
import { cloneDeep } from 'lodash'
import { lineAreaChartOptions } from '../../../consts';
import api from '../../../api/mine';
import utils from 'utils'

const GRAPH_TYPE = {
    COUNT: 'count',
    DEALY: 'delay'
}

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/legend');
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');
class MyApiDelayGraph extends Component {
    state = {
        data: {},
        graphType: GRAPH_TYPE.COUNT
    }
    componentDidMount () {
        this.getInfo();
    }
    componentDidUpdate (prevProps, prevState) {
        const { showRecord = {}, dateType } = this.props;
        const { showRecord: prevShowRecord = {}, dateType: prevDateType } = prevProps;
        if (showRecord.apiId !== prevShowRecord.apiId || dateType !== prevDateType) {
            this.getInfo();
        }
    }
    getInfo () {
        const { showRecord = {}, dateType } = this.props;
        const { apiId } = showRecord;
        if (!apiId) {
            return;
        }
        api.getApiTimeInfo({
            apiId,
            time: dateType
        })
            .then(
                (res) => {
                    if (res) {
                        this.setState({
                            data: res.data || {}
                        }, () => {
                            this.initLineChart();
                        })
                    }
                }
            )
    }
    resize = () => {
        if (this._lineChart) this._lineChart.resize()
    }
    initLineChart () {
        const { infoList = [] } = this.state.data;
        if (!infoList) {
            return;
        }
        const chartData = infoList;
        let callCountDate = [];
        let times = [];
        /**
         * 处理加工图表数据
         */
        for (let i = 0; i < chartData.length; i++) {
            const chart = chartData[i];
            callCountDate.push(chart.executeTime)
            if (this.props.dateType) {
                const time = chart.invokeTime;
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
                }
            }
        }
        let myChart = echarts.init(document.getElementById('MyApiDetailDelayGraph'));
        const option = cloneDeep(lineAreaChartOptions);
        option.title = null;
        option.grid.right = '40px';
        option.grid.left = '30px';
        option.grid.bottom = '10px';
        option.tooltip.formatter = function (params) {
            var relVal = params[0].name;
            for (var i = 0, l = params.length; i < l; i++) {
                let unit = 'ms'
                relVal += '<br/><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' +
                    params[i].color +
                    '"></span>' +
                    params[i].seriesName +
                    ' : ' +
                    params[i].value + unit;
            }
            return relVal;
        }
        option.series = [{
            symbol: 'none',
            name: '耗时(ms)',
            data: callCountDate,
            type: 'line',
            smooth: true,
            itemStyle: {
                normal: {
                    color: '#1C86EE'
                }
            }
        }];
        option.yAxis[0].axisLabel.formatter = null;
        option.yAxis[0].name = '耗时(ms)';
        option.xAxis[0].data = times;
        console.log(option)
        // 绘制图表
        myChart.setOption(option);
        this._lineChart = myChart;
    }
    getDateText () {
        switch (this.props.dateType) {
            case '1':
                return '24小时';
            case '7':
                return '7天';
            case '30':
                return '30天';
            default:
                return '';
        }
    }
    render () {
        const { data } = this.state;
        return (
            <div>
                <Row gutter={130} className="m-count padding-l20 height-callstate-item">
                    <Col span={8}>
                        <section className="m-count-section margin-t20" style={{ width: 150 }}>
                            <span className="m-count-title text-left">{this.getDateText()}平均耗时</span>
                            <span className="m-count-content font-black text-left">{(data && data.avgExecuteTime) || 0}<span style={{ fontSize: 12 }}>次</span></span>
                        </section>
                    </Col>
                </Row>
                <div style={{ paddingRight: '20px' }}>
                    <Resize onResize={this.resize}>
                        <article id="MyApiDetailDelayGraph" style={{ width: '100%', height: '250px' }} />
                    </Resize>
                </div>
            </div>
        )
    }
}
export default MyApiDelayGraph;
