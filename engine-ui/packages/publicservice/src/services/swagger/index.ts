/**
 * swagger文档中包含的接口,可以由yarn/npm swagger [-t]命令生成
 */
import dataModel from './data-model';
import dataSources from './dataSources';

export default {
  ...dataModel,
  ...dataSources
};
