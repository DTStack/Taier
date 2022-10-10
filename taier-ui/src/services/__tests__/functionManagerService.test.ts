import { FolderTreeService } from '@dtinsight/molecule/esm/services';
import functionManagerService from '../functionManagerService';
import resourceManagerTree from '../resourceManagerService';

describe('Test function and resource manager service', () => {
	it('Should instance of FolderTreeService', () => {
		expect(functionManagerService).toBeInstanceOf(FolderTreeService);
		expect(resourceManagerTree).toBeInstanceOf(FolderTreeService);
	});
});
