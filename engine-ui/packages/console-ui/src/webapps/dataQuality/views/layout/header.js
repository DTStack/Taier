import React, { Component } from 'react'
import { connect } from 'react-redux'

import Navigator from 'main/components/nav';
import { getHeaderLogo } from 'main/consts';

@connect(state => {
    return {
        user: state.user,
        apps: state.apps,
        routing: state.routing,
        app: state.app
    };
})
class Header extends Component {
    constructor (props) {
        super(props);
        this.state = {};
    }

    render () {
        const { app } = this.props;
        const baseUrl = '/dataQuality.html#';
        const menuItems = [
            {
                id: 'dq/overview',
                name: '概览',
                link: `${baseUrl}/dq/overview`,
                enable: true
            },
            {
                id: 'dq/taskQuery',
                name: '任务查询',
                link: `${baseUrl}/dq/taskQuery`,
                enable: true
            },
            {
                id: 'dq/rule',
                name: '规则配置',
                link: `${baseUrl}/dq/rule`,
                enable: true
            },
            {
                id: 'dq/dataCheck',
                name: '逐行校验',
                link: `${baseUrl}/dq/dataCheck`,
                enable: true
            },
            {
                id: 'dq/dataSource',
                name: '数据源管理',
                link: `${baseUrl}/dq/dataSource`,
                enable: true
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
            {...this.props}
            showHelpSite={true}
            helpUrl='http://rdos.dev.dtstack.net:8080/public/helpSite/dtinsight-valid/v3.0/01_DTinsightValidHelp_Summary.html'
        />;
    }
}
export default Header;
