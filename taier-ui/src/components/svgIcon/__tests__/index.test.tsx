import { render } from '@testing-library/react';
import SvgIcon from '..';

describe('Test SvgIcon Component', () => {
    it('Should match snapshot', () => {
        expect(render(<SvgIcon linkHref="iconicon_copy" />).asFragment()).toMatchSnapshot();
    });
});
