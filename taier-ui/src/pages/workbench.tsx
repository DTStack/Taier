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
// 这是一个自动生成的文件，减少不必要的修改除了格式化以外
import 'reflect-metadata';
import React from 'react';
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

import { ILayoutController, LayoutController } from '@dtinsight/molecule/esm/controller/layout';
import { ILayout, MenuBarMode } from '@dtinsight/molecule/esm/model/workbench/layout';

import { IWorkbench } from '@dtinsight/molecule/esm/model';
import SplitPane from '@dtinsight/molecule/esm/components/split/SplitPane';
import { Pane } from '@dtinsight/molecule/esm/components/split';
import { Display } from '@dtinsight/molecule/esm/components';
import molecule from '@dtinsight/molecule';
import RightBar from './rightBar';

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

export function WorkbenchView(props: IWorkbench & ILayout & ILayoutController) {
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

	const getSizes = () => {
		if (panel.hidden) {
			return ['100%', 0];
		}
		if (panel.panelMaximized) {
			return [0, '100%'];
		}
		return horizontalSplitPanePos;
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
						sizes={sidebar.hidden ? [0, '100%'] : splitPanePos}
						split="vertical"
						showSashes={!sidebar.hidden}
						allowResize={[false]}
						onChange={onPaneSizeChange!}
					>
						<Pane minSize={170} maxSize="80%">
							<SidebarView />
						</Pane>
						<SplitPane
							sizes={getSizes()}
							showSashes={!panel.hidden && !panel.panelMaximized}
							allowResize={[true, false]}
							split="horizontal"
							onChange={onHorizontalPaneSizeChange!}
						>
							<Pane minSize="10%" maxSize="80%">
								<EditorView />
							</Pane>
							<PanelView />
						</SplitPane>
					</SplitPane>
					<RightBar />
				</div>
			</div>
			<Display visible={!statusBar.hidden}>
				<StatusBarView />
			</Display>
		</div>
	);
}

export const Workbench = connect(molecule.layout, WorkbenchView, layoutController);
