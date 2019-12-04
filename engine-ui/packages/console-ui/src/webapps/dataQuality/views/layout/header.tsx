import * as React from 'react'
import { connect } from 'react-redux'
import { Menu, Icon, Dropdown, Input } from 'antd';
import utils from 'utils';
import Navigator from 'main/components/nav';
import { getHeaderLogo } from 'main/consts';
import * as ProjectAction from '../../actions/project';
const NOT_SHOW_KEY = 'projectList'; // projectList不显示导航
const SubMenu = Menu.SubMenu;
const Search = Input.Search;
declare var window: any;

@(connect((state: any) => {
    return {
        user: state.user,
        apps: state.apps,
        routing: state.routing,
        app: state.app,
        licenseApps: state.licenseApps
    };
}, (dispatch: any) => {
    return {
        setProject (project: any) {
            return dispatch(ProjectAction.setProject(project))
        }
    }
}) as any)
class Header extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            current: 'overview',
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
        if (arr && arr.length > 1) {
            arr.map((item: any) => {
                switch (item.name) {
                    case '概览':
                        fixArrChildrenApps[0] = item;
                        break;
                    case '任务查询':
                        fixArrChildrenApps[1] = item;
                        break;
                    case '规则配置':
                        fixArrChildrenApps[2] = item;
                        break;
                    case '逐行校验':
                        fixArrChildrenApps[3] = item;
                        break;
                    case '数据源管理':
                        fixArrChildrenApps[4] = item;
                        break;
                    case '项目管理':
                        fixArrChildrenApps[5] = item;
                        break;
                }
            })
            return fixArrChildrenApps
        } else {
            return []
        }
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
        const { app, licenseApps, project } = this.props;
        const { current } = this.state;
        const baseUrl = '/dataQuality.html#';
        const fixArrChildrenApps = this.fixArrayIndex(licenseApps[3] && licenseApps[3].children);
        const overviewNav = fixArrChildrenApps[0];
        const taskQueryNav = fixArrChildrenApps[1];
        const ruleNav = fixArrChildrenApps[2];
        const dataCheckNav = fixArrChildrenApps[3];
        const dataSourceNav = fixArrChildrenApps[4];
        const projectNav = fixArrChildrenApps[5];
        const display: boolean = current !== NOT_SHOW_KEY;
        const pid = project && project.id ? project.id : '';
        const menuItems: any = display && [
            {
                id: 'dq/overview',
                name: '概览',
                link: `${baseUrl}/dq/overview`,
                enable: overviewNav && overviewNav.isShow
            },
            {
                id: 'dq/taskQuery',
                name: '任务查询',
                link: `${baseUrl}/dq/taskQuery`,
                enable: taskQueryNav && taskQueryNav.isShow
            },
            {
                id: 'dq/rule',
                name: '规则配置',
                link: `${baseUrl}/dq/rule`,
                enable: ruleNav && ruleNav.isShow
            },
            {
                id: 'dq/dataCheck',
                name: '逐行校验',
                link: `${baseUrl}/dq/dataCheck`,
                enable: dataCheckNav && dataCheckNav.isShow
            },
            {
                id: 'dq/dataSource',
                name: '数据源管理',
                link: `${baseUrl}/dq/dataSource`,
                enable: dataSourceNav && dataSourceNav.isShow
            },
            {
                id: 'dq/project',
                name: '项目管理',
                link: `${baseUrl}/dq/project/${pid}/config`,
                enable: projectNav && projectNav.isShow
            }
        ];

        const logo = (
            <div onClick={this.goIndex} style={{ cursor: 'pointer' }}>
                <img
                    className='c-header__logo c-header__logo--dq'
                    alt="logo"
                    src={getHeaderLogo(app.id)}
                />
                <span className='c-header__title c-header__title--dq'>
                    {window.APP_CONF.prefix ? `${window.APP_CONF.prefix}.` : ''}{window.APP_CONF.name}
                </span>
            </div>
        );
        return <Navigator
            selectProjectsubMenu={display && this.renderProjectSelect()}
            logo={logo}
            menuItems={menuItems}
            licenseApps={licenseApps}
            {...this.props}
            showHelpSite={true}
            helpUrl='/public/helpSite/valid/v3.0/Summary.html'
        />;
    }
}
export default Header;
