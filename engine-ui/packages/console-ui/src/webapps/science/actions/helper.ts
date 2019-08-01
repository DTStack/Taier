export function removeMetadata (data = {}) {
    const { scheduleConf } = data as {
        scheduleConf: any
    };
    if (!scheduleConf) {
        return data;
    }
    try {
        const conf = JSON.parse(scheduleConf);
        if (conf) {
            delete conf._metaData;
            return {
                ...data,
                scheduleConf: JSON.stringify(conf)
            }
        }
    } catch (e) {
        return data;
    }
}
