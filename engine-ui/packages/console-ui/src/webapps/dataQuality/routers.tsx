import * as React from 'react'
import { Route, IndexRoute, IndexRedirect } from 'react-router'

// import asyncComponent from 'utils/asyncLoad'
// import { openNewWindow } from 'funcs'

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

// 数据质量
import Container from './views'
import Dashboard from './views/dashboard'
import TaskQuery from './views/taskQuery'
import RuleConfigIndex from './views/ruleConfig/dashboard'
import RuleConfigEdit from './views/ruleConfig/edit'
import DataCheckIndex from './views/dataCheck/dashboard'
import DataCheckEdit from './views/dataCheck/edit'
import DataCheckReport from './views/dataCheck/report'
import DataSourceIndex from './views/dataSource'

// ======= 测试 =======
// const Test = asyncComponent(() => import('./views/test')
// .then((module: any) => module.default), { name: 'testPage' })

export default (
    <Route path="/" component={ Main }>
        <IndexRedirect to="/dq" />
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
        <Route path="/dq" component={ Container }>
            <IndexRoute component={ Dashboard } />
            <Route path="overview" component={ Dashboard }></Route>
            <Route path="taskQuery" component={ TaskQuery }></Route>
            <Route path="rule" component={ RuleConfigIndex }></Route>
            <Route path="rule/add" component={ RuleConfigEdit }></Route>
            <Route path="rule/edit/:id" component={ RuleConfigEdit }></Route>
            <Route path="dataCheck" component={ DataCheckIndex }></Route>
            <Route path="dataCheck/add" component={ DataCheckEdit }></Route>
            <Route path="dataCheck/edit/:verifyId" component={ DataCheckEdit }></Route>
            <Route path="dataCheck/report/:verifyRecordId" component={ DataCheckReport }></Route>
            <Route path="dataSource" component={ DataSourceIndex }></Route>
        </Route>
        <Route path="/*" component={NotFund} />
    </Route>
)
