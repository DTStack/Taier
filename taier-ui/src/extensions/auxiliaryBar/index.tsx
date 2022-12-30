import { rightBarService, taskRenderService } from '@/services';
import molecule from '@dtinsight/molecule';
import { UniqueId } from '@dtinsight/molecule/esm/common/types';
import { IExtension } from '@dtinsight/molecule/esm/model';
import { IExtensionService } from '@dtinsight/molecule/esm/services';

export default class AuxiliaryBarExtensions implements IExtension {
	id: UniqueId = 'auxiliaryBar';
	name: string = 'auxiliaryBar';
	activate(extensionCtx: IExtensionService): void {
		molecule.auxiliaryBar.setMode('tabs');

		const setAuxiliaryBar = () => {
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
					molecule.auxiliaryBar.setChildren(
						rightBarService.createContent(current as string),
					);
				}
			}
		};

		setAuxiliaryBar();

		molecule.auxiliaryBar.onTabClick((key) => {
			const tab = molecule.auxiliaryBar.getCurrentTab();
			if (tab) {
				molecule.auxiliaryBar.setChildren(rightBarService.createContent(key as string));
			}

			molecule.layout.setAuxiliaryBar(!tab);
		});

		molecule.editor.onOpenTab(() => {
			window.requestAnimationFrame(() => {
				setAuxiliaryBar();
			});
		});
		molecule.editor.onSelectTab(() => {
			setAuxiliaryBar();
		});
		molecule.editor.onCloseTab(() => {
			setAuxiliaryBar();
		});
	}
	dispose(extensionCtx: IExtensionService): void {
		throw new Error('Method not implemented.');
	}
}
