import * as React from 'react'
import { connect } from 'react-redux'
import { hashHistory } from 'react-router';
import PropTypes from 'prop-types'
import { Layout, Icon } from 'antd'

import Sidebar from './sidebar'

const { Sider, Content } = Layout;

const propType: any = {
    children: PropTypes.node
}
const defaultPro: any = {
    children: []
}

@(connect((state: any) => {
    return {
        project: state.project
    }
}) as any)
class Container extends React.Component<any, any> {
    state: any = {
        collapsed: false,
        mode: 'inline'
    };
    static propTypes: any;
    static defaultProps: any;
    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const { params = {}, project = {} } = nextProps;
        if (params.pid != project.id) {
            hashHistory.push(location.hash.replace(/.*?(\/dq\/project\/)[^/]+(.*)/i, `$1${project.id}$2`))
        }
    }

    toggleCollapsed = () => {
        this.setState({
            collapsed: !this.state.collapsed,
            mode: !this.state.collapsed ? 'vertical' : 'inline'
        });
    }
    render () {
        const { children } = this.props
        return (
            <Layout className="dt-dev-project">
                <Sider className="bg-w ant-slider-pos"
                    style={{ background: '#262E36' }}
                    collapsed={this.state.collapsed}
                >
                    <div className="ant-slider-pos--collapsed" onClick={ this.toggleCollapsed }>
                        <Icon type={this.state.collapsed ? 'menu-unfold' : 'menu-fold'} />
                    </div>
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
