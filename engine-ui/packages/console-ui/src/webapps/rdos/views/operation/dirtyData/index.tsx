import * as React from 'react';
import { cloneDeep } from 'lodash';
import { Link } from 'react-router';
import { connect } from 'react-redux';

import {
    Row,
    Col,
    Card,
    Button,
    Form,
    Select,
    Input,
    Table,
    Radio
} from 'antd';

import utils from 'utils';
import Resize from 'widgets/resize';

import Api from '../../../api/dataManage';
import { lineAreaChartOptions } from '../../../comm/const';
import TableLog from '../../dataManage/tableLog';
import SlidePane from 'widgets/slidePane';

import { workbenchActions } from '../../../store/modules/offlineTask/offlineAction';

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

const Search = Input.Search;
const FormItem = Form.Item;
const Option: any = Select.Option;
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;

const TIME_OBJ: any = {
    3: {
        interval: 2,
        field: 1,
        formatter: function (time: any) {
            return utils.formatDateHours(time);
        }
    },
    7: {
        interval: 3,
        field: 1,
        formatter: function (time: any) {
            return utils.formatDateHours(time);
        }
    },
    30: {
        interval: 1,
        field: 2,
        formatter: function (time: any) {
            return utils.formatDate(time);
        }
    },
    60: {
        interval: 1,
        field: 2,
        formatter: function (time: any) {
            return utils.formatDate(time);
        }
    }
};

@(connect(
    (state: any) => {
        return {
            project: state.project
        };
    },
    (dispatch: any) => {
        const actions = workbenchActions(dispatch);
        return {
            goToTaskDev: (id: any) => {
                actions.openTaskInDev(id);
            }
        };
    }
) as any)
class DirtyData extends React.Component<any, any> {
    state: any = {
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
        tableLog: {
            tableId: undefined,
            tableName: undefined,
            visible: false
        },
        editRecord: {}
    };

