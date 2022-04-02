import {
	EDIT_TASK_PREFIX,
	EDIT_FOLDER_PREFIX,
	CREATE_TASK_PREFIX,
	CREATE_DATASOURCE_PREFIX,
	EDIT_DATASOURCE_PREFIX,
} from '@/constant';
import { runTask } from '@/utils/extensions';
import molecule from '@dtinsight/molecule';
import { KeyMod, KeyCode } from '@dtinsight/molecule/esm/monaco';
import { Action2 } from '@dtinsight/molecule/esm/monaco/action';
import { KeybindingWeight } from '@dtinsight/molecule/esm/monaco/common';

export default class QuickRunSQLAction extends Action2 {
	static readonly ID = 'RunSQL';
	static readonly LABEL = 'Excute SQL';
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
		// 不需要运行任务的 tab
		const NOT_RUN = [
			EDIT_TASK_PREFIX,
			EDIT_FOLDER_PREFIX,
			CREATE_TASK_PREFIX,
			CREATE_DATASOURCE_PREFIX,
			EDIT_DATASOURCE_PREFIX,
		];
		if (current && !NOT_RUN.some((prefix) => current.activeTab?.toString().includes(prefix))) {
			runTask(current);
		}
	}
}
