import React, { Component } from 'react';
import { Menu, Card, Table, Input } from 'antd'
import utils from 'utils'
class NewApiDataSourceTable extends Component {
    state = {
        pageIndex: 1,
        loading: false
    }
    // 表格换页/排序
    onTableChange = (page, filter, sorter) => {
        this.setState({
            pageIndex: page.current,
            sortedInfo: sorter,
            loading: true
        });

        setTimeout(
            () => {
                this.setState({
                    loading: false
                })
            }, 1000
        )
    }
    initColumns () {
        const columns = this.props.data.columnList;
        if (!columns) {
            return null;
        }
        let arr = [];
        for (let i = 0; i < columns.length; i++) {
            arr.push({
                key: i,
                dataIndex: i,
                title: columns[i],
                width: (columns[i].length * 12 + 28) + 'px',
                render: function (text) {
                    return (
                        <span title={text}>{utils.textOverflowExchange(text, 30)}</span>
                    );
                }
            })
        }
        console.log(arr);
        return arr;
    }
    getSource () {
        const dataList = this.props.data.dataList;
        if (!dataList) {
            return null;
        }
        let arr = [];
        for (let i = 0; i < dataList.length; i++) {
            let dic = {

            };
            let item = dataList[i];
            for (let j = 0; j < item.length; j++) {
                dic[j] = item[j]
            }

            arr.push(dic);
        }
        console.log(arr);
        return arr;
    }
    getPagination () {
        return {
            current: this.state.pageIndex,
            pageSize: 20,
            total: 30
        }
    }
    getScroll () {
        let i = 100;
        const columnList = this.props.data.columnList;
        for (let j in columnList) {
            let item = columnList[j];
            i = i + item.length * 12 + 28
        }
        return i + 'px';
    }

    render () {
        return (

            <Table
                rowKey={(record, index) => index}
                loading={this.props.loading}
                className="m-table monitor-table"
                columns={this.initColumns()}
                loading={this.state.loading}
                pagination={false}
                dataSource={this.getSource()}
                onChange={this.onTableChange}
                scroll={{ x: this.getScroll(), y: 400 }}
            />

        )
    }
}
export default NewApiDataSourceTable;
