import * as React from 'react';
import { Link, hashHistory } from 'react-router';
import { Card, Table, Input, Button } from 'antd';
import EmptyComp from './emptyComp';
import DeleteModal from '../../../components/deleteModal';
import './style.scss';

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
}

export default class EntityList extends React.Component<any, IState> {
    state: IState = {
        pageNo: 1,
        pageSize: 20,
        total: 2,
        dataSource: [
            {
                id: 1, name: '实体名称1', desc: '描述', key: '主键', keyName: '主键名', updateTime: '2019-12-10 12:33', creator: '创建者一号', dataCount: '200', relateCount: '4000'
            }, {
                id: 2, name: '实体名称1', desc: '描述', key: '主键', keyName: '主键名', updateTime: '2019-12-10 12:33', creator: '创建者一号', dataCount: '200', relateCount: '4000'
            }
        ],
        searchVal: undefined,
        loading: false,
        desc: true,
        sorterField: '',
        deleteVisible: false,
        deleteItem: {}
    }

    componentDidMount () {

    }

    loadData = () => {

    }

    handleSearch = (query: any) => {
        this.setState({
            searchVal: query,
            pageNo: 1
        }, this.loadData)
    }

    handleTableChange = (pagination: any, filters: any, sorter: any) => {
        this.setState({
            pageNo: pagination.current,
            sorterField: sorter.field || '',
            desc: sorter.order == 'descend' || false
        }, this.loadData);
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
                this.setState({
                    deleteVisible: true,
                    deleteItem: record
                })
                break;
            }
            default: ;
        }
    }

    handleDeleteModel = (type: string) => {
        if (type == 'ok') {
            // TODO 请求处理删除
        }
        this.setState({
            deleteVisible: false,
            deleteItem: {}
        })
    }

    initColumns = () => {
        return [{
            title: '实体名称',
            dataIndex: 'name',
            key: 'name',
            render: (text: any, record: any) => {
                return <Link to={{ pathname: '/entityManage/detail', state: record }}>{text}</Link>
            }
        }, {
            title: '实体主键',
            dataIndex: 'key',
            key: 'key',
            render: (text: any, record: any) => {
                return (<span>
                    {text}
                    <span style={{ color: '#bfbfbf' }}>({record.keyName})</span>
                </span>)
            }
        }, {
            title: '描述',
            dataIndex: 'desc',
            key: 'desc'
        }, {
            title: '更新时间',
            dataIndex: 'updateTime',
            key: 'updateTime',
            sorter: true
        }, {
            title: '创建者',
            dataIndex: 'creator',
            key: 'creator'
        }, {
            title: '数据量',
            dataIndex: 'dataCount',
            key: 'dataCount',
            sorter: true
        }, {
            title: '关联标签数量',
            dataIndex: 'relateCount',
            key: 'relateCount',
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
        const { total, pageSize, pageNo, dataSource, loading, searchVal, deleteVisible } = this.state;
        const pagination: any = {
            total: total,
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
                {dataSource.length ? <Search
                    value={searchVal}
                    placeholder="搜索实体、创建者名称"
                    style={{ width: 200, padding: 0 }}
                    onSearch={this.handleSearch}
                /> : null}
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
                            className="dt-ant-table--border self-define-empty"
                            pagination={pagination}
                            onChange={this.handleTableChange}
                            loading={loading}
                            columns={this.initColumns()}
                            dataSource={dataSource}
                        />
                        {!dataSource.length ? <EmptyComp /> : null}
                    </Card>
                </div>
                <DeleteModal
                    title={'删除实体'}
                    content={'实体下标签及目录将同步删除，无法恢复，请谨慎操作！'}
                    visible={deleteVisible}
                    onCancel={this.handleDeleteModel.bind(this, 'cancel')}
                    onOk={this.handleDeleteModel.bind(this, 'ok')}
                />
            </div>
        )
    }
}
