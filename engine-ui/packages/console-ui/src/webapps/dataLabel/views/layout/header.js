import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Icon } from 'antd'

import { Navigator, Logo } from 'main/components/nav';

@connect(state => {
    return {
        user: state.user,
        apps: state.apps,
        routing: state.routing,
        app: state.app,
        common: state.common,
    }
})
class Header extends Component {

    constructor(props) {
        super(props)
        this.state = {}
    }

    render() {
        const { common } = this.props;
        const baseUrl = '/dataLabel.html#';

        const menuItems = [{
            id: 'dl/market',
            name: '标签市场',
            link: `${baseUrl}/dl/market`,
            enable: common.menuList.indexOf('tag_market_menu') > -1,
        }, {
            id: 'dl/mine',
            name: '我的标签',
            link: `${baseUrl}/dl/mine`,
            enable: common.menuList.indexOf('tag_mytag_menu') > -1,
        }, {
            id: 'dl/tagConfig',
            name: '标签配置',
            link: `${baseUrl}/dl/tagConfig`,
            enable: common.menuList.indexOf('tag_config_menu') > -1,
        }, {
            id: 'dl/approval',
            name: '审批授权',
            link: `${baseUrl}/dl/approval`,
            enable: common.menuList.indexOf('tag_authorized_menu') > -1,
        }, {
            id: 'dl/manage',
            name: '服务管理',
            link: `${baseUrl}/dl/manage`,
            enable: common.menuList.indexOf('tag_service_menu') > -1,
        }, {
            id: 'dl/dataSource',
            name: '数据源管理',
            link: `${baseUrl}/dl/dataSource`,
            enable: common.menuList.indexOf('datasource_menu') > -1,
        }];

        const logo = <span>
            <img
                style={{ height: "36px", width: '36px', marginTop: "5px" }}
                alt="logo"
                src="/public/dataLabel/img/logo.svg"
            />
            <span style={{
                paddingLeft: "10px",
                fontSize: "14px",
                color: "#ffffff",
                position: "absolute",
                left: "80px",
                top: 0
             }}>
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

