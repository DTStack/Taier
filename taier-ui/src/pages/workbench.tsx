import 'reflect-metadata';
import SplitPane from 'react-split-pane';
// @ts-ignore
import Pane from 'react-split-pane/lib/Pane';
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
import { Display } from '@dtinsight/molecule/esm/components';
import molecule from '@dtinsight/molecule';
import RightBar from './rightBar';
import { useState } from 'react';
import { MenuBarMode } from '@dtinsight/molecule/esm/model/workbench/layout';
import { LocalDB } from '@dtinsight/dt-utils/lib';

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
		setRightBarSize(key ? '350px' : '30px');
	};

	const getContent = (panelMaximized: boolean, panelHidden: boolean) => {
		const editor = (
			<Pane
				key="editorView"
				initialSize={panelHidden ? '100%' : horizontalSplitPanePos[0]}
				maxSize="100%"
				minSize="10%"
			>
				<SplitPane allowResize={false}>
					<Pane>
						<EditorView />
					</Pane>
					<Pane minSize={rightBarSize} initialSize={rightBarSize} maxSize={rightBarSize}>
						<RightBar onTabClick={handleClickTab} />
					</Pane>
				</SplitPane>
			</Pane>
		);

		const Panel = (
			<Pane key="panelView">
				<PanelView />
			</Pane>
		);

		if (panelHidden) {
			return editor;
		}
		if (panelMaximized) {
			return Panel;
		}
		return [editor, Panel];
	};

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
						<Display
							visible={!activityBar.hidden}
							className={displayActivityBarClassName}
						>
							<ActivityBarView />
						</Display>
					</div>
					<SplitPane
						split="vertical"
						primary="first"
						allowResize={true}
						onChange={onPaneSizeChange as any}
					>
						<Pane
							minSize="170px"
							initialSize={splitPanePos[0]}
							maxSize="80%"
							className={sidebar.hidden ? 'hidden' : ''}
						>
							<SidebarView />
						</Pane>
						<SplitPane
							primary="first"
							split="horizontal"
							allowResize={true}
							// react-split-pane onChange: (newSizes: [size, ratio]) => void；
							onChange={onHorizontalPaneSizeChange as any}
						>
							{getContent(!!panel.panelMaximized, !!panel.hidden)}
						</SplitPane>
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
