import * as React from 'react';
import { Table } from 'antd'

export default class PaneData extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            paginationParams: {
                current: 1,
                total: 0,
                pageSize: 10
            },
            dataList: [],
            tableCol: [],
            previewList: []
        }
    }
    componentDidMount () {
        this.processData(this.props.data);
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        if (nextProps.data != this.props.data) {
            this.setState({
                tableCol: [],
                dataList: []
            }, () => {
                this.processData(nextProps.data);
            })
        }
    }

    processData = (list: any) => {
        if (list.length === 0) return;
        let { dataList, paginationParams, tableCol } = this.state;

        list[0].map((o: any) => {
            tableCol.push({
                title: o,
                dataIndex: o,
                width: o.length * 10 + 10
            })
        })

        // list.shift();
        let sh = list.slice(1);
        for (let item in sh) {
            let j: any = {};
            let row: any = [];
            sh[item].map((o: any, i: any) => {
                let key = tableCol[i].dataIndex;
                j[key] = o && o.toString();
                row.push(j)
            })
            dataList.push(row.pop())
        }

        paginationParams.current = 1;
        paginationParams.total = list.length - 1;

        this.setState({
            tableCol: tableCol,
            dataList: dataList,
            paginationParams: paginationParams
        })
    }

    handleTableChange = (pagination: any, sorter: any, filter: any) => {
        let { paginationParams } = this.state;

        paginationParams.current = pagination.current;

        this.setState({
            paginationParams: paginationParams
        })
    }
    getTableX (tableCol: any) {
        let base = 100;
        if (tableCol) {
            for (let i = 0; i < tableCol.length; i++) {
                base = base + 10 * tableCol[i].length;
            }
        }
        return base;
    }
    render () {
        // const {previewList} = this.props;
        const { paginationParams, dataList, tableCol } = this.state;
        return (
            <div className="partition-container">
                <Table
                    size="small"
                    columns={tableCol}
                    scroll={{ x: this.getTableX() }}
                    dataSource={dataList}
                    rowKey={(record: any, index: any) => {
                        return index;
                    }}
                    pagination={paginationParams}
                    onChange={this.handleTableChange} />
            </div>
        )
    }
}
