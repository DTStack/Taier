import api from '@/api';
import { CATALOGUE_TYPE, MENU_TYPE_ENUM } from '@/constant';
import molecule from '@dtinsight/molecule';
import CatalogueService from '../catalogueService';
import functionManagerService from '../functionManagerService';
import resourceManagerService from '../resourceManagerService';

jest.mock('@/api');

jest.mock('@/services/resourceManagerService', () => ({
    get: jest.fn(),
    add: jest.fn(),
    getState: jest.fn(),
}));

jest.mock('@/services/functionManagerService', () => ({
    get: jest.fn(),
    add: jest.fn(),
    getState: jest.fn(),
}));

jest.mock('@/utils', () => ({
    getTenantId: jest.fn(),
    getUserId: jest.fn(),
}));

jest.mock('@/utils/extensions', () => ({
    fileIcon: jest.fn(() => 'code'),
}));

describe('Test catalogue service', () => {
    beforeEach(() => {
        (api.getOfflineCatalogue as jest.Mock).mockReset();
        (molecule.folderTree.get as jest.Mock).mockReset();
        (molecule.folderTree.getState as jest.Mock).mockReset();
        (functionManagerService.getState as jest.Mock).mockReset();
        (resourceManagerService.getState as jest.Mock).mockReset();
    });

    it('Should load root folder', async () => {
        (api.getOfflineCatalogue as jest.Mock).mockResolvedValue({
            code: 1,
            data: {
                children: [
                    {
                        catalogueType: 'TaskManager',
                        id: 0,
                        name: '任务管理',
                        type: 'folder',
                        children: [
                            {
                                catalogueType: 'TaskDevelop',
                                id: 3,
                                name: '任务开发',
                                type: 'folder',
                            },
                        ],
                    },
                    {
                        catalogueType: 'ResourceManager',
                        id: 1,
                        name: '资源管理',
                        type: 'folder',
                        children: [
                            {
                                catalogueType: 'ResourceManager',
                                id: 35,
                                name: '资源管理',
                                type: 'folder',
                            },
                        ],
                    },
                    {
                        catalogueType: 'FunctionManager',
                        id: 2,
                        name: '函数管理',
                        type: 'folder',
                        children: [
                            {
                                catalogueType: 'FunctionManager',
                                id: 39,
                                name: '函数管理',
                                type: 'folder',
                            },
                        ],
                    },
                ],
            },
        });
        const service = new CatalogueService();
        service.loadRootFolder();

        await new Promise<void>((resolve) => {
            setTimeout(() => {
                expect((resourceManagerService.add as jest.Mock).mock.calls[0][0]).toMatchSnapshot();

                expect((functionManagerService.add as jest.Mock).mock.calls[0][0]).toMatchSnapshot();

                expect((molecule.folderTree.add as jest.Mock).mock.calls[0][0]).toMatchSnapshot();
                resolve();
            }, 0);
        });
    });

    it('Should support to get root folder by catalogue type', () => {
        (molecule.folderTree.getState as jest.Mock).mockImplementation(() => ({
            folderTree: {
                data: ['this is molecule.folderTree'],
            },
        }));

        (functionManagerService.getState as jest.Mock).mockImplementation(() => ({
            folderTree: {
                data: ['this is function.getState'],
            },
        }));

        (resourceManagerService.getState as jest.Mock).mockImplementation(() => ({
            folderTree: {
                data: [{ children: ['this is resource.getState'] }],
            },
        }));

        const service = new CatalogueService();
        expect(service.getRootFolder(CATALOGUE_TYPE.TASK)).toBe('this is molecule.folderTree');
        expect(service.getRootFolder(CATALOGUE_TYPE.FUNCTION)).toBe('this is function.getState');
        expect(service.getRootFolder(CATALOGUE_TYPE.RESOURCE)).toBe('this is resource.getState');

        // @ts-ignore
        expect(service.getRootFolder('')).toBe(undefined);
    });

    it('Should load tree node', async () => {
        (api.getOfflineCatalogue as jest.Mock).mockResolvedValue({
            code: 1,
            data: {
                children: [
                    {
                        catalogueType: 'TaskManager',
                        id: 0,
                        name: '任务管理',
                        type: 'folder',
                        children: [
                            {
                                id: 259,
                                name: 'test',
                                taskType: 2,
                                type: 'file',
                            },
                        ],
                    },
                ],
            },
        });
        (molecule.folderTree.get as jest.Mock).mockImplementation(() => ({}));
        const service = new CatalogueService();
        // @ts-ignore
        expect(service.loadTreeNode()).rejects.toThrowError();

        service.loadTreeNode({ id: 0, catalogueType: MENU_TYPE_ENUM.TASK }, CATALOGUE_TYPE.TASK);

        await new Promise<void>((resolve) => {
            setTimeout(() => {
                expect((molecule.folderTree.update as jest.Mock).mock.calls[0][0]).toMatchSnapshot();
                resolve();
            }, 0);
        });
    });
});
