import database from './database';
import datamap from './datamap';
import table from './table';
import user from './user';

const Apis = Object.assign(
    user,
    datamap,
    table,
    database,
)

export default Apis;