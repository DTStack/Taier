import * as React from 'react'
import * as ReactDOM from 'react-dom'
import { getStore } from 'utils/reduxUtils'
import ErrorBoundary from './components/errorBoundary';
import Root from './root'

const render = (Component: any) => {
    const rootReducer = require('./reducers').default;
    const { store, history } = getStore(rootReducer, 'hash');
    ReactDOM.render(
        <ErrorBoundary>
            <Component store={store} history={history} />
        </ErrorBoundary>
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
