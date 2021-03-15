/*
 * @Author: 云乐
 * @Date: 2021-03-12 11:48:32
 * @LastEditTime: 2021-03-14 21:07:08
 * @LastEditors: 云乐
 * @Description: 选择数据源
 */
import React, { useEffect, useState } from "react";
import SearchInput from "@/components/SearchInput/SearchInput";
import { Menu, message, List } from "antd";
import { API } from "@/services";

export default function SelectSource() {
  const [current, setCurrent] = useState("1");
  const [list, setList] = useState([]);
  const [iconList, setIconList] = useState([]);

  const getClassifyList = async () => {
    try {
      let { data, success } = await API.queryDsClassifyList();
      data = [
        {
          classifyCode: "code1",
          classifyId: 1,
          classifyName: "全部",
          sorted: 1,
        },
        {
          classifyCode: "code2",
          classifyId: 2,
          classifyName: "常用",
          sorted: 2,
        },
        {
          classifyCode: "code3",
          classifyId: 3,
          classifyName: "关系型",
          sorted: 3,
        },
      ];

      if (success) {
        setList(data || []);
        queryDsTypeByClassify(data[0].classifyId);
      }
    } catch (error) {
      message.error("获取数据源分类类目列表失败");
    }
  };
  
  const queryDsTypeByClassify = async (classifyId: number,search?:string="") => {
    try {
      let { data, success } = await API.queryDsTypeByClassify({
        classifyId,
        search
      });
      data = [
        {
          dataType: "type1",
          haveVersion: true,
          imgUrl: "url1",
          typeId: 1,
          selected: false,
        },
        {
          dataType: "type2",
          haveVersion: true,
          imgUrl: "url2",
          typeId: 2,
          selected: false,
        },
        {
          dataType: "type3",
          haveVersion: true,
          imgUrl: "url3",
          typeId: 3,
          selected: false,
        },
        {
          dataType: "type4",
          haveVersion: true,
          imgUrl: "url4",
          typeId: 4,
          selected: false,
        },
        {
          dataType: "type5",
          haveVersion: true,
          imgUrl: "url5",
          typeId: 5,
          selected: false,
        },
        {
          dataType: "type6",
          haveVersion: true,
          imgUrl: "url6",
          typeId: 6,
          selected: true,
        },
      ];
      if (success) {
        setIconList(data || []);
      }
    } catch (error) {
      message.error("根据分类获取数据源类型失败");
    }
  };

  useEffect(() => {
    getClassifyList(); //获取数据源分类类目列表
  }, []);

  const onSearch = (value) => {
    console.log("value: ", value);
  };

  const handleClick = (e) => {
    setCurrent(e.key);
    queryDsTypeByClassify(e.key);
  };

  //选择对应类型
  const onSelectType = (item) => {
    iconList.forEach((ele) => {
      if (ele.typeId === item.typeId) {
        ele.selected = true;
      }
    });
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
          <Menu selectedKeys={[current]} mode="inline" onClick={handleClick}>
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
                <List.Item>
                  <div
                    className="show-detail"
                    onClick={() => onSelectType(item)}
                  >
                    <img
                      src={item.imgUrl}
                      alt="图片显示失败"
                      className={
                        item.selected ? "selected show-detail" : "show-detail"
                      }
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
