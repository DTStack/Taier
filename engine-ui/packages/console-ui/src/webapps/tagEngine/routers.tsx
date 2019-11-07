import * as React from 'react'
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

import DataSourceStream from './views/dataSource/stream';
import EntityManage from './views/entityManagement';

// The below is async load components
// ======= 项目 =======
// const Dashboard = asyncComponent(() => import('././views/dashboard')
// .then((module: any) => module.default), { name: 'dashboard' })

const ProjectContainer = asyncComponent(() => import('./views/project/container')
    .then((module: any) => module.default), { name: 'projectContainer' })

// ======= 数据源管理 =======
const DataSourceContainer = asyncComponent(() => import('./views/dataSource/container')
    .then((module: any) => module.default), { name: 'dataSourceContainer' })

// ======= 测试 =======
const Test = asyncComponent(() => import('./views/test')
    .then((module: any) => module.default), { name: 'testPage' })

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
        <Route path="/tagEngine" component={Container}>
            <IndexRoute component={EntityManage} />
            <Route path="/project/:pid" component={ProjectContainer} onEnter={isSelectedProject}>
                <IndexRoute component={ProjectConfig} />
                <Route path="config" component={ProjectConfig} />
                <Route path="member" component={ProjectMember} />
                <Route path="role" component={RoleManagement} />
                <Route path="role/add" component={RoleAdd} />
                <Route path="role/edit/:roleId" component={RoleEdit} />
            </Route>
            <Route path="/database" component={DataSourceContainer} onEnter={isSelectedProject}>
                <IndexRoute component={DataSourceStream} />
                <Route path="streamData" component={DataSourceStream} />
            </Route>
        </Route>
        <Route path="/test" component={Test} />
        <Route path="/*" component={NotFund} />
    </Route>
)
