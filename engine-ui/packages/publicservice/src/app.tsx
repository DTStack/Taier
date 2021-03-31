import React from 'react';
import ReactDOM from 'react-dom';
import { getStore } from 'lib/dt-common/src/utils/reduxUtils';
import Root from './root';
import 'assets/styles/index.less';

const render = (Component: any) => {
  const rootReducer = require('./reducers').default;
  const { store, history } = getStore(rootReducer, 'hash');
  ReactDOM.render(
    <Component store={store} history={history} />,
    document.getElementById('app')
  );
};

render(Root);

if ((module as any).hot) {
  (module as any).hot.accept(['./root'], () => {
    const newRoot = require('./root').default;
    render(newRoot);
  });
}
