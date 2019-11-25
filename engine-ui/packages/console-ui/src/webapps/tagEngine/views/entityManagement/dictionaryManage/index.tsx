import * as React from 'react';
import { Link, hashHistory } from 'react-router';
import { Card, Table, Input, Button, message as Message } from 'antd';
import DeleteModal from '../../../components/deleteModal';
import './style.scss';
import { get } from 'lodash';
import API from '../../../api/entity';

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

let timer: any = null;
export default class DictionaryManage extends React.PureComponent<any, IState> {
    constructor (props: any) {
        super(props);
    }

    state: IState = {
        pageNo: 1,
        pageSize: 20,
        total: 2,
        dataSource: [],
        searchVal: undefined,
        loading: false,
        desc: true,
        sorterField: '',
        deleteVisible: false,
        deleteItem: {}
    }
    componentDidMount () {
        this.getDictList();
    }

    componentWillUnmount () {
        if (timer) {
            clearTimeout(timer);
            timer = null;
        }
    }

    getDictList = () => {
        const { pageSize, pageNo, desc, sorterField, searchVal } = this.state;
        let params: any = {
            size: pageSize,
            current: pageNo,
            search: searchVal
        }
        if (sorterField != '') {
            params.orders = [{
                filed: sorterField,
                asc: !desc
            }]
        }
        API.getDictList(params).then((res: any) => {
            const { data = {}, code } = res;
            if (code === 1) {
                this.setState({
                    dataSource: data.contentList || [],
                    total: +data.total || 0,
                    loading: false
                })
            }
        })
    }

    handleSearch = (query: any) => {
        this.setState({
            searchVal: query,
            pageNo: 1,
            loading: true
        }, this.getDictList)
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
        }, this.getDictList);
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
                API.dictCanDelete({
                    dictId: record.id
                }).then((res: any) => {
                    const { data, code } = res;
                    if (code === 1) {
                        this.setState({
                            deleteVisible: true,
                            deleteItem: {
                                ...record,
                                hasRefered: !data
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
            API.deleteDict({
                dictId: this.state.deleteItem.id
            }).then((res: any) => {
                const { code } = res;
                if (code === 1) {
                    Message.success('删除成功！')
                    this.setState({
                        pageNo: 1,
                        loading: true,
                        deleteVisible: false,
                        deleteItem: {}
                    }, () => {
                        this.getDictList();
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
            dataIndex: 'createAt',
            key: 'createAt',
            sorter: true
        }, {
            title: '创建者',
            dataIndex: 'createBy',
            key: 'createBy'
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
        const { total, pageSize, pageNo, dataSource, loading, searchVal, deleteVisible, deleteItem } = this.state;
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
                <Search
                    value={searchVal}
                    placeholder="搜索字典名称"
                    style={{ width: 200, padding: 0 }}
                    onSearch={this.handleSearch}
                    onChange={this.handleSearchChange}
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
                            className="dt-ant-table--border"
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
                    content={get(deleteItem, 'hasRefered') ? '解除字典的引用关系后，可删除' : '删除字典后无法恢复，请谨慎操作！'}
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
