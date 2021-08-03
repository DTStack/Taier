import * as React from 'react'
import PropTypes from 'prop-types'
import { Layout } from 'antd'
import Sidebar from './sidebar'
import Header from '../layout/header'
import { connect } from 'react-redux'

import { updateApp } from 'dt-common/src/actions/app'
import { consoleApp } from 'dt-common/src/consts/defaultApps'

const { Sider, Content } = Layout;
const propType: any = {
    children: PropTypes.node
}
const defaultPro: any = {
    children: []
}

const mapStateToProps = (state: any) => {
    const { common } = state;
    return { common }
};
@(connect(
    mapStateToProps
) as any)
class Container extends React.Component<any, any> {
    state: any = {
        collapsed: false
    };
    static propTypes = propType
    static defaultProps = defaultPro

    componentDidMount () {
        const { dispatch } = this.props;
        dispatch(updateApp(consoleApp));
    }

    toggleCollapsed = (data) => {
        this.setState({
            collapsed: data
        });
    }

    render () {
        const { children } = this.props
        const { collapsed } = this.state
        let header = <Header operationHeader={true} />

        return (
            <div className="main">
                {header}
                <div className="container overflow-x-hidden" id='JS_console_container'>
                    <Layout className="dt-operation">
                        <Sider className="bg-w ant-slider-pos"
                            collapsed={collapsed}
                        >
                            <Sidebar {...this.props} changeCollapsed={this.toggleCollapsed} />
                        </Sider>
                        <Content>
                            { children || "i'm container." }
                        </Content>
                    </Layout>
                </div>
            </div>
        )
    }
}

export default Container
