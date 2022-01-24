import taskResultService from '@/services/taskResultService';
import molecule from '@dtinsight/molecule';
import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import type { IExtension } from '@dtinsight/molecule/esm/model';

export default class PanelExtension implements IExtension {
	id: UniqueId = 'panel';
	name: string = 'panel';
	activate(): void {
		// 关闭结果 panel 的时候清除 service 中存储的数据
		molecule.panel.onTabClose((panelId) => {
			const allResults = taskResultService.getState().results;
			if (Object.keys(allResults).includes(panelId.toString())) {
				taskResultService.clearResult(panelId.toString());
			}
		});
	}
	dispose(): void {
		throw new Error('Method not implemented.');
	}
}
