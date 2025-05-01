package com.tander.chat.model;
import com.tander.chat.model.cmd.AttachUserId;

public enum WebsocketCommands {
    ATTACH_USER_ID("AttachUserId", AttachUserId.class);

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
