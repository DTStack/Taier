import * as React from 'react';
import { Table } from 'antd';

import API from '../../../../../../api/experiment';

class ConfusionTable extends React.Component<any, any> {
    state: any = {
        tableData: [],
        pagination: {
            current: 1,
            pageSize: 10
        },
        loading: false
    };
    componentDidUpdate (prevProps: any, prevState: any) {
        if (this.props.visible && !prevProps.visible) {
            this.fetchData();
        }
    }
    componentDidMount () {
        this.fetchData()
    }
    handleTableChange = (pagination: any, filters: any, sorter: any) => {
        const pager: any = { ...this.state.pagination };
        pager.current = pagination.current;
        this.setState({
            pagination: pager
        });
    }

    fetchData = () => {
        const { data } = this.props;
        if (!data) return;

        this.setState({ loading: true });
        API.getEvaluateReportTableData({
            num: 100,
            taskId: data.id,
            inputType: 32
        }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    tableData: res.data || []
                })
            }
            this.setState({ loading: false });
        });
    }

    initialCols = (fields: any) => {
        const nameMap: any = {
            label: '模型',
            rights: '正确数',
            errors: '错误数',
            counts: '总计',
            accuracy: '准确率',
            precision: '精确率',
            recall: '召回率',
            f1: 'F1指标'
        }
        const columns: any = [];
        if (fields) {
            for (let i = 0; i < fields.length; i++) {
                const field = fields[i];
                columns.push({
                    width: 200,
                    title: nameMap[field] || field,
                    dataIndex: field,
                    key: field,
                    sorter: (a: any, b: any) => {
                        return a[i] - b[i];
                    },
                    render: (text: any, record: any) => {
                        if (['accuracy', 'precision', 'recall', 'f1'].indexOf(field) > -1) {
                            return `${(record[i] * 100).toFixed(2)}%`
                        }
                        return record[i];
                    }
                })
            }
        }
        return columns;
    }

    render () {
        const { tableData } = this.state;
        const cols = this.initialCols(tableData[0]);
        const dataSource = tableData.length > 1 ? tableData.slice(1, tableData.length) : [];
        const scroll: any = { x: cols.length < 5 ? true : 2000, y: 250 };
        return (
            <Table
                className="m-table border-table"
                columns={cols}
                rowKey={(record: any, index: any) => {
                    const rowKey = `${record[index]}-${index}`
                    return rowKey;
                }}
                style={{ height: '100%' }}
                dataSource={dataSource}
                pagination={this.state.pagination}
                loading={this.state.loading}
                onChange={this.handleTableChange}
                scroll={scroll}
            >
            </Table>
        )
    }
}
export default ConfusionTable;
