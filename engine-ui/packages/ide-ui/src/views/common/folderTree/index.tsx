import { FileTypes, IExtension, TreeNodeModel } from "molecule/esm/model";
import { localize } from "molecule/esm/i18n/localize";
import molecule from "molecule/esm";
import Open from "../../task/open";
import { resetEditorGroup } from "../utils";
import DataSync from "../../dataSync";
import ajax from "../../../api";
import { TASK_RUN_ID } from "../utils/const";

function init() {
    ajax
        .getOfflineCatalogue({
            nodePid: 0,
            isGetFile: false,
            catalogueType: 1,
            taskType: 1,
            appointProjectId: 1
        })
        .then((res) => {
            if (res.code === 1) {
                const { id, name } = res.data;
                const node = new TreeNodeModel({
                    id,
                    name,
                    location: name,
                    fileType: FileTypes.RootFolder
                });

                molecule.folderTree.add(node);
            }
        });
}

function createTask() {
    molecule.folderTree.onCreate((type, id) => {
        if (type === "File") {
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
                        language: "sql"
                    }
                });

                molecule.folderTree.add(node, id);

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
                    value: id
                },
                renderPane: () => {
                    return <Open currentId={id} onSubmit={onSubmit} />;
                }
            };

            const { groups = [] } = molecule.editor.getState();
            const isExist = groups.some((group) =>
                group.data?.some((tab) => tab.id === "createTask")
            );
            if (!isExist) {
                molecule.editor.open(tabData);
                molecule.explorer.forceUpdate();
            }
        } else if (type === "Folder") {
            // work through addNode function
            molecule.folderTree.add(
                new TreeNodeModel({
                    id: "folder",
                    name: "",
                    isLeaf: false,
                    fileType: FileTypes.Folder,
                    isEditable: true
                }),
                id
            );
        }
    });
}

function onSelectFile() {
    molecule.folderTree.onSelectFile((file) => {
        if (file.data.taskType === "SparkSql") {
            const tabData = {
                id: file.id,
                name: file.name,
                data: {
                    ...file.data,
                    content: file.content
                }
            };
            molecule.editor.open(tabData);
            molecule.editor.updateActions([{ id: TASK_RUN_ID, disabled: false }]);
        } else if (file.data.taskType === "DataSync") {
            const tabData = {
                id: file.id,
                name: file.name,
                data: {
                    ...file.data,
                    content: file.content
                },
                renderPane: () => {
                    return <DataSync />;
                }
            };
            molecule.editor.open(tabData);
            molecule.editor.updateActions([{ id: TASK_RUN_ID, disabled: false }]);
        } else {
            resetEditorGroup();
        }

        file.data.taskType &&
      molecule.statusBar.add(
          {
              id: "language",
              sortIndex: 3,
              name: file.data.taskType
          },
          "right"
      );
    });
}

export default class FolderTreeExtension implements IExtension {
    activate() {
        init();

        createTask();
        onSelectFile();
    }
}
