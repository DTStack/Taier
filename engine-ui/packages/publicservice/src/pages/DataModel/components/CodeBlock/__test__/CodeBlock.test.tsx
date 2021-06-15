import React from 'react';
import CodeBlock from '../';
import { render, cleanup } from '@testing-library/react';

describe('component CodeBlock:', () => {
  beforeEach(() => cleanup());

  it('module import:', () => {
    expect(CodeBlock).toBeInstanceOf(Function);
  });

  it('empty sql render:', () => {
    const sql = '';
    const wrapper = render(<CodeBlock code={sql} />);
    expect(wrapper.getByText('暂无SQL信息')).not.toBeNull();
    expect(wrapper.getByRole('pre')).toBeNull();
  });

  it('none empty sql render:', () => {
    const sql = 'select name, age from table t1 where id=1';
    const wrapper = render(<CodeBlock code={sql} />);
    expect(wrapper.getByText('暂无SQL信息')).toBeNull();
    expect(wrapper.getByRole('pre')).not.toBeNull();
  });
});
