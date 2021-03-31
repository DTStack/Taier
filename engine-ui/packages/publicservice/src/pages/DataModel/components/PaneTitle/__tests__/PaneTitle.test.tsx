import React from 'react';
import PaneTitle from '../index';
import { cleanup, render } from '@testing-library/react';

describe('component PaneTitle:', () => {
  it('module import:', () => {
    expect(PaneTitle).toBeInstanceOf(Function);
  });

  it('classname:', () => {
    const wrapper = render(<PaneTitle title="title"></PaneTitle>);
    const ele = wrapper.getByTestId('pane-title');
    expect(ele).toHaveClass('pane-title');
    expect(ele.getElementsByClassName('block').length).toBe(1);
    expect(ele.getElementsByClassName('title').length).toBe(1);
  });

  it('title content render:', () => {
    const title = 'title';
    const wrapper = render(<PaneTitle title={title}></PaneTitle>);
    const titleEle = wrapper.getByText(title);
    expect(titleEle).not.toBeNull();
  });

  afterEach(() => {
    cleanup();
  });
});
