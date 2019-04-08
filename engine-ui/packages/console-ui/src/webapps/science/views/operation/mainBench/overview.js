import React from 'react';
import { Row, Col, Icon, Tabs } from 'antd';
import { cloneDeep } from 'lodash';
import Resize from 'widgets/resize';
import { lineChartOptions } from '../../../comm/const.js';
// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/tooltip');
require('echarts/lib/component/legend');
const TabPane = Tabs.TabPane;

class Overview extends React.PureComponent {
    state = {
        experimentData: {},
        activeKey: 'experiment'
    }
    componentDidMount () {
        this.getExperiment();
    }
    componentDidUpdate (prevProps, prevState, snapshot) {
        if (prevState.activeKey !== this.state.activeKey) {
            this.resizeChart();
        }
    }
    drawCharts = (chartDatas, id) => {
        let myChart = echarts.init(document.getElementById(id));
        const option = cloneDeep(lineChartOptions);
        option.color = ['#2491F7', '#00A755', '#E64933'];
        option.title.text = '';
        option.legend = {
            data: chartDatas && chartDatas.y ? chartDatas.y.map((item) => { return { name: item.name, icon: 'circle' } }) : [],
            top: '10px',
            itemHeight: 10,
            itemWidth: 10,
            itemGap: 20
        }
        option.grid = {
            left: '2%',
            right: '2%',
            bottom: 20,
            containLabel: true
        }

        option.xAxis[0].axisTick = {
            show: false,
            alignWithLabel: true
        }

        option.tooltip.formatter = function (params) {
            let dataString = '';
            params.forEach((item) => {
                dataString += `${item.marker} ${item.seriesName}: ${item.data} <br>`;
            })
            return `${params[0].axisValue} <br> ${dataString}`
        }

        // option.xAxis[0].boundaryGap = ['1%', '1%'];
        option.xAxis[0].axisLabel = {
            align: 'center',
            color: '#666666',
            margin: 12
        }
        option.xAxis[0].data = chartDatas && chartDatas.x ? chartDatas.x.data : [];
        option.yAxis[0].minInterval = 1;

        option.series = chartDatas && chartDatas.y ? chartDatas.y.map((item) => {
            return {
                name: item.name,
                type: 'line',
                markLine: {
                    precision: 1
                },
                data: item.data
            }
        }) : []
        // 绘制图表
        myChart.setOption(option, { notMerge: true });
        return myChart;
    }

    resizeChart = () => {
        this._chart1 && this._chart1.resize()
        this._chart2 && this._chart2.resize()
    }
    getExperiment = () => {
        this.setState({
            experimentData: {
                type: null,
                x: {
                    name: 'time',
                    data: ['01-2', '01-01', '01-01', '01-01', '01-01', '01-01', '01-01', '01-01', '01-01', '01-01', '01-01', '01-01', '01-01']
                },
                y: [{
                    name: '总实例数',
                    data: [1, 2, 3, 3, 31, 43, 3, 3, 3, 13, 33, 3, 224]
                }, {
                    name: '成功实例',
                    data: [1, 2, 3, 3, 3, 23, 3, 3, 3, 3, 3, 3, 5]
                }, {
                    name: '失败实例',
                    data: [1, 2, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 56]
                }]
            }
        }, () => {
            this._chart1 = this.drawCharts(this.state.experimentData, 'experiment');
            this._chart2 = this.drawCharts(this.state.experimentData, 'notebook');
        })
    }
    renderOverview = () => {
        return <Row gutter={20} style={{ marginBottom: 30 }}>
            <Col span={6}>
                <div className="info-box blue">
                    <div className="info-box-left">
                        <p><Icon type="appstore-o" /><span>总任务</span></p>
                        <p className="number">222</p>
                    </div>
                    <div className="info-box-right">
                        <p>实验数：222</p>
                        <p>Notebook作业数：4</p>
                    </div>
                </div>
            </Col>
            <Col span={6}>
                <div className="info-box green">
                    <div className="info-box-left">
                        <p><Icon type="appstore-o" /><span>已部署任务</span></p>
                        <p className="number">222</p>
                    </div>
                    <div className="info-box-right">
                        <p>实验数：222</p>
                        <p>Notebook作业数：4</p>
                    </div>
                </div>
            </Col>
            <Col span={6}>
                <div className="info-box purple">
                    <div className="info-box-left">
                        <p><Icon type="appstore-o" /><span>运行失败实例</span></p>
                        <p className="number">222</p>
                    </div>
                    <div className="info-box-right">
                        <p>实验数：222</p>
                        <p>Notebook作业数：4</p>
                    </div>
                </div>
            </Col>
            <Col span={6}>
                <div className="info-box yellow">
                    <div className="info-box-left">
                        <p><Icon type="appstore-o" /><span>运行成功实例</span></p>
                        <p className="number">222</p>
                    </div>
                    <div className="info-box-right">
                        <p>实验数：222</p>
                        <p>Notebook作业数：4</p>
                    </div>
                </div>
            </Col>
        </Row>;
    }
    render () {
        const { activeKey } = this.state;
        return (
            <div className='operation'>
                <div className="title">项目任务执行汇总</div>
                {this.renderOverview()}
                <Tabs type="card" activeKey={activeKey} onChange={(activeKey) => this.setState({ activeKey })}>
                    <TabPane tab="实验" key="experiment" forceRender={true}>
                        <Resize onResize={this.resizeChart}>
                            <div id="experiment" style={{
                                width: '100%',
                                height: '290px'
                            }}></div>
                        </Resize>
                    </TabPane>
                    <TabPane tab="NoteBook作业" key="notebook" forceRender={true}>
                        <Resize onResize={this.resizeChart}>
                            <div id="notebook" style={{
                                width: '100%',
                                height: '290px'
                            }}></div>
                        </Resize>
                    </TabPane>
                </Tabs>
            </div>
        )
    }
}
export default Overview;
