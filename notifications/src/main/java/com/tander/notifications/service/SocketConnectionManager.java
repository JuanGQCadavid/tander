package com.tander.notifications.service;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SocketConnectionManager {
    private final HashMap<String, WebSocketSession> currentConnections = new HashMap<String, WebSocketSession>();
    private final ReentrantLock lock = new ReentrantLock();

    public void addSession(String userId,WebSocketSession session){
        lock.lock();
        try { 
            currentConnections.put(userId, session);
            log.info(userId + " session saved.");
        } finally {
            lock.unlock();
        }
    }

    public Optional<WebSocketSession> getSession(String userId) {
        if (currentConnections.containsKey(userId)) {
            return Optional.of(currentConnections.get(userId));
        }
        return Optional.empty();
    }
}
