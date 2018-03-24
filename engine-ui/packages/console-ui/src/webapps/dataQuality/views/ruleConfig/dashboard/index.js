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

import RuleEditPane from './ruleEditPane';
import RemoteTriggerPane from './remoteTriggerPane';
import SlidePane from 'widgets/slidePane';
import { ruleConfigActions } from '../../../actions/ruleConfig';
import { dataSourceActions } from '../../../actions/dataSource';
import { commonActions } from '../../../actions/common';
import { dataSourceTypes, periodType } from '../../../consts';
import RCApi from '../../../api/ruleConfig';
import '../../../styles/views/ruleConfig.scss';

const Search = Input.Search;
const InputGroup = Input.Group;
const Option = Select.Option;
const TabPane = Tabs.TabPane;

const mapStateToProps = state => {
    const { ruleConfig, dataSource, common } = state;
    return { ruleConfig, dataSource, common }
};

const mapDispatchToProps = dispatch => ({
    getMonitorLists(params) {
        dispatch(ruleConfigActions.getMonitorLists(params));
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
export default class RuleConfig extends Component {

    state = {
        params: {
            pageIndex: 1,
            pageSize: 20,
            tableName: undefined,
            modifyUserId: undefined,
            sourceType: undefined,
            dataSourceId: undefined,
            isSubscribe: undefined,
            periodType: undefined
        },
        tabKey: '1',
        showSlidePane: false,
        currentMonitor: {}
    }

    componentDidMount() {
        this.props.getAllDict();
        this.props.getUserList();
        this.props.getDataSourcesList();
        this.props.getDataSourcesType();
        this.props.getMonitorLists(this.state.params);
        console.log(this)
    }

    // table设置
    initColumns = () => {
        const { dataSourceType } = this.props.common.allDict;
        return [{
            title: '表',
            dataIndex: 'tableName',
            key: 'tableName',
            render: (text, record) => (
                <a onClick={this.openSlidePane.bind(this, record)}>{text}</a>
            )
        }, {
            title: '类型',
            dataIndex: 'dataSourceType',
            key: 'dataSourceType',
            render: (text, record) => {
                return text ? `${dataSourceType.filter(item => item.value === text)[0].name} / ${record.dataName}` : '--';
                // return text ? `${dataSourceTypes[text]} / ${record.dataName}` : '--';
            }
        }, 
        {
            title: '执行周期',
            dataIndex: 'periodType',
            key: 'periodType',
            render: (text) => {
                return periodType[text];
            }
        }, {
            title: '最近30天告警数',
            dataIndex: 'recentNotifyNum',
            key: 'recentNotifyNum',
            sorter: true
        }, {
            title: '远程触发',
            dataIndex: 'isRemoteTrigger',
            key: 'isRemoteTrigger',
            render: (text) => {
                if (text === 0) {
                    return <Icon type="close-circle status-error" />
                } else {
                    return <Icon type="check-circle status-success" />
                }
            }
        }, {
            title: '最近修改人',
            dataIndex: 'modifyUser',
            key: 'modifyUser'
        }, {
            title: '最近修改时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: (text) => {
                return text ? moment(text).format("YYYY-MM-DD HH:mm:ss") : '--';
            }
        }, {
            title: '操作',
            render: (text, record) => {
                return <a onClick={this.onSubscribe.bind(this, record)}>{record.isSubscribe ? '取消订阅' : '订阅'}</a>
            }
        }]
    }

    onSubscribe = (record) => {
        if (record.isSubscribe) {
            RCApi.unSubscribeTable({ tableId: record.tableId }).then((res) => {
                if (res.code === 1) {
                    message.success('取消订阅成功');
                    this.props.getMonitorLists(this.state.params);
                }
            });
        } else {
            RCApi.subscribeTable({ tableId: record.tableId }).then((res) => {
                if (res.code === 1) {
                    message.success('订阅成功');
                    this.props.getMonitorLists(this.state.params);
                }
            })
        }
    }

    deleteRuleConfig = (record) => {
        Api.deleteRule({ id: record.id }).then((res) => {
            if (res.code === 1) {
                message.success("删除成功！");
                this.props.getMonitorLists(this.state.params);
            }
        })
    }

    // 表格换页/排序
    onTableChange = (page, filter, sorter) => {
        let params = {...this.state.params, 
            pageIndex: page.current,
            sort: sorter.columnKey ? (sorter.order === 'descend' ? 'desc' : 'asc') : undefined
        }

        this.setState({ params });
        this.props.getMonitorLists(params);
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
        let sourceType = type ? type : undefined;
        let params = {...this.state.params, sourceType};
        
        this.setState({ params });
        this.props.getMonitorLists(params);
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
        this.props.getMonitorLists(params);
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
        this.props.getMonitorLists(params);
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
        let lastModifyUserId = id ? id : undefined;
        let params = {...this.state.params, lastModifyUserId};
        
        this.setState({ params });
        this.props.getMonitorLists(params);
    }

    // 执行时间变化回调
    onDateChange = (date, dateString) => {
        let executeTime = date ? date.valueOf() : undefined;
        let params = {...this.state.params, executeTime};
        
        this.setState({ params });
        this.props.getMonitorLists(params);
    }

    // 是否订阅
    onSubscribeChange = (e) => {
        let isSubscribe = e.target.checked ? 1 : undefined;
        let params = {...this.state.params, isSubscribe};
        
        this.setState({ params });
        this.props.getMonitorLists(params);
    }

    // table搜索
    handleSearch = (name) => {
        let tableName = name ? name : undefined;
        let params = {...this.state.params, tableName: tableName};

        this.setState({ params });
        this.props.getMonitorLists(params);
    }

    openSlidePane = (record) => {
        this.setState({
            showSlidePane: true,
            currentMonitor: record
        });
    }

    closeSlidePane = () => {
        this.setState({
            showSlidePane: false,
            currentMonitor: {},
            tabKey: '1'
        });
    }

    onTabChange = (key) => {
        this.setState({ tabKey: key });
    }

    render() {
        const { monitorList, loading } = this.props.ruleConfig;
        const { sourceType, sourceList } = this.props.dataSource;
        const { userList, allDict } = this.props.common;
        const { params, showSlidePane, currentMonitor, tabKey } = this.state;

        const pagination = {
            current: params.pageIndex,
            pageSize: params.pageSize,
            total: monitorList.totalCount,
        };

        let periodType = allDict.periodType ? allDict.periodType : [];

        const cardTitle = (
            <div className="flex font-12">
                <Search
                    placeholder="输入表名搜索"
                    style={{ width: 200, margin: '10px 0' }}
                    onSearch={this.handleSearch}
                />

                <div className="m-l-8">
                    类型：
                    <Select allowClear onChange={this.onSourceChange} style={{ width: 150 }}>
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
                    执行周期：
                    <Select allowClear onChange={this.onPeriodTypeChange} style={{ width: 150 }}>
                        {
                            this.renderPeriodType(periodType)
                        }
                    </Select>
                </div>

                <div className="m-l-8 m-r-8">
                    最近修改人：
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

        const cardExtra = (
            <Button type="primary" style={{ margin: '10px 0' }}>
                <Link to="/dq/rule/add">
                    新建监控规则
                </Link>
            </Button>
        )

        return (
            <div className="rule-dashboard">
                <h1 className="box-title">
                    监控规则
                </h1>

                <div className="box-2 m-card shadow">
                    <Card 
                        title={cardTitle} 
                        extra={cardExtra} 
                        noHovering 
                        bordered={false}
                    >
                        <Table 
                            rowKey="tableId"
                            className="m-table monitor-table"
                            columns={this.initColumns()} 
                            loading={loading}
                            pagination={pagination}
                            dataSource={monitorList.data}
                            onChange={this.onTableChange}
                        />

                        <SlidePane 
                            onClose={this.closeSlidePane}
                            visible={showSlidePane}
                            className="slide-pane-box"
                            style={{ right: '-20px', width: '80%', minHeight: '600px' }}
                        >
                            <div className="m-tabs">
                                <Tabs 
                                    animated={false}
                                    activeKey={tabKey}
                                    onChange={this.onTabChange}
                                >
                                    
                                    <TabPane tab="规则管理" key="1">
                                        <RuleEditPane data={currentMonitor} closeSlidePane={this.closeSlidePane}>
                                        </RuleEditPane>
                                    </TabPane>
                                    <TabPane tab="远程触发" key="2">
                                        <RemoteTriggerPane data={currentMonitor} closeSlidePane={this.closeSlidePane}>
                                        </RemoteTriggerPane>
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


