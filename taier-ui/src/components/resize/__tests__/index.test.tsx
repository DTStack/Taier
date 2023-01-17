import { fireEvent, render } from '@testing-library/react';
import Resize from '..';

describe('Test Resize Component', () => {
	it('Should call onResize', () => {
		const fn = jest.fn();
		render(
			<Resize onResize={fn}>
				<div>123</div>
			</Resize>,
		);

		fireEvent.resize(window);
		expect(fn).toBeCalled();
	});
});
