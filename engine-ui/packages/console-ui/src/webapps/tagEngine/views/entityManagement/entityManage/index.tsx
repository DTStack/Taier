import * as React from 'react';
import { Link, hashHistory } from 'react-router';
import { Card, Table, Input, Button, message as Message } from 'antd';
import EmptyComp from './emptyComp';
import DeleteModal from '../../../components/deleteModal';
import './style.scss';
import { API } from '../../../api/apiMap'
import { get } from 'lodash';

const Search = Input.Search

interface IState {
    pageNo: number;
    pageSize: number;
    total: number;
    dataSource: any[];
    searchVal: string;
    loading: boolean;
    desc: boolean;
    sorterField: string;
    deleteVisible: boolean;
    deleteItem: any;
    isSearch: boolean;
}

let timer: any = null;
export default class EntityList extends React.Component<any, IState> {
    state: IState = {
        pageNo: 1,
        pageSize: 20,
        total: 0,
        dataSource: [],
        searchVal: undefined,
        loading: false,
        desc: true,
        sorterField: '',
        deleteVisible: false,
        deleteItem: {},
        isSearch: false
    }

    componentDidMount () {
        this.getEntities();
    }

    getEntities = () => {
        const { pageSize, pageNo, desc, sorterField, searchVal } = this.state;
        let params: any = {
            size: pageSize,
            current: pageNo,
            search: searchVal
        }
        if (sorterField != '') {
            params.orders = [{
                field: sorterField,
                asc: !desc
            }]
        }
        API.getEntities(params).then((res: any) => {
            const { data = [], code } = res;
            if (code === 1) {
                this.setState({
                    dataSource: data.contentList || [],
                    total: data.total || 0,
                    loading: false
                })
            }
        })
    }

    handleSearch = (query: any) => {
        let isSearch = false;
        if (query) {
            isSearch = true;
        }
        this.setState({
            searchVal: query,
            pageNo: 1,
            loading: true,
            isSearch
        }, this.getEntities)
    }

    handleSearchChange = (e) => {
        this.setState({
            searchVal: e.target.value == '' ? undefined : e.target.value
        })
    }

    handleTableChange = (pagination: any, filters: any, sorter: any) => {
        this.setState({
            pageNo: pagination.current,
            sorterField: sorter.field || '',
            desc: sorter.order == 'descend' || false,
            loading: true
        }, this.getEntities);
    }

    handleOperateData = (type: string, record: any) => {
        switch (type) {
            case 'add': {
                hashHistory.push('/entityManage/edit')
                break;
            }
            case 'edit': {
                hashHistory.push({ pathname: '/entityManage/edit', state: { ...record } })
                break;
            }
            case 'delete': {
                API.checkEntityUserd({
                    entityId: record.id
                }).then((res: any) => {
                    const { data, code } = res;
                    if (code === 1) {
                        this.setState({
                            deleteVisible: true,
                            deleteItem: {
                                ...record,
                                hasRefered: data
                            }
                        })
                    }
                })
                break;
            }
            default: ;
        }
    }

    handleDeleteModel = (type: string) => {
        if (type == 'ok') {
            API.deleteEntity({
                entityId: this.state.deleteItem.id
            }).then((res: any) => {
                const { code } = res;
                if (code === 1) {
                    Message.success('删除成功！');
                    this.setState({
                        pageNo: 1,
                        loading: true,
                        deleteVisible: false,
                        deleteItem: {}
                    }, () => {
                        this.getEntities();
                    })
                }
            })
        } else {
            this.setState({
                deleteVisible: false
            })
            timer = setTimeout(() => {
                this.setState({
                    deleteItem: {}
                })
                clearTimeout(timer);
                timer = null;
            }, 100);
        }
    }

    initColumns = () => {
        return [{
            title: '实体名称',
            dataIndex: 'entityName',
            key: 'entityName',
            render: (text: any, record: any) => {
                return <Link to={{ pathname: '/entityManage/detail', state: record }}>{text}</Link>
            }
        }, {
            title: '实体主键',
            dataIndex: 'entityPrimaryKey',
            entityPrimaryKey: 'key',
            render: (text: any, record: any) => {
                return (<span>
                    {text}
                    {record.entityPrimaryKeyCn && <span style={{ color: '#bfbfbf' }}>({record.entityPrimaryKeyCn})</span>}
                </span>)
            }
        }, {
            title: '描述',
            dataIndex: 'entityDesc',
            key: 'entityDesc'
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
            title: '数据量',
            dataIndex: 'dataCount',
            key: 'dataCount',
            sorter: true
        }, {
            title: '关联标签数量',
            dataIndex: 'tagCount',
            key: 'tagCount',
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
                        <a onClick={this.handleOperateData.bind(this, 'delete', record)}>
                            删除
                        </a>
                    </span>
                )
            }
        }]
    }

    render () {
        const { isSearch, total, pageSize, pageNo, dataSource, loading, searchVal, deleteVisible, deleteItem } = this.state;
        const pagination: any = {
            total: +total || 0,
            pageSize: pageSize,
            current: pageNo,
            showTotal: () => (
                <div>
                    总共 <a>{total}</a> 条数据,每页显示{pageSize}条
                </div>
            )
        };
        const title = (
            <div>
                <Search
                    value={searchVal}
                    placeholder="搜索实体、创建者名称"
                    style={{ width: 200, padding: 0 }}
                    onSearch={this.handleSearch}
                    onChange={this.handleSearchChange}
                />
                &nbsp;&nbsp;
            </div>
        )
        const extra = (
            <Button
                type="primary"
                style={{ marginTop: 10 }}
                className="right"
                onClick={this.handleOperateData.bind(this, 'add', {})}
            >新增实体</Button>
        )
        return (
            <div className="entity-list">
                <div className="shadow tage-entity-manage">
                    <Card
                        title={title}
                        extra={extra}
                        noHovering
                        bordered={false}
                        className="noBorderBottom"
                    >
                        <Table
                            rowKey="id"
                            className={!dataSource.length && !isSearch ? ['dt-ant-table--border', 'self-define-empty'].join(' ') : ['dt-ant-table--border'].join('')}
                            pagination={pagination}
                            onChange={this.handleTableChange}
                            loading={loading}
                            columns={this.initColumns()}
                            dataSource={dataSource}
                        />
                        {!dataSource.length && !isSearch ? <EmptyComp /> : null}
                    </Card>
                </div>
                <DeleteModal
                    title={'删除实体'}
                    content={get(deleteItem, 'hasRefered') ? '解除实体下标签的引用关系后，可删除' : '实体下标签及目录将同步删除，无法恢复，请谨慎操作'}
                    visible={deleteVisible}
                    onCancel={this.handleDeleteModel.bind(this, 'cancel')}
                    onOk={this.handleDeleteModel.bind(this, 'ok')}
                    footer={
                        get(deleteItem, 'hasRefered') ? <Button type="primary" onClick={this.handleDeleteModel.bind(this, 'cancel')}>我知道了</Button> : undefined
                    }
                />
            </div>
        )
    }
}
