import React, { useEffect, useState } from 'react';
import { Select, Checkbox, Row, Col, notification } from 'antd';
import { API } from '@/services';
import { getSaveStatus } from '../utils/handelSession';

const { Option } = Select;

export default function ProduceAuth() {
  const [sqlType, setSqlType] = useState({
    dataType: '',
    haveVersion: true,
    imgUrl: '',
    typeId: null,
  });
  const [produceList, setProduceList] = useState([]);

  const [version, setVersion] = useState([]); //版本选择
  const [checkdList, setCheckdList] = useState<string[]>([]); //产品选择
  const [defaultSelect, setDefaultSelect] = useState(''); //添加默认选择版本号

  //根据数据源类型获取版本列表
  const queryDsVersionByType = async () => {
    let saveStatus = getSaveStatus();
    let dataType = saveStatus.sqlType?.dataType || '';

    let { data, success } = await API.queryDsVersionByType({
      dataType,
    });

    if (success) {
      setVersion(data || []);

      if (data.length > 0) {
        let echoVersion = saveStatus.version || data[0].dataVersion;
        sessionStorage.setItem('version', echoVersion);

        setDefaultSelect(echoVersion);
        getAuthProductList(dataType, echoVersion);

        setSqlType(saveStatus.sqlType);

        setCheckdList(
          saveStatus.checkdList ? saveStatus.checkdList.split(',') : []
        );
      } else {
        getAuthProductList(dataType, '');
      }
    } else {
      notification.error({
        message: '错误！',
        description: '根据数据源类型获取版本列表失败！',
      });
    }
  };

  //获取产品授权列表
  const getAuthProductList = async (type: string, version: string) => {
    try {
      let { data, success } = await API.queryProductList({
        dataType: type,
        dataVersion: version,
      });

      data = [
        {
          productCode: 'time',
          productName: '实时开发',
        },
        {
          productCode: 'console',
          productName: '控制台开发',
        },
        {
          productCode: 'dataqua',
          productName: '数据质量',
        },
      ];

      if (success) {
        setProduceList(data);
      }
    } catch (error) {
      notification.error({
        message: '错误！',
        description: '获取产品授权列表失败',
      });
    }
  };

  useEffect(() => {
    queryDsVersionByType();
  }, []);

  //存储数据库版本
  const onSelected = (value) => {
    setDefaultSelect(value);
    getAuthProductList(sqlType.dataType, value); //更新产品列表

    sessionStorage.setItem('version', value);
  };

  //获取产品授权的列表
  const oncheck = (prolist) => {
    setCheckdList(prolist);

    sessionStorage.setItem('checkdList', prolist);
  };

  return (
    <div className="produce-auth">
      <div className="text-show">
        <p>
          将 <span style={{ color: '#3F87FF' }}>{sqlType.dataType}</span>
          授权哪些产品使用：
        </p>
        {version.length > 0 && (
          <div className="version-sel">
            <span className="version">
              <b style={{ color: 'red', verticalAlign: 'center' }}>*</b> 版本：
            </span>
            <Select
              showSearch
              style={{ width: '80%' }}
              placeholder="请选择对应版本"
              optionFilterProp="children"
              onChange={onSelected}
              value={defaultSelect}>
              {version.map((item) => {
                return (
                  <Option value={item.dataVersion}>{item.dataVersion}</Option>
                );
              })}
            </Select>
          </div>
        )}

        <div style={{ display: 'flex' }}>
          <span className="version">授权产品：</span>
          <Checkbox.Group
            onChange={oncheck}
            value={checkdList}
            style={{ flex: 1 }}>
            <Row>
              {produceList.length > 0 &&
                produceList.map((item) => (
                  <Col span={8}>
                    <Checkbox value={item.productCode}>
                      {item.productName}
                    </Checkbox>
                  </Col>
                ))}
            </Row>
          </Checkbox.Group>
        </div>
      </div>
    </div>
  );
}
