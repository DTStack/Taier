import React from 'react';
import molecule from 'molecule';
import { IExtension, SAMPLE_FOLDER_PANEL_ID } from 'molecule/esm/model';
import { localize } from 'molecule/esm/i18n/localize';
import { connect } from 'molecule/esm/react';
import TaskInfo from '../../task/taskInfo';

import {
    TASK_ATTRIBUTONS,
} from '../utils/const';

function changeContextMenuName() {
    const explorerData = molecule.explorer.getState().data?.concat() || [];
    const folderTreePane = explorerData.find(
        (item) => item.id === SAMPLE_FOLDER_PANEL_ID
    );
    if (folderTreePane?.toolbar) {
        folderTreePane.toolbar[0].title = '新建任务';
        molecule.explorer.setState({
            data: explorerData,
        });
    }
}

function initTaskInfo() {
    const TaskinfoView = connect(molecule.editor, TaskInfo);

    molecule.explorer.addPanel({
        id: TASK_ATTRIBUTONS,
        name: localize(TASK_ATTRIBUTONS, '任务属性'),
        renderPanel: () => <TaskinfoView />,
    });
}

export default class ExplorerExtensions implements IExtension {
    activate(extensionCtx: molecule.IExtensionService): void {
        changeContextMenuName();

        initTaskInfo();
    }
}
