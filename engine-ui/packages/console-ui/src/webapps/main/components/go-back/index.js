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
        const { type, style } = this.props;
        let mStyle = {
            cursor: 'pointer'
        }
        if (style) {
            mStyle = Object.assign(mStyle, style)
        }
        switch (type) {
            case "textButton":
                return (
                    <Button style={{marginRight:"5px"}} onClick={this.go} size="small">
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