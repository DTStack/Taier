import * as React from 'react'
import { connect } from 'react-redux'
import { Menu, Icon, Dropdown, Input } from 'antd';
import Navigator from 'main/components/nav';
import { getHeaderLogo } from 'main/consts';

import docPath from '../../consts/docPath';

const NOT_SHOW_KEY = 'projectList'; // projectList不显示导航
const SubMenu = Menu.SubMenu;
const Search = Input.Search;
declare var window: any;
@(connect((state: any) => {
    return {
        user: state.user,
        apps: state.apps,
        routing: state.routing,
        common: state.common,
        app: state.app,
        licenseApps: state.licenseApps
    }
}) as any)
class Header extends React.Component<any, any> {
    constructor (props: any) {
        super(props)
        this.state = {
            current: 'overview',
            filter: ''
        }
    }
    componentDidMount () {
        this.updateSelected();
    }

    // 控制项目下拉菜单的显示
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps () {
        this.updateSelected();
    }
    updateSelected () {
        let pathname = this.props.router.location.pathname;
        const routes = pathname ? pathname.split('/') : [];
        let path =
            routes.length > 0 && routes[1] !== '' ? routes[2] : NOT_SHOW_KEY;
        // if (
        //     path &&
        //     (path.indexOf('task') > -1 || path.indexOf('offline') > -1 || path.indexOf('realtime') > -1)
        // ) {
        //     this.setState({
        //         devPath: pathname
        //     });
        //     path = 'realtime'
        // }
        if (path !== this.state.current) {
            this.setState({
                current: path
            });
        }
        return path;
    }
    fixArrayIndex = (arr: any) => {
        let fixArrChildrenApps: any = [];
        let showList: any = {
            overview: false,
            market: false,
            mine: false,
            manage: false,
            approval: false,
            dataSource: false,
            projectManage: false
        }
        const menuList = this.props.common.menuList;
        if (menuList) {
            for (let i in menuList) {
                let item = menuList[i];
                if (item.indexOf('overview') > -1) {
                    showList.overview = true;
                } else if (item.indexOf('market') > -1) {
                    showList.market = true
                } else if (item.indexOf('myapi') > -1) {
                    showList.mine = true
                } else if (item.indexOf('manager') > -1) {
                    showList.manage = true
                } else if (item.indexOf('authorized') > -1) {
                    showList.approval = true
                } else if (item.indexOf('datasource') > -1) {
                    showList.dataSource = true
                } else if (item.indexOf('project') > -1) {
                    showList.projectManage = true
                }
            }
        }
        if (arr && arr.length) {
            arr.map((item: any) => {
                switch (item.name) {
                    case '概览':
                        fixArrChildrenApps[0] = showList.overview ? item : null;
                        break;
                    case 'API市场':
                        fixArrChildrenApps[1] = showList.market ? item : null;
                        break;
                    case '我的API':
                        fixArrChildrenApps[2] = showList.mine ? item : null;
                        break;
                    case 'API管理':
                        fixArrChildrenApps[3] = showList.manage ? item : null;
                        break;
                    case '授权与安全':
                        fixArrChildrenApps[4] = showList.approval ? item : null;
                        break;
                    case '数据源管理':
                        fixArrChildrenApps[5] = showList.dataSource ? item : null;
                        break;
                    case '项目管理':
                        fixArrChildrenApps[6] = showList.projectManage ? item : null;
                        break;
                }
            })
        }
        return fixArrChildrenApps;
    }

    selectedProject = (evt: any) => {
        // const { router, dispatch } = this.props;
        const projectId = evt.key;
        if (projectId) {
        }
    }

    searchProject = (value: any) => {
        this.setState({
            filter: value || ''
        })
    }
    getProjectItems () {
        const { projects = [] } = this.props;
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
                style={{ border: '1px solid red', width: '200px', height: '200px' }}
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

    goIndex = () => {
        const { router } = this.props;
        this.setState({ current: NOT_SHOW_KEY });
        router.push('/');
    };

    render () {
        const baseUrl = '/dataApi.html#/api'
        const { current } = this.state;
        const { app, licenseApps, user, project } = this.props;
        const fixArrChildrenApps = this.fixArrayIndex(licenseApps[4] && licenseApps[4].children);
        const overviewNav = fixArrChildrenApps[0];
        const marketNav = fixArrChildrenApps[1];
        const mineNav = fixArrChildrenApps[2];
        const manaNav = fixArrChildrenApps[3];
        const approvalNav = fixArrChildrenApps[4];
        const dataSourceNav = fixArrChildrenApps[5];
        const projectManaNav = fixArrChildrenApps[6];
        const display: boolean = current !== NOT_SHOW_KEY;
        const pid = project && project.id ? project.id : '';
        const menuItems: any = display && [{
            id: 'overview',
            name: '概览',
            link: `${baseUrl}/overview`,
            enable: overviewNav && overviewNav.isShow
        }, {
            id: 'market',
            name: 'API市场',
            link: `${baseUrl}/market`,
            enable: marketNav && marketNav.isShow
        }, {
            id: 'mine',
            name: '我的API',
            link: `${baseUrl}/mine`,
            enable: mineNav && mineNav.isShow
        }, {
            id: 'manage',
            name: 'API管理',
            link: `${baseUrl}/manage`,
            enable: manaNav && manaNav.isShow
        }, {
            id: 'approvalAndsecurity',
            name: '授权与安全',
            link: `${baseUrl}/approvalAndsecurity`,
            enable: approvalNav && approvalNav.isShow
        }, {
            id: 'dataSource',
            name: '数据源管理',
            link: `${baseUrl}/dataSource`,
            enable: dataSourceNav && dataSourceNav.isShow
        }, {
            id: 'project',
            name: '项目管理',
            link: `${baseUrl}/project/${pid}/config`,
            enable: true
        }];
        const settingMenus: any = [{
            id: 'admin/audit',
            name: '安全审计',
            link: `/admin/audit?app=dataApi`,
            enable: user.isRoot,
            enableIcon: true,
            className: 'safeaudit'
        }];
        const logo = <div onClick={this.goIndex} style={{ cursor: 'pointer' }}>
            <img
                className='c-header__logo c-header__logo--api'
                alt="logo"
                src={getHeaderLogo(app.id)}
            />
            <span className='c-header__title c-header__title--api'>
                {window.APP_CONF.prefix ? `${window.APP_CONF.prefix}.` : ''}{window.APP_CONF.name}
            </span>
        </div>;
        return (
            <Navigator
                selectProjectsubMenu={display && this.renderProjectSelect()}
                logo={logo}
                menuItems={menuItems}
                licenseApps={licenseApps}
                settingMenus={settingMenus}
                {...this.props}
                showHelpSite={true}
                helpUrl={docPath.INDEX}
            />
        )
    }
}
export default Header
