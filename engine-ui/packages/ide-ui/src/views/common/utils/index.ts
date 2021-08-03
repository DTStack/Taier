import molecule from "molecule/esm";
import { TASK_RUN_ID, TASK_STOP_ID } from "./const";

export function resetEditorGroup() {
  molecule.editor.updateActions([
    { id: TASK_RUN_ID, disabled: true },
    { id: TASK_STOP_ID, disabled: true },
  ]);
}
