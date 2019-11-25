import * as React from 'react';
import { Input, Button, Select, Table, Modal, message as Message } from 'antd';
import AddDirectory from './components/addDirectory';
import MoveTreeNode from './components/moveTreeNode';
import { API } from '../../../api/labelCenter';
import './style.scss';

const Search = Input.Search;
const Option = Select.Option;
interface IProps {
    history: any;
}
interface IState {
    data: any[];
    type: string;
    visible: boolean;
    moveVisible: boolean;
}

export default class LabelDirectory extends React.PureComponent<IProps, IState> {
    state: IState = {
        visible: false,
        moveVisible: false,
        type: '0',
        data: []
    };
    componentDidMount () {
        this.getTagCate(24)
    }
    getTagCate = (entityId: number) => { // 查询标签层级目录
        API.getTagCate({
            entityId: '24'
        }).then(res => { // 获取主键列表
            const { code, data, message } = res;
            if (code) {
                this.setState({
                    data
                });
            } else {
                Message.error(message)
            }
        })
    }
    onHandleClickOperation = (type: '0'|'1'|'2') => { // 0 新建目录 | 1 新建子目录 | 2 重命名
        this.setState({
            type,
            visible: true
        })
    }
    handleCancel = () => {
        this.setState({
            visible: false
        })
    };
    handleOk = () => {
        this.setState({
            visible: false
        })
    };
    onHandleMove = () => {
        this.setState({
            moveVisible: true
        })
    }
    onHandleCancelMove = (type: 'ok'|'cancel') => {
        this.setState({
            moveVisible: false
        })
    }
    handleChange = () => {}
    onHandleDelete = () => {
        Modal.confirm({
            title: '',
            content: '确定删除此目录？',
            okText: '删除',
            cancelText: '取消'
        });
    }
    render () {
        const { data, visible, type, moveVisible } = this.state;
        const columns = [
            {
                title: '目录结构',
                dataIndex: 'cateName',
                key: 'cateName'
            },
            {
                title: '创建者',
                dataIndex: 'age',
                key: 'age',
                width: 200
            },
            {
                title: '操作',
                dataIndex: 'address',
                key: 'address',
                width: 260,
                render: () => {
                    return (
                        <div>
                            <a onClick={() => this.onHandleClickOperation('1')}>新建子目录</a>
                            <span className="ant-divider" />
                            <a onClick={() => this.onHandleClickOperation('2')}>重命名</a>
                            <span className="ant-divider" />
                            <a onClick={() => this.onHandleMove()}>移动</a>
                            <span className="ant-divider" />
                            <a onClick={() => this.onHandleDelete()}>删除</a>
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
                            <Select defaultValue="用户信息" style={{ width: 120 }} onChange={this.handleChange}>
                                <Option value="jack">用户信息</Option>
                                <Option value="lucy">Lucy</Option>
                                <Option value="disabled" disabled>Disabled</Option>
                                <Option value="Yiminghe">yiminghe</Option>
                            </Select>
                        </div>
                        <Search
                            placeholder="搜索目录名称"
                            className="search"
                            onSearch={value => console.log(value)}
                        />
                    </div>
                    <div className="right_wp">
                        <Button type="primary" onClick={() => this.onHandleClickOperation('0')}>新建目录</Button>
                    </div>
                </div>
                <div className="draggable-wrap-table">
                    <Table indentSize={100} columns={columns} rowKey="tagCateId" className="table_wrap" dataSource={data} pagination={ false }/>
                </div>
                <AddDirectory visible={visible} type={type} handleOk={this.handleOk} handleCancel={this.handleCancel}/>
                <MoveTreeNode visible={moveVisible} handleOk={() => this.onHandleCancelMove('ok')} handleCancel={() => this.onHandleCancelMove('cancel')}/>
            </div>
        );
    }
}
