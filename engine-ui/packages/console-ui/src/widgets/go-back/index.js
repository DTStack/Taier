import React, { Component } from 'react'
import { Button } from 'antd'
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

    render () {
        const { title } = this.props
        return (
            <Button {...this.props} onClick={this.go}>{title || '返回' }</Button>
        )
    }
}
