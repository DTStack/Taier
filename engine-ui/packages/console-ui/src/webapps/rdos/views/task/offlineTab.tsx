import * as React from 'react';
import { connect } from 'react-redux';
import {
    Icon, Tooltip, Collapse,
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
    // fnTreeAction,
    sparkFnTreeAction,
    libraFnTreeAction,
    // sysFnTreeActon,
    scriptTreeAction,
    tableTreeAction
} from '../../store/modules/offlineTask/actionType';

import {
    workbenchActions
} from '../../store/modules/offlineTask/offlineAction';

import { MENU_TYPE, PROJECT_TYPE } from '../../comm/const';
import { isProjectCouldEdit } from '../../comm';

const TabPane = Tabs.TabPane;
const Panel = Collapse.Panel;

@(connect((state: any) => {
    const { offlineTask, user } = state;
    const { currentTab, tabs } = offlineTask.workbench;

    const currentTabData = tabs.filter((tab: any) => {
        return tab.id === currentTab;
    })[0];

    return {
        user,
        project: state.project,
        taskTreeData: offlineTask.taskTree,
        resourceTreeData: offlineTask.resourceTree,
        sparkTreeData: offlineTask.sparkTree,
        libraTreeData: offlineTask.libraTree,
        libraSysTreeData: offlineTask.libraSysFnTree,
        functionTreeData: offlineTask.functionTree,
        sysFunctionTreeData: offlineTask.sysFunctionTree,
        scriptTreeData: offlineTask.scriptTree,
        tableTreeData: offlineTask.tableTree,
        currentTab,
        currentTabData
    }
}, workbenchActions) as any)
class OfflineTabPane extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
    }

    state: any = {
        subMenus: [],
        expandedKeys: [],
        expandedKeys2: [],
        menu: MENU_TYPE.TASK,
        sparkSql: '',
        libraSql: ''
    }

    componentDidMount () {
        this.props.loadTaskParams();
        this.getCatelogue();
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
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
            let type = MENU_TYPE.TASK_DEV;
            if (nextTab && nextTab.scriptText !== undefined) {
                type = MENU_TYPE.SCRIPT;
            }
            this.locateFilePos(nextProps.currentTab, null, type)
        }
    }

    onExpand = (expandedKeys: any, { expanded }: any) => {
        let keys = expandedKeys;
        if (expanded) {
            keys = union(this.state.expandedKeys, keys)
        }
        this.setState({
            expandedKeys: keys
        })
    }

    onMenuChange = (key: any) => {
        this.setState({
            menu: key
        })
    }

    onExpand2 = (expandedKeys: any) => {
        this.setState({
            expandedKeys2: expandedKeys
        })
    }

    /**
     * 用于刷新整个菜单
     */
    reloadTreeNodes = (id: any, type: any) => {
        this.props.loadTreeNode(id, type);
        this.setState({
            expandedKeys: [`${type}-folder-${id}`]
        })
    }

    onLocatePos = (currentTab: any, name: any) => {
        let menu = MENU_TYPE.TASK;
        let type = MENU_TYPE.TASK_DEV;
        const tabData = this.props.currentTabData;
        if (tabData && tabData.scriptText !== undefined) {
            menu = MENU_TYPE.SCRIPT;
            type = MENU_TYPE.SCRIPT;
        }
        this.setState({
            menu
        }, () => {
            setTimeout(() => {
                this.locateFilePos(currentTab, name, type);
            }, 100);
        })
    }

    /**
     * 定位任务在文件树的具体位置
     */
    locateFilePos = (currentTab?: any, name?: any, type?: any) => {
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

        const getExpandedKey = (path: any) => {
            const arr = path && path.split('-');
            return arr && arr.map((p: any) => {
                const arr = p.split(':');
                const id = arr[0];
                const fileType = arr[1];
                // Key 的格式与 fodlerTree 中 Node的 key 生成规则对应
                return `${type}-${fileType}-${id}`;
            });
        }

        const scroll = () => {
            setTimeout(() => {
                scrollToView(`JS_${currentTab}`)
            }, 0)
        }

        let checkedPath = ''; let path = ''; // 路径存储

        const hasPath = (data: any, id: any, path: any) => {
            if (!data) return false;
            path = `${path ? path + '-' : path}${data.id}:${data.type}`
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
            }).then((res: any) => {
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
        }).then((res: any) => {
            if (res.code === 1) {
                const arrData = res.data.children;
                const expandedKeys: any = []
                this.setState({ subMenus: arrData })

                for (let i = 0; i < arrData.length; i++) {
                    const menuItem = arrData[i]
                    switch (menuItem.catalogueType) {
                        case MENU_TYPE.TASK: {
                            dispatch({
                                type: taskTreeAction.RESET_TASK_TREE,
                                payload: menuItem.children[0]
                            });
                            this.props.loadTreeNode(menuItem.children[0].id,
                                MENU_TYPE.TASK_DEV,
                                {
                                    taskType: menuItem.children[0].taskType,
                                    parentId: menuItem.children[0].parentId
                                })
                            expandedKeys.push(`${MENU_TYPE.TASK_DEV}-${menuItem.children[0].type}-${menuItem.children[0].id}`)
                            break;
                        }
                        case MENU_TYPE.SCRIPT: {
                            dispatch({
                                type: scriptTreeAction.RESET_SCRIPT_TREE,
                                payload: menuItem.children[0]
                            });
                            this.props.loadTreeNode(menuItem.children[0].id,
                                MENU_TYPE.SCRIPT,
                                {
                                    taskType: menuItem.children[0].taskType,
                                    parentId: menuItem.children[0].parentId
                                })
                            expandedKeys.push(`${MENU_TYPE.SCRIPT}-${menuItem.children[0].type}-${menuItem.children[0].id}`)
                            break;
                        }
                        case MENU_TYPE.RESOURCE: {
                            dispatch({
                                type: resTreeAction.RESET_RES_TREE,
                                payload: menuItem.children[0]
                            });
                            this.props.loadTreeNode(menuItem.children[0].id,
                                MENU_TYPE.RESOURCE,
                                {
                                    taskType: menuItem.children[0].taskType,
                                    parentId: menuItem.children[0].parentId
                                })
                            expandedKeys.push(`${MENU_TYPE.RESOURCE}-${menuItem.children[0].type}-${menuItem.children[0].id}`)
                            break;
                        }
                        case MENU_TYPE.FUNCTION: {
                            const sparkSql = menuItem.children.find((item: any) => item.catalogueType == MENU_TYPE.SPARKFUNC);
                            const libraSql = menuItem.children.find((item: any) => item.catalogueType == MENU_TYPE.LIBRAFUNC);
                            this.setState({
                                sparkSql,
                                libraSql
                            })
                            dispatch({ // dispatch spark  第一级目录文件
                                type: sparkFnTreeAction.GET_SPARK_ROOT,
                                payload: sparkSql
                            })
                            dispatch({ // dispatch libra  第一级目录文件
                                type: libraFnTreeAction.GET_LIBRA_ROOT,
                                payload: libraSql
                            })
                            // spark
                            sparkSql && this.props.loadTreeNode(sparkSql.id,
                                MENU_TYPE.SPARKFUNC,
                                {
                                    taskType: sparkSql.taskType,
                                    parentId: sparkSql.parentId
                                }, true)
                            // libra
                            libraSql && this.props.loadTreeNode(libraSql.id,
                                MENU_TYPE.LIBRAFUNC,
                                {
                                    taskType: libraSql.taskType,
                                    parentId: libraSql.parentId
                                }, true)
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
                this.setState({
                    expandedKeys
                })
            }
        });
    }

    onMenuClick = ({ key }: any) => {
        const {
            toggleCreateTask,
            toggleUpload,
            toggleCreateFolder,
            toggleCoverUpload,
            toggleCreateFn,
            toggleCreateScript
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
            libraSysTreeData,
            functionTreeData,
            sysFunctionTreeData,
            scriptTreeData,
            tableTreeData,
            currentTab,
            project,
            user
        } = this.props;
        const { subMenus, expandedKeys, expandedKeys2, sparkSql, libraSql } = this.state;
        const reloadTreeNodes = this.reloadTreeNodes;
        const isPro = project && project.projectType == PROJECT_TYPE.PRO;
        const couldEdit = isProjectCouldEdit(project, user);
        const menus: any = []
        if (subMenus && subMenus.length > 0) {
            for (let i = 0; i < subMenus.length; i++) {
                const menuItem = subMenus[i]
                let menuContent = null;
                switch (menuItem.catalogueType) {
                    case MENU_TYPE.TASK: {
                        menuContent = <div className="menu-content">
                            <header>
                                <Tooltip title="定位">
                                    <Icon
                                        type="environment"
                                        onClick={() => this.onLocatePos(currentTab, null)}
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
                                        onClick={() => this.onLocatePos(currentTab, null)}
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
                                        <Menu onClick={this.onMenuClick} mode="vertical">
                                            {
                                                !isEmpty(sparkSql) && (
                                                    <Menu.SubMenu title='Spark' key="spark">
                                                        <Menu.Item key="function:newFunc">
                                                            新建函数
                                                        </Menu.Item>
                                                        <Menu.Item key="function:newFolder">
                                                            新建文件夹
                                                        </Menu.Item>
                                                    </Menu.SubMenu>
                                                )
                                            }
                                            {
                                                !isEmpty(libraSql) && (
                                                    <Menu.SubMenu title='LibrA' key="libra">
                                                        <Menu.Item key="">
                                                            LibrA引擎暂不支持创建自定义函数
                                                        </Menu.Item>
                                                    </Menu.SubMenu>
                                                )
                                            }
                                        </Menu>
                                    } trigger={['click']}>
                                        <Icon type="bars" />
                                    </Dropdown>
                                )}
                            </header>
                            <div className="contentBox c-funcMa__collapse m-siderbench">
                                <div className="folder-box">
                                    <Collapse defaultActiveKey={['spark']} accordion>
                                        {
                                            !isEmpty(sparkSql) && (
                                                <Panel header="SparkSQL" key="spark">
                                                    {
                                                        !isEmpty(functionTreeData) &&
                                                        <FolderTree
                                                            isPro={isPro}
                                                            couldEdit={couldEdit}
                                                            type={MENU_TYPE.COSTOMFUC}
                                                            expandedKeys={expandedKeys}
                                                            onExpand={this.onExpand}
                                                            treeData={functionTreeData}
                                                        />
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
                                                </Panel>
                                            )
                                        }
                                        {
                                            !isEmpty(libraSql) && (
                                                <Panel header="LibrA SQL" key="librA">
                                                    {
                                                        !isEmpty(libraSysTreeData) &&
                                                        <FolderTree
                                                            isPro={isPro}
                                                            couldEdit={couldEdit}
                                                            type={MENU_TYPE.LIBRASYSFUN}
                                                            expandedKeys={expandedKeys2}
                                                            onExpand={this.onExpand2}
                                                            treeData={libraSysTreeData}
                                                        />
                                                    }
                                                </Panel>
                                            )
                                        }
                                    </Collapse>
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
                    <TabPane tab={menuItem.name} {...{ id: `${menuItem.catalogueType}-${menuItem.id}` }} key={menuItem.catalogueType}>
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
