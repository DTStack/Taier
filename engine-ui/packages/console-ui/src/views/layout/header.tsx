import * as React from 'react'
import { connect } from 'react-redux'

import Navigator from 'dt-common/src/components/nav';
import { getHeaderLogo } from 'dt-common/src/consts';
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

    render () {
        const baseUrl = '/console';
        const { app, licenseApps } = this.props;

        const menuItems: any = [{
            id: 'queueManage',
            name: '队列管理',
            link: `${baseUrl}/queueManage`,
            enable: true
        }, {
            id: 'resourceManage',
            name: '资源管理',
            link: `${baseUrl}/resourceManage`,
            enable: true
        }, {
            id: 'alarmChannel',
            name: '告警通道',
            link: `${baseUrl}/alarmChannel`,
            enable: true
        }, {
            id: 'clusterManage',
            name: '多集群管理',
            link: `${baseUrl}/clusterManage`,
            enable: true
        }];

        const logo = (
            <div className="logo dt-header-log-wrapper" style={{ float: 'left' }}>
                <img
                    className='c-header__logo'
                    alt="logo"
                    src={getHeaderLogo(app.id)}
                />
                <span className='c-header__title'>
                    {window.APP_CONF.prefix ? `${window.APP_CONF.prefix}.` : ''}{window.APP_CONF.name}
                </span>
            </div>
        )
        return <Navigator
            logo={logo}
            menuItems={menuItems}
            licenseApps={licenseApps}
            {...this.props}
        />
    }
}
export default Header
