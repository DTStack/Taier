import molecule from '@dtinsight/molecule';
import React from 'react';
import { IExtension } from '@dtinsight/molecule/esm/model';
import './style.css';

function EditorEntry(props: any) {
    return (
        <div className="entry">
            <p className="logo">DAGScheduleX</p>
        </div>
    )
}

export default class WelcomeExtension implements IExtension {
    activate() {
        // 初始化资源管理
        molecule.editor.setEntry(<EditorEntry />)
    }
}
