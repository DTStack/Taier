import React from 'react';
import { Input, Select } from 'antd';

import pureRender from 'utils/pureRender'

const Option = Select.Option;

const options = [{
    name:'3天',
    value: 3
}, {
    name: '7天',
    value: 7
}, {
    name: '30天',
    value: 30
}, {
    name: '90天',
    value: 90
}, {
    name: '365天',
    value: 365
},{
    name: '自定义',
    value: -1,
}]

@pureRender
class LifeCycle extends React.Component {

    state = {
        showCustom: false,
        value: 90,
    }
    
    componentDidMount() {
        const value = this.props.value
        if (value) {
            this.initState(value)
        }
    }

    initState = (value) => {
        const nextValue = parseInt(value, 10)
        const res = options.find(opt => opt.value === nextValue)
        if (res) {
            this.setState({
                value: nextValue,
                showCustom: false,
            })
        } else { // 自定义
            this.setState({
                value: -1,
                showCustom: true,
            })
        }
    }

    componentWillReceiveProps(nextProps) {
        const value = nextProps.value
        if (value && this.state.value !== value) {
            this.initState(value)
        }
    }

    onSelect = (value) => {
        if (value === -1) {
            this.setState({
                showCustom: true,
                value,
            })
        } else {
            this.setState({
                value,
                showCustom: false,
            })
            this.props.onChange(value)
        }
    }

    customChange = (e) => {
        const value = e.target.value
        if (this.state.showCustom) {
            this.props.onChange(value)
        }
    }

    renderOptions = () => {
        return options.map(option => <Option key={option.value} value={option.value}>{option.name}</Option>)
    }

    render() {
        const { width, value } = this.props
        const display = this.state.showCustom ? 'visible' : 'hidden'
        return (
            <div>
                <Select
                    value={this.state.value}
                    style={{ width: width || 200 }}
                    placeholder="请选择存储生命周期"
                    onSelect={this.onSelect}
                >
                    {this.renderOptions()}
                </Select>
                &nbsp;
                <span style={{visibility: display}}>
                    <Input 
                        value={value}
                        style={{ width: '45%', height: '32px' }}
                        type="number"
                        min={0}
                        onChange={this.customChange}
                    />
                    &nbsp;天
                </span>
            </div>
        )
    }

}

export default LifeCycle