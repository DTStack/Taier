import React, { Component } from 'react'
import { Menu, Dropdown, Icon } from 'antd'
import { Link } from 'react-router'

/* eslint-disable */
const UIC_URL_TARGET = APP_CONF.UIC_URL || ''
/* eslint-disable */


const SubMenu = Menu.SubMenu

class Header extends Component {

    constructor(props) {
        super(props)
        this.state = {}
    }

    componentDidMount() {
        this.updateSelected()
    }

    // 控制项目下拉菜单的显示
    componentWillReceiveProps() {
        this.updateSelected()
    }

    handleClick = (e) => {
        const props = e.item.props
        const { router, dispatch } = this.props
        this.setState({ current: e.key });
    }

    clickUserMenu = (obj) => {
        if (obj.key === 'logout') {
            Api.logout();
        }
    }

    goIndex = () => {
        const { router } = this.props
        this.setState({ current: 'overview' })
        router.push('/')
    }

    updateSelected() {
        let pathname = this.props.router.location.pathname
        const routes = pathname ? pathname.split('/') : []
        let path = routes.length > 0 && routes[1] !== '' ? routes[1] : 'overview'
        if (path && (path.indexOf('task') > -1 || path.indexOf('offline') > -1)) {
            this.setState({
                devPath: pathname,
            })
            path = 'realtime'
        }
        if (path !== this.state.current) {
            this.setState({
                current: path,
            })
        }
        return path
    }

    initUserDropMenu = () => {
        return (
            <Menu onClick={this.clickUserMenu}>
                <Menu.Item key="ucenter">
                    <a href={UIC_URL_TARGET}>用户中心</a>
                </Menu.Item>
                <Menu.Item key="logout">
                    退出登录
                </Menu.Item>
            </Menu>
        )
    }

    render() {
        const { user } = this.props
        const { current } = this.state
        return (
            <header className="header">
                <div className="logo left txt-left">
                    <a onClick={this.goIndex}><img alt="logo" src="public/main/img/logo.png" /></a>
                </div>
                <div className="menu left">
                    <Menu
                      className="my-menu"
                      onClick={this.handleClick}
                      selectedKeys={[this.state.current]}
                      mode="horizontal"
                    >
                        <Menu.Item className="my-menu-item">
                            <Link to={'/main'}>首页</Link>
                        </Menu.Item>
                        <Menu.Item className="my-menu-item">
                            <Link to={'/'}>主数据</Link>
                        </Menu.Item>
                        <Menu.Item className="my-menu-item">
                            <Link to={'/rdos.html#/'} target="_blank">数据开发</Link>
                        </Menu.Item>
                        <Menu.Item className="my-menu-item">
                            <Link to={'/rdos.html#/'} target="_blank">数据质量</Link>
                        </Menu.Item>
                        <Menu.Item className="my-menu-item">
                            <Link to={'/rdos.html#/'} target="_blank">API管理</Link>
                        </Menu.Item>
                    </Menu>
                </div>
                <div className="user-info right">
                    <Dropdown overlay={this.initUserDropMenu()} trigger={['click']}>
                        <a className="ant-dropdown-link">
                            {user.userName || '未登录'}
                            <Icon type="down" />
                        </a>
                    </Dropdown>
                </div>
            </header>
        )
    }
}
export default Header

