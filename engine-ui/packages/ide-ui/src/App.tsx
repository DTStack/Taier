import React from 'react';
import Layout from './layout/layout';
import IDE from './ide/workbench';

import './registerMicroApps';
import './App.css';
import 'ant-design-dtinsight-theme/theme/dt-theme/default/index.less';

function App() {
  return (
    <Layout>
        <IDE />
    </Layout>
  );
}

export default App;
