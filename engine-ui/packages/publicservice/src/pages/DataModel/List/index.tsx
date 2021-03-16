import React, { useCallback, useEffect, useMemo, useState } from 'react';
import Container from '../components/Container';
import { Input, Table, message as Message, notification, Pagination, Modal, Drawer, Button } from 'antd'
const { Search } = Input;
import { IModelData } from '../types';
import { EnumModelActionType } from './types';
import { columnsGenerator } from './constants';
import Detail from '../Detail';
import { API } from '@/services';
import './style';

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
}
interface IModelAction {
  type: EnumModelActionType,
  id: number,
}

const List = () => {
  const [modelList, setModelList] = useState<IModelData[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [pagination, setPagination] = useState<IPagination>({
    current: 1,
    size: 10,
    total: 0,
  })

  const [requestParams, setRequestParams] = useState<IReqParams>({
    asc: true,
    current: 1,
    field: "",
    search: "",
    size: 10
  })

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
      if(success) {
        setModelList(data.contentList);
        setPagination({
          current: data.current,
          size: data.size,
          total: data.total
        })
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    } finally {
      setLoading(false);
    }
  }

  // TODO: 文案
  const handleModelAction = useCallback(async (action: IModelAction) => {
    const { type, id } = action;
    let apiAction, messageConfig, messageActor, msg = { title: '', message: '' };
    switch(type) {
      case EnumModelActionType.DELETE:
        apiAction = API.deleteModel;
        messageConfig = ({ message }) => {
          return {
            content: message,
          }
        }
        msg = {
          title: '',
          message: '模型删除成功'
        }
        messageActor = Message;
        break;
      case EnumModelActionType.RELEASE:
        apiAction = API.releaseModel;
        messageConfig = ({ title, message }) => {
          return {
            message: title,
            description: message,
          }
        }
        msg = {
          title: '模型发布成功',
          message: ''
        }
        messageActor = notification;
        break;
      case EnumModelActionType.UNRELEASE:
        apiAction = API.unreleaseModel;
        messageConfig = ({ title, message }) => {
          return {
            message: title,
            description: message,
          }
        }
        msg = {
          title: '模型下线成功',
          message: ''
        }
        messageActor = notification;
        break;
    }

    try {
      const { success, message } = await apiAction({ id });
      if(success) {
        messageActor.success(messageConfig(msg));
      } else {
        messageActor.error(messageConfig({ message }))
      }
    } catch(error) {
      messageActor.error(messageConfig({ message: error.message }));
    }
  }, [])

  // 删除按钮点击事件处理，二次确认弹窗
  const handleDeleteBtnClick = (id) => {
    // TODO: 提示文案待修改
    Modal.confirm({
      title: '确认删除吗',
      content: 'aaaaa',
      onOk() {
        handleModelAction({
          type: EnumModelActionType.DELETE,
          id
        })
      },
    })
  }

  /**
   * 更新模型详情ID
   * 模型名称点击回调
   * @param id
   */
  const handleModelNameClick = (id: number) => {
    setDrawer({
      visible: true,
      modelId: id,
    })
  }

  const columns = useMemo(() => {
    return columnsGenerator({ handleModelAction, handleDeleteBtnClick, handleModelNameClick });
  }, [handleModelAction, handleDeleteBtnClick, handleModelNameClick]);

  useEffect(() => {
    fetchModelList(requestParams);
  }, [requestParams]);

  return <div className="dm-list">
    <Container>
      <header className="search-area">
        <Search
          className="search"
          placeholder="模型名称/英文名"
          onSearch={value => setRequestParams(prev => ({ ...prev, search: value }))}
        />
        <Button className="float-right" type="primary">新建模型</Button>
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
        />
        <Drawer
          closable={false}
          visible={drawer.visible}
          className="drawer"
          width={1000}
          getContainer={() => {
            return document.querySelector('.table-area')
          }}
          mask={false}
          onClose={() => {
            setDrawer({
              visible: false,
              modelId: -1
            })
          }}
        >
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
              setRequestParams(prev => ({
                ...prev,
                current,
                size
              }))
            }}
          />
        </div>
      </div>
    </Container>
  </div>
}

export default List;
