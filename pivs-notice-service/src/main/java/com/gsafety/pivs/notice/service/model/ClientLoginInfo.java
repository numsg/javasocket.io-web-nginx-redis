package com.gsafety.pivs.notice.service.model;

/**
 * Created by gaoqiang on 2018/1/18.
 */
public class ClientLoginInfo {

    private String userId;
    private String deviceCode;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }
}
