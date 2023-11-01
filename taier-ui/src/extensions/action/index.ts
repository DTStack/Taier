import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import type { IExtension } from '@dtinsight/molecule/esm/model';
import type { IExtensionService } from '@dtinsight/molecule/esm/services';

import QuickRunSQLAction from './quickRunSQLAction';
import QuickSaveTaskAction from './quickSaveTaskAction';

export default class ActionExtensions implements IExtension {
    id: UniqueId = 'actions';
    name = 'actions';
    activate(extensionCtx: IExtensionService): void {
        extensionCtx.registerAction(QuickRunSQLAction);
        extensionCtx.registerAction(QuickSaveTaskAction);
    }
    dispose(): void {
        throw new Error('Method not implemented.');
    }
}
