import React, { Component } from 'react'

import { Icon } from 'antd'

export default class Index extends Component {
    render () {
        return (
            <div className="txt-center" style={{ lineHeight: '200px' }}>
                <h1><Icon type="frown-o" /> 亲，是不是走错地方了？</h1>
            </div>
        )
    }
}
