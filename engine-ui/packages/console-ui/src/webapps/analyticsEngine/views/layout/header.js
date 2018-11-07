import React, { Component } from 'react'
import { connect } from 'react-redux'

import { Navigator }  from 'main/components/nav';

@connect(state => {
    return {
        user: state.user,
        apps: state.apps,
        routing: state.routing,
        app: state.app,
    }
})
class Header extends Component {

    constructor(props) {
        super(props)
        this.state = {}
    }

    render() {
  
        const menuItems = [];

        const logo = <span>
            <img
                style={{ height: "20px", marginTop: "15px" }}
                alt="logo"
                src="/public/analyticsEngine/img/logo.svg"
            />
            <span style={{
                fontSize: "14px",
                color: "#ffffff",
                position: "absolute",
                left: "70px",
                top: 0
            }}>
                DTinsight.Analytics
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

