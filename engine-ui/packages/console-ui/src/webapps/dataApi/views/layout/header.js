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
        app: state.app,
        licenseApps: state.licenseApps
    }
})
class Header extends Component {
    constructor (props) {
        super(props)
        this.state = {}
    }

    fixArrayIndex = (arr) => {
        let fixArrChildrenApps = [];
        if (arr && arr.length > 1) {
            arr.map(item => {
                switch (item.name) {
                    case '概览':
                        fixArrChildrenApps[0] = item;
                        break;
                    case 'API市场':
                        fixArrChildrenApps[1] = item;
                        break;
                    case '我的API':
                        fixArrChildrenApps[2] = item;
                        break;
                    case 'API管理':
                        fixArrChildrenApps[3] = item;
                        break;
                    case '授权与安全':
                        fixArrChildrenApps[4] = item;
                        break;
                    case '数据源管理':
                        fixArrChildrenApps[5] = item;
                        break;
                }
            })
            return fixArrChildrenApps
        } else {
            return [];
        }
    }

    render () {
        const baseUrl = '/dataApi.html#/api'
        const { app, licenseApps } = this.props;
        const fixArrChildrenApps = this.fixArrayIndex(licenseApps[4] && licenseApps[4].children);
        const overviewNav = fixArrChildrenApps[0];
        const marketNav = fixArrChildrenApps[1];
        const mineNav = fixArrChildrenApps[2];
        const manaNav = fixArrChildrenApps[3];
        const approvalNav = fixArrChildrenApps[4];
        const dataSourceNav = fixArrChildrenApps[5];
        const menuItems = [{
            id: 'overview',
            name: '概览',
            link: `${baseUrl}/overview`,
            enable: overviewNav && overviewNav.isShow
        }, {
            id: 'market',
            name: 'API市场',
            link: `${baseUrl}/market`,
            enable: marketNav && marketNav.isShow
        }, {
            id: 'mine',
            name: '我的API',
            link: `${baseUrl}/mine`,
            enable: mineNav && mineNav.isShow
        }, {
            id: 'manage',
            name: 'API管理',
            link: `${baseUrl}/manage`,
            enable: manaNav && manaNav.isShow
        }, {
            id: 'approvalAndsecurity',
            name: '授权与安全',
            link: `${baseUrl}/approvalAndsecurity`,
            enable: approvalNav && approvalNav.isShow
        }, {
            id: 'dataSource',
            name: '数据源管理',
            link: `${baseUrl}/dataSource`,
            enable: dataSourceNav && dataSourceNav.isShow
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
            licenseApps={licenseApps}
            {...this.props}
        />
    }
}
export default Header
