import React from 'react';
import moment from 'moment';

import { Checkbox, DatePicker } from 'antd';
const RangePicker = DatePicker.RangePicker;

export default class CallCountInput extends React.Component {
    state = {
        dateMode: null
    }
    static getDerivedStateFromProps (props, state) {
        if (props.hasOwnProperty('value') && props.value != state.dateMode) {
            return {
                dateMode: props.value
            }
        }
        return null;
    }
    emitChange (dateMode) {
        const { onChange } = this.props;
        if (onChange) {
            onChange(dateMode);
        }
    }
    changeDateMode (e) {
        let dateMode = e.target.checked ? [] : null;
        this.setState({
            dateMode: dateMode
        })
        this.emitChange(dateMode);
    }
    disabledDate = (current) => {
        return current && current.valueOf() < moment().subtract(1, 'days').valueOf();
    }
    render () {
        const { disabled } = this.props;
        const { dateMode } = this.state;
        const unLimited = dateMode && !dateMode.length;
        return (
            <React.Fragment>
                <RangePicker
                    disabledDate={this.disabledDate}
                    disabled={disabled || unLimited}
                    style={{ width: '220px', verticalAlign: 'middle', marginRight: '8px' }}
                    popupStyle={{ fontSize: '14px' }}
                    onChange={(value) => {
                        this.setState({
                            dateMode: value
                        })
                        this.emitChange(value);
                    }}
                    value={unLimited ? null : dateMode}
                />
                <Checkbox disabled={disabled} checked={unLimited} onChange={this.changeDateMode.bind(this)}>不限制调用时间</Checkbox>
            </React.Fragment>
        )
    }
}
