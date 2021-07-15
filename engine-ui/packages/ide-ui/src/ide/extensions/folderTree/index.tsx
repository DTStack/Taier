import { FileTypes, IExtension, TreeNodeModel } from "molecule/esm/model";
import axios from "axios";
import { localize } from "molecule/esm/i18n/localize";
import molecule from "molecule/esm";
import Open from "./open";
import { TASK_RUN_ID, TASK_STOP_ID } from "../editor";
import { resetEditorGroup } from "../common";

function init() {
  axios.post("/api/rdos/batch/batchCatalogue/getCatalogue").then(({ data }) => {
    const res = data.data;
    if (res.success) {
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
      const node = new TreeNodeModel({
        id: new Date().getTime(),
        name,
        data: rest,
        fileType: FileTypes.File,
      });

      molecule.folderTree.addNode(id, node);
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
    if (file.data.taskType === "SparkSql") {
      molecule.editor.updateActions([{ id: TASK_RUN_ID, disabled: false }]);
    } else {
      resetEditorGroup();
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
