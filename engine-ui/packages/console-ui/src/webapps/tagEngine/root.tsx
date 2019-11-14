import * as React from 'react'
import { Router } from 'react-router'
import { Provider } from 'react-redux'

// 继承主应用
import 'main/styles/comm.css'
import 'main/styles/layout.scss'
import 'main/styles/myantd.scss'
// Codemirror
import './styles/codemirror';
// Common styles
import './styles/main.scss'

import './styles/pages/project.scss'

import routers from './routers'

export default class Root extends React.Component<any, any> {
    render () {
        const { store, history } = this.props
        return (
            <Provider store={store} >
                <Router routes={routers} history={history}/>
            </Provider>
        )
    }
}
