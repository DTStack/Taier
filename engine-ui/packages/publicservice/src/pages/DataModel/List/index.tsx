import React, { useCallback, useEffect, useMemo, useState } from 'react';
import Container from '../components/Container';
import { Input, Table, Pagination, Modal, Drawer, Button } from 'antd';
import { IModelData } from '../types';
import { EnumModelActionType } from './types';
import { columnsGenerator } from './constants';
import Message from 'pages/DataModel/components/Message';
import Detail from '../Detail';
import { API } from '@/services';
import './style';
import _ from 'lodash';
const { Search } = Input;

interface IPagination {
  current: number;
  size: number;
  total: number;
}

interface IReqParams {
  asc: boolean;
  current: number;
  field: string;
  search: string;
  size: number;
  // TODO:筛选字段名称参数需要变更
  // dataSourceId: string | number;
  // modelStatus: 0 | 1 | 2;
}
interface IModelAction {
  type: EnumModelActionType;
  id: number;
}

const List = () => {
  const [modelList, setModelList] = useState<IModelData[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [pagination, setPagination] = useState<IPagination>({
    current: 1,
    size: 10,
    total: 0,
  });

  const [requestParams, setRequestParams] = useState<IReqParams>({
    asc: true,
    current: 1,
    field: '',
    search: '',
    size: 10,
    // dataSourceId: '',
    // modelStatus: 0,
  });
  const [usedDataSourceList, setUsedDatasourceList] = useState([]);

  const [drawer, setDrawer] = useState({
    visible: false,
    modelId: -1,
  });

  /**
   * 获取数据模型列表
   * @param requestParams
   */
  const fetchModelList = async (requestParams: IReqParams) => {
    try {
      setLoading(true);
      const { success, data, message } = await API.getModelList(requestParams);
      if (success) {
        setModelList(data.data);
        setPagination({
          current: data.currentPage,
          size: data.pageSize,
          total: data.totalCount,
        });
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    } finally {
      setLoading(false);
    }
  };

  const fetchFilterDataSourceList = async () => {
    try {
      const {
        success,
        data,
        message,
      } = await API.getDataModelUsedDataSourceList();
      if (success) {
        setUsedDatasourceList(data);
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    }
  };

  useEffect(() => {
    fetchFilterDataSourceList();
  }, []);

  // TODO: icon,toast位置
  const handleModelAction = useCallback(async (action: IModelAction) => {
    const { type, id } = action;
    let apiAction, msg;
    switch (type) {
      case EnumModelActionType.DELETE:
        apiAction = API.deleteModel;
        msg = '模型删除成功';
        break;
      case EnumModelActionType.RELEASE:
        apiAction = API.releaseModel;
        msg = '发布成功';
        break;
      case EnumModelActionType.UNRELEASE:
        apiAction = API.unreleaseModel;
        msg = '下线成功';
        break;
    }

    try {
      const { success, message } = await apiAction({ id });
      if (success) {
        Message.success(msg);
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    }
  }, []);

  // 删除按钮点击事件处理，二次确认弹窗
  const handleDeleteBtnClick = (id) => {
    // TODO: icon待替换
    Modal.confirm({
      title: '确认要删除这条模型？',
      content: '删除后，已经引用该模型的数据将不可用！',
      onOk() {
        handleModelAction({
          type: EnumModelActionType.DELETE,
          id,
        });
      },
    });
  };

  /**
   * 更新模型详情ID
   * 模型名称点击回调
   * @param id
   */
  const handleModelNameClick = (id: number) => {
    setDrawer({
      visible: true,
      modelId: id,
    });
  };

  const columns = useMemo(() => {
    return columnsGenerator({
      handleModelAction,
      handleDeleteBtnClick,
      handleModelNameClick,
      dataSourceFilterOptions: _.uniqBy(
        usedDataSourceList,
        'dsType'
      ).map((item) => ({ text: item.dsTypeName, value: item.dsType })),
    });
  }, [
    handleModelAction,
    handleDeleteBtnClick,
    handleModelNameClick,
    usedDataSourceList,
  ]);

  useEffect(() => {
    fetchModelList(requestParams);
  }, [requestParams]);

  return (
    <div className="dm-list">
      <Container>
        <header className="search-area">
          <Search
            className="search"
            placeholder="模型名称/英文名"
            onSearch={(value) =>
              setRequestParams((prev) => ({ ...prev, search: value }))
            }
          />
          <Button className="float-right" type="primary">
            新建模型
          </Button>
        </header>
        <div className="table-area">
          <Table
            rowKey="id"
            className="table dt-table-border"
            columns={columns as any}
            loading={loading}
            dataSource={modelList}
            pagination={false}
            scroll={{ x: 1300, y: 800 }}
            onChange={(pagination, filters) => {
              // TODO:
              // setRequestParams(reqParams => ({
              //   ...reqParams,
              //   modelStatus: filters.modelStatus as any,
              //   dataSourceId: (filters as any).dataSourceType
              // }))
            }}
          />
          <Drawer
            closable={false}
            visible={drawer.visible}
            className="drawer"
            width={1000}
            getContainer={() => document.querySelector('.table-area')}
            mask={false}
            onClose={() => {
              setDrawer({
                visible: false,
                modelId: -1,
              });
            }}>
            {/* TODO: ICON */}
            <div
              className="slider"
              onClick={() => setDrawer({ visible: false, modelId: -1 })}
            />
            <Detail modelId={drawer.modelId} />
          </Drawer>
          <div className="pagination-container">
            <Pagination
              className="pagination"
              current={pagination.current}
              pageSize={pagination.size}
              total={pagination.total}
              size="small"
              onChange={(current, size) => {
                setRequestParams((prev) => ({
                  ...prev,
                  current,
                  size,
                }));
              }}
            />
          </div>
        </div>
      </Container>
    </div>
  );
};

export default List;
