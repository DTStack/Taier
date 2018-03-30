import React, { Component } from 'react'
import { Menu, Dropdown, Icon, Badge } from 'antd'
import { Link } from 'react-router'
import styled from 'styled-components'

import pureRender from 'utils/pureRender'
import MIcon from '../icon'
import UserApi from '../../api/user'
import './style.scss'

const UIC_URL_TARGET = APP_CONF.UIC_URL || '';
const SubMenu = Menu.SubMenu

export const Title = styled.span`
    color: #ffffff;
    margin-left: 10px;
    font-size: 14px;
`
export const MyIcon = styled.span`
    font-size: 18px;
`

function renderMenuItems(menuItems) {
    return menuItems && menuItems.length > 0 ? menuItems.map(menu => 
        menu.enable ? <Menu.Item key={menu.id}>
            <Link to={menu.link} target={menu.target}>{menu.name}</Link>
        </Menu.Item> : ''
    ) : []
}

function renderATagMenuItems(menuItems) {
    return menuItems && menuItems.length > 0 ? menuItems.map(menu => 
        menu.enable ? <Menu.Item key={menu.id}>
            <a href={menu.link} target={menu.target}>{menu.name}</a>
        </Menu.Item> : ''
    ) : []
}

export function Logo(props) {
    const { linkTo, img } = props
    return (
        <Link to={linkTo}><img alt="logo" src={img} /></Link>
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
    const { activeKey, onClick, settingMenus, user, apps, app } = props;
    const extraParms = app ? `?app=${app && app.id}` : '';

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

    const settingMenuItems = (
        <Menu>
            <Menu.Item key="setting:1">
                <a href={`/admin/user${extraParms}`} target="blank">用户管理</a>
            </Menu.Item>
            <Menu.Item key="setting:2">
                <a href={`/admin/role${extraParms}`} target="blank">角色管理</a>
            </Menu.Item>
            {renderMenuItems(settingMenus)}
        </Menu>
    )

    const appMenus = (
        <Menu>
            {renderATagMenuItems(apps)}
        </Menu>
    )


    return (
        <div className="menu right">
            <menu className="menu-right">
                <Dropdown overlay={appMenus} trigger={['click']}>
                    <span>
                        <Icon type="home" />
                        {/* <MIcon type="home" /> */}
                    </span>
                </Dropdown>
                <span className="divide"></span>
                <span>
                    <a href={`/message${extraParms}`} target="blank" style={{color: '#ffffff'}}>
                        <Icon type="message" />
                        {/* <Badge dot>
                        </Badge> */}
                    </a>
                </span>
                <Dropdown overlay={settingMenuItems} trigger={['click']}>
                    <span><Icon type="setting" /> </span>
                </Dropdown>
                <Dropdown overlay={userMenu} trigger={['click']}>
                    <div className="user-info">
                        <Icon  className="avatar" type="user" />
                        <span className="user-name">
                            { (user && user.userName) || '未登录'}
                        </span>
                    </div>
                </Dropdown>
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

    componentWillReceiveProps(nextProps) {
        if (this.props.routing !== nextProps.routing) {
            this.updateSelected()
        }
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
        let pathname = window.location.href
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
            settingMenus, apps, app,
            menuLeft, menuRight 
        } = this.props;
        const { current } = this.state
        return (
            <header className="header">
                <div className="logo left txt-left">
                    { logo }
                </div>
                {
                    menuLeft ? menuLeft : <MenuLeft 
                        activeKey={ current }
                        menuItems={ menuItems }
                        onClick={this.handleClick} 
                    /> 
                }
                {
                     menuRight ? menuRight : <MenuRight 
                        activeKey={ current } 
                        user={ user }
                        app={ app }
                        apps={ apps }
                        onClick={ this.clickUserMenu }
                        settingMenus={ settingMenus }
                    /> 
                }
            </header>
        )
    }
}

