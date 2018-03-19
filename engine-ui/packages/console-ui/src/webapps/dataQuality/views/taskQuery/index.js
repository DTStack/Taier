import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { 
    Table, Button, Icon, Input, 
    DatePicker, Select, Popconfirm, 
    message, Card, Checkbox,
    Tabs, Row, Col, Modal
} from 'antd';
import moment from 'moment';

import SlidePane from 'widgets/slidePane';
import TaskDetailPane from './taskDetailPane';
import { taskQueryActions } from '../../actions/taskQuery';
import { dataSourceActions } from '../../actions/dataSource';
import { commonActions } from '../../actions/common';
import { dataSourceTypes, periodType } from '../../consts';

// import '../../../styles/views/taskQuery.scss';

const Search = Input.Search;
const InputGroup = Input.Group;
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
    getDataSourcesType(params) {
        dispatch(dataSourceActions.getDataSourcesType(params));
    },
    getUserList(params) {
        dispatch(commonActions.getUserList(params));
    },
    getAllDict(params) {
        dispatch(commonActions.getAllDict(params));
    },
});

@connect(mapStateToProps, mapDispatchToProps)
export default class TaskQuery extends Component {

    state = {
        params: {
            currentPage: 1,
            pageSize: 20,
            fuzzyName: undefined,
            userId: undefined,
            configureId: undefined,
            dataSourceId: undefined,
            dataSourceType: undefined,
            subscribe: undefined,
            executeTime: undefined,
            bizTime: undefined
        },
        visibleSlidePane: false,
        currentTask: {}
    }

    componentDidMount() {
        this.props.getAllDict();
        this.props.getUserList();
        this.props.getDataSourcesList();
        this.props.getDataSourcesType();
        this.props.getTaskList(this.state.params);
    }

    // table设置
    initColumns = () => {
    	const { dataSourceType } = this.props.common.allDict;
        return [{
            title: '表',
            dataIndex: 'tableName',
            key: 'tableName',
            render: (text, record) => (
                <a onClick={this.showSlidePane.bind(this, record)}>{text}</a>
            )
        }, {
            title: '分区',
            dataIndex: 'partationValue',
            key: 'partationValue'
        }, 
        {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render: (text) => {
            	switch (text) {
            		case 0:
            			return '成功';
            		case 1:
            			return '运行中';
            		case 2:
            			return '失败';
            		default:
            			// statements_def
            			break;
            	}
            }
        }, {
            title: '告警总数',
            dataIndex: 'allAlarmSum',
            key: 'allAlarmSum'
        }, {
            title: '类型',
            dataIndex: 'dataSourceType',
            key: 'dataSourceType',
            render: (text, record) => {
                return text ? `${dataSourceTypes[text]} / ${record.dataName}` : '--';
            }
        }, {
            title: '配置人',
            dataIndex: 'configureUserName',
            key: 'configureUserName'
        }, {
            title: '业务日期',
            dataIndex: 'bizTime',
            key: 'bizTime',
            render: (text) => {
                return text ? moment(text).format("YYYY-MM-DD HH:mm:ss") : '--';
            }
        }, {
            title: '执行时间',
            dataIndex: 'executeTime',
            key: 'executeTime',
            render: (text) => {
                return text ? moment(text).format("YYYY-MM-DD HH:mm:ss") : '--';
            }
        }]
    }

    // 表格换页/排序
    onTableChange = (page, filter, sorter) => {
        let params = {...this.state.params, 
            currentPage: page.current,
            // sort: sorter.columnKey ? (sorter.order === 'descend' ? 'desc' : 'asc') : undefined
        }

        this.setState({ params });
        this.props.getTaskList(params);
    }

    // 数据源类型下拉框
    renderSourceType = (data) => {
        return data.map((source) => {
            return (
                <Option key={source.value} value={source.value.toString()}>{source.name}</Option>
            )
        });
    }

    onSourceChange = (type) => {
        let dataSourceType = type ? type : undefined;
        let params = {...this.state.params, dataSourceType};
        
        this.setState({ params });
        this.props.getTaskList(params);
    }

    // 数据源下拉框
    renderUserSource = (data) => {
        return data.map((source) => {
            return (
                <Option key={source.id} value={source.id.toString()}>{source.dataName}（{dataSourceTypes[source.type]}）</Option>
            )
        });
    }

    onUserSourceChange = (id) => {
        let dataSourceId = id ? id : undefined;
        let params = {...this.state.params, dataSourceId};
        
        this.setState({ params });
        this.props.getTaskList(params);
    }

    // 调度周期下拉框
    renderPeriodType = (data) => {
        return data.map((item) => {
            return (
                <Option key={item.value} value={item.value.toString()}>{item.name}</Option>
            )
        })
    }

