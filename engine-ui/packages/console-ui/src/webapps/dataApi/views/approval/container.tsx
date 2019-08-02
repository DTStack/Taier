import * as React from 'react';

import {
    Layout
} from 'antd';
import ContainerSider from './sider';

const { Sider, Content } = Layout;

class ApprovalAndSecurityContainer extends React.Component<any, any> {
    render () {
        const { children, ...others } = this.props;
        return (
            <Layout>
                <Sider>
                    <ContainerSider {...others} />
                </Sider>
                <Content>
                    {children}
                </Content>
            </Layout>
        )
    }
}

export default ApprovalAndSecurityContainer;
