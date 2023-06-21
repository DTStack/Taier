import { cleanup, render, waitFor } from '@testing-library/react';
import { act } from 'react-dom/test-utils';
import '@testing-library/jest-dom';
import Publish, { CONTAINER_ID } from '../publish';
import api from '@/api';
import { modal } from 'ant-design-testing';

jest.mock('@/api');

jest.mock('@/utils', () => ({
    getTenantId: jest.fn(() => 1),
    getUserId: jest.fn(() => 1),
}));

describe('Test Publish Component', () => {
    beforeEach(() => {
        cleanup();
        document.body.innerHTML = '';
    });

    it('Should match snapshot', () => {
        const { asFragment } = render(
            <div id={CONTAINER_ID}>
                <Publish taskId={1} />
            </div>
        );

        expect(asFragment()).toMatchSnapshot();
    });

    it('Should submit', async () => {
        (api.publishOfflineTask as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
        });
        act(() => {
            render(<Publish taskId={1} />);
        });

        modal.fireOk(document);

        await waitFor(() => {
            expect(document.querySelector('.ant-message-notice-content')).not.toBeUndefined();
        });
    });
});
