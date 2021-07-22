import { IExtension } from "molecule/esm/model";

import { ExtendsAccBar } from "./activityBar";
import EditorExtension from "./editor";
import ExplorerExtensions from "./explorer";
import FolderTreeExtension from "./folderTree";
import SideBarExtension from "./sideBar";

export const extensions: IExtension[] = [
  new ExtendsAccBar(),
  new SideBarExtension(),
  new ExplorerExtensions(),
  new EditorExtension(),
  new FolderTreeExtension()
];
