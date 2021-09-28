import React from 'react';
import classnames from 'classnames';

// 自定义step icon render
/**
 * @param step 渲染下表index
 * @param current 当前active的步骤
 * @returns
 */
const stepIconRender = (step: number, current: number) => {
  const wrapper = (child: React.ReactChild) => {
    return (
      <div
        className={classnames({
          'step-icon-wrapper': true,
          filled: step === current,
          'border-gray': step > current,
        })}>
        {child}
      </div>
    );
  };

  if (step < current) {
    return wrapper(
      <i className="step-icon iconfont2 iconOutlinedxianxing_buzhou_wancheng" />
    );
  } else if (step === current) {
    return wrapper(<span className="font">{step + 1}</span>);
  } else {
    return wrapper(<span className="font">{step + 1}</span>);
  }
};

export default stepIconRender;
