import api from '@/api';
import { $, $$, triggerOkOnConfirm } from '@/tests/utils';
import { fireEvent, render, waitFor } from '@testing-library/react';
import Queue from '../queue';
import '@testing-library/jest-dom';
import { history } from 'umi';
import { getAllCluster, getClusterDetail, getNodeAddressSelect } from './fixtures/mock';
import { select } from 'ant-design-testing';

jest.mock('@/api');
jest.useFakeTimers();
// use the original Modal
jest.mock('antd', () => {
    const original = jest.requireActual('antd');
    return {
        ...original,
    };
});
jest.mock('umi', () => ({
    history: {
        push: jest.fn(),
    },
}));

describe('Test Queue Page', () => {
    beforeEach(() => {
        (api.getAllCluster as jest.Mock).mockReset().mockResolvedValue(getAllCluster);
        (api.getNodeAddressSelect as jest.Mock).mockReset().mockResolvedValue(getNodeAddressSelect);
        (api.getClusterDetail as jest.Mock).mockReset().mockResolvedValue(getClusterDetail);
    });

    it('Should match snapshot', async () => {
        const { asFragment } = render(<Queue />);

        await waitFor(() => {
            expect($$('.ant-table-row').length).toBe(2);
            expect(asFragment()).toMatchSnapshot();
        });
    });

    it('Should re-request', async () => {
        render(<Queue />);

        // Get data at first time
        await waitFor(() => {
            expect(api.getClusterDetail).toBeCalledTimes(1);
        });

        // Get data since the refresh button clicked
        fireEvent.click($('button.dt-refresh')!.firstChild!);
        await waitFor(() => {
            expect(api.getClusterDetail).toBeCalledTimes(2);
        });

        // Get data since the cluster changed
        select.fireOpen($$<HTMLDivElement>('.ant-form-item')[0]);
        select.fireSelect(document.body, 1);
        document.querySelector('div.ant-select-dropdown')?.remove();

        await waitFor(() => {
            expect(api.getClusterDetail).toBeCalledTimes(3);
        });

        // Get data since the node changed
        select.fireOpen($$<HTMLDivElement>('.ant-form-item')[1]);
        select.fireSelect(document.body, 0);

        await waitFor(() => {
            expect(api.getClusterDetail).toBeCalledTimes(4);
        });
    });

    it('Should support kill jobs', async () => {
        (api.killAllTask as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
        });
        const { getAllByText, findByText } = render(<Queue />);
        await waitFor(() => {
            expect($$('.ant-table-row').length).toBe(2);
        });

        fireEvent.click(getAllByText('杀死全部')[0]);

        expect(
            await findByText('杀死全部', {
                selector: 'span.ant-modal-confirm-title',
            })
        ).toBeInTheDocument();

        await triggerOkOnConfirm();

        await waitFor(() => {
            expect(api.killAllTask).toBeCalled();
        });
    });

    it('History should push', async () => {
        const { findByText } = render(<Queue />);

        expect(await findByText('spark_sql_default_default_batch_Yarn', { selector: 'a' })).toBeInTheDocument();

        fireEvent.click(await findByText('spark_sql_default_default_batch_Yarn', { selector: 'a' }));

        expect(history.push).toBeCalledWith({
            query: {
                clusterName: 'default',
                drawer: 'queue-detail',
                jobResource: 'spark_sql_default_default_batch_Yarn',
                jobStage: '2',
                node: undefined,
            },
        });
    });
});
