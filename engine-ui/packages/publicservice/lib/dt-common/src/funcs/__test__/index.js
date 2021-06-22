// function filterComments (sql) {
//     return sql.replace(/([^'])(--)+(.)+(\n|\s)+/g, '')
// }

const testStr = `
-- test;afafa;afadfa;
CREATE TABLE IF NOT EXISTS pw_competing_add_sku_1(
    id BIGINT,
    goods_is_bundle BIGINT COMMENT '--0:未捆绑销售;1:捆绑销售'
)
PARTITIONED BY( dt STRING)
lifecycle 365;
`;
function testSQLExtract() {
  // const sqls = testStr.split(/(;)/);
  const regx = /^(.)+(\n|\w|\s)+(;)+$/g;
  const sqls = testStr.match(regx);
}

testSQLExtract();
// testFilterComments();
