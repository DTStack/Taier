import * as React from 'react';
import { Menu, Dropdown, Icon } from 'antd';
import { Link } from 'react-router';
import styled from 'styled-components';
import { Cookie, Utils } from 'dt-utils';

import UserApi from '../../api/user';
import { compareEnableApp } from '../../funcs';
import TenantModal from '../tenantModal';
import '../../public/dtinsightFont/iconfont.css';
import './style.scss';

const SubMenu = Menu.SubMenu;
declare var window: any;
declare var APP_CONF: any;

const UIC_URL_TARGET = APP_CONF.UIC_URL || '';

export const Title = styled.span`
  color: #ffffff;
  margin-left: 10px;
  font-size: 14px;
`;
export const MyIcon = styled.span`
  font-size: 18px;
`;

/**
 * 渲染各个应用的菜单导航项
 * @param menuItems 菜单项列表
 */
function renderMenuItem(
  menuItems: any,
  isRoot?: boolean,
  isRenderIcon = false
) {
  return menuItems && menuItems.length > 0
    ? menuItems.map((menu: any) => {
        const isShow =
          menu && menu.enable && (!menu.needRoot || (menu.needRoot && isRoot));
        return isShow ? (
          <Menu.Item key={menu.id} className={menu.menuClass}>
            {/* isDiffOrigin 非同源直接使用 a 标签跳转 */}
            {menu?.isDiffOrigin ? (
              <a
                href={menu.link}
                target={menu.target}
                className="dropdown-content">
                {(isRenderIcon || menu.enableIcon) && (
                  <span
                    className={`iconfont icon-${menu.className || ''}`}></span>
                )}
                {menu.name}
              </a>
            ) : (
              <Link
                to={menu.link}
                target={menu.target}
                className="dropdown-content">
                {(isRenderIcon || menu.enableIcon) && (
                  <span
                    className={`iconfont icon-${menu.className || ''}`}></span>
                )}
                {menu.name}
              </Link>
            )}
          </Menu.Item>
        ) : (
          ''
        );
      })
    : [];
}

/**
 * 渲染各个应用的菜单导航项
 * @param menuItems 菜单项列表
 */
function renderATagMenuItem(
  menuItems: any,
  isRoot?: boolean,
  isRenderIcon = false
) {
  return menuItems && menuItems.length > 0
    ? menuItems.map((menu: any) => {
        const isShow =
          menu && menu.enable && (!menu.needRoot || (menu.needRoot && isRoot));
        return isShow ? (
          <Menu.Item key={menu.id} className={menu.menuClass}>
            <a
              href={menu.link}
              target={menu.target}
              className="dropdown-content">
              {(isRenderIcon || menu.enableIcon) && (
                <span
                  className={`iconfont icon-${menu.className || ''}`}></span>
              )}
              {menu.name}
            </a>
          </Menu.Item>
        ) : (
          ''
        );
      })
    : [];
}

function renderMenuItems(
  menuItems: any,
  isRoot?: boolean,
  isRenderIcon = false,
  subMenuList?: any
) {
  let subMenuItems = subMenuList || [];
  let Menu = renderMenuItem(menuItems, isRoot, isRenderIcon);
  let subMenu = renderMenuItem(subMenuItems, isRoot, isRenderIcon);
  let isShowSubMenu =
    subMenuItems &&
    subMenuItems.findIndex(
      (item: any) =>
        item && item.enable && (!item.needRoot || (item.needRoot && isRoot))
    ) > -1;
  return [
    Menu,
    // eslint-disable-next-line react/jsx-key
    isShowSubMenu && (
      <SubMenu
        className="my-menu-item menu_mini"
        title={
          <span
            style={{
              height: '61px',
            }}
            className="my-menu-item">
            <span className="dt-menu-text-ellipsis">其他</span>&nbsp;
            <Icon type="caret-down" />
          </span>
        }>
        {subMenu}
      </SubMenu>
    ),
  ];
}

/**
 * 渲染带 A Tag 的标签
 * @param menuItems
 * @param isRoot
 * @param isRenderIcon
 * @param subMenuList
 */
