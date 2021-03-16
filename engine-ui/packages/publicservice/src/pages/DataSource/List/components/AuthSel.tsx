/*
 * @Author: 云乐
 * @Date: 2021-03-11 17:43:45
 * @LastEditTime: 2021-03-16 15:20:01
 * @LastEditors: 云乐
 * @Description: 选择授权页面
 */
import React, { useEffect, useState } from "react";
import { API } from "@/services";
import { Checkbox, Row, Col, notification, message } from "antd";
interface IProps {
  record: {
    dataInfoId: number;
  };
  oncheck(checkedValues: string): void;
}
export default function AuthSel(props: IProps) {
  let { record, oncheck } = props;
  const [authList, setauthList] = useState([]);
  const [checkedlist, setcheckedlist] = useState([]);

  //获取产品授权列表
  const getauthProductList = async () => {
    try {
      let { data } = await API.authProductList({
        dataInfoId: record.dataInfoId,
      });
      if (data.length > 0) {
        data.forEach((item) => {
          if (item.isAuth === 1) {
            checkedlist.push(item.appType);
          }
        });

        setauthList(data);
      }
    } catch (error) {
      notification["error"]({
        message: "错误！",
        description: "获取产品授权列表失败",
      });
    }
  };

  useEffect(() => {
    getauthProductList();
  }, []);

  const onChange = (checkedValues) => {
    oncheck(checkedValues);
    setcheckedlist(checkedValues);
  };

  return (
    <div>
      <p>点击进行授权/取消，若已在产品中应用，不能取消授权</p>
      <br />
      <Checkbox.Group
        style={{ width: "100%" }}
        onChange={onChange}
        value={checkedlist}
      >
        <Row>
          {authList.length > 0 &&
            authList.map((item) => (
              <Col
                span={8}
                onClick={() => {
                  if (item.isAuth === 1) {
                    notification["error"]({
                      message: "错误！",
                      description: "已在产品中应用，不能取消授权。",
                    });
                  }
                }}
              >
                <Checkbox value={item.appType} disabled={item.isAuth === 1}>
                  {item.appName}
                </Checkbox>
              </Col>
            ))}
        </Row>
      </Checkbox.Group>
    </div>
  );
}
