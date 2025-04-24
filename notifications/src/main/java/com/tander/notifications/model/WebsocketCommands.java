package com.tander.notifications.model;

import com.tander.notifications.model.commands.AttachUserId;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public enum WebsocketCommands {
    ATTACH_USER_ID("AttachUserId", AttachUserId.class);

    // This is like the constructor to the enum class value
    // the ? is a generic!
    private final Class<?> commandClass;
    private final String cmd;


    WebsocketCommands(String cmd, Class<?> commandClass){
        this.cmd = cmd;
        this.commandClass = commandClass;
    }

    public Class<?> getCommandClass() {
        return commandClass;
    }

    public String getCmd() {
        return cmd;
    }
}
