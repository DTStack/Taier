import React, { Component } from "react";
import { Menu, Icon } from "antd";

import { MenuRight } from "main/components/nav";

import Api from "../../api";
import * as ProjectAction from "../../store/modules/project";

/* eslint-disable */
const UIC_URL_TARGET = APP_CONF.UIC_URL || "";
/* eslint-disable */

const SubMenu = Menu.SubMenu;

class Header extends Component {
    constructor(props) {
        super(props);
        this.state = {
            current: "project",
            devPath: "/offline/task"
        };
    }

    componentDidMount() {
        this.updateSelected();
    }

    // 控制项目下拉菜单的显示
    componentWillReceiveProps() {
        this.updateSelected();
    }

    handleClick = e => {
        const props = e.item.props;
        const { router, dispatch, cleanAllTabData } = this.props;
        this.setState({ current: e.key });
        const project = props.data;
        if (project) {
            dispatch(ProjectAction.getProject(project.id));
            // 清理tab数据
            if (this.state.current === "overview") {
                router.push("/offline/task");
            }
        }
    };

    clickUserMenu = obj => {
        if (obj.key === "logout") {
            Api.logout();
        }
    };

    goIndex = () => {
        const { router } = this.props;
        this.setState({ current: "overview" });
        router.push("/");
    };

    getProjectItems() {
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

    updateSelected() {
        let pathname = this.props.router.location.pathname;
        const routes = pathname ? pathname.split("/") : [];
        let path =
            routes.length > 0 && routes[1] !== "" ? routes[1] : "overview";
        if (
            path &&
            (path.indexOf("task") > -1 || path.indexOf("offline") > -1)
        ) {
            this.setState({
                devPath: pathname
            });
            path = "realtime";
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
                <Menu.Item key="ucenter">
                    <a href={UIC_URL_TARGET}>用户中心</a>
                </Menu.Item>
                <Menu.Item key="logout">退出登录</Menu.Item>
            </Menu>
        );
    };

    renderProjectSelect = () => {
        const { project, projects } = this.props;

        const projectName =
            project && project.projectName
                ? project.projectAlias || project.projectName
                : "项目选择";

        return (
            <SubMenu
                defaultSelectedKeys={
                    projects && projects[0] ? projects[0].id : ""
                }
                title={
                    <span className="my-menu-item">
                        {
                            <span
                                className="menu-text-ellipsis"
                                title={projectName}
                            >
                                {projectName}
                            </span>
                        }{" "}
                        <Icon type="caret-down" />
                    </span>
                }
            >
                {this.getProjectItems()}
            </SubMenu>
        );
    };

    render() {
        const { user, project, apps, app, router } = this.props;
        const { current, devPath } = this.state;

        let pathname = router.location.pathname;

        const userMenu = this.initUserDropMenu();
        const display = current !== "overview" ? "inline-block" : "none";
        const pid = project && project.id ? project.id : "";

        const basePath = app.link;

        // 如果是数据地图模块，隐藏项目下拉选择菜单
        const showProjectSelect =
            pathname.indexOf("/data-manage") > -1 ? false : true;

        return (
            <div className="header">
                <div onClick={this.goIndex} className="logo left txt-left">
                    <img
                        style={{ height: "20px", marginTop: "10px" }}
                        alt="logo"
                        src="/public/rdos/img/logo.svg"
                    />
                    <span
                        style={{
                            fontSize: "14px",
                            color: "#ffffff",
                            position: "absolute",
                            left: "70px",
                            top: 0
                        }}
                    >
                        DTinsight.IDE
                    </span>
                </div>
                <div className="menu left">
                    <Menu
                        className="my-menu"
                        onClick={this.handleClick}
                        selectedKeys={[this.state.current]}
                        mode="horizontal"
                    >
                        {showProjectSelect && this.renderProjectSelect()}
                        <Menu.Item
                            className="my-menu-item"
                            key="database"
                            style={{ display }}
                        >
                            <a href={`${basePath}/database`}>数据集成</a>
                        </Menu.Item>
                        <Menu.Item
                            className="my-menu-item"
                            key="realtime"
                            style={{ display }}
                        >
                            <a href={`${basePath}${devPath}`}>数据开发</a>
                        </Menu.Item>
                        <Menu.Item
                            className="my-menu-item"
                            key="operation"
                            style={{ display }}
                        >
                            <a href={`${basePath}/operation`}>运维中心</a>
                        </Menu.Item>
                        <Menu.Item
                            className="my-menu-item"
                            key="data-manage"
                            style={{ display }}
                        >
                            <a href={`${basePath}/data-manage/assets`}>
                                数据地图
                            </a>
                        </Menu.Item>
                        <Menu.Item
                            className="my-menu-item"
                            key="data-model"
                            style={{ display }}
                        >
                            <a href={`${basePath}/data-model/overview`}>
                                数据模型
                            </a>
                        </Menu.Item>
                        <Menu.Item
                            className="my-menu-item"
                            key="project"
                            style={{ display }}
                        >
                            <a href={`${basePath}/project/${pid}/config`}>
                                项目管理
                            </a>
                        </Menu.Item>
                    </Menu>
                </div>

                <MenuRight
                    user={user}
                    app={app}
                    apps={apps}
                    onClick={this.clickUserMenu}
                    showHelpSite={true}
                    helpUrl="/public/rdos/helpSite/index.html"
                />
            </div>
        );
    }
}
export default Header;
