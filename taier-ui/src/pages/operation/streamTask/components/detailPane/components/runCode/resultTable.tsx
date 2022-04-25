import * as React from 'react';
import { Table } from 'antd';
import type { ColumnsType } from 'antd/lib/table/interface';

const Api = {} as any

interface IProps {
    taskId: number;
}

interface IState {
    loading: boolean;
    data: IDataSource[];
}

interface IDataSource {
    tableName: string;
}


class ResultTable extends React.Component<IProps, IState> {
    state: IState = {
        data: [],
        loading: false
    }

    componentDidMount () {
        this.initData();
    }

    async initData () {
        const { taskId } = this.props;
        if (!taskId) return
        this.setState({
            loading: true
        })
        try {
            let res = await Api.getResultTable({ taskId });
            if (res?.code == 1) {
                this.setState({
                    data: res.data || []
                })
            }
        } finally {
            this.setState({
                loading: false
            })
        }
    }

    columns: ColumnsType<IDataSource> = [
        {
            title: '表名',
            key: 'tableName'
        }
    ]

    render () {
        const { data } = this.state;

        return (
            <div>
                <Table
                    columns={this.columns}
                    dataSource={data}
                    pagination={false}
                />
            </div>
        )
    }
}

export default ResultTable;
