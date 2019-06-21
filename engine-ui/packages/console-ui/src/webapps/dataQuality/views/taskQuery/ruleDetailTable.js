import React from 'react';

import { Modal, Table, Select } from 'antd';

class RuleDetailTableModal extends React.Component {
    state = {
        dataSource: []
    }
    _key = null;
    onCancel = () => {
        this._key = Math.random();
        this.setState({
            dataSource: []
        })
        this.props.onCancel();
    }
    initColumns () {
        return [
            {
                dataIndex: 'database',
                title: 'database'
            },
            {
                dataIndex: 'tableName',
                title: 'tableName'
            },
            {
                dataIndex: 'isTemporary',
                title: 'isTemporary'
            }
        ]
    }
    render () {
        const { visible, tableName } = this.props;
        const { dataSource } = this.state;
        return (
            <Modal
                visible={visible}
                key={this._key}
                footer={null}
                onCancel={this.onCancel}
                width='800'
                title='查看明细'
            >
                <div style={{ marginBottom: '10px', overflow: 'hidden' }}>
                    表名：{tableName || ''}
                    <span style={{ float: 'right' }}>
                        运行时间：
                        <Select style={{ width: '200px' }}>

                        </Select>
                    </span>
                </div>
                <Table
                    className='dt-ant-table--border m-table'
                    dataSource={dataSource}
                    columns={this.initColumns()}
                />
            </Modal>
        )
    }
}
export default RuleDetailTableModal;
