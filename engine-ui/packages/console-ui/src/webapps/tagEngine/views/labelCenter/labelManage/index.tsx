import * as React from 'react';
import { Input, Button, Select } from 'antd';
import TableFilter from './components/tableFilter/index';
import { Conditions } from './components/conditions';
import './style.scss';

const Search = Input.Search;
const Option = Select.Option;
interface IProps {
    router?: any;
}
interface IState {
    searchValue: string;
    tagSelect: any[];
    entityId: number;
    serachParams: {
        searchValue: string;
        tagSelect: any[];
    };
}

export default class LabelManage extends React.PureComponent<IProps, IState> {
    state: IState = {
        searchValue: '',
        tagSelect: [],
        serachParams: {
            searchValue: '',
            tagSelect: []
        },
        entityId: 24
    };
    onHandleClick = () => {
        this.props.router.push('/createLabel')
    };
    handleChange = () => {

    }
    onChangeSearch = (e) => {
        const value = e.target.value;
        this.setState({
            searchValue: value
        })
    }
    onSearch = (value) => {
        const { serachParams } = this.state;
        this.setState({
            searchValue: value,
            serachParams: Object.assign({}, serachParams, { searchValue: value })
        })
    }
    onChangeTagSelect = (value) => {
        const { serachParams } = this.state;
        this.setState({
            tagSelect: value,
            serachParams: Object.assign({}, serachParams, { tagSelect: value })
        })
    }
    render () {
        const { searchValue, tagSelect, serachParams, entityId } = this.state;
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
                            value={searchValue}
                            onChange={this.onChangeSearch}
                            onSearch={this.onSearch}
                        />
                        <Button type="primary" onClick={this.onHandleClick}>新建标签</Button>
                    </div>
                </div>
                <Conditions tagSelect={tagSelect} onChange={this.onChangeTagSelect}/>
                <div className="draggable-wrap-table">
                    <TableFilter entityId={entityId} params={serachParams}/>
                </div>
            </div>
        );
    }
}
