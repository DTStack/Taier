import React, { Component } from 'react'
import { connect } from 'react-redux'
import { hashHistory } from 'react-router';
import PropTypes from 'prop-types'
import { Layout } from 'antd'

import Sidebar from './sidebar'

const { Sider, Content } = Layout;

const propType = {
    children: PropTypes.node
}
const defaultPro = {
    children: []
}

@connect(state => {
    return {
        project: state.project
    }
})
class Container extends Component {
    componentWillReceiveProps (nextProps) {
        const { params = {}, project = {} } = nextProps;
        if (params.pid != project.id) {
            hashHistory.push(location.hash.replace(/.*?(\/project\/)[^\/]+(.*)/i, `$1${project.id}$2`))
        }
    }
    render () {
        const { children } = this.props
        return (
            <Layout className="dt-dev-project">
                <Sider className="bg-w">
                    <Sidebar {...this.props} />
                </Sider>
                <Content>
                    { children || "i'm container." }
                </Content>
            </Layout>
        )
    }
}
Container.propTypes = propType
Container.defaultProps = defaultPro
export default Container
