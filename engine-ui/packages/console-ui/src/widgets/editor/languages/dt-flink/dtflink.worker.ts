import * as dtsql from 'dt-sql-parser';

const ctx: Worker = self as any;

console.log('*****dtsql-worker初始化*****')

ctx.onmessage = (e: any) => {
    const message = e.data;
    const { eventId, data = [], type } = message;
    if (type == 'parseSyntax') {
        let result = dtsql.flinksqlParser(...data);
        ctx.postMessage({
            eventId: eventId,
            result: result && {
                ...result,
                token: {
                    start: result.token.start,
                    stop: result.token.stop
                }
            }
        }, null)
    }
}

console.log('*****dtsql-worker初始化完成*****')
// Trickery to fix TypeScript since this will be done by "worker-loader"
// eslint-disable-next-line @typescript-eslint/no-object-literal-type-assertion
export default {} as typeof Worker & (new () => Worker);
