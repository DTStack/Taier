import database from '../api/database';
import datamap from '../api/datamap';
import user from '../api/database';
import table from '../api/table';
import comm from './comm';

const Apis = Object.assign(
    comm,
    user,
    datamap,
    table,
    database,
    table,
)

export default Apis;
