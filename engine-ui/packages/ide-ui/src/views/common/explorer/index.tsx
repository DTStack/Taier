import React from 'react';
import molecule from '@dtinsight/molecule';
import { IExtension, SAMPLE_FOLDER_PANEL_ID } from '@dtinsight/molecule/esm/model';
import { localize } from '@dtinsight/molecule/esm/i18n/localize';
import { connect } from '@dtinsight/molecule/esm/react';
import TaskInfo from '../../task/taskInfo';
import { TASK_ATTRIBUTONS } from '../utils/const';

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

        // 右键菜单也需要修改
        const contextMenu = molecule.folderTree.getFolderContextMenu().concat();
        contextMenu[0].name = '新建任务';
        molecule.folderTree.setFolderContextMenu(contextMenu);
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
