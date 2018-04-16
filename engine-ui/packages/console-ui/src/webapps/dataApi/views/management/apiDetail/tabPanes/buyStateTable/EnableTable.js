import React, { Component } from "react";
import { Table } from "antd"
import utils from "utils";
import { EXCHANGE_ADMIN_API_STATUS } from '../../../../../consts';
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
            key: 'userName'

        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            filters: [
                { text: '正常', value: '0' },
                { text: '禁用', value: '1' }

            ],
            render: (text, record) => {
                const dic = {
                    success: "正常",
                    stop: "禁用",
                }
                return <span className={`state-${EXCHANGE_ADMIN_API_STATUS[text]}`}>{dic[EXCHANGE_ADMIN_API_STATUS[text]]}</span>
            }
        }, {
            title: '最近24小时调用',
            dataIndex: 'recent24HCallNum',
            key: 'recent24HCallNum'

        }, {
            title: '最近24小时失败率',
            dataIndex: 'recent24HFailRate',
            key: 'recent24HFailRate'
        }, {
            title: '最近7天调用',
            dataIndex: 'recent7DCallNum',
            key: 'recent7DCallNum'

        }, {
            title: '最近30天调用',
            dataIndex: 'recent30DCallNum',
            key: 'recent30DCallNum'

        }, {
            title: '累计调用',
            dataIndex: 'totalCallNum',
            key: 'totalCallNum'

        }, {
            title: '订购时间',
            dataIndex: 'applyTime',
            key: 'applyTime',
            render(text) {
                return utils.formatDateTime(text);
            }

        }, {
            title: '操作',
            dataIndex: '',
            key: 'deal',
            render: (text, record) => {
                if (record.status == "success") {
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
            pageSize: 5,
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
                rowKey="userId"
                className="m-table monitor-table table-p-l20"
                columns={this.initColumns()}
                loading={this.props.loading}
                pagination={this.getPagination()}
                dataSource={this.props.data}
                onChange={this.onTableChange}
            />
        )
    }
}
export default EnableTable;