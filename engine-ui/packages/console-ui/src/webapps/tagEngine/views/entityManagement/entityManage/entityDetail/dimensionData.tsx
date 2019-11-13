import * as React from 'react';
import EditCell from '../../../../components/editCell';
import { Card, Table } from 'antd';
import './style.scss';

interface IProps {
    dataSource: any;
}

export default class DimensionData extends React.Component<IProps, any> {
    state: any = {

    }

    componentDidMount () {

    }

    handleCNChange = () => {

    }

    initColumns = () => {
        return [{
            title: '维度名称',
            dataIndex: 'name',
            key: 'name',
            width: 200,
            render: (text: any, record: any) => {
                return (<span>
                    {text}
                    {record.isKey ? '(主键)' : ''}
                </span>)
            }
        }, {
            title: '中文名',
            dataIndex: 'chName',
            key: 'chName',
            width: 250,
            render: (text: any, record: any) => {
                return <EditCell
                    keyField="chName"
                    isView={false}
                    onHandleEdit={this.handleCNChange}
                    value={text || ''}
                />
            }
        }, {
            title: '数据类型',
            dataIndex: 'type',
            key: 'type',
            width: 200
        }, {
            title: '属性值数量',
            dataIndex: 'propertyNum',
            key: 'propertyNum',
            width: 150
        }, {
            title: '多值列',
            dataIndex: 'isMultiply',
            key: 'isMultiply',
            width: 150
        }, {
            title: '关联原子标签',
            dataIndex: 'isRelateLabel',
            key: 'isRelateLabel',
            filters: [
                { text: '否', value: '否' },
                { text: '是', value: '是' }
            ],
            filterMultiple: false,
            onFilter: (value: string, record: any) => {
                return record.isRelateLabel == value;
            }
        }];
    }

    render () {
        const { dataSource } = this.props;

        return (
            <div className="ed-dimension-data shadow">
                <Card
                    noHovering
                    bordered={false}
                    className="noBorderBottom"
                >
                    <Table
                        rowKey="id"
                        className="dt-ant-table dt-ant-table--border"
                        pagination={false}
                        scroll={{ y: 400 }}
                        columns={this.initColumns()}
                        dataSource={dataSource}
                    />
                </Card>
            </div>
        )
    }
}
