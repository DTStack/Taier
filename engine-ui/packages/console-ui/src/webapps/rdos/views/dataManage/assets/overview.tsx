import * as React from 'react'
import { hashHistory } from 'react-router';

import { cloneDeep } from 'lodash'
import moment from 'moment'

import {
    Row, Col, Select, DatePicker, Tooltip
} from 'antd'

import utils from 'utils'
import Resize from 'widgets/resize'

import Api from '../../../api'
import TableDataApi from '../../../api/dataManage'
import MyIcon from '../../../components/icon'
import { lineAreaChartOptions, defaultBarOption } from '../../../comm/const'
import { wrap } from 'module';

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

export default class ProjectList extends React.Component<any, any> {
    state: any = {
        total: this.props.total || true,
        project: {},
        projectTable: '',
        projectStore: '',
        chart1: '',
        chart2: '',
        chart3: '',
        selectedDate: '',
        selectedProject: (this.props.projects[0] && this.props.projects[0].id) || '',
        topStyle: {
            width: '50%',
            height: '100%',
            display: 'inline-block'
        },
        chartSpan: 12
    }

    componentDidMount () {
        const { selectedProject } = this.state;
        this.loadProjectStoreTop5()
        this.loadProjectTableTop5()
        this.resizeChart()
        this.loadProjectCount()
        this.loadDataOverview(selectedProject)
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const nextProjects = nextProps.projects
        const old = this.props.projects
        if (old.length !== nextProjects.length) {
            const pid = nextProjects[0].id
            this.setState({
                selectedProject: pid
            }, () => {
                this.loadDataOverview(pid);
            })
            this.loadProjectCount();
        } else if (nextProps.total != this.props.total) {
            this.setState({
                total: nextProps.total
            }, this.componentDidMount)
        }
    }

    loadProjectCount () {
        const ctx = this
        const { total } = this.state;
        const params: any = {};

        params.total = total;

        Api.getProjectInfo(params).then((res: any) => {
            ctx.setState({
                project: res.data
            })
        })
        TableDataApi.countProjectTable(params).then((res: any) => {
            ctx.setState({
                projectTable: res.data
            })
        })
        TableDataApi.countProjectStore(params).then((res: any) => {
            ctx.setState({
                projectStore: res.data
            })
        })
    }

    loadDataOverview (projectId: any) { // 默认最近7天
        const ctx = this
        const { selectedDate } = this.state
        if (!projectId) return;

        const params: any = { pId: projectId }
        if (selectedDate.length > 0) {
            params.start = selectedDate[0].unix()
            params.end = selectedDate[1].unix()
        }
        TableDataApi.getProjectDataOverview(params).then((res: any) => {
            if (res.code === 1) {
                ctx.drawOverviewChart(res.data)
            }
        })
    }

    loadProjectStoreTop5 () {
        const ctx = this;
        const { total } = this.state;
        const params: any = { top: 5 };
        params.total = total;
        TableDataApi.getProjectStoreTop(params).then((res: any) => {
            if (res.code === 1) {
                ctx.drawStoreTop5(res.data)
            }
        })
    }

    loadProjectTableTop5 () {
        const ctx = this
        const { total } = this.state;
        const params: any = { top: 5 };
        params.total = total;
        TableDataApi.getProjectTableStoreTop(params).then((res: any) => {
            if (res.code === 1) {
                ctx.drawTableTop5(res.data)
            }
        })
    }

