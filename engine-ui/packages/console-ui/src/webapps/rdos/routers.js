import React from 'react'
import { Route, IndexRoute } from 'react-router'

import asyncComponent from 'utils/asyncLoad'
import { isSelectedProject } from './interceptor'

import NotFund from 'widgets/notFund'

// 继承主应用的的公共View组件
import Main from 'main/views'
import MsgCenter from 'main/views/message'
import MsgList from 'main/views/message/list'
import MsgDetail from 'main/views/message/detail'

import SysAdmin from 'main/views/admin'
import AdminUser from 'main/views/admin/user'
import AdminRole from 'main/views/admin/role'
import GRoleAdd from 'main/views/admin/role/add'
import GRoleEdit from 'main/views/admin/role/edit'

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
import OpenOfflineTask from './views/task/offline/openOfflineTask'
// import OfflineDefault from '././views/task/offline/default'

// ======= 运维 =======
// import OpeRealTimeList from './views/operation/realtime/list'
import OpeAlarm from './views/operation/alarm/list'
import OpeAlarmConfig from './views/operation/alarm/config'
import OpeOfflineList from './views/operation/offline/taskOperation'
import OpeOfflineTaskMana from './views/operation/offline/taskMana'
import OpeOfflineTaskRunTime from './views/operation/offline/taskRuntime'
import OperationPatchData from './views/operation/offline/patchDataList'
import OperationPatchDataDetail from './views/operation/offline/patchDataDetail'
import DirtyData from './views/operation/dirtyData/index';
import DirtyDataTbOverview from './views/operation/dirtyData/table';
// ======= 运维 =======
import DataSourceOffline from './views/dataSource';
import DataSourceMigrationOffline from './views/dataSource/dbSync';

// ======= 数据管理 =======
import TableManage from './views/dataManage/tableManage';
import TableList from './views/dataManage/tableList';
import TableCreator from './views/dataManage/tableCreator';
import TableViewer from './views/dataManage/tableViewer';
import TableEditor from './views/dataManage/tableEditor';
import Log from './views/dataManage/log';
import DataCatalogue from './views/dataManage/dataCatalogue';
import AuthMana from './views/dataManage/authority';
import SearchTable from './views/dataManage/search';
import AssetOverview from './views/dataManage/assets';
import DesensitizationMange from './views/dataManage/dataDesensitization/desensitizationMange';
import RuleManage from './views/dataManage/dataDesensitization/ruleMange'

// ======= 数据模型 =======
import DataModelOverview from './views/dataModel/overview';
import DataModelCheckCenter from './views/dataModel/dataCheck';
import DataModelDesign from './views/dataModel/modelDesign';
import DataModelCreate from './views/dataModel/modelDesign/tableCreator';
import DataModelModify from './views/dataModel/modelDesign/tableEditor';
import DataModelConfig from './views/dataModel/configure';

// The below is async load components
// ======= 项目 =======
// const Dashboard = asyncComponent(() => import('././views/dashboard')
// .then(module => module.default), { name: 'dashboard' })

const ProjectContainer = asyncComponent(() => import('./views/project/container')
    .then(module => module.default), { name: 'projectContainer' })

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

// ======= 数据管理 =======
const DataManageContainer = asyncComponent(() => import('./views/dataManage/container')
    .then(module => module.default), { name: 'dataManageContainer' })

// ======= 数据源管理 =======
const DataSourceContainer = asyncComponent(() => import('./views/dataSource/container')
    .then(module => module.default), { name: 'dataSourceContainer' })

// ======= 数据源管理 =======
const DataModelContainer = asyncComponent(() => import('./views/dataModel/index')
    .then(module => module.default), { name: 'dataModelContainer' })

// ======= 测试 =======
const Test = asyncComponent(() => import('./views/test')
    .then(module => module.default), { name: 'testPage' })

// ======= 发布 =======
const PackageContainer = asyncComponent(() => import('./views/operation/package/container')
    .then(module => module.default), { name: 'packageContainer' })

