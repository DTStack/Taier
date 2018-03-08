import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import moment from 'moment';
import { dataCheckActions } from '../../../actions/dataCheck';
import * as UserAction from '../../../actions/user';
import { Table, Button, Icon, Input, DatePicker, Menu, Dropdown, Select } from 'antd';
import '../../../styles/views/dataCheck.scss';

const Search = Input.Search;
const InputGroup = Input.Group;
const Option = Select.Option;

const mapStateToProps = state => {
    const { dataCheck, user } = state;
    return { dataCheck,user }
};

const mapDispatchToProps = dispatch => ({
    getLists(params) {
        dispatch(dataCheckActions.getLists(params));
    },
    getUserList(params) {
        dispatch(UserAction.getUserList(params));
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
        this.props.getLists(this.state.params);
        this.props.getUserList();
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
            dataIndex: 'originPartitionColumn',
            key: 'originPartitionColumn',
            width: '10%',
            render: (text, record) => {
                return text ? `${text} -- ${record.originPartitionValue}` : '--';
            }
        }, 
        {
            title: '右侧表',
            dataIndex: 'targetTableName',
            key: 'targetTableName',
            width: '12%'
        }, {
            title: '分区',
            dataIndex: 'targetPartitionColumn',
            key: 'targetPartitionColumn',
            width: '10%',
            render: (text, record) => {
                return text ? `${text} -- ${record.targetPartitionValue}` : '--';
            }
        }, {
            title: '校验结果',
            dataIndex: 'status',
            key: 'status',
            filters: [{
                text: '无差异',
                value: '0',
            }, {
                text: '有差异',
                value: '1',
            }, {
                text: '进行中',
                value: '2',
            }, {
                text: '未开始',
                value: '3',
            }],
            width: '8%',
            filterMultiple: false,
            onFilter: (value, record) => console.log(value,record),
        }, {
            title: '差异总数',
            dataIndex: 'diverseNum',
            key: 'diverseNum',
            width: '8%',
            render: (text, record) => {
                return text ? text : '--';
            },
            sorter: true
        }, {
            title: '差异比例',
            dataIndex: 'diverseRatio',
            key: 'diverseRatio',
            width: '8%',
            render: (text, record) => {
                 return text ? text : '--';
            },
            sorter: true
        }, {
            title: '最近修改人',
            dataIndex: 'modifyUserName',
            key: 'modifyUserName',
            width: '11%'
        }, {
            title: '执行时间',
            dataIndex: 'executeTime',
            key: 'executeTime',
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
                            record.status === 0
                            &&
                            <Menu.Item>
                                <a>查看报告</a>
                            </Menu.Item>
                        }
                        <Menu.Item>
                            <Link to={`dq/dataCheck/edit/${record.id}`}>编辑</Link>
                        </Menu.Item>
                        <Menu.Item>
                            <a type="danger">删除</a>
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

    // 表名
    onInputChange = (e) => {
        let tableName = e.target.value ? e.target.value : undefined;
        let params = {...this.state.params, tableName: tableName};
        this.setState({ params });
    }

    // 表格换页/排序
    onTableChange = (page, filter, sorter) => {
        console.log(page, filter, sorter)
        let params = {...this.state.params, 
            currentPage: page.current,
            // sortBy: sorter.columnKey ? sorter.columnKey : '',
            // orderBy: sorter.columnKey ? (sorter.order == 'ascend' ? '01' : '02') : ''
        }
        // this.props.getFileList(params);
        this.setState({ params });
        this.props.getLists(params);
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
        let params = {...this.state.params, lastModifyUserId: value};
        this.setState({ params });
        this.props.getLists(params);
    }

    // 执行时间改变
    onDateChange = (date, dateString) => {
        let executeTime = date ? date.valueOf() : undefined;
        let params = {...this.state.params, executeTime: executeTime};
        
        this.setState({ params });
        this.props.getLists(params);
    }

    // table搜索
    handleSearch = () => {
        this.props.getLists(this.state.params);
    }

    render() {
        const { lists, loading } = this.props.dataCheck;
        const { userList } = this.props.user;
        const { params } = this.state;

        const pagination = {
            current: params.currentPage,
            pageSize: params.pageSize,
            total: lists.totalCount,
        };

        return (
        	<div className="inner-container check-page">
        		<div className="action-panel">
                    <div className="flex">
            			<InputGroup compact>
                            <Input 
                                placeholder="输入表名搜索" 
                                style={{ width: 300 }} 
                                onChange={this.onInputChange} 
                            />
                            <Button type="primary" onClick={this.handleSearch}>搜索</Button>
                        </InputGroup>

                        <div className="m-l-8">
                            最近修改人：
                            <Select allowClear onChange={this.onUserChange} style={{ width: 200 }}>
                                {
                                    this.renderUserList(userList)
                                }
                            </Select>
                        </div>

                        <div className="m-l-8">
                            执行时间：
                            <DatePicker
                                format="YYYY-MM-DD"
                                placeholder="选择日期"
                                onChange={this.onDateChange}
                            />
                        </div>
                    </div>

                    <Button type="primary">
                        <Link to="/dq/dataCheck/add">
                            新建逐行校验
                        </Link>
                    </Button>
                </div>

                <Table 
                    rowKey="id"
                    className="m-table box-5"
                    columns={this.initColumns()} 
                    loading={loading}
                    pagination={pagination}
                    dataSource={lists.data}
                    onChange={this.onTableChange}
                />
        	</div>
        )
    }
}


