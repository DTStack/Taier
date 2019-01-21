import React, { Component } from 'react'
import { connect } from 'react-redux'

import Navigator from 'main/components/nav';
import { getHeaderLogo } from 'main/consts';

@connect(state => {
    return {
        user: state.user,
        apps: state.apps,
        routing: state.routing,
        app: state.app,
        licenseApps: state.licenseApps
    };
})
class Header extends Component {
    constructor (props) {
        super(props);
        this.state = {};
    }
    fixArrayIndex = (arr) => {
        let fixArrChildrenApps = [];
        arr.map(item => {
            switch (item.name) {
                case '概览':
                    fixArrChildrenApps[0] = item;
                    break;
                case '任务查询':
                    fixArrChildrenApps[1] = item;
                    break;
                case '规则配置':
                    fixArrChildrenApps[2] = item;
                    break;
                case '逐行校验':
                    fixArrChildrenApps[3] = item;
                    break;
                case '数据源管理':
                    fixArrChildrenApps[4] = item;
                    break;
            }
        })
        return fixArrChildrenApps
    }
    render () {
        const { app, licenseApps } = this.props;
        const baseUrl = '/dataQuality.html#';
        console.log(licenseApps[3].children)
        const fixArrChildrenApps = this.fixArrayIndex(licenseApps[3].children);
        console.log(fixArrChildrenApps);
        const menuItems = [
            {
                id: 'dq/overview',
                name: '概览',
                link: `${baseUrl}/dq/overview`,
                enable: fixArrChildrenApps[0].is_Show
            },
            {
                id: 'dq/taskQuery',
                name: '任务查询',
                link: `${baseUrl}/dq/taskQuery`,
                enable: fixArrChildrenApps[1].is_Show
            },
            {
                id: 'dq/rule',
                name: '规则配置',
                link: `${baseUrl}/dq/rule`,
                enable: fixArrChildrenApps[2].is_Show
            },
            {
                id: 'dq/dataCheck',
                name: '逐行校验',
                link: `${baseUrl}/dq/dataCheck`,
                enable: fixArrChildrenApps[3].is_Show
            },
            {
                id: 'dq/dataSource',
                name: '数据源管理',
                link: `${baseUrl}/dq/dataSource`,
                enable: fixArrChildrenApps[4].is_Show
            }
        ];

        const logo = (
            <span>
                <img
                    className='c-header__logo c-header__logo--dq'
                    alt="logo"
                    src={getHeaderLogo(app.id)}
                />
                <span className='c-header__title c-header__title--dq'>
                    {window.APP_CONF.prefix ? `${window.APP_CONF.prefix}.` : ''}{window.APP_CONF.name}
                </span>
            </span>
        );
        return <Navigator
            logo={logo}
            menuItems={menuItems}
            licenseApps={licenseApps}
            {...this.props}
            showHelpSite={true}
            helpUrl='http://rdos.dev.dtstack.net:8080/public/helpSite/dtinsight-valid/v3.0/01_DTinsightValidHelp_Summary.html'
        />;
    }
}
export default Header;
