import * as React from 'react'
import * as ReactDOM from 'react-dom'
import { AppContainer } from 'react-hot-loader'
import { getStore } from 'utils/reduxUtils'
import Root from './root'

const render = (Component: any) => {
    const rootReducer = require('./reducers').default;
    const { store, history } = getStore(rootReducer, 'hash');

    ReactDOM.render(
        <AppContainer>
            <Component store={store} history={history} />
        </AppContainer>
        , document.getElementById('app')
    )
}

render(Root)

if ((module as any).hot) {
    (module as any).hot.accept(['./root'], () => {
        const newRoot = require('./root').default;
        render(newRoot)
    })
}
