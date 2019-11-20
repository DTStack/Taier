import * as React from 'react';
import { Link, hashHistory } from 'react-router';
import { Card, Table, Input, Button, Popconfirm } from 'antd';
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
}

export default class RelationManage extends React.Component<any, IState> {
    state: IState = {
        pageNo: 1,
        pageSize: 20,
        total: 2,
        dataSource: [{
            id: 1, name: '关系名称1', desc: '描述', entiry: '关联实体', updateTime: '2019-12-10 12:33', creator: '创建者一号', useNum: '4000'
        }, {
            id: 2, name: '关系名称2', desc: '描述', entiry: '关联实体', updateTime: '2019-12-10 12:33', creator: '创建者一号', useNum: '4000'
        }],
        searchVal: undefined,
        loading: false,
        desc: true,
        sorterField: ''
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

    handDeleteRelation = () => {
        // TODO delete a relation entity.
    }

    handleOperateData = (type: string, record: any) => {
        const basePath = '/relationManage';
        switch (type) {
            case 'add': {
                hashHistory.push(`${basePath}/create`)
                break;
            }
            case 'detail': {
                hashHistory.push(`${basePath}/detail`)
                break;
            }
            case 'edit': {
                hashHistory.push({ pathname: `${basePath}/edit/${record.id}`, state: { ...record } })
                break;
            }
            case 'delete': {
                this.handDeleteRelation();
                // 请求删除
                break;
            }
            default:;
        }
    }

    initColumns = () => {
        return [{
            title: '关系名称',
            dataIndex: 'name',
            key: 'name',
            render: (text: any, record: any) => {
                return <Link to={{ pathname: '/', state: record }}>{text}</Link>
            }
        }, {
            title: '关系实体',
            dataIndex: 'entiry',
            key: 'entiry'
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
            title: '被使用情况',
            dataIndex: 'useNum',
            key: 'useNum',
            sorter: true
        }, {
            title: '操作',
            dataIndex: 'operation',
            key: 'operation',
            width: '150px',
            render: (text: any, record: any) => {
                return (
                    <span key={record.id}>
                        <a onClick={this.handleOperateData.bind(this, 'detail', record)}>
                            查看
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
        }]
    }

    render () {
        const { total, pageSize, pageNo, dataSource, loading, searchVal } = this.state;
        const pagination: any = {
            total: total,
            pageSize: pageSize,
            current: pageNo
        };
        const title = (
            <div>
                <Search
                    value={searchVal}
                    placeholder="搜索关系、创建者名称"
                    style={{ width: 200, padding: 0 }}
                    onSearch={this.handleSearch}
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
