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
        arr.map(item => {
            switch (item.name) {
                case '概览':
                    fixArrChildrenApps[0] = item;
                    break;
                case 'API市场':
                    fixArrChildrenApps[1] = item;
                    break;
                case '我是API':
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
    }

    render () {
        const baseUrl = '/dataApi.html#/api'
        const { app, licenseApps } = this.props;
        const fixArrChildrenApps = this.fixArrayIndex(licenseApps[4].children);
        console.log('----------------')
        console.log(fixArrChildrenApps);
        // let showList = {
        //     overview: false,
        //     market: false,
        //     mine: false,
        //     manage: false,
        //     approval: false,
        //     dataSource: false

        // }
        // const menuList = this.props.common.menuList;
        // if (menuList) {
        //     for (let i in menuList) {
        //         let item = menuList[i];
        //         if (item.indexOf('overview') > -1) {
        //             showList.overview = true;
        //         } else if (item.indexOf('market') > -1) {
        //             showList.market = true
        //         } else if (item.indexOf('myapi') > -1) {
        //             showList.mine = true
        //         } else if (item.indexOf('manager') > -1) {
        //             showList.manage = true
        //         } else if (item.indexOf('authorized') > -1) {
        //             showList.approval = true
        //         } else if (item.indexOf('datasource') > -1) {
        //             showList.dataSource = true
        //         }
        //     }
        // }

        const menuItems = [{
            id: 'overview',
            name: '概览',
            link: `${baseUrl}/overview`,
            enable: fixArrChildrenApps[0].is_Show
        }, {
            id: 'market',
            name: 'API市场',
            link: `${baseUrl}/market`,
            enable: fixArrChildrenApps[1].is_Show
        }, {
            id: 'mine',
            name: '我的API',
            link: `${baseUrl}/mine`,
            enable: fixArrChildrenApps[2].is_Show
        }, {
            id: 'manage',
            name: 'API管理',
            link: `${baseUrl}/manage`,
            enable: fixArrChildrenApps[3].is_Show
        }, {
            id: 'approvalAndsecurity',
            name: '授权与安全',
            link: `${baseUrl}/approvalAndsecurity`,
            enable: fixArrChildrenApps[4].is_Show
        }, {
            id: 'dataSource',
            name: '数据源管理',
            link: `${baseUrl}/dataSource`,
            enable: fixArrChildrenApps[5].is_Show
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
