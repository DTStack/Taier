import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Layout, Spin } from 'antd'
import SplitPane from 'react-split-pane'
import { connect } from 'react-redux'

import Sidebar from './sidebar'
import SearchTaskModal from './searchTaskModal'

const { Content } = Layout

const propType = {
    children: PropTypes.node,
}
const defaultPro = {
    children: [],
}

class Container extends Component {

    constructor(props) {
        super(props)
        this.state = {
            loading: 'success',
        }
    }

    componentDidMount() {
        // window.addEventListener('beforeunload', this.beforeunload, false);
    }

    componentWillUnmount() {
        window.removeEventListener('beforeunload', this.beforeunload, false)
    }

    beforeunload = (e) => {
        /* eslint-disable */
        const confirmationMessage = '\o/';
        (e || window.event).returnValue = confirmationMessage;// Gecko + IE
        return confirmationMessage;// Webkit, Safari, Chrome
        /* eslint-disable */
    }

    showLoading = () => {
        const self = this;
        this.setState({ loading: 'loading' })
        setTimeout(() => {
            self.setState({ loading: 'success' })
        }, 200)
    }

    render() {
        const { children } = this.props
        return (
            <Layout className="dt-dev-task">
                <SplitPane split="vertical" minSize={260} maxSize="80%" defaultSize={260} primary="first">
                    <div className="bg-w ant-layout-sider" style={{ width: 'inherit' }}>
                        <Sidebar />
                        <SearchTaskModal />
                    </div>
                    <Content>
                        <Spin
                        tip="Loading..."
                        size="large"
                        spinning={this.state.loading === 'loading'}
                        >
                            <div style={{ width: '100%', height: '100%' }}>
                            { children || "i'm container." }
                            </div>
                        </Spin>
                    </Content>
                </SplitPane>
            </Layout>
        )
    }
}
Container.propTypes = propType
Container.defaultProps = defaultPro

export default Container
