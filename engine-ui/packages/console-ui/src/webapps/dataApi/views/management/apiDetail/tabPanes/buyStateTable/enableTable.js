import React, { Component } from "react";
import { Table } from "antd";
import moment from "moment"

import utils from "utils";
import { EXCHANGE_API_STATUS } from '../../../../../consts';

class EnableTable extends Component {
    state = {
        pageIndex: 1,
        filter: {},
        sortedInfo: {}
    }
    initColumns() {
        return [{
            title: '用户',
            dataIndex: 'userName',
            key: 'userName',
            fixed:"left",
            width:"180px"

        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            fixed:"left",
            width:"80px",
            filters: [
                { text: '正常', value: '1' },
                { text: '停用', value: '3' },
                { text: '取消授权', value: '4' }

            ],
            render: (text, record) => {
                const dic = {
                    success: "正常",
                    stop: "停用",
                    disabled:"取消授权"
                }
             
                return <span className={`state-${EXCHANGE_API_STATUS[text]}`}>{dic[EXCHANGE_API_STATUS[text]]}</span>
            }
        },{
            title: '最大调用次数',
            dataIndex: 'callLimit',
            key: 'callLimit',
            width:"150px"
 
        },{
            title: '调用周期',
            dataIndex: 'callDateRange',
            key: 'callDateRange',
            width:"200px",
            render(text,record){
                return <span>{new moment(record.beginTime).format("YYYY-MM-DD")} ~ {new moment(record.endTime).format("YYYY-MM-DD")}</span>
            }
 
        }, {
            title: '最近24小时调用',
            dataIndex: 'recent24HCallNum',
            key: 'recent24HCallNum',
            width:"150px"

        }, {
            title: '最近24小时失败率',
            dataIndex: 'recent24HFailRate',
            key: 'recent24HFailRate',
            width:"150px",
            render(text){
                return text+"%";
            }
        }, {
            title: '最近7天调用',
            dataIndex: 'recent7DCallNum',
            key: 'recent7DCallNum',
            width:"150px"

        }, {
            title: '最近30天调用',
            dataIndex: 'recent30DCallNum',
            key: 'recent30DCallNum',
            width:"150px"

        }, {
            title: '累计调用',
            dataIndex: 'totalCallNum',
            key: 'totalCallNum',
        }, {
            title: '订购时间',
            dataIndex: 'applyTime',
            key: 'applyTime',
            fixed:"right",
            width:"180px",
            render(text) {
                return utils.formatDateTime(text);
            }

        }, {
            title: '操作',
            dataIndex: '',
            key: 'deal',
            fixed:"right",
            width:"100px",
            render: (text, record) => {
                if (EXCHANGE_API_STATUS[record.status] != "disabled") {
                    return <a onClick={
                        () => {
                            this.props.cancelApi(record.applyId)
                        }
                    }>取消授权</a>
                }
                return <a onClick={
                    () => {
                        this.props.applyApi(record.applyId)
                    }
                }>恢复授权</a>
            }

        }]
    }
    getPagination() {
        return {
            current: this.state.pageIndex,
            pageSize: 10,
            total: this.props.total,
        }
    }

    // 表格换页/排序
    onTableChange = (page, filter, sorter) => {
        this.setState({
            pageIndex: page.current,
            filter: filter,
            sortedInfo: sorter
        });
        this.props.tableChange({
            page: page.current,
            filter: filter,
            sortedInfo: sorter
        })
    }
    lookAllErrorText() {
        console.log("lookAllErrorText")
    }
    render() {
        return (
            <Table
                rowKey="applyId"
                className="m-table monitor-table table-p-l20"
                columns={this.initColumns()}
                loading={this.props.loading}
                pagination={this.getPagination()}
                dataSource={this.props.data}
                onChange={this.onTableChange}
                scroll={{x:1620}}
            />
        )
    }
}
export default EnableTable;