function renderATagMenuItems(
  menuItems: any,
  isRoot?: boolean,
  isRenderIcon = false,
  subMenuList?: any
) {
  let subMenuItems = subMenuList || [];
  let Menu = renderATagMenuItem(menuItems, isRoot, isRenderIcon);
  let subMenu = renderATagMenuItem(subMenuItems, isRoot, isRenderIcon);
  let isShowSubMenu =
    subMenuItems &&
    subMenuItems.findIndex(
      (item: any) =>
        item && item.enable && (!item.needRoot || (item.needRoot && isRoot))
    ) > -1;
  return [
    Menu,
    // eslint-disable-next-line react/jsx-key
    isShowSubMenu && (
      <SubMenu
        className="my-menu-item menu_mini"
        title={
          <span
            style={{
              height: '61px',
            }}
            className="my-menu-item">
            <span className="dt-menu-text-ellipsis">其他</span>&nbsp;
            <Icon type="caret-down" />
          </span>
        }>
        {subMenu}
      </SubMenu>
    ),
  ];
}

export function Logo(props: any) {
  const { linkTo, img } = props;
  return (
    <Link to={linkTo}>
      <img alt="logo" src={img} />
    </Link>
  );
}

export function MenuLeft(props: any) {
  const {
    activeKey,
    onClick,
    menuItems,
    subMenuItems,
    user,
    customItems = [],
    selectProjectsubMenu,
  } = props;
  return (
    <div className="menu" style={{ flex: 1 }}>
      <Menu
        className="my-menu"
        onClick={onClick}
        selectedKeys={[activeKey]}
        mode="horizontal">
        {selectProjectsubMenu}
        {customItems.concat(
          renderMenuItems(menuItems, user.isRoot, false, subMenuItems)
        )}
      </Menu>
    </div>
  );
}

export class MenuRight extends React.PureComponent<any, any> {
  state = {
    visible: false,
    curTenantName: Cookie.getCookie('dt_tenant_name') || '',
  };
  closeTenantModal = () => {
    this.setState({ visible: false });
  };
  onHandleMenu = (obj: any) => {
    if (obj.key === 'stenant') {
      this.setState({ visible: true });
    } else {
      this.props.onClick(obj);
    }
  };
  render() {
    const {
      settingMenus,
      user,
      licenseApps = [],
      apps,
      app,
      showHelpSite,
      helpUrl,
    } = this.props;
    const { visible, curTenantName } = this.state;
    const isShowExt = !app || (!app.disableExt && !app.disableMessage);
    const isShowAla = !app || !app.disableMessage;
    const isLogin = user && user.userName && Cookie.getCookie('dt_user_id');
    const extraParms = app ? `?app=${app && app.id}` : '';
    const hasShowApp = licenseApps.some((licapp: any) => licapp.isShow == true);
    const isLeastOneLicAppShow =
      licenseApps && licenseApps.length > 0 && hasShowApp; // 至少有一个licenseApp，isShow为true才显示用户管理和角色管理
    const userMenu = (
      <Menu onClick={this.onHandleMenu}>
        {isLogin && (
          <Menu.Item key="stenant">
            <a className="dropdown-content" href="javascript:void(0)">
              <Icon type="swap" className="iconfont" />
              {Utils.textOverflowExchange(curTenantName, 6)}
            </a>
          </Menu.Item>
        )}
        {!window.APP_CONF.hideUserCenter && (
          <Menu.Item key="ucenter">
            <a href={UIC_URL_TARGET} className="dropdown-content">
              <span className="iconfont icon-icon_uic"></span>
              用户中心
            </a>
          </Menu.Item>
        )}
        <Menu.Item key="logout">
          <a className="dropdown-content" href="javascript:void(0)">
            <span className="iconfont icon-icon_logout"></span>
            {isLogin ? '退出登录' : '去登录'}
          </a>
        </Menu.Item>
      </Menu>
    );
    const settingMenuItems = (
      <Menu>
        <Menu.Item key="setting:1">
          <Link
            to={`/admin/user${extraParms}`}
            target="blank"
            className="dropdown-content">
            <span className="iconfont icon-icon_usermanagement"></span>
            用户管理
          </Link>
        </Menu.Item>
        <Menu.Item key="setting:2">
          <Link
            to={`/admin/role${extraParms}`}
            target="blank"
            className="dropdown-content">
            <span className="iconfont icon-role_usermanagement"></span>
            角色管理
          </Link>
        </Menu.Item>
        {renderMenuItems(settingMenus)}
      </Menu>
    );
    // 右下拉菜单
    const appMenus = (
      <Menu selectedKeys={[`${app && app.id}`]}>
        {renderATagMenuItems(
          compareEnableApp(apps, licenseApps, true) || apps,
          user.isRoot,
          true
        )}
      </Menu>
    );

    return (
      <div className="menu" style={{ height: '100%' }}>
        <menu className="menu-right">
          {showHelpSite && !window.APP_CONF.disableHelp ? (
            <span title="帮助文档" className="menu-item">
              <a href={helpUrl} target="blank" style={{ color: '#ffffff' }}>
                <Icon type="question-circle-o" />
              </a>
            </span>
          ) : null}
          <Dropdown
            overlay={appMenus}
            trigger={['click']}
            getPopupContainer={(triggerNode: any) => triggerNode.parentNode}>
            <span className="menu-item">
              <Icon type="home" />
            </span>
          </Dropdown>
          <span className="divide"></span>
          {isShowExt && isLeastOneLicAppShow && (
            <Link
              to={`/message${extraParms}`}
              target="blank"
              style={{ color: '#ffffff' }}>
              <span className="menu-item">
                <Icon type="message" />
              </span>
            </Link>
          )}
          {(isShowExt || !isShowAla) && isLeastOneLicAppShow && (
            <Dropdown
              overlay={settingMenuItems}
              trigger={['click']}
              getPopupContainer={(triggerNode: any) => triggerNode.parentNode}>
              <span className="menu-item">
                <Icon type="setting" />
              </span>
            </Dropdown>
          )}
          <Dropdown
            overlayClassName="user_info_wrapper"
            overlay={userMenu}
            trigger={['click']}
            getPopupContainer={(triggerNode: any) => triggerNode.parentNode}>
            <div className="user-info">
              <div className="user-name" title={user && user.userName}>
                {(user && user.userName) || '未登录'}
              </div>
            </div>
          </Dropdown>
        </menu>
        {isLogin && (
          <TenantModal
            showTanantModal={visible}
            closeTenantModal={this.closeTenantModal}
          />
        )}
      </div>
    );
  }
}

