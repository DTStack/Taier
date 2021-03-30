import * as React from 'react'
import * as ReactDOM from 'react-dom'

import { getStore } from './utils/reduxUtils'

import * as log from './utils/log';
import { LocaleProvider } from 'antd';
import zhCN from 'antd/es/locale/zh_CN';

import Root from './root';

declare var module: any;

const render = (Component: any) => {
    const rootReducer = require('./reducers').default;
    const { store, history } = getStore(rootReducer, 'hash');

    ReactDOM.render(
        <LocaleProvider locale={zhCN}>
                <Component store={store} history={history} />
        </LocaleProvider>
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

log.appInfo();
