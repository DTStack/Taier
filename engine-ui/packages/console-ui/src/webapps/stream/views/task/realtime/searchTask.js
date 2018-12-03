import React, { Component } from 'react'
import { Select } from 'antd'

const Option = Select.Option

class SearchTask extends Component {
    render () {
        return (
            <Select
                showSearch
                style={{ width: 200 }}
                placeholder="按名称搜索"
                optionFilterProp="children"
                filterOption={(input, option) => option.props.value.toLowerCase().indexOf(input.toLowerCase()) >= 0}
            >
                <Option value="jack">Jack</Option>
                <Option value="lucy">Lucy</Option>
                <Option value="tom">Tom</Option>
            </Select>
        )
    }
}
export default SearchTask
