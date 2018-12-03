import React from 'react';

import { Table } from 'antd';

import Api from '../../../../../../api'

class RunCodeAddess extends React.Component {
    state = {
        data: []
    }
    componentDidMount () {
        this.initAddress(this.props.taskId);
    }
    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps (nextProps) {
        const { taskId } = nextProps;
        const { taskId: oldTaskId } = this.props;

        if (taskId && oldTaskId != taskId) {
            this.initAddress(taskId);
        }
    }
    initAddress (id) {
        if (id) {
            Api.getContainerInfos({
                taskId: id
            }).then((res) => {
                if (res.code == 1) {
                    this.setState({
                        data: res.data
                    })
                }
            })
        }
    }
    initColumns () {
        return [
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
    }
    render () {
        const { style } = this.props;
        const { data } = this.state;

        return (
            <div style={{
                ...style,
                paddingLeft: '20px',
                paddingRight: '20px'
            }}
            >
                <Table
                    className="m-table"
                    rowKey={(record, index) => { return index; }}
                    columns={this.initColumns()}
                    dataSource={data}
                    pagination={false}
                    scroll={{ y: 500 }}
                />
            </div>
        )
    }
}

export default RunCodeAddess;
