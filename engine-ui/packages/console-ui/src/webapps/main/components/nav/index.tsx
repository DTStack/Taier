import * as React from 'react'
import { Menu, Dropdown, Icon } from 'antd'
import { Link } from 'react-router'
import styled from 'styled-components'
import { cloneDeep } from 'lodash';
import pureRender from 'utils/pureRender'
import UserApi from '../../api/user'
import { MY_APPS } from '../../consts';
import 'public/dtinsightFont/iconfont.css'
import './style.scss'

const SubMenu = Menu.SubMenu;
declare var window: any;
declare var APP_CONF: any;

const UIC_URL_TARGET = APP_CONF.UIC_URL || '';

export const Title = styled.span`
    color: #ffffff;
    margin-left: 10px;
    font-size: 14px;
`
export const MyIcon = styled.span`
    font-size: 18px;
`

// 比较apps和licenseApps,控制显示主页导航栏菜单、右下拉菜单以及首页应用模块
export function compareEnableApp (apps: any, licenseApps: any, isShowHome?: any) {
    let validateAppIds = [MY_APPS.RDOS, MY_APPS.STREAM, MY_APPS.DATA_QUALITY,
        MY_APPS.API, MY_APPS.ANALYTICS_ENGINE, MY_APPS.SCIENCE]; // license控制的appID
    if (licenseApps && licenseApps.length) {
        const newApps = cloneDeep(apps);
        const licenseAppIds = licenseApps.map((item: any) => item.id) || [];
        const notConigAppIds = validateAppIds.filter(id => licenseAppIds.indexOf(id) === -1) || []; // 未返回的license Id
        // 将license控制的app中，将接口未返回的配置app, enable置为false
        for (let i = 0; i < newApps.length; i++) {
            for (let j = 0; j < notConigAppIds.length; j++) {
                if (newApps[i].id == notConigAppIds[j]) {
                    newApps[i].enable = false
                }
            }
        }
        // 将license控制的app中，将接口返回的已配置app, enable置为isShow
        for (let i = 0; i < newApps.length; i++) {
            for (let j = 0; j < licenseApps.length; j++) {
                if (newApps[i].id == licenseApps[j].id) {
                    newApps[i].enable = licenseApps[j].isShow
                }
            }
        }
        return newApps
    } else {
        if (isShowHome) { // 主页导航栏以及右拉菜单显示首页
            const mainApp = apps.find((item: any) => {
                return item.id == MY_APPS.MAIN
            })
            return [mainApp]
        } else { // 不显示首页应用模块
            return []
        }
    }
}

/**
 * 渲染各个应用的菜单导航项
 * @param menuItems 菜单项列表
 */
function renderMenuItem (menuItems: any, isRoot?: boolean, isRenderIcon = false) {
    return menuItems && menuItems.length > 0 ? menuItems.map((menu: any) => {
        const isShow = menu.enable && (!menu.needRoot || (menu.needRoot && isRoot))
        return isShow ? (<Menu.Item key={menu.id} className={menu.menuClass}>
            <a href={menu.link} target={menu.target} className="dropdown-content">
                {(isRenderIcon || menu.enableIcon) && <span className={`iconfont icon-${menu.className || ''}`}></span>}
                {menu.name}
            </a>
        </Menu.Item>) : ''
    }) : []
}
function renderATagMenuItems (menuItems: any, isRoot?: boolean, isRenderIcon = false, subMenuList?: any) {
    let subMenuItems = subMenuList || [];
    let Menu = renderMenuItem(menuItems, isRoot, isRenderIcon);
    let subMenu = renderMenuItem(subMenuItems, isRoot, isRenderIcon);
    let isShowSubMenu = subMenuItems && (subMenuItems.findIndex((item: any) => item.enable && (!item.needRoot || (item.needRoot && isRoot))) > -1)
    return (
        [ Menu,
            // eslint-disable-next-line react/jsx-key
            isShowSubMenu && (<SubMenu
                className="my-menu-item menu_mini"
                title={(
                    <span
                        style={{
                            height: '47px'
                        }}
                        className="my-menu-item"
                    >
                        <span
                            className="menu-text-ellipsis"
                        >
                            其他
                        </span>&nbsp;
                        <Icon type="caret-down" />
                    </span>
                )}
            >
                { subMenu }
            </SubMenu>)]
    )
}
export function Logo (props: any) {
    const { linkTo, img } = props
    return (
        <Link to={linkTo}><img alt="logo" src={img} /></Link>
    )
}

