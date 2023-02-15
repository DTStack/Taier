import { render } from '@testing-library/react';
import PreviewTable from '..';

describe('Test PreviewTable Component', () => {
    it('Should match snapshot', () => {
        const { asFragment } = render(
            <PreviewTable
                data={{
                    columnList: ['age', 'name'],
                    dataList: [
                        ['1', 'test'],
                        ['2', 'jest'],
                    ],
                }}
            />
        );

        expect(asFragment()).toMatchSnapshot();
    });
});
