import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Link } from 'react-router'
import { Icon } from 'antd'

import NotFund from 'widgets/notFund'

import Navigator, { Title, MyIcon } from '../../components/nav'

@connect(state => {
    return {
        user: state.user,
        apps: state.apps,
        licenseApps: state.licenseApps,
        routing: state.routing
    }
})
class SysAdmin extends Component {
    componentDidMount () {}

    render () {
        const { apps, children, licenseApps, user } = this.props;
        const logo = (<Link to="/admin/user">
            <MyIcon>
                <Icon type="setting" />
            </MyIcon>
            <Title>系统管理</Title>
        </Link>)

        const content = children ? React.cloneElement(children, {
            apps
        }) : <NotFund />;

        const menuItems = [{
            id: 'admin/user',
            name: '用户管理',
            link: '/admin/user',
            enable: true
        }, {
            id: 'admin/role',
            name: '角色管理',
            link: '/admin/role',
            enable: true
        }, {
            id: 'admin/audit',
            name: '安全审计',
            link: '/admin/audit',
            enable: user.isRoot
        }]

        const settingMenus = [{
            id: 'admin/audit',
            name: '安全审计',
            link: `/admin/audit`,
            enable: user.isRoot,
            enableIcon: true,
            className: 'safeaudit'
        }];

        return (
            <div className="message">
                <Navigator
                    logo={logo}
                    licenseApps={licenseApps}
                    menuItems={menuItems}
                    settingMenus={settingMenus}
                    {...this.props}
                />
                <div className="container">
                    { content }
                </div>
            </div>
        )
    }
}

export default SysAdmin
