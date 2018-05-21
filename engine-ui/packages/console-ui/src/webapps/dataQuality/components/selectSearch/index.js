import React, { Component } from "react";
import { Tooltip, message, Select } from 'antd';

const Option = Select.Option;
export default class SelectSearch extends Component {
    state = {
        searchValue: ""
    }

    wrapOption() {
        const searchValue = this.state.searchValue;
        const { children } = this.props;

        if (children && children.length > 0) {
            for (let i in children) {
                if (children[i].props.value == searchValue) {
                    return null;
                }
            }
        }

        return searchValue ? (
            <Option
                key={searchValue}
                value={searchValue}>
                {searchValue}
            </Option>
        ) : null
    }

    onSearch(value) {
        this.setState({
            searchValue: value
        })
    }

    render() {
        const { children, ...otherProps } = this.props;

        return (
            <Select
                allowClear
                showSearch
                onSearch={this.onSearch.bind(this)}
                {...otherProps}
            >
                {this.wrapOption()}
                {children}
            </Select>
        )
    }
}