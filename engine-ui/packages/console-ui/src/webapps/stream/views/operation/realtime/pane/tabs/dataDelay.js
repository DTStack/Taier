import React from "react"
import utils from "utils"

import { Table } from "antd"
import Api from "../../../../../api"
import DetailModal from "./delay/detailModal";

class DataDelay extends React.Component {
    state = {
        pagination: {
            total: 0,
            pageSize: 10,
            current: 1
        },
        delayList: [],
        loading: false,
        detailVisible:false,
        detailRecord:{},
        sorter:{}
    }
    componentDidMount() {
        console.log("DataDelay")
        this.getDelayList();
    }
    initPage() {
        this.setState({
            pagination: {
                total: 0,
                pageSize: 10,
                current: 1
            }
        })
    }
    componentWillReceiveProps(nextProps) {
        const { data = {} } = this.props;
        const { data: nextData = {} } = nextProps;
        if (data.id != nextData.id
        ) {
            this.initPage();
            this.getDelayList(nextData);
        }
    }
    getDelayList(data) {
        const { pagination, sorter } = this.state;
        data = data || this.props.data;

        this.setState({
            delayList: []
        })

        if (!data) {
            return;
        }

        let extParams={};
        extParams.orderBy=sorter.columnKey;
        extParams.sort=utils.exchangeOrder(sorter.order);
        this.setState({
            loading: true
        })

        Api.getDelayList({
            taskId: data.id,
            currentPage: pagination.current,
            pageSize: pagination.pageSize,
            ...extParams
        }).then(
            (res) => {
                if (res.code == 1) {
                    this.setState({
                        delayList: res.data.data,
                        pagination:{
                            ...pagination,
                            total:res.data.totalCount
                        }
                    })
                }
                this.setState({
                    loading: false
                })
            }
        )
    }
    initDelayListColumns() {
        return [{
            title: '分区ID',
            dataIndex: 'partitionId',
            width:180
        }, {
            title: '延迟消息数',
            dataIndex: 'delayCount',
            sorter:true,
            width:180
        }, {
            title: '总消息数',
            dataIndex: 'totalCount',
            sorter:true,
            width:180
        }, {
            title: '当前消费位置',
            dataIndex: 'currentLocal',
        }, {
            title: '操作',
            dataIndex: 'deal',
            render:(text,record)=>{
                return <a onClick={this.showDetail.bind(this,record)}>查看详情</a>
            }
        }]
    }
    showDetail(record){
        this.setState({
            detailRecord:record,
            detailVisible:true
        })
    }
    closeDetail(){
        this.setState({
            detailRecord:{},
            detailVisible:false
        })
    }
    onTableChange(page, filters,sorter){
        const {pagination} =this.state;
        console.log(sorter);
        this.setState({
            pagination: {
                ...pagination,
                current: page.current
            },
            sorter:sorter
        },this.getDelayList.bind(this))
    }
    render() {
        const { pagination, loading, delayList, detailVisible,detailRecord } = this.state;
        const {data={}} = this.props;
        return (
            <div style={{ padding: "21px 20px 20px 25px" }}>
                <Table
                    rowKey="partitionId"
                    className="m-table"
                    columns={this.initDelayListColumns()}
                    dataSource={delayList}
                    pagination={pagination}
                    loading={loading}
                    onChange={this.onTableChange.bind(this)}
                />
                <DetailModal 
                    visible={detailVisible}
                    closeDetail={this.closeDetail.bind(this)}
                    taskId={data.id}
                    partitionId={detailRecord.partitionId}
                />
            </div>
        )
    }
}

export default DataDelay;