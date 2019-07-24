import PropTypes from 'prop-types'
import _ from 'lodash'
import { Input } from 'antd'
import React, { Component } from 'react'

const searchTypeList = [
    {
        key: 'caseSensitive',
        svg: 'jqpp'
    },
    {
        key: 'precise',
        svg: 'jq'
    },
    {
        key: 'front',
        svg: 'kt'
    },
    {
        key: 'tail',
        svg: 'jw'
    }
]

const propType = {
    placeholder: PropTypes.string,
    style: PropTypes.object,
    value: PropTypes.any, // input框的值
    onChange: PropTypes.func,
    onSearch: PropTypes.func,
    onTypeChange: PropTypes.func,
    searchType: PropTypes.string, // input框中选中的筛选方式
    filterOptions: PropTypes.array // 数组中一共最多四个字符串caseSensitive表示有区分大小写功能，precise表示有精确功能，front表示有匹配头部功能，tail表示有匹配尾部功能
}

class MultiSearchInput extends Component {
    constructor (props) {
        super(props);
        this.state = {
            placeholder: this.props.placeholder || '',
            style: this.props.style || {},
            value: this.props.value || '',
            onChange: this.props.onChange || (() => {}),
            onSearch: this.props.onSearch || (() => {}),
            onTypeChange: this.props.onTypeChange || (() => {}),
            searchType: this.props.searchType || 'fuzzy',
            filterOptions: this.props.filterOptions || ['precise', 'front', 'tail']
        }
    }

    render () {
        const {
            placeholder,
            style,
            value,
            onChange,
            onSearch,
            onTypeChange,
            searchType,
            filterOptions
        } = this.state;
        const propsValue = this.props.value;
        // const propsSearchType = this.props.searchType;
        const filterList = _.filter(searchTypeList, (item) => {
            return _.includes(filterOptions, item.key)
        });
        return (
            <div
                style={{
                    position: 'relative'
                }}
            >
                <Input
                    value={propsValue != null ? propsValue : value}
                    placeholder={placeholder}
                    style={{
                        ...style,
                        paddingRight: `${filterOptions.length * 32}px`
                    }}
                    onChange={(e) => {
                        console.log(e.target.value);
                        this.setState({ value: e.target.value });
                        onChange(e.target.value);
                    }}
                    onPressEnter={(e) => {
                        console.log(e.target, e.target.value);
                        onSearch(e.target);
                    }}
                />
                <div
                    style={{
                        position: 'absolute',
                        height: '100%',
                        top: '0px',
                        right: '0px',
                        width: `${filterOptions.length * 32}px`,
                        display: 'flex',
                        justifyContent: 'space-around',
                        alignItems: 'center'
                    }}
                >
                    {
                        _.map(filterList, (item) => {
                            return (
                                <div
                                    style={{
                                        cursor: 'pointer',
                                        display: 'block',
                                        height: '22px',
                                        width: '26px',
                                        border: searchType === item.key ? '1px solid #2491F7' : 'none',
                                        overflow: 'hidden'
                                    }}
                                    onClick={() => {
                                        const newSearchType = searchType === item.key ? 'fuzzy' : item.key
                                        this.setState({
                                            searchType: newSearchType
                                        });
                                        onTypeChange(newSearchType)
                                    }}
                                >
                                    <img
                                        src={`/public/widgets/img/${item.svg}_icon.svg`}
                                        style={{
                                            marginTop: '-6px',
                                            marginLeft: '-3px'
                                        }}
                                    />
                                </div>
                            );
                        })
                    }
                </div>
            </div>
        )
    }
}

MultiSearchInput.propTypes = propType

export default MultiSearchInput
