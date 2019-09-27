import * as React from 'react'
import { Menu, Dropdown } from 'antd'
import { Link } from 'react-router'
import styled from 'styled-components'
import pureRender from 'utils/pureRender'
import UserApi from '../../api/user'
import 'public/dtinsightFont/iconfont.css'
import './style.scss'

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

function renderATagMenuItems (menuItems: any, isRoot?: boolean, isRenderIcon = false) {
    return menuItems && menuItems.length > 0 ? menuItems.map((menu: any) => {
        const isShow = menu.enable && (!menu.needRoot || (menu.needRoot && isRoot))
        return isShow ? (<Menu.Item key={menu.id}>
            <a href={menu.link} target={menu.target} className="dropdown-content">
                {(isRenderIcon || menu.enableIcon) && <span className={`iconfont icon-${menu.className || ''}`}></span>}
                {menu.name}
            </a>
        </Menu.Item>) : ''
    }) : []
}
export function Logo (props: any) {
    const { linkTo, img } = props
    return (
        <Link to={linkTo}><img alt="logo" src={img} /></Link>
    )
}

export function MenuLeft (props: any) {
    const { activeKey, onClick, menuItems, user, customItems = [] } = props;
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

export function MenuRight (props: any) {
    const {
        onClick, settingMenus, user, licenseApps = [],
        app, showHelpSite, helpUrl
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

    return (
        <div className="menu right" style={{ height: '60px' }}>
            <menu className="menu-right" style={{ paddingRight: '30px' }}>
                {showHelpSite && !window.APP_CONF.disableHelp ? (
                    <span title="帮助文档" className="menu-item">
                        <a href={helpUrl} target="blank" style={{ color: '#ffffff' }} >
                            <img src="/public/main/img/icon_help.svg" alt="帮助图标" style={{ height: '16px' }}/>
                        </a>
                    </span>
                ) : null}
                {isShowExt && isLeastOneLicAppShow && <a href={`/message${extraParms}`} target="blank" style={{ color: '#ffffff' }}>
                    <span className="menu-item">
                        <img src="/public/main/img/icon_message.svg" alt="消息图标"/>
                    </span>
                </a>}
                {(isShowExt || !isShowAla) && isLeastOneLicAppShow && <Dropdown overlay={settingMenuItems} trigger={['click']} getPopupContainer={(triggerNode: any) => triggerNode.parentNode}>
                    <span className="menu-item">
                        <img src="/public/main/img/icon_set.svg" alt="设置图标"/>
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
        if (obj.key === 'logout') {
            UserApi.logout();
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
            user, logo,
            settingMenus, apps, app, licenseApps,
            menuRight, logoWidth, showHelpSite, helpUrl
        } = this.props;
        const { current } = this.state
        const theme = window.APP_CONF.theme;
        return (
            <header className={`newheader ${theme || 'default'}`} style={{ background: 'none' }}>
                <div style={{ width: logoWidth, paddingLeft: 0, display: 'flex', alignItems: 'center' }} className="logo left txt-left">
                    {logo}
                </div>
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
