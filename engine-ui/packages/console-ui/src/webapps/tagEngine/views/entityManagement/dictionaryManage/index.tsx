import * as React from 'react';
import { Link, hashHistory } from 'react-router';
import { Card, Table, Input, Button } from 'antd';
import DeleteModal from '../../../components/deleteModal';
import './style.scss';

const Search = Input.Search;

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

export default class DictionaryManage extends React.PureComponent<any, IState> {
    constructor (props: any) {
        super(props);
    }

    state: IState={
        pageNo: 1,
        pageSize: 20,
        total: 2,
        dataSource: [{
            id: 1, name: '字典名称1', desc: '描述', type: '标签字典', createTime: '2019-12-10 12:33', creator: '创建者一号'
        }, {
            id: 2, name: '字典名称1', desc: '描述', type: '标签字典', createTime: '2019-12-10 12:33', creator: '创建者一号'
        }],
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
                hashHistory.push('/dictionaryManage/edit')
                break;
            }
            case 'edit': {
                hashHistory.push({ pathname: '/dictionaryManage/edit', state: { ...record } })
                break;
            }
            case 'delete': {
                this.setState({
                    deleteVisible: true,
                    deleteItem: record
                })
                break;
            }
            default:;
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
            title: '字典名称',
            dataIndex: 'name',
            key: 'name',
            render: (text: any, record: any) => {
                return <Link to={{ pathname: '/dictionaryManage/detail', state: record }}>{text}</Link>
            }
        }, {
            title: '类型',
            dataIndex: 'type',
            key: 'type'
        }, {
            title: '描述',
            dataIndex: 'desc',
            key: 'desc'
        }, {
            title: '创建时间',
            dataIndex: 'createTime',
            key: 'createTime',
            sorter: true
        }, {
            title: '创建者',
            dataIndex: 'creator',
            key: 'creator'
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
            current: pageNo
        };
        const title = (
            <div>
                <Search
                    value={searchVal}
                    placeholder="搜索字典名称"
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
            >新增字典</Button>
        )
        return (
            <div className="dictionary-manage">
                <div className="shadow tage-dictionary-manage">
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
                <DeleteModal
                    title={'删除字典'}
                    content={'删除字典后无法恢复，请谨慎操作！'}
                    visible={deleteVisible}
                    onCancel={this.handleDeleteModel.bind(this, 'cancel')}
                    onOk={this.handleDeleteModel.bind(this, 'ok')}
                />
            </div>
        )
    }
}
