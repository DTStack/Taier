import React from 'react'
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
import Container from './views'
import Dashboard from './views/dashboard'

import APIApproval from './views/approval'
import DataSource from './views/dataSource'
import APIManage from './views/management'
import APIMarket from './views/market'
import MyAPI from './views/myApi'

import APIDetail from './views/market'
import APIManageDetail from './views/management/apiDetail'
import ApiType from './views/management/apiType'
import NewApi from './views/management/createApi'

// ======= 测试 =======
// const Test = asyncComponent(() => import('./views/test')
// .then(module => module.default), { name: 'testPage' })

export default (
    <Route path="/" component={ Main }>
        <IndexRedirect to="/api" />
        {/* <IndexRoute component={ Container } /> */}
        <Route path="/message" component={ MsgCenter }>
            <IndexRoute component={ MsgList } />
            <Route path="list" component={ MsgList } />
            <Route path="detail/:msgId" component={ MsgDetail } />
        </Route>
        <Route path="/admin" component={ SysAdmin }>
            <IndexRoute component={ AdminUser } />
            <Route path="user" component={ AdminUser } />
            <Route path="role" component={ AdminRole } />
            <Route path="role/add" component={ RoleAdd } />
            <Route path="role/edit/:roleId" component={ RoleEdit } />
        </Route>
        <Route path="/api" component={ Container }>
            <IndexRoute component={ Dashboard } />
            <Route path="overview" component={ Dashboard }></Route>
            <Route path="approval" component={ APIApproval }></Route>
            <Route path="manage" component={ APIManage }></Route>
            <Route path="manage/detail/:api" component={ APIManageDetail }></Route>
            <Route path="manage/apiType" component={ ApiType }></Route>
            <Route path="manage/newApi" component={ NewApi }></Route>
            <Route path="market" component={ APIMarket }></Route>
            <Route path="market/detail/:api" component={ APIDetail }></Route>
            <Route path="mine" component={ MyAPI }></Route>
            <Route path="mine/:view" component={ MyAPI }></Route>
            <Route path="dataSource" component={ DataSource }></Route>
        </Route>
        <Route path="/*" component={NotFund} />
    </Route>
)
