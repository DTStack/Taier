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

import React from 'react';
import molecule from '@dtinsight/molecule';
import { IExtension, SAMPLE_FOLDER_PANEL_ID } from '@dtinsight/molecule/esm/model';
import { localize } from '@dtinsight/molecule/esm/i18n/localize';
import { connect } from '@dtinsight/molecule/esm/react';
import TaskInfo from '../../task/taskInfo';
import { TASK_ATTRIBUTONS } from '../utils/const';

function changeContextMenuName() {
	const explorerData = molecule.explorer.getState().data?.concat() || [];
	const folderTreePane = explorerData.find((item) => item.id === SAMPLE_FOLDER_PANEL_ID);
	if (folderTreePane?.toolbar) {
		folderTreePane.toolbar[0].title = '新建任务';
		molecule.explorer.setState({
			data: explorerData,
		});
	}
}

function initTaskInfo() {
	const TaskinfoView = connect(molecule.editor, TaskInfo);

	molecule.explorer.addPanel({
		id: TASK_ATTRIBUTONS,
		name: localize(TASK_ATTRIBUTONS, '任务属性'),
		renderPanel: () => <TaskinfoView />,
	});
}

export default class ExplorerExtensions implements IExtension {
	activate(extensionCtx: molecule.IExtensionService): void {
		changeContextMenuName();

		initTaskInfo();
	}
}
