import { act } from 'react-dom/test-utils';
import { cleanup, render } from '@testing-library/react';
import { modal } from 'ant-design-testing';
import '@testing-library/jest-dom';

import api from '@/api';
import Publish, { CONTAINER_ID } from '../publish';

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
        const { findByText } = await act(() => {
            return render(
                <div id={CONTAINER_ID}>
                    <Publish taskId={1} />
                </div>
            );
        });

        modal.fireOk(document);
        expect(await findByText('提交成功！')).toBeInTheDocument();
    });
});