export function MenuLeft (props: any) {
    const { activeKey, onClick, menuItems, subMenuItems, user, customItems = [], selectProjectsubMenu } = props;
    return (
        <div className="menu left">
            <Menu
                className="my-menu"
                onClick={onClick}
                selectedKeys={[activeKey]}
                mode="horizontal"
            >
                {selectProjectsubMenu}
                {customItems.concat(renderATagMenuItems(menuItems, user.isRoot, false, subMenuItems))}
            </Menu>
        </div>
    )
}

export function MenuRight (props: any) {
    const {
        onClick, settingMenus, user, licenseApps = [],
        apps, app, showHelpSite, helpUrl
    } = props;
    const isShowExt = !app || (!app.disableExt && !app.disableMessage);
    const isShowAla = !app || !app.disableMessage;
    const isLogin = user && user.userName;
    const extraParms = app ? `?app=${app && app.id}` : '';
    const hasShowApp = licenseApps.some((licapp: any) => licapp.isShow == true)
    const isLeastOneLicAppShow = licenseApps && licenseApps.length > 0 && hasShowApp; // 至少有一个licenseApp，isShow为true才显示用户管理和角色管理
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
            {renderATagMenuItems(compareEnableApp(apps, licenseApps, true) || apps, user.isRoot, true)}
        </Menu>
    )

    return (
        <div className="menu right" style={{ height: '50px' }}>
            <menu className="menu-right">
                {showHelpSite && !window.APP_CONF.disableHelp ? (
                    <span title="帮助文档" className="menu-item">
                        <a href={helpUrl} target="blank" style={{ color: '#ffffff' }} >
                            <Icon type="question-circle-o" />
                        </a>

                    </span>
                ) : null

                }
                <Dropdown overlay={appMenus} trigger={['click']} getPopupContainer={(triggerNode: any) => triggerNode.parentNode}>
                    <span className="menu-item">
                        <Icon type="home" />
                    </span>
                </Dropdown>
                <span className="divide"></span>
                {isShowExt && isLeastOneLicAppShow && <a href={`/message${extraParms}`} target="blank" style={{ color: '#ffffff' }}>
                    <span className="menu-item">
                        <Icon type="message" />
                    </span>
                </a>}
                {(isShowExt || !isShowAla) && isLeastOneLicAppShow && <Dropdown overlay={settingMenuItems} trigger={['click']} getPopupContainer={(triggerNode: any) => triggerNode.parentNode}>
                    <span className="menu-item">
                        <Icon type="setting" />
                    </span>
                </Dropdown>}
                <Dropdown overlay={userMenu} trigger={['click']} getPopupContainer={(triggerNode: any) => triggerNode.parentNode}>
                    <div className="user-info">
                        <div className="user-name" title={user && user.userName}>
                            {(user && user.userName) || '未登录'}
                        </div>
                    </div>
                </Dropdown>
            </menu>
        </div>
    )
}

@pureRender
class Navigator extends React.Component<any, any> {
    constructor (props: any) {
        super(props)
        this.state = {
            current: ''
        }
    }

    componentDidMount () {
        this.updateSelected()
    }

    componentDidUpdate (prevProps: any) {
        if (this.props.routing) {
            if (this.props.routing.locationBeforeTransitions.pathname != prevProps.routing.locationBeforeTransitions.pathname) {
                this.updateSelected();
            }
        }
    }

    handleClick = (e: any) => {
        const { onMenuClick } = this.props
        this.setState({ current: e.key });
        if (onMenuClick) onMenuClick(e)
    }

    clickUserMenu = (obj: any) => {
        const { app } = this.props;
        if (obj.key === 'logout') {
            UserApi.logout(app && app.id);
        }
    }

    updateSelected = () => {
        const menuItems = this.props.menuItems
        let pathname = `${window.location.pathname}${window.location.hash}`;
        if (menuItems && menuItems.length > 0) {
            const pathFund = menuItems.find((item: any) => {
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
            user, logo, menuItems, subMenuItems,
            settingMenus, apps, app, licenseApps,
            menuLeft, menuRight, logoWidth, showHelpSite, helpUrl, customItems
        } = this.props;
        const { current } = this.state
        const theme = window.APP_CONF.theme;
        console.log(this.props)
        return (
            <header className={`header ${theme || 'default'}`}>
                <div style={{ width: logoWidth }} className="logo left txt-left">
                    {logo}
                </div>
                {
                    menuLeft || <MenuLeft
                        selectProjectsubMenu={this.props.selectProjectsubMenu}
                        user={user}
                        activeKey={current}
                        customItems={customItems}
                        menuItems={menuItems}
                        subMenuItems={subMenuItems}
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
