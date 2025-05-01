package com.tander.chat.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebsocketConnectionsManager {
    final Map<String, Map<String, WebSocketSession>> state = new HashMap<String, Map<String, WebSocketSession>>();
    private final ReentrantLock lock = new ReentrantLock();

    public void attachSessionToChat(String chatId, String userId, WebSocketSession session) {
        log.info("Adding "  + userId + " to chat id " + chatId + " with socket id " + session.getId());
        lock.lock();
        if (!this.state.containsKey(chatId)){
            this.state.put(chatId, new HashMap<String, WebSocketSession>());
        }

        this.state.get(chatId).put(userId, session);
        lock.unlock();
    }

    public void removeSessionFromChat(WebSocketSession session) {
        for(String chatTd : this.state.keySet()) {
            for(String userId: this.state.get(chatTd).keySet()){
                if (this.state.get(chatTd).get(userId).getId().equals(session.getId())){
                    lock.lock();
                    log.info("Bye bye " + userId + " with sessionID " + session.getId());
                    this.state.get(chatTd).remove(userId);
                    lock.unlock();
                }
            }
        }
    }

    public List<WebSocketSession> getSessionsFromChatId(String chatId){
        return this.state.get(chatId).values().stream().collect(Collectors.toList());
    }

    public Optional<String> getUserIdFromChatSession(String chatId, String sessionId){
        for(String userId: this.state.get(chatId).keySet()){
            if(this.state.get(chatId).get(userId).getId().equals(sessionId)){
                return Optional.of(userId);
            }
        }
        return Optional.empty();
    }
}
