import * as React from 'react';
import { Input, Button, Table, Popconfirm, message as Message } from 'antd';
import AddDirectory from './components/addDirectory';
import MoveTreeNode from './components/moveTreeNode';
import { API } from '../../../api/apiMap';
import SelectEntity from '../../../components/selectEntity';
import './style.scss';

const Search = Input.Search;
interface IProps {
    history: any;
}
interface IState {
    data: any[];
    type: string;
    visible: boolean;
    moveVisible: boolean;
    expandedKeys: any[];
    searchValue: string;
    dataList: any[];
    entityId: string;
    currentData: any;
}

export default class LabelDirectory extends React.PureComponent<IProps, IState> {
    state: IState = {
        visible: false,
        moveVisible: false,
        expandedKeys: [],
        dataList: [],
        searchValue: '',
        type: '0',
        data: [],
        entityId: '',
        currentData: {}
    };
    handleChange = (value) => { // 改变实体
        this.setState({
            entityId: value,
            visible: false,
            moveVisible: false,
            expandedKeys: [],
            dataList: [],
            searchValue: '',
            type: '0',
            data: [],
            currentData: {}
        }, () => {
            this.getTagCate()
        })
    }
    getTagCate = () => { // 查询标签层级目录
        const { entityId } = this.state;
        API.getTagCate({
            entityId
        }).then(res => { // 获取主键列表
            const { code, data } = res;
            if (code) {
                let dataList = [];
                const generateList = data => {
                    for (let i = 0; i < data.length; i++) {
                        const node = data[i];
                        const { tagCateId, cateName } = node;
                        dataList.push({ tagCateId, cateName });
                        if (node.children) {
                            generateList(node.children);
                        }
                    }
                };
                generateList(data);
                this.setState({
                    data,
                    dataList
                });
            }
        })
    }
    getParentKey = (key, tree) => {
        let parentKey;
        for (let i = 0; i < tree.length; i++) {
            const node = tree[i];
            if (node.children) {
                if (node.children.some(item => item.tagCateId === key)) {
                    parentKey = node.tagCateId;
                } else if (this.getParentKey(key, node.children)) {
                    parentKey = this.getParentKey(key, node.children);
                }
            }
        }
        return parentKey;
    };
    onExpand = (expandedKeys: any) => {
        this.setState({
            expandedKeys
        });
    };
    onChangeSearch = (e: any) => {
        const { value } = e.target;
        const { dataList, data } = this.state;
        const expandedKeys = dataList
            .map(item => {
                if (item.cateName.indexOf(value) > -1) {
                    return this.getParentKey(item.tagCateId, data);
                }
                return null;
            })
            .filter((item, i, self) => item && self.indexOf(item) === i);
        this.setState({
            expandedKeys,
            searchValue: value

        });
    };
    onHandleClickOperation = (type: '0'|'1'|'2', record) => { // 0 新建目录 | 1 新建子目录 | 2 重命名
        this.setState({
            type,
            visible: true,
            currentData: record
        })
    }
    handleCancel = () => {
        this.setState({
            visible: false,
            currentData: {}
        })
    };
    handleOk = () => {
        this.setState({
            visible: false,
            currentData: {}
        });
        this.getTagCate();
    };
    onHandleMove = (record) => {
        this.setState({
            moveVisible: true,
            currentData: record
        });
        this.getTagCate();
    }
    onHandleCancelMove = (type: 'ok'|'cancel') => {
        if (type == 'ok') {
            this.getTagCate();
        }
        this.setState({
            moveVisible: false,
            currentData: {}
        })
    }
    deleteTagCate = (id) => {
        const { entityId } = this.state;
        API.deleteTagCate({
            entityId,
            tagCateId: id
        }).then(res => { // 获取主键列表
            const { code } = res;
            if (code === 1) {
                Message.success('删除成功！');
                this.getTagCate();
            }
        })
    }
    render () {
        const { data, visible, type, moveVisible, expandedKeys, searchValue, entityId, currentData } = this.state;
        const columns = [
            {
                title: '目录结构',
                dataIndex: 'cateName',
                key: 'cateName',
                render: (cateName, record) => {
                    const index = cateName.indexOf(searchValue);
                    const beforeStr = cateName.substr(0, index);
                    const afterStr = cateName.substr(index + searchValue.length);
                    const title =
                        index > -1 ? (
                            <span>
                                {beforeStr}
                                <span style={{ color: '#f50' }}>{searchValue}</span>
                                {afterStr}
                                {` (${record.tagNum})`}
                            </span>
                        ) : (
                            <span>{`${cateName} (${record.tagNum})`}</span>
                        );
                    return title
                }
            },
            {
                title: '创建者',
                dataIndex: 'createBy',
                key: 'createBy',
                width: 200
            },
            {
                title: '操作',
                dataIndex: 'tagCateId',
                key: 'tagCateId',
                width: 260,
                render: (id, record) => {
                    return record.cateName == '默认分组' ? null : (
                        <div>
                            <a onClick={() => this.onHandleClickOperation('1', record)}>新建子目录</a>
                            <span className="ant-divider" />
                            <a onClick={() => this.onHandleClickOperation('2', record)}>重命名</a>
                            <span className="ant-divider" />
                            <a onClick={() => this.onHandleMove(record)}>移动</a>
                            <span className="ant-divider" />
                            <Popconfirm placement="top" title="确定删除此目录？" onConfirm={() => this.deleteTagCate(id)}>
                                <a >删除</a>
                            </Popconfirm>

                        </div>
                    );
                }
            }
        ];

        return (
            <div className="labelDirectory">
                <div className="title_wrap">
                    <div className="left_wp">
                        <div>
                            <span>选择实体：</span>
                            <SelectEntity value={entityId} onChange={this.handleChange}/>
                        </div>
                        <Search value={searchValue} className="search" placeholder="搜索目录名称" onChange={this.onChangeSearch} />
                    </div>
                    <div className="right_wp">
                        <Button type="primary" disabled={!entityId} onClick={() => this.onHandleClickOperation('0', {})}>新建目录</Button>
                    </div>
                </div>
                <div className="draggable-wrap-table">
                    <Table indentSize={100} expandedRowKeys={expandedKeys } onExpandedRowsChange={this.onExpand} columns={columns} rowKey="tagCateId" className="table_wrap" dataSource={data} pagination={ false }/>
                </div>
                <AddDirectory entityId={entityId} data={currentData} visible={visible} type={type} handleOk={this.handleOk} handleCancel={this.handleCancel}/>
                <MoveTreeNode data={data} entityId={entityId} id={currentData.tagCateId} visible={moveVisible} handleOk={() => this.onHandleCancelMove('ok')} handleCancel={() => this.onHandleCancelMove('cancel')}/>
            </div>
        );
    }
}
