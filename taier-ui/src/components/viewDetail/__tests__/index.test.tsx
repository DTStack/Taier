import { render } from '@testing-library/react';
import ViewDetail from '..';

describe('Test ViewDetail Component', () => {
	it('Should match snapshot', () => {
		const { asFragment } = render(<ViewDetail />);

		expect(asFragment()).toMatchSnapshot();
	});
});
