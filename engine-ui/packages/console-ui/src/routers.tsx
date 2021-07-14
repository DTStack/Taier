import * as React from 'react'
import { Route, IndexRoute, IndexRedirect } from 'react-router'
import { NotFound } from 'dt-react-component'
// 继承主应用的的公共View组件

// 数据API
import Container from './views'
import QueueManage from './views/queueManage'
import ResourceManage from './views/resourceManage'
import ClusterManage from './views/clusterManage'
import EditCluster from './views/clusterManage/newEdit'
import QueueManageDetail from './views/queueManage/taskDetail'
import AlarmChannel from './views/alarmChannel';
import AlarmRule from './views/alarmChannel/alarmRule'
import AlarmConfig from './views/alarmChannel/alarmConfig'

export default (
    <Route path="/" >
        <IndexRedirect to="/console-ui" />
        <Route path="/console-ui" component={ Container }>
            <IndexRoute component={ QueueManage } />
            <Route path="queueManage" component={QueueManage} />
            <Route path="queueManage/detail" component={QueueManageDetail} />
            <Route path="resourceManage" component={ ResourceManage } />
            <Route path="clusterManage" component={ClusterManage} />
            <Route path="clusterManage/editCluster" component={ EditCluster } />
            <Route path='alarmChannel' component={AlarmChannel} />
            <Route path='alarmChannel/alarmRule' component={AlarmRule} />
            <Route path='alarmChannel/AlarmConfig' component={AlarmConfig} />
        </Route>
        <Route path="/*" component={NotFound} />
    </Route>
)
