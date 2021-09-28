import React from 'react';
import Container from '../index';
import { cleanup, render } from '@testing-library/react';

describe('component Container:', () => {
  it('module import:', () => {
    expect(Container).toBeInstanceOf(Function);
  });

  it('classname:', () => {
    const wrapper = render(<Container></Container>);
    const ele = wrapper.getByTestId('container');
    expect(ele).toHaveClass('dm-container');
  });

  it('null children render:', () => {
    const wrapper = render(<Container></Container>);
    const ele = wrapper.getByTestId('container');
    expect(ele).toBeEmptyDOMElement();
  });

  it('not null children render:', () => {
    const wrapper = render(
      <Container>
        <div>hello, world</div>
      </Container>
    );
    const child = wrapper.getByText('hello, world');
    expect(child).not.toBeNull();
  });

  afterEach(() => {
    cleanup();
  });
});