class Navigator extends React.PureComponent<any, any> {
  constructor(props: any) {
    super(props);
    this.state = {
      current: '',
    };
  }

  componentDidMount() {
    this.updateSelected();
  }

  componentDidUpdate(prevProps: any) {
    if (this.props.routing) {
      if (
        this.props.routing.locationBeforeTransitions.pathname !=
        prevProps.routing.locationBeforeTransitions.pathname
      ) {
        this.updateSelected();
      }
    }
  }

  handleClick = (e: any) => {
    const { onMenuClick } = this.props;
    this.setState({ current: e.key });
    if (onMenuClick) onMenuClick(e);
  };

  clickUserMenu = (obj: any) => {
    const { app } = this.props;
    if (obj.key === 'logout') {
      UserApi.logout(app && app.id);
    }
  };
  updateSelected = () => {
    const menuItems = this.props.menuItems;
    let pathname = `${window.location.pathname}${window.location.hash}`;
    if (menuItems && menuItems.length > 0) {
      const pathFund = menuItems.find((item: any) => {
        return pathname.indexOf(item.id) > -1;
      });

      if (pathFund) {
        this.setState({
          current: pathFund.id,
        });
      } else {
        this.setState({
          current: menuItems[0].id,
        });
      }
    }
  };

  render() {
    const {
      user,
      logo,
      menuItems,
      subMenuItems,
      settingMenus,
      apps,
      app,
      licenseApps,
      menuLeft,
      menuRight,
      logoWidth,
      showHelpSite,
      helpUrl,
      customItems,
    } = this.props;
    const { current } = this.state;
    return (
      <header className="dt-layout-header">
        <div style={{ width: logoWidth }} className="logo txt-left">
          {logo}
        </div>
        {menuLeft || (
          <MenuLeft
            selectProjectsubMenu={this.props.selectProjectsubMenu}
            user={user}
            activeKey={current}
            customItems={customItems}
            menuItems={menuItems}
            subMenuItems={subMenuItems}
            licenseApps={licenseApps}
            onClick={this.handleClick}
          />
        )}
        {menuRight || (
          <MenuRight
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
        )}
      </header>
    );
  }
}

export default Navigator;
