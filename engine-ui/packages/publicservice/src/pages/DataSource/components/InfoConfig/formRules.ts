export function getRules(item) {
  let ruleArr: any = [
    {
      required: item.required === 1 ? true : false,
      message: `${item.label}不能为空`,
    },
  ];
  if (item.validInfo !== '') {
    let validInfo = JSON.parse(item.validInfo);
    if (Object.keys(validInfo).includes('length')) {
      ruleArr.push(validInfo?.length);
    }
    if (Object.keys(validInfo).includes('regex')) {
      ruleArr.push({
        pattern: item.regex
          ? RegExp(item.regex.substring(1, item.regex.length - 1))
          : null,
        message: validInfo?.regex?.message,
      });
    }
  }

  if (item.label === '数据源名称') {
    ruleArr.push({
      pattern: /^[\u4e00-\u9fa50-9A-Za-z_]+$/,
      message: '仅支持中文、数字、英文大小写、下划线',
    });
  }

  return {
    initialValue: item.initialValue,
    rules: ruleArr,
  };
}

export interface IParams {
  dataType: string;
  dataVersion: string;
  appTypeList: string[];
}

export const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 8 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 10 },
  },
};

export const formNewLayout = {
  labelCol: {
    span: 10,
  },
  wrapperCol: {
    span: 10,
    offset: 8,
  },
};
