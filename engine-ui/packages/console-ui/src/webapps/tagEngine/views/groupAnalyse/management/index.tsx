import * as React from 'react';
import { Link, hashHistory } from 'react-router';
import { Card, Table, Input, Button, Popconfirm } from 'antd';

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

const basePath = '/groupAnalyse';

export default class GroupManage extends React.Component<any, IState> {
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
        switch (type) {
            case 'add': {
                hashHistory.push(`${basePath}/upload`)
                break;
            }
            case 'detail': {
                hashHistory.push(`${basePath}/detail`)
                break;
            }
            case 'edit': {
                hashHistory.push({ pathname: `${basePath}/detail`, state: { ...record } })
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
            title: '群组名称',
            dataIndex: 'name',
            key: 'name',
            render: (text: any, record: any) => {
                return <Link to={{ pathname: `${basePath}/detail`, state: record }}>{text}</Link>
            }
        }, {
            title: '群组数据量',
            dataIndex: 'entiry',
            key: 'entiry'
        }, {
            title: '群组类型',
            dataIndex: 'desc',
            key: 'desc'
        }, {
            title: '创建者',
            dataIndex: 'creator',
            key: 'creator'
        }, {
            title: '创建时间',
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
                    placeholder="搜索群组名称"
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
            >新增群组</Button>
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
