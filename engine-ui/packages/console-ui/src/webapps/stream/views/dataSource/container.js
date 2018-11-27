import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Layout } from 'antd'

import '../../styles/pages/dataSource.scss';

const { Content } = Layout;

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
            <Layout >
                <Content className="inner-container">
                    { children || "i'm container." }
                </Content>
            </Layout>
        )
    }
}
Container.propTypes = propType
Container.defaultProps = defaultPro
export default Container
