import { EnumModelStatus } from '@/pages/DataModel/types';
import React from 'react';
import VersionHistory from '../';
import { render } from '@testing-library/react';

describe('version history:', () => {
  it('module import:', () => {
    expect(VersionHistory).toBeInstanceOf(Function);
  });
  it('render:', () => {
    const wrapper = render(
      <VersionHistory modelId={-1} modelStatus={EnumModelStatus.OFFLINE} />
    );
    expect(wrapper.queryByText('版本对比')).not.toBeNull();
    expect(wrapper.queryAllByRole('table')).not.toBeNull();
  });
});
