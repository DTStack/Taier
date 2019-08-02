import * as React from 'react'
import { Router } from 'react-router'
import { Provider } from 'react-redux'

// Styles
import './styles/main.scss'

import routers from './routers'

export default class Root extends React.Component<any, any> {
    render () {
        const { store, history } = this.props

        return (
            <Provider store={store} >
                <Router routes={routers} history={history} key={Math.random()} />
            </Provider>
        )
    }
}
