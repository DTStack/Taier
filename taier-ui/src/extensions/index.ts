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

import type { IExtension } from '@dtinsight/molecule/esm/model';

import ActionExtension from './action';
import AuxiliaryBarExtensions from './auxiliaryBar';
import CatalogueExtension from './catalogue';
import ConfirmExtension from './confirm';
import DTStackTheme from './dtstackTheme';
import EditorExtension from './editor';
import FolderTreeExtension from './folderTree';
import { LocaleExtension } from './i18n';
import InitializeExtension from './init';
import { ExtendsSparkSQL } from './languages';
import MenuExtension from './menu';
import PanelExtension from './panel';
import ColorThemeExtensions from './themes';

export const extensions: IExtension[] = [
    LocaleExtension,
    DTStackTheme,
    new ConfirmExtension(),
    new ColorThemeExtensions(),
    new ActionExtension(),
    new InitializeExtension(),
    new PanelExtension(),
    new CatalogueExtension(),
    new EditorExtension(),
    new FolderTreeExtension(),
    new ExtendsSparkSQL(),
    new MenuExtension(),
    new AuxiliaryBarExtensions(),
];
