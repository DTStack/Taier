import React, { Component } from 'react'
import { Button, Icon } from 'antd'

export default class FullScreenButton extends Component {

    state = {
        isFullScreen: false,
    }

    fullScreen = () => {
        const { target } = this.props;

        var status = document.fullscreenEnabled || 
        document.webkitFullscreenEnabled ||
        document.mozFullscreenEnabled ||
        document.msFullscrrenEnabled;

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
        this.setState({
            isFullScreen: !this.state.isFullScreen
        })
    }

    render(){
        const { title } = this.props;
        const iconType = this.state.isFullScreen ? "shrink" : "arrows-alt";
        return (
            <Button {...this.props} icon={iconType} onClick={this.fullScreen}>
                {title || '全屏' }
            </Button>
        )
    }
}