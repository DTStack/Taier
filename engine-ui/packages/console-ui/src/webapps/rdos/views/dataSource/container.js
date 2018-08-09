import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Layout } from 'antd'

import Sidebar from './sidebar'
import '../../styles/pages/dataSource.scss';

const { Sider, Content } = Layout;

const propType = {
    children: PropTypes.node,
}
const defaultPro = {
    children: [],
}

class Container extends Component {
    render() {
        const { children } = this.props
        return (
            <Layout >
                <Sider className="bg-w">
                    <Sidebar {...this.props} />
                </Sider>
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
