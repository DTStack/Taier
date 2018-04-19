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
            <Icon style={{fontSize: '18px', color: '#2491F7', marginRight: '10px'}} type="database"/>
            <span style={{fontSize: '14px', color: '#ffffff'}}>
                DTinsight.Valid
            </span>
        </span>
        // <Logo linkTo="/" img={'public/main/img/logo.png'}/>
        return <Navigator 
            logo={logo}
            menuItems={menuItems}
            {...this.props}
        />
    }
}
export default Header

