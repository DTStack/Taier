import axios from "axios";
import molecule from "molecule";
import {
  getEditorInitialActions,
  IExtension,
  PANEL_OUTPUT,
} from "molecule/esm/model";
import { searchById } from "molecule/esm/services/helper";
import { resetEditorGroup } from "../common";
import Result from "./result";

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
    ...getEditorInitialActions(),
  ]);
}

function emitEvent() {
  molecule.editor.onActionsClick(async (menuId, current) => {
    switch (menuId) {
      case TASK_RUN_ID: {
        // TODO
        const value = current.tab?.data.value || "";
        if (value) {
          molecule.editor.updateActions([
            {
              id: TASK_RUN_ID,
              icon: "loading~spin",
              disabled: true,
            },
            {
              id: TASK_STOP_ID,
              disabled: false,
            },
          ]);

          const { data } = molecule.panel.getState();
          molecule.panel.setState({
            current: data?.find((item) => item.id === PANEL_OUTPUT),
          });

          const nowDate = new Date();
          molecule.panel.appendOutput(
            `${nowDate.getHours()}:${nowDate.getMinutes()}:${nowDate.getSeconds()}<info>正在提交...` +
              "\n"
          );

          // mock sleeping
          await new Promise<void>((resolve) => {
            setTimeout(() => {
              resolve();
            }, 2000);
          });

          molecule.panel.appendOutput(
            `${nowDate.getHours()}:${nowDate.getMinutes()}:${nowDate.getSeconds()}<info>第1条任务开始执行` +
              "\n"
          );

          molecule.panel.appendOutput(`===========任务信息===========${"\n"}`);
          molecule.panel.appendOutput(`show tables${"\n"}`);
          molecule.panel.appendOutput(`============================${"\n"}`);
          axios
            .post("api/rdos/batch/batchJob/startSqlImmediately")
            .then(({ data }) => {
              const res = data.data;
              if (res.success) {
                const nowDate = new Date();
                molecule.panel.appendOutput(
                  `${nowDate.getHours()}:${nowDate.getMinutes()}:${nowDate.getSeconds()}<info>执行完成!` +
                    "\n"
                );
                const resultTable = res.data;

                molecule.panel.open({
                  id: new Date().getTime().toString(),
                  name: "结果1",
                  renderPane: () => <Result data={resultTable.result} />,
                });
              }
            })
            .finally(() => {
              molecule.editor.updateActions([
                {
                  id: TASK_RUN_ID,
                  icon: "play",
                  disabled: false,
                },
                {
                  id: TASK_STOP_ID,
                  disabled: true,
                },
              ]);
            });
        }
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
