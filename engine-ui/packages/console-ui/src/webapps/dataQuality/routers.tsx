import * as React from 'react'
import { Route, IndexRoute } from 'react-router'
import asyncComponent from 'utils/asyncLoad'
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
import GRoleAdd from 'main/views/admin/role/add'
import GRoleEdit from 'main/views/admin/role/edit'

// 数据质量
import Container from './views/container'
import Dashboard from './views/dashboard'
import TaskQuery from './views/taskQuery'
import RuleConfigIndex from './views/ruleConfig/dashboard'
import RuleConfigEdit from './views/ruleConfig/edit'
import DataCheckIndex from './views/dataCheck/dashboard'
import DataCheckEdit from './views/dataCheck/edit'
import DataCheckReport from './views/dataCheck/report'
import DataSourceIndex from './views/dataSource'

import ProjectList from './views/projectPanel/projectList';

// ======= 项目 =======
import ProjectConfig from './views/project/config'
import ProjectMember from './views/project/member'
import RoleManagement from './views/project/role'
import RoleAdd from './views/project/role/add'
import RoleEdit from './views/project/role/edit'

import { isSelectedProject } from './interceptor'

// ======= 测试 =======
// const Test = asyncComponent(() => import('./views/test')
// .then((module: any) => module.default), { name: 'testPage' })

const ProjectContainer = asyncComponent(() => import('./views/project/container')
    .then((module: any) => module.default), { name: 'projectContainer' })

export default (
    <Route path="/" component={ Main }>
        {/* <IndexRedirect to="/dq" /> */}
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
            <Route path="role/add" component={ GRoleAdd } />
            <Route path="role/edit/:roleId" component={ GRoleEdit } />
        </Route>
        <Route path="/dq" component={ Container }>
            <IndexRoute component={Dashboard} />
            <Route path="overview" component={ Dashboard } onEnter={isSelectedProject}></Route>
            <Route path="taskQuery" component={ TaskQuery } onEnter={isSelectedProject}></Route>
            <Route path="rule" component={ RuleConfigIndex } onEnter={isSelectedProject}></Route>
            <Route path="rule/add" component={ RuleConfigEdit } onEnter={isSelectedProject}></Route>
            <Route path="rule/edit/:id" component={ RuleConfigEdit } onEnter={isSelectedProject}></Route>
            <Route path="dataCheck" component={ DataCheckIndex } onEnter={isSelectedProject}></Route>
            <Route path="dataCheck/add" component={ DataCheckEdit } onEnter={isSelectedProject}></Route>
            <Route path="dataCheck/edit/:verifyId" component={ DataCheckEdit } onEnter={isSelectedProject}></Route>
            <Route path="dataCheck/report/:verifyRecordId" component={ DataCheckReport } onEnter={isSelectedProject}></Route>
            <Route path="dataSource" component={ DataSourceIndex } onEnter={isSelectedProject}></Route>
            <Route path="project/:pid" component={ProjectContainer} onEnter={isSelectedProject}>
                <IndexRoute component={ProjectConfig} />
                <Route path="config" component={ProjectConfig} />
                <Route path="member" component={ProjectMember} />
                <Route path="role" component={RoleManagement} />
                <Route path="role/add" component={RoleAdd} />
                <Route path="role/edit/:roleId" component={RoleEdit} />
            </Route>
            <Route path='projectList' component={ProjectList} />
        </Route>
        <Route path="/*" component={NotFund} />
    </Route>
)
