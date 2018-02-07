import React, { Component } from 'react'
import { Link } from 'react-router'
import { Menu, Icon } from 'antd'

const SubMenu = Menu.SubMenu;

export default class Sidebar extends Component {

    constructor(props) {
        super(props)
        this.state = {
            current: 'overview',
        }
    }

    componentDidMount() {
        this.updateSelected()
    }

    componentWillReceiveProps() {
        this.updateSelected()
    }

    updateSelected = () => {
        const routes = this.props.router.routes
        if (routes.length > 2) {
            let current = routes[2].path || 'overview'
            if (current.indexOf('task-flow') > -1 || current.indexOf('task-patch-data') > -1) {
                current = 'offline-operation'
            }
            this.setState({ current })
        }
    }

    handleClick = (e) => {
        this.setState({
            current: e.key,
        });
    }

    render() {
        const base = '/operation'
        return (
            <div className="sidebar my-ant-menu" style={{ borderRight: 'none' }}>
                <Menu
                  onClick={this.handleClick}
                  selectedKeys={[this.state.current]}
                  defaultOpenKeys={['offline', 'alarm']}
                  defaultSelectedKeys={[this.state.current]}
                  style={{ height: '100%' }}
                  mode={this.props.mode}
                >
                    <Menu.Item key="overview">
                        <Link to={`${base}`}><Icon type="line-chart" />
                            <span className="nav-text">运维总览</span>
                        </Link>
                    </Menu.Item>
                    <SubMenu key="offline" title={<span><Icon type="code" /><span className="nav-text">离线任务</span></span>}>
                        <Menu.Item key="offline-operation">
                            <Link to={`${base}/offline-operation`}>
                                任务运维
                            </Link>
                        </Menu.Item>
                        <Menu.Item key="offline-management">
                            <Link to={`${base}/offline-management`}>
                                任务管理
                            </Link>
                        </Menu.Item>
                    </SubMenu>
                    <Menu.Item key="realtime">
                        <Link to={`${base}/realtime`}><Icon type="code-o" />
                            <span className="nav-text">实时任务</span>
                        </Link>
                    </Menu.Item>
                    <SubMenu key="alarm" title={<span><Icon type="bell" /><span className="nav-text">监控告警</span></span>}>
                        <Menu.Item key="alarm-record">
                            <Link to={`${base}/alarm-record`}>
                                告警记录
                            </Link>
                        </Menu.Item>
                        <Menu.Item key="alarm-config">
                            <Link to={`${base}/alarm-config`}>
                                自定义配置
                            </Link>
                        </Menu.Item>
                    </SubMenu>
                </Menu>
            </div>
        )
    }
}
