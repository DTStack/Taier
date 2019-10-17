import * as React from 'react';

import { get } from 'lodash';

import { Table, Card, Radio } from 'antd';
import { TableInfo } from './index';

const RadioGroup = Radio.Group;
const RadioButton = Radio.Button;

interface FieldsTableState {
    showType: string;
}
class FieldsTable extends React.PureComponent<{ tableInfo: TableInfo }, FieldsTableState> {
    state: FieldsTableState = {
        showType: '0'
    }
    switchType (e: React.ChangeEvent<HTMLInputElement>) {
        this.setState({
            showType: e.target.value
        })
    }
    getColumn (isPartition: boolean) {
        return [
            {
                title: '序号',
                key: 'columnIndex'
            },
            {
                title: '字段名称',
                key: 'columnName'
            },
            {
                title: '类型',
                key: 'columnType'
            },
            {
                title: '注释',
                key: 'comment'
            }
        ]
    }
    render () {
        const { showType } = this.state;
        const { tableInfo } = this.props;
        const isPartition = showType == '1';
        const dataSource = isPartition ? get(tableInfo, 'partition', []) : get(tableInfo, 'column', [])
        return (
            <Card
                bordered={false}
                noHovering
                title={
                    <RadioGroup
                        value={showType}
                        onChange={this.switchType.bind(this)}
                    >
                        <RadioButton value={'0'}>
                            非分区字段
                        </RadioButton>
                        <RadioButton value={'1'}>
                            分区字段
                        </RadioButton>
                    </RadioGroup>
                }
            >
                <Table
                    rowKey="index"
                    className="dt-ant-table dt-ant-table--border dt-ant-table--padding"
                    columns={this.getColumn(isPartition)}
                    dataSource={dataSource}
                />
            </Card>
        )
    }
}
export default FieldsTable;
