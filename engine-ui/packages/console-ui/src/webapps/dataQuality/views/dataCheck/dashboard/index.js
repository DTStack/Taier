import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { Table, Button, Icon, Input, DatePicker, Menu, Dropdown, Select, Popconfirm, message, Card } from 'antd';
import moment from 'moment';

import { dataCheckActions } from '../../../actions/dataCheck';
import { dataSourceActions } from '../../../actions/dataSource';
import { commonActions } from '../../../actions/common';
import { CHECK_STATUS, CHECK_STATUS_CN } from '../../../consts';
import DCApi from '../../../api/dataCheck';
import '../../../styles/views/dataCheck.scss';

const Search = Input.Search;
const InputGroup = Input.Group;
const Option = Select.Option;

const enableCheckReport = (status) => {
    return status === CHECK_STATUS.SUCCESS ||
    status === CHECK_STATUS.PASS ||
    status === CHECK_STATUS.UNPASS ||
    status === CHECK_STATUS.EXPIRED;
}

const mapStateToProps = state => {
    const { dataCheck, dataSource, common } = state;
    return { dataCheck, dataSource, common }
};

const mapDispatchToProps = dispatch => ({
    getLists(params) {
        dispatch(dataCheckActions.getLists(params));
    },
    getUserList(params) {
        dispatch(commonActions.getUserList(params));
    },
    getDataSourcesList(params) {
        dispatch(dataSourceActions.getDataSourcesList(params));
    },
});

@connect(mapStateToProps, mapDispatchToProps)
export default class DataCheck extends Component {

    state = {
        params: {
            currentPage: 1,
            pageSize: 20,
            tableName: undefined,
            lastModifyUserId: undefined,
            executeTime: undefined
        }
    }

    componentDidMount() {
        this.props.getUserList();
        this.props.getDataSourcesList();
        this.props.getLists(this.state.params);
    }

    // table设置
    initColumns = () => {
        return [{
            title: '左侧表',
            dataIndex: 'originTableName',
            key: 'originTableName',
            width: '12%'
        }, {
            title: '分区',
            dataIndex: 'originPartition',
            key: 'originPartition',
            width: '10%',
            render: (text, record) => {
                return text ? text : '--';
            }
        }, 
        {
            title: '右侧表',
            dataIndex: 'targetTableName',
            key: 'targetTableName',
            width: '12%'
        }, {
            title: '分区',
            dataIndex: 'targetPartition',
            key: 'targetPartition',
            width: '10%',
            render: (text, record) => {
                return text ? text : '--';
            }
        }, {
            title: '校验结果',
            dataIndex: 'statusEN',
            key: 'statusEN',
            width: '8%',
            // filterMultiple: false,
        }, {
            title: '差异总数',
            dataIndex: 'diverseNum',
            key: 'diverseNum',
            width: '8%',
            // sorter: true
        }, {
            title: '差异比例',
            dataIndex: 'diverseRatio',
            key: 'diverseRatio',
            width: '8%',
            render: (text => text ? `${text} %` : text)
            // sorter: true
        }, {
            title: '最近修改人',
            dataIndex: 'modifyUserName',
            key: 'modifyUserName',
            width: '11%'
        }, {
            title: '执行时间',
            dataIndex: 'executeTimeFormat',
            key: 'executeTimeFormat',
            render: (text, record) => {
                return text ? text : '--';
            },
            width: '11%'
        }, {
            title: '操作',
            width: '10%',
            render: (text, record) => {
                let menu = (
                    <Menu>
                        {
                            enableCheckReport(record.status) &&
                            <Menu.Item>
                                <Link to={`dq/dataCheck/report/${record.id}`}>查看报告</Link>
                            </Menu.Item>
                        }
                        <Menu.Item>
                            <Link to={`dq/dataCheck/edit/${record.verifyId}`}>编辑</Link>
                        </Menu.Item>
                        <Menu.Item>
                            <Popconfirm
                                title="确定删除此校验？"
                                okText="确定" cancelText="取消"
                                onConfirm={() => {this.deleteDataCheck(record.id)}}
                            >
                                <a type="danger">删除</a>
                            </Popconfirm>
                        </Menu.Item>
                    </Menu>
                );
                return (
                    <Dropdown overlay={menu} trigger={['click']}>
                        <Button>操作<Icon type="down" /></Button>
                    </Dropdown>
                )
            }
        }]
    }

    deleteDataCheck = (id) => {
        DCApi.deleteCheck({ verifyRecordId: id }).then((res) => {
            if (res.code === 1) {
                message.success("删除成功！");
                this.props.getLists(this.state.params);
            }
        })
    }

