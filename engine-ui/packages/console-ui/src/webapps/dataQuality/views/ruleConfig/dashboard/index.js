import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { Table, Button, Input, Select, message, Card, Checkbox, Tabs } from 'antd';
import moment from 'moment';
import utils from "utils";

import RuleEditPane from './ruleEditPane';
import RemoteTriggerPane from './remoteTriggerPane';
import SlidePane from 'widgets/slidePane';
import { dataSourceActions } from '../../../actions/dataSource';
import { ruleConfigActions } from '../../../actions/ruleConfig';
import RCApi from '../../../api/ruleConfig';
import { DATA_SOURCE } from '../../../consts';

import '../../../styles/views/ruleConfig.scss';

const Search = Input.Search;
const Option = Select.Option;
const TabPane = Tabs.TabPane;

const mapStateToProps = state => {
    const { ruleConfig, dataSource, common } = state;
    return { ruleConfig, dataSource, common }
};

const mapDispatchToProps = dispatch => ({
    getMonitorLists(params) {
        return dispatch(ruleConfigActions.getMonitorLists(params));
    },
    getDataSourcesList(params) {
        dispatch(dataSourceActions.getDataSourcesList(params));
    },
});

@connect(mapStateToProps, mapDispatchToProps)
export default class RuleConfig extends Component {

    state = {
        params: {
            pageIndex: 1,
            pageSize: 20,
            tableName: utils.getParameterByName('tableName') || undefined,
            modifyUserId: undefined,
            sourceType: undefined,
            dataSourceId: undefined,
            isSubscribe: undefined,
            periodType: undefined
        },
        tabKey: '1',
        showSlidePane: false,
        currentMonitor: {},

    }

    componentDidMount() {
        this.props.getDataSourcesList();
        this.props.getMonitorLists(this.state.params)
        .then(
            (res)=>{
                if(res&&res.data&&res.data.data){

                    const record=res.data.data.filter(
                        (item)=>{
                            return item.tableId==utils.getParameterByName('tableId')
                        }
                    );

                    if(record&&record.length>0){
                        this.openSlidePane(record[0]);
                    }
                }
                this.props.router.replace("/dq/rule")
            }
        );
    }

    // table设置
    initColumns = () => {
        return [{
            title: '表',
            dataIndex: 'tableName',
            key: 'tableName',
            render: (text, record) => (
                <a onClick={this.openSlidePane.bind(this, record)}>{text}</a>
            )
        }, {
            title: '类型',
            dataIndex: 'sourceTypeName',
            key: 'sourceTypeName',
            render: (text, record) => {
                return text ? `${text} / ${record.dataName}` : '--';
            }
        }, {
            title: '执行周期',
            dataIndex: 'periodTypeName',
            key: 'periodTypeName'
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
                return text ? '已开启' : ''
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
            width: '10%',
            render: (text, record) => {
                return <span>
                        <a onClick={this.onSubscribe.bind(this, record)}>
                            {record.isSubscribe ? '取消订阅' : '订阅'}
                        </a> 
                    </span>
            }
        }]
    }

    // 订阅操作
    onSubscribe = (record) => {
        const { params } = this.state;

        if (record.isSubscribe) {
            RCApi.unSubscribeTable({ tableId: record.tableId }).then((res) => {
                if (res.code === 1) {
                    message.success('取消订阅成功');
                    this.props.getMonitorLists(params);
                }
            });
        } else {
            RCApi.subscribeTable({ tableId: record.tableId }).then((res) => {
                if (res.code === 1) {
                    message.success('订阅成功');
                    this.props.getMonitorLists(params);
                }
            })
        }
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
            pageIndex: 1,
            sourceType: type ? type : undefined
        };
        
        this.props.getMonitorLists(params);
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
            pageIndex: 1,
            dataSourceId: id ? id : undefined
        };
        
        this.props.getMonitorLists(params);
        this.setState({ params });
    }

    // 调度周期下拉框
    renderPeriodType = (data) => {
        return data.map((item) => {
            return (
                <Option 
                    key={item.value} 
                    value={item.value.toString()}>
                    {item.name}
                </Option>
            )
        })
    }

    // 调度周期筛选
    onPeriodTypeChange = (type) => {
        let params = {
            ...this.state.params,
            pageIndex: 1,
            periodType: type ? type : undefined
        };
        
        this.props.getMonitorLists(params);
        this.setState({ params });
    }

    // user下拉框
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

    // user筛选
    onUserChange = (id) => {
        let params = {
            ...this.state.params, 
            pageIndex: 1,
            lastModifyUserId: id ? id : undefined
        };
        
        this.props.getMonitorLists(params);
        this.setState({ params });
    }

    // 执行时间筛选
    onDateChange = (date, dateString) => {
        let params = {
            ...this.state.params, 
            pageIndex: 1,
            executeTime: date ? date.valueOf() : undefined
        };
        
        this.props.getMonitorLists(params);
        this.setState({ params });
    }

    // 是否订阅筛选
    onSubscribeChange = (e) => {
        let params = {
            ...this.state.params, 
            pageIndex: 1,
            isSubscribe: e.target.checked ? 1 : undefined
        };
        
        this.props.getMonitorLists(params);
        this.setState({ params });
    }

    // table搜索
    handleSearch = (name) => {
        let params = {
            ...this.state.params, 
            pageIndex: 1,
            tableName: name ? name : undefined
        };

        this.props.getMonitorLists(params);
        this.setState({ params });
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

    refresh = () => {
        this.closeSlidePane();
        this.props.getMonitorLists(this.state.params);
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
            total: monitorList ? monitorList.totalCount : 0,
        };

        let periodType = allDict.periodType ? allDict.periodType : [];

        const cardTitle = (
            <div className="flex font-12">

                <Search
                    placeholder="输入表名搜索"
                    onSearch={this.handleSearch}
                    defaultValue={params.tableName}
                    style={{ width: 200, margin: '10px 0' }}
                />

                <div className="m-l-8">
                    类型：
                    <Select 
                        allowClear 
                        style={{ width: 150 }}
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
                    执行周期：
                    <Select 
                        allowClear 
                        style={{ width: 150 }} 
                        placeholder="选择执行周期"
                        onChange={this.onPeriodTypeChange}>
                        {
                            this.renderPeriodType(periodType)
                        }
                    </Select>
                </div>

                <div className="m-8">
                    最近修改人：
                    <Select 
                        allowClear 
                        showSearch
                        style={{ width: 150 }} 
                        optionFilterProp="name"
                        placeholder="选择最近修改人"
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
                            rowClassName={
                                (record, index) => {
                                    if (currentMonitor && currentMonitor.tableId == record.tableId) {
                                        return "row-select"
                                    } else {
                                        return "";
                                    }
                                }
                            }
                            rowKey="tableId"
                            className="m-table"
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
                            style={{ right: '0px', width: '80%', height:"100%", minHeight: '650px' }}
                        >
                            <div className="m-tabs">
                                <Tabs 
                                    animated={false}
                                    activeKey={tabKey}
                                    onChange={this.onTabChange}
                                >
                                    <TabPane tab="规则管理" key="1">
                                        <RuleEditPane 
                                            data={currentMonitor} 
                                            closeSlidePane={this.closeSlidePane} 
                                            refresh={this.refresh} />
                                    </TabPane>

                                    <TabPane tab="远程触发" key="2">
                                        <RemoteTriggerPane 
                                            data={currentMonitor} 
                                            closeSlidePane={this.closeSlidePane} />
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


