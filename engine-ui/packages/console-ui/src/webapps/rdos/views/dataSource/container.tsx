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
    static propTypes = propType;
    static defaultProps = defaultPro;
    render () {
        const { children } = this.props
        return (
            <div className="inner-container">
                {children || "i'm container."}
            </div>
        )
    }
}
export default Container
