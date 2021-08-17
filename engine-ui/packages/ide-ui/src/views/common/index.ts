import { IExtension } from 'molecule/esm/model';

import EditorExtension from './editor';
import ExplorerExtensions from './explorer';
import FolderTreeExtension from './folderTree';
import PanelExtension from './panel';

export const extensions: IExtension[] = [
    new ExplorerExtensions(),
    new EditorExtension(),
    new FolderTreeExtension(),
    new PanelExtension(),
];
