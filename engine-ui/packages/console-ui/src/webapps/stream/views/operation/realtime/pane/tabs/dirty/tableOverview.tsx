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

interface TableOverviewState {
    recentTime: string;
    x: any[];
    y: {
        conversionErrors: string[];
        duplicateErrors: string[];
        nErrors: string[];
        nullErrors: string[];
        otherErrors: string[];
    }
}

export default class TableOverview extends React.Component<any, TableOverviewState> {
    state: TableOverviewState = {
        recentTime: TIME_TYPE.M10,
        x: [],
        y: {
            conversionErrors: [],
            duplicateErrors: [],
            nErrors: [],
            nullErrors: [],
            otherErrors: []
        }
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
        let chartName = 'dirtyErrors';
        const params = {
            taskId: this.props.taskId,
            timeStr: this.state.recentTime,
            chartNames: [chartName]
        }
        let res = await Api.getTaskMetrics(params);
        if (res && res.code == 1) {
            let data = get(res, 'data[0].data', []);
            let x = [];
            let y: TableOverviewState['y'] = {
                conversionErrors: [],
                duplicateErrors: [],
                nErrors: [],
                nullErrors: [],
                otherErrors: []
            };
            for (let i = 0; i < data.length; i++) {
                x.push(data[i].time);
                y.conversionErrors.push(data[i].conversionErrors);
                y.duplicateErrors.push(data[i].duplicateErrors);
                y.nErrors.push(data[i].nErrors);
                y.nullErrors.push(data[i].nullErrors);
                y.otherErrors.push(data[i].otherErrors);
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
            return utils.formatDateTime(parseInt(obj.value, 10));
        }
        option.tooltip.axisPointer.label.formatter = formatDate
        option.xAxis[0].axisLabel.formatter = function (value: any) {
            return utils.formatDateHours(+value);
        }

        option.yAxis[0].minInterval = 1
        option.yAxis[0].axisLabel.formatter = '{value} 条'
        option.yAxis[0].name = '脏数据'
        option.xAxis[0].data = x;
        option.grid.right = '6%'
        option.grid.left = '5%'
        option.series = [{
            type: 'line',
            name: '错误总数',
            data: y.nErrors
        }, {
            type: 'line',
            name: '字段类型转换错误',
            data: y.conversionErrors
        }, {
            type: 'line',
            name: '主键冲突',
            data: y.duplicateErrors
        }, {
            type: 'line',
            name: '空指针错误',
            data: y.nullErrors
        }, {
            type: 'line',
            name: '其他',
            data: y.otherErrors
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
                            <Option value={TIME_TYPE.W1}>最近1周</Option>
                        </Select>
                    </React.Fragment>
                }>
                <Resize onResize={this.resize}>
                    <div id="Table_Overview" style={{ height: '300px', padding: '10px' }}></div>
                </Resize>
            </Card>
        </div>
    }
}
