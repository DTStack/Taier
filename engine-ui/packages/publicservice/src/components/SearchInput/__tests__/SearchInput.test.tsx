import React from 'react';
import SearchInput from '../index';
import { cleanup, render, fireEvent } from '@testing-library/react';

describe('test SearchInput', () => {
  afterEach(() => {
    cleanup();
  });

  it('should get search input component', () => {
    let { queryByTestId } = render(
      <SearchInput
        placeholder="请输入数据源名称或描述"
        width={200}
        onSearch={(value) => {
          console.log('value:', value);
        }}
      />
    );
    expect(queryByTestId('input')).not.toBeNull();
  });

  it('should get placeholder property', () => {
    let { getByPlaceholderText } = render(
      <SearchInput
        placeholder="请输入数据源名称或描述"
        width={200}
        onSearch={(value) => {
          console.log('value:', value);
        }}
      />
    );
    expect(getByPlaceholderText('请输入数据源名称或描述')).toBeTruthy();
  });

  it('should get change value', () => {
    const { queryByTestId } = render(
      <SearchInput
        onSearch={(value) => {
          console.log('value-search:', value);
        }}
      />
    );
    fireEvent.change(queryByTestId('input'), {
      target: { value: 'description' },
    });
    const id: any = queryByTestId('input');
    expect(id.value).toBe('description');
  });

  it('should get search event', () => {
    const myMockSearch = jest.fn((value: any) => {
      return { value };
    });
    const { queryByTestId } = render(<SearchInput onSearch={myMockSearch} />);

    fireEvent.change(queryByTestId('input'), {
      target: { value: '123456789' },
    });
    fireEvent.click(queryByTestId('search-icon'));
    expect(myMockSearch.mock.calls.length).toBe(1);
  });
});
