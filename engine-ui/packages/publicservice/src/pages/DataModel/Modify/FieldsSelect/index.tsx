import React, {
  useCallback,
  useEffect,
  useImperativeHandle,
  useMemo,
  useState,
} from 'react';
import { message, Table } from 'antd';
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
    // getColumnList调用逻辑：
    // 新增模型时进入到选择维度时，需要通过dsId,schema,tableName字段获取columnList
    // 新增模型时，修改关联表关联关系时，需要重新获取columnList
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
    /**
     * logic:
     * 当维度选中时，判断对应column是否为度量
     * 若为度量，直接清除度量标记
     */
    const key = step === EnumModifyStep.DIMENSION_STEP ? 'dimension' : 'metric';
    const ds = dataSource.map((item) => {
      const bool = item.id === record.id ? !item[key] : item[key];
      const target = {
        ...item,
        [key]: bool,
      };
      if (
        item.id === record.id &&
        step === EnumModifyStep.DIMENSION_STEP &&
        item.metric
      ) {
        target.metric = false;
      }
      return target;
    });
    setDataSource(ds);
  };

  // 全选逻辑
  const onSelectAll = (selected) => {
    const isDimension = step === EnumModifyStep.DIMENSION_STEP;
    let ds = [];
    if (isDimension) {
      // 维度
      // 维度可以看到所有的表字段，当维度列全选时，需要将所有的字段的dimension置为true
      // 同时需要将所有的metric字段置为false
      ds = dataSource.map((item) => {
        return {
          ...item,
          dimension: selected,
          metric: item.metric === true ? false : item.metric,
        };
      });
    } else {
      ds = dataSource.map((item) => {
        // 判断维度是否已经勾选，若已经勾选，不修改metric字段；若未勾选则更新metric字段
        if (item.dimension === true) return item;
        return {
          ...item,
          metric: selected,
        };
      });
    }
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
      new Promise((resolve, reject) => {
        const isDimension = step === EnumModifyStep.DIMENSION_STEP;
        if (isDimension) {
          if (dataSource.filter((item) => item.dimension).length === 0) {
            message.error('请选择维度');
            return reject();
          }
        } else {
          if (dataSource.filter((item) => item.metric).length === 0) {
            message.error('请选择度量');
            return reject();
          }
        }

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
    const columnFilter =
      step === EnumModifyStep.DIMENSION_STEP
        ? (item) =>
            reg.test(item.tableName) ||
            reg.test(item.columnName) ||
            reg.test(item.columnComment)
        : (item) =>
            !item.dimension &&
            (reg.test(item.tableName) ||
              reg.test(item.columnName) ||
              reg.test(item.columnComment));
    return dataSource.filter(columnFilter);
  }, [dataSource, filter, step]);

  const y = useMemo(() => {
    return document.documentElement.clientHeight - 362;
  }, [cref.current]);

  return (
    <div className="padding-top-20" ref={cref}>
      <SearchInput placeholder="输入关键字" onSearch={setFilter} />
      <Table
        loading={loading}
        columns={columns}
        dataSource={ds}
        rowSelection={{
          selectedRowKeys,
          onChange,
          onSelect,
          onSelectAll,
        }}
        className="dt-table-border dt-table-last-row-noborder margin-top-13"
        pagination={false}
        rowKey={(record, index) => record.id}
        scroll={{
          x: 678,
          y,
        }}
      />
    </div>
  );
};

export default FieldsSelect;
