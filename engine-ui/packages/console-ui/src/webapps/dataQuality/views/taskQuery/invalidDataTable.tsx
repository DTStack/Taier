import * as React from 'react';
import { isEmpty } from 'lodash';
import { Card, Icon, Table } from 'antd';
import { PaginationProps } from 'antd/lib/pagination';
import { updateComponentState } from 'funcs';

import { Pagination } from 'typing';

import TQApi, { InvalidData } from '../../api/taskQuery';

export interface InvalidDataProps {
    record: any;
    rule: any;
}

export interface InvalidDataState {
    data: InvalidData;
    pagination: Pagination;
    showDirtyData: boolean;
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
                total: 0,
                pageSize: 10
            },
            showDirtyData: false
        };
    }

    componentDidMount () {
        this.fetchData();
    }

    fetchData = async () => {
        const { record, rule } = this.props;
        const { pagination } = this.state;
        if (!record || !rule) return;
        const res = await TQApi.getInvalidData({
            recordId: record.id,
            ruleId: rule.id,
            current: pagination.current,
            pageSize: pagination.pageSize
        });
        if (res.code === 1) {
            const data = res.data;
            updateComponentState(this, {
                data: {
                    table: data.table,
                    lifeCycle: data.lifeCycle,
                    result: data.result || []
                },
                pagination: {
                    total: data.total
                },
                showDirtyData: data && data.showDirtyData
            })
        }
    }

    onChange = (pagination: PaginationProps) => {
        this.setState({ pagination }, this.fetchData)
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
        const { data, pagination, showDirtyData } = this.state;
        const { record, rule } = this.props;

        let invalidDataTitle = (
            !isEmpty(rule) ? `不规范数据（${rule.columnName} -- ${rule.functionName}）` : ''
        )
        const downloadUrl = TQApi.getDownInvalidDataURL(record.id, rule.id);
        const columns = this.initInvalidDataTableColumns(data.result[0]);
        const scroll: any = { x: columns.length < 6 ? true : 2000, y: 250 };
        return (
            showDirtyData && <Card
                noHovering
                bordered={false}
                loading={false}
                className="shadow"
                style={{ marginTop: '10px' }}
                title={<span>{invalidDataTitle} <small>数据表名：{data.table || '无'}，生命周期：{data.lifeCycle || 0}天</small></span>}
                extra={
                    data.result && data.result.length > 0
                        ? <a href={downloadUrl} download><Icon
                            type="download"
                            style={{ fontSize: 16, cursor: 'pointer' }}
                        /></a> : null
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
