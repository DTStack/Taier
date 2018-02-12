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

// 数据质量
import Container from './views'
import Dashboard from './views/dashboard'
import TaskQuery from './views/taskQuery'
import RuleConfig from './views/ruleConfig'
import DataCheck from './views/dataCheck'


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
        </Route>
        <Route path="/dq" component={ Container }>
            <IndexRoute component={ Dashboard } />
            <Route path="overview" component={ Dashboard }></Route>
            <Route path="taskQuery" component={ TaskQuery }></Route>
            <Route path="rule" component={ RuleConfig }></Route>
            <Route path="dataCheck" component={ DataCheck }></Route>
            <Route path="dataSource" component={ DataCheck }></Route>
        </Route>
    </Route>
)
