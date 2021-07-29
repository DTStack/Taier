import { IExtension } from "molecule/esm/model";

import { ExtendsAccBar } from "./activityBar";
import EditorExtension from "./editor";
import ExplorerExtensions from "./explorer";
import FolderTreeExtension from "./folderTree";
import PanelExtension from "./panel";

export const extensions: IExtension[] = [
  new ExtendsAccBar(),
  new ExplorerExtensions(),
  new EditorExtension(),
  new FolderTreeExtension(),
  new PanelExtension(),
];
