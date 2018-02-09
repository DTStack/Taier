import React from 'react'
import { Route, IndexRoute, Redirect } from 'react-router'

import asyncComponent from 'utils/asyncLoad'
import { openNewWindow } from 'funcs'

import NotFund from 'widgets/notFund'

import Container from './views'
import Dashboard from './views/dashboard'

import MsgCenter from './views/message'
import MsgList from './views/message/list'
import MsgDetail from './views/message/detail'

import SysAdmin from './views/admin'
import AdminUser from './views/admin/user'
import AdminRole from './views/admin/role'

// ======= 测试 =======
// const Test = asyncComponent(() => import('./views/test')
// .then(module => module.default), { name: 'testPage' })

export default (
    <Route path="/" component={ Container }>
        <IndexRoute component={ Dashboard } />
        <Route path="/index.html" component={ Dashboard }></Route>
        <Route path="/message" component={ MsgCenter }>
            <IndexRoute component={ MsgList } />
            <Route path="list" component={ MsgList } />
            <Route path="detail/:msgId" component={ MsgDetail } />
        </Route>
        <Route path="/admin" component={ SysAdmin }>
            <IndexRoute component={ AdminUser } />
            <Route path="user" component={ AdminUser } />
            <Route path="role" component={ AdminRole } />
        </Route>
        {/* <Route path="/rdos" onEnter={() => openNewWindow('/rdos.html')}/> */}
        {/* <Route path="/project/:pid" ></Route> */}
        {/* <Redirect from="/*" to="/"/> */}
    </Route>
)
