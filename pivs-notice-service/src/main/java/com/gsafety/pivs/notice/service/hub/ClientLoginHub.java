package com.gsafety.pivs.notice.service.hub;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.gsafety.pivs.notice.common.utils.HttpClientUtilImpl;
import com.gsafety.pivs.notice.service.constant.WebSocketConstant;
import com.gsafety.pivs.notice.service.model.ClientLoginInfo;
import com.gsafety.pivs.notice.service.model.DeviceInfo;
import com.gsafety.pivs.notice.service.model.DeviceStatus;
import com.gsafety.pivs.notice.service.socket.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by gaoqiang on 2018/1/18.
 */

@Component
public class ClientLoginHub {

    @Autowired
    private ServerManager serverManager;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /*** #更新设备状态接口*/
    @Value("${remote.devicestatusurl}")
    private String deviceStatusURL;
    private HttpClientUtilImpl httpClientUtil = new HttpClientUtilImpl();

    //设备登录接口
    @OnEvent(value = WebSocketConstant.DEVICE_LOGIN_EVENT)
    public void onDeviceEvent(SocketIOClient client, AckRequest request, ClientLoginInfo clientInfo)
    {
        ClientLoginInfo clientLoginInfo = serverManager.getClients().get(client);
        if (clientLoginInfo == null) {
            clientLoginInfo = clientInfo;
            serverManager.getClients().put(client, clientLoginInfo);
        }
        // 发送消息给业务服务
        try{
           List<String> list =  new ArrayList<>();
            list.add(clientLoginInfo.getUserId());
            serverManager.sendRedis("123", list, false);
//            if (clientLoginInfo.getUserId() != null && !clientLoginInfo.getDeviceCode().isEmpty()) {
//                DeviceInfo deviceInfo = new DeviceInfo();
//                deviceInfo.setDeviceCode(clientInfo.getDeviceCode());
//                deviceInfo.setStatusCode(String.valueOf(DeviceStatus.ONLINE.getParamType()));
//                DeviceInfo result = httpClientUtil.httpPost(deviceStatusURL, deviceInfo,deviceInfo.getClass());
//                logger.info("client login to the server ,sessionId={}",client.getSessionId().toString());
//            }
        } catch (Exception ex) {
            logger.info("exception", ex.getMessage());
        }

    }
}
