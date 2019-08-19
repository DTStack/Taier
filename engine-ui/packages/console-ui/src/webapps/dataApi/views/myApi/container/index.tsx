import * as React from 'react';

import {
    Layout
} from 'antd';
import ContainerSider from './sider';

const { Sider, Content } = Layout;

class MyApiContainer extends React.Component<any, any> {
    render () {
        const { children, ...others } = this.props;
        return (
            <Layout>
                <Sider>
                    <ContainerSider {...others} />
                </Sider>
                <Content style={{ height: '100%' }}>
                    {children}
                </Content>
            </Layout>
        )
    }
}

export default MyApiContainer;
