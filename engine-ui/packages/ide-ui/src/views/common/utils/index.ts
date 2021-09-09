import molecule from 'molecule/esm';
import {
    FileTypes,
    IStatusBarItem,
    TreeNodeModel,
    Float,
} from 'molecule/esm/model';
import { TASK_RUN_ID, TASK_STOP_ID } from './const';

export function resetEditorGroup() {
    molecule.editor.updateActions([
        { id: TASK_RUN_ID, disabled: true },
        { id: TASK_STOP_ID, disabled: true },
    ]);
}

export function updateStatusBarLanguage(item: IStatusBarItem) {
    const states = molecule.statusBar.getState();
    const languageStatus = states.rightItems.find(
        (item) => item.id === 'language'
    );
    if (languageStatus) {
        molecule.statusBar.update(item);
    } else {
        molecule.statusBar.add(item, Float.right);
    }
}

export function convertToTreeNode(data: any[]) {
    if (!data) {
        return;
    }
    return data.map((child) => {
        const { id, name, children, type } = child;
        const node: TreeNodeModel = new TreeNodeModel({
            id,
            name: !name ? '数据开发' : name,
            location: name,
            fileType: type === 'folder' ? FileTypes.Folder : FileTypes.File,
            isLeaf: type !== 'folder',
            data: child,
            children: convertToTreeNode(children),
        });

        return node;
    });
}
