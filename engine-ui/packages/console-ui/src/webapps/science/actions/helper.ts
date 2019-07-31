export function removeMetadata (data = {}) {
    const { scheduleConf } = data;
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
