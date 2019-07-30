import React, { Component } from 'react'
import { Card, Input, Table, Select, Modal, Form, message } from 'antd';
import { connect } from 'react-redux';

import utils from 'utils';
import ApprovalModal from '../../components/approvalBox';

import { EXCHANGE_APPLY_STATUS, API_USER_STATUS } from '../../consts'
import { approvalActions } from '../../actions/approval';
import { mineActions } from '../../actions/mine';

const confirm = Modal.confirm;
const InputGroup = Input.Group;
const Option = Select.Option;

const sortType = {
    'applyTime': 'gmt_create'
}
const orderType = {
    'ascend': 'asc',
    'descend': 'desc'
}
const mapStateToProps = state => {
    const { user, approval, apiManage } = state;
    return { approval, apiManage, user }
};

const mapDispatchToProps = dispatch => ({
    approvalList (params) {
        return dispatch(approvalActions.allApplyList(params));
    },
    updateUserApiStatus (params) {
        return dispatch(mineActions.updateApplyStatus(params));
    }
});

@connect(mapStateToProps, mapDispatchToProps)
class APIApproval extends Component {
    state = {
        pageIndex: 1,
        spVisible: false,
        data: [],
        sorter: {},
        filter: {
            status: null
        },
        userName: '',
        apiName: '',
        spApplyMsg: {},
        total: 0,
        searchType: '用户名称'
    }
    // eslint-disable-next-line
    componentWillMount () {

    }
    componentDidMount () {
        const status = this.props.router.location.query && this.props.router.location.query.status
        let arr = [];
        if (status) {
            arr.push(status.toString())
        }

        this.setState({
            filter: {
                status: (arr && arr.length > 0) ? arr : null
            }
        }, () => {
            this.getApprovalList();
        })
    }
    getApprovalList () {
        this.props.approvalList({
            userName: this.state.userName,
            status: (this.state.filter.status && this.state.filter.status.length > 0) ? this.state.filter.status : null,
            currentPage: this.state.pageIndex,
            pageSize: 20,
            sort: orderType[this.state.sorter.order],
            orderBy: sortType[this.state.sorter.columnKey],
            apiName: this.state.apiName
        })
            .then(
                (res) => {
                    if (res) {
                        this.setState({
                            total: res.data.totalCount
                        })
                    }
                }
            );
    }
    onSourceChange () {

    }
    handleCancel () {
        this.setState({
            spVisible: false,
            spApplyMsg: {},
            mode: null
        })
    }
    // 表格换页/排序
    onTableChange = (page, filter, sorter) => {
        this.setState({
            pageIndex: page.current,
            filter: filter,
            sorter: sorter

        }, () => {
            this.getApprovalList();
        });
    }
    getPagination () {
        return {
            current: this.state.pageIndex,
            pageSize: 20,
            total: this.state.total
        }
    }
    cancelApi (applyId) {
        confirm({
            title: '确认取消?',
            content: '确定取消此用户的API调用权限?',
            onOk: () => {
                this.props.updateUserApiStatus({
                    applyId: applyId,
                    useAdmin: true,
                    status: API_USER_STATUS.DISABLE
                })
                    .then(
                        (res) => {
                            if (res) {
                                message.success('取消成功')
                                this.getApprovalList();
                            }
                        }
                    )
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }
    redoApi (applyId) {
        confirm({
            title: '确认恢复?',
            content: '确定恢复此用户的API调用权限?',
            onOk: () => {
                this.props.updateUserApiStatus({
                    applyId: applyId,
                    useAdmin: true,
                    status: API_USER_STATUS.PASS
                })
                    .then(
                        (res) => {
                            if (res) {
                                message.success('取消成功')
                                this.getApprovalList();
                            }
                        }
                    )
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }
    getDealType (type, record) {
        const detailButton = (<a onClick={this.spShow.bind(this, record, false)}>查看详情</a>);
        const approvalButton = (<a onClick={this.spShow.bind(this, record, true)}>立即审批</a>);
        const cancelButton = (<a onClick={this.cancelApi.bind(this, record.id)}>取消授权</a>);
        const redoButton = (<a onClick={this.redoApi.bind(this, record.id)}>恢复授权</a>);

        let dealView = [];

        switch (type) {
            case API_USER_STATUS.IN_HAND:
                dealView.push(approvalButton);
                break;
            case API_USER_STATUS.PASS:
            case API_USER_STATUS.STOPPED:
                dealView.push(detailButton);
                dealView.push(<span className="ant-divider" ></span>);
                dealView.push(cancelButton);
                break;
            case API_USER_STATUS.DISABLE:
                dealView.push(detailButton);
                dealView.push(<span className="ant-divider" ></span>);
                dealView.push(redoButton);
                break;
            case API_USER_STATUS.EXPIRED:
            case API_USER_STATUS.REJECT:
                dealView.push(detailButton);
                break;
        }
        return <span>{dealView}</span>;
    }
    getSource () {
        return this.props.approval.approvalList;
    }
    initColumns () {
        return [{
            title: '申请人',
            dataIndex: 'applyUserName',
            key: 'applyUserName'
        }, {
            title: '申请API',
            dataIndex: 'apiName',
            key: 'apiName'

        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render (text) {
                const dic = {
                    'notApproved': '未审批',
                    'pass': '已通过',
                    'rejected': '已拒绝',
                    'stop': '已停用',
                    'disabled': '取消授权',
                    'expired': '已过期'
                }
                return <span className={`state-${EXCHANGE_APPLY_STATUS[text]}`}>{dic[EXCHANGE_APPLY_STATUS[text]]}</span>
            },
            filterMultiple: true,
            filters: [
                {
                    text: '未审批',
                    value: '0'
                },
                {
                    text: '已通过',
                    value: '1'
                },
                {
                    text: '已拒绝',
                    value: '2'
                },
                {
                    text: '已停用',
                    value: '3'
                }, {
                    text: '取消授权',
                    value: '4'
                },
                {
                    text: '已过期',
                    value: '5'
                }
            ],
            filteredValue: this.state.filter.status || null
        }, {
            title: '申请说明',
            dataIndex: 'applyContent',
            key: 'applyContent',
            width: '250px'
        }, {
            title: '申请时间',
            dataIndex: 'applyTime',
            key: 'applyTime',
            sorter: true,
            render (text) {
                return utils.formatDateTime(text);
            }
        }, {
            title: '操作',
            dataIndex: 'deal',
            render: (text, record) => {
                return this.getDealType(record.status, record)
            }
        }]
    }
    renderSourceType () {
        return null;
    }
    searchRequire (v) { // 搜索条件
        let { userName, apiName, searchType } = this.state;
        let value = v.trim();// 去掉首位空格
        if (searchType === '用户名称') {
            userName = value;
            apiName = '';
        } else {
            userName = '';
            apiName = value;
        }
        this.setState({
            userName,
            apiName,
            pageIndex: 1
        }, () => {
            this.getApprovalList();
        })
    };
    selectSearchType (v) { // 设置搜索类型
        this.setState({
            searchType: v
        })
    }
    getCardTitle () {
        return (
            <div className="flex font-12">
                <InputGroup compact style={{ width: 500 }} >
                    <Select style={{ marginTop: '10px' }} defaultValue="用户名称" onChange={this.selectSearchType.bind(this)}>
                        <Option value="用户名称">用户名称</Option>
                        <Option value="API名称">API名称</Option>
                    </Select>
                    <Input.Search style={{ width: '50%' }} placeholder="请输入搜索条件" onSearch={this.searchRequire.bind(this)} />
                </InputGroup>
            </div>
        )
    }
    // 审批操作
    sp (isPass) {
        this.props.form.validateFields(
            (err, values) => {
                if (!err) {
                    const applyId = this.state.spApplyMsg.id;
                    const approvalContent = values.APIGroup;
                    this.props.handleApply({
                        applyId: applyId,
                        isPassed: isPass,
                        approvalContent: approvalContent
                    })
                        .then(
                            (res) => {
                                this.setState({
                                    spVisible: false
                                })
                                if (res) {
                                    message.success('审批成功');
                                    this.getApprovalList();
                                }
                            }
                        )
                }
            }
        )
    }
    spShow (record, isApproval) {
        this.setState({
            spVisible: true,
            spApplyMsg: record,
            mode: isApproval ? 'approval' : 'view'
        })
    }
    render () {
        const { spApplyMsg, spVisible, mode } = this.state;

        return (
            <div className="api-approval">
                <h1 className="box-title">审批授权</h1>
                <div className="margin-0-20 m-card">
                    <Card

                        noHovering
                        title={this.getCardTitle()}
                        className="shadow"
                    >
                        <Table
                            rowKey="id"
                            className="m-table monitor-table"
                            columns={this.initColumns()}
                            loading={false}
                            pagination={this.getPagination()}
                            dataSource={this.getSource()}
                            onChange={this.onTableChange}

                        />
                    </Card>
                </div>
                <ApprovalModal
                    mode={mode}
                    spVisible={spVisible}
                    data={spApplyMsg}
                    onCancel={this.handleCancel.bind(this)}
                    onOk={() => {
                        this.handleCancel()
                        this.getApprovalList()
                    }}
                />
            </div>
        )
    }
}
const WrapComponent = Form.create()(APIApproval)
export default WrapComponent
