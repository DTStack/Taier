import React, { Component } from 'react'
import { Menu, Dropdown, Icon, Badge } from 'antd'
import { Link } from 'react-router'

import pureRender from 'utils/pureRender'
import UserApi from '../../api/user'
import './style.scss'

const UIC_URL_TARGET = APP_CONF.UIC_URL || '';
const SubMenu = Menu.SubMenu

function renderMenuItems(menuItems) {
    return menuItems && menuItems.length > 0 ? menuItems.map(menu => 
        menu.enable ? <Menu.Item key={menu.id} className="my-menu-item">
            <Link to={menu.link} target={menu.target}>{menu.name}</Link>
        </Menu.Item> : ''
    ) : []
}


export function Logo(props) {
    const { linkTo, img } = props
    return (
        <div className="logo left txt-left">
            <Link to={linkTo}><img alt="logo" src={img} /></Link>
        </div>
    )
}

export function MenuLeft(props) {
    const { activeKey, onClick, menuItems } = props;
    return (
        <div className="menu left">
            <Menu
                className="my-menu"
                onClick={onClick}
                selectedKeys={[activeKey]}
                mode="horizontal"
            >
                {renderMenuItems(menuItems)}
            </Menu>
        </div>
    )
}

export function MenuRight(props) {
    const { activeKey, onClick, settingMenus, user } = props;
    const userMenu = (
        <Menu onClick={onClick}>
            <Menu.Item key="ucenter">
                <a href={UIC_URL_TARGET}>用户中心</a>
            </Menu.Item>
            <Menu.Item key="logout">
                退出登录
            </Menu.Item>
        </Menu>
    )
    return (
        <div className="menu right">
            <menu className="menu-right">
                <span>
                    <Icon type="home" />
                </span>
                <span className="divide"></span>
                <span>
                    <Badge dot>
                        <Icon type="message" />
                    </Badge>
                </span>
                <span>
                    <Icon type="setting" />
                </span>
                <div className="user-info">
                    <Dropdown overlay={userMenu} trigger={['click']}>
                        <span>
                            <img className="avatar" />
                            {user.userName || '未登录'}
                        </span>
                    </Dropdown>
                </div>
            </menu>
        </div>
    )
}

@pureRender
export class Navigator extends Component {

    constructor(props) {
        super(props)
        this.state = {
            current: '',
        }
    }

    componentDidMount() {
        this.updateSelected()
    }

    componentWillReceiveProps() {
        this.updateSelected()
    }

    handleClick = (e) => {
        const props = e.item.props
        const { onMenuClick } = this.props
        this.setState({ current: e.key });
        if (onMenuClick) onMenuClick(e)
    }

    clickUserMenu = (obj) => {
        if (obj.key === 'logout') {
            UserApi.logout();
        }
    }

    updateSelected = () => {
        const menuItems = this.props.menuItems
        let pathname = this.props.router.location.pathname
        if (menuItems && menuItems.length > 0) {
            const pathFund = menuItems.find(item => {
                return pathname.indexOf(item.id) > -1
            });
            if (pathFund) {
                this.setState({
                    current: pathFund.id
                })
            } else {
                this.setState({
                    current: menuItems[0].id
                })
            }
        }
    }

    render() {
        const { 
            user, logo, menuItems, 
            settingMenus, 
            menuLeft, menuRight 
        } = this.props;
        const { current } = this.state
        return (
            <header className="header">
                { logo }
                {
                    menuLeft ? menuLeft : <MenuLeft 
                        activeKey={ current }
                        menuItems={menuItems}
                        onClick={this.handleClick} 
                    /> 
                }
                {
                     menuRight ? menuRight : <MenuRight 
                        activeKey={ current } 
                        user={user}
                        onClick={this.clickUserMenu}
                        settingMenus={ settingMenus }
                    /> 
                }
            </header>
        )
    }
}

