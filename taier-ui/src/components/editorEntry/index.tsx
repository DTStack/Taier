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

import { useEffect, useState } from 'react';
import { KeybindingHelper } from '@dtinsight/molecule/esm/services/keybinding';
import './index.scss';
import { Utils } from '@dtinsight/dt-utils/lib';

const commands = [
	{ id: 'sidebar', label: '切换侧边栏' },
	{ id: 'workbench.action.showPanel', label: '切换面板' },
	{ id: 'RunSQL', label: '运行 SQL' },
	{ id: 'workbench.action.selectTheme', label: '切换主题颜色' },
];

export default function EditorEntry() {
	const [keys, setKeys] = useState<{ id: string; label: string; keybindings: string }[]>([]);

	useEffect(() => {
		setKeys(
			commands
				.map((command) => {
					const simpleKeybindings = KeybindingHelper.queryGlobalKeybinding(command.id);
					if (simpleKeybindings?.length) {
						const keybindings =
							KeybindingHelper.convertSimpleKeybindingToString(simpleKeybindings);
						return { ...command, keybindings };
					}
					return null;
				})
				.filter(Boolean) as { id: string; label: string; keybindings: string }[],
		);
	}, []);

	return (
		<div className="entry">
			<img className="logo" width={200} src="images/taier.png" />
			<div className="commands">
				{keys.map((key) => (
					<div className="command" key={key.id}>
						<div className="label">{key.label}</div>
						<div className="keybindings">
							{key.keybindings
								.split(Utils.isMacOs() ? '' : '+')
								.filter(Boolean)
								.map((keyCode) => (
									<code key={keyCode} className="keyCode">{keyCode}</code>
								))}
						</div>
					</div>
				))}
			</div>
		</div>
	);
}
