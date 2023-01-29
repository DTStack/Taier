import { selectItem, toggleOpen } from '@/tests/utils';
import { render } from '@testing-library/react';
import Result from '../result';

jest.useFakeTimers();

describe('Test Result Component', () => {
    beforeEach(() => {
        jest.spyOn(global.Math, 'random').mockReturnValue(10);
    });

    it('Should match snapshot', () => {
        const { asFragment } = render(
            <Result
                data={[
                    ['age', 'name'],
                    ['1', 'bob'],
                    ['2', 'sam'],
                ]}
                tab={{
                    tableType: 0,
                    tableNameArr: ['option1'],
                }}
                extraView={<span data-testid="extraView" />}
            />
        );

        expect(asFragment()).toMatchSnapshot();
    });

    it('Should call getTableData on didMount', () => {
        const fn = jest.fn();
        render(
            <Result
                data={[
                    ['age', 'name'],
                    ['1', 'bob'],
                    ['2', 'sam'],
                ]}
                tab={{
                    tableType: 1,
                    tableNameArr: ['option1'],
                }}
                getTableData={fn}
            />
        );

        expect(fn).toBeCalled();
    });

    it('Should trigger updateTableData event handler', () => {
        const fn = jest.fn();
        const { container } = render(
            <Result
                data={[
                    ['age', 'name'],
                    ['1', 'bob'],
                    ['2', 'sam'],
                ]}
                tab={{
                    tableType: 1,
                    tableNameArr: ['option1'],
                }}
                updateTableData={fn}
            />
        );

        toggleOpen(container);
        selectItem(0);

        expect(fn).toBeCalled();
    });
});