    componentDidMount () {
        this.loadProduceTrendData({
            recent: 3
        });
        this.loadProduceTop30({
            recent: 3,
            topN: 30
        });
        this.loadProduceData();
        this.loadSyncTasks();
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps(nextProps: any) {
        const project = nextProps.project;
        const oldProj = this.props.project;
        if (oldProj && project && oldProj.id !== project.id) {
            this.setState({
                taskId: null
            }, () => {
                this.loadProduceTrendData({
                    recent: 3
                });
                this.loadProduceTop30({
                    recent: 3,
                    topN: 30
                });
                this.loadProduceData();
                this.loadSyncTasks();
            })
        }
    }

    resize = () => {
        if (this.state.lineChart) this.state.lineChart.resize();
    };

    loadProduceTrendData = (params: any) => {
        const time = params.recent;
        params.interval = TIME_OBJ[time].interval;
        params.field = TIME_OBJ[time].field;

        Api.getDirtyDataTrend(params).then((res: any) => {
            if (res.code === 1) {
                this.renderProduceTrend(res.data);
            }
        });
    };

    loadProduceTop30 = (params: any) => {
        const ctx = this;
        this.setState({
            loadingTop: true
        });
        Api.top30DirtyData(params).then((res: any) => {
            if (res.code === 1) {
                ctx.setState({ top30: res.data });
            }
            this.setState({
                loadingTop: false
            });
        });
    };

    loadProduceData = (params?: any) => {
        const ctx = this;
        this.setState({ loading: true });
        Api.getDirtyDataTables(params).then((res: any) => {
            if (res.code === 1) {
                ctx.setState({ produceList: res.data });
            }
            this.setState({
                loading: false
            });
        });
    };

    loadSyncTasks = (params?: any) => {
        const ctx = this;
        Api.getPubSyncTask(params).then((res: any) => {
            if (res.code === 1) {
                ctx.setState({ taskList: res.data });
            }
        });
    };

    search = () => {
        const { taskId, tableName, currentPage } = this.state;
        this.loadProduceData({
            tableName: tableName,
            taskId: taskId,
            pageIndex: currentPage
        });
    };

    onTableChange = (pagination: any, filters: any, sorter: any) => {
        this.setState(
            {
                currentPage: pagination.current
            },
            this.search
        );
    };

    onTimeRangeChange = (e: any) => {
        const value = e.target.value;
        this.setState({ timeRange: value });
        this.loadProduceTrendData({
            recent: value
        });
        this.loadProduceTop30({
            recent: value,
            topN: 30
        });
    };

    onTrendSelectTask = (value: any) => {
        const { timeRange } = this.state;
        this.loadProduceTrendData({
            taskId: value,
            recent: timeRange
        });
    };

    onTableNameChange = (e: any) => {
        this.setState({ tableName: e.target.value, currentPage: 1 });
    };

    onTableSelectTask = (value: any) => {
        this.setState({ taskId: value, currentPage: 1 });
    };

    getSeries = (data: any) => {
        const arr: any = [];
        if (data && data.y) {
            const legend = data && data.type ? data.type.data : ['无'];
            for (let i = 0; i < legend.length; i++) {
                arr.push({
                    name: legend[i],

                    type: 'line',
                    data: data.y[i].data
                });
            }
        } else {
            arr.push({
                name: '无',
                type: 'line',
                data: [0]
            });
        }
        return arr;
    };

    renderProduceTrend = (chartData: any) => {
        let myChart = echarts.init(document.getElementById('ProduceTrend'));
        const option = cloneDeep(lineAreaChartOptions);
        const { timeRange } = this.state;

        option.grid = {
            left: '2%',
            right: '8%',
            bottom: '2%',
            containLabel: true
        };
        option.legend.show = false;
        option.title.text = '';
        option.tooltip.axisPointer.label.formatter = function (obj: any) {
            return obj ? TIME_OBJ[timeRange].formatter(+obj.value) : null;
        };

        option.xAxis[0].boundaryGap = ['5%', '5%'];
        option.xAxis[0].axisLabel.formatter = function (value: any) {
            return value ? TIME_OBJ[timeRange].formatter(+value) : null;
        };

        option.yAxis[0].minInterval = 1;
        option.legend.data = chartData && chartData.type ? chartData.type.data : ['无'];
        option.xAxis[0].data = chartData && chartData.x ? chartData.x.data : [(new Date()).getTime()];
        option.series = this.getSeries(chartData);
        // 绘制图表
        console.log(option);
        myChart.setOption(option, true);
        this.setState({ lineChart: myChart });
    };

    renderProduceTop30 = () => {
        const { loadingTop, top30 } = this.state;
        const columns: any = [
            {
                title: '任务名称',
                width: '100px',
                dataIndex: 'taskName',
                key: 'taskName'
            },
            {
                title: '脏数据表',
                dataIndex: 'tableName',
                key: 'tableName',
                width: '100px'
            },
            {
                title: '累计产生（条）',
                dataIndex: 'totalNum',
                key: 'totalNum',
                width: '100px'
            },
            {
                title: '单次执行产生最多(条)',
                dataIndex: 'maxNum',
                key: 'maxNum',
                width: '100px'
            },
            {
                title: '最近1次执行产生(条)',
                dataIndex: 'recentNum',
                key: 'recentNum',
                width: '100px'
            }
        ];

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
                    className="full-screen-table-50 dirt-table-header_rmscroll"
                    pagination={false}
                    loading={loadingTop}
                    columns={columns}
                    dataSource={top30 || []}
                    scroll={{ y: '251px' }}
                />
            </Card>
        );
    };

    showTableLog (table: any) {
        const { id, tableName } = table;
        const { tableLog } = this.state;
        tableLog.tableId = id;
        tableLog.tableName = tableName;
        tableLog.visible = true;
        this.setState({
            tableLog,
            editRecord: table
        });
    }

    closeSlidePane = () => {
        const { tableLog } = this.state;
        tableLog.visible = false;
        tableLog.tableId = undefined;
        tableLog.tableName = undefined;
        this.setState({
            tableLog,
            editRecord: {}
        });
    };

