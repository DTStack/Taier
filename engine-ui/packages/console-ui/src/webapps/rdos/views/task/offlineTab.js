import React, { Component } from 'react';
import { connect } from 'react-redux';
import {
    Icon, Tooltip,
    Tabs, Dropdown, Menu
} from 'antd';
import { isEmpty, union } from 'lodash';

import { scrollToView } from 'funcs';

import FolderTree from './offline/folderTree';
import TableTree from './offline/tableTree';
import ajax from '../../api';

import {
    taskTreeAction,
    resTreeAction,
    fnTreeAction,
    sysFnTreeActon,
    scriptTreeAction,
    tableTreeAction
} from '../../store/modules/offlineTask/actionType';

import {
    workbenchActions
} from '../../store/modules/offlineTask/offlineAction';

import { MENU_TYPE, PROJECT_TYPE } from '../../comm/const';
import { isProjectCouldEdit } from '../../comm';

const TabPane = Tabs.TabPane;

@connect(state => {
    const { offlineTask, user } = state;
    const { currentTab, tabs } = offlineTask.workbench;

    const currentTabData = tabs.filter(tab => {
        return tab.id === currentTab;
    })[0];

    return {
        user,
        project: state.project,
        taskTreeData: offlineTask.taskTree,
        resourceTreeData: offlineTask.resourceTree,
        functionTreeData: offlineTask.functionTree,
        sysFunctionTreeData: offlineTask.sysFunctionTree,
        scriptTreeData: offlineTask.scriptTree,
        tableTreeData: offlineTask.tableTree,
        currentTab,
        currentTabData
    }
}, workbenchActions)
class OfflineTabPane extends Component {
    constructor (props) {
        super(props);
    }

    state = {
        subMenus: [],
        expandedKeys: [],
        expandedKeys2: [],
        menu: MENU_TYPE.TASK
    }

    componentDidMount () {
        this.props.loadTaskParams();
        this.getCatelogue();
    }
    /* eslint-disable */
    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps (nextProps) {
        const old = this.props.project;
        const newData = nextProps.project;
        const nextTab = nextProps.currentTabData;
        if (newData && (!old || (old.id !== 0 && old.id !== newData.id))) {
            this.getCatelogue();
            this.setState({
                expandedKeys: [],
                expandedKeys2: []
            })
        }
        // 任务定位滚动
        if (this.props.currentTab !== nextProps.currentTab) {
            let type = MENU_TYPE.TASK_DEV; let menu = MENU_TYPE.TASK;
            if (nextTab && nextTab.scriptText !== undefined) {
                type = MENU_TYPE.SCRIPT;
                menu = MENU_TYPE.SCRIPT;
            }
            this.setState({
                menu
            }, () => {
                this.locateFilePos(nextProps.currentTab, null, type, nextTab)
            })
        }
    }
    /* eslint-disable */

    onExpand = (expandedKeys, { expanded }) => {
        let keys = expandedKeys;
        if (expanded) {
            keys = union(this.state.expandedKeys, keys)
        }
        this.setState({
            expandedKeys: keys
        })
    }

    onMenuChange = (key) => {
        this.setState({
            menu: key
        })
    }

    onExpand2 = (expandedKeys) => {
        this.setState({
            expandedKeys2: expandedKeys
        })
    }

    reloadTreeNodes = (id, type) => {
        this.props.loadTreeNode(id, type);
        this.setState({
            expandedKeys: [`${type}-${id}`]
        })
    }

