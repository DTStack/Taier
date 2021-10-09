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

import molecule from '@dtinsight/molecule';
import { IEditorTab, IExtension } from '@dtinsight/molecule/esm/model';
import { getStatusBarLanguage, updateStatusBarLanguage } from '../utils';

function statusBarLanguage() {
    const moleculeEditor = molecule.editor;
    moleculeEditor.onSelectTab((tabId, groupId) => {
        if (!groupId) return;
        const group = moleculeEditor.getGroupById(groupId);
        if (!group) return;
        const tab = moleculeEditor.getTabById<IEditorTab>(tabId, group);
        if (tab) {
            updateStatusBarLanguage(
                getStatusBarLanguage((tab.data as any)?.taskType)
            );
        }
    });

    moleculeEditor.onCloseTab(() => {
        const { current } = moleculeEditor.getState();
        if (current) {
            const group = moleculeEditor.getGroupById(current.id!)!;
            const tab = moleculeEditor.getTabById<IEditorTab>(
                current.activeTab!,
                group
            );
            if (tab) {
                updateStatusBarLanguage(
                    getStatusBarLanguage((tab.data as any)?.taskType)
                );
            }
        }
    });
}

export default class StatusBarExtension implements IExtension {
    activate() {
        statusBarLanguage();
    }
}
