import { fireConfirmOnModal, selectItem, toggleOpen } from '@/tests/utils';
import { cleanup, render, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import ResourceManageModal from '..';
import api from '@/api';

jest.mock('@/api');
jest.useFakeTimers();

describe('Test ResourceManageModal Component', () => {
    beforeEach(() => {
        cleanup();
        document.body.innerHTML = '';

        (api.getClusterResources as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            data: {
                queues: [{ queueName: 'test' }],
            },
        });
    });

    it('Should match snapshot', () => {
        const { asFragment } = render(<ResourceManageModal visible clusterId={1} />);

        expect(asFragment()).toMatchSnapshot();
    });

    it('Should log Error', async () => {
        const { getByTestId, getByText } = render(<ResourceManageModal visible clusterId={1} />);

        fireConfirmOnModal(getByTestId);

        await waitFor(() => {
            expect(getByText('资源队列不可为空！')).toBeInTheDocument();
        });
    });

    it('Should submit successfully', async () => {
        (api.switchQueue as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
        });
        const fn = jest.fn();
        const { container, getByTestId } = render(<ResourceManageModal visible clusterId={1} onOk={fn} />);

        toggleOpen(container);
        await waitFor(() => {
            expect(document.querySelectorAll('div.ant-select-item-option-content').length).toBe(1);
        });
        selectItem(0);

        fireConfirmOnModal(getByTestId);

        await waitFor(() => {
            expect(fn).toBeCalled();
        });
    });
});
