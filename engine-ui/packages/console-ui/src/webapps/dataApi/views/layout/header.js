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
        const baseUrl = '/dataApi.html#/api'
        const { app } = this.props;
        let showList = {
            overview: false,
            market: false,
            mine: false,
            manage: false,
            approval: false,
            dataSource: false

        }
        const menuList = this.props.common.menuList;
        if (menuList) {
            for (let i in menuList) {
                let item = menuList[i];
                if (item.indexOf('overview') > -1) {
                    showList.overview = true;
                } else if (item.indexOf('market') > -1) {
                    showList.market = true
                } else if (item.indexOf('myapi') > -1) {
                    showList.mine = true
                } else if (item.indexOf('manager') > -1) {
                    showList.manage = true
                } else if (item.indexOf('authorized') > -1) {
                    showList.approval = true
                } else if (item.indexOf('datasource') > -1) {
                    showList.dataSource = true
                }
            }
        }

        const menuItems = [{
            id: 'overview',
            name: '概览',
            link: `${baseUrl}/overview`,
            enable: showList.overview
        }, {
            id: 'market',
            name: 'API市场',
            link: `${baseUrl}/market`,
            enable: showList.market
        }, {
            id: 'mine',
            name: '我的API',
            link: `${baseUrl}/mine`,
            enable: showList.mine
        }, {
            id: 'manage',
            name: 'API管理',
            link: `${baseUrl}/manage`,
            enable: showList.manage
        }, {
            id: 'approvalAndsecurity',
            name: '授权与安全',
            link: `${baseUrl}/approvalAndsecurity`,
            enable: showList.approval
        }, {
            id: 'dataSource',
            name: '数据源管理',
            link: `${baseUrl}/dataSource`,
            enable: showList.dataSource
        }];

        const logo = <span>
            <img
                className='c-header__logo c-header__logo--api'
                alt="logo"
                src={getHeaderLogo(app.id)}
            />
            <span className='c-header__title c-header__title--api'>
                {window.APP_CONF.prefix ? `${window.APP_CONF.prefix}.` : ''}{window.APP_CONF.name}
            </span>
        </span>;
        return <Navigator
            logo={logo}
            menuItems={menuItems}
            {...this.props}
        />
    }
}
export default Header
