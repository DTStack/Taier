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

import 'reflect-metadata';
import { container } from 'tsyringe';
import {
	classNames,
	getFontInMac,
	prefixClaName,
	getBEMModifier,
	getBEMElement,
} from '@dtinsight/molecule/esm/common/className';
import { EditorView } from '@dtinsight/molecule/esm/workbench/editor';
import { SidebarView } from '@dtinsight/molecule/esm/workbench/sidebar';
import { MenuBarView } from '@dtinsight/molecule/esm/workbench/menuBar';
import { ActivityBarView } from '@dtinsight/molecule/esm/workbench/activityBar';
import { StatusBarView } from '@dtinsight/molecule/esm/workbench/statusBar';
import { PanelView } from '@dtinsight/molecule/esm/workbench/panel';
import { ID_APP } from '@dtinsight/molecule/esm/common/id';
import { APP_PREFIX } from '@dtinsight/molecule/esm/common/const';
import { connect } from '@dtinsight/molecule/esm/react';
import type { ILayoutController } from '@dtinsight/molecule/esm/controller/layout';
import { LayoutController } from '@dtinsight/molecule/esm/controller/layout';
import type { ILayout } from '@dtinsight/molecule/esm/model/workbench/layout';
import type { IWorkbench } from '@dtinsight/molecule/esm/model';
import { Display, Pane, SplitPane } from '@dtinsight/molecule/esm/components';
import molecule from '@dtinsight/molecule';
import RightBar from './rightBar';
import { useState } from 'react';
import { MenuBarMode } from '@dtinsight/molecule/esm/model/workbench/layout';

const mainBenchClassName = prefixClaName('mainBench');
const workbenchClassName = prefixClaName('workbench');
const compositeBarClassName = prefixClaName('compositeBar');
const appClassName = classNames(APP_PREFIX, getFontInMac());
const workbenchWithHorizontalMenuBarClassName = getBEMModifier(
	workbenchClassName,
	'with-horizontal-menuBar',
);
const withHiddenStatusBar = getBEMModifier(workbenchClassName, 'with-hidden-statusBar');
const displayActivityBarClassName = getBEMElement(workbenchClassName, 'display-activityBar');

const layoutController = container.resolve(LayoutController);

function WorkbenchView(props: IWorkbench & ILayout & ILayoutController) {
	const {
		activityBar,
		menuBar,
		panel,
		sidebar,
		statusBar,
		onPaneSizeChange,
		onHorizontalPaneSizeChange,
		splitPanePos,
		horizontalSplitPanePos,
	} = props;
	const [rightBarSize, setRightBarSize] = useState('30px');

	const handleClickTab = (key?: string) => {
		setRightBarSize(key ? '400px' : '30px');
	};

	const handleSideBarChanged = (sizes: number[]) => {
		if (sidebar.hidden) {
			const clientSize = sizes[1];
			const sidebarSize = splitPanePos[0];
			if (typeof sidebarSize === 'string') {
				// the sideBar size is still a default value
				const numbSize = parseInt(sidebarSize, 10);
				onPaneSizeChange?.([numbSize, clientSize - numbSize]);
			} else {
				onPaneSizeChange?.([sidebarSize, clientSize - sidebarSize]);
			}
		} else {
			onPaneSizeChange?.(sizes);
		}
	};

	const handleEditorChanged = (sizes: number[]) => {
		if (panel.hidden) {
			// get the non-zero size means current client size
			const clientSize = sizes.find((s) => s)!;
			const panelSize = horizontalSplitPanePos[1];
			if (typeof panelSize === 'string') {
				// the editor size is still a default value
				const editorPercent = parseInt(horizontalSplitPanePos[0] as string, 10) / 100;
				const numbericSize = clientSize * editorPercent;
				onHorizontalPaneSizeChange?.([numbericSize, clientSize - numbericSize]);
			} else {
				onHorizontalPaneSizeChange?.([clientSize - panelSize, panelSize]);
			}
		} else {
			onHorizontalPaneSizeChange?.(sizes);
		}
	};

	const getSizes = () => {
		if (panel.hidden) {
			return ['100%', 0];
		}
		if (panel.panelMaximized) {
			return [0, '100%'];
		}
		return horizontalSplitPanePos.concat();
	};

	const isMenuBarVertical = !menuBar.hidden && menuBar.mode === MenuBarMode.vertical;
	const isMenuBarHorizontal = !menuBar.hidden && menuBar.mode === MenuBarMode.horizontal;
	const horizontalMenuBar = isMenuBarHorizontal ? workbenchWithHorizontalMenuBarClassName : null;
	const hideStatusBar = statusBar.hidden ? withHiddenStatusBar : null;
	const workbenchFinalClassName = classNames(
		workbenchClassName,
		horizontalMenuBar,
		hideStatusBar,
	);

	return (
		<div id={ID_APP} className={appClassName} tabIndex={0}>
			<div className={workbenchFinalClassName}>
				<Display visible={isMenuBarHorizontal}>
					<MenuBarView mode={MenuBarMode.horizontal} />
				</Display>
				<div className={mainBenchClassName}>
					<div className={compositeBarClassName}>
						<Display visible={isMenuBarVertical}>
							<MenuBarView mode={MenuBarMode.vertical} />
						</Display>
						<Display
							visible={!activityBar.hidden}
							className={displayActivityBarClassName}
						>
							<ActivityBarView />
						</Display>
					</div>
					<SplitPane
						sizes={sidebar.hidden ? [0, 'auto', rightBarSize] : splitPanePos.concat(rightBarSize)}
						split="vertical"
						allowResize={[false, true, false]}
						onChange={handleSideBarChanged}
						onResizeStrategy={() => ['keep', 'pave', 'keep']}
					>
						<Pane minSize={170} maxSize="80%">
							<SidebarView />
						</Pane>
						<SplitPane
							sizes={getSizes()}
							allowResize={[false, true, false]}
							split="horizontal"
							onChange={handleEditorChanged}
							onResizeStrategy={() => ['pave', 'keep', 'keep']}
						>
							<Pane minSize="10%" maxSize="80%">
								<EditorView />
							</Pane>
							<PanelView />
						</SplitPane>
						<Pane>
						    <RightBar onTabClick={handleClickTab} />
						</Pane>
					</SplitPane>
				</div>
			</div>
			<Display visible={!statusBar.hidden}>
				<StatusBarView />
			</Display>
		</div>
	);
}

export default connect(molecule.layout, WorkbenchView, layoutController);
