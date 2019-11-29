import * as React from 'react';
import { Table, Select, message as Message, Popconfirm } from 'antd';
import { Link } from 'react-router';
import MoveTreeNode from '../moveTreeNode';
import { API } from '../../../../../api/apiMap';

import './style.scss';

interface IProps {
    history?: any;
    params: {
        searchValue: string;
        tagSelect: any[];
    };
    entityId: number;
}
interface IState {
    loading: boolean;
    pageNo: number;
    pageSize: number;
    total: number;
    dataSource: any;
    visible: boolean;
    tagStatus: string;
    tagType: string;
    order: string;
    field: string;
    tagId: number;
}
const Option = Select.Option;
export default class TableFilter extends React.PureComponent<
IProps,
IState
> {
    state: IState = {
        loading: false,
        pageNo: 1,
        pageSize: 10,
        total: 0,
        visible: false,
        tagStatus: '-1',
        tagType: '-1',
        dataSource: [],
        order: '',
        field: '',
        tagId: null
    };
    componentDidMount () {
        const { entityId } = this.props;
        entityId && this.loadMainData();
    }
    componentDidUpdate (preProps) {
        const { params } = this.props;
        if (params != preProps.params) {
            this.setState({
                pageNo: 1,
                pageSize: 10,
                total: 100,
                visible: false,
                tagStatus: '-1',
                tagType: '-1',
                dataSource: []
            })
            this.loadMainData();
        }
    }
    loadMainData () {
        const { pageNo, pageSize, tagType, tagStatus, order, field } = this.state;
        const { params, entityId } = this.props;
        const { searchValue, tagSelect } = params;
        API.getTagList({
            entityId,
            tagStatus: tagStatus,
            tagType: tagType,
            search: searchValue,
            current: pageNo,
            size: pageSize,
            tagCateId: tagSelect.length ? tagSelect[0] : null,
            orders: [{
                field: field,
                asc: order != 'descend'
            }]
        }).then(res => {
            const { data, code } = res;
            if (code === 1) {
                this.setState({
                    dataSource: data.contentList,
                    total: data.total ? parseInt(data.total) : 0,
                    loading: false
                })
            }
        })
    }
    handleChange = (value, type) => {
        let { tagType, tagStatus } = this.state;
        if (type == 'tagType') {
            tagType = value;
        } else {
            tagStatus = value
        }
        this.setState({
            pageNo: 1,
            pageSize: 10,
            loading: true,
            tagType,
            tagStatus
        }, () => {
            this.loadMainData();
        })
    }
    onTableChange = (pagination: any, filters: any, sorter: any) => {
        const { current, pageSize } = pagination;
        const { order = '' } = sorter;
        this.setState(
            {
                pageNo: current,
                pageSize: pageSize,
                loading: true,
                order,
                field: 'tag_data_count'
            },
            () => {
                this.loadMainData();
            }
        );
    };
    deleteTag = (id) => { // 删除标签
        API.deleteTag({
            tagId: id
        }).then(res => {
            const { code } = res;
            if (code === 1) {
                Message.success('删除成功！');
                this.loadMainData();
            }
        })
    }
    onHandleMove = (id) => {
        this.setState({
            visible: true,
            tagId: id
        })
    }
    onHandleCancelMove = (type) => {
        if (type == 'ok') {
            this.loadMainData()
        }
        this.setState({
            visible: false,
            tagId: null
        })
    }
    render () {
        const { pageNo, pageSize, loading, total, dataSource, tagStatus, tagType, visible, tagId } = this.state;
        const { entityId } = this.props;
        const columns = [
            {
                title: '标签名',
                dataIndex: 'tagName',
                key: 'tagName',
                render: (text: any, record) => {
                    const { tagId } = record;
                    const { entityId } = this.props;
                    return <Link to={
                        {
                            pathname: '/labelDetails',
                            state: { tagId, entityId }
                        }
                    }>{text}</Link>
                }
            },
            {
                title: '状态',
                dataIndex: 'tagStatus',
                key: 'tagStatus'
            },
            {
                title: '标签类型',
                dataIndex: 'tagType',
                key: 'tagType'
            },
            {
                title: '样本数量',
                dataIndex: 'sampleNum',
                key: 'sampleNum',
                sorter: true
            },
            {
                title: '操作',
                dataIndex: 'tagId',
                key: 'tagId',
                width: 220,
                render: (id, record) => {
                    const { tagType } = record;
                    return (
                        <div>
                            <Link to={
                                {
                                    pathname: tagType == '原子标签' ? '/editAtomicLabel' : '/createLabel',
                                    query: { tagId: id, entityId }
                                }
                            }>编辑</Link>
                            <span className="ant-divider" />
                            <a onClick={() => this.onHandleMove(id)}>移动</a>
                            <span className="ant-divider" />
                            <Popconfirm placement="top" title="确定删除此标签？" onConfirm={() => this.deleteTag(id)}>
                                <a >删除</a>
                            </Popconfirm>
                        </div>
                    );
                }
            }
        ];
        return (
            <div className="table-filter">
                <div className="title">已选择{`${total}`}个标签</div>
                <div className="table_filter_content">
                    <div className="select_wrap">
                        <div className="select_item">
                            <span className="label">标签状态：</span>
                            <Select value={tagStatus} style={{ width: 120 }} onChange={(value) => this.handleChange(value, 'tagStatus')}>
                                <Option value="-1">全部</Option>
                                <Option value="0">正常</Option>
                                <Option value="1">异常</Option>
                            </Select>
                        </div>
                        <div className="select_item">
                            <span className="label">标签类型：</span>
                            <Select value={tagType} style={{ width: 120 }} onChange={(value) => this.handleChange(value, 'tagType')}>
                                <Option value="-1">全部</Option>
                                <Option value="0">原子标签</Option>
                                <Option value="1">衍生标签</Option>
                            </Select>
                        </div>
                    </div>
                    <Table
                        dataSource={dataSource}
                        columns={columns}
                        loading={loading}
                        rowKey="tagId"
                        onChange={this.onTableChange}
                        pagination={{
                            current: pageNo,
                            pageSize: pageSize,
                            total: total,
                            showSizeChanger: true,
                            showQuickJumper: true,
                            showTotal: () => (
                                <div>
                                    总共 <a>{total}</a> 条数据,每页显示{pageSize}条
                                </div>
                            )
                        }}
                    />
                </div>
                <MoveTreeNode id={tagId} entityId={entityId} visible={visible} handleOk={() => this.onHandleCancelMove('ok')} handleCancel={() => this.onHandleCancelMove('cancel')}/>

            </div>
        );
    }
}
