import React, { Component } from 'react'
import { Icon } from 'antd'
import { browserHistory } from 'react-router'

export default class GoBack extends Component {

    go = () => {
        const url = this.props.url
        if (url) {
            browserHistory.push(url)
        } else {
            browserHistory.go(-1)
        }
    }

    render(){
        return (
            <Icon type="left-circle" {...this.props} onClick={this.go} />
        )
    }
}