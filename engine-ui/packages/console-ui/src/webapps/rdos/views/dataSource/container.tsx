import * as React from 'react'
import PropTypes from 'prop-types'

import '../../styles/pages/dataSource.scss';

const propType: any = {
    children: PropTypes.node
}
const defaultPro: any = {
    children: []
}

class Container extends React.Component<any, any> {
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
