import * as comm from './comm';
import * as database from './database';
import * as table from './table';
import * as datamap from './datamap';

export default Object.assign(
    {},
    comm,
    table,
    datamap,
    database,
)