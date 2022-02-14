import molecule from '@dtinsight/molecule';
import { DRAWER_MENU_ENUM, TENANT_MENU } from '@/constant';
import { history } from 'umi';
import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import type { IExtension } from '@dtinsight/molecule/esm/model';
import ReactDOM from 'react-dom';
import AddTenantModal from '@/components/addTenantModal';

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
			case 'Open': {
				molecule.extension.executeCommand('quickOpen');
				break;
			}
			case 'About': {
				window.open('https://github.com/DTStack/Taiger');
				break;
			}

			case TENANT_MENU.ADD_TENANT: {
				const root = document.getElementById('molecule')!;
				const node = document.createElement('div');
				node.id = 'add-tenant-modal';
				root.appendChild(node);
				ReactDOM.render(<AddTenantModal />, node);
				break;
			}
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
