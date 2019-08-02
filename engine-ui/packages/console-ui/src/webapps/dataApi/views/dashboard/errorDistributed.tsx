import * as React from 'react'
import { Card } from 'antd';
import { pieOption } from '../../consts';
import { cloneDeep } from 'lodash'
import Resize from 'widgets/resize';
// 引入 ECharts 主模块
import echarts from 'echarts/lib/echarts';
// 引入柱状图
require('echarts/lib/chart/pie');
// 引入提示框和标题组件
require('echarts/lib/component/legend');
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

const errorDic: any = [
    {
        code: 'disable',
        name: '禁用',
        color: '#71C671'
    },
    {
        code: 'unauthorize',
        name: '未认证',
        color: 'rgba(244,67,54,0.9)'
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
class ErrorDistributed extends React.Component<any, any> {
    state: any = {

    }
    componentDidMount () {
        const data = this.props.chartData || [];
        this.intPie(data);
    }
    resize = () => {
        if (this.state.pieChart) this.state.pieChart.resize()
    }
    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        if (this.props.chartData != nextProps.chartData) {
            this.intPie(nextProps.chartData)
        }
    }
    intPie (chartData: any) {
        let item: any = [];
        let data: any = [];

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

        let myChart = echarts.init(document.getElementById('ErrorDistributedPie') as HTMLDivElement);
        const option = cloneDeep(pieOption);
        option.legend.data = item;
        option.series[0].data = data

        // 绘制图表
        myChart.setOption(option);
        this.setState({ pieChart: myChart })
    }
    render () {
        return (
            <Card
                noHovering
                title="错误类型分布"
                style={{ height: 403 }}
                className="shadow"
            >
                <Resize onResize={this.resize.bind(this)}>
                    <article id="ErrorDistributedPie" style={{ width: '100%', height: '300px' }} />
                </Resize>

            </Card>
        )
    }
}

export default ErrorDistributed;
