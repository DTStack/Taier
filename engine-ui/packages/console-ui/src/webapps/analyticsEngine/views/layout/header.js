import React, { Component } from 'react'
import { connect } from 'react-redux'

import Navigator from 'main/components/nav';
import { getHeaderLogo } from 'main/consts';
@connect(state => {
    return {
        user: state.user,
        apps: state.apps,
        routing: state.routing,
        app: state.app
    }
})
class Header extends Component {
    constructor (props) {
        super(props)
        this.state = {}
    }

    render () {
        const menuItems = [];
        const { app } = this.props;
        const logo = <span>
            <img
                className='c-header__logo c-header__logo--analytics'
                alt="logo"
                src={getHeaderLogo(app.id)}
            />
            <span className='c-header__title c-header__title--analytics'>
                {window.APP_CONF.prefix ? `${window.APP_CONF.prefix}.` : ''}Analytics
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
