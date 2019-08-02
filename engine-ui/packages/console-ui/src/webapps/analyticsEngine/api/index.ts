import database from './database';
import datamap from './datamap';
import table from './table';
import comm from './comm';
import user from './user';

const Apis = Object.assign(
    comm,
    user,
    datamap,
    table,
    database,
    table
)

export default Apis;
