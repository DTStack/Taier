import molecule from 'molecule/esm';
import type { IStatusBarItem } from 'molecule/esm/model';
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
        molecule.statusBar.add(item, 'right');
    }
}
