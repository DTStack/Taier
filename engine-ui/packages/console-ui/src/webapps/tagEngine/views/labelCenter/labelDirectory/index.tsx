import * as React from 'react';
import { Input, Button, Select, Table, Modal } from 'antd';
import AddDirectory from './components/addDirectory';
import MoveTreeNode from './components/moveTreeNode';
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
        data: [
            {
                key: 1,
                name: 'John Brown sr.',
                age: 60,
                address: 'New York No. 1 Lake Park',
                children: [
                    {
                        key: 11,
                        name: 'John Brown',
                        age: 42,
                        address: 'New York No. 2 Lake Park'
                    },
                    {
                        key: 12,
                        name: 'John Brown jr.',
                        age: 30,
                        address: 'New York No. 3 Lake Park',
                        children: [
                            {
                                key: 121,
                                name: 'Jimmy Brown',
                                age: 16,
                                address: 'New York No. 3 Lake Park'
                            }
                        ]
                    },
                    {
                        key: 13,
                        name: 'Jim Green sr.',
                        age: 72,
                        address: 'London No. 1 Lake Park',
                        children: [
                            {
                                key: 131,
                                name: 'Jim Green',
                                age: 42,
                                address: 'London No. 2 Lake Park',
                                children: [
                                    {
                                        key: 1311,
                                        name: 'Jim Green jr.',
                                        age: 25,
                                        address: 'London No. 3 Lake Park'
                                    },
                                    {
                                        key: 1312,
                                        name: 'Jimmy Green sr.',
                                        age: 18,
                                        address: 'London No. 4 Lake Park'
                                    }
                                ]
                            }
                        ]
                    }
                ]
            },
            {
                key: 2,
                name: 'Joe Black',
                age: 32,
                address: 'Sidney No. 1 Lake Park'
            }
        ]
    };
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
                title: '一级',
                dataIndex: 'name',
                key: 'name'
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
                width: 220,
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
            <div className="labelManage">
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
                    <Table indentSize={100} columns={columns} className="table_wrap" dataSource={data} pagination={ false }/>
                </div>
                <AddDirectory visible={visible} type={type} handleOk={this.handleOk} handleCancel={this.handleCancel}/>
                <MoveTreeNode visible={moveVisible} handleOk={() => this.onHandleCancelMove('ok')} handleCancel={() => this.onHandleCancelMove('cancel')}/>
            </div>
        );
    }
}
