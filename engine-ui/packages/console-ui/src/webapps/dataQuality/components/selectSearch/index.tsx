import * as React from 'react';
import { Tooltip, message, Select } from 'antd';

const Option = Select.Option;
export default class SelectSearch extends React.Component<any, any> {
    state: any = {
        searchValue: ''
    }

    wrapOption () {
        const searchValue = this.state.searchValue;
        const { children } = this.props;

        if (children && children.length > 0) {
            for (let i in children) {
                if (children[i].props.title == searchValue) {
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
