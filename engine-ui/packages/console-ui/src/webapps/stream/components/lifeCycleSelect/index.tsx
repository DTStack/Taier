import * as React from 'react';
import { InputNumber, Select } from 'antd';

import pureRender from 'utils/pureRender'

const Option = Select.Option;

const options: any = [{
    name: '3天',
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
}, {
    name: '自定义',
    value: -1
}]

@pureRender
class LifeCycle extends React.Component<any, any> {
    state: any = {
        showCustom: false,
        value: 90
    }

    componentDidMount () {
        const value = this.props.value
        if (value) {
            this.initState(value)
        }
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const value = nextProps.value
        if (value && this.state.value !== value && !this.state.showCustom) {
            this.initState(value)
        }
    }

    initState = (value: any) => {
        const res = options.find((opt: any) => opt.value == value)
        if (!res) { // 自定义
            this.setState({
                value: -1,
                showCustom: true
            })
        } else {
            this.setState({
                value
            })
        }
    }

    onSelect = (value: any) => {
        if (value === '-1') {
            this.setState({
                showCustom: true,
                value: -1
            })
        } else {
            this.setState({
                value: parseInt(value, 10),
                showCustom: false
            })
            this.props.onChange(value)
        }
    }

    customChange = (value: any) => {
        this.props.onChange(value < 0 ? 1 : value)
    }

    renderOptions = () => {
        return options.map((option: any) => <Option
            key={option.value}
            value={`${option.value}`}
        >
            {option.name}
        </Option>)
    }

    render () {
        const { width, value } = this.props
        const display = this.state.showCustom ? 'visible' : 'hidden'
        return (
            <div>
                <Select
                    value={`${this.state.value}`}
                    style={{ width: width || 200 }}
                    placeholder="请选择存储生命周期"
                    onSelect={this.onSelect}
                >
                    {this.renderOptions()}
                </Select>
                &nbsp;
                <span style={{ visibility: display }}>
                    <InputNumber
                        value={value}
                        style={{ width: '45%' }}
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
