import * as React from 'react';
import { connect } from 'react-redux';
import {
    Card,
    Checkbox,
    DatePicker,
    Input,
    Select,
    Table,
    Tabs,
    Tooltip,
    Icon,
    Modal
} from 'antd';
import moment from 'moment';

import utils from 'utils';
import SlidePane from 'widgets/slidePane';
import TaskDetailPane from './taskDetailPane';
import TaskTablePane from './taskTablePane';

import { TaskStatus } from '../../components/display';
import {
    TASK_STATUS,
    TRIG_MODE_TEXT,
    TRIG_MODE,
    taskStatusFilter
} from '../../consts';
import { taskQueryActions } from '../../actions/taskQuery';
import { dataSourceActions } from '../../actions/dataSource';

const Search = Input.Search;
const Option = Select.Option;
const TabPane = Tabs.TabPane;
const RangePicker = DatePicker.RangePicker;

const mapStateToProps = (state: any) => {
    const { taskQuery, dataSource, common, project } = state;
    return { taskQuery, dataSource, common, project };
};

const mapDispatchToProps = (dispatch: any) => ({
    getTaskList (params: any) {
        dispatch(taskQueryActions.getTaskList(params));
    },
    getDataSourcesList (params: any) {
        dispatch(dataSourceActions.getDataSourcesList(params));
    }
});

@(connect(
    mapStateToProps,
    mapDispatchToProps
) as any)
class TaskQuery extends React.Component<any, any> {
    state: any = {
        params: {
            currentPage: 1,
            pageSize: 20,
            fuzzyName: utils.getParameterByName('tb') || undefined,
            configureId: undefined,
            dataSourceId: undefined,
            dataSourceType: utils.getParameterByName('source') || undefined,
            subscribe: undefined,
            executeStartTime:
                utils.getParameterByName('startTime') || undefined,
            executeEndTime: utils.getParameterByName('endTime') || undefined,
            bizTime: 0,
            statusFilter: utils.getParameterByName('statusFilter') || ''
        },
        tabKey: '1',
        showSlidePane: false,
        currentTask: {},
        visibleList: []
    };

