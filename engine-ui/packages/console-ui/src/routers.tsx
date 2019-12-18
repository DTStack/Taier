import * as React from 'react'
import { Route, IndexRoute, IndexRedirect } from 'react-router'
import NotFund from 'dt-common/src/widgets/notFund'
// 继承主应用的的公共View组件
import Main from 'dt-common/src/views'
import MsgCenter from 'dt-common/src/views/message'
import MsgList from 'dt-common/src/views/message/list'
import MsgDetail from 'dt-common/src/views/message/detail'
import SysAdmin from 'dt-common/src/views/admin'
import AdminUser from 'dt-common/src/views/admin/user'
import AdminRole from 'dt-common/src/views/admin/role'
import RoleAdd from 'dt-common/src/views/admin/role/add'
import RoleEdit from 'dt-common/src/views/admin/role/edit'
import Audit from 'dt-common/src/views/admin/audit'

// 数据API
import Container from './views'
import QueueManage from './views/queueManage'
import ResourceManage from './views/resourceManage'
import ClusterManage from './views/clusterManage'
import EditCluster from './views/clusterManage/edit'

// ======= 测试 =======
// const Test = asyncComponent(() => import('./views/test')
// .then((module: any) => module.default), { name: 'testPage' })

export default (
    <Route path="/" component={ Main }>
        <IndexRedirect to="/console" />
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
            <Route path="audit" component={ Audit } />
        </Route>
        <Route path="/console" component={ Container }>
            <IndexRoute component={ QueueManage } />
            <Route path="queueManage" component={QueueManage} />
            <Route path="resourceManage" component={ ResourceManage } />
            <Route path="clusterManage" component={ClusterManage} />
            <Route path="clusterManage/editCluster" component={ EditCluster } />
        </Route>
        <Route path="/*" component={NotFund} />
    </Route>
)
