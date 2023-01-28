import { render } from '@testing-library/react';
import Language from '..';

jest.mock('@/context', () => {
	const react = jest.requireActual('react');
	return react.createContext({
		supportJobTypes: [{ key: 1, value: 'spark' }],
	});
});

describe('Test Language Component', () => {
	it('Should match snapshot', () => {
		expect(
			render(
				<Language current={{ tab: { id: 1, data: { id: 1, taskType: 1 } } }} />,
			).asFragment(),
		).toMatchSnapshot();
	});

	it('Should return null', () => {
		expect(render(<Language />).asFragment().textContent).toBe('');
	});
});
