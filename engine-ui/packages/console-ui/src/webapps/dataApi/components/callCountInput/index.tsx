import * as React from 'react';

import { InputNumber, Checkbox } from 'antd';

export default class CallCountInput extends React.Component<any, any> {
    state: any = {
        callCount: null
    }
    static getDerivedStateFromProps (props: any, state: any) {
        if (props.hasOwnProperty('value') && props.value != state.callCount) {
            return {
                callCount: props.value
            }
        }
        return null;
    }
    emitChange (callCount: any) {
        const { onChange } = this.props;
        if (onChange) {
            onChange(callCount);
        }
    }
    changeCountMode (e: any) {
        let callCount = e.target.checked ? -1 : null;
        this.setState({
            callCount: callCount
        })
        this.emitChange(callCount);
    }
    render () {
        const { disabled } = this.props;
        const { callCount } = this.state;
        const unLimited = callCount == -1;
        return (
            <React.Fragment>
                <InputNumber onChange={(value: any) => {
                    this.setState({
                        callCount: value
                    })
                    this.emitChange(value);
                }} value={unLimited ? null : callCount} min={1} disabled={disabled || unLimited} {...{ type: 'number' }} />
                <Checkbox disabled={disabled} checked={unLimited} onChange={this.changeCountMode.bind(this)}>不限制调用次数</Checkbox>
            </React.Fragment>
        )
    }
}
