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
require('echarts/lib/chart/bar');

// 引入提示框和标题组件
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');
require('echarts/lib/component/dataZoom');
require('echarts/lib/component/toolbox');

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
    componentDidUpdate (prevProps, prevState) {
        if (this.props.visible && !prevProps.visible) {
            this.renderChart(this.state.selectedBtn);
        }
    }
    componentWillUnmount () {
        this._chart1 = null;
        this.setState({
            selectedBtn: 'roc',
            chartData: null
        })
    }

    renderChart = async (chart) => {
        const myChart = this._chart1;
        const { data } = this.props;
        const option = cloneDeep(lineAreaChartOptions);
        const reqParams = {};
        option.color = ['#2491F7'];
        option.grid = {
            left: 40,
            right: 20,
            bottom: 70
        }
        option.toolbox = {
            show: true,
            feature: {
                magicType: { type: ['line', 'bar'] },
                saveAsImage: {}
            }
        }
        option.tooltip = {
            trigger: 'item',
            borderColor: '#2491F7',
            backgroundColor: '#2491F7',
            axisPointer: {
                type: 'cross',
                crossStyle: {
                    color: '#2491F7'
                },
                label: {
                    backgroundColor: '#6a7985'
                }
            }
        };
        option.dataZoom = [{
            show: true,
            realtime: true,
            start: 0,
            end: 10
        }, {
            type: 'inside',
            realtime: true,
            start: 65,
            end: 85
        }];
        option.yAxis = [{
            type: 'value',
            splitNumber: 5,
            nameLocation: 'end',
            nameTextStyle: {
                fontSize: 13,
                color: '#666666',
                align: 'center'
            },
            axisLine: {
                lineStyle: {
                    color: '#DDDDDD'
                }
            },
            axisTick: {
                show: false
            },
            splitLine: {
                lineStyle: {
                    type: 'dashed'
                }
            },
            axisLabel: {
                color: '#666666',
                fontSize: 13
            }
        }]

        option.title.textStyle = {
            fontSize: 14,
            color: '#333333',
            textAlign: 'left'
        }
        option.title.padding = [5, 10, 5, 0];

        switch (chart) {
            case 'roc': {
                reqParams.type = EVALUATE_REPORT_CHART_TYPE.ROC;
                option.title.text = 'ROC';
                option.yAxis[0].name = 'AUC值：';
                option.tooltip.formatter = (params, ticket, callback) => {
                    return (parseFloat(params.value) * 100).toFixed(2) + '%';
                }
                break;
            }
            case 'ks': {
                reqParams.type = EVALUATE_REPORT_CHART_TYPE.K_S;
                option.title.text = 'K-S';
                option.yAxis[0].name = 'KS值：';
                option.tooltip.formatter = (params, ticket, callback) => {
                    return (parseFloat(params.value) * 100).toFixed(2) + '%';
                }
                break;
            } case 'lift': {
                reqParams.type = EVALUATE_REPORT_CHART_TYPE.LIFT;
                option.title.text = 'Lift';
                option.yAxis[0].name = 'Lift值：';
                option.tooltip.formatter = (params, ticket, callback) => {
                    return (params.value * 1).toFixed(2);
                }
                break;
            } case 'gain': {
                reqParams.type = EVALUATE_REPORT_CHART_TYPE.GAIN;
                option.title.text = 'Gain';
                option.yAxis[0].name = 'Gain值：';
                option.tooltip.formatter = (params, ticket, callback) => {
                    return params.value;
                }
                break;
            } case 'pre': {
                reqParams.type = EVALUATE_REPORT_CHART_TYPE.PRECISION_RECALL;
                option.title.text = 'Precision Recall';
                option.yAxis[0].name = 'F1-Score值：';
                option.tooltip.formatter = (params, ticket, callback) => {
                    return (parseFloat(params.value) * 100).toFixed(2) + '%';
                }
                break;
            }
        }
        if (data) {
            reqParams.taskId = data.id;
            myChart.showLoading()
            const res = await API.getEvaluateReportChartData(reqParams);
            if (res.code === 1) {
                const chartData = res.data;
                option.xAxis[0].data = chartData.xAxis || [];
                const seriesData = chartData.series || [{ type: 'line' }];
                option.series = seriesData.map((item) => {
                    item.type = 'line';
                    item.areaStyle = {
                        normal: {
                            color: 'rgba(36,145,247,0.20)'
                        }
                    }
                    return item;
                });
            }
        }
        // 绘制图表
        myChart.setOption(option, true);
        myChart.hideLoading()
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
                onClick={this.onSelected}>
                {imgs}
            </div>
        )
    }

    render () {
        return (
            <div style={{
                padding: '30px 20px',
                height: '100%',
                display: 'grid',
                gridTemplateColumns: '132px auto',
                gridColumnGap: '10px'
            }}>
                {this.renderSwitchButtons()}
                <Resize onResize={this.resizeChart}>
                    <div id="JS_CurveChart" style={{
                        height: '90%',
                        width: '100%'
                    }}></div>
                </Resize>
            </div>
        )
    }
}

export default CurveChart
