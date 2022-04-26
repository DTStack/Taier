import * as React from 'react';
import { Table } from 'antd';
import type { ColumnsType } from 'antd/lib/table/interface';

const Api = {} as any

interface IState {
    dataSource: IDataSource[]
}

interface IProps {
    taskId: number;
    style: React.CSSProperties;
}

interface IDataSource {
    host: string;
    ip: string;
    port: string;
}

class RunCodeAddess extends React.Component<IProps, IState> {
    state: IState = {
        dataSource: []
    }

    componentDidMount () {
        this.initAddress(this.props.taskId);
    }

	UNSAFE_componentWillReceiveProps(nextProps: IProps) {
        const { taskId } = nextProps;
        const { taskId: oldTaskId } = this.props;

        if (taskId && oldTaskId != taskId) {
            this.initAddress(taskId);
        }
    }

    initAddress (id: number) {
        if(!id) return
        Api.getContainerInfos({
            taskId: id
        }).then((res: any) => {
            if (res.code == 1) {
                this.setState({
                    dataSource: res?.data || []
                })
            }
        })
    }

    columns: ColumnsType<IDataSource> = [
        {
            dataIndex: 'host',
            title: '主机名',
            width: 200
        },
        {
            dataIndex: 'ip',
            title: 'IP',
            width: 200
        },
        {
            dataIndex: 'port',
            title: '端口',
            width: 200
        }
    ]

    render () {
        const { style } = this.props;
        const { dataSource } = this.state;

        return (
            <div style={{
                ...style,
                paddingLeft: '20px',
                paddingRight: '20px'
            }}
            >
                <Table
                    className="dt-table-border"
                    rowKey={(record: any, index: number | undefined) => { return index! }}
                    columns={this.columns}
                    dataSource={dataSource}
                    pagination={false}
                    scroll={{ y: 500 }}
                />
            </div>
        )
    }
}

export default RunCodeAddess;
