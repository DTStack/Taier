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
        common: state.common
    }
})
class Header extends Component {
    constructor (props) {
        super(props)
        this.state = {}
    }

    render () {
        const { common, app } = this.props;
        const baseUrl = '/dataLabel.html#';

        const menuItems = [{
            id: 'dl/market',
            name: '标签市场',
            link: `${baseUrl}/dl/market`,
            enable: common.menuList.indexOf('tag_market_menu') > -1
        }, {
            id: 'dl/mine',
            name: '我的标签',
            link: `${baseUrl}/dl/mine`,
            enable: common.menuList.indexOf('tag_mytag_menu') > -1
        }, {
            id: 'dl/tagConfig',
            name: '标签配置',
            link: `${baseUrl}/dl/tagConfig`,
            enable: common.menuList.indexOf('tag_config_menu') > -1
        }, {
            id: 'dl/approval',
            name: '审批授权',
            link: `${baseUrl}/dl/approval`,
            enable: common.menuList.indexOf('tag_authorized_menu') > -1
        }, {
            id: 'dl/manage',
            name: '服务管理',
            link: `${baseUrl}/dl/manage`,
            enable: common.menuList.indexOf('tag_service_menu') > -1
        }, {
            id: 'dl/dataSource',
            name: '数据源管理',
            link: `${baseUrl}/dl/dataSource`,
            enable: common.menuList.indexOf('datasource_menu') > -1
        }];

        const logo = <span>
            <img
                className='c-header__logo c-header__logo--label'
                alt="logo"
                src={getHeaderLogo(app.id)}
            />
            <span className='c-header__title c-header__title--label'>
                {window.APP_CONF.prefix}.Tag
            </span>
        </span>
        return <Navigator
            logo={logo}
            menuItems={menuItems}
            {...this.props}
        />
    }
}
export default Header
