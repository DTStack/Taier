import React, { Component } from 'react';
import { connect } from 'react-redux';
// import { Icon } from 'antd';

import { Navigator } from 'main/components/nav';

@connect(state => {
    return {
        user: state.user,
        apps: state.apps,
        routing: state.routing,
        app: state.app
    };
})
class Header extends Component {
    constructor (props) {
        super(props);
        this.state = {};
    }

    render () {
        // const { user, apps } = this.props;
        const baseUrl = '/dataQuality.html#';
        const menuItems = [
            {
                id: 'dq/overview',
                name: '概览',
                link: `${baseUrl}/dq/overview`,
                enable: true
            },
            {
                id: 'dq/taskQuery',
                name: '任务查询',
                link: `${baseUrl}/dq/taskQuery`,
                enable: true
            },
            {
                id: 'dq/rule',
                name: '规则配置',
                link: `${baseUrl}/dq/rule`,
                enable: true
            },
            {
                id: 'dq/dataCheck',
                name: '逐行校验',
                link: `${baseUrl}/dq/dataCheck`,
                enable: true
            },
            {
                id: 'dq/dataSource',
                name: '数据源管理',
                link: `${baseUrl}/dq/dataSource`,
                enable: true
            }
        ];

        const logo = (
            <span>
                <img
                    style={{ height: '36px', width: '36px', marginTop: '5px' }}
                    alt="logo"
                    src="/public/dataQuality/img/logo.svg"
                />
                <span
                    style={{
                        paddingLeft: '10px',
                        fontSize: '14px',
                        color: '#ffffff',
                        position: 'absolute',
                        left: '80px',
                        top: 0
                    }}
                >
                    {window.APP_CONF.prefix}.Valid
                </span>
            </span>
        );
        return <Navigator logo={logo} menuItems={menuItems} {...this.props} />;
    }
}
export default Header;
