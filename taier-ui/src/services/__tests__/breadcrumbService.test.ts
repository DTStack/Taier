import { TreeViewUtil } from '@dtinsight/molecule/esm/common/treeUtil';

import BreadcrumbService from '../breadcrumbService';
import { catalogueService } from '../';

class MockService {
    private cb = () => {};
    onUpdate = (cb: any) => {
        this.cb = cb;
    };
    emitUpdate = () => this.cb();
    getRootFolder = jest.fn();
}

jest.mock('@/services', () => {
    return {
        catalogueService: new (class {
            private cb = () => {};
            onUpdate = (cb: any) => {
                this.cb = cb;
            };
            emitUpdate = () => this.cb();
            getRootFolder = jest.fn();
        })(),
    };
});

describe('Test breadcrumb service', () => {
    beforeEach(() => {
        (catalogueService.getRootFolder as jest.Mock).mockReset();
        (TreeViewUtil as jest.Mock).mockReset();
    });

    it('Should initial with null', () => {
        const service = new BreadcrumbService();
        // @ts-ignore
        expect(service.hashTree).toBe(null);
    });

    it('Should listen to the catalogue service changed', () => {
        (catalogueService.getRootFolder as jest.Mock).mockImplementation(() => ({}));
        (TreeViewUtil as jest.Mock).mockImplementation(() => ({}));

        const service = new BreadcrumbService();
        // @ts-ignore
        expect(service.hashTree).toBe(null);

        // call onUpdate callback
        (catalogueService as unknown as MockService).emitUpdate();

        // @ts-ignore
        expect(service.hashTree).toEqual({});
    });

    it('Should get breadcrumb by id', () => {
        (catalogueService.getRootFolder as jest.Mock).mockImplementation(() => ({}));
        (TreeViewUtil as jest.Mock).mockImplementation(() => ({
            getHashMap: jest
                .fn()
                .mockImplementationOnce(() => ({
                    node: {
                        id: '1',
                        name: 'test',
                    },
                    parent: '2',
                }))
                .mockImplementationOnce(() => ({
                    node: {
                        id: '2',
                        name: 'test2',
                    },
                })),
        }));

        const service = new BreadcrumbService();

        expect(service.getBreadcrumb('1')).toEqual([]);

        // call onUpdate callback
        (catalogueService as unknown as MockService).emitUpdate();

        expect(service.getBreadcrumb('1')).toEqual([
            { id: '2', name: 'test2' },
            { id: '1', name: 'test' },
        ]);
    });
});
