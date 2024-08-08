package com.yumi.QrCodeLogin.socket;

import com.yumi.QrCodeLogin.respository.CodeStateRepository;
import jakarta.annotation.Resource;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

@ServerEndpoint(value = "/websocket/{code}", configurator= EndpointConfigure.class)
@Component
public class QrCodeWebSocketHandler {
    @Resource
    private CodeStateRepository codeStateRepository;

    @OnOpen
    public void onOpen(Session session, @PathParam("code") String code) {
        // 这里可以获取到URL中的param参数
        System.out.println("WebSocket opened with param: " + code);
        codeStateRepository.addSession(code, session);
    }

}
