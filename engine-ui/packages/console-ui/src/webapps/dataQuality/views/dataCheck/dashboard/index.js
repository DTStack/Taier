import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { dataCheckActions } from '../../../actions/dataCheck';
import { Table, Button, Icon, Input, DatePicker, Menu, Dropdown } from 'antd';
import '../../../styles/views/dataCheck.scss';

const Search = Input.Search;
const InputGroup = Input.Group;

const mapStateToProps = state => {
    const { dataCheck } = state;
    return { dataCheck }
};

const mapDispatchToProps = dispatch => ({
    getLists(params) {
        dispatch(dataCheckActions.getLists(params));
    }
});

@connect(mapStateToProps, mapDispatchToProps)
export default class DataCheck extends Component {

    state = {
        params: {
            currentPage: 1,
            pageSize: 20,
            // tableName: '',
            // lastModifyUserId: undefined,
            // executeTime: ''
        }
    }

    componentDidMount() {
        this.props.getLists(this.state.params);
    }

    initColumns = () => {
        return [{
            title: '左侧表',
            dataIndex: 'originTableName',
            key: 'originTableName',
            width: '15%'
        }, {
            title: '分区',
            dataIndex: 'originPartitionColumn',
            key: 'originPartitionColumn',
            width: '12%',
            render: (text, record) => {
                return `${record.originPartitionColumn} -- ${record.originPartitionValue}`
            }
        }, 
        {
            title: '右侧表',
            dataIndex: 'targetTableName',
            key: 'targetTableName',
            width: '15%'
        }, {
            title: '分区',
            dataIndex: 'targetPartitionColumn',
            key: 'targetPartitionColumn',
            width: '12%',
            render: (text, record) => {
                return `${record.targetPartitionColumn} -- ${record.targetPartitionValue}`
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
            sorter: true
        }, {
            title: '差异比例',
            dataIndex: 'diverseRatio',
            key: 'diverseRatio',
            width: '8%',
            sorter: true
        }, {
            title: '最近修改人',
            dataIndex: 'modifyUserName',
            key: 'modifyUserName',
            width: '10%'
        }, {
            title: '执行时间',
            dataIndex: 'executeTime',
            key: 'executeTime',
            width: '10%'
        }, {
            title: '操作',
            width: '12%',
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
                            <a>编辑</a>
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

    handleInputChange = (e) => {
        console.log(e.target.value)
    }

    handleTableChange = (page, filter, sorter) => {
        console.log(page, filter, sorter)
    }

    render() {
        const { lists, loading } = this.props.dataCheck;
        const pagination = {
            current: this.state.params.currentPage,
            pageSize: 20,
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
                                onChange={this.handleInputChange} 
                            />
                            <Button type="primary" onClick={this.handleSearch}>搜索</Button>
                        </InputGroup>

                        <div className="m-l-8">
                            最近修改人：
                            <Input style={{ width: 100 }}  />
                        </div>

                        <div className="m-l-8">
                            执行时间：
                            <DatePicker
                                format="YYYY-MM-DD"
                                placeholder="选择日期"
                                onChange={this.handleDateChange}
                                onOk={this.onCheckDate}
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
                    onChange={this.handleTableChange}
                />
        	</div>
        )
    }
}


