import * as React from 'react';

import { Table } from 'antd';

import Api from '../../../../../../api'

class ResultTable extends React.Component<any, any> {
    state: any = {
        data: [],
        pagination: false,
        loading: false
    }
    componentDidMount () {
        this.initData();
    }
    async initData () {
        const { taskId } = this.props;
        if (!taskId) {
            return;
        }
        let params: any = {
            taskId: taskId
        };
        this.setState({
            loading: true
        })
        try {
            let res = await Api.getResultTable(params);
            if (res && res.code == 1) {
                this.setState({
                    data: res.data
                })
            }
        } finally {
            this.setState({
                loading: false
            })
        }
    }
    initColumns () {
        return [{
            title: '表名',
            key: 'tableName'
        }]
    }
    render () {
        const { data, pagination } = this.state;
        return (
            <div>
                <Table
                    columns={this.initColumns()}
                    dataSource={data || []}
                    pagination={pagination}
                />
            </div>
        )
    }
}
export default ResultTable;
