import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Card, Checkbox, DatePicker, Input, Select, Table, Tabs, Tooltip, Icon } from 'antd';
import moment from 'moment';

import utils from 'utils';
import SlidePane from 'widgets/slidePane';
import TaskDetailPane from './taskDetailPane';
import TaskTablePane from './taskTablePane';

import { TaskStatus } from '../../components/display';
import { taskStatusFilter } from '../../consts';
import { taskQueryActions } from '../../actions/taskQuery';
import { dataSourceActions } from '../../actions/dataSource';

const Search = Input.Search;
const Option = Select.Option;
const TabPane = Tabs.TabPane;

const mapStateToProps = state => {
    const { taskQuery, dataSource, common } = state;
    return { taskQuery, dataSource, common }
};

const mapDispatchToProps = dispatch => ({
	getTaskList(params) {
		dispatch(taskQueryActions.getTaskList(params));
	},
    getDataSourcesList(params) {
        dispatch(dataSourceActions.getDataSourcesList(params));
    },
});

@connect(mapStateToProps, mapDispatchToProps)
export default class TaskQuery extends Component {

    state = {
        params: {
            currentPage: 1,
            pageSize: 20,
            fuzzyName: utils.getParameterByName('tb') || undefined,
            configureId: undefined,
            dataSourceId: undefined,
            dataSourceType: utils.getParameterByName('source') || undefined,
            subscribe: undefined,
            executeTime: 0,
            bizTime: 0
        },
        tabKey: '1',
        showSlidePane: false,
        currentTask: {},
    }

    componentDidMount() {
        this.props.getDataSourcesList();
        this.props.getTaskList(this.state.params);
    }

    // table设置
    initColumns = () => {
        return [{
            title: '表',
            dataIndex: 'tableName',
            key: 'tableName',
            render: (text, record) => (
                <a onClick={this.openSlidePane.bind(this, record)}>{text}</a>
            ),
            width: '15%'
        }, {
            title: '分区',
            dataIndex: 'partationValue',
            key: 'partationValue',
            render: (text, record) => {
                return text ? text : '--';
            },
            width: '15%'
        }, {
            title: '状态',
            width: '10%',
            dataIndex: 'status',
            key: 'status',
            render: (text, record) => {
                return <div>
                    <TaskStatus style={{ marginRight: 30 }} value={text} />
                    {
                        text === 2 
                        &&
                        <Tooltip 
                            placement="right" 
                            title={record.logInfo}
                            overlayStyle={{ wordBreak: 'break-word' }}
                        >
                            <Icon className="font-14" type="info-circle-o" />
                        </Tooltip>
                    }
                    </div>
            },
            filters: taskStatusFilter,
        }, {
            title: '规则异常数',
            dataIndex: 'alarmSum',
            key: 'alarmSum',
            width: '8%',
            // sorter: true
        }, {
            title: '类型',
            dataIndex: 'sourceTypeValue',
            key: 'sourceTypeValue',
            render: (text, record) => {
                return text ? `${text} / ${record.sourceName}` : '--';
            },
            width: '15%'
        }, {
            title: '配置人',
            dataIndex: 'configureUserName',
            key: 'configureUserName',
            width: '12%'
        }, 
        {
            title: '执行时间',
            dataIndex: 'executeTime',
            key: 'executeTime',
            render: (text) => {
                return text ? moment(text).format("YYYY-MM-DD HH:mm:ss") : '--';
            },
            width: '12%',
            sorter: true
        }]
    }

    // 表格换页/排序
    onTableChange = (page, filter, sorter) => {
        let { field, order } = sorter;
        let params = {
            ...this.state.params,
            currentPage: page.current,
            statusFilter: filter.status && filter.status.length > 0 ? filter.status.join(',') : undefined,
            alarmSumSort: undefined,
            executeTimeSort: undefined,
        };

        if (field) {
            params[
                field === 'alarmSum' ? 'alarmSumSort' : 'executeTimeSort'
            ] = order === 'descend' ? 'desc' : 'asc';
        }

        this.props.getTaskList(params);
        this.setState({ params });
    }

    // 数据源类型下拉框
    renderSourceType = (data) => {
        return data.map((source) => {
            return (
                <Option 
                    key={source.value} 
                    value={source.value.toString()}>
                    {source.name}
                </Option>
            )
        });
    }

    // 数据源类型筛选
    onSourceChange = (type) => {
        let params = {
            ...this.state.params, 
            currentPage: 1,
            dataSourceType: type ? type : undefined
        };
        
        this.props.getTaskList(params);
        this.setState({ params });
    }

    // 数据源下拉框
    renderUserSource = (data) => {
        return data.map((source) => {
            let title = `${source.dataName}（${source.sourceTypeValue}）`;
            return (
                <Option 
                    key={source.id} 
                    value={source.id.toString()}
                    title={title}>
                    {title}
                </Option>
            )
        });
    }

