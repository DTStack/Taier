import React from 'react'
import { Route, IndexRoute, Redirect } from 'react-router'

import asyncComponent from 'utils/asyncLoad'
import { isSelectedProject } from './interceptor'

import NotFund from 'widgets/notFund'

import Container from './views/container'
import Dashboard from './views/dashboard'

// ======= 项目 =======
import ProjectConfig from './views/project/config'
import ProjectMember from './views/project/member'
import RoleManagement from './views/project/role'
import RoleAdd from './views/project/role/add'
import RoleEdit from './views/project/role/edit'


// ======= 任务 =======
import TaskContainer from './views/task/container'
import Default from './views/task/realtime/default'
// import OfflineDefault from '././views/task/offline/default'

// ======= 运维 =======
import OpeRealTimeList from './views/operation/realtime/list'
import OpeAlarm from './views/operation/alarm/list'
import OpeAlarmConfig from './views/operation/alarm/config'
import OpeOfflineList from './views/operation/offline/taskOperation'
import OpeOfflineTaskMana from './views/operation/offline/taskMana'
import OpeOfflineTaskLog from './views/operation/offline/taskLog'
import OpeOfflineTaskRunTime from './views/operation/offline/taskRuntime'

// ======= 运维 =======
import DataSourceIndex from './views/dataSource'

// =========数据管理==========
import TableManage from './views/dataManage/tableManage';
import TableList from './views/dataManage/tableList';
import TableCreator from './views/dataManage/tableCreator';
import TableViewer from './views/dataManage/tableViewer';
import TableEditor from './views/dataManage/tableEditor';
import Log from './views/dataManage/log';
import DataCatalogue from './views/dataManage/dataCatalogue';
import DirtyData from './views/dataManage/dirtyData/index';
import DirtyDataTbOverview from './views/dataManage/dirtyData/table';

// The below is async load components
// ======= 项目 =======
// const Dashboard = asyncComponent(() => import('././views/dashboard')
// .then(module => module.default), { name: 'dashboard' })

const ProjectContainer = asyncComponent(() => import('./views/project/container')
.then(module => module.default), { name: 'projectContainer' })

// ======= 实时任务 =========
const TaskIndex = asyncComponent(() => import('./views/task/realtime')
.then(module => module.default), { name: 'pageTask' })

// ======= 离线任务 =========
const TaskOffline = asyncComponent(() => import('./views/task/offline')
.then(module => module.default), { name: 'offlineTaskPage' });
const OfflineDefault = asyncComponent(() => import('./views/task/offline/default')
.then(module => module.default), { name: 'offlineTaskPage' });

// ======= 运维 =======
const Operation = asyncComponent(() => import('./views/operation/container')
.then(module => module.default), { name: 'operationPage' })

const OperationOverview = asyncComponent(() => import('./views/operation/overview')
.then(module => module.default), { name: 'operationAbstract' })

const OperationTaskFlow = asyncComponent(() => import('./views/operation/offline/taskflow')
.then(module => module.default), { name: 'taskflow' })

// ======= 数据管理 =======
const DataManageContainer = asyncComponent(() => import('./views/dataManage/container')
.then(module => module.default), { name: 'dataManageContainer' })

// ======= 数据源管理 =======
const DataSourceContainer = asyncComponent(() => import('./views/dataSource/container')
.then(module => module.default), { name: 'dataSourceContainer' })

// ======= 测试 =======
const Test = asyncComponent(() => import('./views/test')
.then(module => module.default), { name: 'testPage' })

export default (
    <Route path="/" component={Container}>
        <Route path="/rdos.html" component={Dashboard}/>
        <IndexRoute component={Dashboard} />
        <Route path="/project/:pid" component={ProjectContainer} onEnter={isSelectedProject}>
            <IndexRoute component={ProjectConfig} />
            <Route path="config" component={ProjectConfig} />
            <Route path="member" component={ProjectMember} />
            <Route path="role" component={RoleManagement} />
            <Route path="role/add" component={RoleAdd} />
            <Route path="role/edit/:roleId" component={RoleEdit} />
        </Route>
        <Route path="realtime" component={TaskContainer} onEnter={isSelectedProject}>
            <IndexRoute component={Default} />
            <Route path="task" component={TaskIndex} />
            <Route path="task/:tid" component={TaskIndex} />
        </Route>
        <Route path="offline" component={TaskContainer} onEnter={isSelectedProject}>
            <IndexRoute component={OfflineDefault} />
            <Route path="task" component={TaskOffline} />
            <Route path="task/:tid" component={TaskOffline} />
        </Route>
        <Route path="operation" component={Operation} onEnter={isSelectedProject}>
            <IndexRoute component={OperationOverview} />
            <Route path="realtime" component={OpeRealTimeList} />
            <Route path="offline-operation" component={OpeOfflineList} />
            <Route path="offline-management" component={OpeOfflineTaskMana} />
            <Route path="task-log/:jobId" component={OpeOfflineTaskLog} />
            <Route path="task-runtime/:jobId" component={OpeOfflineTaskRunTime} />
            <Route path="task-flow" component={OperationTaskFlow} />
            <Route path="task-patch-data" component={OperationTaskFlow} />
            <Route path="task-flow/:jobId" component={OperationTaskFlow} />
            <Route path="alarm-record" component={OpeAlarm} />
            <Route path="alarm-config" component={OpeAlarmConfig} />
        </Route>
        <Route path="/database" component={DataSourceContainer}>
            <IndexRoute component={DataSourceIndex} />
        </Route>
        <Route path="/data-manage" component={DataManageContainer}>
            <Route path="table" component={TableManage}>
                <IndexRoute component={TableList} />
                <Route path="create" component={TableCreator} />
                <Route path="view/:tableId" component={TableViewer} />
                <Route path="edit/:tableId" component={TableEditor} />
            </Route>
            <Route path="log" component={Log} />
            <Route path="log/:tableId/:tableName" component={Log}></Route>
            <Route path="catalogue" component={DataCatalogue} />
            <Route path="dirty-data" component={DirtyData} />
            <Route path="dirty-data/table/:tableId" component={DirtyDataTbOverview} />
        </Route>

        <Route path="/test" component={Test} />
        <Route path="/*" component={NotFund} />
    </Route>
)
