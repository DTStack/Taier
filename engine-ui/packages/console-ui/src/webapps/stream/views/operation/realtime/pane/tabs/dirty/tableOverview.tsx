import * as React from 'react';
import { cloneDeep, get } from 'lodash';
import {
    Card, Select, Tooltip, Icon
} from 'antd'

import utils from 'utils';
import Resize from 'widgets/resize';

import Api from '../../../../../../api';
import { lineAreaChartOptions, TIME_TYPE } from '../../../../../../comm/const'

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

const Option = Select.Option

export default class TableOverview extends React.Component<any, any> {
    state: any = {
        recentTime: TIME_TYPE.M10,
        x: [],
        y: []
    }
    _lineChart: any;
    componentDidMount () {
        this.getTableOverview();
    }

    onExecCountChange = (value: any) => {
        this.setState({
            recentTime: value
        }, this.getTableOverview.bind(this))
    }

    resize = () => {
        if (this._lineChart) {
            this._lineChart.resize();
        }
    }

    async getTableOverview () {
        let chartName = 'nErrors';
        const params = {
            taskId: this.props.taskId,
            timeStr: this.state.recentTime,
            chartNames: [chartName]
        }
        let res = await Api.getTaskMetrics(params);
        if (res && res.code == 1) {
            let data = get(res, 'data[0].data', []);
            let x = [];
            let y = [];
            for (let i = 0; i < data.length; i++) {
                x.push(data[i].time);
                y.push(data[i][chartName]);
            }
            this.setState({
                x,
                y
            })
        }
    }

    componentDidUpdate (prevProps: any, prevState: any) {
        if (prevState.x != this.state.x || prevState.y != this.state.y) {
            this.paint();
        }
    }

    paint () {
        const { x, y } = this.state;
        let myChart = echarts.init(document.getElementById('Table_Overview'));
        const option = cloneDeep(lineAreaChartOptions);
        option.title.text = '脏数据概览图'
        const formatDate = function (obj: any) {
            return utils.formatDate(parseInt(obj.value, 10));
        }
        option.tooltip.axisPointer.label.formatter = formatDate
        option.xAxis[0].axisLabel.formatter = function (value: any) {
            return utils.formatDate(+value);
        }

        option.yAxis[0].minInterval = 1
        option.yAxis[0].axisLabel.formatter = '{value} 条'
        option.xAxis[0].data = x;
        option.series = [{
            type: 'line',
            data: y
        }]
        // 绘制图表
        myChart.setOption(option);
        this._lineChart = myChart
    }

    render () {
        const { recentTime } = this.state;
        return <div>
            <Card
                noHovering
                bordered={false}
                extra={
                    <React.Fragment>
                        <Tooltip
                            title="刷新"
                        >
                            <Icon
                                onClick={this.getTableOverview.bind(this)}
                                type="reload"
                                style={{ color: '#333', marginRight: '20px', cursor: 'pointer' }}
                            />
                        </Tooltip>
                        <Select
                            showSearch
                            value={recentTime}
                            style={{ width: 126, marginTop: '10px' }}
                            placeholder="请选择时间"
                            onChange={this.onExecCountChange}
                        >
                            <Option value={TIME_TYPE.M10}>最近10分钟</Option>
                            <Option value={TIME_TYPE.H1}>最近1小时</Option>
                            <Option value={TIME_TYPE.H6}>最近6小时</Option>
                            <Option value={TIME_TYPE.D1}>最近1天</Option>
                            <Option value={TIME_TYPE.W1 }>最近1周</Option>
                        </Select>
                    </React.Fragment>
                }>
                <Resize onResize={this.resize}>
                    <div id="Table_Overview" style={{ height: '300px', padding: '20px' }}></div>
                </Resize>
            </Card>
        </div>
    }
}
