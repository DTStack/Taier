import React from 'react';
import { Input, Select } from 'antd';

import pureRender from 'utils/pureRender'

const Option = Select.Option;

const options = [{
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
class LifeCycleSelect extends React.Component {
    constructor (props) {
        super(props);
        this.state = {
            value: props.value,
            selectValue: -1
        }
    }

    static getDerivedStateFromProps (nextProps, prevState) {
        if (typeof nextProps.value != 'undefined' && nextProps.value != prevState.value) {
            return {
                value: nextProps.value
            }
        }
        return null;
    }
    showCustom = () => {
        const { selectValue } = this.state;
        return selectValue == -1;
    }

    onSelect = (value) => {
        let nextValue = null;
        if (value != '-1') {
            nextValue = parseInt(value, 10)
        } else {
            nextValue = null;
        }
        this.setState({
            value: nextValue,
            selectValue: value
        }, () => {
            this.props.onChange(nextValue)
        })
    }

    customChange = (e) => {
        const value = e.target.value
        this.setState({
            value: value <= 0 ? 1 : value
        })
        this.props.onChange(value)
    }

    renderOptions = () => {
        return options.map(option => <Option
            key={option.value}
            value={`${option.value}`}
        >
            {option.name}
        </Option>)
    }
    render () {
        const { width } = this.props;
        const { selectValue, value } = this.state;
        return (
            <div>
                <Select
                    value={`${selectValue}`}
                    style={{ width: width || 200 }}
                    size="large"
                    placeholder="请选择存储生命周期"
                    onSelect={this.onSelect}
                >
                    {this.renderOptions()}
                </Select>
                {this.showCustom() ? (
                    <Input
                        size="large"
                        value={value}
                        style={{ width: 220, marginLeft: '5px' }}
                        type="number"
                        min={0}
                        addonAfter={'天'}
                        placeholder="请输入生命周期"
                        onChange={this.customChange}
                    />
                ) : null}
            </div>
        )
    }
}

export default LifeCycleSelect
