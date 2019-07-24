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
    state = {
        collapsed: false,
        mode: 'inline'
    };

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps (nextProps) {
        const { params = {}, project = {} } = nextProps;
        if (params.pid != project.id) {
            // eslint-disable-next-line
            hashHistory.push(location.hash.replace(/.*?(\/project\/)[^\/]+(.*)/i, `$1${project.id}$2`))
        }
    }
    onCollapse = (collapsed) => {
        this.setState({
            collapsed,
            mode: collapsed ? 'vertical' : 'inline'
        });
    }

    render () {
        const { children } = this.props
        return (
            <Layout className="dt-dev-project">
                <Sider className="bg-w"
                    collapsible
                    collapsed={this.state.collapsed}
                    onCollapse={this.onCollapse}
                >
                    <Sidebar {...this.props} mode={this.state.mode} />
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
