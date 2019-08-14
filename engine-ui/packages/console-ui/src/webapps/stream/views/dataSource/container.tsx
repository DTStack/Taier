import * as React from 'react'
import PropTypes from 'prop-types'
import { Layout } from 'antd'

import '../../styles/pages/dataSource.scss';

const { Content } = Layout;

const propType: any = {
    children: PropTypes.node
}
const defaultPro: any = {
    children: []
}

class Container extends React.Component<any, any> {
    static propTypes: any;
    static defaultProps: any;
    render () {
        const { children } = this.props
        return (
            <Layout>
                <Content className="inner-container" style={{ overflow: 'auto' }}>
                    { children || "i'm container." }
                </Content>
            </Layout>
        )
    }
}
Container.propTypes = propType
Container.defaultProps = defaultPro
export default Container
