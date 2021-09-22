import { IExtension } from 'molecule/esm/model';

import CatalogueExtension from './catalogue';
import EditorExtension from './editor';
import ExplorerExtensions from './explorer';
import FolderTreeExtension from './folderTree';
import PanelExtension from './panel';
import SidebarExtension from './sidebar';
import { ExtendsSparkSQL } from './languages';
import StatusBarExtension from './statusBar';
import ThemeExtension from './colorTheme';
import WelcomeExtension from './welcome';

export const extensions: IExtension[] = [
    new ThemeExtension(),
    new CatalogueExtension(),
    new ExplorerExtensions(),
    new EditorExtension(),
    new FolderTreeExtension(),
    new PanelExtension(),
    new SidebarExtension(),
    new ExtendsSparkSQL(),
    new StatusBarExtension(),
    new WelcomeExtension(),
];
