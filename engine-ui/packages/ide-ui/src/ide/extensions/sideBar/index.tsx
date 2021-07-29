import { FileTypes, IExtension, TreeNodeModel } from "molecule/esm/model";
import molecule from "molecule/esm";
import { connect } from "molecule/esm/react";
import TaskParams from "./taskParams";

const TASK_PARAMS_ID = "task.params";

function initTaskParams() {
  molecule.activityBar.addBar({
    id: TASK_PARAMS_ID,
    name: "任务参数",
    iconName: "codicon-history",
  });

  // molecule.editor.editorInstance;

  const TaskParamsView = connect(molecule.editor, TaskParams);

  molecule.sidebar.addPane({
    id: TASK_PARAMS_ID,
    title: "任务参数",
    render: () => (
      <TaskParamsView
        tabData={{
          taskVariables: [
            {
              paramName: "$system",
              paramCommand: "xiuneng",
              type: 0,
            },
            {
              paramName: "$key",
              paramCommand: "",
              type: 1,
            },
          ],
        }}
      />
    ),
  });
}

export default class SideBarExtension implements IExtension {
  activate() {
    initTaskParams();
  }
}
