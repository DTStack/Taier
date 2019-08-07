import * as React from 'react';
import { Table } from 'antd';
import { get } from 'lodash';
import { PaginationProps } from 'antd/lib/pagination';
import { TableProps } from 'antd/lib/table/Table';

import { Pagination } from 'typing';
import Http from 'utils/http';

export interface InvalidDataProps<T> extends TableProps<T> {
    url: string;
    method?: 'GET' | 'POST';
    params?: any;
    dataSourceIndex: string | number | Function;
}

export interface InvalidDataState {
    dataSource: [];
    pagination: Pagination;
}

/**
 * FetchTable
 * 直接把 URL 和 fetch 逻辑封装到了组件
 */
export default class FetchTable<T> extends React.Component<InvalidDataProps<T>, InvalidDataState> {
    constructor (props: any) {
        super(props);
        const { pagination } = props;
        this.state = {
            dataSource: [],
            pagination: {
                current: get(pagination, 'current', 1),
                total: get(pagination, 'total', 0),
                pageSize: get(pagination, 'pageSize', 10)
            }
        };
    }

    componentDidMount () {
        this.fetchData();
    }

    fetchData = async () => {
        let res: any = null;
        const { pagination } = this.state;
        const { method, url, params, dataSourceIndex } = this.props;
        if (method && method === 'GET') {
            res = await Http.get(url, params);
        } else {
            res = await Http.post(url, { ...params, ...pagination });
        }
        let sourceData = null;
        if (typeof dataSourceIndex === 'function') {
            sourceData = dataSourceIndex(res);
        } else {
            sourceData = res[dataSourceIndex];
        }

        if (sourceData === 1) {
            this.setState({
                dataSource: sourceData
            })
        }
    }

    onChange = (pagination: PaginationProps) => {
        this.setState({ pagination: {
            current: pagination.current
        } }, this.fetchData)
    }

    render () {
        const { dataSource } = this.state;
        return (
            <Table
                { ...this.props }
                dataSource={dataSource}
                onChange={this.onChange}
            />
        );
    }
}
