import React, { useEffect, useState } from 'react';
import SearchInput from '@/components/SearchInput';
import { Menu, notification } from 'antd';
import { API } from '@/services';
import { getSaveStatus } from '../utils/handelSession';
import { useRef } from 'react';
const IMG_URL = '';
import _ from 'lodash';

export default function SelectSource(props) {
  const { nextType } = props;
  const [current, setCurrent] = useState(null);
  const [list, setList] = useState([]);
  const [iconList, setIconList] = useState([]);
  const [defaultMenu, setDefaultMenu] = useState(null);
  const refContainer = useRef(null);
  const [ratio, setRatio] = useState(1.0);

  const getClassifyList = async () => {
    let saveStatus = getSaveStatus();
    try {
      let { data, success } = await API.queryDsClassifyList();

      if (success) {
        let echoCurrent = saveStatus.menuSelected || data[0].classifyId;

        setList(data || []); //左侧菜单列表
        setCurrent(echoCurrent); //左侧菜单列表选择的id
        setDefaultMenu(data[0].classifyId); //左侧菜单全部选项id

        queryDsTypeByClassify(echoCurrent, '', saveStatus.sqlType?.typeId);
      }
    } catch (error) {
      notification.error({
        message: '错误！',
        description: '获取数据源分类类目列表失败！',
      });
    }
  };

  const queryDsTypeByClassify = async (
    classifyId: number,
    search: string = '',
    echoTypeId?: string
  ) => {
    try {
      let { data, success } = await API.queryDsTypeByClassify({
        classifyId,
        search: search,
      });

      if (success) {
        data.forEach((ele) => {
          ele.selected = ele.typeId === echoTypeId ? true : false;
          ele.imgUrl = IMG_URL + '/assets/imgs/' + ele.imgUrl;
        });

        setIconList(data || []);
      }
    } catch (error) {
      notification.error({
        message: '错误！',
        description: '根据分类获取数据源类型失败！',
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

    sessionStorage.setItem('current', e.key); //菜单回显
  };

  //选择对应类型
  const onSelectType = (item) => {
    let data: any = [];
    try {
      data = JSON.parse(JSON.stringify(iconList));
      data.forEach((ele) => {
        if (ele.typeId === item.typeId) {
          ele.selected = true;
        } else {
          ele.selected = false;
        }
      });
    } catch (error) {}

    setIconList(data);
    nextType(true); //显示下一步
    sessionStorage.setItem('sqlType', JSON.stringify(item)); //存储数据源
    sessionStorage.removeItem('version');
  };

  // 计算图片缩放倍率
  const computeScaleRatio = _.debounce(() => {
    if (!refContainer.current) return;
    const width = parseFloat(
      window.getComputedStyle(refContainer.current)['width']
    );
    const len = Math.floor(width / (216 + 16));
    const ratio = (width - 16 * len) / len / 216;
    setRatio(ratio);
  }, 500);

  useEffect(() => {
    computeScaleRatio();
    window.addEventListener('resize', () => {
      computeScaleRatio();
    });
  }, [refContainer.current]);

  return (
    <div className="fill">
      <SearchInput
        placeholder="按数据源名称搜索"
        onSearch={(value) => onSearch(value)}
        width={200}></SearchInput>

      <div className="show-type">
        <div className="left-menu">
          <Menu
            selectedKeys={[String(current)]}
            mode="inline"
            onClick={handleMenuClick}>
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
          <div className="right-menu-main">
            <div className="right-menu-main-content" ref={refContainer}>
              {iconList.length > 0 &&
                iconList.map((item, index) => {
                  let col = (
                    <div
                      className="right-menu-item"
                      key={index}
                      style={{ width: 'auto', marginBottom: 20 }}
                      onClick={() => onSelectType(item)}>
                      <img
                        src={item.imgUrl}
                        alt="图片显示失败"
                        className={item.selected ? 'selected' : ''}
                        style={{ width: 216 * ratio, height: 108 * ratio }}
                      />
                      <p style={{ textAlign: 'center', marginTop: 12 }}>
                        {item.dataType}
                      </p>
                    </div>
                  );
                  return col;
                })}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
