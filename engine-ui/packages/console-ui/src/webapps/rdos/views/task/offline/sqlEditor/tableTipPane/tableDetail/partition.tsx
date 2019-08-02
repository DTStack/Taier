import * as React from 'react';

import TablePartition from '../../../../../dataManage/tablePartition'

class ExtraPaneTableDetailPartition extends React.Component<any, any> {
    state: any = {
        data: []
    }

    initColumns () {
        return [{
            title: '字段',
            dataIndex: 'columnName',
            width: '100px'
        }, {
            title: '类型',
            dataIndex: 'columnType',
            width: '80px'
        }, {
            title: '描述',
            dataIndex: 'comment',
            width: '80px'
        }]
    }

    getTableId () {
        const { columns } = this.props;
        if (columns && columns.length) {
            return columns[0].tableId;
        } else {
            return null;
        }
    }

    render () {
        return (
            <div>
                <TablePartition
                    havaBorder
                    table={{
                        id: this.getTableId()
                    }}
                    pagination={{ simple: true, size: 'small' }}
                />
            </div>
        )
    }
}

export default ExtraPaneTableDetailPartition;