    // 调度周期变化回调
    onPeriodTypeChange = (type) => {
        let periodType = type ? type : undefined;
        let params = {...this.state.params, periodType};
        
        this.setState({ params });
        this.props.getTaskList(params);
    }

    // user下拉框
    renderUserList = (data) => {
        return data.map((item) => {
            return (
                <Option key={item.id} value={item.id.toString()}>{item.userName}</Option>
            )
        })
    }

    // 监听userList的select
    onUserChange = (id) => {
        let userId = id ? id : undefined;
        let params = {...this.state.params, userId};
        
        this.setState({ params });
        this.props.getTaskList(params);
    }

    // 业务日期变化回调
    onBizTimeChange = (date, dateString) => {
        let bizTime = date ? date.valueOf() : undefined;
        let params = {...this.state.params, bizTime};
        
        this.setState({ params });
        this.props.getTaskList(params);
    }

    // 执行时间变化回调
    onExecuteTimeChange = (date, dateString) => {
        let executeTime = date ? date.valueOf() : undefined;
        let params = {...this.state.params, executeTime};
        
        this.setState({ params });
        this.props.getTaskList(params);
    }

    // 是否订阅
    onSubscribeChange = (e) => {
        let subscribe = e.target.checked ? true : undefined;
        let params = {...this.state.params, subscribe};
        
        this.setState({ params });
        this.props.getTaskList(params);
    }

    // table搜索
    handleSearch = (name) => {
        let fuzzyName = name ? name : undefined;
        let params = {...this.state.params, fuzzyName};

        this.setState({ params });
        this.props.getTaskList(params);
    }

    showSlidePane = (record) => {
        this.setState({
            visibleSlidePane: true,
            currentTask: record
        })
    }

    closeSlidePane = () => {
        this.setState({
            visibleSlidePane: false,
            currentTask: {}
        })
    }

	

    render() {
    	const { dataSource, taskQuery, common } = this.props;
        const { sourceType, sourceList } = dataSource;
        const { userList, allDict } = common;
        const { loading, taskList } = taskQuery;
        const { params, visibleSlidePane, currentTask } = this.state;

        const pagination = {
            current: params.currentPage,
            pageSize: params.pageSize,
            total: taskList.totalCount,
        };

        let periodType = allDict.periodType ? allDict.periodType : [];

        const cardTitle = (
            <div className="flex font-12">
                <Search
                    placeholder="输入表名搜索"
                    style={{ width: 150, margin: '10px 0' }}
                    onSearch={this.handleSearch}
                />

                <div className="m-l-8">
                    类型：
                    <Select allowClear onChange={this.onSourceChange} style={{ width: 100 }}>
                        {
                            this.renderSourceType(sourceType)
                        }
                    </Select>
                </div>

                <div className="m-l-8">
                    数据源：
                    <Select allowClear onChange={this.onUserSourceChange} style={{ width: 150 }}>
                        {
                            this.renderUserSource(sourceList)
                        }
                    </Select>
                </div>

                <div className="m-l-8">
                    业务日期：
                    <DatePicker
                        format="YYYY-MM-DD"
                        placeholder="选择日期"
                        style={{ width: 100 }}
                        onChange={this.onBizTimeChange}
                    />
                </div>

                <div className="m-l-8">
                    执行时间：
                    <DatePicker
                        format="YYYY-MM-DD"
                        placeholder="选择日期"
                        style={{ width: 100 }}
                        onChange={this.onExecuteTimeChange}
                    />
                </div>

                <div className="m-l-8 m-r-8">
                    配置人：
                    <Select allowClear onChange={this.onUserChange} style={{ width: 150 }}>
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
            <div className="rule-dashboard">
                <h1 className="box-title">
                    任务查询
                </h1>

                <div className="box-2 m-card shadow">
                    <Card 
                        title={cardTitle}
                        noHovering 
                        bordered={false}
                    >
                        <Table 
                            rowKey="id"
                            className="m-table monitor-table"
                            columns={this.initColumns()} 
                            loading={loading}
                            pagination={pagination}
                            dataSource={taskList.data}
                            onChange={this.onTableChange}
                        />

                        <SlidePane 
                            onClose={ this.closeSlidePane }
                            visible={ visibleSlidePane } 
                            style={{ right: '-20px', width: '80%', height: '100%', minHeight: '600px' }}
                        >
                            <div className="m-tabs m-card bd" style={{height: '100%'}}>
                                <Tabs 
                                    animated={false}
                                    // onChange={ this.getPreview.bind(this) }
                                >
                                    
                                    <TabPane tab="规则管理" key="1">
                                    	<TaskDetailPane data={currentTask}>
                                    	</TaskDetailPane>
                                    </TabPane>
                                    <TabPane tab="远程触发" key="2">
                                        22
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




