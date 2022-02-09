import molecule from '@dtinsight/molecule';
import { DRAWER_MENU_ENUM } from '@/constant';
import { history } from 'umi';
import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import type { IExtension } from '@dtinsight/molecule/esm/model';

function handleMenuBarEvents() {
	molecule.menuBar.onSelect((menuId) => {
		switch (menuId) {
			case DRAWER_MENU_ENUM.TASK:
			case DRAWER_MENU_ENUM.SCHEDULE:
			case DRAWER_MENU_ENUM.PATCH:
			case DRAWER_MENU_ENUM.QUEUE:
			case DRAWER_MENU_ENUM.RESOURCE:
			case DRAWER_MENU_ENUM.CLUSTER:
				history.push({
					query: {
						drawer: menuId,
					},
				});
				break;
			default:
				break;
		}
	});
}

/**
 * This is for adding menu data modules
 */
export default class MenuExtension implements IExtension {
	id: UniqueId = 'menu';
	name: string = 'menu';
	activate(): void {
		handleMenuBarEvents();
	}
	dispose(): void {
		throw new Error('Method not implemented.');
	}
}
