import { render } from '@testing-library/react';
import EditorEntry from '..';

jest.mock('@dtinsight/molecule/esm/services/keybinding', () => ({
	KeybindingHelper: {
		queryGlobalKeybinding: jest.fn((id) => [id]),
		convertSimpleKeybindingToString: jest.fn((id) => `${id}`),
	},
}));

jest.mock('@dtinsight/dt-utils/lib', () => ({
	Utils: {
		isMacOs: jest.fn(() => false),
	},
}));

describe('Test EditorEntry', () => {
	it('Should match snapshot', () => {
		expect(render(<EditorEntry />).asFragment()).toMatchSnapshot();
	});
});
