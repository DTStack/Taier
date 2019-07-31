'use strict'
import * as React from 'react';

export default class NoData extends React.Component<any, any> {
    render () {
        return (
            <p className="txt-center" style={{ lineHeight: '35px', margin: 0 }} {...this.props}>
               暂无数据 ☹️
            </p>
        )
    }
}
