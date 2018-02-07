import React, { Component } from 'react'
import { cloneDeep } from 'lodash'
import { Link } from 'react-router'
import {
    Row, Col, Card, Button,
    Form, Select, Input, Table, 
    Radio,
} from 'antd'

import utils from 'utils'
import Resize from 'widgets/resize'

import Api from '../../../api'
import { lineAreaChartOptions } from '../../../comm/const'
import taskOperation from '../../operation/offline/taskOperation';

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

const Search = Input.Search
const FormItem = Form.Item
const Option = Select.Option
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;

class DirtyData extends Component {

    state = {
        lineChart: '',
        taskId: '',
        tableName: '',
        produceList: { data: [] },
        top30: [],
        dirtyTables: [],
        taskList: [],
        timeRange: 3,
        loading: false,
    }

    componentDidMount() {
        this.loadProduceTrendData({ 
            recent: 3, 
        })
        this.loadProduceTop30({ 
            recent: 3, 
            topN: 30, 
        })
        this.loadProduceData()
        this.loadSyncTasks()
    }

    componentWillReceiveProps(nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadProduceTrendData({ 
                recent: 3, 
            })
            this.loadProduceTop30({ 
                recent: 3, 
                topN: 30, 
            })
            this.loadProduceData()
        }
    }

    resize = () => {
        if (this.state.lineChart) this.state.lineChart.resize()
    }

    loadProduceTrendData = (params) => {
        const ctx = this
        Api.getDirtyDataTrend(params).then((res) => {
            if (res.code === 1) {
                this.renderProduceTrend(res.data)
            }
        })
    }

    loadProduceTop30 = (params) => {
        const ctx = this
        Api.top30DirtyData(params).then((res) => {
            if (res.code === 1) {
                ctx.setState({ top30: res.data })
            }
        })
    }

    loadProduceData = (params) => {
        const ctx = this
        this.setState({ loading: true })
        Api.getDirtyDataTables(params).then((res) => {
            if (res.code === 1) {
                ctx.setState({ produceList: res.data, loading: false })
            }
        })
    }

    loadSyncTasks = (params) => {
        const ctx = this
        Api.getPubSyncTask(params).then((res) => {
            if (res.code === 1) {
                ctx.setState({ taskList: res.data })
            }
        })
    }

    onTimeRangeChange = (e) => {
        const value = e.target.value
        this.setState({ timeRange: value })
        this.loadProduceTrendData({
            recent: value,
        })
        this.loadProduceTop30({ 
            recent: value, 
            topN: 30, 
        })
    }

    onTrendSelectTask = (value) => {
        const { timeRange } = this.state
        this.loadProduceTrendData({
            taskId: value,
            recent: timeRange, 
        })
    }
    
    onTableNameChange = (e) => {
        this.setState({ tableName: e.target.value })
    }

    onTableSelectTask = (value) => {
        this.loadProduceData({ taskId: value, })
        this.setState({ taskId: value })
    }

    search = () => {
        const { taskId, tableName } = this.state
        this.loadProduceData({
            tableName: tableName,
            taskId: taskId,
        })
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

    renderProduceTrend = (chartData) => {
        let myChart = echarts.init(document.getElementById('ProduceTrend'));
        const option = cloneDeep(lineAreaChartOptions);
        option.title.text = '脏数据产生趋势'
        const formateDate = function(obj) {
            return utils.formateDate(obj.value);
        }
        option.tooltip.axisPointer.label.formatter = formateDate;
        option.xAxis[0].axisLabel.formatter = formateDate;
        option.yAxis[0].minInterval = 1
        option.legend.data = chartData && chartData.type ? chartData.type.data : []
        option.xAxis[0].data =  chartData && chartData.x ? chartData.x.data : []
        option.series = this.getSeries(chartData)
        // 绘制图表
        myChart.setOption(option);
        this.setState({ lineChart: myChart })
    }

    renderProduceTop30 = () => {
        const top30 = this.state.top30
        const columns = [
            {
                title: '任务名称',
                dataIndex: 'taskName',
                key: 'taskName'
            }, {
                title: '脏数据表',
                dataIndex: 'tableName',
                key: 'tableName'
            }, {
                title: '累计产生（条）',
                dataIndex: 'totalNum',
                key: 'totalNum'
            }, {
                title: '单次执行产生最多(条)',
                dataIndex: 'maxNum',
                key: 'maxNum'
            }, {
                title: '最近1次执行产生(条)',
                dataIndex: 'recentNum',
                key: 'recentNum'
            },
        ]

        return (
            <Card title="脏数据产生TOP30任务" noHovering>
                <Table
                    rowKey="taskName"
                    pagination={false}
                    loading={this.state.loading}
                    style={{ height: '300px', }}
                    columns={columns}
                    dataSource={top30 || []}
                />
            </Card>
        )
    }

    renderProduceList = (taskOptions) => {
        const { produceList } = this.state
        const columns = [
            {
                title: '表名',
                dataIndex: 'tableName',
                key: 'tableName'
            }, {
                title: '相关任务',
                dataIndex: 'tableId',
                key: 'tableId',
                render: function(text, record) {
                    const arr = (record.tasks && record.tasks.map(task => task.name) ) || []
                    return arr.join(',');
                }
            }, {
                title: '创建者',
                dataIndex: 'userName',
                key: 'userName'
            }, {
                title: '描述',
                dataIndex: 'tableDesc',
                key: 'tableDesc'
            }, {
                title: '最近更新时间',
                dataIndex: 'lastDataChangeTime',
                key: 'lastDataChangeTime',
                render: function(text) {
                    return utils.formateDateTime(text);
                }
            }, {
                title: '占用存储',
                dataIndex: 'storeSize',
                key: 'storeSize'
            }, {
                title: '生命周期',
                dataIndex: 'lifeDay',
                key: 'lifeDay'
            }, {
                title: '操作',
                dataIndex: 'tableId',
                key: 'operation',
                render(id, record) {
                    return (
                        <span>
                            <Link to={`/data-manage/dirty-data/table/${id}`}>详情</Link>
                            <span className="ant-divider" />
                            <Link to={`/data-manage/log/${id}/${record.tableName}`}>操作记录</Link>
                        </span>
                    )
                }
            },
        ]

        const title = (
            <span>
                <Form layout="inline" 
                    style={{ marginTop: '8px' }}
                    >
                    <FormItem
                        label="选择任务"
                    >
                        <Select
                            allowClear
                            showSearch
                            style={{ width: 150 }}
                            placeholder="选择任务"
                            optionFilterProp="name"
                            onChange={this.onTableSelectTask}
                        >
                            {taskOptions}
                        </Select>
                    </FormItem>
                    <FormItem>
                        <Search
                            placeholder="按表名称搜索"
                            style={{ width: 150 }}
                            onChange={this.onTableNameChange}
                            onSearch={this.search}
                        />
                    </FormItem>
                    <FormItem>
                        <Button type="primary" onClick={this.search}>搜索</Button>
                    </FormItem>
                </Form>
            </span>
        );
        const pagination = {
            total: produceList.totalCount,
            defaultPageSize: 10,
        };
        return (
            <Card title={title}>
                <Table
                    rowKey="tableName"
                    pagination={pagination}
                    style={{minHeight: '0'}}
                    columns={columns}
                    dataSource={produceList.data || []}
                />
            </Card>
        )
    }

    render() {

        const { taskList } = this.state
       
        const taskOptions = taskList && taskList.map(option => 
            <Option 
                key={option.id} 
                name={option.taskName} 
                value={`${option.id}`}>
                {option.taskName}
            </Option>
        )

        return (
            <div className="dirty-data">
                <Row>
                    <Card title="脏数据统计" extra={
                        <RadioGroup 
                            defaultValue={3}
                            onChange={this.onTimeRangeChange}
                            style={{ marginTop: '10px' }}
                        >
                            <RadioButton value={3}>最近3天</RadioButton>
                            <RadioButton value={7}>最近7天</RadioButton>
                            <RadioButton value={30}>最近30天</RadioButton>
                            <RadioButton value={60}>最近60天</RadioButton>
                        </RadioGroup>
                    }>
                        <Row>
                            <Col span={12} style={{ paddingRight: '10px' }}>
                                <Card noHovering title="脏数据产生趋势" extra={
                                    <Select  
                                        showSearch
                                        style={{ width: 150, marginTop: '10px' }}
                                        placeholder="请选择任务"
                                        onChange={this.onTrendSelectTask}
                                    >
                                       { taskOptions }
                                    </Select>
                                }>
                                    <Resize onResize={this.resize}>
                                        <section id="ProduceTrend" style={{height: '300px'}}></section>
                                    </Resize>
                                </Card>
                            </Col>
                            <Col span={12} style={{paddingLeft: '10px'}}>
                                { this.renderProduceTop30() }
                            </Col>
                        </Row>
                    </Card>
                </Row>
                <Row style={{marginTop: '20px'}}>
                    { this.renderProduceList(taskOptions) }
                </Row>
            </div>
        )
    }
}

export default DirtyData
