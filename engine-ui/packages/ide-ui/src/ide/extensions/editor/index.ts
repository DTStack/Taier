import molecule from "molecule";
import { getEditorInitialActions, IExtension } from "molecule/esm/model";

const TASK_RUN_ID = "task.run";
const TASK_STOP_ID = "task.stop";

function initActions() {
  molecule.editor.updateGroupActions([
    {
      id: TASK_RUN_ID,
      name: "Run Task",
    },
    {
      id: TASK_STOP_ID,
      name: "Stop Task",
      disabled: true,
    },
    {
      type: "divider",
    },
    ...getEditorInitialActions(),
  ]);
}

function emitEvent() {
  molecule.editor.onActionsClick((menuId, current) => {
    switch (menuId) {
      case TASK_RUN_ID: {
        // TODO
        console.log("run task");
        break;
      }
      case TASK_STOP_ID: {
        // TODO
        console.log("stop task");
        break;
      }
    }
  });
}

export default class EditorExtension implements IExtension {
  activate() {
    initActions();
    emitEvent();
  }
}
