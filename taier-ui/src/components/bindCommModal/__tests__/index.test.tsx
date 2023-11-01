import { cleanup, render, waitFor } from '@testing-library/react';
import { form,modal, select } from 'ant-design-testing';
import '@testing-library/jest-dom';

import api from '@/api';
import { $ } from '@/tests/utils';
import BindCommModal from '..';

jest.mock('@/api');

describe('Test BindCommModal Component', () => {
    beforeEach(() => {
        cleanup();
        document.body.innerHTML = '';
        jest.useFakeTimers();
        (api.getTenantList as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            data: [
                {
                    tenantId: 1,
                    tenantName: 'test',
                },
            ],
        });

        (api.getEnginesByCluster as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            data: {
                engines: [
                    {
                        id: 0,
                        gmtCreate: null,
                        gmtModified: null,
                        isDeleted: 0,
                        components: null,
                        clusterId: -1,
                        engineName: 'Hadoop',
                        engineType: 1,
                    },
                ],
            },
        });

        (api.getClusterResources as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            data: {
                queues: [
                    {
                        queueName: 'queueName',
                    },
                ],
            },
        });
    });

    afterEach(() => {
        jest.useRealTimers();
    });

    it('Should match snapshot', () => {
        render(
            <BindCommModal
                title="绑定新租户"
                visible
                clusterList={[{ clusterId: 1, clusterName: 'test' }] as any}
                isBindTenant
            />
        );

        expect(document.body).toMatchSnapshot();
    });

    it('Should log errors', async () => {
        const { getByText } = render(
            <BindCommModal
                title="绑定新租户"
                visible
                clusterList={[{ clusterId: 1, clusterName: 'test' }] as any}
                isBindTenant
            />
        );

        modal.fireOk(document.body);

        await waitFor(() => {
            expect(getByText('租户不可为空！')).toBeInTheDocument();
            expect(getByText('集群不可为空！')).toBeInTheDocument();
        });

        select.fireOpen(form.queryFormItems(document, 1)!);
        select.fireSelect(document.body, 0);

        await waitFor(() => {
            expect(api.getEnginesByCluster).toBeCalled();
            expect(api.getClusterResources).toBeCalled();
        });
    });

    it('Should confirm successfully', async () => {
        const fn = jest.fn();
        render(
            <BindCommModal
                title="绑定新租户"
                visible
                clusterList={[{ clusterId: 1, clusterName: 'test' }] as any}
                isBindTenant
                onOk={fn}
            />
        );

        await waitFor(() => {
            expect(api.getTenantList).toBeCalled();
        });

        select.fireOpen(form.queryFormItems(document.body, 0)!);
        select.fireSelect(document.body, 0);
        $('div.ant-select-dropdown')?.remove();

        select.fireOpen(form.queryFormItems(document.body, 1)!);
        select.fireSelect(document.body, 0);
        $('div.ant-select-dropdown')?.remove();

        await waitFor(() => {
            expect(api.getEnginesByCluster).toBeCalled();
            expect(api.getClusterResources).toBeCalled();
        });

        select.fireOpen(form.queryFormItems(document.body, 2)!);
        select.fireSelect(document.body, 0);

        modal.fireOk(document.body);

        await waitFor(() => expect(fn).toBeCalledWith({ tenantId: 1, clusterId: 1, queueName: 'queueName' }));
    });
});
