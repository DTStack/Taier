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
        return path || 'approval';
    }
    render () {
        const base = `/api/approvalAndsecurity`;
        return (
            <div className="m-ant-menu" style={{ height: '100%' }}>
                <Menu

                    style={{ width: 200, height: '100%' }}
                    mode="inline"
                    selectedKeys={[this.getCurrentKey()]}
                >
                    <MenuItem key='approval' >
                        <Link to={`${base}/approval`}>
                            <Icon type="solution" />审批授权
                        </Link>
                    </MenuItem>
                    <MenuItem key='security'>
                        <Link to={`${base}/security`}>
                            <Icon type="safety" />安全组
                        </Link>
                    </MenuItem>
                </Menu>
            </div>
        )
    }
}

export default ApprovalAndSecuritySider;
