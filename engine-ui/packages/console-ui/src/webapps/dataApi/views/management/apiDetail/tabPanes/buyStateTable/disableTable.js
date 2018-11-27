import React, { Component } from 'react';
import moment from 'moment';
import { Table } from 'antd'
import utils from 'utils';
class DisableTable extends Component {
    state = {
        pageIndex: 1,
        filter: {},
        sortedInfo: {}
    }
    initColumns () {
        return [{
            title: '用户',
            dataIndex: 'userName',
            key: 'userName',
            width: '180px'

        }, {
            title: '最大调用次数',
            dataIndex: 'callLimit',
            key: 'callLimit',
            width: '100px',
            render (text) {
                return text == -1 ? '无限制' : text;
            }
        }, {
            title: '调用周期',
            dataIndex: 'callDateRange',
            key: 'callDateRange',
            width: '200px',
            render (text, record) {
                const beginTime = record.beginTime ? new moment(record.beginTime).format('YYYY-MM-DD') : null;
                const endTIme = record.endTime ? new moment(record.endTime).format('YYYY-MM-DD') : null;
                const time = beginTime ? `${beginTime} ~ ${endTIme}` : '无限制'
                return <span>{time}</span>
            }

        }, {
            title: '最近24小时调用',
            dataIndex: 'recent24HCallNum',
            key: 'recent24HCallNum'

        }, {
            title: '最近24小时失败率',
            dataIndex: 'recent24HFailRate',
            key: 'recent24HFailRate',
            render (text) {
                return text + '%';
            }
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
            width: '200px',
            render (text) {
                return utils.formatDateTime(text);
            }

        }]
    }
    getPagination () {
        return {
            current: this.state.pageIndex,
            pageSize: 10,
            total: this.props.total
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
    lookAllErrorText () {
        console.log('lookAllErrorText')
    }
    render () {
        return (
            <Table
                rowKey="applyId"
                className="m-table monitor-table table-p-l20"
                columns={this.initColumns()}
                loading={this.props.loading}
                pagination={this.getPagination()}
                dataSource={this.props.data}
                onChange={this.onTableChange}
                scroll={{ x: 1200 }}
            />
        )
    }
}
export default DisableTable;
