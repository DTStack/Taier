import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Layout, Button } from 'antd'
import { connect } from 'react-redux'

import Sidebar from './sidebar'
import * as UserAction from '../../store/modules/user'

const { Sider, Content } = Layout;

const propType = {
    children: PropTypes.node,
}
const defaultPro = {
    children: [],
}

class Container extends Component {

    state = {
        collapsed: false,
        mode: 'inline',
    };

    componentDidMount() {
        this.props.dispatch(UserAction.getProjectUsers())
    }

    onCollapse = (collapsed) => {
        this.setState({
            collapsed,
            mode: collapsed ? 'vertical' : 'inline',
        });
    }

    render() {
        const { children } = this.props
        const collapsed = this.state.collapsed
        return (
            <Layout className="dt-operation">
                <Sider 
                    collapsible
                    className="bg-w"
                    collapsed={this.state.collapsed}
                    onCollapse={this.onCollapse}>
                    <Sidebar {...this.props} mode={this.state.mode} />
                </Sider>
                <Content style={{ background: '#fff' }}>
                    { children || "i'm container." }
                </Content>
            </Layout>
        )
    }
}
Container.propTypes = propType
Container.defaultProps = defaultPro
export default connect()(Container)
