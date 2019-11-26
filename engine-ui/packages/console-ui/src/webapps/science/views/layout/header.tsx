import * as React from 'react'
import { get } from 'lodash';
import { connect } from 'react-redux'

import { Menu, Dropdown, Icon } from 'antd';
import Navigator from 'main/components/nav';
import { getHeaderLogo } from 'main/consts';

import { setProject } from '../../actions/base'

const SubMenu = Menu.SubMenu;
@(connect((state: any) => {
    return {
        user: state.user,
        apps: state.apps,
        routing: state.routing,
        app: state.app,
        licenseApps: state.licenseApps,
        projects: state.project.projectList,
        project: state.project.currentProject
    }
}, (dispatch: any) => {
    return {
        setProject (project: any) {
            return dispatch(setProject(project))
        }
    }
}) as any)
class Header extends React.Component<any, any> {
    constructor (props: any) {
        super(props)
        this.state = {}
    }
    getProjectItems () {
        const { projects = [] } = this.props;
        return projects.map((project: any) => {
            const name = project.projectAlias || project.projectName;
            return (
                <Menu.Item
                    data={project}
                    title={name}
                    value={project.id}
                    key={project.id}
                >
                    {project.projectAlias || project.projectName}
                </Menu.Item>
            );
        })
    }
    selectedProject = (e: any) => {
        const { projects } = this.props;
        this.props.setProject(projects.find((project: any) => {
            return e.key == project.id;
        }));
    }
    getMenuLicense (licenseApps: any[] = []) {
        const result: any = {
            develop: true,
            operation: true,
            source: true
        };
        const app = licenseApps[5];
        if (!app) {
            return result;
        }
        const scienceItems = licenseApps[5].children || [];
        scienceItems.forEach((item: any) => {
            switch (item.name) {
                case '算法实验': {
                    result.develop = item.isShow;
                    break;
                }
                case '运维中心': {
                    result.operation = item.isShow;
                    break;
                }
                case '数据管理': {
                    result.source = item.isShow;
                    break;
                }
            }
        });
        return result;
    }

    render () {
        const { app, licenseApps } = this.props;
        const { project } = this.props;
        const projectName = get(project, 'projectAlias', project ? project.projectName : '项目名称');
        const menulicense = this.getMenuLicense(licenseApps);
        const baseUrl = app.link;
        const path = location.hash.split('/');
        let menuItems: any = [];
        let customItems: any = []
        if (path.length > 2 && path[2] !== 'index') {
            const menu = <Menu
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
            customItems.push(
                <SubMenu
                    className="my-menu-item"
                    key='sub-menu-item'
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
            menuItems = [{
                id: 'science/workbench',
                name: '算法实验',
                link: `${baseUrl}science/workbench`,
                enable: menulicense.develop
            },
            {
                id: 'science/operation',
                name: '运维中心',
                link: `${baseUrl}science/operation`,
                enable: menulicense.operation
            },
            {
                id: 'science/source',
                name: '数据管理',
                link: `${baseUrl}science/source`,
                enable: menulicense.source
            }];
        }
        const logo = <React.Fragment>
            <img
                className='c-header__logo'
                alt="logo"
                src={getHeaderLogo(app.id)}
            />
            <a style={{ textDecoration: 'none' }} href={baseUrl}>
                <span className='c-header__title'>
                    {window.APP_CONF.prefix ? `${window.APP_CONF.prefix}.` : ''}{window.APP_CONF.name}
                </span>
            </a>
        </React.Fragment >

        return <Navigator
            logo={logo}
            customItems={customItems}
            menuItems={menuItems}
            licenseApps={licenseApps}
            {...this.props}
        />
    }
}
export default Header
