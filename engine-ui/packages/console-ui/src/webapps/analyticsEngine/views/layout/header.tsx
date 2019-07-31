import * as React from 'react'
import { connect } from 'react-redux'

import Navigator from 'main/components/nav';
import { getHeaderLogo } from 'main/consts';

declare var window: any;

@(connect((state: any) => {
    return {
        user: state.user,
        apps: state.apps,
        routing: state.routing,
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
        const menuItems: any = [];
        const { app, licenseApps } = this.props;
        const logo = <React.Fragment>
            <img
                className='c-header__logo c-header__logo--analytics'
                alt="logo"
                src={getHeaderLogo(app.id)}
            />
            <span className='c-header__title c-header__title--analytics'>
                {window.APP_CONF.prefix ? `${window.APP_CONF.prefix}.` : ''}{window.APP_CONF.name}
            </span>
        </React.Fragment>
        return <Navigator
            logo={logo}
            menuItems={menuItems}
            licenseApps={licenseApps}
            {...this.props}
        />
    }
}
export default Header
