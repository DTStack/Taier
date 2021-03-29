export function getRules(item) {
  return {
    initialValue: item.initialValue,
    rules: [
      {
        required: item.required === 1 ? true : false,
        message: `${item.label}不能为空`,
      },
    ],
  };
}

export function getRulesJdbc(item) {
  let test = {
    length: {
      max: 128,
      message: '不得超过128个字符',
    },
    regex: {
      message: 'JDBC URL格式不符合规则!',
    },
  };

  let ruleArr: any = [
    {
      required: item.required === 1 ? true : false,
      message: `${item.label}不能为空`,
    },
    {
      pattern: item.regex
        ? RegExp(item.regex.substring(1, item.regex.length - 1))
        : null,
      message: test?.regex?.message,
    },
  ];

  ruleArr.push(test?.length);

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
