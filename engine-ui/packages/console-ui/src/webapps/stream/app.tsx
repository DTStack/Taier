import * as React from 'react'
import * as ReactDOM from 'react-dom'
import { AppContainer } from 'react-hot-loader'

import Root from './root'

const render = (Component: any) => {
    ReactDOM.render(
        <AppContainer>
            <Component />
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
