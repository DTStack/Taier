import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Layout } from 'antd'
import { connect } from 'react-redux';

import Sidebar from './sidebar';
import '../../styles/pages/dataManage.scss';

const { Sider, Content } = Layout;

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
            <Layout className="dt-dev-datamanagement g-datamanage">
                <Sider className="bg-w">
                    <Sidebar {...this.props} />
                </Sider>
                <Content style={{ position: 'relative' }}>
                    { children || '概览' }
                </Content>
            </Layout>
        )
    }
}
Container.propTypes = propType
Container.defaultProps = defaultPro
export default connect(state => ({
    project: state.project.id,
    project_obj: state.project,
    user: state.user
}), null)(Container);
