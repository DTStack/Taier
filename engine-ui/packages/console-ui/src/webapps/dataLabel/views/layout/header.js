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
        const { user, apps } = this.props;
        const baseUrl = '/dataLabel.html#'
        const menuItems = [{
            id: 'dl/overview',
            name: '概览',
            link: `${baseUrl}/dl/overview`,
            enable: true,
        }, {
            id: 'dl/tagConfig',
            name: '标签配置',
            link: `${baseUrl}/dl/tagConfig`,
            enable: true,
        }, {
            id: 'dl/dataSource',
            name: '数据源管理',
            link: `${baseUrl}/dl/dataSource`,
            enable: true,
        }];

        const logo = <span>
            <Icon style={{ fontSize: '18px', color: '#2491F7', marginRight: '10px' }} type="tags-o"/>
            <span style={{ fontSize: '14px', color: '#ffffff' }}>
                DTinsight.Tag
            </span>
        </span>
        // <Logo linkTo="/" img={'public/main/img/logo.png'}/>
        return <Navigator 
            logo={logo}
            menuItems={menuItems}
            {...this.props}
        />
    }
}
export default Header

