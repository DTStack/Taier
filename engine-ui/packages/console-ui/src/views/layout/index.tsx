import * as React from 'react'
import { Layout } from 'antd';
import Sidebar from './sidebar';

const { Content } = Layout;

export function MyLayout(props: React.PropsWithChildren<any>) {
    const { children } = props;
    return (
        <Layout style={{ position: "relative", height: '100%' }}>
            <Sidebar />
            <Layout style={{ padding: '20px' }}>
                <Content style={{ background: '#fff', padding: 20, margin: 0, minHeight: 280 }}>
                    { children }
                </Content>
            </Layout>
        </Layout>
    )
}

export default MyLayout;
