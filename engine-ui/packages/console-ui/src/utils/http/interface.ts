
export default interface HttpInterface {
    /**
     * HTTP Get method
     * @param url request URL
     * @param params  request Parameter
     */
    get<R, P = {}>(url: string, params?: P): Promise<R>;
    /**
     * HTTP Post method
     * @param url request URL
     * @param body request body object
     */
    post<R, P = {}>(url: string, body?: P): Promise<R>;
    /**
     * Post an object as a formData object
     * @param url request URL
     * @param params the params object that wait to convert to formData
     */
    postAsFormData<R, P = {}>(url: string, params?: P): Promise<R>;
    /**
     * Post a form element
     * @param url request URL
     * @param form HTML Form element
     */
    postForm<R>(url: string, form: HTMLElement): Promise<R>;
    /**
     * Http request
     * @param url request URL
     * @param options request options
     */
    request<R>(url: string, options?: RequestInit): Promise<R>;
}
