import * as React from 'react'
import { Link } from 'react-router'
import { Menu, Icon } from 'antd'

const SubMenu = Menu.SubMenu;

export default class Sidebar extends React.Component<any, any> {
    constructor (props: any) {
        super(props)
        this.state = {
            current: 'overview'
        }
    }

    componentDidMount () {
        this.updateSelected()
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps () {
        this.updateSelected()
    }

    updateSelected = () => {
        const routes = this.props.router.routes
        if (routes.length > 3) {
            let current = routes[3].path || 'overview'
            if (current.indexOf('task-patch-data') > -1) {
                current = 'task-patch-data'
            }
            this.setState({ current })
        }
    }

    handleClick = (e: any) => {
        this.setState({
            current: e.key
        });
    }

    render () {
        const base = '/operation'
        return (
            <div className="sidebar m-ant-menu" style={{ borderRight: 'none' }}>
                <Menu
                    onClick={this.handleClick}
                    selectedKeys={[this.state.current]}
                    defaultOpenKeys={['offline', 'alarm']}
                    defaultSelectedKeys={[this.state.current]}
                    style={{ height: '100%' }}
                    mode={this.props.mode}
                >
                    <Menu.Item key="overview" className="ant-itempad_small">
                        <Link to={`${base}`}><Icon type="line-chart" />
                            <span className="nav-text">运维总览</span>
                        </Link>
                    </Menu.Item>
                    <SubMenu key="offline" title={<span><Icon type="usb" /><span className="nav-text">离线任务</span></span>}>
                        <Menu.Item key="offline-management">
                            <Link to={`${base}/offline-management`}>
                                任务管理
                            </Link>
                        </Menu.Item>
                        <Menu.Item key="offline-operation">
                            <Link to={`${base}/offline-operation`}>
                                周期实例
                            </Link>
                        </Menu.Item>
                        <Menu.Item key="task-patch-data">
                            <Link to={`${base}/task-patch-data`}>
                                补数据实例
                            </Link>
                        </Menu.Item>
                    </SubMenu>
                    <SubMenu key="alarm" title={<span><Icon type="exclamation-circle-o" /><span className="nav-text">监控告警</span></span>}>
                        <Menu.Item key="alarm-record">
                            <Link to={`${base}/alarm-record`}>
                                告警记录
                            </Link>
                        </Menu.Item>
                        <Menu.Item key="alarm-config">
                            <Link to={`${base}/alarm-config`}>
                                告警配置
                            </Link>
                        </Menu.Item>
                    </SubMenu>
                    <Menu.Item key="dirty-data" className="ant-itempad_small">
                        <Link to={`${base}/dirty-data`}>
                            <Icon type="book" />
                            <span className="nav-text">脏数据管理</span>
                        </Link>
                    </Menu.Item>
                    {/* <Menu.Item key="log">
                        <Link to={`${base}/log`}>
                            <Icon type="book" />表操作记录
                        </Link>
                    </Menu.Item> */}
                </Menu>
            </div>
        )
    }
}
