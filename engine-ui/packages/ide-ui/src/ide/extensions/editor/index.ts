import molecule from "molecule";
import { getEditorInitialActions, IExtension } from "molecule/esm/model";
import { searchById } from "molecule/esm/services/helper";
import { resetEditorGroup } from "../common";

export const TASK_RUN_ID = "task.run";
export const TASK_STOP_ID = "task.stop";

function initActions() {
  molecule.editor.setDefaultActions([
    {
      id: TASK_RUN_ID,
      name: "Run Task",
      icon: "play",
      place: "outer",
      disabled: true,
    },
    {
      id: TASK_STOP_ID,
      name: "Stop Task",
      icon: "debug-pause",
      place: "outer",
      disabled: true,
    },
    ...getEditorInitialActions()
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

    molecule.editor.onSelectTab((tabId, groupId) => {
      const { current } = molecule.editor.getState();
      if (!current) return;
      const group = molecule.editor.getGroupById(groupId || current.id!);
      if (group) {
        const targetTab = group.data?.find(searchById(tabId));
        if (targetTab?.data.taskType === "SparkSql") {
          molecule.editor.updateActions([{ id: TASK_RUN_ID, disabled: false }]);
        } else {
          resetEditorGroup();
        }
      }
    });

    molecule.editor.onCloseTab(() => {
      const { current } = molecule.editor.getState();
      if (current?.tab?.data.taskType === "SparkSql") {
        molecule.editor.updateActions([{ id: TASK_RUN_ID, disabled: false }]);
      } else {
        resetEditorGroup();
      }
    });
  }
}
