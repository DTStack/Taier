import api from '@/api';
import { CREATE_MODEL_TYPE, ID_COLLECTIONS } from '@/constant';
import { CatalogueDataProps, IOfflineTaskProps } from '@/interface';
import { editorActionBarService } from '@/services';
import { isTaskTab } from '@/utils/enums';
import saveTask from '@/utils/saveTask';
import molecule from '@dtinsight/molecule';
import { Action2 } from '@dtinsight/molecule/esm/monaco/action';
import { KeybindingWeight } from '@dtinsight/molecule/esm/monaco/common';
import { message } from 'antd';
import { KeyMod, KeyCode } from 'monaco-editor';

export default class QuickSaveTaskAction extends Action2 {
	static readonly ID = 'SaveTask';
	static readonly LABEL = 'Save Task';
	static readonly DESC = 'Save Task';

	constructor() {
		super({
			id: QuickSaveTaskAction.ID,
			title: {
				value: QuickSaveTaskAction.LABEL,
				original: QuickSaveTaskAction.DESC,
			},
			label: QuickSaveTaskAction.LABEL,
			alias: QuickSaveTaskAction.DESC,
			f1: true,
			keybinding: {
				when: undefined,
				weight: KeybindingWeight.WorkbenchContrib,
				// eslint-disable-next-line no-bitwise
				primary: KeyMod.CtrlCmd | KeyCode.KeyS,
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
			if (taskToolbar.find((t) => t.id === ID_COLLECTIONS.TASK_SAVE_ID)) {
				saveTask()
					.then((res) => res?.data?.id)
					.then((id) => {
						if (id !== undefined) {
							api.getOfflineTaskByID({ id }).then((res) => {
								const { code } = res;
								if (code === 1) {
									molecule.editor.updateTab({
										id: current.tab!.id,
										status: undefined,
									});
								}
							});
						}
					})
					.catch((err: Error | undefined) => {
						if (err) {
							message.error(err.message);
						}
					});
			}
		}
	}
}