    componentDidMount () {
        this.props.getTaskList(this.state.params);
        this.props.getDataSourcesList();
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps: any) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.props.getTaskList(this.state.params);
            this.props.getDataSourcesList();
        }
    }

    renderLogInfo = (status: any, record: any) => {
        const { visibleList } = this.state;
        let title: React.ReactNode = '';
        let icon = 'close-circle-o';
        let preStyle: any = { whiteSpace: 'pre-wrap', wordBreak: 'break-all' };
        if (status === TASK_STATUS.FAIL) {
            title = (
                <a
                    className="tooltip_content_a"
                    onClick={this.showDetailLogInfo.bind(
                        this,
                        record.id,
                        record.tableName,
                        record.logInfo
                    )}
                >
                    <pre style={preStyle}>{record.logInfo || '空'}</pre>
                </a>
            );
        } else if (status === TASK_STATUS.UNPASS) {
            title = (
                <span className="tooltip_content_a">
                    <pre style={preStyle}>{record.logInfo || '空'}</pre>&nbsp;
                    <a onClick={this.openSlidePane.bind(this, record)}>
                        查看详情
                    </a>
                </span>
            );
            icon = 'exclamation-circle-o';
        } else {
            return null;
        }
        return (
            <Tooltip
                placement="right"
                visible={visibleList.indexOf(record.id) > -1}
                onVisibleChange={this.tooltipChange.bind(this, record.id)}
                title={title}
                overlayStyle={{ wordBreak: 'break-word' }}
            >
                <Icon className="font-14" type={icon} />
            </Tooltip>
        );
    };

    // table设置
    initColumns = () => {
        const { params } = this.state;
        return [
            {
                title: '表',
                dataIndex: 'tableName',
                key: 'tableName',
                render: (text: any, record: any) => (
                    <a onClick={this.openSlidePane.bind(this, record)}>
                        {text}
                    </a>
                ),
                width: '15%'
            },
            {
                title: '分区',
                dataIndex: 'partationValue',
                key: 'partationValue',
                render: (text: any, record: any) => {
                    return text || '--';
                },
                width: '15%'
            },
            {
                title: '状态',
                width: '10%',
                dataIndex: 'status',
                key: 'status',
                render: (status: any, record: any) => {
                    return (
                        <div>
                            <TaskStatus
                                style={{
                                    display: 'inline-block',
                                    width: '80px'
                                }}
                                value={status}
                            />
                            {this.renderLogInfo(status, record)}
                        </div>
                    );
                },
                filters: taskStatusFilter,
                filteredValue: params.statusFilter
                    ? params.statusFilter.split(',')
                    : []
            },
            {
                title: '规则异常数',
                dataIndex: 'alarmSum',
                key: 'alarmSum',
                width: '8%',
                sorter: true,
                render: (text: any) => {
                    return text == null ? '-' : text;
                }
            },
            {
                title: '数据源',
                dataIndex: 'sourceTypeValue',
                key: 'sourceTypeValue',
                render: (text: any, record: any) => {
                    return text ? `${text} / ${record.sourceName}` : '--';
                },
                width: '15%'
            },
            {
                title: '触发方式',
                dataIndex: 'trigMode',
                key: 'trigMode',
                width: '80px',
                render (text: any) {
                    return TRIG_MODE_TEXT[text];
                },
                filters: [
                    {
                        text: TRIG_MODE_TEXT[TRIG_MODE.LOOP],
                        value: TRIG_MODE.LOOP
                    },
                    {
                        text: TRIG_MODE_TEXT[TRIG_MODE.HAND],
                        value: TRIG_MODE.HAND
                    }
                ],
                filterMultiple: false
            },
            {
                title: '配置人',
                dataIndex: 'configureUserName',
                key: 'configureUserName',
                width: '150px'
            },
            {
                title: '执行时间',
                dataIndex: 'executeTime',
                key: 'executeTime',
                render: (text: any) => {
                    return text
                        ? moment(text).format('YYYY-MM-DD HH:mm:ss')
                        : '--';
                },
                width: '12%',
                sorter: true
            }
        ];
    };

    // 表格换页/排序
    onTableChange = (page: any, filter: any, sorter: any) => {
        let { field, order } = sorter;
        let params: any = {
            ...this.state.params,
            currentPage: page.current,
            statusFilter:
                filter.status && filter.status.length > 0
                    ? filter.status.join(',')
                    : undefined,
            alarmSort: undefined,
            trigMode:
                filter.trigMode && filter.trigMode.length > 0
                    ? filter.trigMode.join(',')
                    : undefined,
            executeTimeSort: undefined
        };

        if (field) {
            params[field === 'alarmSum' ? 'alarmSort' : 'executeTimeSort'] =
                order === 'descend' ? 'desc' : 'asc';
        } else {
            params.alarmSort = params.executeTimeSort = undefined;
        }

        this.props.getTaskList(params);
        this.setState({ params });
    };

    // 数据源类型下拉框
    renderSourceType = (data: any) => {
        return data.map((source: any) => {
            return (
                <Option key={source.value} value={source.value.toString()}>
                    {source.name}
                </Option>
            );
        });
    };

    // 数据源类型筛选
    onSourceChange = (type: any) => {
        let params: any = {
            ...this.state.params,
            currentPage: 1,
            dataSourceType: type || undefined
        };

        this.props.getTaskList(params);
        this.setState({ params });
    };

    // 数据源下拉框
    renderUserSource = (data: any) => {
        return data.map((source: any) => {
            let title = `${source.dataName}（${source.sourceTypeValue}）`;
            return (
                <Option
                    key={source.id}
                    value={source.id.toString()}
                    title={title}
                >
                    {title}
                </Option>
            );
        });
    };

    // 数据源筛选
    onUserSourceChange = (id: any) => {
        let params: any = {
            ...this.state.params,
            currentPage: 1,
            dataSourceId: id || undefined
        };

        this.props.getTaskList(params);
        this.setState({ params });
    };

    // 配置人下拉框
    renderUserList = (data: any) => {
        return data.map((item: any) => {
            return (
                <Option
                    key={item.id}
                    value={item.id.toString()}
                    {...{ name: item.userName }}
                >
                    {item.userName}
                </Option>
            );
        });
    };

    // 配置人筛选
    onUserChange = (id: any) => {
        let params: any = {
            ...this.state.params,
            currentPage: 1,
            configureId: id || undefined
        };

        this.props.getTaskList(params);
        this.setState({ params });
    };

    // 执行时间筛选
    onExecuteTimeChange = (date: any, dateString: any) => {
        let params: any = {
            ...this.state.params,
            currentPage: 1,
            executeStartTime: date && date[0] ? date[0].valueOf() : null,
            executeEndTime: date && date[1] ? date[1].valueOf() : null
        };

        this.props.getTaskList(params);
        this.setState({ params });
    };

    // 是否订阅筛选
    onSubscribeChange = (e: any) => {
        let params: any = {
            ...this.state.params,
            currentPage: 1,
            subscribe: e.target.checked ? true : undefined
        };

        this.props.getTaskList(params);
        this.setState({ params });
    };

    // table搜索
    onTableSearch = (name: any) => {
        this.props.getTaskList(this.state.params);
    };

    onTableNameChange = (e: any) => {
        let params: any = {
            ...this.state.params,
            currentPage: 1,
            fuzzyName: e.target.value || undefined
        };
        this.setState({ params });
    }

    showDetailLogInfo (id: any, tableName: any, info: any) {
        this.tooltipChange(id, false);
        Modal.error({
            width: '70%',
            title: '表: ' + tableName,
            content: (
                <pre
                    style={{ wordBreak: 'break-word', whiteSpace: 'pre-wrap' }}
                >
                    {info}
                </pre>
            )
        });
    }

    tooltipChange (id: any, show: any) {
        const { visibleList } = this.state;
        if (show) {
            this.setState({
                visibleList: visibleList.concat(id)
            });
        } else {
            this.setState({
                visibleList: visibleList.filter((itemId: any) => {
                    return id != itemId;
                })
            });
        }
    }
    openSlidePane = (record: any) => {
        this.setState({
            showSlidePane: true,
            currentTask: record
        });
    };

    closeSlidePane = () => {
        this.setState({
            showSlidePane: false,
            currentTask: {},
            tabKey: '1'
        });
    };

    onTabChange = (key: any) => {
        this.setState({ tabKey: key });
    };

    disabledDate = (current: any) => {
        return current && current.valueOf() > Date.now();
    };

    render () {
        const { dataSource, taskQuery, common } = this.props;
        const { sourceType, sourceList } = dataSource;
        const { userList } = common;
        const { loading, taskList } = taskQuery;
        const { params, showSlidePane, tabKey, currentTask } = this.state;
        const { executeStartTime, executeEndTime } = params;
        const pagination: any = {
            current: params.currentPage,
            pageSize: params.pageSize,
            total: taskList.totalCount
        };

        let defaultRangeValue: any;

        if (executeStartTime && executeEndTime) {
            defaultRangeValue = [
                moment(parseInt(executeStartTime)),
                moment(parseInt(executeEndTime))
            ];
        }

        const cardTitle = (
            <div className="flex font-12">
                <Search
                    placeholder="输入表名搜索"
                    onSearch={this.onTableSearch}
                    onChange={this.onTableNameChange}
                    defaultValue={params.fuzzyName}
                    style={{ width: 200 }}
                />

                <div className="m-l-8">
                    类型：
                    <Select
                        allowClear
                        style={{ width: 150 }}
                        value={params.dataSourceType}
                        placeholder="选择数据源类型"
                        onChange={this.onSourceChange}
                    >
                        {this.renderSourceType(sourceType)}
                    </Select>
                </div>

                <div className="m-l-8">
                    数据源：
                    <Select
                        allowClear
                        showSearch
                        style={{ width: 150 }}
                        optionFilterProp="title"
                        placeholder="选择数据源"
                        onChange={this.onUserSourceChange}
                    >
                        {this.renderUserSource(sourceList)}
                    </Select>
                </div>

                <div className="m-l-8">
                    执行时间：
                    <RangePicker
                        defaultValue={defaultRangeValue}
                        format="YYYY-MM-DD"
                        placeholder={['开始日期', '结束日期']}
                        style={{
                            width: 200,
                            verticalAlign: 'middle',
                            marginTop: '-1px'
                        }}
                        disabledDate={this.disabledDate}
                        onChange={this.onExecuteTimeChange}
                    />
                </div>

                <div className="m-8">
                    配置人：
                    <Select
                        allowClear
                        showSearch
                        style={{ width: 150 }}
                        placeholder="选择配置人"
                        optionFilterProp="name"
                        onChange={this.onUserChange}
                    >
                        {this.renderUserList(userList)}
                    </Select>
                </div>

                <div className="m-l-8">
                    <Checkbox onChange={this.onSubscribeChange}>
                        我订阅的表
                    </Checkbox>
                </div>
            </div>
        );

        return (
            <div className="task-dashboard">
                <h1 className="box-title">
                    任务查询{' '}
                    <span
                        style={{
                            fontSize: '12px',
                            color: 'rgb(153, 153, 153)'
                        }}
                    >
                        告警总数:{' '}
                        {taskList.data && taskList.data[0]
                            ? taskList.data[0].allAlarmSum
                            : 0}
                    </span>
                </h1>

                <div className="box-2 m-card shadow">
                    <Card
                        title={cardTitle}
                        extra={
                            <Tooltip title="刷新数据">
                                <Icon
                                    type="sync"
                                    onClick={() => {
                                        this.props.getTaskList(params);
                                    }}
                                    style={{
                                        cursor: 'pointer',
                                        marginTop: '18px',
                                        color: '#94A8C6'
                                    }}
                                />
                            </Tooltip>
                        }
                        noHovering
                        bordered={false}
                    >
                        <Table
                            rowClassName={(record: any, index: any) => {
                                if (
                                    currentTask &&
                                    currentTask.id == record.id
                                ) {
                                    return 'row-select';
                                } else {
                                    return '';
                                }
                            }}
                            rowKey="id"
                            className="m-table"
                            columns={this.initColumns()}
                            loading={loading}
                            pagination={pagination}
                            dataSource={taskList.data}
                            onChange={this.onTableChange}
                        />
                    </Card>
                </div>
                <SlidePane
                    onClose={this.closeSlidePane}
                    visible={showSlidePane}
                    style={{
                        right: '0',
                        top: '45px',
                        width: '80%',
                        minHeight: '600px',
                        height: 'calc(100% - 45px)'
                    }}
                >
                    <div className="m-tabs c-tabs--scroll">
                        <Tabs
                            animated={false}
                            activeKey={tabKey}
                            onChange={this.onTabChange}
                        >
                            <TabPane tab="监控报告" key="1" style={{
                                overflow: 'auto',
                                position: 'absolute',
                                top: '36px',
                                bottom: '0px'
                            }}>
                                <TaskDetailPane
                                    currentTab={tabKey}
                                    data={currentTask}
                                />
                            </TabPane>
                            <TabPane tab="表级报告" key="2">
                                <TaskTablePane
                                    currentTab={tabKey}
                                    data={currentTask}
                                />
                            </TabPane>
                        </Tabs>
                    </div>
                </SlidePane>
            </div>
        );
    }
}

export default TaskQuery;
