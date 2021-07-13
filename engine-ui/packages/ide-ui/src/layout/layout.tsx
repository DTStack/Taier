import React from 'react';
import { Layout, Menu } from 'antd';
import { AppContainer } from '../registerMicroApps';

const { Header, Content } = Layout;

export default function MyLayout(props: React.PropsWithChildren<any>) {
    const { children } = props;

    return (
        <Layout style={{ position: "relative", height: '100%' }}>
            <Header className="dt-layout-header" style={{ width: "100%", minWidth: 100 }}>
                <div className="logo dt-header-log-wrapper" style={{ marginRight: '50px' }}>
                    <span className='c-header__title'>DAGScheduleX</span>
                </div>
                <Menu
                    mode="horizontal"
                    defaultSelectedKeys={['devTask']}
                >
                    <Menu.Item key="dataSource"><a href="/#/operation-ui/database">数据源</a></Menu.Item>
                    <Menu.Item key="devTask"><a href="/">任务开发</a></Menu.Item>
                    <Menu.Item key="operation"><a href="/#/operation-ui/operation">运维中心</a></Menu.Item>
                    <Menu.Item key="console"><a href="/#/console-ui">控制台</a></Menu.Item>
                </Menu>
            </Header>
            <Layout>
                <Content id={AppContainer} className="dt-container">
                    { children }
                </Content>
            </Layout>
        </Layout>
    )
}