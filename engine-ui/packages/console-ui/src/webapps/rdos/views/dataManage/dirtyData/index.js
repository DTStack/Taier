import React, { Component } from 'react'
import { cloneDeep } from 'lodash'
import { Link } from 'react-router'
import { connect } from 'react-redux';

import {
    Row, Col, Card, Button,
    Form, Select, Input, Table, 
    Radio,
} from 'antd'

import utils from 'utils'
import Resize from 'widgets/resize'

import Api from '../../../api'
import { lineAreaChartOptions } from '../../../comm/const'
import taskOperation from '../../operation/offline/taskOperation'

import {
    workbenchActions
} from '../../../store/modules/offlineTask/offlineAction'

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts')
// 引入柱状图
require('echarts/lib/chart/line')
// 引入提示框和标题组件
require('echarts/lib/component/tooltip')
require('echarts/lib/component/title')

const Search = Input.Search;
const FormItem = Form.Item;
const Option = Select.Option;
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;

@connect(null, (dispatch) => {
    const actions = workbenchActions(dispatch)
    return {
        goToTaskDev: (id) => {
            actions.openTaskInDev(id)
        }
    }
})
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
        loadingTop: false,
        loading: false,
        currentPage: 1,
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
        this.setState({
            loadingTop: 'loading',
        })
        Api.top30DirtyData(params).then((res) => {
            if (res.code === 1) {
                ctx.setState({ top30: res.data })
            }
            this.setState({
                loadingTop: false,
            })
        })
    }

    loadProduceData = (params) => {
        const ctx = this
        this.setState({ loading: true })
        Api.getDirtyDataTables(params).then((res) => {
            if (res.code === 1) {
                ctx.setState({ produceList: res.data })
            }
            this.setState({
                loading: false
            })
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

    search = () => {
        const { taskId, tableName, currentPage } = this.state
        this.loadProduceData({
            tableName: tableName,
            taskId: taskId,
            pageIndex: currentPage,
        })
    }

    onTableChange = (pagination, filters, sorter) => {
        this.setState({
            currentPage: pagination.current
        }, this.search)
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
        this.setState({ taskId: value })
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
       
        option.grid = {
            left: 40,
            right: 20,
            bottom: 20,
            top: 30,
        }

        option.title.text = ''
        option.tooltip.axisPointer.label.formatter = function(obj) {
            return obj ? utils.formatDate(+obj.value) : null;
        };

        option.xAxis[0].boundaryGap = ['5%', '5%'];
        option.xAxis[0].axisLabel.formatter = function(obj) {
            return obj ? utils.formatDate(obj.value) : null;
        };
        option.yAxis[0].minInterval = 1
        option.legend.data = chartData && chartData.type ? chartData.type.data : []
        option.xAxis[0].data =  chartData && chartData.x ? chartData.x.data : []
        option.series = this.getSeries(chartData)
        // 绘制图表
        myChart.setOption(option);
        this.setState({ lineChart: myChart })
    }

    renderProduceTop30 = () => {
        const { loadingTop, top30 } = this.state
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
            <Card 
                noHovering
                bordered={false}
                loading={false}
                className="shadow"
                title="脏数据产生TOP30任务"
            >
                <Table
                    rowKey="taskName"
                    pagination={false}
                    loading={ loadingTop }
                    style={{ height: '300px', }}
                    columns={ columns }
                    dataSource={ top30 || [] }
                />
            </Card>
        )
    }

    renderProduceList = (taskOptions) => {
        const { produceList, loading } = this.state
        const ctx = this;
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
                    const arr = (record.tasks && record.tasks.map(task => 
                        task.isDeleted === 1 ? 
                        `${task.name} (已删除)` :
                        <a onClick={
                            () => {
                                ctx.props.goToTaskDev(task.id)
                            }
                        }>{task.name}</a>) 
                    ) || []
                    return arr;
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
                    return utils.formatDateTime(text);
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
            <Form layout="inline" 
                className="m-form-inline"
                style={{ marginTop: '10px' }}
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
                        size="default"
                        onChange={ this.onTableNameChange }
                        onSearch={ this.search }
                    />
                </FormItem>
                <FormItem>
                    <Button type="primary" size="default" onClick={this.search}>搜索</Button>
                </FormItem>
            </Form>
        );

        const pagination = {
            total: produceList.totalCount,
            defaultPageSize: 10,
        };

        return (
            <Card title={title} 
                noHovering
                bordered={false}
                loading={false}
                className="shadow"
            >
                <Table
                    rowKey="tableName"
                    className="m-table"
                    pagination={pagination}
                    style={{minHeight: '0'}}
                    loading={ loading }
                    columns={columns}
                    onChange={this.onTableChange}
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
            <div className="dirty-data m-card">
                <h1 className="box-title">
                    脏数据统计
                    <span className="right">
                        <RadioGroup 
                            defaultValue={3}
                            onChange={this.onTimeRangeChange}
                            style={{ marginTop: '8.5px' }}
                        >
                            <RadioButton value={3}>最近3天</RadioButton>
                            <RadioButton value={7}>最近7天</RadioButton>
                            <RadioButton value={30}>最近30天</RadioButton>
                            <RadioButton value={60}>最近60天</RadioButton>
                        </RadioGroup>
                    </span>
                </h1>
                <Row style={{margin: '0 20px'}}>
                    <Col span={12} style={{ paddingRight: '10px' }}>
                        <Card className="shadow" noHovering bordered={false} title="脏数据产生趋势" extra={
                            <Select  
                                allowClear
                                showSearch
                                style={{ width: 150, marginTop: '10px' }}
                                placeholder="请选择任务"
                                onChange={this.onTrendSelectTask}
                                optionFilterProp="name"
                            >
                                { taskOptions }
                            </Select>
                        }>
                            <Resize onResize={this.resize}>
                                <section id="ProduceTrend" style={{height: '300px', padding: '0 20px 20px 20px'}}></section>
                            </Resize>
                        </Card>
                    </Col>
                    <Col span={12} style={{paddingLeft: '10px'}}>
                        { this.renderProduceTop30() }
                    </Col>
                </Row>
                <Row style={{margin: '20px'}}>
                    { this.renderProduceList(taskOptions) }
                </Row>
            </div>
        )
    }
}

export default DirtyData
