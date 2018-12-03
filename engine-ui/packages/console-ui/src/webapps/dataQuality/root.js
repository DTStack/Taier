import React from 'react'
// import ReactDOM from 'react-dom'
import { Router } from 'react-router'
import { Provider } from 'react-redux'

// 继承主应用
import 'main/styles/comm.css'
import 'main/styles/reset.css'
import 'main/styles/layout.scss'
import 'main/styles/myantd.scss'

// Styles
import './styles/main.scss'

import routers from './routers'

export default class Root extends React.Component {
    render () {
        const { store, history } = this.props
        return (
            <Provider store={store} >
                <Router routes={routers} history={history} key={Math.random()} />
            </Provider>
        )
    }
}
