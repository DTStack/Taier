import React, { Component } from 'react'
import { connect } from 'react-redux'

import Navigator from '../../components/nav';
import { getHeaderLogo } from '../../consts'

@connect(state => {
    return {
        user: state.user,
        apps: state.apps,
        routing: state.routing
    }
})
class Header extends Component {
    constructor (props) {
        super(props)
        this.state = {}
    }

    render () {
        const { apps } = this.props;
        const logo = <span>
            <img
                className='c-header__logo'
                alt="logo"
                src={getHeaderLogo()}
            />
            <span className='c-header__title c-header__title--main'>
                {window.APP_CONF.prefix}
            </span>
        </span>;

        return <Navigator
            logo={logo}
            menuItems={apps}
            {...this.props}
        />
    }
}
export default Header
