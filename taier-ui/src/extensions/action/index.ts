import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import type { IExtension } from '@dtinsight/molecule/esm/model';
import type { IExtensionService } from '@dtinsight/molecule/esm/services';
import QuickRunSQLAction from './quickRunSqlAction';

export default class ActionExtensions implements IExtension {
	id: UniqueId = 'actions';
	name: string = 'actions';
	activate(extensionCtx: IExtensionService): void {
		extensionCtx.registerAction(QuickRunSQLAction);
	}
	dispose(): void {
		throw new Error('Method not implemented.');
	}
}
