import { CREATE_MODEL_TYPE, ID_COLLECTIONS } from '@/constant';
import type { CatalogueDataProps, IOfflineTaskProps } from '@/interface';
import { editorActionBarService } from '@/services';
import { isTaskTab } from '@/utils/enums';
import { runTask } from '@/utils/extensions';
import molecule from '@dtinsight/molecule';
import { KeyMod, KeyCode } from '@dtinsight/molecule/esm/monaco';
import { Action2 } from '@dtinsight/molecule/esm/monaco/action';
import { KeybindingWeight } from '@dtinsight/molecule/esm/monaco/common';

export default class QuickRunSQLAction extends Action2 {
	static readonly ID = 'RunSQL';
	static readonly LABEL = 'Execute SQL';
	static readonly DESC = 'Run SQL';

	constructor() {
		super({
			id: QuickRunSQLAction.ID,
			title: {
				value: QuickRunSQLAction.LABEL,
				original: QuickRunSQLAction.DESC,
			},
			label: QuickRunSQLAction.LABEL,
			alias: QuickRunSQLAction.DESC,
			f1: true,
			keybinding: {
				when: undefined,
				weight: KeybindingWeight.WorkbenchContrib,
				// eslint-disable-next-line no-bitwise
				primary: KeyMod.CtrlCmd | KeyCode.Enter,
			},
		});
	}

	run() {
		const { current } = molecule.editor.getState();
		if (current && isTaskTab(current.tab?.id)) {
			const currentTabData: CatalogueDataProps & IOfflineTaskProps = current?.tab?.data;
			const taskToolbar = editorActionBarService.getActionBar(
				currentTabData.taskType,
				currentTabData.createModel === CREATE_MODEL_TYPE.GUIDE,
			);
			// 只要当前任务存在运行按钮才可以执行运行命令
			if (taskToolbar.find((t) => t.id === ID_COLLECTIONS.TASK_RUN_ID)) {
				runTask(current);
			}
		}
	}
}
