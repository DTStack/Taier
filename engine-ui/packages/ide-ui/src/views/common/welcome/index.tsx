import molecule from 'molecule';
import React from 'react';
import { IExtension } from 'molecule/esm/model';
import './style.css';

function EditorEntry(props: any) {
    return (
        <div className="entry">
            <p style={{ fontSize: '18px' }}>DAGScheduleX</p>
        </div>
    )
}

export default class WelcomeExtension implements IExtension {
    activate() {
        // 初始化资源管理
        molecule.editor.setEntry(<EditorEntry />)
    }
}
