import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Icon } from 'antd'

import { Navigator, Logo } from '../../components/nav';

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
        const { user, apps } = this.props;
        const logo = <span>
            <img
                alt="logo"
                style={{ height: '20px', marginTop: '15px' }}
                src="public/main/img/logo.svg"
            />
            <span style={{
                paddingLeft: '10px',
                fontSize: '14px',
                color: '#ffffff',
                position: 'absolute',
                left: '70px',
                top: 0,
                letterSpacing: '1px'
            }}>
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
