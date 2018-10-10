import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Icon } from 'antd'

import { Navigator, Logo }  from 'main/components/nav';

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
                style={{ height: "36px", width: '36px', marginTop: "5px" }}
                alt="logo"
                src="/public/analyticsEngine/img/logo.svg"
            />
            <span style={{
                paddingLeft: "10px",
                fontSize: "14px",
                color: "#ffffff",
                position: "absolute",
                left: "80px",
                top: 0
            }}>
                分析引擎
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

