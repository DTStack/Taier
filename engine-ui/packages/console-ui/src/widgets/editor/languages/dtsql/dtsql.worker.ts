import * as dtsql from 'dt-sql-parser';
declare var self: any;

console.log('*****dtsql-worker初始化*****')

self.onmessage = (e: any) => {
    const message = e.data;
    const { eventId, data = [], type } = message;
    if (type == 'parserSql') {
        self.postMessage({
            eventId: eventId,
            result: dtsql.parser.parserSql(...data)
        }, null)
    } else if (type == 'parseSyntax') {
        self.postMessage({
            eventId: eventId,
            result: dtsql.parser.parseSyntax(...data)
        }, null)
    }
}
export default self;
console.log('*****dtsql-worker初始化完成*****')
