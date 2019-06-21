import React, { Component } from 'react'
import { Menu, Dropdown, Icon } from 'antd'
import { Link } from 'react-router'
import styled from 'styled-components'
import { cloneDeep } from 'lodash';
import pureRender from 'utils/pureRender'
import UserApi from '../../api/user'
import { MY_APPS } from '../../consts';
import 'public/dtinsightFont/iconfont.css'
import './style.scss'

/* eslint-disable */
const UIC_URL_TARGET = APP_CONF.UIC_URL || '';
/* eslint-disable */

export const Title = styled.span`
    color: #ffffff;
    margin-left: 10px;
    font-size: 14px;
`
export const MyIcon = styled.span`
    font-size: 18px;
`

function renderMenuItems (menuItems) {
    return menuItems && menuItems.length > 0 ? menuItems.map(menu =>
        menu.enable ? <Menu.Item key={menu.id}>
            <Link to={menu.link} target={menu.target} className="dropdown-content">{menu.name}</Link>
        </Menu.Item> : ''
    ) : []
}

// 比较apps和licenseApps,控制显示主页菜单以及右下拉菜单
export function compareEnable (apps, licenseApps) {
    if (licenseApps && licenseApps.length) {
        const newApps = cloneDeep(apps);
        for (let i = 0; i < newApps.length; i++) {
            for (let j = 0; j < licenseApps.length; j++) {
                if (newApps[i].id == licenseApps[j].id) {
                    newApps[i].enable = licenseApps[j].isShow
                }
            }
        }
        return newApps
    }else { // 空数组只显示首页菜单栏
        const mainApp = apps.find(item => {
            return item.id == MY_APPS.MAIN
        })
        return [mainApp]
    }
}

function renderATagMenuItems (menuItems, isRoot, isRenderIcon = false) {
    return menuItems && menuItems.length > 0 ? menuItems.map(menu => {
        const isShow = menu.enable && (!menu.needRoot || (menu.needRoot && isRoot))
        return isShow ? (<Menu.Item key={menu.id}>
            <a href={menu.link} target={menu.target} className="dropdown-content">
                {(isRenderIcon || menu.enableIcon) && <span className={`iconfont icon-${menu.className || ''}`}></span>}
                {menu.name}
            </a>
        </Menu.Item>) : ''
    }) : []
}
export function Logo (props) {
    const { linkTo, img } = props
    return (
        <Link to={linkTo}><img alt="logo" src={img} /></Link>
    )
}

export function MenuLeft (props) {
    const { activeKey, onClick, menuItems, user, licenseApps, customItems = [] } = props;
    return (
        <div className="menu left">
            <Menu
                className="my-menu"
                onClick={onClick}
                selectedKeys={[activeKey]}
                mode="horizontal"
            >   
                {customItems.concat(renderATagMenuItems(menuItems, user.isRoot))}
            </Menu>
        </div>
    )
}

