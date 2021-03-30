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
  return {
    initialValue: item.initialValue,
    rules: [
      {
        required: item.required === 1 ? true : false,
        message: `${item.label}不能为空`,
      },
      {
        pattern: item.regex
          ? RegExp(item.regex.substring(1, item.regex.length - 1))
          : null,
        message: item.validInfo,
      },
    ],
  };
}
