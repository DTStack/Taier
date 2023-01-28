import molecule from '@dtinsight/molecule';
import { renderHook } from '@testing-library/react';
import useCurrentTheme from '../useCurrentTheme';

describe('Test useCurrentTheme hook', () => {
	it('Should get default theme', () => {
		(molecule.colorTheme.getColorThemeMode as jest.Mock)
			.mockReset()
			.mockImplementation(() => 'dark');

		const { result } = renderHook(() => useCurrentTheme());

		expect(result.current).toEqual(['dark']);
	});
});
