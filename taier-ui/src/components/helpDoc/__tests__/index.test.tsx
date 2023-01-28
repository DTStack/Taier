import { render } from '@testing-library/react';
import HelpDoc from '..';

describe('Test HelpDoc Component', () => {
	it('Should match snapshot', () => {
		expect(render(<HelpDoc />).asFragment()).toMatchSnapshot();
	});
});
