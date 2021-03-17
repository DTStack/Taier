import React, { useCallback, useEffect, useMemo, useState } from 'react';
import Container from '../components/Container';
import { Input, Table, message as Message, notification, Pagination, Modal } from 'antd'
import { IModelData } from '../types';
import { EnumModalActionType } from './types';
import { columnsGenerator } from './constants';
import { API } from '@/services';
import './style';
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
}
interface IModelAction {
  type: EnumModalActionType,
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
    let apiAction; let messageConfig; let messageActor; let msg = { title: '', message: '' };
    switch(type) {
      case EnumModalActionType.DELETE:
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
      case EnumModalActionType.RELEASE:
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
      case EnumModalActionType.UNRELEASE:
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
          type: EnumModalActionType.DELETE,
          id
        })
      },
    })
  }

  const columns = useMemo(() => {
    return columnsGenerator({ handleModelAction, handleDeleteBtnClick });
  }, [handleModelAction, handleDeleteBtnClick]);

  useEffect(() => {
    fetchModelList(requestParams);
  }, [requestParams]);

  return <div className="dm-list">
    <Container>
      <header className="search-area">
        <Search
          className="search"
          onSearch={value => setRequestParams(prev => ({ ...prev, search: value }))}
        />
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
        <Pagination
          className="pagination"
          current={pagination.current}
          pageSize={pagination.size}
          total={pagination.total}
          onChange={(current, size) => {
            setRequestParams(prev => ({
              ...prev,
              current,
              size
            }))
          }}
        />
      </div>
    </Container>
  </div>
}

export default List;
