import database from './database';
import datamap from './datamap';
import table from './table';
import user from './user';
import comm from './comm';

const Apis = Object.assign(
    comm,
    user,
    datamap,
    table,
    database,
)

export default Apis;