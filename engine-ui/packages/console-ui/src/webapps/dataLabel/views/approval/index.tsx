import * as React from 'react'
import { Card, Input, Table, Modal, Form, Button, message } from 'antd';
import { connect } from 'react-redux';
import utils from 'utils';
import { formItemLayout, EXCHANGE_APPLY_STATUS } from '../../consts'
import { approvalActions } from '../../actions/approval';
const TextArea = Input.TextArea;
const Search = Input.Search;
const FormItem = Form.Item;

const sortType: any = {
    'applyTime': 'gmt_modified'
}
const orderType: any = {
    'ascend': 'asc',
    'descend': 'desc'
}
const mapStateToProps = (state: any) => {
    const { user, approval, apiManage } = state;
    return { approval, apiManage, user }
};

const mapDispatchToProps = (dispatch: any) => ({
    approvalList(params: any) {
        return dispatch(approvalActions.allApplyList(params));
    },
    handleApply(params: any) {
        return dispatch(approvalActions.handleApply(params));
    }
});

@(connect(mapStateToProps, mapDispatchToProps) as any)
class APIApproval extends React.Component<any, any> {
    state: any = {
        pageIndex: 1,
        spVisible: false,
        msgVisible: false,
        data: [],
        sorter: {},
        filter: {
            status: null
        },
        userName: '',
        applyContent: '',
        replyContent: '',
        apiName: undefined,
        spApplyMsg: {},
        total: 0
    }
    componentDidMount () {
        const status = this.props.router.location.query && this.props.router.location.query.status
        let arr: any = [];
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
            status: this.state.filter.status && this.state.filter.status[0],
            currentPage: this.state.pageIndex,
            pageSize: 20,
            sort: orderType[this.state.sorter.order],
            orderBy: sortType[this.state.sorter.columnKey],
            apiName: this.state.apiName
        })
            .then(
                (res: any) => {
                    if (res) {
                        this.setState({
                            total: res.data.totalCount
                        })
                    }
                }
            );
    }

    handleSearch(key: any) {
        this.setState({
            userName: key,
            pageIndex: 1
        },
        () => {
            this.getApprovalList();
        })
    }

    handleApiSearch(key: any) {
        this.setState({
            apiName: key,
            pageIndex: 1
        },
        () => {
            this.getApprovalList();
        })
    }

    onSourceChange () {
    }

    handleCancel () {
        this.setState({
            spVisible: false,
            msgVisible: false
        })
    }
    // 表格换页/排序
    onTableChange = (page: any, filter: any, sorter: any) => {
        this.setState({
            pageIndex: page.current,
            filter: filter,
            sorter: sorter

        },
        () => {
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
    getDealType(type: any) {
        const dic: any = {
            'notApproved': '立即审批',
            'pass': '查看详情',
            'rejected': '查看详情',
            'stop': '查看详情',
            'disabled': '查看详情'

        }
        return dic[type || 'nothing']
    }

    deal(record: any) {
        const methodKey = 'deal' + EXCHANGE_APPLY_STATUS[record.status];
        const method: any = this[methodKey as keyof this];
        if (method) {
            method.call(this, record);
        }
    }

    dealrejected(record: any) {
        this.lookDetail(record);
    }

    dealstop(record: any) {
        this.lookDetail(record);
    }

    dealdisabled(record: any) {
        this.lookDetail(record);
    }

    dealpass(record: any) {
        this.lookDetail(record);
    }

    dealnotApproved(record: any) {
        this.spShow(record);
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
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render(text: any) {
                const dic: any = {
                    'notApproved': '未审批',
                    'pass': '已通过',
                    'rejected': '已拒绝',
                    'stop': '停用',
                    'disabled': '取消授权'
                }
                return <span className={`state-${EXCHANGE_APPLY_STATUS[text]}`}>{dic[EXCHANGE_APPLY_STATUS[text]]}</span>
            },
            filterMultiple: false,
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
                    text: '停用',
                    value: '3'
                }, {
                    text: '取消授权',
                    value: '4'
                }
            ],
            filteredValue: this.state.filter.status || null
        }, {
            title: '申请标签',
            dataIndex: 'apiName',
            key: 'apiName'
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
            render(text: any) {
                return utils.formatDateTime(text);
            }
        }, {
            title: '操作',
            dataIndex: 'deal',
            render: (text: any, record: any) => {
                return <a onClick={this.deal.bind(this, record)}>{this.getDealType(EXCHANGE_APPLY_STATUS[record.status])}</a>
            }
        }]
    }
    renderSourceType (): any {
        return null;
    }
    getCardTitle () {
        return (
            <div className="flex font-12">
                <Search
                    placeholder="输入用户名称搜索"
                    style={{ width: 150, margin: '10px 0' }}
                    onSearch={this.handleSearch.bind(this)}
                />

                <Search
                    placeholder="输入标签名称搜索"
                    style={{ width: 150, margin: '10px 0px', marginLeft: '30px' }}
                    onSearch={this.handleApiSearch.bind(this)}
                />
            </div>
        )
    }
    // 审批操作
    sp(isPass: any) {
        this.props.form.validateFields(
            (err: any, values: any) => {
                if (!err) {
                    const applyId = this.state.spApplyMsg.id;
                    const approvalContent = values.APIGroup;
                    this.props.handleApply({
                        applyId: applyId,
                        isPassed: isPass,
                        approvalContent: approvalContent
                    })
                        .then(
                            (res: any) => {
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
    spShow(record: any) {
        this.props.form.resetFields();
        this.setState({
            spVisible: true,
            spApplyMsg: record
        })
    }

    lookDetail(record: any) {
        this.setState({
            msgVisible: true,
            applyContent: record.applyContent,
            replyContent: record.replyContent
        })
    }
    render () {
        const { getFieldDecorator } = this.props.form
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
                <Modal
                    title="审批授权"
                    visible={this.state.spVisible}
                    onCancel={this.handleCancel.bind(this)}
                    footer={
                        (
                            <div>
                                <Button type="danger" onClick={this.sp.bind(this, false)}>拒绝</Button>
                                <Button type="primary" onClick={this.sp.bind(this, true)}>同意</Button>
                            </div>
                        )
                    }
                >
                    <Form>
                        <FormItem
                            {...formItemLayout}
                            label="申请说明"
                        >
                            <TextArea disabled value={this.state.spApplyMsg.applyContent} />
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="审批说明"

                        >
                            {getFieldDecorator('APIGroup', {
                                rules: [
                                    { required: true, message: '请填写审批说明' },
                                    { max: 200, message: '最大字数不能超过200' }
                                ]

                            })(
                                <TextArea />
                            )
                            }
                        </FormItem>
                    </Form>
                </Modal>
                <Modal
                    title="审批详情"
                    visible={this.state.msgVisible}
                    footer={<Button type="primary" onClick={this.handleCancel.bind(this)}>关闭</Button>}
                    onCancel={this.handleCancel.bind(this)}>
                    <Form>
                        <FormItem
                            {...formItemLayout}
                            label="申请说明"
                        >
                            <TextArea disabled value={this.state.applyContent} />
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="审批说明"
                        >
                            <TextArea disabled value={this.state.replyContent} />
                        </FormItem>
                    </Form>
                </Modal>
            </div>
        )
    }
}
const WrapComponent = Form.create<any>()(APIApproval)
export default WrapComponent
