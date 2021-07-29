import { FileTypes, IExtension, TreeNodeModel } from "molecule/esm/model";
import { localize } from "molecule/esm/i18n/localize";
import molecule from "molecule/esm";
import Open from "./open";
import { TASK_RUN_ID } from "../editor";
import { resetEditorGroup } from "../common";
import ajax from "../../api";

function init() {
  ajax
    .getOfflineCatalogue({
      isGetFile: !!1,
      nodePid: 0,
    })
    .then((res) => {
      if (res.code === 1) {
        const { id, name } = res.data;
        const node = new TreeNodeModel({
          id,
          name,
          location: name,
          fileType: FileTypes.RootFolder,
        });

        molecule.folderTree.addRootFolder(node);
      }
    });
}

function createTask() {
  molecule.folderTree.onNewFile((id) => {
    resetEditorGroup();

    const onSubmit = (values: any) => {
      const { name, ...rest } = values;
      molecule.editor.closeTab("createTask", 1);
      molecule.explorer.forceUpdate();
      const node = new TreeNodeModel({
        id: new Date().getTime(),
        name,
        fileType: FileTypes.File,
        isLeaf: true,
        data: {
          ...rest,
          language: "sql",
        },
      });

      molecule.folderTree.addNode(id, node);

      const { current } = molecule.editor.getState();
      if (current?.tab?.data.taskType === "SparkSql") {
        molecule.editor.updateActions([{ id: TASK_RUN_ID, disabled: false }]);
      }
    };

    const tabData = {
      id: "createTask",
      modified: false,
      name: localize("create task", "新建任务"),
      data: {
        value: id,
      },
      renderPane: () => {
        return <Open currentId={id} onSubmit={onSubmit} />;
      },
    };

    const { groups = [] } = molecule.editor.getState();
    const isExist = groups.some((group) =>
      group.data?.some((tab) => tab.id === "createTask")
    );
    if (!isExist) {
      molecule.editor.open(tabData);
      molecule.explorer.forceUpdate();
    }
  });
}

function createFolder() {
  molecule.folderTree.onNewFolder((id: number) => {
    // work through addNode function
    molecule.folderTree.addNode(
      id,
      new TreeNodeModel({
        id: "folder",
        name: "",
        isLeaf: false,
        fileType: FileTypes.Folder,
        isEditable: true,
      })
    );
  });
}

function onSelectFile() {
  molecule.folderTree.onSelectFile((file) => {
    if (file.fileType === FileTypes.File) {
      if (file.data.taskType === "SparkSql") {
        molecule.editor.updateActions([{ id: TASK_RUN_ID, disabled: false }]);
      } else {
        resetEditorGroup();
      }

      file.data.taskType &&
        molecule.statusBar.appendRightItem({
          id: "language",
          sortIndex: 3,
          name: file.data.taskType,
        });
    }
  });
}

export default class FolderTreeExtension implements IExtension {
  activate() {
    init();

    createTask();
    createFolder();
    onSelectFile();
  }
}
