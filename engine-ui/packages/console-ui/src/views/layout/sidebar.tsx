import * as React from 'react';
import { Layout, Menu, Icon } from 'antd';
import { Link } from 'react-router'

const { Sider } = Layout;

class Sidebar extends React.Component<any, any> {
    state: any = {
        collapsed: false,
        mode: 'inline'
    }

    constructor (props: any) {
        super(props)
    }

    onCollapse = () => {
        this.setState({ collapsed: !this.state.collapsed, mode: !this.state.collapsed ? 'vertical' : 'inline' });
    };

    render () {
        const baseUrl = '/console';
        const { collapsed, mode } = this.state;

        const menuItems: any = [{
            id: 'queueManage',
            name: '队列管理',
            link: `${baseUrl}/queueManage`,
            enable: true
        }, {
            id: 'resourceManage',
            name: '资源管理',
            link: `${baseUrl}/resourceManage`,
            enable: true
        }, {
            id: 'alarmChannel',
            name: '告警通道',
            link: `${baseUrl}/alarmChannel`,
            enable: true
        }, {
            id: 'clusterManage',
            name: '多集群管理',
            link: `${baseUrl}/clusterManage`,
            enable: true
        }];

        return (
            <Sider className="dt-layout-sider" collapsed={collapsed}>
                <div className="dt-slider-top-icon" onClick={this.onCollapse}>
                    <Icon type={collapsed ? 'menu-unfold' : 'menu-fold'} />
                </div>
                <Menu
                    mode={mode}
                    defaultSelectedKeys={['queueManage']}
                >
                    {
                        menuItems.map(item => 
                            <Menu.Item key={item.id} >
                                <Link to={item.link} target={item.target} rel="noopener noreferrer">
                                    <span>{item.name}</span>
                                </Link>
                            </Menu.Item>
                        )
                    }
                </Menu>
            </Sider>
        )
    }
}
export default Sidebar
