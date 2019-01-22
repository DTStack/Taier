import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Menu, Icon, Dropdown, Modal } from 'antd';

import { MenuRight } from 'main/components/nav';
import { getHeaderLogo } from 'main/consts';

import Api from '../../api';
import { PROJECT_TYPE } from '../../comm/const';
import * as ProjectAction from '../../store/modules/project';

// eslint-disable-next-line
const UIC_URL_TARGET = APP_CONF.UIC_URL || '';

const SubMenu = Menu.SubMenu;
const confirm = Modal.confirm;

@connect(state => {
    const { workbench } = state.offlineTask;

    return {
        offlineTabs: workbench.tabs,
        licenseApps: state.licenseApps
    }
})
class Header extends Component {
    constructor (props) {
        super(props);
        this.state = {
            current: 'project',
            devPath: '/offline/task'
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

    handleClick = e => {
        /**
         * 数据地图特殊处理,当在项目里面点击数据地图，则打开新窗口
         */
        const isDataMap = e.key == 'data-manage';
        const isIndex = this.isIndex();
        if (!isIndex && isDataMap) {
            return;
        }
        this.setState({ current: e.key });
    };

    selectedProject = (evt) => {
        const { router, dispatch } = this.props;
        const projectId = evt.key;
        if (projectId) {
            const switchProject = () => {
                dispatch(ProjectAction.getProject(projectId));
                // 清理tab数据
                if (this.state.current === 'overview') {
                    router.push('/offline/task');
                }
            }
            this.checkUnSaveTask(switchProject);
        }
    }

    checkUnSaveTask = (onOk) => {
        const { offlineTabs } = this.props;
        const tabsData = offlineTabs || [];

        const hasUnSave = (tabs) => {
            for (let tab of tabs) {
                if (tab.notSynced) {
                    return true;
                }
            }
            return false;
        }
        if (hasUnSave(tabsData)) {
            confirm({
                title: '部分任务修后未同步到服务器，是否强制关闭?',
                content: '在未保存任务前，切换项目将会丢弃这些任务的修改数据，建议您确认后再行操作！',
                onOk () {
                    if (onOk) onOk();
                }
            });
        } else {
            onOk();
        }
    }

    clickUserMenu = obj => {
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
        if (projects && projects.length > 0) {
            return projects.map(project => {
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
                        <a href={UIC_URL_TARGET}>用户中心</a>
                    </Menu.Item>
                )}
                <Menu.Item key="logout">退出登录</Menu.Item>
            </Menu>
        );
    };

    renderProjectSelect = () => {
        const { project } = this.props;

        const projectName =
            project && project.projectName
                ? project.projectAlias || project.projectName
                : '项目选择';
        let projectTypeText = '';
        let projectTypeIcon = null;
        switch (project && project.projectType) {
            case PROJECT_TYPE.TEST: {
                projectTypeText = ' (测试项目)'
                projectTypeIcon = <img style={{ verticalAlign: 'text-bottom', marginRight: '5px' }} src="/public/rdos/img/icon/develop.svg" />
                break;
            }
            case PROJECT_TYPE.PRO: {
                projectTypeText = ' (生产项目)'
                projectTypeIcon = <img style={{ verticalAlign: 'text-bottom', marginRight: '5px' }} src="/public/rdos/img/icon/produce.svg" />
                break;
            }
            default: projectTypeText = '';
        }
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
                            {projectTypeIcon}
                            <span
                                className="menu-text-ellipsis"
                                title={projectName}
                            >
                                {projectName}
                            </span>
                            <span>{projectTypeText}</span>
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
        switch (project && project.projectType) {
            case PROJECT_TYPE.TEST: {
                return (
                    <div
                        className="head-project-tip"
                    >
                        <span className="content">
                            <img src="/public/rdos/img/icon/develop.svg" />测试
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
                            <img src="/public/rdos/img/icon/produce.svg" />生产
                        </span>
                    </div>
                )
            }
            default: {
                return '';
            }
        }
    }

    isIndex () {
        const { current } = this.state;
        const isIndex = current == 'overview' || current == 'data-manage' || current == 'metaDataImport';
        return isIndex;
    }
    // 固定数组Index, 控制显示子应用
    fixArrayIndex = (arr) => {
        let fixArrChildrenApps = [];
        if (arr && arr.length > 1) {
            arr.map(item => {
                switch (item.name) {
                    case '数据源':
                        fixArrChildrenApps[0] = item;
                        break;
                    case '数据开发':
                        fixArrChildrenApps[1] = item;
                        break;
                    case '运维中心':
                        fixArrChildrenApps[2] = item;
                        break;
                    case '数据地图':
                        fixArrChildrenApps[3] = item;
                        break;
                    case '数据模型':
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
    render () {
        const { user, project, apps, app, licenseApps } = this.props;
        const { devPath } = this.state;
        const isIndex = this.isIndex();
        const display = !isIndex ? 'inline-block' : 'none';
        const fixArrChildrenApps = this.fixArrayIndex(licenseApps[0].children);
        const dataSourceNav = fixArrChildrenApps[0];
        const taskNav = fixArrChildrenApps[1];
        const operaNav = fixArrChildrenApps[2];
        const dataMapNav = fixArrChildrenApps[3];
        const dataModalNav = fixArrChildrenApps[4];
        const projectNav = fixArrChildrenApps[5];
        const pid = project && project.id ? project.id : '';

        const basePath = app.link;

        // 如果是数据地图模块，隐藏项目下拉选择菜单
        const showProjectSelect = !isIndex;
        // const projectTypeView = this.renderProjectType();
        return (
            <div className={`header ${window.APP_CONF.theme || 'default'}`}>
                <div onClick={this.goIndex} className="logo left txt-left">
                    <img
                        className='c-header__logo c-header__logo--rdos'
                        alt="logo"
                        src={getHeaderLogo(app.id)}
                    />
                    <span className='c-header__title c-header__title--rdos'>
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
                        {/* {showProjectSelect && projectTypeView && <Menu.Item
                            className="my-menu-item tip"
                            key="env_logo"
                            style={{ display }}
                            disabled
                        >
                            {this.renderProjectType()}
                        </Menu.Item>} */}
                        {isIndex ? (
                            <Menu.Item
                                className="my-menu-item"
                                key="overview"
                            >
                                <a href={`${basePath}/`}>
                                    项目列表
                                </a>
                            </Menu.Item>
                        ) : null}
                        {dataSourceNav && dataSourceNav.isShow ? (
                            <Menu.Item
                                className="my-menu-item"
                                key="database"
                                style={{ display }}
                            >
                                <a href={`${basePath}/database`} >数据源</a>
                            </Menu.Item>
                        ) : null }
                        {taskNav && taskNav.isShow ? (
                            <Menu.Item
                                className="my-menu-item"
                                key="realtime"
                                style={{ display }}
                            >
                                <a href={`${basePath}${devPath}`}>数据开发</a>
                            </Menu.Item>
                        ) : null }
                        {operaNav && operaNav.isShow ? (
                            <Menu.Item
                                className="my-menu-item"
                                key="operation"
                                style={{ display }}
                            >
                                <a href={`${basePath}/operation`}>运维中心</a>
                            </Menu.Item>
                        ) : null }
                        {dataMapNav && dataMapNav.isShow ? (
                            <Menu.Item
                                className="my-menu-item"
                                key="data-manage"
                            >
                                <a href={`${basePath}/data-manage/assets`} target={isIndex ? '_self' : '_blank'}>
                                    数据地图
                                </a>
                            </Menu.Item>
                        ) : null }
                        {dataModalNav && dataModalNav.isShow ? (
                            <Menu.Item
                                className="my-menu-item menu_large"
                                key="data-model"
                                style={{ display }}
                            >
                                <a href={`${basePath}/data-model/overview`}>
                                    数据模型
                                </a>
                            </Menu.Item>
                        ) : null }
                        {projectNav && projectNav.isShow ? (
                            <Menu.Item
                                className="my-menu-item menu_large"
                                key="project"
                                style={{ display }}
                            >
                                <a href={`${basePath}/project/${pid}/config`}>
                                    项目管理
                                </a>
                            </Menu.Item>
                        ) : null }
                        <SubMenu
                            className="my-menu-item menu_mini"
                            style={{ display }}
                            title={(<span
                                style={{
                                    display,
                                    height: '47px'
                                }}
                                className="my-menu-item"
                            >
                                <span
                                    className="menu-text-ellipsis"
                                >
                                    其他
                                </span>
                                &nbsp;
                                <Icon type="caret-down" />
                            </span>)}
                        >
                            {dataModalNav && dataModalNav.isShow ? (
                                <Menu.Item
                                    className="my-menu-item no-border"
                                    key="data-model"
                                >
                                    <a href={`${basePath}/data-model/overview`}>
                                        数据模型
                                    </a>
                                </Menu.Item>
                            ) : null }
                            {projectNav && projectNav.isShow ? (
                                <Menu.Item
                                    className="my-menu-item"
                                    key="project"
                                >
                                    <a href={`${basePath}/project/${pid}/config`}>
                                        项目管理
                                    </a>
                                </Menu.Item>
                            ) : null }
                        </SubMenu>
                    </Menu>
                </div>

                <MenuRight
                    user={user}
                    app={app}
                    apps={apps}
                    licenseApps={licenseApps}
                    onClick={this.clickUserMenu}
                    showHelpSite={true}
                    helpUrl="/public/helpSite/dtinsight-batch/v3.0/Summary.html"
                />
            </div>
        );
    }
}
export default Header;
