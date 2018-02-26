import React, { Component } from 'react'
import { cloneDeep } from 'lodash'
import moment from 'moment'

import {
    Row, Col, Card, Select, DatePicker,
} from 'antd'

import utils from 'utils'
import Resize from 'widgets/resize'

import Api from '../../api'
import MyIcon from '../../components/icon'
import { Normal } from '../../components/font'
import { lineAreaChartOptions, defaultBarOption } from '../../comm/const'

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
require('echarts/lib/chart/bar');
// 引入提示框和标题组件
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

const { RangePicker } = DatePicker;
const Option = Select.Option;

function initProject(props) {
    return props.projects && props.projects.length > 0 
    ? props.projects[0].id : ''
}

export default class ProjectList extends Component {

    state = {
        project: {},
        projectTable: '',
        projectStore: '',
        chart1: '',
        chart2: '',
        chart3: '',
        selectedDate: '',
        selectedProject: initProject(this.props),
    }

    componentDidMount() {
        this.loadProjectCount()
        this.loadProjectStoreTop5()
        this.loadProjectTableTop5()
        this.resizeChart()
        if (this.props.projects.length > 0) {
            this.loadDataOverview()
        }
    }

    componentWillReceiveProps(nextProps) {
        const nextProjects = nextProps.projects
        const old = this.props.projects
        if (old.length !== nextProjects.length) {
            this.setState({
                selectedProject: nextProjects[0].id
            }, () => {
                this.loadDataOverview();
                this.loadProjectCount();
            })
        }
    }

    loadProjectCount() {
        const ctx = this
        const { user } = this.props
        Api.getProjectInfo({
            tenantId: user.id, // 租户ID
        }).then((res) => {
            ctx.setState({
                project: res.data,
            })
        })
        Api.countProjectTable().then((res) => {
            ctx.setState({
                projectTable: res.data,
            })
        })
        Api.countProjectStore().then((res) => {
            ctx.setState({
                projectStore: res.data,
            })
        })
    }

    loadDataOverview() { // 默认最近7天
        const ctx = this
        const { selectedDate, selectedProject } = this.state
        const params = {}
        if (selectedDate.length > 0) {
            params.start = selectedDate[0].unix()
            params.end = selectedDate[1].unix()
        }
        if (selectedProject) {
            params.pId = selectedProject
        }
        Api.getProjectDataOverview(params).then((res) => {
            if (res.code === 1) {
                ctx.drawOverviewChart(res.data)
            }
        })
    }

    getSeries = (data) => {
        const arr = []
        if (data.tableNum) {
            arr.push({
                name: '表数量',
                type: 'line',
                smooth: true,
                symbolSize: 8,
                areaStyle: { normal: { opacity: 0.4 } },
                markLine: {
                    precision: 1,
                    label: {
                        normal: {
                            show: false,
                        }
                    }
                },
                data: data.tableNum.y.data || []
            })
        }
        if (data.projectSize) {
            arr.push({
                name: '存储量',
                type: 'line',
                smooth: true,
                symbolSize: 8,
                yAxisIndex: 1,
                areaStyle: { normal: { opacity: 0.4 } },
                markLine: {
                    precision: 1,
                },
                data: data.projectSize.y.data || []
            })
        }
        return arr
    }

    drawOverviewChart(chartData) {
        let myChart = echarts.init(document.getElementById('DataOverview'));
        const option = cloneDeep(lineAreaChartOptions);
        option.title.text = '数据概览'
        option.legend.show = false
        option.color[0] = '#F5A623'; //'#69e3be'
        option.color[1] = '#2491F7'; //'#F5A623'

        option.tooltip.formatter = function (params) {
            const showVal = utils.convertBytes(params[1].value)
            return `${params[0].axisValue}
                <br />${params[0].seriesName}: ${params[0].value} 个
                <br />${params[1].seriesName}: ${showVal}`
        }

        option.xAxis[0].axisLabel.formatter = '{value}'
        option.xAxis[0].axisLabel.textStyle.color = '#666666';
        option.xAxis[0].data = chartData && chartData.tableNum
            ? chartData.tableNum.x.data.map(item => moment(item).format('MM-DD'))
            : []

        option.yAxis[0].name = '表数量'
        // option.yAxis[0].minInterval = 1
        option.yAxis[0].axisLabel.formatter = '{value}'

        option.yAxis[1] = cloneDeep(option.yAxis[0])
        option.yAxis[1].name = '存储量'
        option.yAxis[1].axisLine.show = false
        option.yAxis[1].splitLine.show = false
        option.yAxis[1].axisLabel.formatter = function (value) {
            return utils.convertBytes(value)
        }

        // option.yAxis[0].axisLabel.formatter = '{value}'
        option.series = this.getSeries(chartData)
        // 绘制图表
        myChart.setOption(option);
        this.setState({ chart1: myChart })
    }

    loadProjectStoreTop5() {
        const ctx = this
        const { user } = this.props
        Api.getProjectStoreTop({ top: 5 }).then((res) => {
            if (res.code === 1) {
                ctx.drawStoreTop5(res.data)
            }
        })
    }

    drawStoreTop5(chartData) {
        let myChart = echarts.init(document.getElementById('StoreTop5'));
        const option = cloneDeep(defaultBarOption);
        const data = this.getPieData(chartData)
        option.title.text = '项目占用存储TOP5'
        option.color = ['#2491F7']

        option.tooltip.formatter = function (params) {
            const showVal = utils.convertBytes(params[0].value)
            return `${params[0].seriesName}: ${showVal}`
        }

        option.yAxis.data = data.y
        option.series[0].name = '占用'
        option.series[0].data = data.x
        option.series[0].label.normal.formatter = function (params) {
            return utils.convertBytes(params.value)
        }
        option.legend.show = false
        // 绘制图表
        myChart.setOption(option);
        this.setState({ chart2: myChart })
    }

