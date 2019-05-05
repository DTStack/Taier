import React, { Component } from 'react';
import { cloneDeep } from 'lodash';

import Resize from 'widgets/resize';

import {
    lineAreaChartOptions,
    EVALUATE_REPORT_CHART_TYPE
} from '../../../../../../consts';

import API from '../../../../../../api/experiment';

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

const imgBaseUrl = '/public/science/img/evaluate-report';
const btnStyle = {
    width: '132px',
    height: '72px',
    marginTop: '-1px',
    cursor: 'pointer'
}
class CurveChart extends Component {
    state = {
        selectedBtn: 'roc',
        chartData: null
    }

    componentDidMount () {
        this._chart1 = echarts.init(document.getElementById('JS_CurveChart'));
        this.renderChart('roc');
    }

    renderChart = async (chart) => {
        const myChart = this._chart1;
        const { data } = this.props;
        const option = cloneDeep(lineAreaChartOptions);
        option.yAxis[0].nameLocation = 'start';
        option.yAxis[0].nameTextStyle.fontSize = 13;
        option.yAxis[0].nameTextStyle.color = '#666666';
        option.title.textStyle = {
            fontSize: 14,
            color: '#333333'
        }
        const reqParams = {};
        switch (chart) {
            case 'roc': {
                reqParams.type = EVALUATE_REPORT_CHART_TYPE.ROC;
                option.yAxis[0].minInterval = 1;
                option.title.text = 'ROC';
                option.yAxis[0].name = 'AUC值';
                break;
            }
            case 'ks': {
                reqParams.type = EVALUATE_REPORT_CHART_TYPE.K_S;
                option.title.text = 'K-S';
                option.yAxis[0].name = 'KS值';
                break;
            } case 'lift': {
                reqParams.type = EVALUATE_REPORT_CHART_TYPE.LIFT;
                option.title.text = 'Lift';
                option.yAxis[0].name = 'Lift值';
                break;
            } case 'gain': {
                reqParams.type = EVALUATE_REPORT_CHART_TYPE.GAIN;
                option.title.text = 'Gain';
                option.yAxis[0].name = 'Gain值';
                break;
            } case 'pre': {
                reqParams.type = EVALUATE_REPORT_CHART_TYPE.PRECISION_RECALL;
                option.title.text = 'Precision Recall';
                option.yAxis[0].name = 'F1-Score值';
                break;
            }
        }
        if (data) {
            reqParams.taskId = data.id;
            const res = await API.getEvaluateReportChartData(reqParams);
            if (res.code === 1) {
                const chartData = res.data;
                option.xAxis[0].data = chartData.xAxis || [];
                option.series[0] = chartData.xAxis || [];
            }
        }
        // 绘制图表
        myChart.setOption(option);
    }

    resizeChart = () => {
        if (this._chart1) {
            this._chart1.resize();
        }
    }

    onSelected = (e) => {
        const ele = e.target;
        if (ele.tagName && ele.tagName.toLowerCase() === 'img') {
            const key = ele.getAttribute('data-key');
            this.setState({
                selectedBtn: key
            });
            this.renderChart(key);
        }
    }

    renderSwitchButtons = () => {
        const { selectedBtn } = this.state;
        const arr = ['roc', 'ks', 'lift', 'gain', 'pre'];
        const imgs = arr.map(key => {
            const url = `${imgBaseUrl}/${key}-${key === selectedBtn ? 'selected' : 'normal'}.png`;
            const style = {
                ...btnStyle,
                marginBottom: key === selectedBtn ? '1px' : undefined
            }
            return <img style={style} data-key={key} key={key} src={url}/>
        })
        return (
            <div
                style={{
                    width: '132px',
                    display: 'inline-block'
                }}
                onClick={this.onSelected}>
                {imgs}
            </div>
        )
    }

    render () {
        return (
            <div style={{ padding: '30px 20px', height: '100%' }}>
                {this.renderSwitchButtons()}
                <Resize onResize={this.resizeChart}>
                    <div id="JS_CurveChart" style={{
                        display: 'inline-block',
                        float: 'right',
                        height: 'calc(100% - 100px)',
                        width: 'calc(100% - 152px)'
                    }}></div>
                </Resize>
            </div>
        )
    }
}

export default CurveChart
