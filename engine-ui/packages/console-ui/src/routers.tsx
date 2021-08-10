import * as React from 'react'
import { Route, IndexRoute, IndexRedirect } from 'react-router'
import { NotFound } from 'dt-react-component'
import asyncComponent from 'dt-common/src/utils/asyncLoad'

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

import Container from './views'
import QueueManage from './views/queueManage'
import ResourceManage from './views/resourceManage'
import ClusterManage from './views/clusterManage'
import EditCluster from './views/clusterManage/newEdit'
import QueueManageDetail from './views/queueManage/taskDetail'
import AlarmChannel from './views/alarmChannel';
import AlarmRule from './views/alarmChannel/alarmRule'
import AlarmConfig from './views/alarmChannel/alarmConfig'

// ======= 测试 =======
// const Test = asyncComponent(() => import('./views/test')
// .then((module: any) => module.default), { name: 'testPage' })

// 运维中心
import { isSelectedProject } from './interceptor'
import OpeOfflineTaskMana from './views/operation/offline/taskMana'
import OpeOfflineList from './views/operation/offline/taskOperation'
import OperationPatchData from './views/operation/offline/patchDataList'
import OperationPatchDataDetail from './views/operation/offline/patchDataDetail'

const Operation = asyncComponent(() => import('./views/operation/container')
    .then((module: any) => module.default), { name: 'operationPage' })

export default (
    <Route path="/" component={Main}>
        <IndexRedirect to="/console" />
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
        <Route path="/console" component={Container}>
            <IndexRoute component={QueueManage} />
            <Route path="queueManage" component={QueueManage} />
            <Route path="queueManage/detail" component={QueueManageDetail} />
            <Route path="resourceManage" component={ResourceManage} />
            <Route path="clusterManage" component={ClusterManage} />
            <Route path="clusterManage/editCluster" component={EditCluster} />
            <Route path='alarmChannel' component={AlarmChannel} />
            <Route path='alarmChannel/alarmRule' component={AlarmRule} />
            <Route path='alarmChannel/AlarmConfig' component={AlarmConfig} />
        </Route>
        <Route path="/operation" component={Operation} onEnter={isSelectedProject}>
            <IndexRoute component={OpeOfflineTaskMana} />
            <Route path="offline-management" component={OpeOfflineTaskMana} />
            <Route path="offline-operation" component={OpeOfflineList} />
            <Route path="task-patch-data" component={OperationPatchData} />
            <Route path="task-patch-data/detail" component={OperationPatchDataDetail} />
        </Route>
        <Route path="/*" component={NotFound} />
    </Route>
)
