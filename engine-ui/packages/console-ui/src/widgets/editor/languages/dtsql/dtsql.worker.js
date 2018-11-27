import * as dtsql from 'dt-sql-parser';
console.log('*****dtsql-worker初始化*****')
self.onmessage = (e) => {
    const message = e.data;
    const { eventId, data = [], type } = message;
    if (type == 'parserSql') {
        self.postMessage({
            eventId: eventId,
            result: dtsql.parser.parserSql(...data)
        })
    } else if (type == 'parseSyntax') {
        self.postMessage({
            eventId: eventId,
            result: dtsql.parser.parseSyntax(...data)
        })
    }
}
console.log('*****dtsql-worker初始化完成*****')
