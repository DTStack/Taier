import React from 'react';
import SearchInput from '../index';
import { cleanup, render } from '@testing-library/react';

let wrapper;
describe('test SearchInput', () => {
  beforeEach(() => {
    wrapper = render(
      <SearchInput
        placeholder="请输入数据源名称或描述"
        width={200}
        onSearch={(value) => {
          console.log('value:', value);
        }}
      />
    );
  });
  afterEach(() => {
    cleanup();
  });
  it('should get placeholder properties', () => {
    expect(wrapper.getByPlaceholderText('请输入数据源名称或描述')).toBeTruthy();
  });

  it('calls onClick prop when clicked', () => {});
});
