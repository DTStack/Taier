import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Icon } from 'antd'

import { Navigator, Logo }  from 'main/components/nav';

@connect(state => {
    return {
        user: state.user,
        apps: state.apps,
        routing: state.routing,
        app: state.app,
    }
})
class Header extends Component {

    constructor(props) {
        super(props)
        this.state = {}
    }

    render() {
        const { user, apps } = this.props;
        const baseUrl = '/dataQuality.html#'
        const menuItems = [{
            id: 'dq/overview',
            name: '概览',
            link: `${baseUrl}/dq/overview`,
            enable: true,
        }, {
            id: 'dq/taskQuery',
            name: '任务查询',
            link: `${baseUrl}/dq/taskQuery`,
            enable: true,
        }, {
            id: 'dq/rule',
            name: '规则配置',
            link: `${baseUrl}/dq/rule`,
            enable: true,
        }, {
            id: 'dq/dataCheck',
            name: '逐行校验',
            link: `${baseUrl}/dq/dataCheck`,
            enable: true,
        }, {
            id: 'dq/dataSource',
            name: '数据源管理',
            link: `${baseUrl}/dq/dataSource`,
            enable: true,
        }];

        const logo = <span>
            <img
                style={{ height: "20px", marginTop: "15px" }}
                alt="logo"
                src="/public/dataQuality/img/logo.png"
            />
            <span style={{
                fontSize: "14px",
                color: "#ffffff",
                position: "absolute",
                left: "70px",
                top: 0
            }}>
                DTinsight.Valid
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

