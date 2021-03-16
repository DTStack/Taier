/**
 * swagger文档中包含的接口,可以由yarn/npm swagger [-t]命令生成
 */
import bar from './bar';
import dataModel from './data-model';

export default {
  ...bar,
  ...dataModel,
};
