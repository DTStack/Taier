import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Icon } from 'antd'

import { Navigator, Logo } from 'main/components/nav';

@connect(state => {
    return {
        user: state.user,
        apps: state.apps,
        routing: state.routing,
        common: state.common,
        app: state.app
    }
})
class Header extends Component {

    constructor(props) {
        super(props)
        this.state = {}
    }

    render() {
        const { user, apps } = this.props;
        const baseUrl = '/console.html#/console'

        const menuItems = [{
            id: 'resourceManage',
            name: '资源管理',
            link: `${baseUrl}/resourceManage`,
            enable: true,
        }, {
            id: 'clusterManage',
            name: '多集群管理',
            link: `${baseUrl}/clusterManage`,
            enable: true,
        }];

        const logo = <span>
            <Icon style={{ fontSize: '18px', color: '#2491F7', marginRight: '10px' }} type="code-o" />
            <span style={{ fontSize: '14px', color: '#ffffff' }}>
                DTinsight.CONSOLE
            </span>
        </span>;
        return <Navigator
            logoWidth="170px"
            logo={logo}
            menuItems={menuItems}
            {...this.props}
        />
    }
}
export default Header

