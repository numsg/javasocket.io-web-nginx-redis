package com.gsafety.pivs.notice.backend.configs;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.corundumstudio.socketio.store.RedissonStoreFactory;
import com.gsafety.pivs.notice.service.constant.WebSocketConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.stream.Stream;

/**
 * Created by gaoqiang on 2018/1/17.
 */
@org.springframework.context.annotation.Configuration
public class SocketConfig {

    @Value("${websocket.server.host}")
    private String host;
    @Value("${websocket.server.port}")
    private Integer port;
    @Value("${websocket.server.nodes}")
    private String nodes;

    @Bean
    public SocketIOServer server() //Config config
    {
        Configuration configuration = new Configuration();
        configuration.setHostname(host);
        configuration.setPort(port);
//        RedissonClient redissonClient = Redisson.create(config);
//        configuration.setStoreFactory(new RedissonStoreFactory(redissonClient));

        configuration.setAuthorizationListener(new AuthorizationListener() {
            public boolean isAuthorized(HandshakeData data) {
                //TODO
                return true;
            }
        });
        final SocketIOServer server = new SocketIOServer(configuration);
        server.addNamespace(WebSocketConstant.Namespace);
        return server;
    }


//    @Bean
//    public Config config(){
//        Config config = new Config();
//        Stream.of(nodes.split(",")).forEach(node->{
//            config.useClusterServers().addNodeAddress(node);
//        });
//        return config;
//    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }
}
