import React, { Component } from "react";
import { Menu, Card, Table, Modal } from "antd";
import SlidePane from "./ApprovedSlidePane";
import SlidePaneDisabled from "./DisabledCardSlidePane"
import SlidePaneDetail from "./detailSlidePane"
import utils from "utils"
const confirm = Modal.confirm;
const exchangeDic = {
    0: 'inhand',
    1: 'success',
    2: 'notPass',
    3: 'stop',
    4: 'disabled'
}

const sortType = {
    "applyTime": 'gmt_modified'
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
        showRecord: {}
    }
    getAppliedList() {
        this.setState({
            loading: true
        })
        this.props.getAppliedList(
            this.state.pageIndex,
            sortType[this.state.sortedInfo.columnKey],
            orderType[this.state.sortedInfo.order],
            this.state.filterInfo.status
        )

            .then(
                (res) => {
     
                    if(this.props.apiId){
                        if(res){
                            for(let i in res.data.data){
                                let item=res.data.data[i];
                                if(this.props.apiId==item.apiId){
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
    componentWillReceiveProps(nextProps){
        if(this.props.apiId!=nextProps.apiId&&nextProps.apiId){
                
                const res=this.getSource();
                if(res){
                    for(let i in res.data){
                        let item=res.data[i];
                        if(nextProps.apiId==item.apiId){
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
            slidePaneShowSuccess: false
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
                        () => {
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
                        () => {
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
        this.props.router.push("/api/market")
    }
    initColumns() {

        return [{
            title: 'API名称',
            dataIndex: 'apiName',
            key: 'apiName',
            render: (text, record) => {
                return <a onClick={this.apiClick.bind(this, record)} >{text}</a>
            }
        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render(text) {

                const dic = {
                    success: "正常",
                    disabled: "禁用",
                    stop: "停用",
                    notPass: "未通过"
                }
                return <span className={`state-${exchangeDic[text]}`}>{dic[exchangeDic[text]]}</span>
            },
            filters: [
                { text: '正常', value: '1' },
                { text: '停用', value: '3' },
                { text: '禁用', value: '4' },
                { text: '未通过', value: '2' }
            ]
        }, {
            title: '描述',
            dataIndex: 'apiDesc',
            key: 'apiDesc',
        }, {
            title: '最近24小时调用(次)',
            dataIndex: 'recentCallNum',
            key: 'recentCallNum',


        }, {
            title: '最近24小时失败率',
            dataIndex: 'recentFailRate',
            key: 'recentFailRate',

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

    render() {
        return (
            <div>

                <Card

                    noHovering
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
                    style={{right:"0px"}}
                        {...this.props}
                        showRecord={this.state.showRecord}
                        slidePaneShow={this.state.slidePaneShowSuccess}
                        closeSlidePane={this.closeSlidePane.bind(this)}
                    >
                    </SlidePaneDetail>
                    <Table
                        rowKey="apiId"
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