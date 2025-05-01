package com.tander.chat.model;
import com.tander.chat.model.cmd.AttachUserId;
import com.tander.chat.model.cmd.Message;

public enum WebsocketCommands {
    ATTACH_USER_ID("AttachUserId", AttachUserId.class),
    NEW_MESSAGE("NewMessage", Message.class);

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
