//每个模块定义的相关接口
export default {
  post1: {
    method: "post",
    url: "/foo/bar",
  },
  get2: {
    method: "get",
    url: "/foo/bas",
  },
  //数据源列表分页信息
  dataSourcepage: {
    method: "post",
    url: "/publicService/v1/dataSource/page",
  },
  //数据源类型下拉列表
  typeList: {
    method: "get",
    url: "/publicService/v1/dataSource/type/list",
  },
  // 授权产品下拉列表
  productList: {
    method: "get",
    url: "/publicService/v1/dataSource/product/list",
  },
  //删除一条数据源实例
  dataSourceDelete: {
    method: "get",
    url: "/publicService/v1/dataSource/delete",
  },
  //产品授权界面
  authProductList: {
    method: "get",
    url: "/publicService/v1/dataSource/auth/product/list",
  },
  // 产品授权
  dataSoProAuth: {
    method: "post",
    url: "/publicService/v1/dataSource/product/auth",
  },
  //获取数据源分类类目列表
  queryDsClassifyList: {
    method: "get",
    url: "/publicService/v1/addDs/queryDsClassifyList",
  },
  //根据分类获取数据源类型
  queryDsTypeByClassify: {
    method: "get",
    url: "/publicService/v1/addDs/queryDsTypeByClassify",
  },
  //根据数据源类型获取版本列表
  queryDsVersionByType: {
    method: "get",
    url: "/publicService/v1/addDs/queryDsVersionByType",
  },
  //根据数据库类型和版本查找表单模版
  findTemplateByTypeVersion: {
    method: "post",
    url: "/publicService/v1/dsForm/findFormByTypeVersion",
  },
  // 获取数据源与租户交集的产品列表
  queryProductList: {
    method: "post",
    url: "/publicService/v1/addDs/queryProductList",
  },
  //模板下载
  downloadtemplate: {
    method: "get",
    url: "/publicService/v1/download",
  },
};
