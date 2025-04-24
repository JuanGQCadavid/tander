package com.tander.notifications.managers;

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
    private final HashMap<String, String> userIdToSessionId = new HashMap<String, String>();

    private final ReentrantLock lock = new ReentrantLock();

    public void addSession(String sessionId,WebSocketSession session){
        lock.lock();
        try { 
            currentConnections.put(sessionId, session);
            log.info(sessionId + " session saved.");
        } finally {
            lock.unlock();
        }
    }

    public void removeSession(String sessionId){
        lock.lock();
        try { 
            currentConnections.remove(sessionId);
            log.info(sessionId + " session removed.");
            for (String key: userIdToSessionId.keySet()) {
                if(userIdToSessionId.get(key) == sessionId){
                    log.info("User Id " + userIdToSessionId.get(key) + " was detached from session id ", sessionId);
                    userIdToSessionId.remove(key);
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void attachSessionToUserId(String sessionId, String userId){
        lock.lock();
        try { 
            if (currentConnections.containsKey(sessionId)) {
                userIdToSessionId.put(userId, sessionId);
                log.info("User id " + userId+" attached to session " + sessionId);
            }else {
                log.info("Np sesion active " + sessionId+" to attached to user id  " + userId);
            }
        } finally {
            lock.unlock();
        }
    }

    public Optional<WebSocketSession> getSessionByUserId(String userId) {
        if (userIdToSessionId.containsKey(userId)) {
            return Optional.of(currentConnections.get(userIdToSessionId.get(userId)));
        }
        return Optional.empty();
    }

    public Optional<WebSocketSession> getSessionBySessionId(String sessionId) {
        if (currentConnections.containsKey(sessionId)) {
            return Optional.of(currentConnections.get(sessionId));
        }
        return Optional.empty();
    }
}
