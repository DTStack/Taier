import React, {
  useCallback,
  useEffect,
  useImperativeHandle,
  useMemo,
  useState,
} from 'react';
import { Table } from 'antd';
import { FieldColumn, IModelDetail } from 'pages/DataModel/types';
import { columnsGenerator } from './constants';
import { EnumModifyStep } from '../types';
import SearchInput from 'components/SearchInput';
import './style';
import { API } from '@/services';
import Message from 'pages/DataModel/components/Message';

interface IPropsDimensionSelect {
  cref?: any;
  step?: number;
  modelDetail: IModelDetail;
}

const idGenerator = () => {
  let _id = 0;
  return () => ++_id;
};
const id = idGenerator();

const FieldsSelect = (props: IPropsDimensionSelect) => {
  const { cref, modelDetail, step } = props;
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [dataSource, setDataSource] = useState<Partial<FieldColumn>[]>([]);
  const [filter, setFilter] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);

  const getColumnList = async (
    options: { datasourceId: number; schema: string; tableName: string }[]
  ) => {
    if (options.length === 0) return;
    setLoading(true);
    try {
      const { success, data, message } = await API.getDataModelColumns(options);
      if (success) {
        data.forEach((item) => {
          item.id = id();
        });
        setDataSource(data);
        window.localStorage.setItem('refreshColumns', 'false');
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (window.localStorage.getItem('refreshColumns') === 'true')
      getColumnList(
        modelDetail.joinList
          .map((item) => ({
            datasourceId: modelDetail.dsId,
            schema: item.schema,
            tableName: item.table,
          }))
          .concat({
            datasourceId: modelDetail.dsId,
            schema: modelDetail.schema,
            tableName: modelDetail.tableName,
          })
      );
    else setDataSource(modelDetail.columns);
  }, [modelDetail, step]);

  useEffect(() => {
    // step切换时，清空选中项
    let selectedRowKeys = [];
    if (step === EnumModifyStep.DIMENSION_STEP) {
      selectedRowKeys = dataSource
        .filter((item) => item.dimension)
        .map((item) => item.id);
    } else if (step === EnumModifyStep.METRIC_STEP) {
      selectedRowKeys = dataSource
        .filter((item) => item.metric)
        .map((item) => item.id);
    }
    setSelectedRowKeys(selectedRowKeys);
  }, [dataSource, step]);

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

  const onInputBlur = useCallback(
    (id, value) => {
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
      return {
        columns: dataSource || [],
      };
    },
    validate: () =>
      new Promise((resolve) => {
        return resolve({
          columns: dataSource || [],
        });
      }),
  }));

  const columns = useMemo(
    () => columnsGenerator({ onInputBlur, data: dataSource }),
    [onInputBlur]
  );

  const ds = useMemo(() => {
    const reg = new RegExp(filter);
    return dataSource.filter(
      (item) => reg.test(item.tableName) || reg.test(item.columnName)
    );
  }, [dataSource, filter]);

  return (
    <div ref={cref}>
      <SearchInput placeholder="输入关键字" onSearch={setFilter} />
      <Table
        loading={loading}
        columns={columns}
        dataSource={ds}
        rowSelection={{
          selectedRowKeys,
          onChange,
          onSelect: onSelect,
        }}
        className="dt-table-border margin-top-13"
        pagination={false}
        rowKey={(record, index) => record.id}
        scroll={{
          y: 380,
        }}
      />
    </div>
  );
};

export default FieldsSelect;
