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

// 数据质量
import Container from './views';
import Workbench from './views/workbench';
import Index from './views/index/';
import Welcome from './views/index/welcome';
import ProjectList from './views/index/projectsList';
import Operation from './views/operation';
import Source from './views/source';

// ======= 测试 =======
// const Test = asyncComponent(() => import('./views/test')
// .then((module: any) => module.default), { name: 'testPage' })
// import GraphView from './views/workbench/mainBench/panelGroup/graphPanel/graphContainer'

export default (
    <Route path="/" component={Main}>
        <IndexRedirect to="/science" />
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
        <Route path="/science" component={Container}>
            {/* <IndexRedirect to='index' /> */}
            <IndexRoute component={Index} />
            <Route path='index' component={Index}>
                <IndexRoute component={Welcome} />
                <Route path='welcome' component={Welcome} />
                <Route path='projectList' component={ProjectList} />
            </Route>
            <Route path='workbench' component={Workbench} />
            <Route path='source' component={Source} />
            <Route path='operation' component={Operation} />
            {/* <Route path='test' component={GraphView} /> */}
        </Route>
        <Route path="/*" component={NotFund} />
    </Route>
)
