import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'
import { Link } from 'react-router'
import { Icon } from 'antd'

import utils from 'utils'
import NotFund from 'widgets/notFund'

import Header from './../layout/header'
import * as UserAction from '../../actions/user'
import { Navigator, Logo, Title, MyIcon }  from '../../components/nav'

@connect(state => {
    return {
        user: state.user,
        apps: state.apps,
        routing: state.routing
    }
})
class SysAdmin extends Component {

    componentDidMount() {}

    render() {
        const { user, apps, children } = this.props;
        const logo = (<Link to="/message">
            <MyIcon>
                <Icon type="setting" />
            </MyIcon>
            <Title>系统管理</Title>
        </Link>)

        const content = children ? React.cloneElement(children, {
            apps,
        }) : <NotFund />;

        const menuItems = [{
            id: 'admin/user',
            name: '用户管理',
            link: '/admin/user',
            enable: true,
        }, {
            id: 'admin/role',
            name: '角色管理',
            link: '/admin/role',
            enable: true,
        }]

        return (
            <div className="message">
                <Navigator 
                    logo={logo}
                    menuItems={menuItems}
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