    /**
     * 定位任务在文件树的具体位置
     */
    locateFilePos = (currentTab, name, type) => {
        const { expandedKeys, menu } = this.state;
        const {
            taskTreeData, scriptTreeData
        } = this.props;

        // 过滤任务开发定位脚本，或者脚本定位任务的无效情况
        if (
            !currentTab ||
            (type === MENU_TYPE.TASK_DEV && menu !== MENU_TYPE.TASK) ||
            (type === MENU_TYPE.SCRIPT && menu !== MENU_TYPE.SCRIPT)
        ) return;

        let treeData = taskTreeData;
        if (type === MENU_TYPE.SCRIPT) {
            treeData = scriptTreeData;
        }

        const getExpandedKey = (path) => {
            const arr = path && path.split('-');
            return arr && arr.map(p => `${type}-${p}`);
        }

        const scroll = () => {
            setTimeout(() => {
                scrollToView(`JS_${currentTab}`)
            }, 0)
        }

        let checkedPath = ''; let path = ''; // 路径存储

        const hasPath = (data, id, path) => {
            if (!data) return false;
            path = `${path ? path + '-' : path}${data.id}`
            if (data && data.id === id) {
                checkedPath = path;
                return true;
            }

            if (data.children) {
                const children = data.children
                for (let i = 0; i < children.length; i += 1) {
                    if (hasPath(children[i], id, path)) {
                        return true;
                    }
                }
            }
            return false;
        }

        if (hasPath(treeData, currentTab, path)) {
            const keys = getExpandedKey(checkedPath);
            this.setState({ expandedKeys: union(expandedKeys, keys) });
            scroll();
        } else {
            ajax.locateCataPosition({
                id: currentTab,
                catalogueType: type,
                name: name
            }).then(res => {
                if (res.code === 1 && res.data) {
                    const data = res.data.children[0];
                    if (hasPath(data, currentTab, path)) {
                        const keys = getExpandedKey(checkedPath);
                        this.setState({
                            expandedKeys: keys
                        })
                    }
                    this.props.locateFilePos(data, type);
                    scroll();
                }
            });
        }
    }

    getCatelogue () {
        const { dispatch } = this.props;
        // 四组数据在一个接口里面, 所以不在container中dispatch
        ajax.getOfflineCatalogue({
            isGetFile: !!1,
            nodePid: 0
        }).then(res => {
            if (res.code === 1) {
                const arrData = res.data.children;

                this.setState({ subMenus: arrData })

                for (let i = 0; i < arrData.length; i++) {
                    const menuItem = arrData[i]
                    switch (menuItem.catalogueType) {
                    case MENU_TYPE.TASK: {
                        dispatch({
                            type: taskTreeAction.RESET_TASK_TREE,
                            payload: menuItem.children[0]
                        });
                        break;
                    }
                    case MENU_TYPE.SCRIPT: {
                        dispatch({
                            type: scriptTreeAction.RESET_SCRIPT_TREE,
                            payload: menuItem.children[0]
                        });
                        break;
                    }
                    case MENU_TYPE.RESOURCE: {
                        dispatch({
                            type: resTreeAction.RESET_RES_TREE,
                            payload: menuItem.children[0]
                        });
                        break;
                    }
                    case MENU_TYPE.FUNCTION: {
                        dispatch({
                            type: fnTreeAction.RESET_FUC_TREE,
                            payload: menuItem.children[0]
                        });
                        dispatch({
                            type: sysFnTreeActon.RESET_SYSFUC_TREE,
                            payload: menuItem.children[1]
                        });
                        break;
                    }
                    case MENU_TYPE.TABLE: {
                        dispatch({
                            type: tableTreeAction.RESET_TABLE_TREE,
                            payload: menuItem.children[0]
                        });
                        break;
                    }
                    }
                }
            }
        });
    }

    onMenuClick = ({ key }) => {
        const {
            toggleCreateTask,
            toggleUpload,
            toggleCreateFolder,
            toggleCoverUpload,
            toggleCreateFn,
            toggleCreateScript,
        } = this.props;

        switch (key) {
        case 'task:newFolder': {
            toggleCreateFolder(MENU_TYPE.TASK_DEV)
            return;
        }
        case 'task:newTask': {
            toggleCreateTask()
            return;
        }
        case 'script:newScript': {
            toggleCreateScript()
            return;
        }
        case 'script:newFolder': {
            toggleCreateFolder(MENU_TYPE.SCRIPT)
            return;
        }
        case 'resource:newFolder': {
            toggleCreateFolder(MENU_TYPE.RESOURCE)
            return;
        }
        case 'resource:upload': {
            toggleUpload()
            return;
        }
        case 'resource:replace': {
            toggleCoverUpload()
            return;
        }
        case 'function:newFolder': {
            toggleCreateFolder(MENU_TYPE.COSTOMFUC)
            return;
        }
        case 'function:newFunc': {
            toggleCreateFn()
        }
        }
    }

