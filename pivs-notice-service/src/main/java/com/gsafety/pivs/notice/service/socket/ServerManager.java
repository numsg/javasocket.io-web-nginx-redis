package com.gsafety.pivs.notice.service.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.gsafety.pivs.notice.common.utils.HttpClientUtilImpl;
import com.gsafety.pivs.notice.common.utils.JsonUtil;
import com.gsafety.pivs.notice.service.constant.WsInvocation;
import com.gsafety.pivs.notice.service.model.ClientLoginInfo;
import com.gsafety.pivs.notice.service.model.DeviceInfo;
import com.gsafety.pivs.notice.service.model.DeviceStatus;
import com.gsafety.pivs.notice.service.redis.JedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ServerManager
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<SocketIOClient, ClientLoginInfo> clients = new ConcurrentHashMap<>();

    private String redisHost;
    private int redisPort;

    /*** #更新设备状态接口*/
    @Value("${remote.devicestatusurl}")
    private String deviceStatusURL;
    private HttpClientUtilImpl httpClientUtil = new HttpClientUtilImpl();

    @Autowired
    private SocketIOServer server;

    @OnConnect
    public void onConnect(SocketIOClient client)
    {
        logger.info("client connect to the server ,sessionId={}",client.getSessionId().toString());
//        client.joinRoom("studentJoinRoom"); //client.getHandshakeData().getSingleUrlParam("studentJoinRoom")
//        String sds =client.getHandshakeData().getSingleUrlParam("studentJoinRoom");
//        client.sendEvent("test","111");
//        server.getRoomOperations("studentJoinRoom").sendEvent("test","111");
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client)
    {
        try{
                ClientLoginInfo clientLoginInfo = clients.get(client);
                if (clientLoginInfo.getUserId() != null && !clientLoginInfo.getDeviceCode().isEmpty()) {
                    DeviceInfo deviceInfo = new DeviceInfo();
                    deviceInfo.setDeviceCode(clientLoginInfo.getDeviceCode());
                    deviceInfo.setStatusCode(String.valueOf(DeviceStatus.OFFLINE.getParamType()));
//                    DeviceInfo result = httpClientUtil.httpPost(deviceStatusURL, deviceInfo,deviceInfo.getClass());
                }
            logger.info("client disconnect to the server ,sessionId={}",client.getSessionId().toString());
        } catch (Exception ex) {
            logger.info("exception", ex.getMessage());
        }
        clients.remove(client);
        // client.leaveRoom(client.getHandshakeData().getSingleUrlParam("roomId"));
    }

    public Map<SocketIOClient, ClientLoginInfo> getClients() {
        return clients;
    }

    /**
     * Send redis.
     *
     * @param <T>           the type parameter
     * @param msgParams     the msg params
     * @param noticeTargers the notice targers
     * @param isGroup       the is group, if true : notice targer id is RoleClassify
     */
    public <T> void sendRedis(T msgParams, List<String> noticeTargers, boolean isGroup) {
        Jedis jedis = null;
        try {
            jedis = JedisUtils.getJedis(redisHost, redisPort);
            jedis.publish(WsInvocation.CHANNEL, buildMessage(msgParams, noticeTargers, isGroup));
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * Build message string.
     *
     * @param <T>           the type parameter
     * @param param         the param
     * @param noticeTargers the notice targers
     * @return the string
     */
    private <T> String buildMessage(T param, List<String> noticeTargers, boolean isGroup) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        Map<String, Object> map = new HashMap<>();
        creatMap(map, param);
        map.put(WsInvocation.CALLBACKID, WsInvocation.CALLBACKID);
        map.put(WsInvocation.HUB, getClassName(stackTraceElements));
        map.put(WsInvocation.ISGROUP, isGroup);
        map.put(WsInvocation.METHOD, getMethodName(stackTraceElements));
        map.put(WsInvocation.CLIENT, noticeTargers);
        return JsonUtil.toJson(map);
    }

    private <T> void creatMap(Map<String, Object> map, T param) {
        if (param instanceof String) {
            List<Map<String, Object>> mapList = new ArrayList<>();
            Map<String, Object> mapParam = new HashMap<>();
            mapParam.put(WsInvocation.STRINGPARAM, param);
            mapList.add(mapParam);
            map.put(WsInvocation.ARGS, mapList);
        } else {
            List<T> paramList = new ArrayList<>();
            paramList.add(param);
            map.put(WsInvocation.ARGS, paramList);
        }
    }

    private String getClassName(StackTraceElement[] stackTraceElements) {
        StackTraceElement stackTraceElement = stackTraceElements[3];
        String classFullName = stackTraceElement.getClassName();
        String[] classNames = classFullName.split("\\.");
        return classNames[classNames.length - 1];
    }

    private String getMethodName(StackTraceElement[] stackTraceElements) {
        StackTraceElement stackTraceElement = stackTraceElements[3];
        return stackTraceElement.getMethodName();
    }

    /**
     * 推送消息
     *
     * @param message the message
     * @throws IOException the io exception
     */
    public void sendMessage(String message) throws IOException {
        if (clients == null) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map = JsonUtil.fromJson(message, map.getClass());
        boolean isGroup = (Boolean) map.get(WsInvocation.ISGROUP);
        List<String> list = (ArrayList) map.get(WsInvocation.CLIENT);
//        if (isGroup) {
//            handlerSendMessageByGroup(list, message);
//            return;
//        }
//
        for (Map.Entry<SocketIOClient, ClientLoginInfo> entry : clients.entrySet()) {
            for (String personId : list) {
//                if (clients.get(entry.getKey()).getUserId().equals(personId)) {
                    entry.getKey().sendEvent("text_msg", message);
//                }
            }
        }
    }

    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }

    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
    }
}
