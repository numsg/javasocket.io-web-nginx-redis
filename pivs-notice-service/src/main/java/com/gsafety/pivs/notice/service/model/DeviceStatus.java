package com.gsafety.pivs.notice.service.model;

import com.gsafety.pivs.notice.service.model.metadata.EnumValueContract;

/**
 * Created by gaoqiang on 2018/1/19.
 */
public enum  DeviceStatus implements EnumValueContract<DeviceStatus> {
    /**
     *    ONLINE
     */
    ONLINE(0),

    /**
     *    OFFLINE
     */
    OFFLINE(1),

    /**
     *    ERROR
     */
    ERROR(2);

    DeviceStatus(int paramType) {
        this.paramType = paramType;
    }


    /**
     * Gets param type.
     *
     * @return the param type
     */
    @Override
    public int getParamType() {
        return paramType;
    }


    private int paramType;
}
