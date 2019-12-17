import * as React from 'react'
import { Router } from 'react-router'
import { Provider } from 'react-redux'

import 'antd/dist/antd.css'

// 继承主应用
import 'dt-common/src/styles/comm.css'
import 'dt-common/src/styles/reset.css'
import 'dt-common/src/styles/layout.scss'
import 'dt-common/src/styles/myantd.scss'

// Styles
import './styles/main.scss'

import routers from './routers'

export default class Root extends React.Component<any, any> {
    render () {
        const { store, history } = this.props
        return (
            <Provider store={store} >
                <Router routes={routers} history={history} key={Math.random()} {...{ onEnter: () => {
                    console.log('enter')
                } }} />
            </Provider>
        )
    }
}
