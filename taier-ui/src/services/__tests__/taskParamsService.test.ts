import api from '@/api';
import { PARAMS_ENUM } from '@/constant';
import { waitFor } from '@testing-library/react';
import TaskParamsService from '../taskParamsService';

jest.mock('@/api');

describe('Test TaskParamService', () => {
    it('Should support get default system params when constructor', () => {
        (api.getCustomParams as jest.Mock).mockResolvedValue({
            code: 1,
            data: [
                {
                    id: 1,
                    paramCommand: 'test',
                    paramName: 'test',
                    type: PARAMS_ENUM.SYSTEM,
                },
            ],
        });

        const taskParamsService = new TaskParamsService();

        waitFor(() => {
            expect(taskParamsService.getState().systemParams).toEqual([
                { id: 1, paramCommand: 'test', paramName: 'test', type: PARAMS_ENUM.SYSTEM },
            ]);
        });
    });

    it('Should match params', async () => {
        (api.getCustomParams as jest.Mock).mockResolvedValue({
            code: 1,
            data: [
                {
                    id: 1,
                    paramCommand: 'test',
                    paramName: 'test',
                    type: PARAMS_ENUM.SYSTEM,
                },
            ],
        });

        const taskParamsService = new TaskParamsService();

        await waitFor(() => {
            expect(taskParamsService.getState().systemParams).toEqual([
                { id: 1, paramCommand: 'test', paramName: 'test', type: PARAMS_ENUM.SYSTEM },
            ]);
        });

        expect(taskParamsService.matchTaskParams('${test};${a.b}')).toEqual([
            { paramCommand: 'test', paramName: 'test', type: PARAMS_ENUM.SYSTEM },
            { paramCommand: '', paramName: 'a.b', type: PARAMS_ENUM.CUSTOM },
        ]);
    });
});
