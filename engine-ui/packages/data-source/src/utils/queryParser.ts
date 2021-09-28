const queryParse = (url: string) => {
  const search = url.split('?')[1];
  if (!search) return {};
  return search.split('&').reduce((temp, current) => {
    const [key, value] = current.split('=');
    temp[key] = value;
    return temp;
  }, {});
};

export default queryParse;
