import React, { Component } from 'react'
import { connect } from 'react-redux'

import Navigator from 'main/components/nav';

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
    constructor (props) {
        super(props)
        this.state = {}
    }

    render () {
        const { user, apps } = this.props;
        const baseUrl = '/console.html#/console'

        const menuItems = [{
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
            id: 'clusterManage',
            name: '多集群管理',
            link: `${baseUrl}/clusterManage`,
            enable: true
        }];

        const logo = <span>
            <img
                style={{ height: '20px', width: '20px', marginTop: '13px' }}
                alt="logo"
                src="/public/console/img/logo.svg"
            />
            <span style={{
                fontSize: '14px',
                color: '#ffffff',
                position: 'absolute',
                left: '80px',
                top: 0
            }}>
                {window.APP_CONF.prefix}.Console
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
