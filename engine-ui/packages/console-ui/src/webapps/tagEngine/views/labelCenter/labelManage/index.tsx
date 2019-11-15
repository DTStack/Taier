import * as React from 'react';
import { Input, Button, Modal, Select } from 'antd';
import TableFilter from './components/tableFilter/index';
import { Conditions } from './components/conditions';
import './style.scss';

const Search = Input.Search;
const Option = Select.Option;
interface IProps {
    router?: any;
}
interface IState {
    type: string;
    visible: boolean;
    moveVisible: boolean;
}

export default class LabelManage extends React.PureComponent<IProps, IState> {
    state: IState = {
        visible: false,
        moveVisible: false,
        type: '0'
    };
    onHandleClick = () => {
        this.props.router.push('/createLabel')
    };
    componentDidMount () {
        console.log(this.props);
    }
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
    handleChange = () => {

    }
    render () {
        return (
            <div className="labelManage">
                <div className="title_wrap">
                    <div className="left_wp">
                        <span>选择实体：</span>
                        <Select defaultValue="用户信息" style={{ width: 120 }} onChange={this.handleChange}>
                            <Option value="jack">用户信息</Option>
                            <Option value="lucy">Lucy</Option>
                            <Option value="disabled" disabled>Disabled</Option>
                            <Option value="Yiminghe">yiminghe</Option>
                        </Select>
                    </div>
                    <div className="right_wp">
                        <Search
                            placeholder="搜索标签名称"
                            className="search"
                            onSearch={value => console.log(value)}
                        />
                        <Button type="primary" onClick={this.onHandleClick}>新建标签</Button>
                    </div>
                </div>
                <Conditions/>
                <div className="draggable-wrap-table">
                    <TableFilter/>
                </div>
            </div>
        );
    }
}
