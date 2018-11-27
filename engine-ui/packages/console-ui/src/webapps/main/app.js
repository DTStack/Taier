import React from 'react'
import ReactDOM from 'react-dom'
import { AppContainer } from 'react-hot-loader'

import { getStore } from 'utils/reduxUtils'

import Root from './root'

const render = (Component) => {
    const rootReducer = require('./reducers').default;
    const { store, history } = getStore(rootReducer);

    ReactDOM.render(
        <AppContainer>
            <Component store={store} history={history} />
        </AppContainer>
        , document.getElementById('app')
    )
}

render(Root)

if (module.hot) {
    module.hot.accept(['./root'], () => {
        const newRoot = require('./root').default;

        render(newRoot)
    })
}
