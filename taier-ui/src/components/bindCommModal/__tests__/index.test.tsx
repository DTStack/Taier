import api from '@/api';
import { fireConfirmOnModal } from '@/tests/utils';
import { select } from 'ant-design-testing';
import { cleanup, render, waitFor } from '@testing-library/react';
import BindCommModal from '..';
import '@testing-library/jest-dom';

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
        const { asFragment } = render(
            <BindCommModal
                title="绑定新租户"
                visible
                clusterList={[{ clusterId: 1, clusterName: 'test' }] as any}
                isBindTenant
            />
        );

        expect(asFragment()).toMatchSnapshot();
    });

    it('Should log errors', async () => {
        const { getByTestId, getByText, container, asFragment } = render(
            <BindCommModal
                title="绑定新租户"
                visible
                clusterList={[{ clusterId: 1, clusterName: 'test' }] as any}
                isBindTenant
            />
        );

        fireConfirmOnModal(getByTestId);

        await waitFor(() => {
            expect(getByText('租户不可为空！')).toBeInTheDocument();
            expect(getByText('集群不可为空！')).toBeInTheDocument();
        });

        select.fireOpen(container.querySelectorAll<HTMLElement>('.ant-form-item')[1]);
        select.fireSelect(document.body, 0);

        await waitFor(() => {
            expect(api.getEnginesByCluster).toBeCalled();
            expect(api.getClusterResources).toBeCalled();
        });

        expect(asFragment()).toMatchSnapshot('Show queue field');
    });

    it('Should confirm successfully', async () => {
        const fn = jest.fn();
        const { getByTestId, container } = render(
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

        select.fireOpen(container.querySelectorAll<HTMLElement>('.ant-form-item')[0]);
        select.fireSelect(document.body, 0);
        document.querySelector('div.ant-select-dropdown')?.remove();

        select.fireOpen(container.querySelectorAll<HTMLElement>('.ant-form-item')[1]);
        select.fireSelect(document.body, 0);
        document.querySelector('div.ant-select-dropdown')?.remove();

        await waitFor(() => {
            expect(api.getEnginesByCluster).toBeCalled();
            expect(api.getClusterResources).toBeCalled();
        });

        select.fireOpen(container.querySelectorAll<HTMLElement>('.ant-form-item')[2]);
        select.fireSelect(document.body, 0);

        fireConfirmOnModal(getByTestId);

        await waitFor(() => expect(fn).toBeCalledWith({ tenantId: 1, clusterId: 1, queueName: 'queueName' }));
    });
});
