import React from 'react';
import './App.css';
import Workbench from './workbench/workbench';
import { MoleculeProvider } from 'molecule';

// import Layout from './layout/layout';
// import 'ant-design-dtinsight-theme/theme/dt-theme/default/index.less';
import { extensions } from './extensions';
import Nav from './layout/nav';

export const AppContainer = 'AppContainer';

function App() {
  return (
    <MoleculeProvider extensions={extensions}>
      <Nav />
      {/* <Layout> */}
        <div className="container">
          <Workbench />
        </div>
      {/* </Layout> */}
    </MoleculeProvider>
  );
}

export default App;