    loadProjectTableTop5() {
        const ctx = this
        Api.getProjectTableStoreTop({ top: 5 }).then((res) => {
            if (res.code === 1) {
                ctx.drawTableTop5(res.data)
            }
        })
    }

    drawTableTop5(chartData) {
        let myChart = echarts.init(document.getElementById('TableTop5'));
        const option = cloneDeep(defaultBarOption);
        const data = this.getPieData(chartData)

        option.color = ['#F5A623']
        option.title.text = '表占用存储TOP5'
        option.legend.show = false

        option.tooltip.formatter = function (params) {
            const showVal = utils.convertBytes(params[0].value)
            return `${params[0].seriesName}: ${showVal}`
        }

        option.yAxis.data = data.y
        option.series[0].data = data.x
        option.series[0].name = '占用'
        option.series[0].label.normal.formatter = function (params) {
            return utils.convertBytes(params.value)
        }

        // 绘制图表
        myChart.setOption(option);
        this.setState({ chart3: myChart })
    }

    getPieData(data) {
        const y = [], x = []
        if (data && data.length > 0) {
            for (let i = data.length - 1; i >= 0; i--) {
                y.push(data[i].projectname)
                x.push(parseInt(data[i].size, 10))
            }
        }
        return { y, x }
    }

    resizeChart = () => {
        const { chart1, chart2, chart3 } = this.state
        if (chart1) {
            chart1.resize()
            chart2.resize()
            chart3.resize()
        }
    }

    changeDate = (selectedDate) => {
        this.setState({ selectedDate }, () => {
            this.loadDataOverview()
        })
    }

    disabledDate = (current) => {
        return current && current.valueOf() > new Date().getTime();
    }

    projectOnChange = (selectedProject) => {
        this.setState({ selectedProject }, () => {
            this.loadDataOverview()
        })
    }

    render() {

        const { project, selectedProject, projectTable, projectStore } = this.state

        const { projects } = this.props
        const projectOptions = projects ? projects.map(item => {
            return <Option key={item.id} value={item.id}>{item.projectAlias || item.projectName}</Option>
        }) : []

        const topStyle = {
            width: '50%',
            height: '100%',
            display: 'inline-block',
        }

        return (
            <div style={{margin: '0 14px'}}>
                <Abstract
                    project={project}
                    projectTable={projectTable}
                    projectStore={projectStore}
                />
                <Resize onResize={this.resizeChart}>
                <Row style={{ marginTop: '20px' }}>
                        <Col span={12}>
                            <div className="chart-board shadow">
                                <section
                                    id="DataOverview"
                                    style={{ width: '100%', height: '100%' }}>
                                </section>
                                <div className="filter">
                                    <span className="chart-tip">项目：</span>
                                    <Select
                                        value={selectedProject}
                                        onChange={this.projectOnChange}
                                        style={{ width: '100px' }}
                                    >
                                        {projectOptions}
                                    </Select>
                                    &nbsp;&nbsp;
                                    <RangePicker
                                        style={{ width: '230px' }}
                                        format="YYYY-MM-DD"
                                        disabledDate={this.disabledDate}
                                        onChange={this.changeDate}
                                        ranges={{
                                            '最近7天': [moment().subtract(6, 'days'), moment()],
                                            '最近30天': [moment().subtract(29, 'days'), moment()],
                                            '最近60天': [moment().subtract(59, 'days'), moment()]
                                        }}
                                    />
                                </div>
                            </div>
                        </Col>
                        <Col span={12}>
                            <div className="chart-board shadow">
                                <section
                                    id="StoreTop5"
                                    style={topStyle}>
                                </section>
                                <section
                                    id="TableTop5"
                                    style={topStyle}>
                                </section>
                            </div>
                        </Col>
                </Row>
                </Resize>
            </div>
        )
    }
}

function Abstract(props) {
    const { project, projectTable, projectStore } = props
    return (
        <Row>
            <Col span={6}>
                <div className="indicator-col shadow">
                    <div className="left indicator-icon">
                        <MyIcon type="overview" />
                    </div>
                    <div className="left indicator-detail">
                        <section className="indicator-title">我管理的项目</section>
                        <section className="indicator-content">{project.adminProjects || 0}</section>
                    </div>
                </div>
            </Col>
            <Col span={6}>
                <div className="indicator-col shadow">
                    <div className="left indicator-icon">
                        <MyIcon type="overview" />
                    </div>
                    <div className="left indicator-detail">
                        <section className="indicator-title">参与的项目</section>
                        <section className="indicator-content">{project.joinProjects || 0}</section>
                    </div>
                </div>
            </Col>
            <Col span={6}>
                <div className="indicator-col shadow">
                    <div className="left indicator-icon">
                        <MyIcon type="table" />
                    </div>
                    <div className="left indicator-detail">
                        <section className="indicator-title">总表数</section>
                        <section className="indicator-content">{projectTable || 0}</section>
                    </div>
                </div>
            </Col>
            <Col span={6}>
                <div className="indicator-col shadow">
                    <div className="left indicator-icon">
                        <MyIcon type="store" />
                    </div>
                    <div className="left indicator-detail">
                        <section className="indicator-title">总存储量</section>
                        <section className="indicator-content">{projectStore || 0}</section>
                    </div>
                </div>
            </Col>
        </Row>
    )
}