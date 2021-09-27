import React from 'react';
import { extensions } from './common';
import { MoleculeProvider, Workbench } from '@dtinsight/molecule';
import '@dtinsight/molecule/esm/style/mo.css';

function IDE() {
    return (
        <MoleculeProvider extensions={extensions}>
            <Workbench />
        </MoleculeProvider>
    );
}

export default IDE;
