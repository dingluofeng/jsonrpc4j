package com.googlecode.jsonrpc4j.spring;

import com.googlecode.jsonrpc4j.RequestInterceptor;

public class HttpsServerExorter extends JsonServiceExporter {

    private RequestInterceptor requestInterceptor;

    @Override
    protected void exportService() {
        super.exportService();
        getJsonRpcServer().setRequestInterceptor(requestInterceptor);
    }

    public void setRequestInterceptor(RequestInterceptor requestInterceptor) {
        this.requestInterceptor = requestInterceptor;
    }

}
