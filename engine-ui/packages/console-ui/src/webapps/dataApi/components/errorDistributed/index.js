import React, { Component } from 'react'
import { cloneDeep } from 'lodash'

import Resize from 'widgets/resize';
import { pieOption } from '../../consts';
// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/pie');
// 引入提示框和标题组件
require('echarts/lib/component/legend');
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

const errorDic = [
    {
        code: 'disable',
        name: '禁用',
        color: '#71C671'
    },
    {
        code: 'unauthorize',
        name: '未认证',
        color: '#EE4000'
    },
    {
        code: 'paramerror',
        name: '参数错误',
        color: '#1C86EE'
    },
    {
        code: 'timeout',
        name: '超时',
        color: '#EE9A00'
    },
    {
        code: 'outlimit',
        name: '超出限制',
        color: '#40E0D0'
    },
    {
        code: 'other',
        name: '其他',
        color: '#A2B5CD'
    }
]
class ErrorDistributed extends Component {
    state = {

    }
    componentDidMount () {
        const data = this.props.chartData || [];
        this.intPie(data);
    }
    resize = () => {
        if (this.state.pieChart) this.state.pieChart.resize()
    }
    componentWillReceiveProps (nextProps) {
        if (this.props.chartData != nextProps.chartData) {
            this.intPie(nextProps.chartData)
        }
    }
    intPie (chartData) {
        let item = [];
        let data = [];

        for (let i = 0; i < chartData.length; i++) {
            let d = chartData[i];
            let errorItem = errorDic[d.type - 1]
            if (errorItem && item.indexOf(errorItem.name) < 0) {
                item.push(errorItem.name)
                data.push({
                    value: d.callNum,
                    name: errorItem.name,
                    itemStyle: {
                        normal: {
                            color: errorItem.color
                        }
                    }
                })
            }
        }

        let myChart = echarts.init(document.getElementById('ErrorDistributedPie'));
        const option = cloneDeep(pieOption);
        option.legend.data = item;
        option.series[0].data = data

        // 绘制图表
        myChart.setOption(option);
        this.setState({ pieChart: myChart })
    }
    render () {
        const defaultSize = {
            height: '300px'
        }
        const sizeMap = {
            'mini': {
                height: '280px'
            }
        }
        const { mode } = this.props;
        const size = mode ? sizeMap[mode] : defaultSize

        return (
            <Resize onResize={this.resize.bind(this)}>
                <article id="ErrorDistributedPie" style={{ width: '100%', height: size.height }} />
            </Resize>
        )
    }
}

export default ErrorDistributed;
