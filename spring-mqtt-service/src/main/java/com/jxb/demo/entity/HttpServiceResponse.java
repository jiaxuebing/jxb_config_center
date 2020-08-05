package com.jxb.demo.entity;

/**
 * @desc http服务响应体
 * @param <T>
 */
public class HttpServiceResponse<T> {

    private int respCode;//响应码

    private String errorMsg;

    private T data;

    public HttpServiceResponse(int respCode,String errorMsg,T data){
        this.respCode = respCode;
        this.errorMsg = errorMsg;
        this.data = data;
    }

    /**
     * @desc 返回值工厂
     * @param respCode
     * @param errorMsg
     * @param data
     * @param <T>
     * @return
     */
    public static <T> HttpServiceResponse<T> httpServiceResponseFactory(int respCode,String errorMsg,T data){
      return new HttpServiceResponse<>(respCode,errorMsg,data);
    }

    public int getRespCode() {
        return respCode;
    }

    public void setRespCode(int respCode) {
        this.respCode = respCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
