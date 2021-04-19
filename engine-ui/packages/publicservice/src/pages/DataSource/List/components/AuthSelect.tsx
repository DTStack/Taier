import React, { useEffect, useState } from 'react';
import { Checkbox, Row, Col, message } from 'antd';
import { API } from '@/services';
interface IProps {
  record: {
    dataInfoId: number;
  };
  oncheck(checkedValues: string): void;
}

export default function AuthSelect(props: IProps) {
  let { record, oncheck } = props;
  const [authList, setAuthList] = useState([]);
  const [checkedList, setCheckedList] = useState([]);

  //获取产品授权列表
  const getauthProductList = async () => {
    let { data, success } = await API.authProductList({
      dataInfoId: record.dataInfoId,
    });
    if (success) {
      if (data.length > 0) {
        data.forEach((item) => {
          if (item.isAuth === 1 || item.isImport === 1) {
            let newList = checkedList;
            newList.push(item.appType);
            setCheckedList(newList);
          }
        });

        setAuthList(data);
      }
    } else {
      message.error('获取产品授权列表失败！');
    }
  };

  useEffect(() => {
    getauthProductList();
  }, []);

  const onChange = (checkedValues) => {
    oncheck(checkedValues);
    setCheckedList(checkedValues);
  };

  return (
    <div>
      <p style={{color: '#666'}}>点击进行授权/取消，若已在产品中应用，不能取消授权</p>
      <br />
      <Checkbox.Group
        style={{ width: '100%' }}
        onChange={onChange}
        value={checkedList}>
        <Row>
          {authList.length > 0 &&
            authList.map((item, index) => (
              <Col
                key={index}
                span={8}
                onClick={() => {
                  if (item.isImport === 1) {
                    message.error('已在产品中应用，不能取消授权。');
                  }
                }}>
                <Checkbox value={item.appType} disabled={item.isImport === 1}>
                  {item.appName}
                </Checkbox>
              </Col>
            ))}
        </Row>
      </Checkbox.Group>
    </div>
  );
}
