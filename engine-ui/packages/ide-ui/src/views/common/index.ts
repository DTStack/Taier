import { IExtension } from 'molecule/esm/model';

import CatalogueExtension from './catalogue';
import EditorExtension from './editor';
import ExplorerExtensions from './explorer';
import FolderTreeExtension from './folderTree';
import PanelExtension from './panel';
import SidebarExtension from './sidebar';
import { ExtendsSparkSQL } from './languages';
import StatusBarExtension from './statusBar';

export const extensions: IExtension[] = [
    new CatalogueExtension(),
    new ExplorerExtensions(),
    new EditorExtension(),
    new FolderTreeExtension(),
    new PanelExtension(),
    new SidebarExtension(),
    new ExtendsSparkSQL(),
    new StatusBarExtension(),
];
