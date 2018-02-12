import React, { Component } from 'react'
import { connect } from 'react-redux'

import { Navigator, Logo }  from 'main/components/nav';

@connect(state => {
    return {
        user: state.user,
        apps: state.apps,
    }
})
class Header extends Component {

    constructor(props) {
        super(props)
        this.state = {}
    }

    render() {
        const { user, apps } = this.props;
        const menuItems = [{
            id: 'overview',
            name: '概览',
            link: '/dq/overview',
            enable: true,
        }, {
            id: 'taskQuery',
            name: '任务查询',
            link: '/dq/taskQuery',
            enable: true,
        }, {
            id: 'ruleConfig',
            name: '规则配置',
            link: '/dq/rule',
            enable: true,
        }, {
            id: 'dataCheck',
            name: '逐行校验',
            link: '/dq/dataCheck',
            enable: true,
        }, {
            id: 'dataSource',
            name: '数据源管理',
            link: '/dq/dataSource',
            enable: true,
        }];

        const logo = <Logo linkTo="/" img={'public/main/img/logo.png'}/>
        return <Navigator 
            logo={logo}
            menuItems={menuItems}
            {...this.props}
        />
    }
}
export default Header

