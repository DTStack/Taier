import React from 'react';
import { Provider } from 'react-redux'
import { getStore } from '../views/common/utils'
import Layout from '../layout/layout';
import IDE from './workbench';

import './registerMicroApps';
import './App.css';
import 'ant-design-dtinsight-theme/theme/dt-theme/default/index.less';

function App() {
  const rootReducer = require('../controller').default;
  const { store } = getStore(rootReducer, 'hash');
  return (
    <Provider store={store}>
      <Layout>
        <IDE />
      </Layout>
    </Provider>
  );
}

export default App;
