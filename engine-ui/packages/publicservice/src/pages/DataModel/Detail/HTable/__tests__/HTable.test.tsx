import React from 'react';
import HTable from '../index';
import { cleanup, render } from '@testing-library/react';
import { IModelDetail } from 'pages/DataModel/types';

const detail: Partial<IModelDetail> = {
  modelName: 'model name',
  dsUrl: 'url',
  creator: 'admin@dtstack.com',
  createTime: '2020-01-01 12:00:00',
  modelPartition: {
    datePartitionColumn: {
      columnName: 'date',
    },
    timePartitionColumn: {
      columnName: 'time',
    },
  },
  remark: 'remark',
};

describe('component HTable:', () => {
  it('module import:', () => {
    expect(HTable).toBeInstanceOf(Function);
  });

  it('render with empty detail data:', () => {
    const wrapper = render(<HTable detail={{}}></HTable>);
    const ele = wrapper.getByTestId('h-table');
    expect(ele.getElementsByTagName('tr').length).toBe(4);
    expect(wrapper.getAllByText('--').length).toBe(7);
  });

  it('render with non-empty detail data:', () => {
    const wrapper = render(<HTable detail={detail}></HTable>);
    wrapper.getByText(detail.modelName);
    wrapper.getByText(detail.dsUrl);
    wrapper.getByText(detail.creator);
    wrapper.getByText(detail.createTime);
    wrapper.getByText(detail.modelPartition.datePartitionColumn.columnName);
    wrapper.getByText(detail.modelPartition.timePartitionColumn.columnName);
    wrapper.getByText(detail.remark);
  });

  afterEach(() => {
    cleanup();
  });
});
