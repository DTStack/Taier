import * as React from 'react'

import { Icon } from 'antd'

export default class Index extends React.Component<any, any> {
    render () {
        return (
            <div className="txt-center" style={{ lineHeight: '200px' }}>
                <h1><Icon type="frown-o" /> 亲，是不是走错地方了？</h1>
            </div>
        )
    }
}
