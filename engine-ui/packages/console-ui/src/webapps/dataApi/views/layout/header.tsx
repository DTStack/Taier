import * as React from 'react'
import { connect } from 'react-redux'

import Navigator from 'main/components/nav';
import { getHeaderLogo } from 'main/consts';

import docPath from '../../consts/docPath';

declare var window: any;
@(connect((state: any) => {
    return {
        user: state.user,
        apps: state.apps,
        routing: state.routing,
        common: state.common,
        app: state.app,
        licenseApps: state.licenseApps
    }
}) as any)
class Header extends React.Component<any, any> {
    constructor (props: any) {
        super(props)
        this.state = {}
    }

    fixArrayIndex = (arr: any) => {
        let fixArrChildrenApps: any = [];
        let showList: any = {
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
        if (arr && arr.length) {
            arr.map((item: any) => {
                switch (item.name) {
                    case '概览':
                        fixArrChildrenApps[0] = showList.overview ? item : null;
                        break;
                    case 'API市场':
                        fixArrChildrenApps[1] = showList.market ? item : null;
                        break;
                    case '我的API':
                        fixArrChildrenApps[2] = showList.mine ? item : null;
                        break;
                    case 'API管理':
                        fixArrChildrenApps[3] = showList.manage ? item : null;
                        break;
                    case '授权与安全':
                        fixArrChildrenApps[4] = showList.approval ? item : null;
                        break;
                    case '数据源管理':
                        fixArrChildrenApps[5] = showList.dataSource ? item : null;
                        break;
                }
            })
        }
        return fixArrChildrenApps;
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
        const menuItems: any = [{
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

        const logo = <React.Fragment>
            <img
                className='c-header__logo c-header__logo--api'
                alt="logo"
                src={getHeaderLogo(app.id)}
            />
            <span className='c-header__title c-header__title--api'>
                {window.APP_CONF.prefix ? `${window.APP_CONF.prefix}.` : ''}{window.APP_CONF.name}
            </span>
        </React.Fragment>;
        return <Navigator
            logo={logo}
            menuItems={menuItems}
            licenseApps={licenseApps}
            {...this.props}
            showHelpSite={true}
            helpUrl={docPath.INDEX}
        />
    }
}
export default Header
