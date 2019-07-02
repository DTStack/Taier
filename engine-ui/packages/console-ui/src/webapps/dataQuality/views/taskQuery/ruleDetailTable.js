import React from 'react';

import { Modal, Table, Select } from 'antd';

import Api from '../../api/taskQuery'

const Option = Select.Option;

class RuleDetailTableModal extends React.Component {
    _key = null;
    onCancel = () => {
        this._key = Math.random();
        this.props.onCancel();
    }
    render () {
        const { visible, ...others } = this.props;
        return (
            <Modal
                visible={visible}
                footer={null}
                onCancel={this.onCancel}
                key={this._key}
                width='800'
                title='查看明细'
            >
                <RuleDetailTable
                    {...others}
                />
            </Modal>
        )
    }
}
class RuleDetailTable extends React.Component {
    state = {
        dataSource: [],
        pagination: {
            current: 1,
            total: 0,
            pageSize: 10
        },
        timeList: [],
        tableName: null,
        cyctime: null,
        loading: false
    }
    componentDidMount () {
        this.initData();
    }
    initData = async () => {
        const { recordId, ruleData } = this.props;
        const { pagination, cyctime } = this.state;
        const ruleId = ruleData && ruleData.id;
        if (!recordId || !ruleId) {
            return;
        }
        this.setState({
            loading: true
        })
        try {
            let res = await Api.getFormatTableResult({
                pageSize: pagination.pageSize,
                pageNo: pagination.current,
                cyctime,
                recordId,
                ruleId
            });
            if (res && res.code == 1) {
                this.setState({
                    dataSource: res.data.result,
                    tableName: res.data.table,
                    timeList: res.data.timeList,
                    cyctime: res.data.cyc_time,
                    pagination: {
                        ...pagination,
                        total: res.data.totalCount
                    }
                });
            }
        } finally {
            this.setState({
                loading: false
            })
        }
    }
    initColumns () {
        const { dataSource } = this.state;
        if (!dataSource || !dataSource.length) {
            return [];
        }
        const item = dataSource[0];
        return Object.keys(item).map((key) => {
            return {
                dataIndex: key,
                title: key,
                width: Math.max(key.length * 8, 80)
            }
        });
    }
    onTableChange = (pagination) => {
        this.setState({
            pagination: pagination
        }, this.initData)
    }
    onSelectTime = (value) => {
        this.setState({
            cyctime: value,
            pagination: {
                ...this.state.pagination,
                current: 1,
                total: 0
            }
        }, this.initData);
    }
    render () {
        const { dataSource, cyctime, pagination, tableName, timeList } = this.state;
        const columns = this.initColumns();
        return (
            <div>
                <div style={{ marginBottom: '10px', overflow: 'hidden' }}>
                    表名：{tableName || ''}
                    <span style={{ float: 'right' }}>
                        运行时间：
                        <Select value={cyctime} onChange={this.onSelectTime} style={{ width: '200px' }}>
                            {timeList.map((time) => {
                                return <Option key={time} value={time}>{time}</Option>
                            })}
                        </Select>
                    </span>
                </div>
                <Table
                    className='dt-ant-table--border m-table'
                    dataSource={dataSource}
                    columns={columns}
                    onChange={this.onTableChange}
                    pagination={pagination}
                    scroll={{
                        x: columns.reduce((a, b) => {
                            return a + b.width
                        }, 0),
                        y: 400
                    }}
                />
            </div>
        )
    }
}
export default RuleDetailTableModal;
