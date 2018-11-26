import React from 'react';

import Default from './default';
import TaskModal from './taskModal';
import UploadModal from './uploadModal';
import FolderModal from './folderModal';
import FnModal from './fnModal';
import FnMoveModal from './fnMoveModal';
import FnViewModal from './fnViewModal';
import ResViewModal from './resViewModal';
import ScriptModal from './scriptModal';

import CloneTaskModal from './cloneTaskModal';
export default class Offline extends React.Component {
    render() {
        return <div>
            <CloneTaskModal></CloneTaskModal>
            <TaskModal />
            <UploadModal />
            <FolderModal />
            <FnModal />
            <FnMoveModal />
            <FnViewModal />
            <ResViewModal />
            <ScriptModal />
            <Default />
        </div>
    }
}
