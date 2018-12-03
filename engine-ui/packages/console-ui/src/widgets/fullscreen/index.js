import React, { Component } from 'react'
import { Button } from 'antd'

import MyIcon from 'rdos/components/icon';
import KeyCombiner from 'widgets/keyCombiner';

export default class FullScreenButton extends Component {
    state = {
        isFullScreen: false
    }
    /**
     * 在一定情况下chrome不会触发resize事件，所以手动触发一下resize。
     */
    dispatchResizeEvent () {
        const event = new Event('resize');
        window.dispatchEvent(event);
    }
    componentDidMount () {
        const { target } = this.props;
        const propsDom = document.getElementById(target)
        const domEle = propsDom || document.body;
        let callBack = (event) => {
            let node;
            if (domEle.requestFullscreen) {
                node = document.fullscreenElement;
            } else if (domEle.msRequestFullscreen) { // IE
                node = document.msFullscreenElement;
            } else if (domEle.mozRequestFullscreen) { // Firefox (Gecko)
                node = document.mozFullScreenElement;
            } else if (domEle.webkitRequestFullscreen) { // Webkit
                node = document.webkitFullscreenElement;
            }
            this.setState({
                isFullScreen: !!node
            }, this.dispatchResizeEvent)
        }
        if (domEle.requestFullscreen) {
            domEle.onfullscreenchange = callBack;
        } else if (domEle.msRequestFullscreen) { // IE
            domEle.onmsfullscreenchange = callBack;
        } else if (domEle.mozRequestFullscreen) { // Firefox (Gecko)
            domEle.onmozfullscreenchange = callBack;
        } else if (domEle.webkitRequestFullscreen) { // Webkit
            domEle.onwebkitfullscreenchange = callBack;
        }
    }
    componentWillUnmount () {
        const { target } = this.props;
        const propsDom = document.getElementById(target)
        const domEle = propsDom || document.body;
        if (domEle.requestFullscreen) {
            document.onfullscreenchange = null;
        } else if (domEle.msRequestFullscreen) { // IE
            document.onmsfullscreenchange = null;
        } else if (domEle.mozRequestFullscreen) { // Firefox (Gecko)
            document.onmozfullscreenchange = null;
        } else if (domEle.webkitRequestFullscreen) { // Webkit
            document.onwebkitfullscreenchange = null;
        }
    }
    keyPressFullScreen = (evt) => {
        evt.preventDefault();
        this.fullScreen();
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
            const propsDom = document.getElementById(target)
            const domEle = propsDom || document.body;
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
        // this.setState({ isFullScreen: !this.state.isFullScreen });
    }

    render () {
        const { themeDark, fullIcon, exitFullIcon, ...other } = this.props;
        const title = this.state.isFullScreen ? '退出全屏' : '全屏';
        const iconType = this.state.isFullScreen ? 'exit-fullscreen' : 'fullscreen';
        const customIcon = this.state.isFullScreen ? exitFullIcon : fullIcon;
        return (
            <KeyCombiner onTrigger={this.keyPressFullScreen} keyMap={{
                70: true,
                91: true,
                16: true
            }}>
                {customIcon ? <span onClick={this.fullScreen}>{customIcon}</span>
                    : <Button {...other} onClick={this.fullScreen}>
                        <MyIcon
                            className="my-icon"
                            type={iconType}
                            themeDark={themeDark}
                        />
                        {title}
                    </Button>}
            </KeyCombiner>
        )
    }
}
