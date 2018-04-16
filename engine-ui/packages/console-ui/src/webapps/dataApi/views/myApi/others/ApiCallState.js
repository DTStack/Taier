import React, { Component } from "react";
import { Col, Row, Table } from 'antd';
import Resize from 'widgets/resize';
import {cloneDeep} from "lodash"
import { doubleLineAreaChartOptions } from '../../../consts';
import utils from "utils"

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/legend');
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');
class ApiCallState extends Component {
    state = {
        apiId: "",
        data: {}
    }
    getInfo() {

        if (!this.state.apiId) {
            return;
        }
        this.props.getApiCallInfo(this.state.apiId, this.props.dateType)
            .then(
                (res) => {
                    if (res) {

                        this.setState({
                            data: res.data
                        },
                            () => {
                                this.initLineChart();
                            })
                    }
                }
            )
    }
    componentDidMount() {
        this.setState({
            apiId: this.props.showRecord && this.props.showRecord.apiId
        })
        this.getInfo();

    }
    componentWillReceiveProps(nextProps) {
        if (
            (nextProps.showRecord && this.state.apiId !== nextProps.showRecord.apiId)
            ||
            (this.props.dateType !== nextProps.dateType)
        ) {
            this.setState({
                apiId: nextProps.showRecord.apiId
            },
                () => {
                    this.getInfo();

                })
        }
    }
    resize = () => {
        if (this.state.lineChart) this.state.lineChart.resize()
    }
    initLineChart() {
        if (!this.state.data || !this.state.data.infoList) {
            return;
        }
        const chartData=this.state.data.infoList;
        let callCountDate = [];
        let failCountDate = [];
        let times = [];
        for (let i = 0; i < chartData.length; i++) {
            callCountDate.push(chartData[i].callCount)
            failCountDate.push(chartData[i].failRate)
            if (this.props.dateType) {
                switch (this.props.dateType) {
                    case "1":
                        times.push(utils.formatHours(chartData[i].time));
                        break;
                    case "7":
                        times.push(utils.formatDateHours(chartData[i].time));
                        break;
                    case "30":
                        times.push(utils.formatDate(chartData[i].time));
                        break;
                }
            }
            
        }
        let myChart = echarts.init(document.getElementById('MyApiDetailState'));
        const option = cloneDeep(doubleLineAreaChartOptions);
        option.grid.right="40px";
        option.series = [{
            symbol: "none",
            name: "调用次数",
            data: callCountDate,
            type: 'line',
            smooth: true,
            itemStyle: {
                normal: {
                    color: '#1C86EE'
                }
            },
        }, {
            symbol: "none",
            name: "失败率",
            data: failCountDate,
            type: 'line',
            smooth: true,
            yAxisIndex: 1,
            itemStyle: {
                normal: {
                    color: '#EE0000'
                }
            },
        }];
        option.xAxis[0].data = times;
        console.log(option)
        // 绘制图表
        myChart.setOption(option);
        this.setState({ lineChart: myChart })
    }
    getDateText() {
        switch (this.props.dateType) {
            case "1":
                return "24小时";
            case "7":
                return "7天";
            case "30":
                return "30天";
            default:
                return '';

        }
    }
    render() {
        return (
            <div style={{ paddingLeft: 30 }}>
                <Row gutter={130} className="m-count padding-l20 height-101">
                    <Col span={8}>
                        <section className="m-count-section margin-t20" style={{ width: 150 }}>
                            <span className="m-count-title text-left">最近{this.getDateText()}累计调用</span>
                            <span className="m-count-content font-black text-left">{this.state.data && this.state.data.callCount || 0}<span style={{ fontSize: 12 }}>次</span></span>
                        </section>
                    </Col>
                    <Col span={6}>
                        <section className="m-count-section margin-t20" style={{ width: 100 }}>
                            <span className="m-count-title text-left">最近{this.getDateText()}失败率</span>
                            <span className="m-count-content font-red text-left">{this.state.data && this.state.data.failRate || 0}<span style={{ fontSize: 12 }}>%</span></span>
                        </section>
                    </Col>
                    <Col span={8}>
                        <section className="m-count-section margin-t20" style={{ width: 150 }}>
                            <span className="m-count-title text-left">累计调用</span>
                            <span className="m-count-content font-black text-left">{this.state.data && this.state.data.totalCount || '---'}</span>
                        </section>
                    </Col>
                </Row>
                <Resize onResize={this.resize}>
                    <article id="MyApiDetailState" style={{ width: '100%', height: '300px' }} />
                </Resize>
            </div>
        )
    }
}
export default ApiCallState;