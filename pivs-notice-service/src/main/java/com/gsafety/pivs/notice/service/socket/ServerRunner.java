package com.gsafety.pivs.notice.service.socket;

import com.corundumstudio.socketio.SocketIOServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ServerRunner implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SocketIOServer server;

    @Autowired
    public ServerRunner(SocketIOServer server) {
        this.server = server;
    }

    public void run(String... args) throws Exception {
//        List<String> eventList = new ArrayList<>();
//        eventList.add("teacherEvent");
//        socketEventListenerManager.initSocketEventListener(eventList);
        try{
            server.start();
        }catch (Exception ex){
            logger.error(ex.getMessage());
        }
    }
}
