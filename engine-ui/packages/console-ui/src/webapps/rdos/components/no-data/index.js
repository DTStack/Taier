'use strict'
import React, { Component } from 'react';

export default class NoData extends Component {
    render () {
        return (
            <p className="txt-center" style={{ lineHeight: '35px', margin: 0 }} {...this.props}>
               暂无数据 ☹️
            </p>
        )
    }
}
