import * as React from 'react';

import { Table } from 'antd';

interface DirtyTableProps {
    columns: any[];
    data: any[];
}
class DirtyTable extends React.PureComponent<any, DirtyTableProps> {
    state: DirtyTableProps = {
        columns: [],
        data: []
    }
    initColumn (columns: any[]): any[] {
        return [];
    }
    render () {
        const { columns, data } = this.state;
        return (
            <Table
                className='dt-ant-table'
                columns={this.initColumn(columns)}
                dataSource={data}
            />
        )
    }
}
export default DirtyTable;
