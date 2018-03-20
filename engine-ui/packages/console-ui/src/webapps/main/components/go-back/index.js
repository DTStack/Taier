import React, { Component } from 'react'
import { Icon } from 'antd'
import { browserHistory, hashHistory } from 'react-router'

export default class GoBack extends Component {

    go = () => {
        const url = this.props.url
        if (url) {
            hashHistory.push(url)
        } else {
            browserHistory.go(-1)
        }
    }

    render() {
        const { type, style } = this.props
        let mStyle = {
            cursor: 'pointer'
        }
        if (style) {
            mStyle = Object.assign(mStyle, style)
        }
        return (
            <Icon 
                style={mStyle} 
                type={ type || "left-circle" }
                onClick={ this.go }
            />
        )
    }
}