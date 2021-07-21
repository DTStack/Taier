import React, {
  useCallback,
  useEffect,
  useMemo,
  useState,
  useRef,
} from 'react';
import Container from '../components/Container';
import { Table, Pagination, Modal, Drawer, Button } from 'antd';
import { IModelData } from '../types';
import { EnumModelActionType } from './types';
import { EnumModelStatus } from 'pages/DataModel/types';
import { columnsGenerator } from './constants';
import Message from 'pages/DataModel/components/Message';
import Detail from '../Detail';
import { API } from '@/services';
import './style';
import _ from 'lodash';
import SearchInput from 'components/SearchInput';
import historyPsuhWithQuery from '@/utils/historyPushWithQuery';

interface IPagination {
  current: number;
  size: number;
  total: number;
}

interface IReqParams {
  asc: boolean;
  currentPage: number;
  field: string;
  search: string;
  pageSize: number;
  datasourceTypes: number[];
  statuses: EnumModelStatus[];
}
interface IModelAction {
  type: EnumModelActionType;
  id: number;
}

interface IPropList {
  router?: any;
}

const List = (props: IPropList) => {
  const { router } = props;
  const [modelList, setModelList] = useState<IModelData[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [pagination, setPagination] = useState<IPagination>({
    current: 1,
    size: 10,
    total: 0,
  });

  const [requestParams, setRequestParams] = useState<IReqParams>({
    asc: true,
    currentPage: 1,
    field: '',
    search: '',
    pageSize: 10,
    datasourceTypes: [1, 2],
    statuses: [
      EnumModelStatus.OFFLINE,
      EnumModelStatus.RELEASE,
      EnumModelStatus.UNRELEASE,
    ],
  });
  const [dataSourceTypeList, setDataSourceTypeList] = useState([]);

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
        setModelList(
          data.data.map((item) => {
            Object.keys(item).map(
              (key) => !item[key] && item[key] !== 0 && (item[key] = '--')
            );
            return item;
          })
        );
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
      const { success, data, message } = await API.getDataSourceTypeList();
      if (success) {
        setDataSourceTypeList(
          data.map((item) => ({
            value: item.leftValue,
            text: item.rightValue,
          }))
        );
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
        fetchModelList(requestParams);
      } else {
        if (apiAction === API.deleteModel) {
          Message.msgError(message);
        } else {
          Message.error(message);
        }
      }
    } catch (error) {
      Message.error(error.message);
    }
  }, []);

  // 删除按钮点击事件处理，二次确认弹窗
  const handleDeleteBtnClick = async (id) => {
    // 请求判断当前模型是否被下游应用引用
    const { success, data, message } = await API.isModelReferenced({ id });
    if (!success) return Message.error(message);
    let content = '';
    if (data.ref) {
      // 数据模型已经被引用
      const prods = data.prod || [];
      content = `该模型已经${prods.join('、')}被引用，修改后可能导致数据异常。`;
    }
    Modal.confirm({
      title: (
        <span className="cus-modal margin-left-40">确认要删除这条模型？</span>
      ),
      content: <span className="cus-modal margin-left-40">{content}</span>,
      onOk() {
        handleModelAction({
          type: EnumModelActionType.DELETE,
          id,
        });
      },
      okText: '删除',
      cancelText: '取消',
      okButtonProps: {
        className: 'cus-modal btn-delete',
      },
      icon: (
        <i className="cus-modal icon iconfont2 iconFilltianchong_Close-Circle-Fill" />
      ),
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

  /**
   * 点击编辑按钮
   * 判断模型是否被下游产品引用，给出提示
   * @param id 模型id
   * @returns
   */
  const handleEditBtnClick = async (id: number) => {
    const { success, data, message } = await API.isModelReferenced({ id });
    if (!success) return Message.error(message);
    if (data.ref) {
      const prods = data.prod || [];
      const title = `该模型已经被${prods.join(
        '、'
      )}引用，修改后可能导致数据异常，确定编辑吗？`;
      Modal.confirm({
        title: title,
        onOk: () => {
          historyPsuhWithQuery(router, `/data-model/edit/${id}`);
        },
        okText: '确定',
        cancelText: '取消',
      });
    } else {
      historyPsuhWithQuery(router, `/data-model/edit/${id}`);
    }
  };

  const columns = useMemo(() => {
    return columnsGenerator({
      handleModelAction,
      handleDeleteBtnClick,
      handleModelNameClick,
      handleEditBtnClick,
      dataSourceFilterOptions: dataSourceTypeList,
    });
  }, [
    handleModelAction,
    handleDeleteBtnClick,
    handleModelNameClick,
    handleEditBtnClick,
    dataSourceTypeList,
  ]);

  useEffect(() => {
    fetchModelList(requestParams);
  }, [requestParams]);
  const container = useRef(null);

  const y = useMemo(() => {
    if (container.current === null) return 0;
    return parseInt(getComputedStyle(container.current)['height']) - 195;
  }, [container.current]);
  return (
    <div ref={container} className="dm-list" data-testid="data-model-list">
      <Container>
        <header className="search-area">
          <SearchInput
            placeholder="模型名称/英文名"
            className="dt-form-shadow-bg"
            onSearch={(value) => {
              setRequestParams((prev) => ({ ...prev, search: value }));
            }}
          />
          <Button
            className="float-right"
            type="primary"
            onClick={() => historyPsuhWithQuery(router, '/data-model/add')}>
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
            scroll={{ x: 1300, y }}
            onChange={(pagination, filters) => {
              const {
                dataSourceType = requestParams.datasourceTypes,
                modelStatus = requestParams.statuses,
              } = filters;
              setRequestParams((reqParams) => ({
                ...reqParams,
                currentPage: 1,
                statuses: modelStatus as EnumModelStatus[],
                datasourceTypes: dataSourceType as number[],
              }));
            }}
          />
          <Drawer
            closable={false}
            destroyOnClose={true}
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
            <div
              className="slider"
              onClick={() => setDrawer({ visible: false, modelId: -1 })}>
              <i className="iconfont2 iconOutlinedxianxing_shouqi" />
            </div>
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
                  currentPage: current,
                  pageSize: size,
                }));
              }}
            />
            <span className="tips">
              共<span className="highlight">{pagination.total}</span>
              条数据，每页显示
              <span className="highlight">{pagination.size}</span>条
            </span>
          </div>
        </div>
      </Container>
    </div>
  );
};

export default List;
