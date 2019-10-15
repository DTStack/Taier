import * as React from 'react'
import { Spin, Icon, Tooltip } from 'antd';
import utils from 'utils';
import { cloneDeep, isArray } from 'lodash';

import Resize from 'widgets/resize';
import FullScreen from 'widgets/fullscreen'

import { lineAreaChartOptions, TIME_TYPE, SOURCE_INPUT_BPS_UNIT_TYPE } from '../../../../../../comm/const'

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/legend');
require('echarts/lib/component/legendScroll');
require('echarts/lib/component/tooltip');

function haveData (lineData: any = {}) {
    const { y = [] } = lineData;

    let haveData = false;
    y.forEach((item: any) => {
        if (item && item.length) {
            haveData = true;
        }
    })
    if (!haveData) {
        return false;
    }
    return true;
}
function isExchangeData (data: any = [], unitType: any) {
    let exChangeArr: any = [];
    switch (unitType) {
        case SOURCE_INPUT_BPS_UNIT_TYPE.BPS: {
            exChangeArr = data;
            break;
        }
        case SOURCE_INPUT_BPS_UNIT_TYPE.KBPS: {
            exChangeArr = data.map((item: any) => isArray(data) ? [data[0], Number((item / 1024).toFixed(6))] : Number((item / 1024).toFixed(6)));
            break;
        }
        case SOURCE_INPUT_BPS_UNIT_TYPE.MBPS: {
            exChangeArr = data.map((item: any) => isArray(data) ? [data[0], Number((item / Math.pow(1024, 2)).toFixed(6))] : Number((item / Math.pow(1024, 2)).toFixed(6)));
            break;
        }
        case SOURCE_INPUT_BPS_UNIT_TYPE.GBPS: {
            exChangeArr = data.map((item: any) => isArray(data) ? [data[0], Number((item / Math.pow(1024, 3)).toFixed(6))] : Number((item / Math.pow(1024, 3)).toFixed(6)));
            break;
        }
        case SOURCE_INPUT_BPS_UNIT_TYPE.TBPS: {
            exChangeArr = data.map((item: any) => isArray(data) ? [data[0], Number((item / Math.pow(1024, 4)).toFixed(6))] : Number((item / Math.pow(1024, 4)).toFixed(6)));
            break;
        }
        default: {
            return exChangeArr
        }
    }
    return exChangeArr
}
class AlarmBaseGraphBox extends React.Component<any, any> {
    state: any = {
        key: '' + Math.random()
    }

    render () {
        const { key } = this.state;
        const { title, lineData, desc } = this.props;
        const { loading } = lineData;
        const haveLineData = haveData(lineData);
        return (
            <div className="basegraph-size">
                <div id={key} className="alarm-basegraph-box">
                    <header>
                        <span className="title">{title}</span>
                        {desc && (
                            <Tooltip
                                title={desc}
                                overlayClassName="big-tooltip"
                                getPopupContainer={(triggerNode: any) => {
                                    return triggerNode;
                                }}
                                placement="bottomLeft"
                                arrowPointAtCenter={true}
                            >
                                <Icon style={{ marginLeft: '8px' }} type="question-circle-o" />
                            </Tooltip>
                        )}

                        {!loading && haveLineData ? <FullScreen
                            target={key}
                            fullIcon={<Icon className="alt" type="arrows-alt" />}
                            exitFullIcon={<Icon className="alt" type="shrink" />}
                            isShowTitle={false} /> : null}
                    </header>
                    {loading
                        ? <div className="loading-box">
                            <Spin className="loading" />
                        </div>
                        : <div className="graph-content">
                            {haveLineData ? <AlarmBaseGraph {...this.props} /> : <p className="no-data-text">暂无数据</p>}
                        </div>
                    }
                </div>
            </div>
        )
    }
}

class AlarmBaseGraph extends React.Component<any, any> {
    _dom: any;
    state: any = {
        lineChart: null
    }

