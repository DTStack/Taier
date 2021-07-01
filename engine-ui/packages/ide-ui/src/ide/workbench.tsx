import React from 'react';
import { MoleculeProvider, Workbench } from 'molecule';
import 'molecule/esm/style/mo.css';
import { extensions } from './extensions';

function IDE() {
  return (
    <MoleculeProvider extensions={extensions}>
      <Workbench />
    </MoleculeProvider>
  )
}

export default IDE;
