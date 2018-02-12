import React from 'react';
import { cloneDeep } from 'lodash';
import {
    Card, Select,
} from 'antd'

import utils from 'utils';
import Resize from 'widgets/resize';

import ajax from '../../../../api';
import { lineAreaChartOptions } from '../../../../comm/const'

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

const Option = Select.Option

export default class TableOverview extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
            showType: 0, // 0/1 (非)字段
            visible: false,
            code: '',
            tableId: this.props.routeParams.tableId,
        };
    }

    componentDidMount() {
        const tableId = this.state.tableId;
        this.getTableOverview({ tableId: tableId, recentTime: '7' });
    }

    onExecCountChange = (value) => {
        const tableId = this.state.tableId;
        this.getTableOverview({ 
            tableId,
            recentTime: value,
        });
    }

    resize = () => {
        if (this._lineChart) {
            this._lineChart.resize();
        }
    }

    getTableOverview(params) {
        ajax.getDirtyDataTableOverview(params).then(res => {
            if(res.code === 1) {
                this.renderLineChart(res.data)
            }
        });
    }

    getSeries = (data) => {
        const arr = []
        if (data && data.y) {
            const legend = data && data.type ? data.type.data : []
            for (let i = 0; i < legend.length; i++) {
                arr.push({
                    name: legend[i],
                    symbol: 'none',
                    type:'line',
                    data: data.y[i].data,
                })
            }
        }
        return arr
    }

    renderLineChart = (chartData) => {
        let myChart = echarts.init(document.getElementById('Table_Overview'));
        const option = cloneDeep(lineAreaChartOptions);
        option.title.text = '脏数据概览图'
        const formatDate = function(obj) {
            return utils.formatDate(obj.value);
        }
        option.tooltip.axisPointer.label.formatter = formatDate
        option.xAxis[0].axisLabel.formatter = formatDate;

        option.yAxis[0].minInterval = 1
        option.legend.data = chartData && chartData.type ? chartData.type.data : []
        option.xAxis[0].data =  chartData && chartData.x ? chartData.x.data : []
        option.series = this.getSeries(chartData)
        // 绘制图表
        myChart.setOption(option);
        this._lineChart = myChart
    }

    render() {
        return <div className="box">
            <Card noHovering 
                extra={
                    <Select  
                        showSearch
                        defaultValue="7"
                        style={{ width: 150, marginTop: '10px' }}
                        placeholder="请选择次数"
                        onChange={this.onExecCountChange}
                    >
                        <Option value="7">最近7次</Option>
                        <Option value="30">最近30次</Option>
                        <Option value="60">最近60次</Option>
                    </Select>
                }>
                <Resize onResize={this.resize}>
                    <div id="Table_Overview" style={{height: '300px'}}></div>
                </Resize>
            </Card>
        </div>
    }
}