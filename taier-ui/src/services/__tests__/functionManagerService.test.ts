import { FolderTreeService } from '@dtinsight/molecule/esm/services';
import functionManagerService from '../functionManagerService';
import resourceManagerTree from '../resourceManagerService';

describe('Test function and resource manager service', () => {
	it('Should instance of FolderTreeService', () => {
		expect(functionManagerService).toBeInstanceOf(FolderTreeService);
	});
});

describe('Test resource manager service', () => {
	it('Should instance of FolderTreeService', () => {
		expect(resourceManagerTree).toBeInstanceOf(FolderTreeService);
	});

	it('Should support to distinguish file and folder', async () => {
		(resourceManagerTree.get as jest.Mock).mockResolvedValueOnce(() => {});

		await expect(resourceManagerTree.checkNotDir(1)).resolves.toBe(undefined);
		await expect(resourceManagerTree.checkNotDir('2-folder')).rejects.toBeInstanceOf(Error);
		await expect(resourceManagerTree.checkNotDir(3)).rejects.toBeInstanceOf(Error);
	});
});
