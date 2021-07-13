import { FileTypes, IExtension, TreeNodeModel } from "molecule/esm/model";
import axios from "axios";
import { localize } from "molecule/esm/i18n/localize";
import molecule from "molecule/esm";
import Open from "./open";

function init() {
  axios.post("/api/rdos/batch/batchCatalogue/getCatalogue").then(({ data }) => {
    const res = data.data;
    if (res.success) {
      const { id, name } = res.data;
      molecule.folderTree.addRootFolder(
        new TreeNodeModel({
          id,
          name,
          location: name,
          fileType: FileTypes.RootFolder,
        })
      );
    }
  });
}

function createTask() {
  molecule.folderTree.onNewFile((id) => {
    const onSubmit = (values: any) => {
      molecule.editor.closeTab("createTask", 1);
      molecule.folderTree.addNode(
        id,
        new TreeNodeModel({
          id: "testtest",
          name: values.name,
          fileType: FileTypes.File,
        })
      );
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

    molecule.editor.open(tabData);
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
        fileType: FileTypes.Folder,
        isEditable: true,
      })
    );
  });
}

export default class FolderTreeExtension implements IExtension {
  activate() {
    init();

    createTask();
    createFolder();
  }
}
