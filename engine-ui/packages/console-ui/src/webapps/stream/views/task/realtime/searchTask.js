import React, { Component } from 'react'
import { Select } from 'antd'

import utils from 'utils'
const Option = Select.Option

class SearchTask extends Component {
    render () {
        const {
            title, data,
            handCancle, visible
        } = this.props

        return (
            <Select
                showSearch
                style={{ width: 200 }}
                placeholder="按名称搜索"
                optionFilterProp="children"
                onChange={handleChange}
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