    renderProduceList = (taskOptions: any) => {
        const { produceList, loading, currentPage, taskId } = this.state;
        const ctx = this;
        const columns: any = [
            {
                title: '表名',
                dataIndex: 'tableName',
                key: 'tableName'
            },
            {
                title: '相关任务',
                dataIndex: 'tableId',
                key: 'tableId',
                render: function (text: any, record: any) {
                    const arr =
                        (record.tasks &&
                            record.tasks.map(
                                (task: any, index: any) =>
                                    task.isDeleted === 1 ? (
                                        `${task.name} (已删除)`
                                    ) : (
                                        <span>
                                            <a
                                                onClick={() => {
                                                    ctx.props.goToTaskDev(task.id);
                                                }}
                                            >
                                                {task.name}
                                            </a>
                                            {index < record.tasks.length - 1 ? '，' : ''}
                                        </span>
                                    )
                            )) ||
                        [];
                    return arr;
                }
            },
            {
                title: '责任人',
                dataIndex: 'chargeUser',
                key: 'chargeUser'
            },
            {
                title: '描述',
                dataIndex: 'tableDesc',
                key: 'tableDesc'
            },
            {
                title: '最近更新时间',
                dataIndex: 'gmtModified',
                key: 'gmtModified',
                render: function (text: any) {
                    return utils.formatDateTime(text);
                }
            },
            {
                title: '占用存储',
                dataIndex: 'tableSize',
                key: 'tableSize'
            },
            {
                title: '生命周期',
                dataIndex: 'lifeDay',
                key: 'lifeDay'
            },
            {
                title: '操作',
                dataIndex: 'id',
                key: 'operation',
                render (id: any, record: any) {
                    return (
                        <span>
                            <Link to={`/operation/dirty-data/table/${id}`}>
                                详情
                            </Link>
                            <span className="ant-divider" />
                            <a
                                href="javascript:void(0)"
                                onClick={ctx.showTableLog.bind(ctx, record)}
                            >
                                操作记录
                            </a>
                        </span>
                    );
                }
            }
        ];

        const title = (
            <Form
                layout="inline"
                className="m-form-inline"
            >
                <FormItem label="选择任务">
                    <Select
                        allowClear
                        showSearch
                        style={{ width: '126px' }}
                        placeholder="选择任务"
                        optionFilterProp="name"
                        onChange={this.onTableSelectTask}
                        value={taskId}
                    >
                        {taskOptions}
                    </Select>
                </FormItem>
                <FormItem>
                    <Search
                        placeholder="按表名称搜索"
                        style={{ width: '200px' }}
                        size="default"
                        onChange={this.onTableNameChange}
                        onSearch={this.search}
                    />
                </FormItem>
                <FormItem>
                    <Button type="primary" size="default" onClick={this.search}>
                        搜索
                    </Button>
                </FormItem>
            </Form>
        );

        const pagination: any = {
            total: produceList.totalCount,
            defaultPageSize: 10,
            current: currentPage
        };

        return (
            <Card
                title={title}
                noHovering
                bordered={false}
                loading={false}
                className="shadow"
            >
                <Table
                    rowKey="tableName"
                    className="dt-ant-table dt-ant-table--border rdos-ant-table-placeholder"
                    pagination={pagination}
                    style={{ minHeight: '0', height: 'calc(100% - 482px)' }}
                    loading={loading}
                    columns={columns}
                    onChange={this.onTableChange}
                    dataSource={produceList.data || []}
                />
            </Card>
        );
    };

    render () {
        const { taskList, tableLog, editRecord } = this.state;
        const projectUsers: any = [];
        const taskOptions =
            taskList &&
            taskList.map((option: any) => (
                <Option
                    key={option.id}
                    name={option.taskName}
                    title={option.taskName}
                    value={`${option.id}`}
                >
                    {option.taskName}
                </Option>
            ));

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
                <Row style={{ margin: '8px 20px 0', height: '350px' }}>
                    <Col span={12} style={{ paddingRight: '10px' }}>
                        <Card
                            className="shadow"
                            noHovering
                            bordered={false}
                            title="脏数据产生趋势"
                            extra={
                                <Select
                                    allowClear
                                    showSearch
                                    style={{ width: '150px', marginTop: '10px' }}
                                    placeholder="请选择任务"
                                    onChange={this.onTrendSelectTask}
                                    optionFilterProp="name"
                                >
                                    {taskOptions}
                                </Select>
                            }
                        >
                            <Resize onResize={this.resize}>
                                <section
                                    id="ProduceTrend"
                                    style={{
                                        height: '300px',
                                        padding: '0 20px 20px 20px'
                                    }}
                                />
                            </Resize>
                        </Card>
                    </Col>
                    <Col span={12} style={{ paddingLeft: '10px' }}>
                        {this.renderProduceTop30()}
                    </Col>
                </Row>
                <Row style={{ margin: '20px' }}>
                    {this.renderProduceList(taskOptions)}
                </Row>
                {tableLog.visible ? (
                    <SlidePane
                        onClose={this.closeSlidePane}
                        visible={tableLog.visible}
                        className="full-screen-table-60"
                        style={{
                            right: '-20px',
                            width: '80%',
                            marginTop: '43px'
                        }}
                    >
                        <div className="m-loglist">
                            <TableLog
                                key={tableLog.tableId}
                                {...tableLog}
                                projectUsers={projectUsers}
                                editRecord={editRecord}
                            />
                        </div>
                    </SlidePane>
                ) : (
                    ''
                )}
            </div>
        );
    }
}

export default DirtyData;
