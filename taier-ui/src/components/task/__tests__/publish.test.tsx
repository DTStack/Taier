import { fireConfirmOnModal } from '@/tests/utils';
import { cleanup, render, waitFor } from '@testing-library/react';
import { act } from 'react-dom/test-utils';
import '@testing-library/jest-dom';
import Publish from '../publish';
import api from '@/api';

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
		const { asFragment } = render(<Publish taskId={1} />);

		expect(asFragment()).toMatchSnapshot();
	});

	it('Should submit', async () => {
		(api.publishOfflineTask as jest.Mock).mockReset().mockResolvedValue({
			code: 1,
		});
		const { getByTestId } = render(<Publish taskId={1} />);

		act(() => {
			fireConfirmOnModal(getByTestId);
		});

		await waitFor(() => {
			expect(document.querySelector('.ant-message-notice-content')).not.toBeUndefined();
		});
	});
});
