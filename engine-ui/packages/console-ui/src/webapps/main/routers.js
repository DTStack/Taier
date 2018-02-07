import React from 'react'
import { Route, IndexRoute, Redirect } from 'react-router'

import asyncComponent from 'utils/asyncLoad'
import { openNewWindow } from 'funcs'

import NotFund from 'widgets/notFund'

import Container from './views'
import Dashboard from './views/dashboard'


// ======= 测试 =======
// const Test = asyncComponent(() => import('./views/test')
// .then(module => module.default), { name: 'testPage' })

export default (
    <Route path="/" component={ Container }>
        <IndexRoute component={Dashboard} />
        <Route path="/index.html" component={Dashboard}></Route>
        {/* <Route path="/rdos" onEnter={() => openNewWindow('/rdos.html')}/> */}
        <Route path="/project/:pid" ></Route>
        {/* <Route path="/test" component={Test} /> */}
        {/* <Redirect from="/*" to="/"/> */}
    </Route>
)
