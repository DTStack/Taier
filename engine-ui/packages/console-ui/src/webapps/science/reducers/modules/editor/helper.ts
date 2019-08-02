export function safeGetConsoleTab (origin: any, tabId: any) {
    origin[tabId] = origin[tabId] || { data: [], activeKey: null };
    return origin[tabId];
}
