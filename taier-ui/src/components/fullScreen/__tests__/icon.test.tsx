import { render } from '@testing-library/react';
import Icon from '../icon';

describe('Test Icon Component', () => {
    it('Should match snapshots', () => {
        expect(render(<Icon />).asFragment()).toMatchSnapshot();
        expect(render(<Icon type />).asFragment()).toMatchSnapshot();
        expect(render(<Icon themeDark />).asFragment()).toMatchSnapshot();
        expect(render(<Icon type themeDark />).asFragment()).toMatchSnapshot();
    });
});
