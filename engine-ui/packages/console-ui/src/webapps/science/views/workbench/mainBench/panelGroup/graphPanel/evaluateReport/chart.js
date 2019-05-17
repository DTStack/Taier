import React, { Component } from 'react';
import { cloneDeep, maxBy } from 'lodash';
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
require('echarts/lib/component/markPoint');
require('echarts/lib/component/marker/MarkerModel');
require('echarts/lib/component/marker/MarkPointModel');
require('echarts/lib/component/marker/MarkPointView');
require('echarts/lib/component/marker/markerHelper');
require('echarts/lib/component/marker/MarkerView');

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
            end: 100
        }, {
            type: 'inside',
            realtime: true,
            start: 0,
            end: 100
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
                break;
            }
            case 'ks': {
                reqParams.type = EVALUATE_REPORT_CHART_TYPE.K_S;
                option.title.text = 'K-S';
                option.yAxis[0].name = 'KS值：';
                break;
            } case 'lift': {
                reqParams.type = EVALUATE_REPORT_CHART_TYPE.LIFT;
                option.title.text = 'Lift';
                option.yAxis[0].name = 'Lift值：';
                break;
            } case 'gain': {
                reqParams.type = EVALUATE_REPORT_CHART_TYPE.GAIN;
                option.title.text = 'Gain';
                option.yAxis[0].name = 'Gain值：';
                break;
            } case 'pre': {
                reqParams.type = EVALUATE_REPORT_CHART_TYPE.PRECISION_RECALL;
                option.title.text = 'Precision Recall';
                option.yAxis[0].name = 'F1-Score值：';
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
                switch (chart) {
                    case 'roc': {
                        const regex = /(?<=\[|\()[^,]*/;
                        const range = res.data.series[0].keyList.findIndex(o => o === 'range');
                        const sensitive = res.data.series[0].keyList.findIndex(o => o === 'cumulaitve_percentages_of_positive');
                        const fpr = res.data.series[0].keyList.findIndex(o => o === 'fpr');
                        option.tooltip.formatter = (params, ticket, callback) => {
                            const data = params.data.colList;
                            const threshold = regex.exec(data[range]) ? regex.exec(data[range])[0] : '';
                            return (
                                `threshold: ${threshold} <br />
                                sensitive: ${data[sensitive]} <br />
                                fpr: ${data[fpr]} <br />
                                `
                            )
                        }
                        break;
                    }
                    case 'ks': {
                        const regex = /(?<=\[|\()[^,]*/;
                        const ks = res.data.series[0].keyList.findIndex(o => o === 'ks');
                        const range = res.data.series[0].keyList.findIndex(o => o === 'range');
                        const cumulaitvePercentagesOfPositive = res.data.series[0].keyList.findIndex(o => o === 'cumulaitve_percentages_of_positive');
                        const cumulaitvePercentagesOfNegative = res.data.series[0].keyList.findIndex(o => o === 'cumulaitve_percentages_of_negative');
                        option.tooltip.formatter = (params, ticket, callback) => {
                            const data = params.data.colList;
                            const threshold = regex.exec(data[range]) ? regex.exec(data[range])[0] : '';
                            return (
                                `
                                KS: ${data[ks]} <br />
                                threshold: ${threshold} <br />
                                sample ratio: ${params.data.value} <br />
                                cumulaitve_percentages_of_positive: ${data[cumulaitvePercentagesOfPositive]} <br />
                                cumulaitve_percentages_of_negative: ${data[cumulaitvePercentagesOfNegative]} <br />
                                `
                            )
                        }
                        break;
                    } case 'lift': {
                        option.xAxis[0].data = option.xAxis[0].data.reverse(); // x 轴反转
                        const regex = /(?<=\[|\()[^,]*/;
                        const range = res.data.series[0].keyList.findIndex(o => o === 'range');
                        const lift = res.data.series[0].keyList.findIndex(o => o === 'lift');
                        option.tooltip.formatter = (params, ticket, callback) => {
                            const data = params.data.colList;
                            const threshold = regex.exec(data[range]) ? regex.exec(data[range])[0] : '';
                            return (
                                `
                                threshold: ${threshold} <br />
                                lift: ${data[lift]} <br />
                                `
                            )
                        }
                        break;
                    } case 'gain': {
                        const recall = res.data.series[0].keyList.findIndex(o => o === 'recall');
                        const threshold = res.data.series[0].keyList.findIndex(o => o === 'threshold');
                        const precision = res.data.series[0].keyList.findIndex(o => o === 'precision');
                        const cumulaitvePercentagesOfPositive = res.data.series[0].keyList.findIndex(o => o === 'cumulaitve_percentages_of_positive');
                        const cumulaitvePercentagesOfNegative = res.data.series[0].keyList.findIndex(o => o === 'cumulaitve_percentages_of_negative');
                        option.tooltip.formatter = (params, ticket, callback) => {
                            const data = params.data.colList;
                            return (
                                `
                                recall: ${data[recall]} <br />
                                threshold: ${data[threshold]} <br />
                                precision: ${data[precision]} <br />
                                cumulaitve_percentages_of_positive: ${data[cumulaitvePercentagesOfPositive]} <br />
                                cumulaitve_percentages_of_negative: ${data[cumulaitvePercentagesOfNegative]} <br />
                                `
                            )
                        }
                        break;
                        ;
                    } case 'pre': {
                        const regex = /(?<=\[|\()[^,]*/;
                        const range = res.data.series[0].keyList.findIndex(o => o === 'range');
                        const f1Score = res.data.series[0].keyList.findIndex(o => o === 'f1_score');
                        const precision = res.data.series[0].keyList.findIndex(o => o === 'precision');
                        const recall = res.data.series[0].keyList.findIndex(o => o === 'recall');
                        const maxData = this.findMax(option.series[0].data, f1Score);
                        option.series = option.series.map((item) => {
                            item.markPoint = {
                                data: [{
                                    xAxis: maxData.value,
                                    yAxis: maxData.name,
                                    value: maxData.colList[precision]
                                }]
                            }
                            return item;
                        })
                        option.tooltip.formatter = (params, ticket, callback) => {
                            const data = params.data.colList;
                            const threshold = regex.exec(data[range]) ? regex.exec(data[range])[0] : '';
                            return (
                                `
                                threshold: ${threshold} <br />
                                f1Score: ${data[f1Score]} <br />
                                precision: ${data[precision]} <br />
                                recall: ${data[recall]} <br />
                                `
                            )
                        }
                        break;
                    }
                }
            }
        }
        // 绘制图表
        console.log('option:', option);
        myChart.setOption(option, true);
        myChart.hideLoading()
    }
    findMax = (arr, index) => {
        let maxData = maxBy(arr, (o) => {
            return o.colList[index]
        })
        return maxData;
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
