import * as React from 'react'
import { connect } from 'react-redux'
import { Link } from 'react-router'
import { Icon } from 'antd'

import Navigator, { Title, MyIcon } from '../../components/nav'
import AdminUser from './user'

import '../../styles/views/admin.scss';

@(connect((state: any) => {
    return {
        user: state.user,
        apps: state.apps,
        licenseApps: state.licenseApps,
        routing: state.routing
    }
}) as any)
class SysAdmin extends React.Component<any, any> {
    componentDidMount () {}

    render () {
        const { apps, children, licenseApps, user } = this.props;
        const logo = (<Link to="/admin/user">
            <MyIcon>
                <Icon type="setting" />
            </MyIcon>
            <Title>系统管理</Title>
        </Link>)

        const content = children ? React.cloneElement(children as any, {
            apps
        }) : <AdminUser {...this.props} />;

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
            link: '/admin/audit?app=rdos',
            enable: user.isRoot
        }]

        const settingMenus = [{
            id: 'admin/audit',
            name: '安全审计',
            link: `/admin/audit?app=rdos`,
            enable: user.isRoot,
            enableIcon: true,
            className: 'icon_safe'
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
