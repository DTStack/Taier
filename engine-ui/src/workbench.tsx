import React from 'react';
import { Workbench } from 'molecule/esm/workbench';
import { MoleculeProvider } from 'molecule/esm/provider/molecule';
import 'molecule/esm/style/mo.css';

function App() {
  return (
    <MoleculeProvider>
      <Workbench />
    </MoleculeProvider>
  )
}

export default App;
