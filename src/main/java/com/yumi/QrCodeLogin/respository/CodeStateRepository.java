package com.yumi.QrCodeLogin.respository;

import jakarta.websocket.Session;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Repository
public class CodeStateRepository {
    private final ConcurrentHashMap<String, String> codeMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Session> codeSessionMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

    public void addCode(String code) {
        scheduledExecutorService.schedule(() -> {
            codeMap.remove(code);
            Session session = codeSessionMap.remove(code);
            if (null != session) {
                try {
                    session.close();
                } catch (IOException e) {
                    //忽略
                }
            }
            System.out.println(code + " 被清理");
        }, 5, TimeUnit.MINUTES);
        codeMap.put(code, code);
    }

    public void addCode(String code, String targetCode) {
        scheduledExecutorService.schedule(() -> {
            codeMap.remove(code);
        }, 5, TimeUnit.MINUTES);
        codeMap.put(code, targetCode);
    }

    public String getCode(String code) {
        //code只能使用一次
        String mapCode = codeMap.remove(code);
        if (null == mapCode) {
            throw new IllegalStateException("code不存在,请刷新页面");
        }
        return mapCode;
    }

    public void addSession(String code, Session session) {
        String mapCode = codeMap.get(code);
        if (null == mapCode) {
            throw new IllegalStateException("code不存在,请刷新页面");
        }
        codeSessionMap.put(code, session);
    }

    public Session getSession(String code) {
        Session session = codeSessionMap.get(code);
        if (null == session) {
            throw new IllegalStateException("code不存在,请刷新页面");
        }
        return session;
    }

}
