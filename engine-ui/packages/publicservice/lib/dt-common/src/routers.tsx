import * as React from 'react'
import { Route, IndexRoute } from 'react-router'

import { NotFound } from 'dt-react-component'

import Container from './views'

import MsgCenter from './views/message'
import MsgList from './views/message/list'
import MsgDetail from './views/message/detail'

import SysAdmin from './views/admin'
import AdminUser from './views/admin/user'
import AdminRole from './views/admin/role'
import RoleAdd from './views/admin/role/add'
import RoleEdit from './views/admin/role/edit'
import Audit from './views/admin/audit'

export default (
    <Route path="/" component={Container}>
        <IndexRoute component={SysAdmin} />

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
            <Route path="audit" component={Audit} />
        </Route>
        <Route path="/*" component={NotFound} />
    </Route>
)
