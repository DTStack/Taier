import * as React from 'react';
import { isEmpty } from 'lodash';
import { Card, Icon, Table } from 'antd';
import { PaginationProps } from 'antd/lib/pagination';

import { Pagination } from 'typing';

import TQApi, { InvalidData } from '../../api/taskQuery';

export interface InvalidDataProps {
    record: any;
    rule: any;
}

export interface InvalidDataState {
    data: InvalidData;
    pagination: Pagination;
}

export default class InvalidDataTable extends React.Component<InvalidDataProps, InvalidDataState> {
    constructor (props: any) {
        super(props);

        this.state = {
            data: {
                table: '',
                result: [],
                lifeCycle: ''
            },
            pagination: {
                current: 1,
                total: null,
                pageSize: 10
            }
        };
    }

    componentDidMount () {
        this.fetchData();
    }

    fetchData = async () => {
        const { record, rule } = this.props;
        const { pagination } = this.state;
        const res = await TQApi.getInvalidData({
            recordId: record.id,
            ruleId: rule.id,
            current: pagination.current,
            pageSize: pagination.pageSize
        });
        if (res.code === 1) {
            const data = res.data;
            this.setState({
                data: {
                    table: data.table,
                    lifeCycle: data.lifeCycle,
                    result: data.result || []
                },
                pagination: {
                    current: data.current,
                    total: data.total
                }
            })
        }
    }

    onChange = (pagination: PaginationProps) => {
        this.setState({ pagination: {
            current: pagination.current
        } }, this.fetchData)
    }

    initInvalidDataTableColumns = (fields: {}) => {
        const columns: any = [];
        if (fields) {
            const keys = Object.keys(fields);
            for (let i = 0; i < keys.length; i++) {
                const key = keys[i];
                columns.push({
                    width: 200,
                    title: key,
                    dataIndex: key,
                    key: key
                })
            }
        }
        return columns;
    }

    render () {
        const { data, pagination } = this.state;
        const { record, rule } = this.props;

        let invalidDataTitle = (
            !isEmpty(record) ? `不规范数据（${record.columnName} -- ${record.functionName}）` : ''
        )
        const downloadUrl = TQApi.getDownInvalidDataURL(record.id, rule.id);
        const columns = this.initInvalidDataTableColumns(data.result[0]);
        const scroll: any = { x: columns.length < 6 ? true : 2000, y: 250 };

        return (
            <Card
                noHovering
                bordered={false}
                loading={false}
                className="shadow"
                style={{ marginTop: '10px' }}
                title={<span>{invalidDataTitle} <small>数据表名：{data.table}，生命周期：{data.lifeCycle}天</small></span>}
                extra={
                    <a href={downloadUrl} download><Icon
                        type="download"
                        style={{ fontSize: 16, cursor: 'pointer' }}
                    /></a>
                }
            >
                <Table
                    rowKey={(record: any, index: any) => {
                        const rowKey = `${record[index]}-${index}`
                        return rowKey;
                    }}
                    className="m-table txt-center-table"
                    columns={columns}
                    pagination={{ total: 0, ...pagination }}
                    dataSource={data.result}
                    onChange={this.onChange}
                    scroll={scroll}
                />
            </Card>
        );
    }
}
