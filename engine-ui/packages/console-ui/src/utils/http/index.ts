import 'whatwg-fetch';

import HttpInterface from './interface';

class Http implements HttpInterface {
    public get<R, P = {}> (url: string, params?: P): Promise<R> {
        const newUrl: string = params ? this.build(url, params) : url;
        return this.request<R>(newUrl, {
            method: 'GET'
        });
    }
    public post<R, P = {}> (url: string, body?: P): Promise<R> {
        const options: RequestInit = { method: 'POST' };
        if (body) {
            options.body = JSON.stringify(body);
        }
        return this.request<R>(url, options);
    }
    public postAsFormData<R, P = {}> (url: string, params?: P): Promise<R> {
        const options: RequestInit = { method: 'POST' };
        if (params) {
            options.body = this.buildFormData(params);
        }
        return this.request<R>(url, options);
    }
    public postForm<R> (url: string, form: HTMLFormElement): Promise<R> {
        const options: RequestInit = { method: 'POST' };
        if (form) {
            options.body = new FormData(form);
        }
        return this.request<R>(url, options);
    }
    public request<R> (url: string, options?: RequestInit): Promise<R> {
        options.credentials = 'same-origin';
        return fetch(url, options).then<R>((response) => {
            return response.json();
        }).catch((err) => {
            return err; // 错误信息返回
        });
    }

    public build (url: string, params: any) { // URL构建方法
        const ps = [];
        if (params) {
            for (const p in params) {
                if (p) {
                    ps.push(p + '=' + encodeURIComponent(params[p]));
                }
            }
        }
        return url + '?' + ps.join('&');
    }

    public buildFormData (params: any) {
        if (params) {
            const data = new FormData();
            for (const p in params) {
                if (p) {
                    data.append(p, params[p]);
                }
            }
            return data;
        }
    }
}

export default new Http();
