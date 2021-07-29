import molecule from "molecule";
import { IExtension, SAMPLE_FOLDER_PANEL_ID } from "molecule/esm/model";
import { localize } from "molecule/esm/i18n/localize";
import { connect } from "molecule/esm/react";
import TaskInfo from "./taskInfo";
import TaskParams from "./taskParams";
import { SchedulingConfig } from "./schedulingConfig";

function changeContextMenuName() {
  const explorerData = molecule.explorer.getState().data?.concat() || [];
  const folderTreePane = explorerData.find(
    (item) => item.id === SAMPLE_FOLDER_PANEL_ID
  );
  if (folderTreePane?.toolbar) {
    folderTreePane.toolbar[0].title = "新建任务";
    molecule.explorer.setState({
      data: explorerData,
    });
  }
}

const TASK_ATTRIBUTONS = "task.attributions";
const TASK_PARAMS_ID = "task.params";
const TASK_SCHEDULE_CONFIG = "task.schedule.config";

function initTaskInfo() {
  const TaskinfoView = connect(molecule.editor, TaskInfo);

  molecule.explorer.addPanel({
    id: TASK_ATTRIBUTONS,
    name: localize(TASK_ATTRIBUTONS, "任务属性"),
    renderPanel: () => <TaskinfoView />,
  });
}

function initTaskParams() {
  const TaskParamsView = connect(molecule.editor, TaskParams);

  molecule.explorer.addPanel({
    id: TASK_PARAMS_ID,
    name: localize(TASK_PARAMS_ID, "任务参数"),
    config: {
      grow: 2,
    },
    renderPanel: () => (
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

function initSchedulingConfig() {
  const SchedulingConfigView = connect(molecule.editor, SchedulingConfig);

  molecule.explorer.addPanel({
    id: TASK_SCHEDULE_CONFIG,
    name: localize(TASK_SCHEDULE_CONFIG, "调度依赖"),
    renderPanel: () => (
      <SchedulingConfigView
        isPro={false}
        couldEdit={false}
        isScienceTask={false}
        tabData={{
          scheduleConf: '{}',
          scheduleStatus: 0
        }}
        updateKey={0}
        key={`schedule-1`}
        isIncrementMode={false}
      />
    ),
  });
}

export default class ExplorerExtensions implements IExtension {
  activate(extensionCtx: molecule.IExtensionService): void {
    changeContextMenuName();

    initTaskInfo();
    initTaskParams();
    initSchedulingConfig();
  }
}
