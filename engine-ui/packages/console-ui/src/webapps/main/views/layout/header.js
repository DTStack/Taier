import React, { Component } from 'react'
import { connect } from 'react-redux'

import { Navigator, Logo }  from '../../components/nav';


@connect(state => {
    return {
        user: state.user,
        apps: state.apps,
        routing: state.routing
    }
})
class Header extends Component {

    constructor(props) {
        super(props)
        this.state = {}
    }

    render() {
        const { user, apps } = this.props;
        const logo = <Logo linkTo="/" img={'public/main/img/logo.png'}/>
        return <Navigator 
            logo={logo}
            menuItems={apps}
            {...this.props}
        />
    }
}
export default Header

