import React, {
  useCallback,
  useEffect,
  useImperativeHandle,
  useMemo,
  useState,
} from 'react';
import { Table } from 'antd';
import { FieldColumn } from 'pages/DataModel/types';
import { columnsGenerator, data } from './constants';
import { EnumModifyStep } from '../types';

interface IPropsDimensionSelect {
  cref?: any;
  formValue: any;
  step?: number;
}

const DimensionSelect = (props: IPropsDimensionSelect) => {
  const { cref, formValue = { columns: data }, step } = props;
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [dataSource, setDataSource] = useState<FieldColumn[]>([]);

  useEffect(() => {
    setDataSource(formValue.columns);
  }, [formValue.columns]);

  const onChange = (selectedRowKeys) => {
    setSelectedRowKeys(selectedRowKeys);
  };

  const onSelect = (record) => {
    const key = step === EnumModifyStep.DIMENSION_STEP ? 'dimension' : 'metric';
    const ds = dataSource.map((item) => {
      const bool = item.id === record.id ? !record[key] : record[key];
      return {
        ...item,
        [key]: bool,
      };
    });
    setDataSource(ds);
  };

  useEffect(() => {
    // step切换时，清空选中项
    let selectedRowKeys = [];
    if (step === EnumModifyStep.DIMENSION_STEP) {
      selectedRowKeys = formValue.columns
        .filter((item) => item.dimension)
        .map((item) => item.id);
    } else if (step === EnumModifyStep.METRIC_STEP) {
      selectedRowKeys = formValue.columns
        .filter((item) => item.metric)
        .map((item) => item.id);
    }
    setSelectedRowKeys(selectedRowKeys);
  }, [formValue.columns, step]);

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
        dataSource={formValue.columns}
        rowSelection={{
          selectedRowKeys,
          onChange,
          onSelect: onSelect,
        }}
        pagination={false}
        rowKey={(record, index) => '' + record.id}
      />
    </div>
  );
};

export default DimensionSelect;