    // 数据源筛选
    onUserSourceChange = (id) => {
        let params = {
            ...this.state.params, 
            currentPage: 1,
            dataSourceId: id ? id : undefined
        };
        
        this.props.getTaskList(params);
        this.setState({ params });
    }

    // 配置人下拉框
    renderUserList = (data) => {
        return data.map((item) => {
            return (
                <Option 
                    key={item.id} 
                    value={item.id.toString()}
                    name={item.userName}>
                    {item.userName}
                </Option>
            )
        })
    }

    // 配置人筛选
    onUserChange = (id) => {
        let params = {
            ...this.state.params, 
            currentPage: 1,
            configureId: id ? id : undefined
        };
        
        this.props.getTaskList(params);
        this.setState({ params });
    }

    // 执行时间筛选
    onExecuteTimeChange = (date, dateString) => {
        let params = {
            ...this.state.params, 
            currentPage: 1,
            executeTime: date ? date.valueOf() : 0
        };
        
        this.props.getTaskList(params);
        this.setState({ params });
    }

    // 是否订阅筛选
    onSubscribeChange = (e) => {
        let params = {
            ...this.state.params, 
            currentPage: 1,
            subscribe: e.target.checked ? true : undefined
        };
        
        this.props.getTaskList(params);
        this.setState({ params });
    }

    // table搜索
    onTableSearch = (name) => {
        let params = {
            ...this.state.params, 
            currentPage: 1,
            fuzzyName: name ? name : undefined
        };

        this.props.getTaskList(params);
        this.setState({ params });
    }

    openSlidePane = (record) => {
        this.setState({
            showSlidePane: true,
            currentTask: record
        })
    }

    closeSlidePane = () => {
        this.setState({
            showSlidePane: false,
            currentTask: {},
            tabKey: '1'
        })
    }

    onTabChange = (key) => {
        this.setState({ tabKey: key });
    }

    disabledDate = (current) => {
        return current && current.valueOf() > Date.now();
    }

    render() {
    	const { dataSource, taskQuery, common } = this.props;
        const { sourceType, sourceList } = dataSource;
        const { userList } = common;
        const { loading, taskList } = taskQuery;
        const { params, showSlidePane, tabKey, currentTask } = this.state;

        const pagination = {
            current: params.currentPage,
            pageSize: params.pageSize,
            total: taskList.totalCount,
        };

        const cardTitle = (
            <div className="flex font-12">
                <Search
                    placeholder="输入表名搜索"
                    onSearch={this.onTableSearch}
                    defaultValue={params.fuzzyName}
                    style={{ width: 200, margin: '10px 0' }}
                />

                <div className="m-l-8">
                    类型：
                    <Select 
                        allowClear
                        style={{ width: 150 }}
                        value={params.dataSourceType}
                        placeholder="选择数据源类型"
                        onChange={this.onSourceChange}>
                        {
                            this.renderSourceType(sourceType)
                        }
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
                        onChange={this.onUserSourceChange}>
                        {
                            this.renderUserSource(sourceList)
                        }
                    </Select>
                </div>

                <div className="m-l-8">
                    执行时间：
                    <DatePicker
                        format="YYYY-MM-DD"
                        placeholder="选择日期"
                        style={{ width: 150 }}
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
                        onChange={this.onUserChange}>
                        {
                            this.renderUserList(userList)
                        }
                    </Select>
                </div>

                <div className="m-l-8">
                    <Checkbox onChange={this.onSubscribeChange}>我订阅的表</Checkbox>
                </div>
            </div>
        )

        return (
            <div className="task-dashboard" style={{ height: '100%', overflowX: 'hidden' }}>
                <h1 className="box-title">
                    任务查询 <span style={{ fontSize: "12px", color: "rgb(153, 153, 153)" }}>
                    告警总数: {
                        taskList.data && taskList.data[0] ? 
                        taskList.data[0].allAlarmSum : 0 
                    }
                    </span>
                </h1>

                <div className="box-2 m-card shadow">
                    <Card 
                        title={cardTitle}
                        noHovering 
                        bordered={false}
                    >
                        <Table 
                            rowKey="id"
                            className="m-table"
                            columns={this.initColumns()} 
                            loading={loading}
                            pagination={pagination}
                            dataSource={taskList.data}
                            onChange={this.onTableChange}
                        />

                        <SlidePane 
                            onClose={this.closeSlidePane}
                            visible={showSlidePane} 
                            style={{ right: '0', width: '80%', minHeight: '450px', height: 'auto' }}
                        >
                            <div className="m-tabs">
                                <Tabs 
                                    animated={false}
                                    activeKey={tabKey}
                                    onChange={this.onTabChange}
                                >
                                    <TabPane tab="详细报告" key="1">
                                        <TaskDetailPane data={currentTask} />
                                    </TabPane>
                                    <TabPane tab="表级报告" key="2">
                                        <TaskTablePane data={currentTask} />
                                    </TabPane>
                                </Tabs>
                            </div>
                        </SlidePane>
                    </Card>
                </div>
            </div>
        )
    }
}




