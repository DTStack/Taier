/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { IExtension } from '@dtinsight/molecule/esm/model';

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
