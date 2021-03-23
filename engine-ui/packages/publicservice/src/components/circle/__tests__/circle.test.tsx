import React from 'react';
import { Circle } from '../index';
import { render, cleanup } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
const defaultProps = {
    className: 'test-circle',
    children: <div>circle</div>
}

let wrapper, wrapper2, element, element2;
describe('test circle', () => {
    beforeEach(() => {
        wrapper = render(<Circle {...defaultProps} data-testid='test1'></Circle>)
        wrapper2 = render(<Circle data-testid='test2' />)
        element = wrapper.getByTestId('test1');
        element2 = wrapper2.getByTestId('test2')
    })
    afterEach(() => {
        cleanup();
    })
    test('should render the correct className in Circle', () => {
        expect(element).toBeInTheDocument();
        expect(element).toHaveClass('circle_default test-circle');
    })
    test('should render the correct children in Circle', () => {
        expect(wrapper.getByText('circle')).toBeInTheDocument();
    })
    test('should render nothing without children', () => {
        expect(element2).toBeEmptyDOMElement();
    })
})
