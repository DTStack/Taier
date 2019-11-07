import * as React from 'react'
import { Router } from 'react-router'
import { Provider } from 'react-redux'

// 继承主应用
import 'main/styles/comm.css'
import 'main/styles/layout.scss'
import 'main/styles/myantd.scss'

// Codemirror
import 'codemirror/lib/codemirror.css'
import 'codemirror/addon/lint/lint.css'
// import 'codemirror/addon/fold/foldgutter.css'

// Common styles
import './styles/main.scss'
import './styles/pages/layout.scss'
import './styles/pages/project.scss'
import './styles/pages/dashboard.scss'
import './styles/pages/iconfont.scss'
import './styles/themes/default.scss'
import './styles/themes/dark.scss'

import routers from './routers'
import { store, history } from './store'

export default class Root extends React.Component<any, any> {
    render () {
        return (
            <Provider store={store} >
                <Router routes={routers} history={history} key={Math.random()} />
            </Provider>
        )
    }
}
