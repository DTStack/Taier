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
        const baseUrl = '/label.html#'
        const menuItems = [{
            id: 'label/overview',
            name: '概览',
            link: `${baseUrl}/label/overview`,
            enable: true,
        }, 
        // {
        //     id: 'label/taskQuery',
        //     name: '任务查询',
        //     link: `${baseUrl}/label/taskQuery`,
        //     enable: true,
        // }, {
        //     id: 'label/rule',
        //     name: '规则配置',
        //     link: `${baseUrl}/label/rule`,
        //     enable: true,
        // }, {
        //     id: 'label/dataCheck',
        //     name: '逐行校验',
        //     link: `${baseUrl}/label/dataCheck`,
        //     enable: true,
        // }, 
        {
            id: 'label/dataSource',
            name: '数据源管理',
            link: `${baseUrl}/label/dataSource`,
            enable: true,
        }];

        const logo = <span>
            <Icon style={{fontSize: '18px', color: '#2491F7', marginRight: '10px'}} type="tags-o"/>
            <span style={{fontSize: '14px', color: '#ffffff'}}>
                标签工厂
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

