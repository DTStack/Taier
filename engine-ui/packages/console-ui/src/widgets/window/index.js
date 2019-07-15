
import React, { Component } from 'react';

/**
 * 窗口切换事件监听，
 * 用法：
 * <SwitchWindow onSwitch={}></SwitchWindow>
 */
class Window extends Component {
    componentDidMount () {
        this.initEvent();
    }

    listener = (e) => {
        const { onSwitch } = this.props;
        console.log('switch window is focusing!', window.location)
        if (onSwitch) onSwitch(e);
    }

    componentWillUnmount () {
        window.removeEventListener('focus', this.listener);
    }

    initEvent = () => {
        window.addEventListener('focus', this.listener);
    }

    render () {
        return (
            <React.Fragment>
                {
                    this.props.children
                }
            </React.Fragment>
        )
    }
}

export default Window
