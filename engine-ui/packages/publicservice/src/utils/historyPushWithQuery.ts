
const historyPsuhWithQuery = (router: { push: Function, location: any }, pathname: string) => {
  const query = router?.location?.query;
  router.push({
    pathname,
    query,
  })
}

export default historyPsuhWithQuery;