export default (
    <Route path="/" component={Main}>
        <IndexRoute component={Container} />
        <Route path="/message" component={ MsgCenter }>
            <IndexRoute component={ MsgList } />
            <Route path="list" component={ MsgList } />
            <Route path="detail/:msgId" component={ MsgDetail } />
        </Route>
        <Route path="/admin" component={ SysAdmin }>
            <IndexRoute component={ AdminUser } />
            <Route path="user" component={ AdminUser } />
            <Route path="role" component={ AdminRole } />
            <Route path="role/add" component={ GRoleAdd } />
            <Route path="role/edit/:roleId" component={ GRoleEdit } />
        </Route>
        <Route path="/rdos" component={Container}>
            <IndexRoute component={Dashboard} />
            <Route path="/project/:pid" component={ProjectContainer} onEnter={isSelectedProject}>
                <IndexRoute component={ProjectConfig} />
                <Route path="config" component={ProjectConfig} />
                <Route path="member" component={ProjectMember} />
                <Route path="role" component={RoleManagement} />
                <Route path="role/add" component={RoleAdd} />
                <Route path="role/edit/:roleId" component={RoleEdit} />
            </Route>
            <Route path="/offline" component={TaskContainer} onEnter={isSelectedProject}>
                <IndexRoute component={OfflineDefault} />
                <Route path="task" component={TaskOffline} />
            </Route>
            <Route path="/view/task/:taskId" component={OpenOfflineTask} />
            <Route path="/package/:type" component={PackageContainer} onEnter={isSelectedProject} />
            <Route path="/operation" component={Operation} onEnter={isSelectedProject}>
                <IndexRoute component={OperationOverview} />
                <Route path="offline-operation" component={OpeOfflineList} />
                <Route path="offline-management" component={OpeOfflineTaskMana} />
                <Route path="task-runtime/:jobId" component={OpeOfflineTaskRunTime} />
                <Route path="task-patch-data" component={OperationPatchData} />
                <Route path="task-patch-data/:fillJobName" component={OperationPatchDataDetail} />
                <Route path="alarm-record" component={OpeAlarm} />
                <Route path="alarm-config" component={OpeAlarmConfig} />
                <Route path="dirty-data" component={DirtyData} />
                <Route path="dirty-data/table/:tableId" component={DirtyDataTbOverview} />
                <Route path="log" component={Log} />
                <Route path="log/:tableId/:tableName" component={Log}></Route>
            </Route>
            <Route path="/database" component={DataSourceContainer} onEnter={isSelectedProject}>
                <IndexRoute component={DataSourceOffline} />
                <Route path="offLineData" component={DataSourceOffline} />
                <Route path="offLineData/db-sync/:sourceId/:sourceName" component={DataSourceMigrationOffline} />
            </Route>
            <Route path="/data-manage" component={DataManageContainer}>
                <Route path="table" component={TableManage}>
                    <IndexRoute component={TableList} />
                    <Route path="view/:tableId" component={TableViewer} />
                    <Route path="edit/:tableId" component={TableEditor} />
                </Route>
                <Route path="catalogue" component={DataCatalogue} />
                <Route path="auth" component={AuthMana} />
                <Route path="search" component={SearchTable} />
                <Route path="assets" component={AssetOverview} />
                <Route path="desensitization-manage" component={DesensitizationMange} />
                <Route path="rule-manage" component={RuleManage} />

            </Route>
            <Route path="/data-model" component={DataModelContainer}>
                <IndexRoute component={DataModelOverview} />
                <Route path="overview" component={DataModelOverview} />
                <Route path="check" component={DataModelCheckCenter}></Route>
                <Route path="table" component={DataModelDesign} />
                <Route path="table/create" component={TableCreator} />
                <Route path="table/design" component={DataModelCreate} />
                <Route path="table/modify/:tableId" component={DataModelModify} />
                <Route path="config" component={DataModelConfig} />
            </Route>
        </Route>
        <Route path="/test" component={Test} />
        <Route path="/*" component={NotFund} />
    </Route>
)
