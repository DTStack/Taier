import React, { Component } from 'react'
import { Button } from 'antd'
import { hashHistory } from 'react-router'

export default class GoBack extends Component {
    go = () => {
        const url = this.props.url
        if (url) {
            hashHistory.push(url)
        } else {
            hashHistory.go(-1)
        }
    }

    render () {
        const { title } = this.props
        return (
            <Button {...this.props} onClick={this.go}>{title || '返回' }</Button>
        )
    }
}
