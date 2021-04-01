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
import SearchInput from 'components/SearchInput';
import './style';

interface IPropsDimensionSelect {
  cref?: any;
  formValue: any;
  step?: number;
}

const FieldsSelect = (props: IPropsDimensionSelect) => {
  const { cref, formValue = { columns: data }, step } = props;
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [dataSource, setDataSource] = useState<FieldColumn[]>([]);
  const [filter, setFilter] = useState<string>('');

  useEffect(() => {
    setDataSource(formValue.columns);
  }, [formValue.columns]);

  const onChange = (selectedRowKeys) => {
    setSelectedRowKeys(selectedRowKeys);
  };

  const onSelect = (record) => {
    const key = step === EnumModifyStep.DIMENSION_STEP ? 'dimension' : 'metric';
    const ds = dataSource.map((item) => {
      const bool = item.id === record.id ? !item[key] : item[key];
      return {
        ...item,
        [key]: bool,
      };
    });
    setDataSource(ds);
  };

  useEffect(() => {
    if (!formValue.columns) return;
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
      return dataSource || [];
    },
  }));

  const columns = useMemo(() => columnsGenerator({ onInputBlur }), [
    onInputBlur,
  ]);

  const ds = useMemo(() => {
    if (!formValue.columns) return [];
    const reg = new RegExp(filter);
    return formValue.columns.filter(
      (item) => reg.test(item.tableName) || reg.test(item.columnName)
    );
  }, [formValue.columns, filter]);
  return (
    <div ref={cref}>
      <SearchInput placeholder="输入关键字" onSearch={setFilter} />
      <Table
        columns={columns}
        dataSource={ds}
        rowSelection={{
          selectedRowKeys,
          onChange,
          onSelect: onSelect,
        }}
        className="dt-table-border margin-top-13"
        pagination={false}
        rowKey={(record, index) => '' + record.id}
      />
    </div>
  );
};

export default FieldsSelect;
