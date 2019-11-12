import * as React from 'react'
import { Route, IndexRoute } from 'react-router'
import asyncComponent from 'utils/asyncLoad'
// import { isSelectedProject } from './interceptor'
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

// 数据API
import Container from './views/container'
import Dashboard from './views/dashboard'

import Approval from './views/approval'
import SecurityContainer from './views/approval/container'
import Security from './views/approval/security'

import DataSource from './views/dataSource'
import APIManage from './views/management'
import APIMarket from './views/market'

import MyApiContainer from './views/myApi/container'
import MyAPI from './views/myApi'
import CallApi from './views/myApi/callApi'

import ApiType from './views/management/apiType'
import NewApi from './views/management/createApi'
import ProjectList from './views/projectPanel/projectList';

// ======= 项目管理 =======
import ProjectConfig from './views/projectManage/config'
import ProjectMember from './views/projectManage/member'
import RoleManagement from './views/projectManage/role'
import RoleAdd from './views/projectManage/role/add'
import RoleEdit from './views/projectManage/role/edit'

// The below is async load components
// ======= 项目 =======
// const Dashboard = asyncComponent(() => import('././views/dashboard')
// .then((module: any) => module.default), { name: 'dashboard' })
const ProjectContainer = asyncComponent(() => import('./views/projectManage/container')
    .then((module: any) => module.default), { name: 'projectContainer' })

// ======= 测试 =======
// const Test = asyncComponent(() => import('./views/test')
// .then((module: any) => module.default), { name: 'testPage' })

export default (
    <Route path="/" component={Main}>
        <IndexRoute component={ Container } />
        <Route path="/message" component={MsgCenter}>
            <IndexRoute component={MsgList} />
            <Route path="list" component={MsgList} />
            <Route path="detail/:msgId" component={MsgDetail} />
        </Route>
        <Route path="/admin" component={SysAdmin}>
            <IndexRoute component={AdminUser} />
            <Route path="user" component={AdminUser} />
            <Route path="role" component={AdminRole} />
            <Route path="role/add" component={GRoleAdd} />
            <Route path="role/edit/:roleId" component={GRoleEdit} />
        </Route>
        <Route path="/api" component={Container}>
            <IndexRoute component={Dashboard} />
            <Route path="overview" component={Dashboard}></Route>
            <Route path="approvalAndsecurity" component={SecurityContainer}>
                <IndexRoute component={Approval} />
                <Route path="approval" component={Approval}></Route>
                <Route path="security" component={Security}></Route>
            </Route>
            <Route path="project/:pid" component={ProjectContainer}>
                <IndexRoute component={ProjectConfig} />
                <Route path="config" component={ProjectConfig} />
                <Route path="member" component={ProjectMember} />
                <Route path="role" component={RoleManagement} />
                <Route path="role/add" component={RoleAdd} />
                <Route path="role/edit/:roleId" component={RoleEdit} />
            </Route>
            <Route path="manage" component={APIManage}></Route>
            <Route path="manage/apiType" component={ApiType}></Route>
            <Route path="manage/newApi" component={NewApi}></Route>
            <Route path="market" component={APIMarket}></Route>
            <Route path="mine" component={MyApiContainer}>
                <IndexRoute component={MyAPI} />
                <Route path="myApi" component={MyAPI}>
                    <Route path=":view" component={MyAPI}></Route>
                </Route>
                <Route path="callApi" component={CallApi}></Route>
            </Route>
            <Route path="dataSource" component={DataSource}></Route>
            {/* 项目列表 */}
            <Route path='projectList' component={ProjectList} />
        </Route>
        <Route path="/*" component={NotFund} />
    </Route>
)
