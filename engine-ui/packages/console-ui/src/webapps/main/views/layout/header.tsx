import * as React from 'react'
import { connect } from 'react-redux'

import Navigator, { compareEnableApp } from '../../components/nav';
import { getHeaderLogo } from '../../consts'

declare var window: any;

@(connect((state: any) => {
    return {
        user: state.user,
        apps: state.apps,
        licenseApps: state.licenseApps,
        routing: state.routing
    }
}) as any)
class Header extends React.Component<any, any> {
    constructor (props: any) {
        super(props)
        this.state = {}
    }

    render () {
        const { apps, licenseApps } = this.props;
        const logo =
            <React.Fragment>
                <img
                    alt="logo"
                    src={getHeaderLogo()}
                />
                <span className='c-header__title c-header__title--main'>
                    {window.APP_CONF.prefix}
                </span>
            </React.Fragment>;

        return <Navigator
            logo={logo}
            menuItems={compareEnableApp(apps, licenseApps, true)}
            licenseApps={licenseApps}
            {...this.props}
        />
    }
}
export default Header
