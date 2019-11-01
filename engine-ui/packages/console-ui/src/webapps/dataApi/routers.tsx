import * as React from 'react'
import { Route, IndexRoute, IndexRedirect } from 'react-router'

import NotFund from 'widgets/notFund'

// 继承主应用的的公共View组件
import Main from 'main/views'
import MsgCenter from 'main/views/message'
import MsgList from 'main/views/message/list'
import MsgDetail from 'main/views/message/detail'

import SysAdmin from 'main/views/admin'
import AdminUser from 'main/views/admin/user'
import AdminRole from 'main/views/admin/role'
import RoleAdd from 'main/views/admin/role/add'
import RoleEdit from 'main/views/admin/role/edit'

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
            <Route path="role/add" component={RoleAdd} />
            <Route path="role/edit/:roleId" component={RoleEdit} />
        </Route>
        <Route path="/api" component={Container}>
            <IndexRoute component={Dashboard} />
            <Route path="overview" component={Dashboard}></Route>
            <Route path="approvalAndsecurity" component={SecurityContainer}>
                <IndexRoute component={Approval} />
                <Route path="approval" component={Approval}></Route>
                <Route path="security" component={Security}></Route>
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
        </Route>
        <Route path="/*" component={NotFund} />
    </Route>
)
