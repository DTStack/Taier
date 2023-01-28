import { render } from '@testing-library/react';
import ScrollText from '..';

describe('Test ScrollText Component', () => {
	it('Should match snapshot', () => {
		expect(render(<ScrollText />).asFragment()).toMatchSnapshot();
	});
});
