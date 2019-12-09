import * as React from 'react';
import { connect } from 'react-redux';
import { Menu, Icon, Dropdown, Input } from 'antd';
import { MenuRight } from 'main/components/nav';
import { getHeaderLogo } from 'main/consts';
import { Link } from 'react-router';
import Api from '../../api';
import UserAPI from 'main/api/user';
import { PROJECT_TYPE } from '../../comm/const';
import * as ProjectAction from '../../reducers/modules/project';

declare var window: any;
declare var APP_CONF: any;
/* eslint-disable-next-line */
const UIC_URL_TARGET = APP_CONF.UIC_URL || "";

const SubMenu = Menu.SubMenu;
const Search = Input.Search;

@(connect((state: any) => {
    return {
        licenseApps: state.licenseApps
    }
}) as any)
class Header extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            current: '/entityManage',
            filter: '',
            navItem: [],
            allChilOfNav: []
        };
    }

    // 控制项目下拉菜单的显示
    // eslint-disable-next-line
    componentDidUpdate (preProps) {
        const { navData, licenseApps, location } = this.props;
        const pathName = location.pathname;
        if (licenseApps != preProps.licenseApps) {
            const currentlicenseApps = licenseApps.find(item => item.id == 'tagEngine');
            if (currentlicenseApps) {
                const { children } = currentlicenseApps;
                let navItem = navData.filter(item => children.some(ele => (ele.name == item.permissionName) && ele.isShow));
                if (navItem.length) {
                    const currentRouter = navItem.filter(item => item.routers.includes(pathName));
                    let current = '';
                    let allChilOfNav = navItem.reduce((pre, curr) => {
                        if (curr.children) {
                            return [ ...pre, ...curr.children ];
                        } else {
                            return pre;
                        }
                    }, []);
                    if (currentRouter[0]) {
                        current = currentRouter[0].permissionUrl
                    } else {
                        current = navItem[0] ? navItem[0].permissionUrl : '';
                    }
                    this.setState({
                        navItem,
                        current,
                        allChilOfNav
                    });
                }
            } else {
                this.goIndex();
            }
        }
    }
    handleClick = (e: any) => {
        this.setState({ current: e.key });
    };

    selectedProject = (evt: any) => {
        const { dispatch, location: { pathname }, router } = this.props;
        const { allChilOfNav } = this.state;
        const projectId = evt.key;
        let currentPartObj = allChilOfNav.find(item => item.routers.includes(pathname));
        if (currentPartObj) {
            router.push(currentPartObj.permissionUrl);
        }
        if (projectId) {
            dispatch(ProjectAction.getProject(projectId));
            this.searchProject('');
        }
    }
    searchProject = (value: any) => {
        this.setState({
            filter: value || ''
        })
    }

    clickUserMenu = (obj: any) => {
        if (obj.key === 'logout') {
            Api.logout({}).then((res: any) => {
                if (res.code === 1) {
                    UserAPI.openLogin();
                }
            }); ;
        }
    };

    goIndex = () => {
        const { router } = this.props;
        const { navItem } = this.state;
        let current = navItem[0] ? navItem[0].permissionUrl : '';
        this.setState({ current });
        router.push('/');
    };

    getProjectItems () {
        const projects = this.props.projects;
        const { filter } = this.state;
        if (projects && projects.length > 0) {
            return projects
                .filter((o: any) => o.projectIdentifier.indexOf(filter) > -1 || o.projectName.indexOf(filter) > -1 || o.projectAlias.indexOf(filter) > -1)
                .map((project: any) => {
                    const name = project.projectAlias || project.projectName;
                    return (
                        <Menu.Item
                            data={project}
                            title={name}
                            value={name}
                            key={project.id}
                        >
                            {project.projectAlias || project.projectName}
                        </Menu.Item>
                    );
                });
        }
        return [];
    }
    updateSelected (pathname) {
        this.setState({
            current: pathname
        });
    }

    initUserDropMenu = () => {
        return (
            <Menu onClick={this.clickUserMenu}>
                {!window.APP_CONF.hideUserCenter && (
                    <Menu.Item key="ucenter">
                        <Link to={UIC_URL_TARGET}>用户中心</Link>
                    </Menu.Item>
                )}
                <Menu.Item key="logout">退出登录</Menu.Item>
            </Menu>
        );
    };

    renderProjectSelect = () => {
        const { project } = this.props;
        const { filter } = this.state;
        const projectName =
            project && project.projectName
                ? project.projectAlias || project.projectName
                : '项目选择';
        const menu = (
            <Menu
                onClick={this.selectedProject}
                selectedKeys={
                    project ? [`${project.id}`] : []
                }
                style={{
                    maxHeight: '400px',
                    overflowY: 'auto',
                    width: '170px'
                }}
            >
                <Menu.Item disabled>
                    <Search placeholder="请输入项目名称" value={filter} onChange={(e: any) => this.searchProject(e.target.value)} />
                </Menu.Item>
                {this.getProjectItems()}
            </Menu>
        )

        return (
            <SubMenu
                className="my-menu-item"
                title={
                    <Dropdown
                        overlay={menu}
                        trigger={['click']}
                        placement="bottomCenter"
                    >
                        <span
                            style={{
                                display: 'inline-block',
                                height: '47px'
                            }}
                            className="my-menu-item"
                        >
                            <span
                                className="menu-text-ellipsis"
                                title={projectName}
                            >
                                {projectName}
                            </span>
                            &nbsp;
                            <Icon style={{ fontSize: '12px' }} type="caret-down" />
                        </span>
                    </Dropdown>
                }
            >
            </SubMenu>
        );
    };
    renderProjectType () {
        const { project } = this.props;
        switch (project.projectType) {
            case PROJECT_TYPE.TEST: {
                return (
                    <div
                        className="head-project-tip"
                    >
                        <span className="content">
                            <img src="/public/stream/img/icon/develop.svg" />测试
                        </span>
                    </div>
                )
            }
            case PROJECT_TYPE.PRO: {
                return (
                    <div
                        className="head-project-tip"
                    >
                        <span className="content">
                            <img src="/public/stream/img/icon/produce.svg" />生产
                        </span>
                    </div>
                )
            }
            default: {
                return '';
            }
        }
    }
    render () {
        const { user, apps, app, licenseApps, router } = this.props;
        const { current, navItem } = this.state;
        let pathname = router.location.pathname;
        const isProject = pathname == '/';
        return (
            <div className={`header ${window.APP_CONF.theme || 'default'}`}>
                <div onClick={this.goIndex} className="logo left txt-left">
                    <img
                        className='c-header__logo c-header__logo--stream'
                        alt="logo"
                        src={getHeaderLogo(app.id)}
                    />
                    <span className='c-header__title c-header__title--stream'>
                        {window.APP_CONF.prefix ? `${window.APP_CONF.prefix}.` : ''}{window.APP_CONF.name}
                    </span>
                </div>
                <div className="menu left" style={{ position: 'relative' }}>
                    <Menu
                        className={'my-menu'}
                        onClick={this.handleClick}
                        selectedKeys={[current]}
                        mode="horizontal"
                    >
                        {!isProject && this.renderProjectSelect()}
                        {
                            !isProject && navItem.map(item => (
                                <Menu.Item
                                    className="my-menu-item"
                                    key={item.permissionUrl}
                                >
                                    <Link to={item.permissionUrl}>{item.permissionName}</Link>
                                </Menu.Item>
                            ))
                        }
                    </Menu>
                </div>
                <MenuRight
                    user={user}
                    app={app}
                    apps={apps}
                    licenseApps={licenseApps}
                    onClick={this.clickUserMenu}
                    showHelpSite={true}
                    helpUrl="/public/helpSite/stream/v3.0/Summary.html"
                />
            </div>
        );
    }
}
export default Header;
