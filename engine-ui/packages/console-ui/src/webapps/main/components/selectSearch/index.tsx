import * as React from 'react';
import { Select } from 'antd';

const Option = Select.Option;

export default class SelectSearch extends React.Component<any, any> {
    state = {
        searchValue: ''
    }

    wrapOption () {
        const searchValue = this.state.searchValue;
        const { children } = this.props;
        const reactNodeArr: any = children;

        if (reactNodeArr && reactNodeArr.length > 0) {
            for (let i = 0; i < reactNodeArr.length; i++) {
                if (reactNodeArr[i].props.title ? reactNodeArr[i].props.title == searchValue : reactNodeArr[i].props.value == searchValue) {
                    return null;
                }
            }
        }

        return searchValue ? (
            <Option
                key={searchValue}
                value={searchValue}
                title={searchValue}>
                {searchValue}
            </Option>
        ) : null
    }

    onSearch (value: any) {
        this.setState({
            searchValue: value
        })
    }

    render () {
        const { children, ...otherProps } = this.props;
        const wrapOption = this.wrapOption();

        return (
            <Select
                allowClear
                showSearch
                onSearch={this.onSearch.bind(this)}
                {...otherProps}
            >
                {wrapOption}
                {children}
            </Select>
        )
    }
}
