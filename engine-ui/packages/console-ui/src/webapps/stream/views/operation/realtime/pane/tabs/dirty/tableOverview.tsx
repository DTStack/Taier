import * as React from 'react';
import { cloneDeep } from 'lodash';
import {
    Card, Select, Tooltip, Icon
} from 'antd'

import utils from 'utils';
import Resize from 'widgets/resize';

import ajax from '../../../../../../api';
import { lineAreaChartOptions } from '../../../../../../comm/const'

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
        recentTime: '10m'
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

    getTableOverview () {
        const params = {
            tableId: this.props.tableId,
            recentTime: this.state.recentTime
        }
        ajax.getDirtyDataTableOverview(params).then((res: any) => {
            if (res.code === 1) {
                this.renderLineChart(res.data)
            }
        });
    }

    getSeries = (data: any) => {
        const arr: any = []
        if (data && data.y) {
            const legendData = data && data.type ? data.type.data : [];
            const legend = this.coverToCN(legendData);
            for (let i = 0; i < legend.length; i++) {
                arr.push({
                    name: legend[i],
                    type: 'line',
                    data: data.y[i].data
                })
            }
        }
        return arr
    }

    coverToCN (arr: any) {
        for (let i = 0; i < arr.length; i++) {
            switch (arr[i]) {
                case 'total':
                    arr[i] = '错误总数'; continue;
                case 'npe':
                    arr[i] = '空指针错误'; continue;
                case 'duplicate':
                    arr[i] = '主键冲突'; continue;
                case 'conversion':
                    arr[i] = '字段类型转换错误'; continue;
                case 'other':
                    arr[i] = '其他'; continue;
            }
        }
        return arr;
    }

    renderLineChart = (chartData: any) => {
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
        const legendData = chartData && chartData.type ? chartData.type.data : []
        option.legend.data = this.coverToCN(legendData);
        option.xAxis[0].data = chartData && chartData.x ? chartData.x.data : []
        option.series = this.getSeries(chartData)
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
                            <Option value="10m">最近10分钟</Option>
                            <Option value="1h">最近1小时</Option>
                            <Option value="6h">最近6小时</Option>
                            <Option value="1d">最近1天</Option>
                            <Option value="1w">最近1周</Option>
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
