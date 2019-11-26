import * as React from 'react';
import { Input, Button } from 'antd';
import TableFilter from './components/tableFilter/index';
import { Conditions } from './components/conditions';
import SelectEntity from '../../../components/selectEntity';
import './style.scss';

const Search = Input.Search;
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
        entityId: null
    };
    onHandleClick = () => {
        this.props.router.push('/createLabel')
    };
    handleChange = (value) => {
        this.setState({
            entityId: value
        })
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
                        <SelectEntity value={entityId} onChange={this.handleChange}/>
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
                <Conditions key={entityId} entityId={entityId} tagSelect={tagSelect} onChange={this.onChangeTagSelect}/>
                <div className="draggable-wrap-table">
                    <TableFilter key={entityId} entityId={entityId} params={serachParams}/>
                </div>
            </div>
        );
    }
}
