import React from 'react';
import './App.css';
import Workbench from './workbench/workbench';
import { MoleculeProvider } from 'molecule';

import Layout from './layout/layout';
import 'ant-design-dtinsight-theme/theme/dt-theme/default/index.less';
import { extensions } from './extensions';

export const AppContainer = 'AppContainer';

function App() {
  return (
    <MoleculeProvider extensions={extensions}>
      <Layout>
          <Workbench />
      </Layout>
    </MoleculeProvider>
  );
}

export default App;
