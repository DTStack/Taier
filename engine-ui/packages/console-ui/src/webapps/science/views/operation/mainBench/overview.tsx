import * as React from 'react';
import { Row, Col, Icon, Tabs } from 'antd';
import { cloneDeep } from 'lodash';
import Resize from 'widgets/resize';
import { lineChartOptions } from '../../../comm/const.js';
import { taskType } from '../../../consts';
import Api from '../../../api/index';
// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/tooltip');
require('echarts/lib/component/legend');
const TabPane = Tabs.TabPane;

class Overview extends React.PureComponent<any, any> {
    state: any = {
        experimentData: {},
        notebookData: {},
        activeKey: `${taskType.EXPERIMENT}`,
        status: {
            deployNotebookCount: 0,
            failLabCount: 0,
            failNotebookCount: 0,
            successLabCount: 0,
            deployLabCount: 0,
            totalLabCount: 0,
            successNotebookCount: 0,
            totalNotebookCount: 0
        }
    }
    _chart: any;
    componentDidMount () {
        this.getStatistics();
        this.initData();
    }
    componentDidUpdate (prevProps: any, prevState: any, snapshot: any) {
        if (prevState.activeKey !== this.state.activeKey) {
            this.initData();
            // this.resizeChart();
        }
    }
    drawCharts = (chartDatas?: any, id?: any) => {
        const { activeKey, experimentData, notebookData } = this.state;
        switch (activeKey) {
            case `${taskType.EXPERIMENT}`: {
                chartDatas = experimentData;
                id = 'experiment'
                break;
            }
            case `${taskType.NOTEBOOK}`: {
                chartDatas = notebookData;
                id = 'notebook'
                break;
            }
        }
        let myChart = echarts.init(document.getElementById(id));
        const option = cloneDeep(lineChartOptions);
        option.color = ['#2491F7', '#00A755', '#E64933', '#F5BD23'];
        option.title.text = '';
        option.legend = {
            data: chartDatas && chartDatas.y ? chartDatas.y.map((item: any) => { return { name: item.name, icon: 'circle' } }) : [],
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

        option.tooltip.formatter = function (params: any) {
            let dataString = '';
            params.forEach((item: any) => {
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

        option.series = chartDatas && chartDatas.y ? chartDatas.y.map((item: any) => {
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
        this._chart = myChart;
        return myChart;
    }

    resizeChart = () => {
        this._chart && this._chart.resize()
    }
    initData = async () => {
        const { activeKey } = this.state;
        let res = await Api.comm.getProjectJobGraph({
            taskType: activeKey
        });
        if (res && res.code == 1) {
            let params: any = {};
            switch (activeKey) {
                case `${taskType.EXPERIMENT}`: {
                    params.experimentData = {
                        ...res.data,
                        type: null
                    }
                    break;
                }
                case `${taskType.NOTEBOOK}`: {
                    params.notebookData = {
                        ...res.data,
                        type: null
                    }
                    break;
                }
            }
            this.setState({
                ...params
            }, this.drawCharts)
        }
    }
    getStatistics = () => {
        Api.comm.getProjectJobStatus().then((res: any) => {
            if (res && res.code === 1) {
                this.setState({
                    status: res.data
                })
            }
        })
    }
    renderOverview = () => {
        const { status } = this.state;
        return <Row gutter={20} style={{ marginBottom: 30 }}>
            <Col span={6}>
                <div className="info-box blue">
                    <div className="info-box-left">
                        <p><Icon type="appstore-o" /><span>总任务</span></p>
                        <p className="number">{status.totalNotebookCount + status.totalLabCount}</p>
                    </div>
                    <div className="info-box-right">
                        <p>实验数：{status.totalLabCount}</p>
                        <p>Notebook作业数：{status.totalNotebookCount}</p>
                    </div>
                </div>
            </Col>
            <Col span={6}>
                <div className="info-box green">
                    <div className="info-box-left">
                        <p><Icon type="appstore-o" /><span>已提交任务数</span></p>
                        <p className="number">{status.deployLabCount + status.deployNotebookCount}</p>
                    </div>
                    <div className="info-box-right">
                        <p>实验数：{status.deployLabCount}</p>
                        <p>Notebook作业数：{status.deployNotebookCount}</p>
                    </div>
                </div>
            </Col>
            <Col span={6}>
                <div className="info-box purple">
                    <div className="info-box-left">
                        <p><Icon type="appstore-o" /><span>运行失败实例</span></p>
                        <p className="number">{status.failLabCount + status.failNotebookCount}</p>
                    </div>
                    <div className="info-box-right">
                        <p>实验数：{status.failLabCount}</p>
                        <p>Notebook作业数：{status.failNotebookCount}</p>
                    </div>
                </div>
            </Col>
            <Col span={6}>
                <div className="info-box yellow">
                    <div className="info-box-left">
                        <p><Icon type="appstore-o" /><span>运行成功实例</span></p>
                        <p className="number">{status.successLabCount + status.successNotebookCount}</p>
                    </div>
                    <div className="info-box-right">
                        <p>实验数：{status.successLabCount}</p>
                        <p>Notebook作业数：{status.successNotebookCount}</p>
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
                <Tabs type="card" activeKey={activeKey} onChange={(activeKey: any) => this.setState({ activeKey })}>
                    <TabPane tab="实验" key={`${taskType.EXPERIMENT}`} forceRender={true}>
                        <Resize onResize={this.resizeChart}>
                            <div id="experiment" style={{
                                width: '100%',
                                height: '290px'
                            }}></div>
                        </Resize>
                    </TabPane>
                    <TabPane tab="Notebook作业" key={`${taskType.NOTEBOOK}`} forceRender={true}>
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
