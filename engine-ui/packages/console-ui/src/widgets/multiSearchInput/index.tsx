import _ from 'lodash'
import { Input, Tooltip } from 'antd'
import * as React from 'react'

const searchTypeList: any = [
    {
        key: 'caseSensitive',
        svg: 'jqpp',
        tip: '区分大小写匹配'
    },
    {
        key: 'precise',
        svg: 'jq',
        tip: '精确匹配'
    },
    {
        key: 'front',
        svg: 'kt',
        tip: '头部匹配'
    },
    {
        key: 'tail',
        svg: 'jw',
        tip: '尾部匹配'
    }
]

export interface MultiSearchInputProps {
    placeholder: string;
    style: object;
    value: any; // input框的值
    onChange: any;
    onSearch: any;
    onTypeChange: any;
    searchType: string; // input框中选中的筛选方式
    filterOptions?: any[]; // 数组
}

class MultiSearchInput extends React.Component<MultiSearchInputProps, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            placeholder: this.props.placeholder || '',
            style: this.props.style || {},
            value: this.props.value || '',
            onChange: this.props.onChange || ((value: any) => { console.log(value) }),
            onSearch: this.props.onSearch || ((value: any, searchType: any) => { console.log(value, searchType) }),
            onTypeChange: this.props.onTypeChange || ((searchType: any) => { console.log(searchType) }),
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
            filterOptions
        } = this.state;
        let searchType = this.state.searchType;
        const propsValue = this.props.value;
        searchType = this.props.searchType != null ? this.props.searchType : searchType;
        const filterList = _.filter(searchTypeList, (item: any) => {
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
                        paddingRight: `${filterOptions.length * 26 + 5}px`
                    }}
                    onChange={(e: any) => {
                        this.setState({ value: e.target.value });
                        onChange(e.target.value);
                    }}
                    onPressEnter={(e: any) => {
                        onSearch(e.target.value, searchType);
                    }}
                />
                <div
                    style={{
                        position: 'absolute',
                        height: '100%',
                        top: '0px',
                        right: '0px',
                        width: `${filterOptions.length * 26 + 5}px`,
                        display: 'flex',
                        justifyContent: 'space-around',
                        alignItems: 'center',
                        paddingRight: '5px'
                    }}
                >
                    {
                        _.map(filterList, (item: any) => {
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
                                    <Tooltip
                                        title={item.tip}
                                        mouseEnterDelay={0.5}
                                    >
                                        <img
                                            src={`/public/widgets/img/${item.svg}_icon.svg`}
                                            // title={item.tip}
                                            style={{
                                                marginTop: '-6px',
                                                marginLeft: '-3px'
                                            }}
                                        />
                                    </Tooltip>
                                </div>
                            );
                        })
                    }
                </div>
            </div>
        )
    }
}

export default MultiSearchInput
