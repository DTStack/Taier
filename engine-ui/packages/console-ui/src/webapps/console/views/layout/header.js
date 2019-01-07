import React, { Component } from 'react'
import { connect } from 'react-redux'

import Navigator from 'main/components/nav';
import { getHeaderLogo } from 'main/consts';

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
        const baseUrl = '/console.html#/console'
        const { app } = this.props;

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
                className='c-header__logo c-header__logo--console'
                alt="logo"
                src={getHeaderLogo(app.id)}
            />
            <span className='c-header__title c-header__title--console'>
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
