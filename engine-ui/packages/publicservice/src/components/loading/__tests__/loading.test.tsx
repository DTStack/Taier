import React from 'react';
import { render } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import Loading from '../index';

let wrapper, element;
describe('test Loading', () => {
  beforeEach(() => {
    wrapper = render(<Loading />);
    element = wrapper.getByTestId('test-loading');
  });
  test('should render correct className', () => {
    expect(element).toBeInTheDocument();
    expect(element).toHaveClass('loading');
  });
  test('should render correct children', () => {
    expect(wrapper.container.querySelectorAll('.dot').length).toEqual(5);
  });
});
