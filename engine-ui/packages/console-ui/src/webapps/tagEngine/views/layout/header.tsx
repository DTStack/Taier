import * as React from 'react';
import { connect } from 'react-redux';
import { Menu, Icon, Dropdown, Input } from 'antd';

import { MenuRight } from 'main/components/nav';
import { getHeaderLogo } from 'main/consts';
import { Link } from 'react-router';

import Api from '../../api';
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
})as any)
class Header extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            current: 'project',
            devPath: '/realtime/task',
            filter: ''
        };
    }

    componentDidMount () {
        this.updateSelected();
    }

    // 控制项目下拉菜单的显示
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps () {
        this.updateSelected();
    }

    handleClick = (e: any) => {
        this.setState({ current: e.key });
    };

    selectedProject = (evt: any) => {
        const { router, dispatch } = this.props;
        const projectId = evt.key;
        if (projectId) {
            dispatch(ProjectAction.getProject(projectId));
            // 清理tab数据
            if (this.state.current === 'overview') {
                router.push('/tag/overview');
            }
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
            Api.logout();
        }
    };

    goIndex = () => {
        const { router } = this.props;
        this.setState({ current: 'overview' });
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

    fixArrayIndex = (arr: any) => {
        let fixArrChildrenApps: any = [];
        if (arr && arr.length > 1) {
            arr.map((item: any) => {
                switch (item.name) {
                    case '实体管理':
                        fixArrChildrenApps[0] = item;
                        break;
                    case '标签中心':
                        fixArrChildrenApps[1] = item;
                        break;
                    case '群组分析':
                    default :
                        fixArrChildrenApps[2] = item;

                }
            })
            return fixArrChildrenApps
        } else {
            return []
        }
    }

    updateSelected () {
        let pathname = this.props.router.location.pathname;
        const routes = pathname ? pathname.split('/') : [];
        let path =
            routes.length > 0 && routes[1] !== '' ? routes[1] : 'overview';
        if (
            path &&
            (path.indexOf('task') > -1 || path.indexOf('offline') > -1 || path.indexOf('realtime') > -1)
        ) {
            this.setState({
                devPath: pathname
            });
            path = 'realtime'
        }
        if (path !== this.state.current) {
            this.setState({
                current: path
            });
        }
        return path;
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
        const { user, project, apps, app, licenseApps, router } = this.props;
        const { current } = this.state;
        let pathname = router.location.pathname;

        const display = current !== 'overview' ? 'inline-block' : 'none';

        // const pid = project && project.id ? project.id : '';

        // const basePath = app.link;
        const basePath = 'tagEngine.html#/';

        // const fixArrChildrenApps = this.fixArrayIndex(licenseApps[1] && licenseApps[1].children);
        // const dataSourceNav = fixArrChildrenApps[0];
        // const taskNav = fixArrChildrenApps[1];
        // const operaNav = fixArrChildrenApps[2];
        // const projectNav = fixArrChildrenApps[3];
        // 如果是数据地图模块，隐藏项目下拉选择菜单
        const showProjectSelect =
            !(pathname.indexOf('/data-manage') > -1 || pathname === '/');
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
                        selectedKeys={[this.state.current]}
                        mode="horizontal"
                    >
                        {showProjectSelect && this.renderProjectSelect()}
                        <Menu.Item
                            className="my-menu-item"
                            key="entityManage"
                            style={{ display }}
                        >
                            <Link to={`/tag/entityManage`}>实体管理</Link>
                        </Menu.Item>
                        <Menu.Item
                            className="my-menu-item"
                            key="realtime"
                            style={{ display }}
                        >
                            <Link to={`/tag/labelCenter`}>标签中心</Link>
                        </Menu.Item>
                        <Menu.Item
                            className="my-menu-item"
                            key="operation"
                            style={{ display }}
                        >
                            <Link to={`/groupAnalyse`}>群组分析</Link>
                        </Menu.Item>
                        <Menu.Item
                            className="my-menu-item"
                            key="project"
                            style={{ display }}
                        >
                            <Link to={`/apiMarket`}>
                                API市场
                            </Link>
                        </Menu.Item>
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
