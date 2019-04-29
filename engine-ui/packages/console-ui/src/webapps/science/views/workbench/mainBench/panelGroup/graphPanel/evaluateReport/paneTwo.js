import React, { Component } from 'react';
import { cloneDeep } from 'lodash';

import Resize from 'widgets/resize';

import {
    lineAreaChartOptions
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
    height: '72px'
}
class CurveChart extends Component {
    state = {
        selectedBtn: '',
        chartData: null
    }

    componentDidMount () {
        this._chart1 = echarts.init(document.getElementById('JS_CurveChart'));
        this.renderChart('roc');
    }

    renderChart = async (chart) => {
        const myChart = this._chart1;
        const option = cloneDeep(lineAreaChartOptions);
        const reqParams = {};
        switch (chart) {
            case 'roc': {
                option.yAxis[0].minInterval = 1;
                option.title.text = 'ROC';
                break;
            }
            case 'ks': {
                option.title.text = 'K-S';
                break;
            } case 'lift': {
                option.title.text = 'Lift';
                break;
            } case 'gain': {
                option.title.text = 'Gain';
                break;
            } case 'pre': {
                option.title.text = 'Precision Recall';
                break;
            }
        }

        const res = await API.getCurveChartData(reqParams);
        if (res.code === 1) {
            const chartData = res.data;
            option.xAxis[0].data = chartData && chartData.x ? chartData.x.data : [];
            option.series[0].data = chartData.y ? chartData.y[0].data : [];
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
        if (e.target.tagName === 'img') {
            const key = e.target.getAttribute('data-key');
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
            return <img style={btnStyle} data-key="roc" key={key} src={url}/>
        })
        return (
            <div onClick={this.onSelected}>
                {imgs}
            </div>
        )
    }

    render () {
        return (
            <div>
                {this.renderSwitchButtons()}
                <Resize onResize={this.resizeChart}>
                    <div id="JS_CurveChart"></div>
                </Resize>
            </div>
        )
    }
}

export default CurveChart
