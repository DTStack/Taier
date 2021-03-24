import React, {
  useCallback,
  useImperativeHandle,
  useMemo,
  useState,
} from 'react';
import { Table } from 'antd';
import { FieldColumn } from 'pages/DataModel/types';
import { columnsGenerator, data } from './constants';

interface IPropsDimensionSelect {
  cref?: any;
  formValue: any;
}

const DimensionSelect = (props: IPropsDimensionSelect) => {
  const { cref, formValue = { dimensionColumns: data } } = props;
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const onChange = (rowKeys) => {
    setSelectedRowKeys(rowKeys);
  };

  const [dataSource, setDataSource] = useState<FieldColumn[]>([]);

  const onInputBlur = useCallback(
    (id, value) => {
      // TODO: 前端性能瓶颈
      setDataSource((dataSource) =>
        dataSource.map((item) => {
          if (item.id === id) {
            return {
              ...item,
              columnComment: value,
            };
          } else {
            return item;
          }
        })
      );
    },
    [dataSource]
  );

  useImperativeHandle(cref, () => ({
    getValue: () => {
      return dataSource;
    },
  }));

  const columns = useMemo(() => columnsGenerator({ onInputBlur }), [
    onInputBlur,
  ]);
  return (
    <div ref={cref}>
      <Table
        columns={columns}
        dataSource={formValue.dimensionColumns}
        rowSelection={{
          selectedRowKeys,
          onChange,
        }}
        pagination={false}
        rowKey={(record, index) => '' + index}
      />
    </div>
  );
};

export default DimensionSelect;
