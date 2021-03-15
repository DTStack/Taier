import React from 'react';
import createLayout from '@/layouts';
import Exception from '@/components/Exception';
import Entry from 'pages/entry/index';

import dataSource from "pages/DataSource/List"
import addSource from "pages/DataSource/AddSource"


const Layout = createLayout(true, true);

const PageNotFound = () => (
  <Exception status={404} subTitle="对不起，你访问的页面不存在" />
);

const routerConf = [
  {
    path: '/',
    layout: Layout,
    component: Entry,
  },
  //数据源项目路径
  {
    path: '/data-source',
    // layout: Layout,
    component: dataSource,
  },
  {
    path: '/add-source',
    // layout: Layout,
    component: addSource,
  },
  {
    path: '*',
    layout: Layout,
    component: PageNotFound,
  },
];

export default routerConf;
