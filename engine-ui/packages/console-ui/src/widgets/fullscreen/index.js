import React, { Component } from 'react'
import { Button, Icon } from 'antd'

import MyIcon from 'rdos/components/icon';

const isFullScreen = function() {
    return document.fullscreenEnabled || 
    document.webkitFullscreenEnabled ||
    document.mozFullscreenEnabled ||
    document.msFullscrrenEnabled;
}

export default class FullScreenButton extends Component {

    state = {
        isFullScreen: false,
    }

    fullScreen = () => {
        const { target } = this.props;

        if (this.state.isFullScreen) {
            if (document.exitFullscreen) {
                document.exitFullscreen();
            } else if (document.mozExitFullscreen) {
                document.mozExitFullscreen();
            } else if (document.webkitExitFullscreen) {
                document.webkitExitFullscreen();
            } else if (document.msExitFullscreen) {
                document.msExitFullscreen();
            }
        } else {
            const domEle = document.getElementById(target) || document.body;
            if (domEle.requestFullscreen) {
                domEle.requestFullscreen();
            } else if (domEle.msRequestFullscreen) { // IE
                domEle.msRequestFUllscreen();
            } else if (domEle.mozRequestFullscreen) { // Firefox (Gecko)
                domEle.mozRequestFullscreen();
            } else if (domEle.webkitRequestFullscreen) { // Webkit
                domEle.webkitRequestFullscreen();
            }
        }
        this.setState({ isFullScreen: !this.state.isFullScreen })
    }

    render() {
        const title = this.state.isFullScreen ? '退出全屏' : '全屏';
        const iconType = this.state.isFullScreen ? "exit-fullscreen" : "fullscreen";

        return (
            <Button {...this.props} onClick={this.fullScreen}>
                <MyIcon className="my-icon" type={iconType} />
                {title}
            </Button>
        )
    }
}