/*
 * @Author: 云乐
 * @Date: 2021-03-12 11:48:32
 * @LastEditTime: 2021-03-16 17:45:42
 * @LastEditors: 云乐
 * @Description: 选择数据源
 */
import React, { useEffect, useState } from "react";
import SearchInput from "@/components/SearchInput";
import { Menu, List, notification } from "antd";
import { API } from "@/services";

export default function SelectSource(props) {
  const { nextType } = props;
  const [current, setCurrent] = useState(null);
  const [list, setList] = useState([]);
  const [iconList, setIconList] = useState([]);
  const [defaultMenu, setDefaultMenu] = useState(null);

  const getClassifyList = async () => {
    try {
      let { data, success } = await API.queryDsClassifyList();

      if (success) {
        let echoCurrent =
          sessionStorage.getItem("current") || data[0].classifyId;
        setList(data || []); //左侧菜单列表
        setCurrent(echoCurrent); //左侧菜单列表选择的id
        setDefaultMenu(data[0].classifyId); //左侧菜单全部选项id

        queryDsTypeByClassify(echoCurrent);
      }
    } catch (error) {
      notification["error"]({
        message: "错误！",
        description: "获取数据源分类类目列表失败",
      });
    }
  };

  const queryDsTypeByClassify = async (
    classifyId: number,
    search: string = ""
  ) => {
    let echoTypeId = JSON.parse(sessionStorage.getItem("sqlType"))?.typeId;
    try {
      let { data, success } = await API.queryDsTypeByClassify({
        classifyId,
        search: search,
      });

      if (success) {
        data.forEach((ele) => {
          if (ele.typeId === echoTypeId) {
            ele.selected = true;
          } else {
            ele.selected = false;
          }
        });
        setIconList(data || []);
      }
    } catch (error) {
      notification["error"]({
        message: "错误！",
        description: "根据分类获取数据源类型失败",
      });
    }
  };

  useEffect(() => {
    getClassifyList(); //获取数据源分类类目列表
  }, []);

  //搜索功能，左侧菜单默认为 全部
  const onSearch = (value) => {
    setCurrent(defaultMenu);
    queryDsTypeByClassify(defaultMenu, value);
  };

  //left menu click
  const handleMenuClick = (e) => {
    setCurrent(e.key);
    queryDsTypeByClassify(e.key);

    sessionStorage.setItem("current", e.key); //菜单回显
  };

  //选择对应类型
  const onSelectType = (item) => {
    let data = JSON.parse(JSON.stringify(iconList));
    data.forEach((ele) => {
      if (ele.typeId === item.typeId) {
        ele.selected = true;
      } else {
        ele.selected = false;
      }
    });

    setIconList(data);
    nextType(true); //显示下一步
    sessionStorage.setItem("sqlType", JSON.stringify(item)); //存储数据源
  };

  return (
    <div className="fill">
      <SearchInput
        placeholder="按数据源名称搜索"
        onSearch={(value) => onSearch(value)}
        width={220}
      ></SearchInput>

      <div className="show-type">
        <div className="left-menu">
          <Menu
            selectedKeys={[String(current)]}
            mode="inline"
            onClick={handleMenuClick}
          >
            {list.length > 0 &&
              list.map((item) => {
                return (
                  <Menu.Item key={item.classifyId}>
                    <span>{item.classifyName}</span>
                  </Menu.Item>
                );
              })}
          </Menu>
        </div>
        <div className="right-menu">
          {iconList.length > 0 && (
            <List
              grid={{ gutter: 16, column: 4 }}
              dataSource={iconList}
              renderItem={(item) => (
                <List.Item onClick={() => onSelectType(item)}>
                  <div>
                    <img
                      // src={item.imgUrl}
                      alt="图片显示失败"
                      className={item.selected ? "selected" : ""}
                    />
                    <p style={{ textAlign: "center", width: 216 }}>
                      {item.dataType}
                    </p>
                  </div>
                </List.Item>
              )}
            />
          )}
        </div>
      </div>
    </div>
  );
}
