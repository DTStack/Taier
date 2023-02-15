import api from '@/api';
import DataSourceService from '../dataSourceService';

jest.mock('@/api');

const mockDataSource = {
    dataDesc: '',
    dataInfoId: 31,
    dataName: 'oc',
    dataType: 'OceanBase',
    dataTypeCode: 49,
    dataVersion: null,
    gmtModified: 1665565180000,
    isImport: null,
    isMeta: 0,
    linkJson: '',
    schemaName: '',
    status: 1,
};

describe('Test dataSource service', () => {
    it('Should init with data', async () => {
        (api.getAllDataSource as jest.Mock).mockResolvedValue({
            code: 1,
            data: [mockDataSource],
        });
        const dataSourceService = new DataSourceService();

        await new Promise<void>((resolve) => {
            setTimeout(() => {
                expect(dataSourceService.getState()).toEqual({
                    dataSource: [mockDataSource],
                });
                resolve();
            }, 0);
        });

        expect(dataSourceService.getDataSource()).toEqual([mockDataSource]);

        expect(api.getAllDataSource).toBeCalledTimes(1);

        dataSourceService.reloadDataSource();
        expect(api.getAllDataSource).toBeCalledTimes(2);
    });
});
