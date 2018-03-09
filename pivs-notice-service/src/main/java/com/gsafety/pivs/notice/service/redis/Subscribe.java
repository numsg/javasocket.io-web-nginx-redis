package com.gsafety.pivs.notice.service.redis;

import com.gsafety.pivs.notice.service.constant.WsInvocation;
import com.gsafety.pivs.notice.service.socket.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.io.IOException;

/**
 * Created by gaoqiang on 2016/5/12.
 */
@Service
public class Subscribe {

    private Logger logger = LoggerFactory.getLogger(Subscribe.class);

    /**
     * Instantiates a new Subscribe.
     *
     * @param serverManager the ws ServerManager
     */
    @Autowired
    public Subscribe(@Qualifier("serverManager") final ServerManager serverManager,
                     @Value("${redis.host}") String redisHost,
                     @Value("${redis.port}") int redisPort) {
        serverManager.setRedisHost(redisHost);
        serverManager.setRedisPort(redisPort);
        new Thread(() -> {
            try (Jedis jedis = JedisUtils.getJedis(redisHost, redisPort)) {
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        try {
                            serverManager.sendMessage(message);
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }, WsInvocation.CHANNEL);
            }
        }).start();;
    }
}
