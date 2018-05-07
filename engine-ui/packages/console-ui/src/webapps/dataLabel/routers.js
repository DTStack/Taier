import React from 'react'
import { Route, IndexRoute, Redirect } from 'react-router'

import asyncComponent from 'utils/asyncLoad'
import { openNewWindow } from 'funcs'

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

// 标签工厂
import Container from './views'
import TagMarket from './views/market'
import MyAPI from './views/myApi'
import TagManagement from './views/management'
import TagApproval from './views/approval'

import TagConfig from './views/tagConfig'
import IdentifyColumn from './views/tagConfig/identifyColumn'
import DataSourceIndex from './views/dataSource'

// ======= 测试 =======
// const Test = asyncComponent(() => import('./views/test')
// .then(module => module.default), { name: 'testPage' })

export default (
    <Route path="/" component={ Main }>
        <IndexRoute component={ Container } />
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
        <Route path="/dl" component={ Container }>
            <IndexRoute component={ TagMarket } />
            <Route path="market" component={ TagMarket }></Route>
            <Route path="mine" component={ MyAPI }></Route>
            <Route path="approval" component={ TagApproval }></Route>
            <Route path="management" component={ TagManagement }></Route>
            <Route path="tagConfig" component={ TagConfig }></Route>
            <Route path="tagConfig/identify" component={ IdentifyColumn }></Route>
            <Route path="dataSource" component={ DataSourceIndex }></Route>
        </Route>
    </Route>
)
