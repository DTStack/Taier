import * as React from 'react';
import { hashHistory } from 'react-router';
import { Card, Table, Input, Button, Popconfirm, message } from 'antd';
import { TableColumnConfig } from 'antd/lib/table/Table';

import { updateComponentState } from 'funcs';

import './style.scss';
import API from '../../../api/relation';
import { IQueryParams } from '../../../model/comm';
import { IRelation } from '../../../model/relation';
import { REQ_STATUS } from '../../../consts';

const Search = Input.Search

interface IState {
    dataSource: IRelation[];
    loading: boolean;
    queryParams: IQueryParams;
}

export default class RelationManage extends React.Component<any, IState> {
    state: IState = {
        dataSource: [],
        loading: false,
        queryParams: {
            total: 0,
            search: '',
            current: 1,
            size: 20,
            orders: [{
                asc: false,
                field: 'updateAt'
            }]
        }
    }

    componentDidMount () {
        this.loadData();
    }

    loadData = async () => {
        const { queryParams } = this.state;
        const res = await API.getRelations(queryParams);
        if (res.code === 1) {
            const data = res.data;
            updateComponentState(this, {
                dataSource: data.contentList || [],
                queryParams: {
                    total: data.total, current: data.current, size: data.size
                }
            });
        }
    }

    handDeleteRelation = async (id: number) => {
        const res = await API.deleteRelation({ relationId: id });
        if (res.code === 1) {
            message.success('删除关系成功！');
            this.loadData();
        } else {
            message.error('删除关系失败！');
        }
    }

    handleSearch = (query: any) => {
        updateComponentState(this, {
            queryParams: {
                current: 1,
                search: query
            }
        }, this.loadData)
    }

    onSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        updateComponentState(this, {
            queryParams: { search: e.target.value }
        })
    }

    handleTableChange = (pagination: any, filters: any, sorter: any) => {
        const params: IQueryParams = {
            current: pagination.current
        };
        if (sorter) {
            params.orders = [{
                asc: sorter.order !== 'descend',
                field: sorter.field
            }]
        }
        updateComponentState(this, { queryParams: params }, this.loadData)
    }

    handleOperateData = (type: string, record: any) => {
        const basePath = '/relationManage';
        switch (type) {
            case 'add': {
                hashHistory.push(`${basePath}/create`)
                break;
            }
            case 'detail': {
                hashHistory.push(`${basePath}/detail/${record.id}`)
                break;
            }
            case 'edit': {
                hashHistory.push({ pathname: `${basePath}/edit/${record.id}`, state: { ...record } })
                break;
            }
            case 'delete': {
                this.handDeleteRelation(record.id);
                break;
            }
            default:;
        }
    }

    initColumns = (): TableColumnConfig<IRelation>[] => {
        const columns: TableColumnConfig<IRelation>[] = [{
            title: '关系名称',
            dataIndex: 'relationName',
            key: 'relationName',
            render: (text: any, record: IRelation) => {
                return <a onClick={this.handleOperateData.bind(this, 'detail', record)}>{text}</a>
            }
        }, {
            title: '关系实体',
            dataIndex: 'entityNames',
            key: 'entityNames'
        }, {
            title: '描述',
            dataIndex: 'relationDesc',
            key: 'relationDesc'
        }, {
            title: '更新时间',
            dataIndex: 'updateAt',
            key: 'updateAt',
            sorter: true
        }, {
            title: '创建者',
            dataIndex: 'createBy',
            key: 'createBy'
        }, {
            title: '被使用情况',
            dataIndex: 'usedCount',
            key: 'usedCount',
            sorter: true
        }, {
            title: '操作',
            dataIndex: 'operation',
            key: 'operation',
            width: '150px',
            render: (text: any, record: IRelation) => {
                return (
                    <span key={record.id}>
                        <a onClick={this.handleOperateData.bind(this, 'edit', record)}>
                            编辑
                        </a>
                        <span className="ant-divider" />
                        <Popconfirm
                            title={<span>删除关系后，关联的标签将失效<br />请谨慎操作！</span>}
                            okText="删除" cancelText="取消"
                            onConfirm={this.handleOperateData.bind(this, 'delete', record)}
                        >
                            <a>删除</a>
                        </Popconfirm>
                    </span>
                )
            }
        }];
        return columns;
    }

    render () {
        const { dataSource, loading, queryParams } = this.state;
        const pagination: any = {
            total: queryParams.total,
            pageSize: queryParams.size,
            current: queryParams.current
        };
        const title = (
            <div>
                <Search
                    value={queryParams.search}
                    onChange={this.onSearchChange}
                    onSearch={this.handleSearch}
                    placeholder="搜索关系、创建者名称"
                    style={{ width: 200, padding: 0 }}
                />&nbsp;&nbsp;
            </div>
        )
        const extra = (
            <Button
                type="primary"
                style={{ marginTop: 10 }}
                className="right"
                onClick={this.handleOperateData.bind(this, 'add', {})}
            >新增关系</Button>
        )
        return (
            <div className="relation-manage">
                <div className="shadow tage-relation-manage">
                    <Card
                        title={title}
                        extra={extra}
                        noHovering
                        bordered={false}
                        className="noBorderBottom"
                    >
                        <Table
                            rowKey="id"
                            className="dt-ant-table dt-ant-table--border full-screen-table-47"
                            pagination={pagination}
                            onChange={this.handleTableChange}
                            loading={loading}
                            columns={this.initColumns()}
                            dataSource={dataSource}
                        />
                    </Card>
                </div>
            </div>
        )
    }
}
