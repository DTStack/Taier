import molecule from '@dtinsight/molecule';
import type { ListenerEventContext } from '@dtinsight/molecule/esm/common/event';
import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import type { IExtension } from '@dtinsight/molecule/esm/model';
import { EditorEvent } from '@dtinsight/molecule/esm/model';

import { confirm } from '@/components/confirm';
import type { CatalogueDataProps } from '@/interface';
import taskSaveService from '@/services/taskSaveService';
import { isTaskTab } from '@/utils/is';

export default class ConfirmExtension implements IExtension {
    id: UniqueId = 'Confirm';
    name = 'Confirm';
    activate(): void {
        molecule.editor.onCloseTab(function anonymous(this: ListenerEventContext, tabId, groupId) {
            const tab = molecule.editor.getTabById(tabId, groupId!);
            if (tab?.status === 'edited' && isTaskTab(tab.id)) {
                this.stopDelivery();
                showConfirm(tabId, groupId!, () => {
                    molecule.editor.emit(EditorEvent.OnCloseTab, tabId, groupId);
                });
            }
        });

        molecule.editor.onCloseOther(function anonymous(this: ListenerEventContext, tab, groupId) {
            const group = molecule.editor.getGroupById(groupId!);
            if (group) {
                const tabs = group.data?.filter((data) => data !== tab) || [];

                if (tabs.some((t) => t.status === 'edited' && isTaskTab(t.id))) {
                    this.stopDelivery();
                    const unSavedTab = tabs.find((t) => t.status === 'edited')!;

                    molecule.editor.setActive(groupId!, unSavedTab.id);
                    showConfirm(unSavedTab.id, groupId!, () => {
                        molecule.editor.emit(EditorEvent.OnCloseOther, tab, groupId);
                    });
                }
            }
        });

        molecule.editor.onCloseAll(function anonymous(this: ListenerEventContext, groupId) {
            const group = molecule.editor.getGroupById(groupId!);
            if (group?.data?.some((tab) => tab.status === 'edited' && isTaskTab(tab.id))) {
                this.stopDelivery();

                const unSavedTab = group!.data!.find((t) => t.status === 'edited')!;
                molecule.editor.setActive(groupId!, unSavedTab.id);
                showConfirm(unSavedTab.id, groupId!, () => {
                    molecule.editor.emit(EditorEvent.OnCloseAll, groupId);
                });
            }
        });

        molecule.editor.onCloseToLeft(function anonymous(this: ListenerEventContext, tab, groupId) {
            const group = molecule.editor.getGroupById(groupId!)!;
            const idx = group.data?.indexOf(tab) || -1;

            const unSavedTab = group.data?.slice(0, idx).find((t) => t.status === 'edited' && isTaskTab(t.id));

            if (unSavedTab) {
                this.stopDelivery();
                molecule.editor.setActive(groupId!, unSavedTab.id);
                showConfirm(unSavedTab.id, groupId!, () => {
                    molecule.editor.emit(EditorEvent.OnCloseToLeft, tab, groupId);
                });
            }
        });

        molecule.editor.onCloseToRight(function anonymous(this: ListenerEventContext, tab, groupId) {
            const group = molecule.editor.getGroupById(groupId!)!;
            const idx = group.data?.indexOf(tab) || -1;

            const unSavedTab = group.data?.slice(idx + 1).find((t) => t.status === 'edited' && isTaskTab(t.id));

            if (unSavedTab) {
                this.stopDelivery();
                molecule.editor.setActive(groupId!, unSavedTab.id);
                showConfirm(unSavedTab.id, groupId!, () => {
                    molecule.editor.emit(EditorEvent.OnCloseToRight, tab, groupId);
                });
            }
        });

        molecule.editorTree.onClose(function anonymous(this: ListenerEventContext, tabId, groupId) {
            const tab = molecule.editor.getTabById(tabId, groupId!);
            if (tab?.status === 'edited' && isTaskTab(tab.id)) {
                this.stopDelivery();
                showConfirm(tabId, groupId!, () => {
                    molecule.editor.emit(EditorEvent.OnCloseTab, tabId, groupId);
                });
            }
        });

        molecule.editorTree.onCloseAll(function anonymous(this: ListenerEventContext, groupId) {
            const group = molecule.editor.getGroupById(groupId!);
            if (group?.data?.some((tab) => tab.status === 'edited' && isTaskTab(tab.id))) {
                this.stopDelivery();

                const unSavedTab = group!.data!.find((t) => t.status === 'edited')!;
                molecule.editor.setActive(groupId!, unSavedTab.id);
                showConfirm(unSavedTab.id, groupId!, () => {
                    molecule.editor.emit(EditorEvent.OnCloseAll, groupId);
                });
            }
        });

        molecule.editorTree.onCloseOthers(function anonymous(this: ListenerEventContext, tab, groupId) {
            const group = molecule.editor.getGroupById(groupId!);
            if (group) {
                const tabs = group.data?.filter((data) => data !== tab) || [];

                if (tabs.some((t) => t.status === 'edited' && isTaskTab(t.id))) {
                    this.stopDelivery();
                    const unSavedTab = tabs.find((t) => t.status === 'edited')!;

                    molecule.editor.setActive(groupId!, unSavedTab.id);
                    showConfirm(unSavedTab.id, groupId!, () => {
                        molecule.editor.emit(EditorEvent.OnCloseOther, tab, groupId);
                    });
                }
            }
        });

        molecule.editorTree.onCloseSaved((groupId) => {
            const group = molecule.editor.getGroupById(groupId);

            // Close saved tab will ONLY close task tab and ignore others
            const tabs = group?.data?.filter((t) => t.status !== 'edited' && isTaskTab(t.id)) || [];

            tabs.forEach((tab) => {
                molecule.editor.closeTab(tab.id, groupId);
            });
        });
    }
    dispose(): void {
        throw new Error('Method not implemented.');
    }
}

function showConfirm(tabId: UniqueId, groupId: UniqueId, cb?: () => void) {
    const tab = molecule.editor.getTabById(tabId, groupId);
    if (!tab) return;
    const currentTabData = tab.data as CatalogueDataProps;

    const goContinueClose = () => {
        // Change current tab's status to undefined and continue to close
        molecule.editor.updateTab(
            {
                id: tabId,
                status: undefined,
            },
            groupId
        );
        cb?.();
    };

    confirm({
        tab: currentTabData,
        onSave: () => taskSaveService.save().then(() => goContinueClose()),
        onUnSave: () => goContinueClose(),
    });
}