    renderTabPanes = () => {
        const {
            taskTreeData,
            resourceTreeData,
            functionTreeData,
            sysFunctionTreeData,
            scriptTreeData,
            tableTreeData,
            currentTab,
            currentTabData,
            project,
            user
        } = this.props;

        const { subMenus, expandedKeys, expandedKeys2 } = this.state;
        const reloadTreeNodes = this.reloadTreeNodes;
        const isPro = project.projectType == PROJECT_TYPE.PRO;
        const couldEdit = isProjectCouldEdit(project, user);
        const menus = []
        if (subMenus && subMenus.length > 0) {
            for (let i = 0; i < subMenus.length; i++) {
                const menuItem = subMenus[i]
                let menuContent = ''
                switch (menuItem.catalogueType) {
                case MENU_TYPE.TASK: {
                    menuContent = <div className="menu-content">
                        <header>
                            <Tooltip title="定位">
                                <Icon
                                    type="environment"
                                    onClick={() => this.locateFilePos(currentTab, null, MENU_TYPE.TASK_DEV, currentTabData)}
                                />
                            </Tooltip>
                            <Tooltip title="刷新">
                                <Icon
                                    type="sync"
                                    style={{ fontSize: '12px' }}
                                    onClick={() => reloadTreeNodes(taskTreeData.id, MENU_TYPE.TASK_DEV)}
                                />
                            </Tooltip>
                            <Dropdown overlay={
                                <Menu onClick={this.onMenuClick}>
                                    {couldEdit && <Menu.Item key="task:newTask">
                                            新建任务
                                    </Menu.Item>}
                                    {couldEdit && <Menu.Item key="task:newFolder">
                                            新建文件夹
                                    </Menu.Item>}
                                </Menu>
                            } trigger={['click']}>
                                <Icon type="bars" />
                            </Dropdown>
                        </header>
                        <div className="contentBox">
                            <div className="folder-box">
                                {
                                    !isEmpty(taskTreeData) &&
                                        <FolderTree
                                            isPro={isPro}
                                            couldEdit={couldEdit}
                                            type={MENU_TYPE.TASK_DEV}
                                            onExpand={this.onExpand}
                                            treeData={taskTreeData}
                                            expandedKeys={expandedKeys}
                                        />
                                }
                            </div>
                        </div>
                    </div>
                    break;
                }
                case MENU_TYPE.SCRIPT: {
                    menuContent = <div className="menu-content">
                        <header>
                            <Tooltip title="定位" placement="bottom">
                                <Icon
                                    type="environment"
                                    onClick={() => this.locateFilePos(currentTab, null, menuItem.catalogueType, currentTabData)}
                                />
                            </Tooltip>
                            <Tooltip title="刷新" placement="bottom">
                                <Icon
                                    type="sync"
                                    style={{ fontSize: '12px' }}
                                    onClick={() => reloadTreeNodes(scriptTreeData.id, menuItem.catalogueType)}
                                />
                            </Tooltip>
                            {couldEdit && (
                                <Dropdown overlay={
                                    <Menu onClick={this.onMenuClick}>
                                        <Menu.Item key="script:newScript">
                                                新建脚本
                                        </Menu.Item>
                                        <Menu.Item key="script:newFolder">
                                                新建文件夹
                                        </Menu.Item>
                                    </Menu>
                                } trigger={['click']}>
                                    <Icon type="bars" />
                                </Dropdown>
                            )}
                        </header>
                        <div className="contentBox">
                            <div className="folder-box">
                                {
                                    !isEmpty(scriptTreeData) &&
                                        <FolderTree
                                            isPro={isPro}
                                            couldEdit={couldEdit}
                                            type={menuItem.catalogueType}
                                            expandedKeys={expandedKeys}
                                            onExpand={this.onExpand}
                                            treeData={scriptTreeData}
                                        />
                                }
                            </div>
                        </div>
                    </div>
                    break;
                }
                case MENU_TYPE.RESOURCE: {
                    menuContent = <div className="menu-content">
                        <header>
                            <Tooltip title="刷新">
                                <Icon
                                    type="sync"
                                    style={{ fontSize: '12px' }}
                                    onClick={() => reloadTreeNodes(resourceTreeData.id, menuItem.catalogueType)}
                                />
                            </Tooltip>
                            {couldEdit && (
                                <Dropdown overlay={
                                    <Menu onClick={this.onMenuClick}>
                                        <Menu.Item key="resource:upload">
                                                上传资源
                                        </Menu.Item>
                                        <Menu.Item key="resource:replace">
                                                替换资源
                                        </Menu.Item>
                                        <Menu.Item key="resource:newFolder">
                                                新建文件夹
                                        </Menu.Item>
                                    </Menu>
                                } trigger={['click']}>
                                    <Icon type="bars" />
                                </Dropdown>
                            )}
                        </header>
                        <div className="contentBox">
                            <div className="folder-box">
                                {
                                    !isEmpty(resourceTreeData) &&
                                        <FolderTree
                                            isPro={isPro}
                                            couldEdit={couldEdit}
                                            type={menuItem.catalogueType}
                                            expandedKeys={expandedKeys}
                                            onExpand={this.onExpand}
                                            treeData={resourceTreeData}
                                        />
                                }
                            </div>
                        </div>
                    </div>
                    break;
                }
                case MENU_TYPE.FUNCTION: {
                    menuContent = <div className="menu-content">
                        <header>
                            <Tooltip title="刷新">
                                <Icon
                                    type="sync"
                                    style={{ fontSize: '12px' }}
                                    onClick={() => reloadTreeNodes(functionTreeData.id, MENU_TYPE.COSTOMFUC)}
                                />
                            </Tooltip>
                            {couldEdit && (
                                <Dropdown overlay={
                                    <Menu onClick={this.onMenuClick}>
                                        <Menu.Item key="function:newFunc">
                                                新建函数
                                        </Menu.Item>
                                        <Menu.Item key="function:newFolder">
                                                新建文件夹
                                        </Menu.Item>
                                    </Menu>
                                } trigger={['click']}>
                                    <Icon type="bars" />
                                </Dropdown>
                            )}
                        </header>
                        <div className="contentBox">
                            <div className="folder-box">
                                {
                                    !isEmpty(functionTreeData) &&
                                        <FolderTree
                                            isPro={isPro}
                                            couldEdit={couldEdit}
                                            type={MENU_TYPE.COSTOMFUC}
                                            expandedKeys={expandedKeys}
                                            onExpand={this.onExpand}
                                            treeData={functionTreeData} />
                                }
                                {
                                    !isEmpty(sysFunctionTreeData) &&
                                        <FolderTree
                                            isPro={isPro}
                                            couldEdit={couldEdit}
                                            type={MENU_TYPE.SYSFUC}
                                            expandedKeys={expandedKeys2}
                                            onExpand={this.onExpand2}
                                            treeData={sysFunctionTreeData}
                                        />
                                }
                            </div>
                        </div>
                    </div>
                    break;
                }
                case MENU_TYPE.TABLE: {
                    menuContent = <TableTree treeData={tableTreeData} />
                    break;
                }
                default: {
                    menuContent = '';
                    break;
                }
                }
                menus.push(
                    <TabPane tab={menuItem.name} id={`${menuItem.catalogueType}-${menuItem.id}`} key={menuItem.catalogueType}>
                        {menuContent}
                    </TabPane>
                )
            }
        }

        return menus;
    }

    render () {

        return (
            <div className="g-taskOfflineSidebar task-sidebar m-tabs">
                <Tabs
                    tabPosition="left"
                    animated={false}
                    className="task-tab-menu"
                    style={{ height: '100%' }}
                    activeKey={this.state.menu}
                    onChange={this.onMenuChange}
                >
                    {this.renderTabPanes()}
                </Tabs>
            </div>
        )
    }
}

export default OfflineTabPane;
