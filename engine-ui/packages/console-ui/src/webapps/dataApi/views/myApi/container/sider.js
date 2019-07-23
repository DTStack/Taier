import React from 'react';
import { Link } from 'react-router';

import {
    Menu, Icon
} from 'antd';

const MenuItem = Menu.Item;

class ApprovalAndSecuritySider extends React.Component {
    getCurrentKey () {
        const { routes = [] } = this.props;
        let path = '';
        if (routes.length && routes.length > 3) {
            path = routes[3].path;
        }
        return path || 'myApi';
    }
    render () {
        const base = `/api/mine`;
        return (
            <div className="m-ant-menu" style={{ height: '100%' }}>
                <Menu

                    style={{ width: 200, height: '100%' }}
                    mode="inline"
                    selectedKeys={[this.getCurrentKey()]}
                >
                    <MenuItem key='myApi' >
                        <Link to={`${base}/myApi`}>
                            <Icon type="api" />我的API
                        </Link>
                    </MenuItem>
                    <MenuItem key='callApi'>
                        <Link to={`${base}/callApi`}>
                            <Icon type="swap" />API调用
                        </Link>
                    </MenuItem>
                </Menu>
            </div>
        )
    }
}

export default ApprovalAndSecuritySider;
