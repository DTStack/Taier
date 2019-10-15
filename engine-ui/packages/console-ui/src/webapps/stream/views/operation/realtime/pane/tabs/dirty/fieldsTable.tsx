import * as React from 'react';

import { Table, Card, Radio } from 'antd';

const RadioGroup = Radio.Group;
const RadioButton = Radio.Button;

interface FieldsTableProps {
    showType: string;
}
class FieldsTable extends React.PureComponent<any, FieldsTableProps> {
    state: FieldsTableProps = {
        showType: '0'
    }
    switchType (e: React.ChangeEvent<HTMLInputElement>) {
        this.setState({
            showType: e.target.value
        })
    }
    render () {
        const { showType } = this.state;
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
                    columns={[]}
                    dataSource={[]}
                />
            </Card>
        )
    }
}
export default FieldsTable;
