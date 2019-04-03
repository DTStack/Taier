import React, { Component } from 'react'

import '../../styles/views/index/index.scss';

class Container extends Component {
    render () {
        const { children } = this.props
        return (
            <div className="inner-container">
                {children || 'source' }
            </div>
        )
    }
}
export default Container
