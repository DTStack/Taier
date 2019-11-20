import * as React from 'react'
import { connect } from 'react-redux'
import { Menu, Icon, Dropdown, Input } from 'antd';
import Navigator from 'main/components/nav';
import { getHeaderLogo } from 'main/consts';
import { API_ROUTER } from '../../consts';
import * as ProjectAction from '../../actions/project';
import docPath from '../../consts/docPath';
import utils from 'utils';

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
            myapi: false,
            /* eslint-disable-next-line */
            api_manager: false,
            authorized: false,
            datasource: false,
            project: false
        }
        const menuList = this.props.common.menuList;
        if (menuList) {
            const permissionMap = ['overview', 'market', 'myapi',
                'api_manager', 'authorized', 'datasource', 'project'
            ]
            for (let i in menuList) {
                let item = menuList[i];
                for (let j = 0; j <= permissionMap.length; j++) {
                    const navName = permissionMap[j];
                    if (item.indexOf(navName) > -1) showList[navName] = true
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
                        fixArrChildrenApps[2] = showList.myapi ? item : null;
                        break;
                    case 'API管理':
                        fixArrChildrenApps[3] = showList.api_manager ? item : null;
                        break;
                    case '授权与安全':
                        fixArrChildrenApps[4] = showList.authorized ? item : null;
                        break;
                    case '数据源管理':
                        fixArrChildrenApps[5] = showList.datasource ? item : null;
                        break;
                    case '项目管理':
                        fixArrChildrenApps[6] = showList.project ? item : null;
                        break;
                }
            })
        }
        return fixArrChildrenApps;
    }

    selectedProject = (evt: any) => {
        const { dispatch } = this.props;
        const projectId = evt.key;
        if (projectId) {
            const switchProject = () => {
                dispatch(ProjectAction.getProject(projectId));
                this.searchProject();
            }
            switchProject();
        }
    }

    searchProject = (value?: any) => {
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
                            {utils.textOverflowExchange(project.projectAlias || project.projectName, 24)}
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
                style={{ width: '200px', height: '200px' }}
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
            link: `${baseUrl}/${API_ROUTER.OVERVIEW}`,
            enable: overviewNav && overviewNav.isShow
        }, {
            id: 'market',
            name: 'API市场',
            link: `${baseUrl}/${API_ROUTER.MARKET}`,
            enable: marketNav && marketNav.isShow
        }, {
            id: 'mine',
            name: '我的API',
            link: `${baseUrl}/${API_ROUTER.MINE}`,
            enable: mineNav && mineNav.isShow
        }, {
            id: 'manage',
            name: 'API管理',
            link: `${baseUrl}/${API_ROUTER.MANAGE}`,
            enable: manaNav && manaNav.isShow
        }, {
            id: 'approvalAndsecurity',
            name: '授权与安全',
            link: `${baseUrl}/${API_ROUTER.APPROVAL}`,
            enable: approvalNav && approvalNav.isShow
        }, {
            id: 'dataSource',
            name: '数据源管理',
            link: `${baseUrl}/${API_ROUTER.DATASOURCE}`,
            enable: dataSourceNav && dataSourceNav.isShow
        }, {
            id: 'project',
            name: '项目管理',
            link: `${baseUrl}/${API_ROUTER.PROJECT}/${pid}/config`,
            enable: projectManaNav && projectManaNav.isShow
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