export function MenuRight (props) {
    const {
        onClick, settingMenus, user, licenseApps,
        apps, app, showHelpSite, helpUrl
    } = props;
    const isShowExt = !app || (!app.disableExt && !app.disableMessage);
    const isShowAla = !app || !app.disableMessage;
    const isLogin = user && user.userName;
    const extraParms = app ? `?app=${app && app.id}` : '';
    const userMenu = (
        <Menu onClick={onClick}>
            {!window.APP_CONF.hideUserCenter && (
                <Menu.Item key="ucenter">
                    <a href={UIC_URL_TARGET} className="dropdown-content">
                        <span className='iconfont icon-icon_uic'></span>
                        用户中心
                    </a>
                </Menu.Item>
            )}
            <Menu.Item key="logout">
                <a className="dropdown-content" href="javascript:void(0)">
                    <span className='iconfont icon-icon_logout'></span>
                    { isLogin ? '退出登录' : '去登录' }
                </a>
            </Menu.Item>
        </Menu>
    )
    const settingMenuItems = (
        <Menu>
            <Menu.Item key="setting:1">
                <a href={`/admin/user${extraParms}`} target="blank" className="dropdown-content">
                    <span className='iconfont icon-icon_usermanagement'></span>
                    用户管理
                </a>
            </Menu.Item>
            <Menu.Item key="setting:2">
                <a href={`/admin/role${extraParms}`} target="blank" className="dropdown-content">
                    <span className='iconfont icon-role_usermanagement'></span>
                    角色管理
                </a>
            </Menu.Item>
            {renderATagMenuItems(settingMenus)}
        </Menu>
    )
    // 右下拉菜单
    const appMenus = (
        <Menu selectedKeys={[`${app && app.id}`]}>
            {renderATagMenuItems(compareEnable(apps, licenseApps) || apps, user.isRoot, true)}
        </Menu>
    )

    return (
        <div className="menu right">
            <menu className="menu-right">
                {showHelpSite && !window.APP_CONF.disableHelp ? (
                    <span title="帮助文档" className="menu-item">
                        <a href={helpUrl} target="blank" style={{ color: '#ffffff' }} >
                            <Icon type="question-circle-o" />
                        </a>

                    </span>
                ) : null

                }
                <Dropdown overlay={appMenus} trigger={['click']} getPopupContainer={(triggerNode) => triggerNode.parentNode}>
                    <span className="menu-item">
                        <Icon type="home" />
                    </span>
                </Dropdown>
                <span className="divide left"></span>
                {isShowExt && <a href={`/message${extraParms}`} target="blank" style={{ color: '#ffffff' }}>
                    <span className="menu-item">
                        <Icon type="message" />
                        {/* <Badge dot>
                        </Badge> */}
                    </span>
                </a>}
                {(isShowExt || !isShowAla) && <Dropdown overlay={settingMenuItems} trigger={['click']} getPopupContainer={(triggerNode) => triggerNode.parentNode}>
                    <span className="menu-item"><Icon type="setting" /> </span>
                </Dropdown>}
                <Dropdown overlay={userMenu} trigger={['click']} getPopupContainer={(triggerNode) => triggerNode.parentNode}>
                    <div className="user-info">
                        {/* <Icon className="avatar" type="user" /> */}
                        <span className="user-name" title={user && user.userName}>
                            {(user && user.userName) || '未登录'}
                        </span>
                    </div>
                </Dropdown>
            </menu>
        </div>
    )
}

@pureRender
class Navigator extends Component {
    constructor(props) {
        super(props)
        this.state = {
            current: ''
        }
    }

    componentDidMount () {
        this.updateSelected()
    }

    componentDidUpdate (prevProps){
        if (this.props.routing) {
            if (this.props.routing.locationBeforeTransitions.pathname != prevProps.routing.locationBeforeTransitions.pathname) {
                this.updateSelected();
            }
        }
    }

    handleClick = (e) => {
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
        let pathname = `${window.location.pathname}${window.location.hash}`;
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

    render () {
        const {
            user, logo, menuItems,
            settingMenus, apps, app, licenseApps,
            menuLeft, menuRight, logoWidth, showHelpSite, helpUrl, customItems
        } = this.props;
        const { current } = this.state
        const theme = window.APP_CONF.theme;
        return (
            <header className={`header ${theme || 'default'}`}>
                <div style={{ width: logoWidth }} className="logo left txt-left">
                    {logo}
                </div>
                {
                    menuLeft || <MenuLeft
                        user={user}
                        activeKey={current}
                        customItems={customItems}
                        menuItems={menuItems}
                        licenseApps={licenseApps}
                        onClick={this.handleClick}
                    />
                }
                {
                    menuRight || <MenuRight
                        activeKey={current}
                        user={user}
                        app={app}
                        apps={apps}
                        licenseApps={licenseApps}
                        onClick={this.clickUserMenu}
                        settingMenus={settingMenus}
                        showHelpSite={showHelpSite}
                        helpUrl={helpUrl}
                    />
                }
            </header>
        )
    }
}

export default Navigator;
