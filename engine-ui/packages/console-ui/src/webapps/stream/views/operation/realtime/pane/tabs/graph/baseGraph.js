import React from "react"
import moment from "moment";
import utils from "utils";
import { cloneDeep } from "lodash";

import Resize from 'widgets/resize';

import { lineAreaChartOptions, TIME_TYPE } from "../../../../../../comm/const"

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/legend');
require('echarts/lib/component/legendScroll');
require('echarts/lib/component/tooltip');

class AlarmBaseGraph extends React.Component {

    state = {
        lineChart: null
    }

    componentDidMount() {
        this.initGraph();
    }
    componentWillReceiveProps(nextProps) {
        if (this.props.lineData != nextProps.lineData||this.props.time != nextProps.time) {
            this.initGraph(nextProps.lineData,nextProps.time)
        }
    }
    exchangeDate(date,time){
        switch(time){
            case TIME_TYPE.M10:
            case TIME_TYPE.H1:
            case TIME_TYPE.H6:{
                return utils.formatHours(date);
            }
            case TIME_TYPE.D1:
            case TIME_TYPE.W1:{
                return utils.formatDateHours(date)
            }
            default:{
                return utils.formatHours(date);
            }
        }
    }
    initGraph(lineData,time) {
        lineData = lineData||this.props.lineData;
        time= time||this.props.time;
        if (!lineData) {
            return;
        }
        const { x, y, legend, color, unit } = lineData;
        let myChart = echarts.init(this._dom);
        let options = cloneDeep(lineAreaChartOptions);
        /**
         * 隐藏标题
         */
        options.title.show = false;
        /**
         * 设置纵坐标名称
         */
        options.yAxis[0].name = unit;
        /**
         * 设置横坐标数值
         */
        options.xAxis[0].data = x.map((date)=>{
            return this.exchangeDate(date, time)
        });
        /**
         * 设置小图标
         */
        options.legend.type="scroll";
        options.legend.data = legend.map((item) => {
            return {
                name: item,
                icon: "circle"
            }
        });
        options.legend.bottom = "12px"
        options.legend.left = "10px";
        options.legend.itemWidth = 6
        options.legend.itemHeight = 6
        // options.legend.borderRadius="50%";
        /**
         * 画图区域的定位
         */
        options.grid.bottom = "45px";
        options.grid.left = "20px";
        options.grid.right = "30px";
        options.grid.top = "50px";
        /**
         * 设置具体的数据
         */
        options.series = y.map((item, index) => {
            let line = {
                name: legend[index],
                data: item,
                type: "line",
                smooth: true,
                showSymbol:false
            }
            if(color[index]){
                line.lineStyle={
                    normal:{
                        color:color[index]
                    }
                }
                line.itemStyle={
                    normal:{
                        color:color[index]
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
    render() {
        const { title } = this.props;
        return (
            <div className="alarm-basegraph-box">
                <header>
                    {title}
                </header>
                <Resize onResize={this.resize.bind(this)}>
                    <article ref={(ref) => { this._dom = ref }} style={{ width: '100%', height: '260px' }} />
                </Resize>
            </div>
        )
    }
}

export default AlarmBaseGraph;