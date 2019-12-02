import * as React from 'react';
import { hashHistory } from 'react-router';
import { Card, Table, Input, Button, Popconfirm, message } from 'antd';

import { updateComponentState } from 'funcs';

import './index.scss';
import GroupAPI from '../../../api/group';
import { IGroup } from '../../../model/group';
import { IQueryParams } from '../../../model/comm';
import { displayGroupType } from '../../../components/display';
import SelectEntity from '../../../components/selectEntity';

const Search = Input.Search

interface IState {
    dataSource: IGroup[];
    loading: boolean;
    queryParams: { entityId: string } & IQueryParams ;
}

const basePath = '/groupAnalyse';

export default class GroupManage extends React.Component<any, IState> {
    state: IState = {
        dataSource: [],
        loading: false,
        queryParams: {
            entityId: null,
            total: 0,
            search: null,
            current: 1,
            size: 20,
            orders: [{
                asc: false,
                field: 'updateAt'
            }]
        }
    }

    componentDidMount () {
        // TODO
    }

    loadData = async () => {
        const ctx = this;
        ctx.setState({
            loading: true
        })
        const { queryParams } = this.state;
        const res = await GroupAPI.getGroups(queryParams);
        if (res.code === 1) {
            const data = res.data;
            updateComponentState(ctx, {
                dataSource: data.contentList || [],
                queryParams: {
                    total: Number(data.total),
                    current: Number(data.current),
                    size: Number(data.size)
                }
            });
        }
        ctx.setState({
            loading: false
        })
    }

    delete = async (id: number) => {
        const res = await GroupAPI.deleteGroup({ groupId: id });
        if (res.code === 1) {
            message.success('删除群组成功！');
            this.loadData();
        } else {
            message.error('删除群组失败！');
        }
    }

    handleSearch = (query: string) => {
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
        if (sorter.order) {
            params.orders = [{
                asc: sorter.order !== 'descend',
                field: sorter.field
            }]
        }
        updateComponentState(this, { queryParams: params }, this.loadData)
    }

    handleEntityChange = (value) => {
        if (value) {
            updateComponentState(this, {
                queryParams: {
                    entityId: value,
                    current: 1,
                    search: ''
                }
            }, this.loadData)
        }
    }

    handleOperateData = (type: string, record: IGroup) => {
        const { queryParams } = this.state;
        const query = {
            entityId: queryParams.entityId,
            groupId: record.groupId
        }
        switch (type) {
            case 'add': {
                hashHistory.push({
                    pathname: `${basePath}/upload`,
                    query: query
                })
                break;
            }
            case 'detail': {
                hashHistory.push({
                    pathname: `${basePath}/detail`,
                    query: query
                })
                break;
            }
            case 'edit': {
                hashHistory.push({
                    pathname: `${basePath}/upload/edit`,
                    query: query
                })
                break;
            }
            case 'delete': {
                // 请求删除
                this.delete(record.groupId);
                break;
            }
            default: ;
        }
    }

    initColumns = () => {
        return [{
            title: '群组名称',
            dataIndex: 'groupName',
            key: 'groupName',
            render: (text: any, record: any) => {
                return <a onClick={this.handleOperateData.bind(this, 'detail', record)}>
                    {text}
                </a>
            }
        }, {
            title: '群组数据量',
            dataIndex: 'groupDataCount',
            key: 'groupDataCount',
            sorter: true
        }, {
            title: '群组类型',
            dataIndex: 'groupType',
            key: 'groupType',
            render: (groupType: number, record: any) => {
                return displayGroupType(groupType)
            }
        }, {
            title: '创建者',
            dataIndex: 'createBy',
            key: 'createBy'
        }, {
            title: '更新时间',
            dataIndex: 'updateAt',
            key: 'updateAt',
            sorter: true
        }, {
            title: '操作',
            dataIndex: 'operation',
            key: 'operation',
            width: '150px',
            render: (text: any, record: any) => {
                return (
                    <span key={record.id}>
                        <a onClick={this.handleOperateData.bind(this, 'edit', record)}>
                            编辑
                        </a>
                        <span className="ant-divider" />
                        <Popconfirm
                            title={<span>群组删除后无法恢复<br />请谨慎操作！</span>}
                            okText="删除" cancelText="取消"
                            onConfirm={this.handleOperateData.bind(this, 'delete', record)}
                        >
                            <a>删除</a>
                        </Popconfirm>
                    </span>
                )
            }
        }]
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
                <div className="left_wp">
                    <span>选择实体：</span>
                    <SelectEntity value={queryParams.entityId} onChange={this.handleEntityChange}/>
                </div>
            </div>
        )
        const extra = (<div>
            <Search
                value={queryParams.search}
                placeholder="搜索群组名称"
                style={{ width: 200, padding: 0 }}
                onChange={this.onSearchChange}
                onSearch={this.handleSearch}
            />&nbsp;&nbsp;
            <Button
                type="primary"
                style={{ marginTop: 10 }}
                className="right"
                onClick={this.handleOperateData.bind(this, 'add', {})}
            >新增群组</Button>
        </div>
        )
        return (
            <div className="group-manage">
                <div className="shadow m-card">
                    <Card
                        title={title}
                        extra={extra}
                        noHovering
                        bordered={false}
                        className="noBorderBottom"
                    >
                        <Table
                            locale={{
                                emptyText: '暂无数据, 请确认是否已勾选实体！'
                            }}
                            rowKey="groupId"
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
