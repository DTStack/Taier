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

import { TASK_TYPE_ENUM } from '@/constant';
import molecule from '@dtinsight/molecule';
import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import type { IEditorTab, IExtension, IStatusBarItem } from '@dtinsight/molecule/esm/model';
import { Float } from '@dtinsight/molecule/esm/model';

export const STATUS_BAR_LANGUAGE = {
	id: 'TaskLanguage',
	sortIndex: 3,
	name: 'SparkSQL',
};

export function getStatusBarLanguage(language: string | number) {
	const languageBar = { ...STATUS_BAR_LANGUAGE };
	switch (Number(language)) {
		case TASK_TYPE_ENUM.SQL: {
			languageBar.name = 'SparkSQL';
			break;
		}
		case TASK_TYPE_ENUM.SYNC: {
			languageBar.name = 'DataSync';
			break;
		}
		default: {
			return null;
		}
	}
	return languageBar;
}

export function updateStatusBarLanguage(item: IStatusBarItem | null) {
	if (!item) return;
	const languageStatus = molecule.statusBar.getStatusBarItem(STATUS_BAR_LANGUAGE.id, Float.right);
	if (languageStatus) {
		molecule.statusBar.update(item, Float.right);
	} else {
		molecule.statusBar.add(item, Float.right);
	}
}

function statusBarLanguage() {
	const moleculeEditor = molecule.editor;
	moleculeEditor.onSelectTab((tabId, groupId) => {
		if (!groupId) return;
		const group = moleculeEditor.getGroupById(groupId);
		if (!group) return;
		const tab = moleculeEditor.getTabById<IEditorTab>(tabId, group.id);
		if (tab) {
			updateStatusBarLanguage(getStatusBarLanguage((tab.data as any)?.taskType));
		}
	});
}

/**
 * For initlizing the statusbar data
 */
export default class StatusBarExtension implements IExtension {
	id: UniqueId = 'statusBar';
	name: string = 'statusBar';
	dispose(): void {
		throw new Error('Method not implemented.');
	}
	activate() {
		statusBarLanguage();
	}
}
