import React from 'react';
import List from '../index';
import { cleanup, render, fireEvent } from '@testing-library/react';

const mockPush = jest.fn();

const router = {
  push: mockPush,
};

let wrapper, ele;
describe('page DataModelList:', () => {
  beforeEach(() => {
    wrapper = render(<List router={router} />);
    ele = wrapper.getByTestId('data-model-list');
  });
  it('module import:', () => {
    expect(List).toBeInstanceOf(Function);
  });
  it('render item:', () => {
    const header = ele.querySelector('.search-area');
    const table = ele.querySelector('.table-area');
    expect(header).not.toBeNull();
    expect(wrapper.getByPlaceholderText('模型名称/英文名')).not.toBeNull();
    expect(table).not.toBeNull();
    expect(wrapper.getByText('新建模型')).not.toBeNull();
  });

  it('add model button click:', () => {
    expect(mockPush.mock.calls.length).toBe(0);
    fireEvent.click(wrapper.getByText('新建模型'));
    expect(mockPush.mock.calls.length).toBe(1);
  });
  afterEach(() => {
    cleanup();
  });
});
