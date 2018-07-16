import React, { Component } from 'react'
import { Icon, Button } from 'antd'
import { browserHistory, hashHistory } from 'react-router'

export default class GoBack extends Component {

    go = () => {
        const { url, history } = this.props
        if (url) {
            if (history)
                browserHistory.push(url)
            else
                hashHistory.push(url)
        } else {
            browserHistory.go(-1)
        }
    }

    getButtonView() {
        const { type, style, size } = this.props;
        let mStyle = {
            cursor: 'pointer'
        }
        if (style) {
            mStyle = Object.assign(mStyle, style)
        }
        switch (type) {
            case "textButton":
                mStyle.marginRight = '5px';
                return (
                    <Button style={mStyle} onClick={this.go} size={size||"small"}>
                        <Icon type="left" />返回
                    </Button>
                );
            default:
                return (
                    <Icon
                        style={mStyle}
                        type={type || "left-circle"}
                        onClick={this.go}
                    />
                )
        }

    }

    render() {
        return this.getButtonView();
    }
}