import React from 'react';
import SearchInput from '../index';
import { render, cleanup } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';

// const defaultProps = {
//   width: 200,
//   onSearch: (value) => {
//     console.log('value: ', value);
//   },
// };
let wrapper;
describe('test SearchInput', () => {
  //describe(name, fn)：描述块，讲一组功能相关的测试用例组合在一起
  //beforeEach(fn)在每个测试用例执行之前需要执行的方法
  //render，顾名思义，有助于渲染React组件
  // beforeEach(() => {
  //   wrapper = render(<SearchInput {...defaultProps}></SearchInput>);
  //   console.log('wrapper: ', wrapper);
  // });
  it('should take a snapshot', () => {
    wrapper = render(
      <SearchInput
        placeholder="请输入数据源名称或描述"
        width={200}
        onSearch={(value) => {
          console.log(value);
        }}
      />
    );

    expect(wrapper.asFragment()).toMatchSnapshot();
  });

  // it('should get placeholder', () => {
  //   // expect(wrapper.getByTestClassName('ant-input')).
  //   expect(wrapper).toBeInstanceOf("ant-input")
  //  });

  afterEach(() => {
    cleanup(); //在每次测试后清理所有东西，以避免内存泄漏
  });
});
