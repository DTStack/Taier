import React, { Component } from "react";
import { Menu, Card, Table, Modal, message,Input } from "antd";
import SlidePane from "./approvedSlidePane";
import SlidePaneDisabled from "./disabledCardSlidePane"
import SlidePaneDetail from "./detailSlidePane"
import utils from "utils"
const confirm = Modal.confirm;
const Search = Input.Search;
const exchangeDic = {
    0: 'inhand',
    1: 'success',
    2: 'notPass',
    3: 'stop',
    4: 'disabled'
}

const sortType = {
    "applyTime": 'gmt_create'
}
const orderType = {
    "ascend": 'asc',
    "descend": 'desc'
}
class ApprovedCard extends Component {
    state = {
        pageIndex: 1,
        slidePaneShowNoApproved: false,
        slidePaneShowDisabled: false,
        slidePaneShowSuccess: false,
        loading: false,
        sortedInfo: {},
        filterInfo: {},
        showRecord: {},
        apiName:""

    }
    getAppliedList() {
        this.setState({
            loading: true
        })
        this.props.getAppliedList(
            this.state.pageIndex,
            sortType[this.state.sortedInfo.columnKey],
            orderType[this.state.sortedInfo.order],
            this.state.filterInfo.status,
            this.state.apiName
        )

            .then(
                (res) => {

                    if (this.props.tagId) {
                        if (res) {
                            for (let i in res.data.data) {
                                let item = res.data.data[i];
                                if (this.props.tagId == item.tagId) {
                                    this.apiClick(item);
                                    break;
                                }
                            }
                        }
                    }
                    this.setState({
                        loading: false
                    })
                }
            );
    }
    componentDidMount() {
        this.getAppliedList();

    }
    componentWillReceiveProps(nextProps) {
        if (this.props.tagId != nextProps.tagId && nextProps.tagId) {

            const res = this.getSource();
            if (res) {
                for (let i in res.data) {
                    let item = res.data[i];
                    if (nextProps.tagId == item.tagId) {
                        this.dealClick(item);
                        break;
                    }
                }
            }

        }
    }
    // 表格换页/排序
    onTableChange = (page, filter, sorter) => {
        console.log(filter);
        this.setState({
            pageIndex: page.current,
            sortedInfo: sorter,
            filterInfo: filter
        },
            () => {
                this.getAppliedList();
            });
    }
    //关闭pane
    closeSlidePane() {
        this.setState({
            slidePaneShowNoApproved: false,
            slidePaneShowDisabled: false,
            slidePaneShowSuccess: false,
            showRecord:{}
        })
    }
    apiClick(record) {
        const method = this['state' + exchangeDic[record.status]]
        if (method) {
            method.call(this, record);
        }

    }
    statesuccess(record) {

        this.setState({
            slidePaneShowSuccess: true,
            slidePaneShowNoApproved: false,
            slidePaneShowDisabled: false,
            showRecord: record || {}

        })
    }
    statenotPass(record) {
        this.setState({
            slidePaneShowSuccess: false,
            slidePaneShowNoApproved: true,
            slidePaneShowDisabled: false,
            showRecord: record || {}
        })
    }
    statestop(record) {
        this.setState({
            slidePaneShowSuccess: true,
            slidePaneShowNoApproved: false,
            slidePaneShowDisabled: false,
            showRecord: record || {}
        })
    }
    statedisabled(record) {
        this.setState({
            slidePaneShowSuccess: false,
            slidePaneShowNoApproved: false,
            slidePaneShowDisabled: true,
            showRecord: record || {}
        })
    }
    dealClick(record) {
        const method = this['deal' + exchangeDic[record.status]];
        if (method) {
            method.call(this, record);
        }
    }
    dealsuccess(record) {
        confirm({
            title: '确认停止',
            content: '确认停止接口？',
            onOk: () => {
                this.props.updateApplyStatus(record.id, 3).
                    then(
                        (res) => {
                            if (res) {
                                message.success("停止成功")
                            }
                            this.getAppliedList();
                        }
                    )
            },
            onCancel() {
                console.log('Cancel');
            },
        });


    }
    dealstop(record) {
        confirm({
            title: '确认开启',
            content: '确认开启接口？',
            onOk: () => {
                this.props.updateApplyStatus(record.id, 1).
                    then(
                        (res) => {
                            if (res) {
                                message.success("开启成功")
                            }
                            this.getAppliedList();
                        }
                    )
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    dealnotPass(record) {
        this.props.router.push("/dl/market")
    }
    initColumns() {

        return [{
            title: '标签名称',
            dataIndex: 'apiName',
            key: 'apiName',
            render: (text, record) => {
                const isDelete = record.apiStatus == 1 ? true : false;
                const deleteText = isDelete ? '(全平台禁用)' : ''
                return <a className={isDelete?'disable-all':''} onClick={this.apiClick.bind(this, record)} >{text + deleteText}</a>
            }
        }, {
            title: '授权状态',
            dataIndex: 'status',
            key: 'status',
            render(text) {

                const dic = {
                    success: "已通过",
                    disabled: "取消授权",
                    stop: "停用",
                    notPass: "已拒绝"
                }
                return <span className={`state-${exchangeDic[text]}`}>{dic[exchangeDic[text]]}</span>
            },
            filters: [
                { text: '已通过', value: '1' },
                { text: '已拒绝', value: '2' },
                { text: '停用', value: '3' },
                { text: '取消授权', value: '4' },

            ]
        }, {
            title: '描述',
            dataIndex: 'apiDesc',
            key: 'apiDesc',
            width: 300
        }, {
            title: '最近24小时调用(次)',
            dataIndex: 'recentCallNum',
            key: 'recentCallNum',


        }, {
            title: '最近24小时失败率',
            dataIndex: 'recentFailRate',
            key: 'recentFailRate',
            render(text) {
                return text + "%"
            }

        },
        {
            title: '累计调用',
            dataIndex: 'totalCallNum',
            key: 'totalCallNum',

        }, {
            title: '订购时间',
            dataIndex: 'applyTime',
            key: 'applyTime',
            sorter: true,
            render(text) {
                return utils.formatDateTime(text)
            }
        }, {
            title: '操作',
            dataIndex: 'deal',
            key: 'deal',
            render: (text, record) => {

                const dic = {
                    success: "停用",
                    disabled: "",
                    stop: "启用",
                    notPass: "再次申请"
                }

                if (dic[exchangeDic[record.status]]) {
                    return <a onClick={this.dealClick.bind(this, record)}>{dic[exchangeDic[record.status]]}</a>
                }
                return null;

            }
        }]
    }
    getSource() {
        return this.props.mine.apiList.appliedList.data || [];
    }
    getTotal() {
        return (this.props.mine.apiList.appliedList && this.props.mine.apiList.appliedList.totalCount) || 0
    }
    getPagination() {
        return {
            current: this.state.pageIndex,
            pageSize: 20,
            total: this.getTotal(),
        }
    }
    handleApiSearch(key) {
        this.setState({
            apiName: key,
            pageIndex:1
        },
            () => {
                this.getAppliedList();
            })
    }
    render() {

        return (
            <div>
                <Card
                    noHovering
                    bordered={false}
                >
                    <SlidePane
                        {...this.props}
                        isApproved={true}
                        showRecord={this.state.showRecord}
                        slidePaneShow={this.state.slidePaneShowNoApproved}
                        closeSlidePane={this.closeSlidePane.bind(this)}
                    ></SlidePane>
                    <SlidePaneDisabled
                        {...this.props}
                        showRecord={this.state.showRecord}
                        slidePaneShow={this.state.slidePaneShowDisabled}
                        closeSlidePane={this.closeSlidePane.bind(this)}
                    >
                    </SlidePaneDisabled>
                    <SlidePaneDetail
                        style={{ right: "0px" }}
                        {...this.props}
                        showRecord={this.state.showRecord}
                        slidePaneShow={this.state.slidePaneShowSuccess}
                        closeSlidePane={this.closeSlidePane.bind(this)}
                    >
                    </SlidePaneDetail>
                    <div className="flex font-12">
                        <Search
                            placeholder="输入API名称搜索"
                            style={{ width: 150, margin: '10px 0px',marginLeft:"10px" }}
                            onSearch={this.handleApiSearch.bind(this)}
                        />
                    </div>
                    <Table
                        rowClassName={
                            (record, index) => {
                                if (this.state.showRecord.tagId == record.tagId) {
                                    return "row-select"
                                } else {
                                    return "";
                                }
                            }
                        }
                        rowKey="tagId"
                        className="m-table monitor-table"
                        columns={this.initColumns()}
                        loading={this.state.loading}
                        pagination={this.getPagination()}
                        dataSource={this.getSource()}
                        onChange={this.onTableChange}
                    />
                </Card>
            </div>
        )
    }
}
export default ApprovedCard;