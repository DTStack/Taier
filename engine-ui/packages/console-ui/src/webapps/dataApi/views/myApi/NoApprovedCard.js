import React, { Component } from "react";
import { Menu, Card, Table, Input } from "antd"
import SlidePane from "./ApprovedSlidePane";
import utils from "utils"
const sortType = {
    "applyTime": 'gmt_modified'
}
const orderType = {
    "ascend": 'asc',
    "descend": 'desc'
}
const TextArea = Input.TextArea;
class NoApprovedCard extends Component {
    state = {
        pageIndex: 1,
        slidePaneShow: false,
        sortedInfo: {},
        loading: false,
        showRecord: {}
    }
    getApplyingList(callback) {
        this.setState({
            loading: true
        })
        this.props.getApplyingList(this.state.pageIndex, sortType[this.state.sortedInfo.columnKey], orderType[this.state.sortedInfo.order])
            .then(
                () => {
                    this.setState({
                        loading: false
                    })
                    if(callback){
                        
                        callback();
                    }
                }
            );
    }
    componentDidMount() {
        this.getApplyingList(
            ()=>{
                this.openCard(this.props.apiId);
            }
        );
    }
    componentWillReceiveProps(nextProps) {

        if (this.props.apiId != nextProps.apiId && nextProps.apiId) {

            this.openCard(nextProps.apiId);

        }
    }
    openCard(apiId) {
   
        const res = this.getSource();
        if (res) {
            
            for (let i in res) {
                let item = res[i];
                
                if (apiId == item.apiId) {
                    
                    this.openApprovedState(item);
                    break;
                }
            }
        }
    }
    // 表格换页/排序
    onTableChange = (page, filter, sorter) => {
        console.log(sorter)
        this.setState({
            pageIndex: page.current,
            sortedInfo: sorter
        },
            () => {
                this.getApplyingList();
            });


    }
    openApprovedState(record) {
        this.setState({
            slidePaneShow: true,
            showRecord: record || {}
        })

    }
    initColumns() {
        const sortedInfo = this.state.sortedInfo;
        return [{
            title: 'API名称',
            dataIndex: 'apiName',
            key: 'apiName',
            render: (text, record) => {
                return <a onClick={this.openApprovedState.bind(this, record)} >{text}</a>
            }
        }, {
            title: '描述',
            dataIndex: 'apiDesc',
            key: 'apiDesc',
        }, {
            title: '申请说明',
            dataIndex: 'applyContent',
            key: 'applyContent',
            width:"250px"

        }, {
            title: '申请时间',
            dataIndex: 'applyTime',
            key: 'applyTime',
            sorter: true,
            render(text) {
                return utils.formatDateTime(text);
            }
        }]
    }
    getSource() {
        return this.props.mine.apiList.applyingList.data;
    }
    getTotal() {
        return (this.props.mine.apiList.applyingList && this.props.mine.apiList.applyingList.totalCount) || 0
    }
    getPagination() {
        return {
            current: this.state.pageIndex,
            pageSize: 20,
            total: this.getTotal(),
        }
    }
    closeSlidePane() {
        this.setState({
            slidePaneShow: false
        })
    }
    render() {
        return (

            <Card
                noHovering
            >

                <SlidePane
                    {...this.props}
                    slidePaneShow={this.state.slidePaneShow}
                    showRecord={this.state.showRecord}
                    closeSlidePane={this.closeSlidePane.bind(this)}
                ></SlidePane>
                <Table
                    rowKey="id"
                    rowClassName={
                        (record, index)=>{
                            if(this.state.showRecord.apiId==record.apiId){
                                return "row-select"
                            }else{
                                return "";
                            }
                        }
                    }
                    className="m-table monitor-table"
                    columns={this.initColumns()}
                    loading={this.state.loading}
                    pagination={this.getPagination()}
                    dataSource={this.getSource()}
                    onChange={this.onTableChange}
                />
            </Card>

        )
    }
}
export default NoApprovedCard;