import molecule from '@dtinsight/molecule';
import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import type { IExtension } from '@dtinsight/molecule/esm/model';

import { rightBarService, taskRenderService } from '@/services';
import { onTaskSwitch } from '@/utils/extensions';

export default class AuxiliaryBarExtensions implements IExtension {
    id: UniqueId = 'auxiliaryBar';
    name = 'auxiliaryBar';
    activate(): void {
        molecule.auxiliaryBar.setMode('tabs');

        setAuxiliaryBar();

        molecule.auxiliaryBar.onTabClick((key) => {
            const tab = molecule.auxiliaryBar.getCurrentTab();
            if (tab) {
                molecule.auxiliaryBar.setChildren(rightBarService.createContent(key as string));
            }

            molecule.layout.setAuxiliaryBar(!tab);
        });

        onTaskSwitch(setAuxiliaryBar);
    }
    dispose(): void {
        throw new Error('Method not implemented.');
    }
}

/**
 * Call it for opening auxiliary bar
 */
export const setAuxiliaryBar = () => {
    window.requestAnimationFrame(() => {
        const bars = taskRenderService
            .renderRightBar()
            .map((task) => ({ key: task, title: rightBarService.getTextByKind(task) }));

        molecule.auxiliaryBar.setState({
            data: bars,
        });

        const { current } = molecule.auxiliaryBar.getState();
        if (current) {
            if (!bars.find((b) => b.key === current)) {
                molecule.auxiliaryBar.setChildren(null);
                molecule.auxiliaryBar.setActive(undefined);
                molecule.layout.setAuxiliaryBar(true);
            } else {
                molecule.auxiliaryBar.setChildren(rightBarService.createContent(current as string));
            }
        }
    });
};
