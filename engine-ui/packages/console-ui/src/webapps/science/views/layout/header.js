import React, { Component } from 'react'
import { get } from 'lodash';
import { connect } from 'react-redux'
import { Menu, Dropdown, Icon } from 'antd';

import Navigator from 'main/components/nav';
import { getHeaderLogo } from 'main/consts';
const SubMenu = Menu.SubMenu;
@connect(state => {
    return {
        user: state.user,
        apps: state.apps,
        routing: state.routing,
        app: state.app,
        licenseApps: state.licenseApps
    }
})
class Header extends Component {
    constructor (props) {
        super(props)
        this.state = {}
    }
    getProjectItems () {
        const { projects = [] } = this.props;
        return projects.map((project) => {
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
        })
    }
    selectedProject (e) {
        console.log(e.key)
    }
    render () {
        const { app, licenseApps } = this.props;
        const { project } = this.props;
        const projectName = get(project, 'projectAlias', project ? project.projectName : '项目选择');
        const baseUrl = app.link;
        const path = location.hash.split('/');
        let menuItems = [];
        let customItems = []
        if (path.length > 2) {
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
                enable: true
            },
            {
                id: 'science/operation',
                name: '运维中心',
                link: `${baseUrl}science/operation`,
                enable: true
            },
            {
                id: 'science/source',
                name: '数据管理',
                link: `${baseUrl}science/source`,
                enable: true
            }];
        }
        const logo = <a style={{ textDecoration: 'none' }} href={baseUrl}>
            <img
                className='c-header__logo c-header__logo--analytics'
                alt="logo"
                src={getHeaderLogo(app.id)}
            />
            <span className='c-header__title c-header__title--analytics'>
                {window.APP_CONF.prefix ? `${window.APP_CONF.prefix}.` : ''}{window.APP_CONF.name}
            </span>
        </a>
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