    componentDidMount () {
        this.initGraph();
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        if (this.props.lineData != nextProps.lineData ||
            this.props.time != nextProps.time
        ) {
            this.initGraph(nextProps.lineData, nextProps.time)
        }
    }
    exchangeDate (date: any, time: any, joinLine: any) {
        switch (time) {
            case TIME_TYPE.M10:
            case TIME_TYPE.H1: {
                return utils.formatMinute(parseInt(date));
            }
            case TIME_TYPE.H6:
            case TIME_TYPE.D1: {
                return utils.formatHours(parseInt(date));
            }

            case TIME_TYPE.W1: {
                if (joinLine) {
                    return utils.formatDayHours(parseInt(date)).split(' ').join('\n')
                }
                return utils.formatDayHours(parseInt(date));
            }
            default: {
                return utils.formatHours(parseInt(date));
            }
        }
    }
    initGraph (lineData?: any, time?: any) {
        lineData = lineData || this.props.lineData;
        time = time || this.props.time;
        if (!lineData) {
            return;
        }
        let loading = lineData.loading
        if (loading) {
            return;
        }
        const { x, y = [], legend, color, unit, metricsType } = lineData;
        /**
         * 先检验是不是有数据，没数据就不渲染了
         */
        if (!haveData(lineData)) {
            return;
        }
        /**
         * 初始化
         */
        let myChart = echarts.init(this._dom);
        let options = cloneDeep(lineAreaChartOptions);
        /**
         * 隐藏标题
         */
        options.title.show = false;
        /**
         * 设置纵坐标名称
         */
        if (unit == 's') {
            options.yAxis[0].name = '';
            options.yAxis[0].axisLabel.formatter = (value: any) => {
                return utils.formatTime(value);
            }
            options.yAxis[0].axisPointer = {
                label: {
                    formatter: (params: any) => {
                        return utils.formatTime(params.value);
                    }
                }
            }
        } else {
            options.yAxis[0].name = unit;
        }
        /**
         * 设置横坐标数值
         */
        options.xAxis[0].data = x
        options.xAxis[0].axisLabel = {
            ...options.xAxis[0].axisLabel,
            formatter: (value: any) => {
                return this.exchangeDate(value, time, true)
            }
        }
        options.xAxis[0].axisPointer = {
            label: {
                formatter: (params: any) => {
                    return utils.formatDateTime(parseInt(params.value))
                }
            }
        }
        /**
         * 设置小图标
         */
        options.legend.type = 'scroll';
        options.legend.data = legend.map((item: any) => {
            return {
                name: item,
                icon: 'circle'
            }
        });
        options.legend.bottom = '12px'
        options.legend.left = '10px';
        options.legend.itemWidth = 6
        options.legend.itemHeight = 6
        // options.legend.borderRadius="50%";
        /**
         * 画图区域的定位
         */
        options.grid.bottom = '45px';
        options.grid.left = '20px';
        options.grid.right = '40px';
        options.grid.top = '50px';
        options.grid.containLabel = true
        /**
         * tooltip
         */
        options.tooltip.formatter = (params: any[]) => {
            return `${params[0].axisValueLabel}</br>` + params.map((param) => {
                return `${param.marker} ${param.seriesName}: ${isArray(param.data) ? param.data[1] : param.data}`
            }).join('</br>');
        }
        /**
         * 设置具体的数据
         */
        options.series = y.map((item: any, index: any) => {
            let arrData = item;
            if (metricsType == 'source_input_bps') {
                arrData = isExchangeData(item, unit)
            }
            let line: any = {
                name: legend[index],
                data: arrData,
                type: 'line',
                smooth: true,
                showSymbol: !(arrData.length > 2)
            }
            if (color[index]) {
                line.lineStyle = {
                    normal: {
                        color: color[index]
                    }
                }
                line.itemStyle = {
                    normal: {
                        color: color[index]
                    }
                }
            }
            return line;
        })
        /**
         * 执行绘图
         */
        myChart.setOption(options)
        this.setState({
            lineChart: myChart
        })
    }
    resize = () => {
        if (this.state.lineChart) this.state.lineChart.resize()
    }
    render () {
        return (
            <Resize onResize={this.resize.bind(this)}>
                <article ref={(ref: any) => { this._dom = ref }} style={{ width: '100%', height: '100%' }} />
            </Resize>
        )
    }
}

export default AlarmBaseGraphBox;
