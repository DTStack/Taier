/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { useEffect, useState, useMemo, useRef } from 'react';
import { withRouter } from 'react-router';
import Base64 from 'base-64';
import Search from './components/Search';
import { Table, message, Modal, Pagination, notification } from 'antd';
import { columns } from './constants';
import { API } from '@/services';
import AuthSelect from './components/AuthSelect';
import { remove } from '../utils/handelSession';
import { IPagination, IOther, IRecord } from './type';
import './style.scss';
import { DATA_SOURCE_TEXT } from '../constants/index';

function index(props) {
  const [dataSources, setDataSources] = useState([]);
  const [params, setParams] = useState<IPagination>({
    currentPage: 1, //当前页码
    pageSize: 20, //分页个数
  });
  const [other, setOther] = useState<IOther>({
    search: '',
    dataTypeList: [],
    appTypeList: [],
    isMeta: 0,
    status: [],
  });
  const container = useRef(null);

  const [total, setTotal] = useState<number>(null);

  const [visible, setVisible] = useState<boolean>(false);
  const [record, setRecord] = useState<IRecord>({
    dataInfoId: null,
    isAuth: null,
  });
  const [checkedValues, setcheckedValues] = useState<number[]>([]);

  //获取表格数据
  const requestTableData = async (query?: any) => {
    let { data, success, message } = await API.dataSourcepage({
      ...params,
      ...other,
      ...query,
    });
    if (success) {
      let { currentPage, pageSize, totalCount } = data;
      setParams({
        currentPage, //当前页码
        pageSize, //分页个数
      });
      if (data.data) {
        data.data.forEach((element) => {
          Object.keys(DATA_SOURCE_TEXT).forEach((item) => {
            if (element.dataType === DATA_SOURCE_TEXT[item]) {
              element.type = Number(item);
            }
          });

          if (
            element.linkJson &&
            element.linkJson.indexOf('{') === -1 &&
            element.linkJson.indexOf('}') === -1
          ) {
            element.linkJson = Base64.decode(element.linkJson);
          }
        });
      }

      setTotal(totalCount); //总页数
      setDataSources(data.data || []);
    } else {
      notification.error({
        message: '错误！',
        description: message,
      });
    }
  };

  useEffect(() => {
    requestTableData(); //获取数据源列表
    //清除存储数据
    remove();
  }, []);

  //编辑
  const toEdit = (record) => {
    if (record.isMeta === 1) {
      message.error('带meta标识的数据源不能编辑、删除');
    } else {
      props.router.push({
        pathname: '/data-source/edit',
        state: {
          record: record,
        },
      });
    }
  };

  //删除
  const toDelete = async (record) => {
    let { success, message: msg } = await API.dataSourceDelete({
      dataInfoId: record.dataInfoId,
    });

    if (success) {
      message.success('删除成功');
      requestTableData(); //更新表格
    } else {
      message.error(`${msg}`);
    }
  };

  //分页事件
  const onChangePage = (page, pageSize) => {
    let data = { ...params, ...{ currentPage: page }, pageSize: pageSize };
    setParams(data);
    requestTableData(data);
  };

  //搜索事件
  const onSearch = (value) => {
    let data = { ...other, ...value, currentPage: 1 };
    setOther(data);
    requestTableData(data);
  };

  //连接状态筛选
  const handleTableChange = (pagination, filters, sorter) => {
    let data = { ...other, status: filters.status };
    setOther(data);
    requestTableData(data);
  };

  //点击授权按钮
  const toAuth = (record) => {
    setRecord(record);
    setVisible(true);
  };

  //获取产品授权的列表
  const oncheck = (prolist: any) => {
    setcheckedValues(prolist);
  };

  //产品授权隐藏
  const handleAutoProduc = async () => {
    let { success } = await API.dataSoProAuth({
      dataInfoId: record.dataInfoId,
      isAuth: 1, //是否授权，0为取消授权，1为授权
      appTypes: checkedValues,
    });
    if (success) {
      message.success('操作成功');
      requestTableData(); //更新表格
    } else {
      message.error('操作失败');
    }

    setVisible(false);
  };

  const showTotal = () => {
    return (
      <span>
        共 <i style={{ color: '#3F87FF' }}>{total}</i> 条数据，每页显示
        {params.pageSize}条
      </span>
    );
  };
  const y = useMemo(() => {
    if (container.current === null) return 0;
    return parseInt(getComputedStyle(container.current)['height']);
  }, [container.current]);

  return (
    <div className="source">
      <Search onSearch={onSearch}></Search>

      <div className="bottom">
        <div className="conent-table" ref={container}>
          <Table
            size="middle"
            rowKey={(record) => record.dataInfoId}
            columns={columns({
              toEdit: toEdit,
              toAuth: toAuth,
              toDelete: toDelete,
              left: 'left',
              right: 'right',
              filters: [
                { text: '正常', value: 1 },
                { text: '连接失败', value: 0 },
              ],
            })}
            dataSource={dataSources}
            pagination={false}
            scroll={{ x: '100%', y }}
            onChange={handleTableChange}
          />
        </div>
        <div className="page-com">
          <Pagination
            size="small"
            total={total}
            showTotal={showTotal}
            onChange={onChangePage}
            defaultPageSize={params.pageSize}
            current={params.currentPage}
            showSizeChanger
            pageSizeOptions={['20', '50', '100']}
            onShowSizeChange={onChangePage}
          />
        </div>
      </div>

      {visible && (
        <Modal
          className="datasource-modal"
          closeIcon={
            <span
              style={{ fontSize: 20 }}
              className="iconfont2 iconOutlinedxianxing_Close-1"></span>
          }
          title="授权"
          visible={visible}
          onOk={handleAutoProduc}
          onCancel={() => {
            setVisible(false);
          }}>
          <AuthSelect record={record} oncheck={oncheck}></AuthSelect>
        </Modal>
      )}
    </div>
  );
}

export default withRouter(index);
