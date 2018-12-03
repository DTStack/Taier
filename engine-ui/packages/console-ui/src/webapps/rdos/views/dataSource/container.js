import React, { Component } from 'react'
import PropTypes from 'prop-types'

import '../../styles/pages/dataSource.scss';

const propType = {
    children: PropTypes.node
}
const defaultPro = {
    children: []
}

class Container extends Component {
    render () {
        const { children } = this.props
        return (
            <div className="inner-container">
                {children || "i'm container."}
            </div>
        )
    }
}
Container.propTypes = propType
Container.defaultProps = defaultPro
export default Container
