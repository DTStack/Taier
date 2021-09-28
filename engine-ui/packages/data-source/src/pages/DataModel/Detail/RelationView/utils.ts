import { IRelationTree, LoopCallback } from './types';

export const loop = (tree: IRelationTree, cb?: LoopCallback) => {
  const stack = [];
  stack.push(tree);
  while (stack.length > 0) {
    const parent = stack.pop();
    if (typeof cb === 'function') {
      cb(parent);
    }
    if (parent.children && Array.isArray(parent.children)) {
      parent.children.forEach((item) => {
        stack.push(item);
      });
    }
  }
};

export const styleStringGenerator = (split: string) => {
  return (styleConfig: Object) => {
    return Object.keys(styleConfig).reduce(
      (temp, key) => `${temp}${key}${split}${styleConfig[key]};`,
      ''
    );
  };
};
