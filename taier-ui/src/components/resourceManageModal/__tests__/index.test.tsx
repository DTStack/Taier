import { cleanup, render, waitFor } from '@testing-library/react';
import { modal, select } from 'ant-design-testing';
import '@testing-library/jest-dom';

import api from '@/api';
import { $$ } from '@/tests/utils';
import ResourceManageModal from '..';

jest.mock('@/api');

describe('Test ResourceManageModal Component', () => {
    beforeEach(() => {
        cleanup();
        jest.useFakeTimers();
        document.body.innerHTML = '';

        (api.getClusterResources as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            data: {
                queues: [{ queueName: 'test' }],
            },
        });
    });

    afterEach(() => {
        jest.useRealTimers();
    });

    it('Should match snapshot', () => {
        render(<ResourceManageModal visible clusterId={1} />);

        expect(document.body).toMatchSnapshot();
    });

    it('Should log Error', async () => {
        const { getByText } = render(<ResourceManageModal visible clusterId={1} />);

        modal.fireOk(document);

        await waitFor(() => {
            expect(getByText('资源队列不可为空！')).toBeInTheDocument();
        });
    });

    it('Should submit successfully', async () => {
        (api.switchQueue as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
        });
        const fn = jest.fn();
        render(<ResourceManageModal visible clusterId={1} onOk={fn} />);

        select.fireOpen(document);
        await waitFor(() => {
            expect($$('div.ant-select-item-option-content').length).toBe(1);
        });

        select.fireSelect(document.body, 0);

        modal.fireOk(document);

        await waitFor(() => {
            expect(fn).toBeCalled();
        });
    });
});
