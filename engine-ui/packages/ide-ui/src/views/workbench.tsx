import React from 'react';
import { extensions } from './common';
import { MoleculeProvider, Workbench } from 'molecule';
import 'molecule/esm/style/mo.css';

function IDE() {
    return (
        <MoleculeProvider extensions={extensions}>
            <Workbench />
        </MoleculeProvider>
    );
}

export default IDE;