    getSeries = (data: any) => {
        const arr: any = []
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
                            show: false
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
                    precision: 1
                },
                data: data.projectSize.y.data || []
            })
        }
        return arr
    }

    drawOverviewChart (chartData: any) {
        let myChart = echarts.init(document.getElementById('DataOverview'));
        const option = cloneDeep(lineAreaChartOptions);
        option.title.text = '数据概览'
        option.legend.show = false
        option.color[0] = '#F5A623'; // '#69e3be'
        option.color[1] = '#2491F7'; // '#F5A623'

        option.tooltip.formatter = function (params: any) {
            const showVal = utils.convertBytes(params[1].value)
            return `${params[0].axisValue}
                <br />${params[0].seriesName}: ${params[0].value} 个
                <br />${params[1].seriesName}: ${showVal}`
        }

        option.xAxis[0].axisLabel.formatter = '{value}'
        option.xAxis[0].axisLabel.textStyle.color = '#666666';
        option.xAxis[0].data = chartData && chartData.tableNum
            ? chartData.tableNum.x.data.map((item: any) => moment(item).format('MM-DD'))
            : []

        option.yAxis[0].name = '表数量'
        // option.yAxis[0].minInterval = 1
        option.yAxis[0].axisLabel.formatter = '{value}'

        option.yAxis[1] = cloneDeep(option.yAxis[0])
        option.yAxis[1].name = '存储量'
        option.yAxis[1].axisLine.show = false
        option.yAxis[1].splitLine.show = false
        option.yAxis[1].axisLabel.formatter = function (value: any) {
            return utils.convertBytes(value)
        }

        // option.yAxis[0].axisLabel.formatter = '{value}'
        option.series = this.getSeries(chartData)
        // 绘制图表
        myChart.setOption(option);
        this.setState({ chart1: myChart })
    }

    drawStoreTop5 (chartData: any) {
        // const { topStyle } = this.state;
        let myChart = echarts.init(document.getElementById('StoreTop5'));
        const option = cloneDeep(defaultBarOption);
        const data = this.getPieData(chartData)
        option.title.text = '项目占用存储TOP5'
        option.color = ['#2491F7']

        option.tooltip.formatter = function (params: any) {
            const showVal = utils.convertBytes(params[0].value)
            return `${params[0].seriesName}: ${showVal}`
        }

        option.yAxis.data = data.y
        option.yAxis.triggerEvent = true;
        option.series[0].name = '占用'
        option.series[0].data = data.x
        option.series[0].label.normal.formatter = function (params: any) {
            return utils.convertBytes(params.value)
        }
        option.legend.show = false
        // 绘制图表
        myChart.setOption(option);
        myChart.on('click', (params: any) => {
            let projectId: any;
            chartData.map((v: any) => {
                if (v.projectname == params.value) {
                    projectId = v.projectId
                }
            })
            if (projectId) hashHistory.push(`/data-manage/search?pId=${projectId}`)
        });
        // const ctx = this;

        // myChart.on('mouseover', function(params: any) {
        //     console.log(params);
        //     params.event.target.style.textFill="#f60"
        //     topStyle.textFill = "#f60"
        //     ctx.setState({topStyle})
        // });
        // myChart.on('mouseout', function(params: any) {
        //     console.log(params);
        //     params.event.target.style.textFill="#666"
        //     topStyle.textFill = "#666"
        //     ctx.setState({topStyle})
        // });
        this.setState({ chart2: myChart })
    }

    drawTableTop5 (chartData: any) {
        let myChart = echarts.init(document.getElementById('TableTop5'));
        const option = cloneDeep(defaultBarOption);
        const data = this.getPieData(chartData, 'drawTable')

        option.color = ['#F5A623']
        option.title.text = '表占用存储TOP5'
        option.legend.show = false

        option.tooltip.formatter = function (params: any) {
            const param = params[0];
            const data = param.data.data;
            const { projectname, tableName } = data;
            const showVal = utils.convertBytes(params[0].value)
            return `项目:${projectname}<br/>表:${tableName}<br/>${params[0].seriesName}: ${showVal}`
        }

        option.yAxis.data = data.y
        option.yAxis.triggerEvent = true;
        option.series[0].data = data.x
        option.series[0].name = '占用'
        option.series[0].label.normal.formatter = function (params: any) {
            return utils.convertBytes(params.value)
        }
        option.yAxis.axisLabel.formatter = (item: any) => {
            const { value } = item;
            if (value.length > 16) {
                return value.slice(0, 16) + '...';
                // return "..." + value.slice(-20) ;
            } else {
                return value;
            }
        }
        // 绘制图表
        myChart.setOption(option);
        myChart.on('click', (params: any) => {
            const { data } = params.value;
            const { tableId } = data;
            if (tableId || tableId == 0) hashHistory.push(`/data-manage/table/view/${tableId}`)
        });
        this.setState({ chart3: myChart })
    }

    getPieData (data?: any, type?: any) {
        const y: any = []; const x: any = []
        if (type == 'drawTable') {
            if (data && data.length > 0) {
                for (let i = data.length - 1; i >= 0; i--) {
                    y.push({ value: { value: `${data[i].projectname}.${data[i].tableName}`, data: data[i] } })
                    x.push({ value: parseInt(data[i].size, 10), data: data[i] })
                }
            }
        } else {
            if (data && data.length > 0) {
                for (let i = data.length - 1; i >= 0; i--) {
                    y.push(data[i].projectname)
                    x.push(parseInt(data[i].size, 10))
                }
            }
        }

        return { y, x }
    }

    resizeChart = () => {
        const { chart1, chart2, chart3 } = this.state
        let chartSpan: any;
        if (document.body.clientWidth < 1430) {
            chartSpan = 24
        } else {
            chartSpan = 12
        }
        this.setState({
            chartSpan
        })
        if (chart1) {
            chart1.resize()
            chart2.resize()
            chart3.resize()
        }
    }

    changeDate = (selectedDate: any) => {
        const { selectedProject } = this.state
        this.setState({ selectedDate }, () => {
            this.loadDataOverview(selectedProject)
        })
    }

    disabledDate = (current: any) => {
        return current && current.valueOf() > new Date().getTime();
    }

    projectOnChange = (selectedProject: any) => {
        this.setState({ selectedProject }, () => {
            this.loadDataOverview(selectedProject)
        })
    }

    render () {
        const { project, selectedProject, projectTable, projectStore, topStyle, total, chartSpan } = this.state
        const { projects } = this.props
        const projectOptions = projects ? projects.map((item: any) => {
            return <Option key={item.id} value={item.id} title={item.projectAlias || item.projectName} ><Tooltip placement="top" mouseEnterDelay={1} title={item.projectAlias || item.projectName}>{item.projectAlias || item.projectName}</Tooltip></Option>
        }) : []

        return (
            <div style={{ margin: '0 14px' }}>
                <Abstract
                    project={project}
                    projectTable={projectTable}
                    projectStore={projectStore}
                    total={total}
                />
                <Resize onResize={this.resizeChart}>
                    <Row style={{ marginTop: 20 }}>
                        <Col span={chartSpan} style={{ marginBottom: 20 }} >
                            <div className="chart-board shadow">
                                <section id="DataOverview" style={{ width: '100%', height: '100%' }} />
                                <div className="filter">
                                    <span className="chart-tip"> &nbsp;&nbsp;&nbsp;项目：</span>
                                    <Select
                                        style={{ width: 126 }}
                                        showSearch
                                        value={selectedProject}
                                        onChange={this.projectOnChange}
                                        filterOption={(input: any, option: any) => option.props.children.props.children.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                                    >
                                        {projectOptions}
                                    </Select>
                                    &nbsp;&nbsp;
                                    <RangePicker
                                        style={{ width: '230px' }}
                                        format="YYYY-MM-DD"
                                        defaultValue={[moment().subtract(6, 'days'), moment()]}
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
                        <Col span={chartSpan} style={{ minWidth: 600 }}>
                            <div className="chart-board shadow">
                                <section
                                    id="StoreTop5"
                                    color={this.state.color}
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

function Abstract (props: any) {
    const { project, projectTable, projectStore, total } = props
    return (
        <Row gutter={32} style={{ padding: '0 10px' }} type="flex" justify="space-between">
            <Col span={8} >
                <div className="indicator-col shadow">
                    <div className="left indicator-icon">
                        <MyIcon type="overview" />
                    </div>
                    <div className="left indicator-detail">
                        <section className="indicator-title">总项目数</section>
                        <section className="indicator-content">{total ? project.allProjects || 0 : project.joinProjects || 0}</section>
                    </div>
                </div>
            </Col>
            <Col span={8}>
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
            <Col span={8}>
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
