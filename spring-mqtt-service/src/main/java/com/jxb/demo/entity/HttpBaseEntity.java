package com.jxb.demo.entity;

import java.util.Map;

public class HttpBaseEntity {

    private String url;

    private String method;//get or post

    private Map<String,String> headerMap;

    private Map<String,String> contentMap;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public Map<String, String> getContentMap() {
        return contentMap;
    }

    public void setContentMap(Map<String, String> contentMap) {
        this.contentMap = contentMap;
    }
}
