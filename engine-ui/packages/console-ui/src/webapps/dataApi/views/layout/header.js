import React, { Component } from 'react'
import { connect } from 'react-redux'

import { Navigator, Logo }  from 'main/components/nav';

@connect(state => {
    return {
        user: state.user,
        apps: state.apps,
    }
})
class Header extends Component {

    constructor(props) {
        super(props)
        this.state = {}
    }

    render() {
        const { user, apps } = this.props;
        const baseUrl = '/api'
        const menuItems = [{
            id: 'overview',
            name: '概览',
            link: `${baseUrl}/overview`,
            enable: true,
        }, {
            id: 'market',
            name: 'API市场',
            link: `${baseUrl}/market`,
            enable: true,
        }, {
            id: 'mine',
            name: '我的API',
            link: `${baseUrl}/mine`,
            enable: true,
        }, {
            id: 'manage',
            name: 'API管理',
            link: `${baseUrl}/manage`,
            enable: true,
        }, {
            id: 'approval',
            name: '授权审批',
            link: `${baseUrl}/approval`,
            enable: true,
        }, {
            id: 'dataSource',
            name: '数据源管理',
            link: `${baseUrl}/dataSource`,
            enable: true,
        }];

        const logo = <Logo linkTo="/" img={'public/main/img/logo.png'}/>
        return <Navigator 
            logo={logo}
            menuItems={menuItems}
            {...this.props}
        />
    }
}
export default Header

