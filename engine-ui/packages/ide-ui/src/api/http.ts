import 'whatwg-fetch';

// import { authAfterFormated, authBeforeFormate } from "../interceptor";
import { ProgressBar } from 'dt-react-component';
import { PROJECT_KEY } from '../comm/const';
import { message } from 'antd';

class Http {
    get(url: any, params: any) {
        // GET请求
        const newUrl = params ? this.build(url, params) : url;
        return this.request(newUrl, {
            method: 'GET',
        });
    }

    post(url: any, body?: any) {
        // POST请求
        const options: any = { method: 'POST' };
        if (body) options.body = JSON.stringify(body);
        return this.request(url, options);
    }

    postAsFormData(url: any, params: any) {
        const options: any = { method: 'POST' };
        if (params) options.body = this.buildFormData(params);
        return this.request(url, options);
    }

    postForm(url: any, form: any) {
        const options: any = { method: 'POST' };
        if (form) options.body = new FormData(form);
        return this.request(url, options);
    }

    // eslint-disable-next-line no-undef
    request(url: any, options: RequestInit) {
        ProgressBar.show();
        options.credentials = 'same-origin';
        options.headers = {
            ...options.headers,
        };
        const projectId = sessionStorage.getItem(PROJECT_KEY);
        if (projectId) {
            options.headers = {
                'X-Project-ID': projectId,
            };
        }
        return (
            fetch(url, options)
                //   .then(authBeforeFormate)
                .then((response: any) => {
                    setTimeout(() => {
                        ProgressBar.hide();
                    }, 300);
                    return response.json();
                })
                .then((res) => {
                    if (res.code !== 1) {
                        message.error(res.message);
                    }
                    return res;
                })
                //   .then(authAfterFormated)
                .catch((err: any) => {
                    ProgressBar.hide();
                    console.log(err);
                    return err;
                })
        );
    }

    defaultHeader() {
        // 默认头
        const header: any = {
            Accept: '*/*',
            'Content-Type': 'application/json',
        };
        return header;
    }

    build(url: any, params: any) {
        // URL构建方法
        const ps: any = [];
        if (params) {
            for (const p in params) {
                if (p) {
                    ps.push(p + '=' + encodeURIComponent(params[p]));
                }
            }
        }
        return url + '?' + ps.join('&');
    }

    buildFormData(params: any) {
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
