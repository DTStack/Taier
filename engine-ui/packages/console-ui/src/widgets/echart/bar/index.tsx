import * as React from 'react';
import Resize from '../../../widgets/resize';

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/bar');
// 引入提示框和标题组件
require('echarts/lib/component/legend');
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

interface IProps {
    options: any;
    style?: any;
    className?: any;
}

class Bar extends React.Component<IProps, any> {
    private _chartEle: any;
    private _chartInst: any;

    componentDidMount () {
        this.draw();
    }

    draw = () => {
        const { options } = this.props;
        const myChart = echarts.init(this._chartEle);
        // 绘制图表
        myChart.setOption(options);
        this._chartInst = myChart;
    }

    resize = () => {
        if (this._chartInst) {
            this._chartInst.resize()
        }
    }

    render () {
        const { style, className } = this.props;
        const mStyle = Object.assign({
            height: '100%',
            width: '100%'
        }, style);
        return (
            <Resize onResize={this.resize}>
                <div
                    ref={(ref: any) => { this._chartEle = ref; } }
                    style={mStyle}
                    className={className}>
                </div>
            </Resize>
        )
    }
}

export default Bar;