    // 表格换页/排序
    onTableChange = (page, filter, sorter) => {
        let params = {...this.state.params, 
            currentPage: page.current,
            // sortBy: sorter.columnKey ? sorter.columnKey : '',
            // orderBy: sorter.columnKey ? (sorter.order == 'ascend' ? '01' : '02') : ''
        }
        this.props.getLists(params);
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
        let dataSourceId = id ? id : undefined,
            params = {
            ...this.state.params, 
            currentPage: 1,
            dataSourceId
        };
        
        this.props.getLists(params);
        this.setState({ params });
    }

    // 校验状态下拉框
    renderCheckStatus = (data) => {
        return data.map((item) => {
            return (
                <Option 
                    key={item.value} 
                    value={item.value}
                    title={item.text}>
                    {item.text}
                </Option>
            )
        });
    }

    // 校验状态筛选
    onCheckStatusChange = (status) => {
        let params = {
            ...this.state.params, 
            currentPage: 1,
            status: status ? status : undefined
        };
        
        this.props.getLists(params);
        this.setState({ params });
    }

    // user的select选项
    renderUserList = (data) => {
        return data.map((item) => {
            return (
                <Option key={item.id} value={item.id.toString()}>{item.userName}</Option>
            )
        })
    }

    // 监听userList的select
    onUserChange = (value) => {
        let lastModifyUserId = value ? value : undefined,
            params = {
            ...this.state.params, 
            currentPage: 1,
            lastModifyUserId
        };

        this.props.getLists(params);
        this.setState({ params });
    }

    // 执行时间改变
    onDateChange = (date, dateString) => {
        let executeTime = date ? date.valueOf() : undefined,
            params = {
                ...this.state.params, 
                currentPage: 1,
                executeTime
            };
        
        this.props.getLists(params);
        this.setState({ params });
    }

    // table搜索
    onTableSearch = (name) => {
        let tableName = name ? name : undefined,
            params = {
                ...this.state.params, 
                currentPage: 1,
                tableName
            };

        this.props.getLists(params);
        this.setState({ params });
    }

    disabledDate = (current) => {
        return current && current.valueOf() > Date.now();
    }

    render() {
        const { dataCheck, dataSource, common } = this.props;
        const { userList } = common;
        const { sourceList } = dataSource;
        const { lists, loading } = dataCheck;
        const { params } = this.state;

        const pagination = {
            current: params.currentPage,
            pageSize: params.pageSize,
            total: lists.totalCount,
        };

        const cardTitle = (
            <div className="flex font-12">
                <Search
                    placeholder="输入表名搜索"
                    style={{ width: 200, margin: '10px 0' }}
                    onSearch={this.onTableSearch}
                />

                <div className="m-l-8">
                    数据源：
                    <Select allowClear onChange={this.onUserSourceChange} style={{ width: 150 }}>
                        {
                            this.renderUserSource(sourceList)
                        }
                    </Select>
                </div>

                <div className="m-l-8">
                    校验结果：
                    <Select allowClear onChange={this.onCheckStatusChange} style={{ width: 150 }}>
                        {
                            this.renderCheckStatus(CHECK_STATUS_CN)
                        }
                    </Select>
                </div>

                <div className="m-l-8">
                    最近修改人：
                    <Select 
                        allowClear 
                        style={{ width: 150 }}
                        onChange={this.onUserChange}>
                        {
                            this.renderUserList(userList)
                        }
                    </Select>
                </div>

                <div className="m-l-8">
                    执行时间：
                    <DatePicker
                        format="YYYY-MM-DD"
                        disabledDate={this.disabledDate}
                        style={{ width: 150 }}
                        placeholder="选择日期"
                        onChange={this.onDateChange}
                    />
                </div>
            </div>
        )

        const cardExtra = (
            <Button type="primary" style={{ margin: '10px 0' }}>
                <Link to="/dq/dataCheck/add">
                    新建逐行校验
                </Link>
            </Button>
        )

        return (
        	<div className="check-dashboard">
                <h1 className="box-title">
                    逐行校验
                </h1>

                <div className="box-2 m-card shadow">
                    <Card 
                        title={cardTitle} 
                        extra={cardExtra} 
                        noHovering 
                        bordered={false}
                    >
                        <Table 
                            rowKey="id"
                            className="m-table"
                            columns={this.initColumns()} 
                            loading={loading}
                            pagination={pagination}
                            dataSource={lists.data}
                            onChange={this.onTableChange}
                        />
                    </Card>
                </div>

        	</div>
        )
    }
}


