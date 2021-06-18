import React from 'react';
import './App.css';
import Workbench from './workbench/workbench';
import Navbar from './layout/nav';
import { MoleculeProvider } from 'molecule';

export const AppContainer = 'AppContainer';

function App() {
  return (
    <MoleculeProvider>
      <Navbar />
      <div id={AppContainer} className="container">
        <Workbench />
      </div>
    </MoleculeProvider>
  );
}

export default App;
